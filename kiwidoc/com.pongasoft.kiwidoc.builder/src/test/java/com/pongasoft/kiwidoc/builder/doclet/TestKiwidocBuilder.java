
/*
 * Copyright (c) 2012 Yan Pujante
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.pongasoft.kiwidoc.builder.doclet;

import com.pongasoft.kiwidoc.builder.KiwidocBuilder;
import com.pongasoft.kiwidoc.builder.KiwidocLibraryStore;
import com.pongasoft.kiwidoc.builder.KiwidocLibraryStoreImpl;
import com.pongasoft.kiwidoc.builder.StoreException;
import com.pongasoft.kiwidoc.builder.NoSuchContentException;
import com.pongasoft.kiwidoc.builder.ContentHandlersImpl;
import com.pongasoft.kiwidoc.builder.model.LibraryModelBuilder;
import com.pongasoft.kiwidoc.builder.model.PackageModelBuilder;
import com.pongasoft.kiwidoc.model.ClassModel;
import com.pongasoft.kiwidoc.model.LibraryVersionModel;
import com.pongasoft.kiwidoc.model.PackageModel;
import com.pongasoft.kiwidoc.model.RepositoryModel;
import com.pongasoft.kiwidoc.model.DependenciesModel;
import com.pongasoft.kiwidoc.model.InheritanceModel;
import com.pongasoft.kiwidoc.model.MethodModel;
import com.pongasoft.kiwidoc.model.ClassEntryModel;
import com.pongasoft.kiwidoc.model.SimplePackageModel;
import com.pongasoft.kiwidoc.model.ClassDefinitionModel;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.PackageResource;
import com.pongasoft.kiwidoc.model.resource.RepositoryResource;
import com.pongasoft.kiwidoc.model.resource.Resource;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.impl.StandardFileSystemManager;
import org.junit.After;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * @author yan@pongasoft.com
 */
public class TestKiwidocBuilder
{
  public StandardFileSystemManager _manager;

  /**
   * Constructor
   */
  public TestKiwidocBuilder()
  {
  }

  @Before
  public void setUp() throws IOException
  {
    _manager = new StandardFileSystemManager();
    _manager.init();
  }

  @After
  public void teardDown() throws IOException
  {
    _manager.close();
  }

  private void readLibrary(LibraryModelBuilder expectedModelBuilder,
                           KiwidocLibraryStore kiwidocLibraryStore,
                           LibraryVersionResource libraryVersion)
    throws StoreException, NoSuchContentException, IOException
  {
    LibraryVersionModel
      libraryVersionModel = (LibraryVersionModel) kiwidocLibraryStore.loadContent(libraryVersion);

    LibraryVersionModel elvm = expectedModelBuilder.buildModel();
    Map<PackageResource, PackageModel> expectedPackages = new HashMap<PackageResource, PackageModel>();
    for(SimplePackageModel packageModel : elvm.getAllPackages())
    {
      PackageModelBuilder packageModelBuilder = expectedModelBuilder.findPackage(packageModel.getName());
      expectedPackages.put(packageModel.getResource(), packageModelBuilder.buildModel());
    }
    
    for(SimplePackageModel packageModel : libraryVersionModel.getAllPackages())
    {
      PackageModel expectedPackageModel = expectedPackages.remove(packageModel.getResource());
      Map<String, ClassDefinitionModel> expectedClasses = new HashMap<String, ClassDefinitionModel>();
      for(ClassDefinitionModel cdm : expectedPackageModel.getAllClasses())
      {
        expectedClasses.put(cdm.getName(), cdm);
      }

      PackageModel loadedPackageModel =
        (PackageModel) kiwidocLibraryStore.loadContent(packageModel.getResource());
      loadedPackageModel =
        kiwidocLibraryStore.resolve(DependenciesModel.NO_DEPENDENCIES, loadedPackageModel);

      assertEquals(expectedClasses.size(), loadedPackageModel.getAllClasses().size());

      for(ClassDefinitionModel cdm : loadedPackageModel.getAllClasses())
      {
        ClassDefinitionModel expectedClassDefinitionModel = expectedClasses.get(cdm.getName());
        assertEquals(expectedClassDefinitionModel.getType().toString(), cdm.getType().toString());
        assertEquals(expectedClassDefinitionModel.getClassResource(), cdm.getClassResource());
        assertEquals(expectedClassDefinitionModel.getAccess(), cdm.getAccess());
      }

      for(String className : packageModel.getAllClasses())
      {
        expectedClasses.remove(className);
        
        ClassModel expectedClassModel =
          getClassModel(expectedModelBuilder, loadedPackageModel.getResource(), className);
        ClassModel classModel = 
          (ClassModel) kiwidocLibraryStore.loadContent(new ClassResource(loadedPackageModel.getResource(), className));

        checkDoc(expectedClassModel, classModel);

        assertEquals(expectedClassModel.getAllMethods().size(), classModel.getAllMethods().size());
        for(MethodModel methodModel : classModel.getAllMethods())
        {
          MethodModel expectedMethodModel =
            expectedClassModel.findMethod(methodModel.getMemberName());

          checkDoc(expectedMethodModel, methodModel);
        }

        InheritanceModel inheritanceModel =
          kiwidocLibraryStore.resolveWithInheritance(DependenciesModel.NO_DEPENDENCIES, classModel);
        inheritanceModel = inheritanceModel.inheritDoc();
        assertNotNull(inheritanceModel);
      }
      assertTrue(expectedClasses.isEmpty());
    }

    assertTrue(expectedPackages.isEmpty());
  }

  private void checkDoc(ClassEntryModel expectedClassEntryModel, ClassEntryModel classEntryModel)
  {
    if(expectedClassEntryModel.getDoc() == null || !expectedClassEntryModel.getDoc().hasDoc())
      assertNull(classEntryModel.getDoc());
    else
      assertEquals(expectedClassEntryModel.getDoc().toString(), classEntryModel.getDoc().toString());
  }

  private ClassModel getClassModel(LibraryModelBuilder expectedModelBuilder,
                                   PackageResource packageResource,
                                   String className)
  {
    for(PackageModelBuilder packageModelBuilder : expectedModelBuilder.getAllPackages())
    {
      if(packageModelBuilder.buildModel().getResource().equals(packageResource))
        return packageModelBuilder.findClass(className).buildModel();
    }
    fail("could not find " + packageResource + "." + className);
    return null;
  }

  @Test
  public void testWithTestData() throws Exception
  {
    System.out.println(System.getProperty("testdata.build.dir"));
    System.out.println(System.getProperty("testdata.src.dir"));

    FileObject classes =
      _manager.resolveFile(new File(System.getProperty("testdata.build.dir")).toURI().toString());
//    FileObject classes = null;
    FileObject javadoc = null;
    FileObject sources =
      _manager.resolveFile(new File(System.getProperty("testdata.src.dir")).toURI().toString());
//    FileObject sources = null;

    LibraryVersionResource libraryVersionResource =
      new LibraryVersionResource("com.pongasoft", "com.pongasoft.kiwidoc.testdata", "1.0.0");

    FileObject store = _manager.resolveFile("ram://testWithTestData/kiwidoc");

    KiwidocLibraryStore kiwidocLibraryStore = new KiwidocLibraryStoreImpl(new ContentHandlersImpl(store));
    KiwidocBuilder builder = new KiwidocBuilder();

    KiwidocBuilder.Library library = new KiwidocBuilder.Library();
    library.libraryVersionResource = libraryVersionResource;
    library.classes = classes;
    library.javadoc = javadoc;
    library.sources = sources;

    LibraryModelBuilder modelBuilder = builder.buildKiwidoc(library);

    assertEquals(5, modelBuilder.getJdkVersion());

    LibraryVersionResource computedLibraryVersionResource = 
      kiwidocLibraryStore.storeLibrary(modelBuilder);

    assertEquals(libraryVersionResource.getOrganisation(), computedLibraryVersionResource.getOrganisation());
    assertEquals(libraryVersionResource.getName(), computedLibraryVersionResource.getName());
    assertEquals(libraryVersionResource.getVersion(), computedLibraryVersionResource.getVersion());
    //System.out.println(IOUtils.asString(store));

    ClassModel classModel =
      kiwidocLibraryStore.loadContent(new ClassResource(new PackageResource(libraryVersionResource,
                                                                            "com.pongasoft.kiwidoc.testdata.pubdir1"),
                                                        "I1G"));

    RepositoryModel repository = kiwidocLibraryStore.loadContent(RepositoryResource.INSTANCE);

    assertEquals(1, repository.getOrganisationResources().size());
    assertEquals(libraryVersionResource.getOrganisationResource(),
                 repository.getOrganisationResources().iterator().next());

    readLibrary(modelBuilder, kiwidocLibraryStore, libraryVersionResource);

    classModel =
      kiwidocLibraryStore.loadContent(new ClassResource(new PackageResource(libraryVersionResource,
                                                                            "com.pongasoft.kiwidoc.testdata.pubdir1"),
                                                        "Pub1Class3"));

    MethodModel method =
      classModel.findMethod("method1(Ljava/lang/Integer;Ljava/util/List;)");

    // before applying inheritance... should have the data as defined in the class
    assertEquals("This is the javadoc in Pub1Class3", method.getDoc().getDoc().getText());
    assertEquals(1, method.getDoc().getTags().size());
    assertEquals("{@inheritDoc}", method.getDoc().findReturnTag().getText());

    InheritanceModel inheritanceModel =
      kiwidocLibraryStore.resolveWithInheritance(DependenciesModel.NO_DEPENDENCIES, classModel);

    inheritanceModel = inheritanceModel.inheritDoc();

    method =
      inheritanceModel.getBaseClass().findMethod("method1(Ljava/lang/Integer;Ljava/util/List;)");

    MethodModel inheritedMethod =
      inheritanceModel.getSuperClass().getBaseClass().findMethod("method1(Ljava/lang/Integer;Ljava/util/List;)");
    Resource expectedOrigin = inheritedMethod.getDoc().findReturnTag().getOrigin();

    // after applying inheritance... the data has been populated/replaced
    System.out.println(method.getDoc());
    assertEquals("This is the javadoc in Pub1Class3", method.getDoc().getDoc().getText());
    assertEquals(3, method.getDoc().getTags().size());
    assertEquals("return value (from Pub1Class1)", method.getDoc().findReturnTag().getText());
    assertEquals(expectedOrigin, method.getDoc().findReturnTag().getOrigin());
    assertEquals("parameter 1", method.getDoc().findParamTag("p1").getText());
    assertEquals(expectedOrigin, method.getDoc().findParamTag("p1").getOrigin());
    assertEquals("parameter 2", method.getDoc().findParamTag("p2").getText());
    assertEquals(expectedOrigin, method.getDoc().findParamTag("p2").getOrigin());

    // superclass of Pub1Class3 is Puib1Class1 (should be resolved)
    assertEquals("com.pongasoft.kiwidoc.testdata.pubdir1.Pub1Class1", 
                 inheritanceModel.getSuperClass().getBaseClass().getFQCN());

    // superclass of Pub1Class1 is Object (not resolved as in this test the jdk is not present!)
    assertNull(inheritanceModel.getSuperClass().getSuperClass().getBaseClass());
    assertEquals("java.lang.Object",
                 inheritanceModel.getSuperClass().getSuperClass().getBaseResource().getFqcn());

    assertTrue(kiwidocLibraryStore.deleteLibraryVersion(libraryVersionResource));

    repository =
      (RepositoryModel) kiwidocLibraryStore.loadContent(RepositoryResource.INSTANCE);

    assertEquals(0, repository.getOrganisationResources().size());
  }
}

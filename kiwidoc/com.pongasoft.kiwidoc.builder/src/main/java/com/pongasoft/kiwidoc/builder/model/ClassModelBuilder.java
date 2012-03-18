
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

package com.pongasoft.kiwidoc.builder.model;

import com.pongasoft.kiwidoc.model.AnnotationsModel;
import com.pongasoft.kiwidoc.model.BaseEntryModel;
import com.pongasoft.kiwidoc.model.ClassDefinitionModel;
import com.pongasoft.kiwidoc.model.ClassModel;
import com.pongasoft.kiwidoc.model.DocModel;
import com.pongasoft.kiwidoc.model.FieldModel;
import com.pongasoft.kiwidoc.model.LVROverviewModel;
import com.pongasoft.kiwidoc.model.MethodModel;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.type.GenericType;
import com.pongasoft.kiwidoc.model.type.GenericTypeVariable;
import com.pongasoft.kiwidoc.model.type.GenericTypeVariables;
import com.pongasoft.kiwidoc.model.type.GenericVariable;
import com.pongasoft.kiwidoc.model.type.TypePart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author yan@pongasoft.com
 */
public class ClassModelBuilder implements ModelBuilder<ClassResource, ClassModel>
{
  public static final Log log = LogFactory.getLog(ClassModelBuilder.class);

  private final List<MethodModel> _methods = new ArrayList<MethodModel>();
  private final List<FieldModel> _fields = new ArrayList<FieldModel>();

  private int _access;
  private final DocModel _doc;
  private final ClassModel.ClassKind _classKind;
  private final String _fqcn;
  private final GenericTypeVariables _genericTypeVariables;
  private final GenericType _superclass;
  private final Collection<GenericType> _interfaces;
  private final Collection<ClassModelBuilder> _innerClasses = new ArrayList<ClassModelBuilder>();
  private final AnnotationsModel _annotations;

  private PackageModelBuilder _packageModelBuilder;
  private ClassResource _classResource;
  private GenericType _genericType;
  private ClassDefinitionModel _outerClass = null;
  private Collection<ClassDefinitionModel> _allLibrarySubclasses;
  private int _jdkVersion = 0;

  public ClassModelBuilder(int access,
                           DocModel doc,
                           AnnotationsModel annotations,
                           ClassModel.ClassKind classKind,
                           String fqcn,
                           GenericTypeVariables genericTypeVariables,
                           GenericType superclass,
                           Collection<GenericType> interfaces)
  {
    _access = access;
    _doc = doc;
    _annotations = annotations;
    _classKind = classKind;
    _fqcn = fqcn;
    _genericTypeVariables = genericTypeVariables;
    _superclass = superclass;
    _interfaces = interfaces;
  }

  public ClassResource getClassResource()
  {
    return _classResource;
  }

  public int getJdkVersion()
  {
    return _jdkVersion;
  }

  public void setJdkVersion(int jdkVersion)
  {
    _jdkVersion = jdkVersion;
  }

  public PackageModelBuilder getPackageModelBuilder()
  {
    return _packageModelBuilder;
  }

  public void setPackageModelBuilder(PackageModelBuilder packageModelBuilder)
  {
    _packageModelBuilder = packageModelBuilder;
    _classResource = new ClassResource(packageModelBuilder.getLibraryModelBuilder().getLibraryVersionResource(),
                                       getFqcn());
  }

  public int getAccess()
  {
    return _access;
  }

  public DocModel getDoc()
  {
    return _doc;
  }

  public AnnotationsModel getAnnotations()
  {
    return _annotations;
  }


  public String getFqcn()
  {
    return _fqcn;
  }

  public ClassDefinitionModel getOuterClass()
  {
    if(_outerClass == null)
    {
      if(_classResource.isInnerClass())
      {
        ClassResource outerClassResource = _classResource.getOuterClassResource();
        ClassModelBuilder outerClassModel =
          getPackageModelBuilder().findClass(outerClassResource.getSimpleClassName());
        // sanity check... we fake one if not found...
        if(outerClassModel == null)
        {
          log.warn("cannot find outer class " + outerClassResource.getSimpleClassName());
          _outerClass = new ClassDefinitionModel(getAccess(),
                                                 GenericType.create(outerClassResource));
        }
        else
          _outerClass = outerClassModel.getClassDefinition();
      }
    }

    return _outerClass;
  }

  public GenericType getType()
  {
    if(_genericType == null)
    {
      List<TypePart> typeParts = new ArrayList<TypePart>();
      if(getOuterClass() != null)
      {
        typeParts.addAll(getOuterClass().getType().getTypeParts());
      }
      List<GenericTypeVariable> gtvs = _genericTypeVariables.getGenericTypeVariables();
      List<GenericVariable> gvs = new ArrayList<GenericVariable>(gtvs.size());
      for(GenericTypeVariable gtv : gtvs)
      {
        gvs.add(new GenericVariable(gtv.getName()));
      }
      typeParts.add(new TypePart(_classResource, gvs));
      _genericType = GenericType.create(typeParts);
    }

    return _genericType; 
  }

  public ClassDefinitionModel getClassDefinition()
  {
    return new ClassDefinitionModel(getAccess(), getType());
  }

  /**
   * @return <code>null</code> if it is the default package
   */
  public String getPackageName()
  {
    return ClassModel.computePackageName(getFqcn());
  }

  public String getSimpleName()
  {
    return ClassModel.computeSimpleName(getFqcn());
  }

  public boolean isExportedClass()
  {
    return _access >= 0;
  }

  public void addMethod(MethodModel methodModel)
  {
    _methods.add(methodModel);
  }

  public void addField(FieldModel fieldModel)
  {
    _fields.add(fieldModel);
  }

  public void setExportedClass(boolean exportedClass)
  {
    if(exportedClass)
      _access &= ~BaseEntryModel.Access.NON_EXPORTED.getOpcode();
    else
      _access |= BaseEntryModel.Access.NON_EXPORTED.getOpcode();
  }

  public void addInnerClass(ClassModelBuilder innerClass)
  {
    _innerClasses.add(innerClass);
  }

  public Collection<ClassModelBuilder> getInnerClasses()
  {
    return _innerClasses;
  }

  public GenericType getSuperclass()
  {
    return _superclass;
  }

  public Collection<GenericType> getInterfaces()
  {
    return _interfaces;
  }

  public void setAllLibrarySubclasses(Collection<ClassDefinitionModel> allLibrarySubclasses)
  {
    _allLibrarySubclasses = allLibrarySubclasses;
  }

  public Collection<ClassDefinitionModel> getAllLibrarySubclasses()
  {
    return _allLibrarySubclasses;
  }

  /**
   * @return the model
   */
  public ClassModel buildModel()
  {
    Collection<ClassDefinitionModel> innerClasses = new ArrayList<ClassDefinitionModel>();
    for(ClassModelBuilder innerClass : _innerClasses)
    {
      innerClasses.add(innerClass.getClassDefinition());
    }

    return new ClassModel(_classResource,
                          getAccess(),
                          getDoc(),
                          getAnnotations(),
                          _classKind,
                          _genericTypeVariables,
                          _superclass,
                          _interfaces, 
                          _methods,
                          _fields,
                          getOuterClass(),
                          innerClasses,
                          _allLibrarySubclasses);
  }

  public LVROverviewModel.Class buildOverview()
  {
    Collection<LVROverviewModel.Method> methods = new ArrayList<LVROverviewModel.Method>();

    // TODO HIGH YP:  generate signature
    for(MethodModel method : _methods)
    {
      methods.add(new LVROverviewModel.Method(method.getAccess(),
                                              method.getMemberName(),
                                              ""));
    }

    Collection<LVROverviewModel.Field> fields = new ArrayList<LVROverviewModel.Field>();

    for(FieldModel field : _fields)
    {
      fields.add(new LVROverviewModel.Field(field.getAccess(),
                                            field.getName(),
                                            field.getType().toErasureDescriptor()));
    }

    return new LVROverviewModel.Class(getAccess(),
                                      getSimpleName(),
                                      "",
                                      methods,
                                      fields);

  }
}

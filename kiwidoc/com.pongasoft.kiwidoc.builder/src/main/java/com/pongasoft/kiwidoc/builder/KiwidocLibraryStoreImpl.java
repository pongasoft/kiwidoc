
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

package com.pongasoft.kiwidoc.builder;

import com.pongasoft.kiwidoc.builder.model.LibraryModelBuilder;
import com.pongasoft.kiwidoc.builder.model.ModelBuilder;
import com.pongasoft.kiwidoc.builder.model.PackageModelBuilder;
import com.pongasoft.kiwidoc.model.ClassModel;
import com.pongasoft.kiwidoc.model.DependenciesModel;
import com.pongasoft.kiwidoc.model.InheritanceModel;
import com.pongasoft.kiwidoc.model.LibraryVersionModel;
import com.pongasoft.kiwidoc.model.Model;
import com.pongasoft.kiwidoc.model.ResolvableModel;
import com.pongasoft.kiwidoc.model.SimplePackageModel;
import com.pongasoft.kiwidoc.model.resource.LibraryResource;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.util.core.annotations.FieldInitializer;
import com.pongasoft.util.core.annotations.ObjectInitializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author yan@pongasoft.com
 */
public class KiwidocLibraryStoreImpl implements KiwidocLibraryStore
{
  public static final Log log = LogFactory.getLog(KiwidocLibraryStoreImpl.class);

  private ContentHandlers _contentHandlers;

  /**
   * Constructor
   */
  @ObjectInitializer
  public KiwidocLibraryStoreImpl()
  {
  }

  /**
   * Constructor
   */
  public KiwidocLibraryStoreImpl(ContentHandlers contentHandlers)
    throws StoreException
  {
    _contentHandlers = contentHandlers;
  }

  public ContentHandlers getContentHandlers()
  {
    return _contentHandlers;
  }

  @FieldInitializer
  public void setContentHandlers(ContentHandlers contentHandlers)
  {
    _contentHandlers = contentHandlers;
  }

  /**
   * Stores a library and returns the resource to retrieve it.
   *
   * @return the resource to retrieve the library ({@link #loadContent(Resource)})
   * @throws StoreException if there is an error storing the library
   */
  public LibraryVersionResource storeLibrary(LibraryModelBuilder lib) throws DuplicateLibraryException, StoreException
  {
    /**
     * // TODO HIGH YP:  should be done asynchronously (use a queue + single thread)
     */
    LibraryVersionModel libraryVersionModel = lib.buildModel();

    LibraryVersionResource libraryVersionResource = libraryVersionModel.getResource();

    if(_contentHandlers.exists(libraryVersionResource))
        throw new DuplicateLibraryException(libraryVersionModel + " already exists");

    // library
    saveContent(libraryVersionModel);

    // library overview
    saveContent(lib.buildOverview());

    // manifest
    if(libraryVersionModel.getHasManifest())
    {
      saveContent(lib.getManifest());
    }

    // hierarchy
    //saveContent(lib.getHierarchyModel());

    // packages
    for(SimplePackageModel packageModel : libraryVersionModel.getAllPackages())
    {
      PackageModelBuilder packageModelBuilder = lib.findPackage(packageModel.getName());

      saveContent(packageModelBuilder.buildModel());

      for(String className : packageModel.getAllClasses())
      {
        saveContent(packageModelBuilder.findClass(className));
      }
    }

    return libraryVersionResource;
  }

  /**
   * Loads the content of the given resource
   *
   * @param resource the resource to load
   * @return the content as an object since it depends on which content is being read (ex: manifest,
   *         library, packages, class) (never <code>null</code>)
   * @throws NoSuchContentException if the content does not exist
   * @throws StoreException         if there is a problem reading the content.
   */
  public <R extends Resource, M extends Model<R>> M loadContent(R resource)
    throws NoSuchContentException, StoreException
  {
    if(resource == null)
      throw new NoSuchContentException(resource);

    @SuppressWarnings("unchecked")
    M model = (M) _contentHandlers.loadContent(resource);

    return model;
  }

  /**
   * Loads the contents of the given resource
   *
   * @param resources the resources to load
   * @return the content as an object since it depends on which content is being read (ex: manifest,
   *         library, packages, class) (never <code>null</code>). If one of the content is missing it
   *         will simply not be part of the result set.
   * @throws StoreException if there is a problem reading the content.
   */
  public Collection<Model> loadContent(Collection<Resource> resources) throws StoreException
  {
    Collection<Model> res = new ArrayList<Model>(resources.size());

    for(Resource resource : resources)
    {
      try
      {
        Model content = (Model) loadContent(resource);
        res.add(content);
      }
      catch(NoSuchContentException e)
      {
        if(log.isDebugEnabled())
        {
          log.debug("no content for " + resource);
        }
      }
    }

    return res;
  }

  /**
   * Given a set of (optional) dependencies, resolves the dependencies contained in the resolvable
   * model.
   *
   * @param dependencies the dependencies that should takes precedence over the ones coming from the
   *                     model
   * @return the class model with resolved dependencies (note that in pass by reference context, it
   *         will be the same object provided as an argument)
   * @throws StoreException if there is a problem
   */
  public <M extends ResolvableModel> M resolve(DependenciesModel dependencies, M resolvableModel)
    throws StoreException
  {
    DependencyResolver<M> dependencyResolver =
      new DependencyResolver<M>(this, dependencies, resolvableModel);

    return dependencyResolver.resolve();
  }

  /**
   * Given a set of (optional) dependencies, resolves the dependencies contained in the class model
   * and recursively up the chain of inheritance.
   *
   * @param dependencies the dependencies that should takes precedence over the ones coming from the
   *                     model
   * @return the resolved inheritance model
   * @throws StoreException if there is a problem
   */
  public InheritanceModel resolveWithInheritance(DependenciesModel dependencies,
                                                 ClassModel classModel) throws StoreException
  {
    InheritanceDependencyResolver resolver =
      new InheritanceDependencyResolver(this, dependencies, classModel);

    return resolver.resolve();
  }

  /**
   * Resolves the set of libraries (essentially looking to see if they are in the index)
   *
   * @return the set of the libraries that we know
   * @throws StoreException if there is a problem
   */
  public Set<LibraryVersionResource> resolve(Set<LibraryVersionResource> libraries)
    throws StoreException
  {
    Set<LibraryVersionResource> resolvedLibraries = new HashSet<LibraryVersionResource>();

    for(LibraryVersionResource library : libraries)
    {
      if(_contentHandlers.exists(library))
        resolvedLibraries.add(library);
    }

    return resolvedLibraries;
  }

  /**
   * @return the jdk corresponding to the given version
   */
  public LibraryVersionModel findJdk(int version) throws StoreException
  {

    if(version == 0 || version > 6)
      version = 6;
    if(version < 3)
      version = 3;

    LibraryVersionResource lvr = computeJdkLVR(version);
    if(_contentHandlers.exists(lvr))
    {
      try
      {
        return loadContent(lvr);
      }
      catch(NoSuchContentException e)
      {
        throw new StoreException(e);
      }
    }
    else
      return null;
  }

  private LibraryVersionResource computeJdkLVR(int version)
  {
    return new LibraryVersionResource("java", "j2se", "1." + version);
  }

  /**
   * @return the list of known jdks
   */
  public Collection<LibraryVersionResource> getJdks() throws StoreException
  {
    Collection<LibraryVersionResource> jdks = new ArrayList<LibraryVersionResource>();

    for(int version = 3; version <= 6; version++)
    {
      LibraryVersionResource lvr = computeJdkLVR(version);
      if(_contentHandlers.exists(lvr))
      {
        jdks.add(lvr);
      }
    }

    return jdks;
  }

  /**
   * Deletes the library
   *
   * @return <code>true</code> if it was deleted, <code>false</code> if it did not exist
   * @throws StoreException if there is a problem deleting the library
   */
  public boolean deleteLibraryVersion(LibraryVersionResource libraryVersion) throws StoreException
  {
    return _contentHandlers.deleteContent(libraryVersion);
  }

  /**
   * Deletes the library (all versions will be deleted!!) use with caution!!
   *
   * @return <code>true</code> if it was deleted, <code>false</code> if it did not exist
   * @throws StoreException if there is a problem deleting the library
   */
  public boolean deleteLibrary(LibraryResource library) throws StoreException
  {
    return _contentHandlers.deleteContent(library);
  }

  private <R extends Resource, M extends Model<R>> void saveContent(M model)
    throws StoreException
  {
    _contentHandlers.saveContent(model);
  }

  private <R extends Resource, M extends Model<R>> void saveContent(ModelBuilder<R, M> modelBuilder)
    throws StoreException
  {
    M model = modelBuilder.buildModel();
    saveContent(model);
  }
}
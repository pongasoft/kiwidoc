
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
import com.pongasoft.kiwidoc.model.ClassModel;
import com.pongasoft.kiwidoc.model.DependenciesModel;
import com.pongasoft.kiwidoc.model.InheritanceModel;
import com.pongasoft.kiwidoc.model.LibraryVersionModel;
import com.pongasoft.kiwidoc.model.Model;
import com.pongasoft.kiwidoc.model.ResolvableModel;
import com.pongasoft.kiwidoc.model.resource.LibraryResource;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.Collection;
import java.util.Set;

/**
 * @author yan@pongasoft.com
 */
public interface KiwidocLibraryStore
{
  /**
   * Stores a library and returns the resource to retrieve it.
   *
   * @return the resource to retrieve the library ({@link #loadContent(Resource)})
   * @throws StoreException if there is an error storing the library
   */
  LibraryVersionResource storeLibrary(LibraryModelBuilder lib) throws DuplicateLibraryException, StoreException;

  /**
   * Loads the content of the given resource
   *
   * @param resource the resource to load
   * @return the content as an object since it depends on which content is being read (ex: manifest,
   *         library, packages, class) (never <code>null</code>)
   * @throws NoSuchContentException if the content does not exist
   * @throws StoreException if there is a problem reading the content.
   */
  <R extends Resource, M extends Model<R>> M loadContent(R resource) throws NoSuchContentException, StoreException;

  /**
   * Loads the contents of the given resource
   *
   * @param resources the resources to load
   * @return the content as an object since it depends on which content is being read (ex: manifest,
   *         library, packages, class) (never <code>null</code>). If one of the content is missing
   *         it will simply not be part of the result set.
   * @throws StoreException if there is a problem reading the content.
   */
  Collection<Model> loadContent(Collection<Resource> resources) throws StoreException;

  /**
   * Given a set of (optional) dependencies, resolves the dependencies contained in
   * the resolvable model.
   *
   * @param dependencies the dependencies that should takes precedence over the ones coming
   * from the model
   * @return the class model with resolved dependencies (note that in pass by reference context, it
   * will be the same object provided as an argument)
   * @throws StoreException if there is a problem
   */
  <M extends ResolvableModel> M resolve(DependenciesModel dependencies,
                                        M resolvableModel) throws StoreException;

  /**
   * Resolves the set of libraries (essentially looking to see if they are in the index)
   *
   * @return the set of the libraries that we know
   * @throws StoreException if there is a problem
   */
  Set<LibraryVersionResource> resolve(Set<LibraryVersionResource> libraries) throws StoreException;

  /**
   * Given a set of (optional) dependencies, resolves the dependencies contained in the class model
   * and recursively up the chain of inheritance.
   *
   * @param dependencies the dependencies that should takes precedence over the ones coming from the
   *                     model
   * @return the resolved inheritance model
   * @throws StoreException if there is a problem
   */
  InheritanceModel resolveWithInheritance(DependenciesModel dependencies,
                                          ClassModel classModel) throws StoreException;

  /**
   * Deletes the library (specific version)
   *
   * @return <code>true</code> if it was deleted, <code>false</code> if it did not exist
   * @throws StoreException if there is a problem deleting the library
   */
  boolean deleteLibraryVersion(LibraryVersionResource libraryVersion) throws StoreException;

  /**
   * Deletes the library (all versions will be deleted!!) use with caution!!
   *
   * @return <code>true</code> if it was deleted, <code>false</code> if it did not exist
   * @throws StoreException if there is a problem deleting the library
   */
  boolean deleteLibrary(LibraryResource library) throws StoreException;

  /**
   * @return the jdk corresponding to the given version
   */
  LibraryVersionModel findJdk(int version) throws StoreException;

  /**
   * @return the list of known jdks
   */
  Collection<LibraryVersionResource> getJdks() throws StoreException;
}
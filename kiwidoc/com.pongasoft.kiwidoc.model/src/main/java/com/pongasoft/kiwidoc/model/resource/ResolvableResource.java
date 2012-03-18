
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

package com.pongasoft.kiwidoc.model.resource;

/**
 * Identifies a resource which is resolvable.
 *
 * @author yan@pongasoft.com
 */
public interface ResolvableResource<R extends Resource, P extends Resource> extends VersionableResource<R, P> 
{
  /**
   * @return <code>true</code> if the resource is already resolved
   */
  boolean isResolved();

  /**
   * Resolves the resource
   * @throws IllegalStateException if the resource is already resolved
   */
  void resolve(LibraryVersionResource libraryVersionResource);

  /**
   * @return the library version resource (note that it is <code>null</code> if {@link #isResolved()}
   * returns <code>false</code> and not <code>null</code> otherwise)
   */
  LibraryVersionResource getLibraryVersionResource();

  /**
   * @return the name of the package
   */
  String getPackageName();

  /**
   * @return the name of the class (simple name, may be <code>null</code>)
   */
  String getSimpleClassName();
}
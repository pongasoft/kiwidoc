
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

package com.pongasoft.kiwidoc.model;

import com.pongasoft.kiwidoc.model.resource.LibraryResource;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.Set;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Collection;

/**
 * Represents a library (for example junit, hibernate...).
 *
 * @author yan@pongasoft.com
 */
public class LibraryModel implements Model<LibraryResource>
{
  private final LibraryResource _libraryResource;
  private final Set<String> _versions;

  /**
   * Constructor
   */
  public LibraryModel(LibraryResource libraryResource, String... versions)
  {
    _libraryResource = libraryResource;
    _versions = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(versions)));
  }

  /**
   * Constructor
   */
  public LibraryModel(LibraryResource libraryResource, Collection<String> versions)
  {
    _libraryResource = libraryResource;
    _versions = Collections.unmodifiableSet(new HashSet<String>(versions));
  }

  /**
   * @return the resource
   */
  public LibraryResource getResource()
  {
    return _libraryResource;
  }

  @Override
  public Collection<? extends Resource> getChildren()
  {
    return getVersionsResources();
  }

  /**
   * @return all known versions of this library
   */
  public Set<String> getVersions()
  {
    return _versions;
  }

  /**
   * @return all known versions of this library (as resources)
   */
  public Set<LibraryVersionResource> getVersionsResources()
  {
    Set<LibraryVersionResource> res = new HashSet<LibraryVersionResource>();

    for(String version : _versions)
    {
      res.add(new LibraryVersionResource(_libraryResource, version));
    }

    return res;
  }

  /**
   * @return a version of this model with everything that is not part of the public api which has
   *         been stripped out. If the model itself is not part of the public api then
   *         <code>null</code> is returned!
   */
  public LibraryModel toPublicAPI()
  {
    return this;
  }

  /**
   * @return <code>true</code> if this model is part of the public api
   */
  public boolean isPublicAPI()
  {
    return true;
  }

  /**
   * @return the model kind
   */
  public Kind getKind()
  {
    return Kind.LIBRARY;
  }
}

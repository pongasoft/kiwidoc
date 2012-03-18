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

import com.pongasoft.kiwidoc.model.Model;

/**
 * Represent a library (jar file usually)
 * @author yan@pongasoft.com
 */
public class LibraryVersionResource extends AbstractResource<LibraryVersionResource, LibraryResource> implements VersionableResource<LibraryVersionResource, LibraryResource>
{
  private static final long serialVersionUID = 1L;

  private final String _version;

  /**
   * @param organisation
   * @param name
   * @param version
   */
  public LibraryVersionResource(String organisation, String name, String version)
  {
    this(new LibraryResource(organisation, name), version);
  }

  /**
   * @param version
   */
  public LibraryVersionResource(LibraryResource libraryResource, String version)
  {
    super(libraryResource);

    if(libraryResource == null || version == null)
      throw new IllegalArgumentException("null not allowed");
    _version = version;
  }

  /**
   * @return the depth of the resource (root is 0, child of root is 1, etc...)
   */
  public int getDepth()
  {
    return 3;
  }

  /**
   * @return the organisation
   */
  public String getOrganisation()
  {
    return getParent().getOrganisation();
  }

  /**
   * @return the organisation
   */
  public OrganisationResource getOrganisationResource()
  {
    return getParent().getParent();
  }

  /**
   * Clones this resource using the parent provided.
   *
   * @param parent the parent to use
   * @return a new resource using the parent provided
   */
  public LibraryVersionResource clone(LibraryResource parent)
  {
    return new LibraryVersionResource(parent, _version);
  }

  /**
   * @return the name
   */
  public String getName()
  {
    return getParent().getName();
  }

  /**
   * @return the name of the resource
   */
  public String getResourceName()
  {
    return _version;
  }

  /**
   * @return the version
   */
  public String getVersion()
  {
    return _version;
  }

  public LibraryResource getLibraryResource()
  {
    return getParent();
  }

  /**
   * @return the model kind associated to this resource
   */
  public Model.Kind getModelKind()
  {
    return Model.Kind.LIBRARY_VERSION;
  }

  @Override
  public boolean equals(Object o)
  {
    if(this == o) return true;
    if(o == null || getClass() != o.getClass()) return false;
    if(!super.equals(o)) return false;

    LibraryVersionResource resource = (LibraryVersionResource) o;

    if(!_version.equals(resource._version)) return false;

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + _version.hashCode();
    return result;
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder(getParent().toString());
    sb.append('/').append(_version);
    return sb.toString();
  }

  /**
   * @return the library version resource associated to the resource (note that
   * if it is 'higher' than a library version then it will return <code>null</code>)
   */
  public static LibraryVersionResource extractLVR(Resource resource)
  {
    while(resource != null && !(resource instanceof LibraryVersionResource))
    {
      resource = resource.getParent();
    }

    return (LibraryVersionResource) resource;
  }
}

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
 * @author yan@pongasoft.com
 */
public class PackageResource extends AbstractResource<PackageResource, LibraryVersionResource>
  implements ResolvableResource<PackageResource, LibraryVersionResource>
{
  private static final long serialVersionUID = 1L;

  private final String _packageName;

  /**
   * Constructor
   */
  public PackageResource(LibraryVersionResource libraryVersionResource, String packageName)
  {
    super(libraryVersionResource);
    if(packageName == null)
      throw new IllegalArgumentException("packageName cannot be null");
    _packageName = packageName;
  }

  /**
   * Constructor
   */
  public PackageResource(String packageName)
  {
    this(null, packageName);
  }

  /**
   * @return the depth of the resource (root is 0, child of root is 1, etc...)
   */
  public int getDepth()
  {
    return 4;
  }

  public LibraryVersionResource getLibraryVersionResource()
  {
    return getParent();
  }

  public String getPackageName()
  {
    return _packageName;
  }

  /**
   * @return the name of the class (simple name, may be <code>null</code>)
   */
  public String getSimpleClassName()
  {
    return null;
  }

  public String getName()
  {
    return _packageName;
  }

  /**
   * @return the name of the resource
   */
  public String getResourceName()
  {
    return getName();
  }

  /**
   * @return the version
   */
  public String getVersion()
  {
    if(getParent() != null)
      return getParent().getVersion();
    else
      return null;
  }

  /**
   * @return the library in which this versionable resource resides
   */
  public LibraryResource getLibraryResource()
  {
    if(getParent() != null)
      return getParent().getLibraryResource();
    else
      return null;
  }

  /**
   * @return the model kind associated to this resource
   */
  public Model.Kind getModelKind()
  {
    return Model.Kind.PACKAGE;
  }

  /**
   * Clones this resource using the parent provided.
   *
   * @param parent the parent to use
   * @return a new resource using the parent provided
   */
  public PackageResource clone(LibraryVersionResource parent)
  {
    return new PackageResource(parent, _packageName);
  }

  public boolean isResolved()
  {
    return getParent() != null;
  }

  public void resolve(LibraryVersionResource libraryVersionResource)
  {
    if(isResolved())
      throw new IllegalStateException("already resolved!");
    initParent(libraryVersionResource);
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder(getParent() != null ? getParent().toString() : "");
    sb.append("/p/").append(_packageName);
    return sb.toString();
  }

  @Override
  public boolean equals(Object o)
  {
    if(this == o) return true;
    if(o == null || getClass() != o.getClass()) return false;
    if(!super.equals(o)) return false;

    PackageResource that = (PackageResource) o;

    if(!_packageName.equals(that._packageName)) return false;

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + _packageName.hashCode();
    return result;
  }
}


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
public abstract class FieldOrMethodResource<R extends Resource> extends AbstractResource<R, ClassResource>
  implements ResolvableResource<R, ClassResource>
{
  private static final long serialVersionUID = 1L;

  private final String _name;

  /**
   * Constructor
   */
  public FieldOrMethodResource(ClassResource classResource, String name)
  {
    super(classResource);
    if(classResource == null || name == null)
      throw new IllegalArgumentException("null not allowed");

    _name = name;
  }

  /**
   * @return the depth of the resource (root is 0, child of root is 1, etc...)
   */
  public int getDepth()
  {
    return 6;
  }

  public ClassResource getClassResource()
  {
    return getParent();
  }

  public String getName()
  {
    return _name;
  }

  /**
   * @return the name of the resource
   */
  public String getResourceName()
  {
    return getName();
  }

  /**
   * @return <code>true</code> if the resource is already resolved
   */
  public boolean isResolved()
  {
    return getClassResource().isResolved();
  }

  /**
   * @return the library version resource (note that it is <code>null</code> if {@link #isResolved()}
   *         returns <code>false</code> and not <code>null</code> otherwise)
   */
  public LibraryVersionResource getLibraryVersionResource()
  {
    return getClassResource().getLibraryVersionResource();
  }

  /**
   * @return the version
   */
  public String getVersion()
  {
    return getClassResource().getVersion();
  }

  /**
   * @return the library in which this versionable resource resides
   */
  public LibraryResource getLibraryResource()
  {
    return getClassResource().getLibraryResource();
  }

  /**
   * Resolves the resource
   *
   * @throws IllegalStateException if the resource is already resolved
   */
  public void resolve(LibraryVersionResource libraryVersionResource)
  {
    getClassResource().resolve(libraryVersionResource);
  }

  /**
   * @return the name of the package
   */
  public String getPackageName()
  {
    return getClassResource().getPackageName();
  }

  /**
   * @return the name of the class (simple name, may be <code>null</code>)
   */
  public String getSimpleClassName()
  {
    return getClassResource().getSimpleClassName();
  }

  /**
   * @return the model kind associated to this resource
   */
  public Model.Kind getModelKind()
  {
    return Model.Kind.CLASS;
  }

  @Override
  public boolean equals(Object o)
  {
    if(this == o) return true;
    if(!(o instanceof FieldOrMethodResource)) return false;
    if(!super.equals(o)) return false;

    FieldOrMethodResource that = (FieldOrMethodResource) o;

    if(!_name.equals(that._name)) return false;

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + _name.hashCode();
    return result;
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder(getParent().toString());
    sb.append('#').append(_name);
    return sb.toString();
  }

}
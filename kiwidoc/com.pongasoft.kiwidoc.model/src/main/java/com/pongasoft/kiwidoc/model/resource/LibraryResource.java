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
 * Represent a library (a library has usually more than one versions)
 * @author yan@pongasoft.com
 */
public class LibraryResource extends AbstractResource<LibraryResource, OrganisationResource>
{
  private static final long serialVersionUID = 1L;

  public static final LibraryResource JDK = new LibraryResource("java", "j2se");

  private final String _name;

  /**
   * @param organisation
   * @param name
   */
  public LibraryResource(String organisation, String name)
  {
    super(new OrganisationResource(organisation));

    if(name == null)
      throw new IllegalArgumentException("null not allowed");

    _name = name;
  }

  /**
   * @param organisation
   * @param name
   */
  public LibraryResource(OrganisationResource organisation, String name)
  {
    super(organisation);

    if(organisation == null || name == null)
      throw new IllegalArgumentException("null not allowed");

    _name = name;
  }

  /**
   * @return the depth of the resource (root is 0, child of root is 1, etc...)
   */
  public int getDepth()
  {
    return 2;
  }

  /**
   * @return the organisation
   */
  public String getOrganisation()
  {
    return getParent().getOrganisation();
  }

  /**
   * Clones this resource using the parent provided.
   *
   * @param parent the parent to use
   * @return a new resource using the parent provided
   */
  public LibraryResource clone(OrganisationResource parent)
  {
    return new LibraryResource(parent, _name);
  }

  /**
   * @return the name
   */
  public String getName()
  {
    return _name;
  }

  /**
   * @return the name of the resource
   */
  public String getResourceName()
  {
    return _name;
  }

  /**
   * @return the model kind associated to this resource
   */
  public Model.Kind getModelKind()
  {
    return Model.Kind.LIBRARY;
  }

  /**
   * @return <code>true</code> if this library resource represents a jdk
   */
  public boolean isJDK()
  {
    return this.equals(JDK);
  }

  @Override
  public boolean equals(Object o)
  {
    if(this == o) return true;
    if(o == null || getClass() != o.getClass()) return false;
    if(!super.equals(o)) return false;

    LibraryResource that = (LibraryResource) o;

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
    sb.append('/').append(_name);
    return sb.toString();
  }
}
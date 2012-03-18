
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
public class OrganisationResource extends AbstractResource<OrganisationResource, RepositoryResource>
{
  private static final long serialVersionUID = 1L;

  private String _organisation;

  /**
   * Constructor
   */
  public OrganisationResource(String organisation)
  {
    super(RepositoryResource.INSTANCE);

    if(organisation == null)
      throw new IllegalArgumentException("null not allowed");

    _organisation = organisation;
  }

  public String getOrganisation()
  {
    return _organisation;
  }

  /**
   * @return the depth of the resource (root is 0, child of root is 1, etc...)
   */
  public int getDepth()
  {
    return 1;
  }

  /**
   * Clones this resource using the parent provided.
   *
   * @param parent the parent to use
   * @return a new resource using the parent provided
   */
  public OrganisationResource clone(RepositoryResource parent)
  {
    return this;
  }

  /**
   * @return the name
   */
  public String getResourceName()
  {
    return getOrganisation();
  }

  /**
   * @return the model kind associated to this resource
   */
  public Model.Kind getModelKind()
  {
    return Model.Kind.ORGANISATION;
  }

  @Override
  public boolean equals(Object o)
  {
    if(this == o) return true;
    if(o == null || getClass() != o.getClass()) return false;
    if(!super.equals(o)) return false;

    OrganisationResource that = (OrganisationResource) o;

    if(!_organisation.equals(that._organisation)) return false;

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + _organisation.hashCode();
    return result;
  }

  @Override
  public String toString()
  {
    return _organisation;
  }
}

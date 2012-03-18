
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
import com.pongasoft.kiwidoc.model.resource.OrganisationResource;
import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an organisation (for example org.apache)
 *
 * @author yan@pongasoft.com
 */
public class OrganisationModel implements Model<OrganisationResource>
{
  private final OrganisationResource _organisationResource;
  private final Collection<LibraryResource> _libraries;

  /**
   * Constructor
   */
  public OrganisationModel(OrganisationResource organisationResource,
                           Collection<String> libraries)
  {
    _organisationResource = organisationResource;
    Set<LibraryResource> libs = new HashSet<LibraryResource>();
    for(String library : libraries)
    {
      libs.add(new LibraryResource(organisationResource, library));
    }
    _libraries = Collections.unmodifiableSet(libs);
  }

  public Collection<LibraryResource> getLibraryResources()
  {
    return _libraries;
  }

  /**
   * @return the resource
   */
  public OrganisationResource getResource()
  {
    return _organisationResource;
  }

  /**
   * @return the model kind
   */
  public Kind getKind()
  {
    return Kind.ORGANISATION;
  }

  @Override
  public Collection<? extends Resource> getChildren()
  {
    return getLibraryResources();
  }

  /**
   * @return a version of this model with everything that is not part of the public api which has
   *         been stripped out. If the model itself is not part of the public api then
   *         <code>null</code> is returned!
   */
  public OrganisationModel toPublicAPI()
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
}
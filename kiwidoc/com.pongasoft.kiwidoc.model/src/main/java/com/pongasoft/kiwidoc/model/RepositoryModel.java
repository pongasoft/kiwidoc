
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

import com.pongasoft.kiwidoc.model.resource.OrganisationResource;
import com.pongasoft.kiwidoc.model.resource.RepositoryResource;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.LibraryResource;
import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/**
 * @author yan@pongasoft.com
 */
public class RepositoryModel implements Model<RepositoryResource>
{
  private final Map<OrganisationResource, Collection<LibraryResource>> _libraries;
  private final Map<LibraryResource, Collection<LibraryVersionResource>> _libraryVersions;

  /**
   * Constructor
   */
  public RepositoryModel(Collection<LibraryVersionResource> libraryVersions)
  {
    Map<LibraryResource, Collection<LibraryVersionResource>> lvs =
      new HashMap<LibraryResource, Collection<LibraryVersionResource>>();

    Map<OrganisationResource, Collection<LibraryResource>> libs =
      new HashMap<OrganisationResource, Collection<LibraryResource>>();

    for(LibraryVersionResource libraryVersion : libraryVersions)
    {
      LibraryResource library = libraryVersion.getLibraryResource();
      OrganisationResource organisation = library.getParent();

      Collection<LibraryVersionResource> lvsc = lvs.get(library);
      if(lvsc == null)
      {
        lvsc = new HashSet<LibraryVersionResource>();
        lvs.put(library, lvsc);
      }
      lvsc.add(libraryVersion);

      Collection<LibraryResource> lrc = libs.get(organisation);
      if(lrc == null)
      {
        lrc = new HashSet<LibraryResource>();
        libs.put(organisation, lrc);
      }
      lrc.add(library);
    }


    _libraries = Collections.unmodifiableMap(libs);
    _libraryVersions = Collections.unmodifiableMap(lvs);
  }

  public Collection<OrganisationResource> getOrganisationResources()
  {
    return _libraries.keySet();
  }

  public Collection<LibraryResource> getLibraries(OrganisationResource organisation)
  {
    return Collections.unmodifiableCollection(_libraries.get(organisation));
  }

  public Collection<LibraryResource> getLibraries()
  {
    return _libraryVersions.keySet();
  }

  public Collection<LibraryVersionResource> getLibraryVersions(LibraryResource library)
  {
    return Collections.unmodifiableCollection(_libraryVersions.get(library));
  }

  public Collection<LibraryVersionResource> getLibraryVersions()
  {
    Collection<LibraryVersionResource> libraryVersions = new HashSet<LibraryVersionResource>();
    for(Collection<LibraryVersionResource> collection : _libraryVersions.values())
    {
      libraryVersions.addAll(collection);
    }
    return libraryVersions;
  }

  @Override
  public Collection<? extends Resource> getChildren()
  {
    return getOrganisationResources();
  }

  /**
   * @return a version of this model with everything that is not part of the public api which has
   *         been stripped out. If the model itself is not part of the public api then
   *         <code>null</code> is returned!
   */
  public RepositoryModel toPublicAPI()
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
   * @return the resource
   */
  public RepositoryResource getResource()
  {
    return RepositoryResource.INSTANCE;
  }

  /**
   * @return the model kind
   */
  public Kind getKind()
  {
    return Kind.REPOSITORY;
  }
}

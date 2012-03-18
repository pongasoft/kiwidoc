
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

package com.pongasoft.kiwidoc.index.api;

import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.kiwidoc.model.resource.RepositoryResource;
import com.pongasoft.kiwidoc.model.Model;

import java.io.Serializable;
import java.util.Set;

/**
 * @author yan@pongasoft.com
 */
public abstract class BaseQuery implements Serializable, SearchQuery
{
  private static final long serialVersionUID = 1L;

  private Visibility _visibility = Visibility.publicOnly;
  private int _maxResults = 0;
  private Resource _baseResource = RepositoryResource.INSTANCE;
  private Resource _filter = null;
  private Set<Model.Kind> _resourceKinds = null;

  /**
   * Constructor
   */
  public BaseQuery(Visibility visibility)
  {
    _visibility = visibility;
  }

  public BaseQuery()
  {
  }

  public Visibility getVisibility()
  {
    return _visibility;
  }

  public void setVisibility(Visibility visibility)
  {
    _visibility = visibility;
  }

  public int getMaxResults()
  {
    return _maxResults;
  }

  public void setMaxResults(int maxResults)
  {
    if(maxResults < 0)
      throw new IllegalArgumentException(maxResults + " should be >=0");
    
    _maxResults = maxResults;
  }

  /**
   * @return the resource from which the result must be tailored.
   */
  public Resource getBaseResource()
  {
    return _baseResource;
  }

  public void setBaseResource(Resource baseResource)
  {
    _baseResource = baseResource;
  }

  /**
   * @return the resource for filtering: only resources 'below' this resource will be
   * part of the result
   */
  public Resource getFilter()
  {
    return _filter;
  }

  public void setFilter(Resource filter)
  {
    _filter = filter;
  }

  public Set<Model.Kind> getResourceKinds()
  {
    return _resourceKinds;
  }

  public void setResourceKinds(Set<Model.Kind> resourceKinds)
  {
    _resourceKinds = resourceKinds;
  }
}

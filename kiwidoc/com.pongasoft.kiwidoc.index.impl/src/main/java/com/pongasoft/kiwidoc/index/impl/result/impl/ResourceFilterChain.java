
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

package com.pongasoft.kiwidoc.index.impl.result.impl;

import com.pongasoft.kiwidoc.index.impl.result.api.ResourceFilter;
import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * @author yan@pongasoft.com
 */
public class ResourceFilterChain implements ResourceFilter
{
  private final Collection<ResourceFilter> _filters;

  /**
   * Constructor
   */
  public ResourceFilterChain(ResourceFilter... filters)
  {
    _filters = Arrays.asList(filters);
  }

  /**
   * Constructor
   */
  public ResourceFilterChain(Collection<ResourceFilter> filters)
  {
    _filters = filters;
  }

  /**
   * @return <code>true</code> if the resource should be part of the result
   */
  public boolean acceptResource(Resource resource)
  {
    for(ResourceFilter filter : _filters)
    {
      if(!filter.acceptResource(resource))
        return false;
    }
    
    return true;
  }

  /**
   * Handles efficient creation of the chain.
   *
   * @param filters
   * @return the chain
   */
  public static ResourceFilter createChain(Collection<ResourceFilter> filters)
  {
    ArrayList<ResourceFilter> newFilters = new ArrayList<ResourceFilter>(filters.size());

    for(ResourceFilter filter : filters)
    {
      if(filter != null && !(filter instanceof AlwaysAcceptResourceFilter))
        newFilters.add(filter);
    }

    if(newFilters.isEmpty())
      return AlwaysAcceptResourceFilter.INSTANCE;

    if(newFilters.size() == 1)
      return newFilters.get(0);

    return new ResourceFilterChain(newFilters);
  }
}

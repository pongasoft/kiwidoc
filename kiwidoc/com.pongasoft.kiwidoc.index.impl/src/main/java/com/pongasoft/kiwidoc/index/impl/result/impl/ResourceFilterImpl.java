
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
import com.pongasoft.util.core.misc.Utils;

/**
 * @author yan@pongasoft.com
 */
public class ResourceFilterImpl implements ResourceFilter
{
  private final Resource _filter;

  /**
   * Constructor
   */
  public ResourceFilterImpl(Resource filter)
  {
    _filter = filter;
  }

  /**
   * @return <code>true</code> if the resource should be part of the result
   */
  public boolean acceptResource(Resource resource)
  {
    return Utils.isEqual(getResourceAtSameDepth(resource), _filter);
  }

  private Resource getResourceAtSameDepth(Resource resource)
  {
    while(resource != null && resource.getDepth() != _filter.getDepth())
    {
      resource = resource.getParent();
    }

    return resource;
  }

}


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

package com.pongasoft.kiwidoc.index.impl;

import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.Map;
import java.util.HashMap;

/**
 * @author yan@pongasoft.com
 */
public class CanonicalResourceEncoder implements ResourceEncoder<Resource>
{
  private final Map<Resource, Resource> _map = new HashMap<Resource, Resource>();
  
  /**
   * Constructor
   */
  public CanonicalResourceEncoder()
  {
  }

  /**
   * Decodes the previously encoded resource.
   *
   * @param encodedResource the resource as encoded with {@link #encodeResource(Resource)}
   * @param <R>             type of the resource
   * @return the decoded resource
   */
  @SuppressWarnings("unchecked")
  public <R extends Resource> R decodeResource(Resource encodedResource)
  {
    return (R) toCanonicalResource(encodedResource);
  }

  /**
   * Decodes the previously encoded resources.
   *
   * @param encodedResources the resources as encoded with {@link #encodeResource(Resource)}
   * @return the decoded resources
   */
  public Resource[] decodeResources(Resource[] encodedResources)
  {
    for(int i = 0; i < encodedResources.length; i++)
    {
      encodedResources[i] = toCanonicalResource(encodedResources[i]);
    }
    return encodedResources;
  }

  /**
   * @param resource
   * @return the encoded resource (should be more compact that original resource...)
   */
  public Resource encodeResource(Resource resource)
  {
    return toCanonicalResource(resource);
  }

  @SuppressWarnings("unchecked")
  private Resource toCanonicalResource(Resource resource)
  {
    if(resource == null)
      return null;

    Resource res = _map.get(resource);

    if(res == null)
    {
      Resource parent = resource.getParent();
      res = resource.clone(toCanonicalResource(parent));
      _map.put(res, res);
    }

    return res;
  }
}


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

/**
 * @author yan@pongasoft.com
 */
public class NoOpResourceEncoder implements ResourceEncoder<Resource>
{
  public static final NoOpResourceEncoder INSTANCE = new NoOpResourceEncoder();

  public static NoOpResourceEncoder instance()
  {
    return INSTANCE;
  }

  /**
   * Constructor
   */
  public NoOpResourceEncoder()
  {
  }

  /**
   * @param resource
   * @return the encoded resource (should be more compact that original resource...)
   */
  public Resource encodeResource(Resource resource)
  {
    return resource;
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
    return (R) encodedResource;
  }

  /**
   * Decodes the previously encoded resources.
   *
   * @param encodedResources the resources as encoded with {@link #encodeResource(Resource)}
   * @return the decoded resources
   */
  public Resource[] decodeResources(Resource[] encodedResources)
  {
    return encodedResources;
  }
}

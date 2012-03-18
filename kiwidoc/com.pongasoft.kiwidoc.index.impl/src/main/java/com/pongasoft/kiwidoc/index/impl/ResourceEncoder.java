
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
public interface ResourceEncoder<T>
{
  /**
   * @param resource
   * @return the encoded resource (should be more compact that original resource...)
   */
  T encodeResource(Resource resource);

  /**
   * Decodes the previously encoded resource.
   *
   * @param encodedResource the resource as encoded with {@link #encodeResource(Resource)}
   * @param <R> type of the resource
   * @return the decoded resource
   */
  <R extends Resource> R decodeResource(T encodedResource); 

  /**
   * Decodes the previously encoded resources.
   *
   * @param encodedResources the resources as encoded with {@link #encodeResource(Resource)}
   * @return the decoded resources
   */
  Resource[] decodeResources(T[] encodedResources);
}
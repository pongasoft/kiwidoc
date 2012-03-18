
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

import java.net.URI;

/**
 * @author yan@pongasoft.com
 */
public interface PathManager
{
  /**
   * @param resource
   * @return the path to the resource
   */
  URI computePath(Resource resource);

  /**
   * @return the resource given its path (should have been generated with {@link #computePath(Resource)}.
   */
  Resource computeResource(URI path);
}
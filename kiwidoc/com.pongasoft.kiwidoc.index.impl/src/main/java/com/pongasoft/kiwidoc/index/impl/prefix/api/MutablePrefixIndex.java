
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

package com.pongasoft.kiwidoc.index.impl.prefix.api;

import com.pongasoft.util.core.exception.InternalException;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.kiwidoc.model.LVROverviewModel;

/**
 * @author yan@pongasoft.com
 */
public interface MutablePrefixIndex extends PrefixIndex
{
  /**
   * Adds the resource to the index.
   *
   * @param resource   the resource to add
   * @param isExported <code>true</code> if it is an exported resource, <code>false</code>
   *                   otherwise
   * @throws InternalException if there is something wrong
   */
  void indexResource(Resource resource, boolean isExported)
    throws InternalException;

  /**
   * Add all the resources from the libraru to the index.
   *
   * @param lvrOverviewModel   the resources to add
   * @throws InternalException if there is something wrong
   */
  void indexResources(LVROverviewModel lvrOverviewModel)
    throws InternalException;
}
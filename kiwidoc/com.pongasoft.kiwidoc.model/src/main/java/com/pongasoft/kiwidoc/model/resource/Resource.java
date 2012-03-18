
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

import com.pongasoft.kiwidoc.model.Model;

/**
 * @author yan@pongasoft.com
 */
public interface Resource<R extends Resource, P extends Resource>
{
  /**
   * @return the parent resource or <code>null</code> if there is no parent
   */
  P getParent();

  /**
   * @return the depth of the resource (root is 0, child of root is 1, etc...)
   */
  int getDepth();

  /**
   * @return <code>true</code> if it is the root
   */
  boolean isRoot();

  /**
   * @return the name of the resource
   */
  String getResourceName();

  /**
   * Clones this resource using the parent provided.
   *
   * @param parent the parent to use
   * @return a new resource using the parent provided
   */
  R clone(P parent);

  /**
   * @return the model kind associated to this resource
   */
  Model.Kind getModelKind();
}
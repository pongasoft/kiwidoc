
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

/**
 * @author yan@pongasoft.com
 */
public interface Exportable<T extends Exportable>
{
  /**
   * @return a version of this entity with everything that is not part of the public api which has
   * been stripped out. If the entity itself is not part of the public api then <code>null</code>
   * is returned! (note that it can safely be downcasted to the same entity type...). If the entity
   * has not changed then <code>this</code> is returned.
   */
  T toPublicAPI();

  /**
   * @return <code>true</code> if this entity is part of the public api
   */
  boolean isPublicAPI();
}
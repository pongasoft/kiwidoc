
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

import com.pongasoft.util.core.enums.Value;
import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.Collection;

/**
 * @author yan@pongasoft.com
 */
public interface Model<R extends Resource> extends Extensible<Model.Kind>, Exportable<Model>
{
  public static enum Kind
  {
    @Value("r") REPOSITORY,
    @Value("o") ORGANISATION,
    @Value("b") LIBRARY,
    @Value("l") LIBRARY_VERSION,
    @Value("p") PACKAGE,
    @Value("c") CLASS,
    @Value("m") MANIFEST,
    @Value("v") OVERVIEW,
    @Value("h") HIERARCHY
  }

  /**
   * @return the resource
   */
  R getResource();

  /**
   * @return the model kind
   */
  Kind getKind();

  /**
   * @return all the children of this model (non recursive)
   */
  Collection<? extends Resource> getChildren();
}
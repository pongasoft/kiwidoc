
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

package com.pongasoft.kiwidoc.index.impl.keyword.api;

import com.pongasoft.util.core.exception.InternalException;
import com.pongasoft.kiwidoc.model.Model;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;

import java.util.Collection;

/**
 * @author yan@pongasoft.com
 */
public interface MutableKeywordIndex extends KeywordIndex
{
  /**
   * Adds the model to the index.
   *
   * @param model the model to add
   * @throws InternalException if there is something wrong
   */
  void indexModel(Model model) throws InternalException;

  /**
   * Removes the resource from the index
   *
   * @param resource the resource to remove
   * @throws InternalException if there is something wrong
   */
  void unindexResource(Resource resource) throws InternalException;

  /**
   * Removes the resources from the index (batch version)
   *
   * @param resources the resources to remove
   * @throws InternalException if there is something wrong
   */
  void batchUnindexResources(Collection<? extends Resource> resources) throws InternalException;

  /**
   * Unindex resources
   */
  void batchUnindexResources(Collection<? extends Resource> resources, String field)
    throws InternalException;

  /**
   * Batch index multiple models.
   *
   * @param models the models to add
   * @throws InternalException if there is something wrong
   */
  void batchIndexModels(Collection<? extends Model> models) throws InternalException;

  /**
   * Removes the entire library version from the index
   *
   * @throws InternalException if there is something wrong
   */
  void unindexLibraryVersion(LibraryVersionResource libraryVersion) throws InternalException;

  /**
   * Optimizes the index.
   * 
   * @throws InternalException if there is something wrong
   */
  void optimize() throws InternalException;
}
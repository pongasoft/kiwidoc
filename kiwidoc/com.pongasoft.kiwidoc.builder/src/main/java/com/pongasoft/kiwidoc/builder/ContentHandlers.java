
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

package com.pongasoft.kiwidoc.builder;

import com.pongasoft.kiwidoc.model.Model;
import com.pongasoft.kiwidoc.model.resource.Resource;

/**
 * @author yan@pongasoft.com
 */
public interface ContentHandlers
{
  /**
   * Loads the content of the given resource.
   *
   * @param resource the resource to load
   * @return the content as an object since it depends on which content is being read (ex: manifest,
   *         library, packages, class) (never <code>null</code>)
   * @throws NoSuchContentException if the content does not exist
   * @throws StoreException         if there is a problem reading the content.
   */
  Model loadContent(Resource resource) throws NoSuchContentException, StoreException;

  /**
   * @return <code>true</code> if the content for the given resource exists
   * @throws StoreException if there is a problem
   */
  boolean exists(Resource resource) throws StoreException;

  /**
   * Deletes the content pointed to by this resource.
   *
   * @param resource the resource to delete
   * @return <code>true</code> if the resource was deleted, <code>false</code> if it did not exist
   *         in the first place
   * @throws StoreException if there is a problem
   */
  boolean deleteContent(Resource resource) throws StoreException;

  /**
   * Saves the model.
   *
   * @param model the mode to store
   * @return the resource
   * @throws StoreException if there is a problem
   */
  Resource saveContent(Model model) throws StoreException;
}

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

package com.pongasoft.kiwidoc.index.api;

import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.kiwidoc.model.Model;
import com.pongasoft.util.core.exception.InternalException;

import java.util.Collection;
import java.util.Map;

/**
 * @author yan@pongasoft.com
 */
public interface KiwidocIndex
{
  public interface Stats
  {
    int getPackageCount();
    int getClassCount();
    int getLibraryCount();
  }

  /**
   * Return the resources that starts with the given prefix.
   *
   * @return a (non <code>null</code>) collection of resources
   * @throws InternalException if something wrong happened
   */
  SearchResults findByPrefix(PrefixQuery query)
    throws MalformedQueryException, InternalException;

  /**
   * Find all resources that matches the query.
   *
   * @return all the matching resources
   * @throws MalformedQueryException if the query cannot be parsed
   * @throws InternalException       if there is an internal problem
   */
  SearchResults findByKeyword(KeywordQuery query)
    throws MalformedQueryException, InternalException;

  /**
   * Highlights the provided results obtained using the provided query.
   *
   * @param query  the query from which the results were computed
   * @param models the models to highlight
   * @param <R>    the type of resource
   * @return a map representing for each entry in the model its associated resource and highlight
   * @throws MalformedQueryException if the query cannot be parsed
   * @throws InternalException if there is an internal problem
   */
  <R extends Resource> Map<R, String[]> highlightResults(KeywordQuery query,
                                                         Collection<Model<R>> models)
    throws InternalException, MalformedQueryException;

  /**
   * Runs a combined search: first prefix, then keyword
   *
   * @param query the query for the combined search
   * @return the combined search results
   */
  SearchResults combinedSearch(KeywordQuery query)
    throws MalformedQueryException, InternalException;

  /**
   * @return the stats for this index
   */
  Stats getStats();
}
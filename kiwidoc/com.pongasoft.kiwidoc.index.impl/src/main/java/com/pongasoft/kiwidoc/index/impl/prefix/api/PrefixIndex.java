
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
import com.pongasoft.kiwidoc.index.api.SearchResults;
import com.pongasoft.kiwidoc.index.api.PrefixQuery;
import com.pongasoft.kiwidoc.index.api.MalformedQueryException;
import com.pongasoft.kiwidoc.index.impl.result.api.SearchResultsAccumulator;

/**
 * @author yan@pongasoft.com
 */
public interface PrefixIndex
{
  /**
   * Return the resources that starts with the given prefix.
   *
   * @return a (non <code>null</code>) collection of resources
   * @throws InternalException if something wrong happened
   */
  SearchResults findResources(PrefixQuery query)
    throws MalformedQueryException, InternalException;

  /**
   * Runs the search and accumulate the resources in the accumulator.
   *
   * @throws InternalException if something wrong happened
   */
  void findResources(PrefixQuery query, SearchResultsAccumulator accumulator)
    throws MalformedQueryException, InternalException;
}
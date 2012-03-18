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

package com.pongasoft.kiwidoc.index.impl.prefix.impl;

import com.pongasoft.kiwidoc.index.api.MalformedQueryException;
import com.pongasoft.kiwidoc.index.api.PrefixQuery;
import com.pongasoft.kiwidoc.index.api.SearchResult;
import com.pongasoft.kiwidoc.index.api.SearchResults;
import com.pongasoft.kiwidoc.index.impl.prefix.api.MutablePrefixIndex;
import com.pongasoft.kiwidoc.index.impl.result.api.SearchResultsAccumulator;
import com.pongasoft.kiwidoc.model.LVROverviewModel;
import com.pongasoft.kiwidoc.model.Model;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.util.core.exception.InternalException;

import java.util.Collections;

/**
 * @author yan@pongasoft.com
 */
public class NoResultsPrefixIndex implements MutablePrefixIndex
{
  /**
   * Constructor
   */
  public NoResultsPrefixIndex()
  {
  }

  @Override
  public void indexResource(Resource resource, boolean isExported) throws InternalException
  {
    // nothing to do
  }

  @Override
  public void indexResources(LVROverviewModel lvrOverviewModel) throws InternalException
  {
    // nothing to do
  }

  @Override
  public SearchResults findResources(PrefixQuery query)
    throws MalformedQueryException, InternalException
  {
    return new SearchResults(query.getVisibility(),
                             Collections.<SearchResult>emptyList(),
                             Collections.<Model.Kind, Integer>emptyMap(),
                             Collections.<LibraryVersionResource, Integer>emptyMap(),
                             0);
  }

  @Override
  public void findResources(PrefixQuery query, SearchResultsAccumulator accumulator)
    throws MalformedQueryException, InternalException
  {
  }
}

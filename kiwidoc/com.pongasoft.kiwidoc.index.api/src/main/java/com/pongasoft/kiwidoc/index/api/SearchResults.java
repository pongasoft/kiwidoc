
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

import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.Model;

import java.util.Collection;
import java.util.Map;
import java.util.Collections;
import java.io.Serializable;

/**
 * @author yan@pongasoft.com
 */
public class SearchResults implements Serializable
{
  private static final long serialVersionUID = 1L;
  public static final SearchResults NO_RESULTS = new SearchResults(Visibility.publicOnly,
                                                                   Collections.<SearchResult>emptyList(),
                                                                   Collections.<Model.Kind, Integer>emptyMap(),
                                                                   Collections.<LibraryVersionResource, Integer>emptyMap(),
                                                                   0);

  private final Collection<SearchResult> _topResults;
  private final Map<LibraryVersionResource, Integer> _groupByLibrariesCount;
  private final Map<Model.Kind, Integer> _groupByKindCount;
  private final int _totalResultsCount;
  private final Visibility _visibility;

  public SearchResults(Visibility visibility,
                       Collection<SearchResult> topResults,
                       Map<Model.Kind, Integer> groupByKindCount,
                       Map<LibraryVersionResource, Integer> groupByLibrariesCount,
                       int totalResultsCount)
  {
    _visibility = visibility;
    _topResults = topResults;
    _groupByKindCount = groupByKindCount;
    _groupByLibrariesCount = groupByLibrariesCount;
    _totalResultsCount = totalResultsCount;
  }

  /**
   * @return the top results (according to the sort criteria)
   */
  public Collection<SearchResult> getTopResults()
  {
    return _topResults;
  }

  /**
   * @return the visibility
   */
  public Visibility getVisibility()
  {
    return _visibility;
  }

  /**
   * @return the total number of results (may be bigger thant <code>topResults.size</code>!)
   */
  public int getTotalResultsCount()
  {
    return _totalResultsCount;
  }

  public Map<LibraryVersionResource, Integer> getGroupByLibrariesCount()
  {
    return _groupByLibrariesCount;
  }

  public Map<Model.Kind, Integer> getGroupByKindCount()
  {
    return _groupByKindCount;
  }

  /**
   * @return <code>true</code> if there is more results that are not being returned
   */
  public boolean getHasMoreResults()
  {
    return _topResults.size() < _totalResultsCount;
  }
}

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

package com.pongasoft.kiwidoc.index.impl.result.impl;

import com.pongasoft.kiwidoc.index.api.BaseQuery;
import com.pongasoft.kiwidoc.index.api.SearchResults;
import com.pongasoft.kiwidoc.index.api.SearchResult;
import com.pongasoft.kiwidoc.index.impl.result.api.ScoreBooster;
import com.pongasoft.kiwidoc.index.impl.result.api.SearchResultsAccumulator;
import com.pongasoft.kiwidoc.index.impl.result.api.ResourceFilter;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.Model;
import com.pongasoft.util.core.reflect.ReflectUtils;
import com.pongasoft.util.core.sort.MergeSortAccumulator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.EnumMap;

/**
 * @author yan@pongasoft.com
 */
public class SearchResultsAccumulatorImpl implements SearchResultsAccumulator
{
  public static final Resource LATEST_JDK_RESOURCE = new LibraryVersionResource("java", "j2se", "1.6");

  private final MutableSearchResult _msr = new MutableSearchResult();
  private final MergeSortAccumulator<MutableSearchResult> _accumulator;
  private final Map<LibraryVersionResource, Integer> _groupByLibrariesCount =
    new HashMap<LibraryVersionResource, Integer>();
  private final Set<Resource> _alreadyAcceptedResources = new HashSet<Resource>();
  private final Map<Model.Kind, Integer> _groupByKindCount = 
    new EnumMap<Model.Kind, Integer>(Model.Kind.class);

  private int _totalResultsCount = 0;
  private BaseQuery _query;
  private ScoreBooster _scoreBooster;
  private ResourceFilter _resourceFilter;

  /**
   * Constructor
   */
  public SearchResultsAccumulatorImpl(BaseQuery query)
  {
    setQuery(query);

    Class<MutableSearchResult> resultClass =
      ReflectUtils.toGenericClass(MutableSearchResult.class);

    _accumulator =
      new MergeSortAccumulator<MutableSearchResult>(resultClass,
                                                    query.getMaxResults(),
                                                    MutableSearchResult.Comparator.instance(),
                                                    MutableSearchResult.Copier.instance());
  }

  /**
   * Called for each search result
   */
  public void accumulateSearchResult(Resource resource, float score)
  {
    if(_resourceFilter.acceptResource(resource))
    {
      _totalResultsCount++;
      _msr.init(new SearchResult(_query.getType(), resource),
                _scoreBooster.boostScore(resource, score));
      _accumulator.add(_msr);
      
      computeFacets(resource);
    }
  }

  private void computeFacets(Resource resource)
  {
    addGroupByLibrariesFacet(resource);
    addGroupByKindFacet(resource);
  }

  private void addGroupByKindFacet(Resource resource)
  {
    Model.Kind kind = resource.getModelKind();

    Integer count = _groupByKindCount.get(kind);
    if(count == null)
    {
      count = 0;
    }
    count++;
    _groupByKindCount.put(kind, count);
  }

  private void addGroupByLibrariesFacet(Resource resource)
  {
    LibraryVersionResource lvr = LibraryVersionResource.extractLVR(resource);
    Integer count = _groupByLibrariesCount.get(lvr);
    if(count == null)
    {
      count = 0;
    }
    count++;
    _groupByLibrariesCount.put(lvr, count);
  }


  public BaseQuery getQuery()
  {
    return _query;
  }

  public void setQuery(BaseQuery query)
  {
    if(_query != null && (_query.getMaxResults() != query.getMaxResults()))
      throw new IllegalArgumentException("maxResults");

    _scoreBooster = ProximityScoreBooster.create(query.getBaseResource(), LATEST_JDK_RESOURCE);

    Collection<ResourceFilter> resourceFilters = new ArrayList<ResourceFilter>();

    if(query.getFilter() != null)
      resourceFilters.add(new ResourceFilterImpl(query.getFilter()));

    if(query.getResourceKinds() != null && !query.getResourceKinds().isEmpty())
      resourceFilters.add(new ModelKindResourceFilter(query.getResourceKinds()));

    _resourceFilter =
      new RemoveDuplicatesResourceFilter(ResourceFilterChain.createChain(resourceFilters),
                                         _alreadyAcceptedResources);

    _query = query;
  }

  public int getTotalResultsCount()
  {
    return _totalResultsCount;
  }

  /**
   * @return the search results
   */
  public SearchResults getSearchResults()
  {
    MutableSearchResult[] results = _accumulator.close();
    Collection<SearchResult> resources = new ArrayList<SearchResult>(results.length);
    for(MutableSearchResult result : results)
    {
      resources.add(result.getSearchResult());
    }
    return new SearchResults(_query.getVisibility(),
                             resources,
                             _groupByKindCount,
                             _groupByLibrariesCount, 
                             _totalResultsCount);
  }
}


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

package com.pongasoft.kiwidoc.index.impl;

import com.pongasoft.kiwidoc.index.api.MutableKiwidocIndex;
import com.pongasoft.kiwidoc.index.api.MalformedQueryException;
import com.pongasoft.kiwidoc.index.api.KeywordQuery;
import com.pongasoft.kiwidoc.index.api.SearchResults;
import com.pongasoft.kiwidoc.index.api.PrefixQuery;
import com.pongasoft.kiwidoc.index.api.Visibility;
import com.pongasoft.kiwidoc.index.impl.keyword.api.MutableKeywordIndex;
import com.pongasoft.kiwidoc.index.impl.prefix.api.MutablePrefixIndex;
import com.pongasoft.kiwidoc.index.impl.result.impl.SearchResultsAccumulatorImpl;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.kiwidoc.model.Model;
import com.pongasoft.kiwidoc.model.LVROverviewModel;
import com.pongasoft.util.core.annotations.FieldInitializer;
import com.pongasoft.util.core.annotations.ObjectInitializer;
import com.pongasoft.util.core.exception.InternalException;

import java.util.Collection;
import java.util.Map;

/**
 * @author yan@pongasoft.com
 */
public class KiwidocIndexImpl implements MutableKiwidocIndex
{
  private MutablePrefixIndex _prefixIndex;
  private MutableKeywordIndex _keywordIndex;
  private final KiwidocIndexStats _stats = new KiwidocIndexStats(0, 0, 0);

  @ObjectInitializer
  public KiwidocIndexImpl()
  {
  }

  /**
   * Constructor
   */
  public KiwidocIndexImpl(MutablePrefixIndex prefixIndex, MutableKeywordIndex keywordIndex)
  {
    _prefixIndex = prefixIndex;
    _keywordIndex = keywordIndex;
  }

  public MutablePrefixIndex getPrefixIndex()
  {
    return _prefixIndex;
  }

  @FieldInitializer
  public void setPrefixIndex(MutablePrefixIndex prefixIndex)
  {
    _prefixIndex = prefixIndex;
  }

  public MutableKeywordIndex getKeywordIndex()
  {
    return _keywordIndex;
  }

  @FieldInitializer
  public void setKeywordIndex(MutableKeywordIndex keywordIndex)
  {
    _keywordIndex = keywordIndex;
  }

  @Override
  public Stats getStats()
  {
    synchronized(_stats)
    {
      return new KiwidocIndexStats(_stats.getLibraryCount(),
                                   _stats.getPackageCount(),
                                   _stats.getClassCount());
    }
  }

  @FieldInitializer
  public void setStats(Stats stats)
  {
    synchronized(_stats)
    {
      _stats.setLibraryCount(stats.getLibraryCount());
      _stats.setPackageCount(stats.getPackageCount());
      _stats.setClassCount(stats.getClassCount());
    }
  }

  /**
   * Return the resources that starts with the given prefix.
   *
   * @return a (non <code>null</code>) collection of resources
   * @throws InternalException if something wrong happened
   */
  public SearchResults findByPrefix(PrefixQuery query)
    throws MalformedQueryException, InternalException
  {
    return _prefixIndex.findResources(query);
  }

  /**
   * Adds the resource to the index.
   *
   * @param resource   the resource to add
   * @param isExported <code>true</code> if it is an exported resource, <code>false</code>
   *                        otherwise
   * @throws InternalException if there is something wrong
   */
  public void indexResource(Resource resource, boolean isExported)
    throws InternalException
  {
    // YP note: used only for testing!
    _prefixIndex.indexResource(resource, isExported);
  }

  /**
   * Add all the resources from the libraru to the index.
   *
   * @param lvrOverviewModel the resources to add
   * @throws InternalException if there is something wrong
   */
  public void indexResources(LVROverviewModel lvrOverviewModel) throws InternalException
  {
    _prefixIndex.indexResources(lvrOverviewModel);

    synchronized(_stats)
    {
      _stats.setPackageCount(_stats.getPackageCount() + lvrOverviewModel.getPackageCount());
      _stats.setClassCount(_stats.getClassCount() + lvrOverviewModel.getClassCount());
      _stats.setLibraryCount(_stats.getLibraryCount() + 1);
    }
  }

  /**
   * Find all resources that matches the query.
   *
   * @return all the matching resources
   * @throws MalformedQueryException if the query cannot be parsed
   * @throws InternalException       if there is an internal problem
   */
  public SearchResults findByKeyword(KeywordQuery query)
    throws MalformedQueryException, InternalException
  {
    return _keywordIndex.findResources(query);
  }

  /**
   * Runs a combined search: first prefix, then keyword
   *
   * @param query the query for the combined search
   * @return the combined search results
   */
  public SearchResults combinedSearch(KeywordQuery query)
    throws MalformedQueryException, InternalException
  {
    PrefixQuery pq = new PrefixQuery(query.getKeyword());

    pq.setVisibility(query.getVisibility());
    pq.setMaxResults(query.getMaxResults());
    pq.setBaseResource(query.getBaseResource());
    pq.setFilter(query.getFilter());
    pq.setResourceKinds(query.getResourceKinds());

    SearchResultsAccumulatorImpl accumulator = new SearchResultsAccumulatorImpl(pq);
    _prefixIndex.findResources(pq, accumulator);

    // not enought results... add javadoc results
    if(accumulator.getTotalResultsCount() < query.getMaxResults())
    {
      accumulator.setQuery(query);
      _keywordIndex.findResources(query, accumulator);
    }

    if(accumulator.getTotalResultsCount() == 0)
    {
      if(query.getVisibility() == Visibility.publicOnly)
      {
        query.setVisibility(Visibility.publicAndPrivate);
        try
        {
          return combinedSearch(query);
        }
        finally
        {
          query.setVisibility(Visibility.publicOnly);
        }
      }
    }

    return accumulator.getSearchResults();
  }

  /**
   * Highlights the provided results obtained using the provided query.
   *
   * @param query  the query from which the results were computed
   * @param models the models to highlight
   * @param <R>    the type of resource
   * @return a map representing for each entry in the model its associated resource and highlight
   * @throws MalformedQueryException if the query cannot be parsed
   * @throws InternalException       if there is an internal problem
   */
  public <R extends Resource> Map<R, String[]> highlightResults(KeywordQuery query,
                                                                Collection<Model<R>> models)
    throws InternalException, MalformedQueryException
  {
    return _keywordIndex.highlightResults(query, models);
  }
}


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

import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.kiwidoc.index.api.SearchResult;
import com.pongasoft.kiwidoc.index.api.SearchQuery;
import com.pongasoft.kiwidoc.model.resource.ResourceComparator;
import com.pongasoft.util.core.sort.MergeSortAccumulator;

/**
 * @author yan@pongasoft.com
 */
public class MutableSearchResult
{
  public static class Copier implements MergeSortAccumulator.ObjectCopier<MutableSearchResult>
  {
    public static final Copier INSTANCE = new Copier();

    public static Copier instance()
    {
      return INSTANCE;
    }

    public MutableSearchResult copy(MutableSearchResult msr)
    {
      MutableSearchResult mutableSearchResult = new MutableSearchResult();
      mutableSearchResult.init(msr.getSearchResult(), msr.getScore());
      return mutableSearchResult;
    }
  }

  public static class Comparator implements java.util.Comparator<MutableSearchResult>
  {
    public static final Comparator INSTANCE = new Comparator();

    public static Comparator instance()
    {
      return INSTANCE;
    }

    public int compare(MutableSearchResult o1, MutableSearchResult o2)
    {
      SearchQuery.Type qt1 = o1.getSearchResult().getQueryType();
      SearchQuery.Type qt2 = o2.getSearchResult().getQueryType();

      if(qt1 != qt2)
      {
        // prefix is higher priority than javadoc...
        if(qt1 == SearchQuery.Type.prefix)
          return -1;
        else
          return 1;
      }

      float score = o2.getScore() - o1.getScore();
      if(score != 0)
      {
        if(score < 0)
          return -1;
        else
          return 1;
      }

      return ResourceComparator.INSTANCE.compare(o1.getResource(), o2.getResource());
    }
  }

  private SearchResult _searchResult;
  private float _score;

  /**
   * Constructor
   */
  public MutableSearchResult()
  {
  }

  void init(SearchResult resource, float score)
  {
    _searchResult = resource;
    _score = score;
  }

  public SearchResult getSearchResult()
  {
    return _searchResult;
  }

  public Resource getResource()
  {
    return getSearchResult().getResource();
  }

  public float getScore()
  {
    return _score;
  }
}

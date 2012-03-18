
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

package com.pongasoft.kiwidoc.index.impl.lucene.impl;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.HitCollector;
import com.pongasoft.kiwidoc.index.impl.lucene.api.LuceneIndexSearcher;
import com.pongasoft.kiwidoc.index.impl.lucene.api.LuceneHitCollector;

import java.io.IOException;

/**
 * @author yan@pongasoft.com
 */
public class LuceneIndexSearcherImpl<T> implements LuceneIndexSearcher<T>
{
  private final IndexSearcher _indexSearcher;
  private final T[] _userData;

  /**
   * Constructor
   */
  public LuceneIndexSearcherImpl(IndexSearcher indexSearcher, T[] userData)
  {
    _indexSearcher = indexSearcher;
    _userData = userData;
  }

  /**
   * Uses a hit collector with user data
   *
   * @return the same collector provided
   */
  public LuceneHitCollector<T> search(Query query, final LuceneHitCollector<T> collector)
    throws IOException
  {
    _indexSearcher.search(query, new HitCollector()
    {
      @Override
      public void collect(int doc, float score)
      {
        collector.collect(doc, score, _userData[doc]);
      }
    });
    return collector;
  }

  /**
   * @return the underlying lucene index searcher
   */
  public IndexSearcher getIndexSearcher()
  {
    return _indexSearcher;
  }
}

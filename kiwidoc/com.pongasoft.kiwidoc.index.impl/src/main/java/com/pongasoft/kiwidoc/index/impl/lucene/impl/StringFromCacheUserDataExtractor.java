
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

import com.pongasoft.kiwidoc.index.impl.lucene.api.UserDataExtractor;
import com.pongasoft.kiwidoc.index.impl.lucene.api.LuceneDirectory;
import com.pongasoft.util.core.annotations.FieldInitializer;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.FieldCache;

import java.io.IOException;

/**
 * @author yan@pongasoft.com
 */
public class StringFromCacheUserDataExtractor implements UserDataExtractor<String>
{
  private String _fieldName = LuceneDirectory.DEFAULT_ID_FIELD_NAME;

  public StringFromCacheUserDataExtractor()
  {
  }

  /**
   * Constructor
   */
  public StringFromCacheUserDataExtractor(String fieldName)
  {
    _fieldName = fieldName;
  }

  public String getFieldName()
  {
    return _fieldName;
  }

  @FieldInitializer
  public void setFieldName(String fieldName)
  {
    _fieldName = fieldName;
  }

  /**
   * Extracts the user data from the searcher (could be store as payload or others...)
   */
  public String[] extractUserData(IndexSearcher indexSearcher) throws IOException
  {
    return FieldCache.DEFAULT.getStrings(indexSearcher.getIndexReader(), _fieldName);
  }
}


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

package com.pongasoft.kiwidoc.index.impl.keyword.impl;

import org.apache.lucene.document.Document;
import com.pongasoft.kiwidoc.model.Model;
import com.pongasoft.kiwidoc.index.impl.lucene.api.LuceneDirectory;

/**
 * @author yan@pongasoft.com
 */
public interface DocumentFactory<M extends Model>
{
  static final String ID_FIELD = LuceneDirectory.DEFAULT_ID_FIELD_NAME;
  static final String LIBRARY_VERSION_FIELD = "lv";
  static final String CLASS_NAME_FIELD = "cn";
  static final String PACKAGE_NAME_FIELD = "pn";
  static final String BODY_FIELD = "pb";
  static final String FIELD_SEPARATOR = "\n";

  /**
   * @param model the model to process
   * @return the document for the given model
   */
  Document createDocument(M model);
}
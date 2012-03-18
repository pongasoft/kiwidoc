
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

package com.pongasoft.kiwidoc.index.impl.lucene.api;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;

import java.io.IOException;

/**
 * Interface wrapper around {@link org.apache.lucene.index.IndexWriter} to handle transaction
 * properly.
 *
 * @author yan@pongasoft.com
 */
public interface LuceneIndexWriter
{
  void addDocument(Document doc) throws IOException;

  void deleteDocuments(Term term) throws IOException;

  void prepareCommit() throws IOException;

  void commit() throws IOException;

  void rollback() throws IOException;

  void addIndexesNoOptimize(Directory[] directories) throws IOException;

  void optimize() throws IOException;

  /**
   * This method <em>must</em> be called in a finally block and happen no matter what!
   */
  void close() throws IOException;
}
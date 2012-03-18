
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

import org.apache.lucene.index.IndexReader;

import java.io.IOException;

/**
 * Encapsulates a lucene directory to hide the internals of opening
 * and getting readers vs writers.
 *
 * @author yan@pongasoft.com */
public interface LuceneDirectory<T>
{
  static final String DEFAULT_ID_FIELD_NAME = "id";

  /**
   * Opens the directory. Equivalent to calling {@link #open(boolean)} with <code>false</code>.
   *
   * @throws IOException when there is an io related problem
   */
  void open() throws IOException;

  /**
   * Opens the directory. Passing <code>true</code> will start with an
   * empty directory.
   *
   * @param create <code>true</code> if the directory needs to be created/erased
   * @throws IOException when there is an io related problem
   */
  void open(boolean create) throws IOException;

  /**
   * @return <code>true</code> if the directory is opened
   */
  boolean isOpen();

  /**
   * @return a reader of the directory (do not close it!)
   * @throws IOException when there is an io related problem
   */
  IndexReader getReader() throws IOException;

  /**
   * @return a searcher of the directory (do not close it!)
   * @throws IOException when there is an io related problem
   */
  LuceneIndexSearcher<T> getSearcher() throws IOException;

  /**
   * @return a writer of the directory
   * @see LuceneIndexWriter#close() for the contract on the returned value
   * @throws IOException when there is an io related problem
   */
  LuceneIndexWriter getWriter() throws IOException;

  /**
   * Merges the content of <code>this</code> with <code>directory</code>. At the
   * end of the operation <code>this</code> is unchanged and
   * <code>directory</code> contains both data.
   *
   * @param directory the directory to merge
   * @throws IOException when there is an io related problem
   */
  void merge(LuceneDirectory directory) throws IOException;

  /**
   * Optimizes the directory
   * @throws IOException when there is an io related problem
   */
  void optimize() throws IOException;

  /**
   * Closes the directory. Should not use afterwards (until calling {@link #open(boolean)}).
   * @throws IOException when there is an io related problem
   */
  void close() throws IOException;
}

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

import com.pongasoft.kiwidoc.index.impl.lucene.api.LuceneIndexWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;

import java.io.IOException;

/**
 * @author yan@pongasoft.com
 */
class LuceneIndexWriterImpl implements LuceneIndexWriter
{
  private final LuceneDirectoryImpl _directory;

  private IndexWriter _indexWriter;

  private boolean _commited = false;

  /**
   * Constructor
   */
  public LuceneIndexWriterImpl(LuceneDirectoryImpl directory, IndexWriter indexWriter)
  {
    _directory = directory;
    _indexWriter = indexWriter;
  }

  public IndexWriter getIndexWriter()
  {
    return _indexWriter;
  }

  public void addDocument(Document doc) throws IOException
  {
    _indexWriter.addDocument(doc);
  }

  public void deleteDocuments(Term term) throws IOException
  {
    _indexWriter.deleteDocuments(term);
  }

  public void prepareCommit() throws IOException
  {
    _indexWriter.prepareCommit();
  }

  public void commit() throws IOException
  {
    _commited = true;
  }

  public void rollback() throws IOException
  {
    _commited = false;
    _indexWriter.rollback();
    _indexWriter = _directory.writerRollback();
  }

  public void addIndexesNoOptimize(Directory[] directories) throws IOException
  {
    _indexWriter.addIndexesNoOptimize(directories);
  }

  public void optimize() throws IOException
  {
    _indexWriter.optimize();
  }

  /**
   * This method should be called in a finally block and happen no matter what!
   */
  public void close() throws IOException
  {
    if(_commited)
      _indexWriter.commit();
    else
    {
      _indexWriter.rollback();
      _indexWriter = _directory.writerRollback();
    }
  }
}

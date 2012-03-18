
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

import com.pongasoft.kiwidoc.index.impl.lucene.api.LuceneDirectory;
import com.pongasoft.kiwidoc.index.impl.lucene.api.DirectoryFactory;
import com.pongasoft.kiwidoc.index.impl.lucene.api.LuceneIndexWriter;
import com.pongasoft.kiwidoc.index.impl.lucene.api.LuceneIndexSearcher;
import com.pongasoft.kiwidoc.index.impl.lucene.api.UserDataExtractor;
import com.pongasoft.util.core.annotations.ObjectInitializer;
import com.pongasoft.util.core.annotations.FieldInitializer;
import com.pongasoft.util.core.annotations.LifecycleInit;
import com.pongasoft.util.core.annotations.LifecycleDestroy;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * This implementation of the directory makes sure to close the reader or the writer appropriately.
 * The idea is that until there is no write, the reader can remain opened which should improve
 * performance. In the case when there is 1 read for 1 write then it has the same behavior of
 * opening a reader each time (so it is not worse). Note that this class is thread safe.
 *
 * @author yan@pongasoft.com
 */
public class LuceneDirectoryImpl<T> implements LuceneDirectory<T>
{
  public static final Log log = LogFactory.getLog(LuceneDirectoryImpl.class);

  private DirectoryFactory _directoryFactory;
  private Analyzer _analyzer;
  private Similarity _similarity = Similarity.getDefault();
  private IndexWriter.MaxFieldLength _maxFieldLength = IndexWriter.MaxFieldLength.UNLIMITED;
  private UserDataExtractor<T> _userDataExtractor = null;

  private Directory _directory = null;

  private IndexReader _reader = null;
  private LuceneIndexSearcher<T> _searcher = null;
  private IndexWriter _writer = null;

  /**
   * Constructor
   */
  public LuceneDirectoryImpl(DirectoryFactory directoryFactory, Analyzer analyzer)
  {
    this(directoryFactory, analyzer, Similarity.getDefault());
  }

  /**
   * Constructor
   */
  public LuceneDirectoryImpl(DirectoryFactory directoryFactory,
                             Analyzer analyzer,
                             Similarity similarity)
  {
    _directoryFactory = directoryFactory;
    _analyzer = analyzer;
    _similarity = similarity;
  }

  @ObjectInitializer
  public LuceneDirectoryImpl()
  {
  }

  public IndexWriter.MaxFieldLength getMaxFieldLength()
  {
    return _maxFieldLength;
  }

  @FieldInitializer
  public void setMaxFieldLength(IndexWriter.MaxFieldLength maxFieldLength)
  {
    _maxFieldLength = maxFieldLength;
  }

  public DirectoryFactory getDirectoryFactory()
  {
    return _directoryFactory;
  }

  @FieldInitializer
  public void setDirectoryFactory(DirectoryFactory directoryFactory)
  {
    _directoryFactory = directoryFactory;
  }

  public Analyzer getAnalyzer()
  {
    return _analyzer;
  }

  @FieldInitializer
  public void setAnalyzer(Analyzer analyzer)
  {
    _analyzer = analyzer;
  }

  public Similarity getSimilarity()
  {
    return _similarity;
  }

  @FieldInitializer
  public void setSimilarity(Similarity similarity)
  {
    _similarity = similarity;
  }

  public UserDataExtractor<T> getUserDataExtractor()
  {
    return _userDataExtractor;
  }

  @FieldInitializer(optional = true)
  public void setUserDataExtractor(UserDataExtractor<T> userDataExtractor)
  {
    _userDataExtractor = userDataExtractor;
  }

  /**
   * Opens the directory. Equivalent to calling {@link #open(boolean)} with <code>false</code>.
   *
   * @throws IOException when there is an io related problem
   */
  @LifecycleInit
  public void open() throws IOException
  {
    open(false);
  }

  /**
   * Opens the directory. Passing <code>true</code> will start with an empty directory.
   *
   * @param create <code>true</code> if the directory needs to be created/erased
   * @throws IOException when there is an io related problem
   */
  public synchronized void open(boolean create) throws IOException
  {
    if(_directory != null)
      throw new IllegalStateException("already opened!");

    _directory = _directoryFactory.openDirectory(create);
  }

  /**
   * @return <code>true</code> if the directory is opened
   */
  public synchronized boolean isOpen()
  {
    return _directory != null;
  }

  /**
   * checks that the directory is opened and throws an exception if it is not
   */
  private void checkOpen()
  {
    if(!isOpen())
      throw new IllegalStateException("open the directory first!");
  }

  /**
   * @return a reader of the directory
   * @throws IOException when there is an io related problem
   */
  public synchronized IndexReader getReader() throws IOException
  {
    checkOpen();

    if(_reader == null)
    {
      closeWriter();
      _reader = IndexReader.open(_directory, true);
    }

    return _reader;
  }

  /**
   * @return a searcher of the directory
   * @throws IOException when there is an io related problem
   */
  public synchronized LuceneIndexSearcher<T> getSearcher() throws IOException
  {
    checkOpen();

    if(_searcher == null)
    {
      IndexReader reader = getReader();

      IndexSearcher searcher = new IndexSearcher(reader);
      searcher.setSimilarity(_similarity);

      if(_userDataExtractor == null)
        _searcher = new NoUserDataLuceneIndexSearcherImpl<T>(searcher);
      else
        _searcher = new LuceneIndexSearcherImpl<T>(searcher, 
                                                   _userDataExtractor.extractUserData(searcher));
    }

    return _searcher;
  }

  /**
   * @return a writer of the directory
   * @throws IOException when there is an io related problem
   */
  public synchronized LuceneIndexWriter getWriter() throws IOException
  {
    checkOpen();

    if(_writer == null)
    {
      closeReaderAndSearcher();
      _writer = new IndexWriter(_directory, _analyzer, false, _maxFieldLength);
    }

    _writer.setSimilarity(_similarity);

    return new LuceneIndexWriterImpl(this, _writer);
  }

  /**
   * Closes the directory. Should not use afterwards (until calling {@link #open(boolean)}).
   *
   * @throws IOException when there is an io related problem
   */
  @LifecycleDestroy
  public synchronized void close() throws IOException
  {
    closeReaderAndSearcher();
    closeWriter();
    if(_directory != null)
      _directory.close();
    _directory = null;
  }

  /**
   * Called when the writer gets closed (rollback closes it!)
   * @throws IOException
   */
  synchronized IndexWriter writerRollback() throws IOException
  {
    // we close the writer
    closeWriter();

    // we then open it again
    getWriter();

    return _writer;
  }

  /**
   * Merges the content of <code>this</code> with <code>directory</code>. At the end of the
   * operation <code>this</code> is unchanged and <code>directory</code> contains both data.
   *
   * @param directory the directory to merge
   * @throws IOException
   */
  public synchronized void merge(LuceneDirectory directory) throws IOException
  {
    if(!this.isOpen() || !directory.isOpen())
      throw new IllegalStateException("both directories must be opened");

    log.info("merging from directory with " + getReader().numDocs() + " number of documents");

    if(getReader().numDocs() == 0)
      return;

    closeWriter();

    LuceneIndexWriter writer = directory.getWriter();
    try
    {
      writer.addIndexesNoOptimize(new Directory[]{_directory});
      writer.optimize();
      writer.commit();
    }
    finally
    {
      writer.close();
    }
  }

  /**
   * Optimizes the directory
   *
   * @throws IOException when there is an io related problem
   */
  public void optimize() throws IOException
  {
    LuceneIndexWriter writer = getWriter();
    try
    {
      writer.optimize();
      writer.commit();
    }
    finally
    {
      writer.close();
    }
  }

  /**
   * Closes the reader and the searcher if exists
   *
   * @throws IOException
   */
  private void closeReaderAndSearcher() throws IOException
  {
    if(_searcher != null)
    {
      _searcher.getIndexSearcher().close();
      _searcher = null;
    }

    if(_reader != null)
    {
      _reader.close();
      _reader = null;
    }
  }

  /**
   * Closes the writer if exists
   *
   * @throws IOException
   */
  private void closeWriter() throws IOException
  {
    if(_writer != null)
    {
      _writer.close();
      _writer = null;
    }
  }
}

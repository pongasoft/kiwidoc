
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

import com.pongasoft.util.core.io.IOUtils;
import com.pongasoft.util.core.annotations.FieldInitializer;
import com.pongasoft.util.core.annotations.ObjectInitializer;
import com.pongasoft.util.core.annotations.LifecycleInit;
import com.pongasoft.kiwidoc.index.impl.lucene.api.DirectoryFactory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;

/**
 * Implementation for a file system based directory
 *
 * @author yan@pongasoft.com
 */
public class FSDirectoryFactory implements DirectoryFactory
{
  public static final Log log = LogFactory.getLog(FSDirectoryFactory.class);

  private File _directoryPath;

  @ObjectInitializer
  public FSDirectoryFactory()
  {
  }

  /**
   * Constructor
   *
   * @param directoryPath path of the directory
   */
  public FSDirectoryFactory(File directoryPath) throws IOException
  {
    setDirectoryPath(directoryPath);
    init();
  }

  public File getDirectoryPath()
  {
    return _directoryPath;
  }

  @FieldInitializer
  public void setDirectoryPath(File directoryPath)
  {
    _directoryPath = directoryPath;
  }

  @LifecycleInit
  public void init() throws IOException
  {
    // this automatically test for writability
    IOUtils.mkdirs(_directoryPath);

  }

  /**
   * Opens the directory
   *
   * @param create <code>true</code> if the directory needs to be
   *               created/erased
   * @return a directory
   * @throws IOException when there is a problem opening the directory
   */
  public Directory openDirectory(boolean create) throws IOException
  {
    boolean directoryExists = IndexReader.indexExists(_directoryPath);
    if(directoryExists && !create)
    {
      FSDirectory directory = FSDirectory.getDirectory(_directoryPath);
      log.info("Opened existing FS directory " + _directoryPath);
      return directory;
    }
    else
      return openCleanDirectory();
  }

  /**
   * Opens a clean directory. Any data that was stored in the directory
   * previously is erased.
   *
   * @return an empty directory
   * @throws IOException
   */
  private Directory openCleanDirectory() throws IOException
  {
    Directory directory = FSDirectory.getDirectory(_directoryPath);

    IndexWriter writer = new IndexWriter(directory,
                                         new StandardAnalyzer(),
                                         true, 
                                         IndexWriter.MaxFieldLength.UNLIMITED);
    writer.close();

    log.info("Created empty FS directory " + _directoryPath);

    return directory;
  }
}

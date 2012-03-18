
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

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;

import com.pongasoft.util.core.io.IOUtils;
import com.pongasoft.kiwidoc.index.impl.lucene.api.DirectoryFactory;
import org.linkedin.util.clock.Chronos;

/**
 * @author yan@pongasoft.com
 */
public class RAMDirectoryFactory implements DirectoryFactory
{
  public static final Log log = LogFactory.getLog(RAMDirectoryFactory.class);

  private final File _directoryPath;

  /**
   * Constructor
   */
  public RAMDirectoryFactory()
  {
    _directoryPath = null;
  }

  /**
   * Constructor
   */
  public RAMDirectoryFactory(File directoryPath) throws IOException
  {
    // this automatically test for writability
    if(directoryPath != null)
      IOUtils.mkdirs(directoryPath);
    _directoryPath = directoryPath;
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
    if(_directoryPath == null || create)
      return openEmptyRAMDirectory();

    boolean directoryExists = IndexReader.indexExists(_directoryPath);
    if(!directoryExists)
      return openEmptyRAMDirectory();

    Chronos c = new Chronos();
    RAMDirectory ramDirectory = new RAMDirectory(_directoryPath);
    log.info("Opened RAM directory from FS " + _directoryPath + " in " + c.getElapsedTimeAsHMS());
    return ramDirectory;
  }

  /**
   * Opens an empty ram directory
   * @return
   * @throws IOException
   */
  private Directory openEmptyRAMDirectory() throws IOException
  {
    Directory directory = new RAMDirectory();
    IndexWriter writer = new IndexWriter(directory, null, true, IndexWriter.MaxFieldLength.UNLIMITED);
    writer.close();

    log.info("Opened empty RAM directory");
    return directory;
  }
}

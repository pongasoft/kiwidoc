
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

package com.pongasoft.util.core.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author yan@pongasoft.com
 */
public class IOUtils
{
  public static final String DEFAULT_ENCODING = "UTF-8";

  /**
   * Creates a new directory. This method creates automatically all the parent directories if
   * necesary. Contrary to <code>File.mkdirs</code>, this method will fail if the directory cannot
   * be created. The returned value is also different in meaning: <code>false</code> means that the
   * directory was not created because it already existed as opposed to it was not created because
   * we don't know.
   *
   * @param directory the directory to create
   * @return <code>true</code> if the directory was created, <code>false</code> if it existed
   *         already.
   * @throws IOException when there is a problem creating the directory
   */
  public static boolean mkdirs(File directory) throws IOException
  {
    if(directory.exists())
      return false;

    if(!directory.mkdirs())
      throw new IOException("cannot create the directory: " + directory);

    return true;
  }

  /**
   * Deletes the file.
   *
   * @return <code>true</code> if the file was deleted... <code>false</code> if it did not
   * exist in the first place
   * @throws IOException when there is a problem deleting the file
   */
  public static boolean delete(File file) throws IOException
  {
    if(!file.exists())
      return false;

    if(!file.delete())
      throw new IOException("cannot delete the file: " + file);

    return true;
  }

  /**
   * Creates a temporary directory. The method {@link File#createTempFile(String, String)}} creates
   * a file not a directory... this one is equivalent but for a directory.
   *
   * @param prefix
   * @param suffix
   * @return the directory
   * @throws IOException
   */
  public static File createTempDirectory(String prefix, String suffix) throws IOException
  {
    File tempFile = File.createTempFile(prefix, suffix);
    delete(tempFile);
    mkdirs(tempFile);
    return tempFile;
  }

  /**
   * Copies the input stream into the output stream (all)
   *
   * @param in  the input stream to read data
   * @param out the output stream to write data
   */
  public static void copy(InputStream in, OutputStream out) throws IOException
  {
    copy(in, out, -1);
  }

  /**
   * Copies the input stream into the output stream (num_bytes)
   *
   * @param in        the input stream to read data
   * @param out       the output stream to write data
   * @param num_bytes the number of bytes to copy
   */
  public static void copy(InputStream in, OutputStream out, int num_bytes)
    throws IOException
  {
    if(num_bytes == 0)
      return;

    int n;

    if(num_bytes < 0)
    {
      byte[] b = new byte[2048];
      while((n = in.read(b, 0, b.length)) > 0)
        out.write(b, 0, n);
    }
    else
    {
      int offset = 0;
      byte[] b = new byte[num_bytes];
      while(num_bytes > 0 && (n = in.read(b, offset, num_bytes)) > 0)
      {
        offset += n;
        num_bytes -= n;
      }
      out.write(b);
    }
  }

  /**
   * Saves the content in the output stream
   *
   * @param os the output stream to save the content
   * @param content the content to save
   * @throws IOException if there is a problem saving
   */
  public static void saveContent(OutputStream os, byte[] content)
    throws IOException
  {
    BufferedOutputStream bos = new BufferedOutputStream(os);
    bos.write(content);
    bos.close();
  }

  /**
   * Constructor
   */
  protected IOUtils()
  {
  }
}

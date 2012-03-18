
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

package com.pongasoft.util.io;

import com.pongasoft.util.core.net.URIPath;
import com.pongasoft.util.core.text.TextUtils;
import groovy.lang.Closure;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.NameScope;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.FileSystemManager;
import org.linkedin.groovy.util.io.GroovyIOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * @author yan@pongasoft.com
 */
public class IOUtils extends com.pongasoft.util.core.io.IOUtils
{
  /**
   * Saves the content in the resource.
   *
   * @param resource the resource to save the content
   * @param content the content to save
   * @throws IOException if there is a problem saving
   */
  public static void saveContent(FileObject resource, String content)
    throws IOException
  {
    saveContent(resource, content.getBytes(DEFAULT_ENCODING));
  }

  /**
   * Saves the content in the resource.
   *
   * @param resource the resource to save the content
   * @param content the content to save
   * @throws IOException if there is a problem saving
   */
  public static void saveContent(FileObject resource, byte[] content)
    throws IOException
  {
    OutputStream os = resource.getContent().getOutputStream();
    try
    {
      saveContent(os, content);
    }
    finally
    {
      os.close();
    }
  }

  /**
   * Reads the content of the resource
   *
   * @param resource
   * @return the whole content
   * @throws IOException if there is an error reading
   */
  public static byte[] readContent(FileObject resource) throws IOException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    InputStream is = resource.getContent().getInputStream();
    try
    {
      copy(is, baos);
    }
    finally
    {
      is.close();
    }

    return baos.toByteArray();
  }

  /**
   * Reads the content of the resource
   *
   * @param resource
   * @return the whole content and convert it to a string
   * @throws IOException if there is an error reading
   */
  public static String readContentAsString(FileObject resource) throws IOException
  {
    return new String(readContent(resource), DEFAULT_ENCODING);
  }

  /**
   * Returns a child of the provided file object designated by the uri path
   *
   * @param uriPath the path to the child
   * @param fileObject the file object (root)
   * @return the new file object
   * @throws IOException if there is something wrong
   */
  public static FileObject resolveFile(FileObject fileObject, URIPath uriPath) throws IOException
  {
    String fullPath = uriPath.getFullPath();
    if("".equals(fullPath) || "/".equals(fullPath))
      return fileObject;
    else
      return fileObject.resolveFile(fullPath, NameScope.DESCENDENT);
  }

  /**
   * Wraps the provided file object within a jar file object
   * (ex: <code>createJarFileObject(file:///tmp/foo.jar)</code> will return
   * <code>jar:file:///tmp.foo.jar!/</code>
   *
   * @param fileObject the orginal jar file
   * @return the wrapped file object (note that it the orignial object does not exists, it
   *         is simply returned (as wrapping it throws an exception...)
   * @throws IOException if there is something wrong
   */
  public static FileObject createJarFileObject(FileObject fileObject) throws IOException
  {
    if(fileObject == null)
      return null;
    
    if(fileObject.exists())
    {
      FileSystemManager fsm = fileObject.getFileSystem().getFileSystemManager();
      return fsm.resolveFile("jar:" +  fileObject.getURL() + "!/");
    }
    else
      return fileObject;
  }

  /**
   * Returns a string representation of the whole directory structure represented by the
   * {@link FileObject}. The output is very similar to what the <code>jar tvf</code>command does.
   *
   * @param root the root of the directory structure
   * @return the string representation
   * @throws IOException if there is something wrong
   */
  public static String asString(FileObject root) throws IOException
  {
    FileMap fileMap = new FileMap();

    fileMap.addFile(root, root);

    Map<String, FileInfo> allEntries = new TreeMap<String, FileInfo>(fileMap.getFileMap());

    StringBuilder sb = new StringBuilder();

    for(Map.Entry<String, FileInfo> entry : allEntries.entrySet())
    {
      FileInfo ramEntry = entry.getValue();
      sb.append(TextUtils.printf("%6d %tF %2$tT %s",
                                 ramEntry.getSize(),
                                 ramEntry.getLastModifiedTime(),
                                 entry.getKey()));
      sb.append("\n");
    }

    return sb.toString();
  }

  /**
   * Serializes the object to a file. This is done in a safe way in the sense that all parent
   * directories will be properly created and if a file already exists it will be replaced only
   * if the file is succesfully entirely written.
   */
  public static <T> void serializeToFile(final T object, File file) throws IOException
  {
    GroovyIOUtils.safeOverwrite(file, new Closure(IOUtils.class)
    {
      @Override
      public Object call(Object[] args)
      {
        try
        {
          FileOutputStream fos = new FileOutputStream((File) args[0]);
          try
          {
            serialize(object, new BufferedOutputStream(fos));
          }
          finally
          {
            fos.close();
          }
          return null;
        }
        catch(IOException e)
        {
          throw new RuntimeException(e);
        }
      }
    });
  }

  public static <T> void serialize(T object, OutputStream outputStream)
    throws IOException
  {
    ObjectOutputStream oos = new ObjectOutputStream(outputStream);
    oos.writeObject(object);
    oos.flush();
  }

  @SuppressWarnings("unchecked")
  public static <T> T deserializeFromFile(File file) throws IOException, ClassNotFoundException
  {
    if(file == null)
      return null;

    FileInputStream fis = new FileInputStream(file);
    try
    {
      return (T) deserialize(new BufferedInputStream(fis));
    }
    finally
    {
      fis.close();
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T deserialize(InputStream inputStream) throws IOException, ClassNotFoundException
  {
    if(inputStream == null)
      return null;
    
    ObjectInputStream ois = new ObjectInputStream(inputStream);
    return (T) ois.readObject();
  }

  /**
   * Constructor
   */
  private IOUtils()
  {
  }

  /**
   * Internal class used by {@link #asString}
   */
  public static class FileInfo
  {
    private final long _size;
    private final long _lastModifiedTime;

    public FileInfo(long size, long lastModifiedTime)
    {
      _size = size;
      _lastModifiedTime = lastModifiedTime;
    }

    public long getSize()
    {
      return _size;
    }

    public long getLastModifiedTime()
    {
      return _lastModifiedTime;
    }
  }

  /**
   * Internal class used by {@link #asString}
   */
  public static class FileMap
  {
    private final Map<String, FileInfo> _fileMap = new HashMap<String, FileInfo>();

    public FileMap()
    {
    }

    public void addFile(String name, long size, long lastModifiedTime)
    {
      _fileMap.put(name, new FileInfo(size, lastModifiedTime));
    }

    public Map<String, FileInfo> getFileMap()
    {
      return _fileMap;
    }

    public void addFile(FileObject root, FileObject child) throws IOException
    {
      if(root != child)
      {
        FileContent content = child.getContent();

        long size = 0;
        String name = root.getName().getRelativeName(child.getName());
        if(child.getType() == FileType.FILE)
          size = content.getSize();
        else
          name = name + "/";

        addFile(name, size, content.getLastModifiedTime());
      }

      if(child.getType() == FileType.FOLDER)
      {
        for(FileObject fileObject : child.getChildren())
        {
          addFile(root, fileObject);
        }
      }
    }
  }
}

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

package com.pongasoft.kiwidoc.builder;

import com.pongasoft.kiwidoc.model.resource.PathManager;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.util.core.annotations.FieldInitializer;
import com.pongasoft.util.core.annotations.ObjectInitializer;
import com.pongasoft.util.core.net.URIPath;
import com.pongasoft.util.io.IOUtils;
import org.apache.commons.vfs.FileObject;

import java.io.IOException;
import java.net.URI;

/**
 * @author yan@pongasoft.com
 */
public class ResourceLocatorImpl implements ResourceLocator
{
  private FileObject _root;
  private PathManager _pathManager;

  @ObjectInitializer
  public ResourceLocatorImpl()
  {
  }

  /**
   * Constructor
   */
  public ResourceLocatorImpl(FileObject root, PathManager pathManager) throws IOException
  {
    setRoot(root);
    _pathManager = pathManager;
  }

  public PathManager getPathManager()
  {
    return _pathManager;
  }

  @FieldInitializer
  public void setPathManager(PathManager pathManager)
  {
    _pathManager = pathManager;
  }

  public FileObject getRoot()
  {
    return _root;
  }

  @FieldInitializer
  public void setRoot(FileObject root) throws IOException
  {
    _root = root;
    _root.createFolder();
  }

  /**
   * Locates a resource
   *
   * @param resource the resource to locate
   * @return the located resource... note that if the resource does not exist, then it will return a
   *         <code>FileObject</code> which does not exist... <code>null</code> would be returned if
   *         <code>resource</code> is <code>null</code>.
   * @throws StoreException if there is a problem
   */
  public FileObject locateResource(Resource resource) throws StoreException
  {
    if(resource == null)
      return null;

    try
    {
      return getFileObject(_pathManager.computePath(resource));
    }
    catch(IOException e)
    {
      throw new StoreException(e);
    }

  }

  private FileObject getFileObject(URI uri) throws IOException
  {
    return getFileObject(URIPath.createFromURI(uri));
  }

  private FileObject getFileObject(URIPath uriPath) throws IOException
  {
    return IOUtils.resolveFile(_root, uriPath);
  }
}

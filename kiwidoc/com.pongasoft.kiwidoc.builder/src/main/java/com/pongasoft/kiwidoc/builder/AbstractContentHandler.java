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

import com.pongasoft.kiwidoc.model.Model;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.util.core.annotations.FieldInitializer;
import com.pongasoft.util.core.annotations.ObjectInitializer;
import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;

import java.io.IOException;

/**
 * Base class.
 *
 * @author yan@pongasoft.com
 */
public abstract class AbstractContentHandler<R extends Resource, M extends Model<R>>
  implements ContentHandler<R, M>
{
  private ResourceLocator _resourceLocator;

  /**
   * Constructor
   */
  @ObjectInitializer
  public AbstractContentHandler()
  {
  }

  /**
   * Constructor
   */
  @ObjectInitializer
  public AbstractContentHandler(ResourceLocator resourceLocator)
  {
    _resourceLocator = resourceLocator;
  }

  public ResourceLocator getResourceLocator()
  {
    return _resourceLocator;
  }

  @FieldInitializer
  public void setResourceLocator(ResourceLocator resourceLocator)
  {
    _resourceLocator = resourceLocator;
  }

  /**
   * @return <code>true</code> if the content for the given resource exists
   * @throws StoreException if there is a problem
   */
  public boolean exists(R resource) throws StoreException
  {
    if(resource == null)
      return false;

    try
    {
      FileObject resourceFile = getResourceFile(resource);
      return resourceFile.exists() && doExists(resourceFile);
    }
    catch(FileSystemException e)
    {
      throw new StoreException(e);
    }
  }

  /**
   * Logic to determine whether a resource exists (depend on implementation).
   *
   * @param resourceFile the file for the content
   * @return <code>true</code> if the content for the given resource exists
   * @throws StoreException
   */
  protected abstract boolean doExists(FileObject resourceFile) throws StoreException;

  /**
   * Deletes the content pointed to by this resource.
   *
   * @param resource the resource to delete
   * @return <code>true</code> if the resource was deleted, <code>false</code> if it did not exist in
   *         the first place
   * @throws StoreException if there is a problem
   */
  public boolean deleteContent(R resource) throws StoreException
  {
    if(resource == null)
      return false;

    try
    {
      FileObject resourceFile = getResourceFile(resource);
      return resourceFile.delete(new AllFileSelector()) > 0;
    }
    catch(IOException e)
    {
      throw new StoreException(e);
    }
  }

  protected FileObject getResourceFile(R resource)
    throws StoreException
  {
    return _resourceLocator.locateResource(resource);
  }
}
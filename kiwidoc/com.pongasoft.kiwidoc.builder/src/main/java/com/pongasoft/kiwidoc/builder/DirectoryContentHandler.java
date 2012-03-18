
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

import com.pongasoft.kiwidoc.builder.model.ModelFactory;
import com.pongasoft.kiwidoc.model.Model;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.util.core.annotations.FieldInitializer;
import com.pongasoft.util.core.annotations.ObjectInitializer;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author yan@pongasoft.com
 */
public class DirectoryContentHandler<R extends Resource, M extends Model<R>>
  extends AbstractContentHandler<R, M>
{
  private ModelFactory<R, M> _modelFactory;

  @ObjectInitializer
  public DirectoryContentHandler()
  {
  }

  /**
   * Constructor
   */
  public DirectoryContentHandler(ResourceLocator resourceLocator,
                                 ModelFactory<R, M> modelFactory)
  {
    super(resourceLocator);
    _modelFactory = modelFactory;
  }

  public ModelFactory<R, M> getModelFactory()
  {
    return _modelFactory;
  }

  @FieldInitializer
  public void setModelFactory(ModelFactory<R, M> modelFactory)
  {
    _modelFactory = modelFactory;
  }

  /**
   * Logic to determine whether a resource exists (depend on implementation).
   *
   * @param resourceFile the file for the content
   * @return <code>true</code> if the content for the given resource exists
   * @throws StoreException
   */
  @Override
  protected boolean doExists(FileObject resourceFile) throws StoreException
  {
    try
    {
      return resourceFile.getChildren().length > 0;
    }
    catch(FileSystemException e)
    {
      throw new StoreException(e);
    }
  }

  /**
   * Loads the content of the given resource.
   *
   * @param resource the resource to load
   * @return the content as an object since it depends on which content is being read (ex: manifest,
   *         library, packages, class) (never <code>null</code>)
   * @throws NoSuchContentException if the content does not exist
   * @throws StoreException         if there is a problem reading the content.
   */
  public M loadContent(R resource) throws NoSuchContentException, StoreException
  {
    if(resource == null)
      throw new NoSuchContentException(resource);

    Collection<String> childResources = new ArrayList<String>();

    FileObject root = getResourceFile(resource);

    try
    {
      if(!root.exists())
        throw new NoSuchContentException(resource);

      if(root.getType() == FileType.FOLDER)
      {
        FileObject[] children = root.getChildren();
        for(FileObject child : children)
        {
          if(child.getType() == FileType.FOLDER)
          {
            childResources.add(child.getName().getBaseName());
        }
      }
    }
    }
    catch(FileSystemException e)
    {
      throw new StoreException(e);
    }

    return _modelFactory.buildModel(resource, childResources);
  }

  /**
   * Saves the model.
   *
   * @param model the mode to store
   * @return the resource
   * @throws StoreException if there is a problem
   */
  public R saveContent(M model) throws StoreException
  {
    throw new UnsupportedOperationException("model cannot be saved " + model.getClass().getName());
  }

}

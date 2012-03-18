
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

import com.pongasoft.kiwidoc.builder.model.LibraryModelFactory;
import com.pongasoft.kiwidoc.builder.model.OrganisationModelFactory;
import com.pongasoft.kiwidoc.builder.path.StoragePathManager;
import com.pongasoft.kiwidoc.builder.serializer.model.ModelSerializer;
import com.pongasoft.kiwidoc.model.Model;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.resource.HierarchyResource;
import com.pongasoft.kiwidoc.model.resource.LVROverviewResource;
import com.pongasoft.kiwidoc.model.resource.LibraryResource;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.ManifestResource;
import com.pongasoft.kiwidoc.model.resource.OrganisationResource;
import com.pongasoft.kiwidoc.model.resource.PackageResource;
import com.pongasoft.kiwidoc.model.resource.RepositoryResource;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.util.core.annotations.FieldInitializer;
import com.pongasoft.util.core.annotations.ObjectInitializer;
import org.apache.commons.vfs.FileObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yan@pongasoft.com
 */
public class ContentHandlersImpl implements ContentHandlers
{
  private Map<Class<? extends Resource>, ContentHandler> _contentHandlers;

  @ObjectInitializer
  public ContentHandlersImpl()
  {
  }

  /**
   * Constructor
   */
  public ContentHandlersImpl(FileObject root) throws IOException
  {
    setRoot(root);
  }

  public ContentHandlersImpl(Map<Class<? extends Resource>, ContentHandler> contentHandlers)
  {
    _contentHandlers = contentHandlers;
  }

  public Map<Class<? extends Resource>, ContentHandler> getContentHandlers()
  {
    return _contentHandlers;
  }

  @FieldInitializer
  public void setContentHandlers(Map<Class<? extends Resource>, ContentHandler> contentHandlers)
  {
    _contentHandlers = contentHandlers;
  }

  @SuppressWarnings("unchecked")
  @FieldInitializer
  public void setRoot(FileObject root) throws IOException
  {
    _contentHandlers = new HashMap<Class<? extends Resource>, ContentHandler>();
    
    ResourceLocator resourceLocator = new ResourceLocatorImpl(root, new StoragePathManager());

    // directory based resources
    _contentHandlers.put(RepositoryResource.class,
                         new RepositoryContentHandler(resourceLocator));

    _contentHandlers.put(OrganisationResource.class,
                         new DirectoryContentHandler(resourceLocator,
                                                     new OrganisationModelFactory()));

    _contentHandlers.put(LibraryResource.class,
                         new DirectoryContentHandler(resourceLocator,
                                                     new LibraryModelFactory()));

    // json based resources
    JSONContentHandler jsonContentHandler =
      new JSONContentHandler(resourceLocator, new ModelSerializer());

    _contentHandlers.put(LibraryVersionResource.class, jsonContentHandler);
    _contentHandlers.put(LVROverviewResource.class, jsonContentHandler);
    _contentHandlers.put(ManifestResource.class, jsonContentHandler);
    _contentHandlers.put(HierarchyResource.class, jsonContentHandler);
    _contentHandlers.put(PackageResource.class, jsonContentHandler);
    _contentHandlers.put(ClassResource.class, jsonContentHandler);
  }

  /**
   * @return the content handler for the given resource (<code>null</code> if resource is
   *         <code>null</code>)
   * @throws StoreException
   */
  public <R extends Resource, M extends Model<R>> ContentHandler<R, M> getContentHandler(R resource)
    throws StoreException
  {
    if(resource == null)
      return null;

    @SuppressWarnings("unchecked")
    ContentHandler<R, M> handler = getContentHandler((Class<R>) resource.getClass());

    return handler;
  }

  /**
   * @return the content handler for the given resource (<code>null</code> if resource is
   *         <code>null</code>)
   * @throws StoreException
   */
  public <R extends Resource, M extends Model<R>> ContentHandler<R, M> getContentHandler(Class<R> resourceClass)
    throws StoreException
  {
    @SuppressWarnings("unchecked")
    ContentHandler<R, M> handler = _contentHandlers.get(resourceClass);

    if(handler == null)
      throw new StoreException("unkown resource type " + resourceClass);

    return handler;
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
  public Model loadContent(Resource resource) throws NoSuchContentException, StoreException
  {
    return getContentHandler(resource).loadContent(resource);
  }

  /**
   * @return <code>true</code> if the content for the given resource exists
   * @throws StoreException if there is a problem
   */
  public boolean exists(Resource resource) throws StoreException
  {
    return getContentHandler(resource).exists(resource);
  }

  /**
   * Deletes the content pointed to by this resource.
   *
   * @param resource the resource to delete
   * @return <code>true</code> if the resource was deleted, <code>false</code> if it did not exist in
   *         the first place
   * @throws StoreException if there is a problem
   */
  public boolean deleteContent(Resource resource) throws StoreException
  {
    if(getContentHandler(resource).deleteContent(resource))
    {
      Resource parent = resource.getParent();
      while(parent != null && !parent.isRoot() && !exists(parent))
      {
        deleteContent(parent);
        parent = parent.getParent();
      }

      return true;
    }

    return false;
  }

  /**
   * Saves the model.
   *
   * @param model the mode to store
   * @return the resource
   * @throws StoreException if there is a problem
   */
  @SuppressWarnings("unchecked")
  public Resource saveContent(Model model) throws StoreException
  {
    return getContentHandler(model.getResource()).saveContent(model);
  }
}

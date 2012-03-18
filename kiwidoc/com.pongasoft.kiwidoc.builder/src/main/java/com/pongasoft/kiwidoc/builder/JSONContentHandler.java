
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

import com.pongasoft.kiwidoc.builder.serializer.Serializer;
import com.pongasoft.kiwidoc.builder.serializer.SerializerException;
import com.pongasoft.kiwidoc.model.Model;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.util.core.annotations.FieldInitializer;
import com.pongasoft.util.core.annotations.ObjectInitializer;
import com.pongasoft.util.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.NameScope;

import java.io.IOException;
import java.util.Map;

/**
 * This is the generic implementation when there is a serialized version in a json file.
 * 
 * @author yan@pongasoft.com
 */
public class JSONContentHandler extends AbstractContentHandler
{
  public static final Log log = LogFactory.getLog(JSONContentHandler.class);

  public static final String CONTENT_FILENAME = "__content.json";

  private Serializer _modelSerializer;

  /**
   * Constructor
   */
  @ObjectInitializer
  public JSONContentHandler()
  {
  }

  /**
   * Constructor
   */
  @SuppressWarnings("unchecked")
  public JSONContentHandler(ResourceLocator resourceLocator,
                            Serializer modelSerializer)
  {
    super(resourceLocator);
    _modelSerializer = modelSerializer;
  }

  public Serializer getModelSerializer()
  {
    return _modelSerializer;
  }

  @FieldInitializer
  public void setModelSerializer(Serializer modelSerializer)
  {
    _modelSerializer = modelSerializer;
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
      return getContentFile(resourceFile).exists();
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
  public Model loadContent(Resource resource) throws NoSuchContentException, StoreException
  {
    if(resource == null)
      throw new NoSuchContentException(resource);

    @SuppressWarnings("unchecked")
    Model content = deserialize(resource, loadRawContent(resource));
    
    return content;
  }

  /**
   * Saves the model.
   *
   * @param model the mode to store
   * @return the resource
   * @throws StoreException if there is a problem
   */
  public Resource saveContent(Model model) throws StoreException
  {
    try
    {
      @SuppressWarnings("unchecked")
      Object content = _modelSerializer.serialize(model);
      saveContent(content, getContentFile(model.getResource()));
    }
    catch(IOException e)
    {
      throw new StoreException(e);
    }
    catch(SerializerException e)
    {
      throw new StoreException(e);
    }

    return model.getResource();
  }

  /**
   * Deserialize the raw content: this method simply switches on the file type and dispatches
   * accordingly
   */
  private Model deserialize(Resource resource, Map<String, Object> rawContent)
    throws StoreException
  {
    try
    {
      return (Model) _modelSerializer.deserialize(null, rawContent);
    }
    catch(SerializerException e)
    {
      throw new StoreException("for resource " + resource, e);
    }
  }

  /**
   * Loads the content of the given resource.
   *
   * @param resource the resource to load
   * @return the raw content in json format (never <code>null</code>)
   * @throws NoSuchContentException if the content does not exist
   * @throws StoreException         if there is a problem reading the content.
   */
  public Map<String, Object> loadRawContent(Resource resource)
    throws NoSuchContentException, StoreException
  {
    if(resource == null)
      throw new NoSuchContentException(resource);

    try
    {
      FileObject contentFile = getContentFile(resource);
      if(!contentFile.exists())
        throw new NoSuchContentException(resource);
      String content = IOUtils.readContentAsString(contentFile);
      
      if(log.isDebugEnabled())
        log.debug("Loaded " + resource + " : " + content.length());
      
      return JsonCodec.fromJsonStringToMap(content);
    }
    catch(IOException e)
    {
      throw new StoreException(e);
    }
  }

  private void saveContent(Object content, FileObject location) throws IOException
  {
    if(content != null && location != null)
    {
      IOUtils.saveContent(location, JsonCodec.toJsonString(content));
    }
  }

  private FileObject getContentFile(Resource resource)
    throws StoreException
  {
    @SuppressWarnings("unchecked")
    FileObject contentFile = getResourceFile(resource);
    try
    {
      return getContentFile(contentFile);
    }
    catch(FileSystemException e)
    {
      throw new StoreException(e);
    }
  }

  private FileObject getContentFile(FileObject contentFile)
    throws FileSystemException
  {
    return contentFile.resolveFile(CONTENT_FILENAME, NameScope.CHILD);
  }
}

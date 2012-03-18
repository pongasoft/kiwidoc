
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

package com.pongasoft.util.core.net;

import com.pongasoft.util.core.misc.Utils;
import org.linkedin.util.url.URLCodec;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author yan@pongasoft.com
 */
public class URIPath implements Serializable
{
  private static final long serialVersionUID = 1L;

  public static final String PATH_SEPARATOR = "/";

  private final String[] _pathElements;

  public URIPath()
  {
    _pathElements = new String[0];
  }

  public URIPath(String pathElement)
  {
    _pathElements = new String[1];
    _pathElements[0] = URLCodec.INSTANCE.urlEncode(pathElement);
  }

  private URIPath(String[] pathElements) throws URISyntaxException
  {
    // TODO HIGH YP: check for path validity...
    _pathElements = pathElements;
  }

  /**
   * @param pathElement (assumed to not be encoded properly)
   */
  public URIPath addPathElement(String pathElement)
  {
    return addPathElements(pathElement);
  }

  /**
   * @param pathElements (assumed to not be encoded properly)
   */
  public URIPath addPathElements(String... pathElements)
  {
    String[] newPathElements = createNewPath(pathElements);

    // we make sure to encode all the new path elements (the other ones are already encoded properly!)
    for (int i = _pathElements.length; i < newPathElements.length; i++)
    {
      newPathElements[i] = URLCodec.INSTANCE.urlEncode(newPathElements[i]);
    }

    try
    {
      return new URIPath(newPathElements);
    }
    catch (URISyntaxException e)
    {
      // should not happen... encoded properly
      throw new RuntimeException(e);
    }
  }

  /**
   * Adds a path from another one
   * @param path the other path to add
   */
  public URIPath addPath(URIPath path)
  {
    try
    {
      return new URIPath(createNewPath(path._pathElements));
    }
    catch (URISyntaxException e)
    {
      // should not happen... encoded properly
      throw new RuntimeException(e);
    }
  }

  /**
   * @param path (assumed to be encoded properly)
   */
  public URIPath addPath(String path) throws URISyntaxException
  {
    return new URIPath(createNewPath(path.split(PATH_SEPARATOR)));
  }

  /**
   * Creates a new path by appending the new elements to the current ones.
   * 
   * @param pathElements
   * @return the new array
   */
  private String[] createNewPath(String[] pathElements)
  {
    String[] newPathElements = new String[_pathElements.length + pathElements.length];
    System.arraycopy(_pathElements, 0, newPathElements, 0, _pathElements.length);
    System.arraycopy(pathElements, 0, newPathElements, _pathElements.length, pathElements.length);
    return newPathElements;
  }

  /**
   * @return the fullpath as a string (path elements separated by /)
   */
  public String getFullPath()
  {
    if(_pathElements.length > 0)
    {
      StringBuilder sb = new StringBuilder();

      for(String pathElement : _pathElements)
      {
        if(sb.length() > 0)
          sb.append(PATH_SEPARATOR);
        sb.append(pathElement);
      }

      return sb.toString();
    }
    else
      return "/";
  }

  /**
   * @return as a URI
   */
  public URI toURI()
  {
    try
    {
      return new URI(getFullPath());
    }
    catch(URISyntaxException e)
    {
      // should not happen... (checked when adding or encoded appropriately...)
      throw new RuntimeException(e);
    }
  }

  /**
   * @return the path elements making up this path
   */
  public String[] getPathElements()
  {
    return _pathElements;
  }

  @Override
  public String toString()
  {
    return getFullPath();
  }

  public static URIPath createFromPath(String path) throws URISyntaxException
  {
    if(path == null)
      return null;

    if(path.equals(""))
      return new URIPath(new String[] {""});

    if(path.equals("/"))
      return new URIPath();

    String[] array = path.split(PATH_SEPARATOR);
    for(int i = 0; i < array.length; i++)
    {
      array[i] = URLCodec.INSTANCE.urlDecode(array[i]);
    }
    if(path.endsWith("/"))
    {
      array = Utils.expandArray(array, array.length);
      array[array.length - 1] = "";
    }
    return new URIPath(array);
  }

  public static URIPath createFromPathElement(String pathElement)
  {
    if(pathElement == null)
      return null;

    return new URIPath(pathElement);
  }

  public static URIPath createFromURI(URI uri)
  {
    URIPath uriPath = new URIPath();
    try
    {
      uriPath = uriPath.addPath(uri.getRawPath());
    }
    catch(URISyntaxException e)
    {
      // should not happen since a uri is already well formed...
      throw new RuntimeException(e);
    }
    return uriPath;
  }
}

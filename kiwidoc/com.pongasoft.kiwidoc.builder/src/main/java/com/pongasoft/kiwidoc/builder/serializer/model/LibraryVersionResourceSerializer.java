
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

package com.pongasoft.kiwidoc.builder.serializer.model;

import com.pongasoft.kiwidoc.builder.serializer.Serializer;
import com.pongasoft.kiwidoc.builder.serializer.SerializerException;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.util.core.net.URIPath;

import java.net.URISyntaxException;

/**
 * @author yan@pongasoft.com
 */
public class LibraryVersionResourceSerializer implements Serializer<LibraryVersionResource, Object>
{
  /**
   * Constructor
   */
  public LibraryVersionResourceSerializer()
  {
  }

  public Object serialize(LibraryVersionResource lib) throws SerializerException
  {
    if(lib == null)
      return null;

    return new URIPath().addPathElements(lib.getOrganisation(),
                                         lib.getName(), 
                                         lib.getVersion()).toString();
  }

  public LibraryVersionResource deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    String content = (String) objectToDeserialize;
    try
    {
      String[] elements = URIPath.createFromPath(content).getPathElements();
      if(elements.length != 3)
        throw new SerializerException("invalid library version resource: " + content);
      return new LibraryVersionResource(elements[0], elements[1], elements[2]);
    }
    catch(URISyntaxException e)
    {
      throw new SerializerException(e);
    }
  }
}

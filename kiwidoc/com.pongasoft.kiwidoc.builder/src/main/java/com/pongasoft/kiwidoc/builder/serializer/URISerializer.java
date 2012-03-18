
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

package com.pongasoft.kiwidoc.builder.serializer;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author yan@pongasoft.com
 */
public class URISerializer implements Serializer<URI, Object>
{
  /**
   * Constructor
   */
  public URISerializer()
  {
  }

  public Object serialize(URI objectToSerialize) throws SerializerException
  {
    if(objectToSerialize == null)
      return null;
    return objectToSerialize.toString();
  }

  public URI deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;
    try
    {
      return new URI(String.valueOf(objectToDeserialize));
    }
    catch(URISyntaxException e)
    {
      throw new SerializerException(e);
    }
  }
}

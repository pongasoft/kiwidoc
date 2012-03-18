
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

package com.pongasoft.kiwidoc.builder.serializer.type;

import com.pongasoft.kiwidoc.builder.serializer.Serializer;
import com.pongasoft.kiwidoc.builder.serializer.SerializerException;
import com.pongasoft.kiwidoc.model.type.Type;

/**
 * @author yan@pongasoft.com
 */
public class TypeSerializer<T extends Type> implements Serializer<T, Object>
{
  private final TypeEncoder _typeEncoder;
  private final TypeDecoder _typeDecoder;

  /**
   * Constructor
   */
  public TypeSerializer()
  {
    _typeEncoder = new TypeEncoder();
    _typeDecoder = new TypeDecoder();
  }

  public Object serialize(Type type) throws SerializerException
  {
    if(type == null)
      return null;

    return _typeEncoder.encodeType(type);
  }

  @SuppressWarnings("unchecked")
  public T deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    try
    {
      return (T) _typeDecoder.decodeType((String) objectToDeserialize);
    }
    catch(Exception e)
    {
      throw new SerializerException(e);
    }
  }
}
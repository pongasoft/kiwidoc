
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

package com.pongasoft.kiwidoc.builder.serializer.tag;

import com.pongasoft.kiwidoc.builder.serializer.Serializer;
import com.pongasoft.kiwidoc.builder.serializer.SerializerException;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.kiwidoc.model.tag.ThrowsTag;
import com.pongasoft.kiwidoc.model.type.Type;

import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.optString;
import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.putOnce;

/**
 * @author yan@pongasoft.com
 */
public class ThrowsTagSerializer implements Serializer<ThrowsTag, Resource>
{
  private final DocTypeSerializer _docTypeSerializer;
  private final Serializer<Type, Object> _typeSerializer;

  public static class FThrowsTag
  {
    public static final String exceptionName = "w";
    public static final String exceptionType = "e";
  }

  public ThrowsTagSerializer(DocTypeSerializer docTypeSerializer,
                             Serializer<Type, Object> typeSerializer)
  {
    _docTypeSerializer = docTypeSerializer;
    _typeSerializer = typeSerializer;
  }

  public Object serialize(ThrowsTag tag) throws SerializerException
  {
    Map<String, Object> content = _docTypeSerializer.serialize(tag);
    if(content != null)
    {
      putOnce(content, FThrowsTag.exceptionName, tag.getExceptionName());
      putOnce(content, FThrowsTag.exceptionType, _typeSerializer.serialize(tag.getExceptionType()));
    }
    return content;
  }

  public ThrowsTag deserialize(Resource context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    return new ThrowsTag(context,
                         _docTypeSerializer.deserialiseName(content),
                         _docTypeSerializer.deserializeInlineTags(content),
                         optString(content, FThrowsTag.exceptionName),
                         _typeSerializer.deserialize(context, optString(content,
                                                                        FThrowsTag.exceptionType)));
  }
}
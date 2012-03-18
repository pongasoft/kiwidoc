
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
import com.pongasoft.kiwidoc.model.tag.OrdinaryTag;

import java.util.Map;

/**
 * @author yan@pongasoft.com
 */
public class OrdinaryTagSerializer implements Serializer<OrdinaryTag, Resource>
{
  private final DocTypeSerializer _docTypeSerializer;

  public OrdinaryTagSerializer(DocTypeSerializer docTypeSerializer)
  {
    _docTypeSerializer = docTypeSerializer;
  }

  public Object serialize(OrdinaryTag tag) throws SerializerException
  {
    return _docTypeSerializer.serialize(tag);
  }

  public OrdinaryTag deserialize(Resource context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    return new OrdinaryTag(context,
                           _docTypeSerializer.deserialiseName(content),
                           _docTypeSerializer.deserializeInlineTags(content));
  }
}

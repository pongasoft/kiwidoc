
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
import com.pongasoft.kiwidoc.model.tag.LinkReference;
import com.pongasoft.kiwidoc.model.tag.SeeTag;

import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.putAllOnce;

/**
 * @author yan@pongasoft.com
 */
public class SeeTagSerializer implements Serializer<SeeTag, Resource>
{
  private final DocTypeSerializer _docTypeSerializer;
  private final Serializer<LinkReference, Object> _linkReferenceSerializer;

  public SeeTagSerializer(DocTypeSerializer docTypeSerializer,
                          Serializer<LinkReference, Object> linkReferenceSerializer)
  {
    _docTypeSerializer = docTypeSerializer;
    _linkReferenceSerializer = linkReferenceSerializer;
  }

  public Object serialize(SeeTag tag) throws SerializerException
  {
    Map<String, Object> content = _docTypeSerializer.serialize(tag);
    if(content != null)
    {
      putAllOnce((Map<String, Object>) _linkReferenceSerializer.serialize(tag.getLinkReference()), content);
    }
    return content;
  }

  public SeeTag deserialize(Resource context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    return new SeeTag(context,
                      _docTypeSerializer.deserialiseName(content),
                      _docTypeSerializer.deserializeInlineTags(content),
                      _linkReferenceSerializer.deserialize(context, content));
  }
}
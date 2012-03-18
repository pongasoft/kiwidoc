
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
import com.pongasoft.kiwidoc.model.tag.InlineLinkTag;
import com.pongasoft.kiwidoc.model.tag.LinkReference;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.*;

/**
 * @author yan@pongasoft.com
 */
public class InlineLinkTagSerializer implements Serializer<InlineLinkTag, Object>
{
  private final Serializer<LinkReference, Object> _linkReferenceSerializer;

  public static class FInlineLinkTag
  {
    public static final String text = "t";
    public static final String linkReference = "l";
  }

  public InlineLinkTagSerializer(Serializer<LinkReference, Object> linkReferenceSerializer)
  {
    _linkReferenceSerializer = linkReferenceSerializer;
  }

  public Object serialize(InlineLinkTag tag) throws SerializerException
  {
    if(tag == null)
      return null;
    
    Map<String, Object> content = new HashMap<String, Object>();

    putOnce(content, FInlineLinkTag.text, tag.getText());
    putAllOnce((Map<String, Object>) _linkReferenceSerializer.serialize(tag.getLinkReference()), content);

    return content;
  }

  public InlineLinkTag deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;
    
    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    return new InlineLinkTag(optString(content, FInlineLinkTag.text),
                             _linkReferenceSerializer.deserialize(context, objectToDeserialize));
  }
}
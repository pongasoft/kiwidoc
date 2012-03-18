
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

import com.pongasoft.kiwidoc.builder.serializer.CollectionSerializer;
import com.pongasoft.kiwidoc.builder.serializer.Serializer;
import com.pongasoft.kiwidoc.builder.serializer.SerializerException;
import com.pongasoft.kiwidoc.model.tag.DocType;
import com.pongasoft.kiwidoc.model.tag.InlineTag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.*;

/**
 * @author yan@pongasoft.com
 */
public class DocTypeSerializer
{
  public static class FAbstractTag
  {
    public static final String name = "n";
    public static final String inlineTags = "t";
  }

  private final CollectionSerializer<InlineTag, Object> _inlineTagsSerializer;

  public DocTypeSerializer(Serializer<InlineTag, Object> inlineTagSerializer)
  {
    _inlineTagsSerializer = new CollectionSerializer<InlineTag, Object>(inlineTagSerializer);
  }

  public Map<String, Object> serialize(DocType tag) throws SerializerException
  {
    if(tag == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();

    putOnce(content, FAbstractTag.name, tag.getName());
    putOnce(content, FAbstractTag.inlineTags, _inlineTagsSerializer.serialize(tag.getInlineTags()));

    return content;
  }

  public String deserialiseName(Map<String, Object> content)
  {
    return optString(content, FAbstractTag.name);
  }

  public Collection<InlineTag> deserializeInlineTags(Map<String, Object> content) throws SerializerException
  {
    return _inlineTagsSerializer.deserialize(null, opt(content, FAbstractTag.inlineTags));
  }
}

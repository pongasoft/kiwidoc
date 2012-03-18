
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
import com.pongasoft.kiwidoc.model.tag.InlineOrdinaryTag;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.optString;
import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.putOnce;

/**
 * @author yan@pongasoft.com
 */
public class InlineOrdinaryTagSerializer implements Serializer<InlineOrdinaryTag, Object>
{
  public static class FInlineOrdinaryTag
  {
    public static final String text = "t";
    public static final String name = "n";
  }

  public Object serialize(InlineOrdinaryTag tag) throws SerializerException
  {
    if(tag == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();

    putOnce(content, FInlineOrdinaryTag.text, tag.getText());
    putOnce(content, FInlineOrdinaryTag.name, tag.getName());

    return content;
  }

  public InlineOrdinaryTag deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    return new InlineOrdinaryTag(optString(content, FInlineOrdinaryTag.name),
                                 optString(content, FInlineOrdinaryTag.text));
  }
}

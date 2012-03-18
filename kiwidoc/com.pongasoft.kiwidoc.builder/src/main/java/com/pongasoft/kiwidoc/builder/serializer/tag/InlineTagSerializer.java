
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

import com.pongasoft.kiwidoc.builder.serializer.EnumKindSerializer;
import com.pongasoft.kiwidoc.builder.serializer.Serializer;
import com.pongasoft.kiwidoc.model.tag.InlineTag;

import java.util.EnumMap;

/**
 * @author yan@pongasoft.com
 */
public class InlineTagSerializer extends EnumKindSerializer<InlineTag.Kind, InlineTag, Object>
{
  /**
   * Constructor
   */
  public InlineTagSerializer()
  {
    super(InlineTag.Kind.class, doBuildMap());
  }

  private static EnumMap<InlineTag.Kind, Serializer<? extends InlineTag, Object>> doBuildMap()
  {
    EnumMap<InlineTag.Kind, Serializer<? extends InlineTag, Object>> res =
      new EnumMap<InlineTag.Kind, Serializer<? extends InlineTag, Object>>(InlineTag.Kind.class);

    res.put(InlineTag.Kind.ORDINARY, new InlineOrdinaryTagSerializer());
    res.put(InlineTag.Kind.TEXT, new InlineTextTagSerializer());
    res.put(InlineTag.Kind.LINK, new InlineLinkTagSerializer(new LinkReferenceSerializer()));

    return res;
  }
}

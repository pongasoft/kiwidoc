
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
import com.pongasoft.kiwidoc.builder.serializer.type.TypeSerializer;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.kiwidoc.model.tag.Tag;

import java.util.EnumMap;

/**
 * @author yan@pongasoft.com
 */
public class TagSerializer extends EnumKindSerializer<Tag.Kind, Tag, Resource>
{
  /**
   * Constructor
   */
  public TagSerializer()
  {
    super(Tag.Kind.class, doBuildMap());
  }

  private static EnumMap<Tag.Kind, Serializer<? extends Tag, Resource>> doBuildMap()
  {
    EnumMap<Tag.Kind, Serializer<? extends Tag, Resource>> res =
      new EnumMap<Tag.Kind, Serializer<? extends Tag, Resource>>(Tag.Kind.class);

    DocTypeSerializer ats = new DocTypeSerializer(new InlineTagSerializer());

    res.put(Tag.Kind.ORDINARY, new OrdinaryTagSerializer(ats));
    res.put(Tag.Kind.SEE, new SeeTagSerializer(ats, new LinkReferenceSerializer()));
    res.put(Tag.Kind.PARAM, new ParamTagSerializer(ats));
    res.put(Tag.Kind.THROWS, new ThrowsTagSerializer(ats, new TypeSerializer()));

    return res;
  }
}
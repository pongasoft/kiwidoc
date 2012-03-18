
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
import com.pongasoft.kiwidoc.model.tag.InlineTextTag;

/**
 * @author yan@pongasoft.com
 */
public class InlineTextTagSerializer implements Serializer<InlineTextTag, Object>
{
  public Object serialize(InlineTextTag tag) throws SerializerException
  {
    return tag.getText();
  }

  public InlineTextTag deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    return new InlineTextTag(String.valueOf(objectToDeserialize));
  }
}

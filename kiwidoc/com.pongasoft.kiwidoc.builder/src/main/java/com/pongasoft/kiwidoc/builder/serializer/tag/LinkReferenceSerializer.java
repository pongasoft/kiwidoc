
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
import com.pongasoft.kiwidoc.model.tag.LinkReference;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.optString;
import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.putOnce;

/**
 * @author yan@pongasoft.com
 */
public class LinkReferenceSerializer implements Serializer<LinkReference, Object>
{
  public static class FLinkReference
  {
    public static final String rawReference = "r";
    public static final String packageName = "p";
    public static final String simpleClassName = "c";
    public static final String memberName = "m";
  }

  public Object serialize(LinkReference linkReference) throws SerializerException
  {
    if(linkReference == null || linkReference.equals(LinkReference.NO_LINK_REFERENCE))
      return null;

    Map<String, Object> content = new HashMap<String, Object>();

    putOnce(content, FLinkReference.rawReference, linkReference.getRawReference());
    putOnce(content, FLinkReference.packageName, linkReference.getPackageName());
    putOnce(content, FLinkReference.simpleClassName, linkReference.getSimpleClassName());
    putOnce(content, FLinkReference.memberName, linkReference.getMemberName());

    return content;
  }

  public LinkReference deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return LinkReference.NO_LINK_REFERENCE;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    return new LinkReference(optString(content, FLinkReference.rawReference),
                             optString(content, FLinkReference.packageName),
                             optString(content, FLinkReference.simpleClassName),
                             optString(content, FLinkReference.memberName));
  }
}

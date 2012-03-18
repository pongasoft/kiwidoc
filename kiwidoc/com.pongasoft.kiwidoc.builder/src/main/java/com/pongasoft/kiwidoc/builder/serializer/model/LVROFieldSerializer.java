
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

package com.pongasoft.kiwidoc.builder.serializer.model;

import com.pongasoft.kiwidoc.builder.serializer.Serializer;
import com.pongasoft.kiwidoc.builder.serializer.SerializerException;
import com.pongasoft.kiwidoc.builder.serializer.SerializerUtils;
import com.pongasoft.kiwidoc.model.LVROverviewModel;
import com.pongasoft.kiwidoc.model.resource.LVROverviewResource;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.putOnce;
import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.req;

/**
 * @author yan@pongasoft.com
 */
public class LVROFieldSerializer implements Serializer<LVROverviewModel.Field, LVROverviewResource>
{
  public static class FLVROFieldSerializer
  {
    public static final String access = "a";
    public static final String name = "n";
    public static final String signature = "s";
  }

  /**
   * Constructor
   */
  public LVROFieldSerializer()
  {
  }

  public Object serialize(LVROverviewModel.Field field) throws SerializerException
  {
    if(field == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();
    putOnce(content, FLVROFieldSerializer.access, field.getAccess());
    putOnce(content, FLVROFieldSerializer.name, field.getName());
    putOnce(content, FLVROFieldSerializer.signature, field.getSignature());
    return content;
  }

  public LVROverviewModel.Field deserialize(LVROverviewResource context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    return new LVROverviewModel.Field(SerializerUtils.<Integer>req(content,
                                                                   FLVROFieldSerializer.access),
                                      (String) req(content, FLVROFieldSerializer.name),
                                      (String) req(content, FLVROFieldSerializer.signature));
  }
}
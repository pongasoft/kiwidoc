
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
import com.pongasoft.kiwidoc.model.LVROverviewModel;
import com.pongasoft.kiwidoc.model.resource.LVROverviewResource;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.putOnce;
import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.req;

/**
 * @author yan@pongasoft.com
 */
public class LVROMethodSerializer implements Serializer<LVROverviewModel.Method, LVROverviewResource>
{
  public static class FLVROMethodSerializer
  {
    public static final String access = "a";
    public static final String name = "n";
    public static final String signature = "s";
  }

  /**
   * Constructor
   */
  public LVROMethodSerializer()
  {
  }

  public Object serialize(LVROverviewModel.Method method) throws SerializerException
  {
    if(method == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();
    putOnce(content, FLVROMethodSerializer.access, method.getAccess());
    putOnce(content, FLVROMethodSerializer.name, method.getName());
    putOnce(content, FLVROMethodSerializer.signature, method.getSignature());
    return content;
  }

  public LVROverviewModel.Method deserialize(LVROverviewResource context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    return new LVROverviewModel.Method((Integer) req(content, FLVROMethodSerializer.access),
                                       (String) req(content, FLVROMethodSerializer.name),
                                       (String) req(content, FLVROMethodSerializer.signature));
  }
}
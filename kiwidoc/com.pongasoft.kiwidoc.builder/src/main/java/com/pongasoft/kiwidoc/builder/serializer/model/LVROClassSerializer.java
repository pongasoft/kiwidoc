
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

import com.pongasoft.kiwidoc.builder.serializer.CollectionSerializer;
import com.pongasoft.kiwidoc.builder.serializer.Serializer;
import com.pongasoft.kiwidoc.builder.serializer.SerializerException;
import com.pongasoft.kiwidoc.builder.serializer.SerializerUtils;
import com.pongasoft.kiwidoc.model.LVROverviewModel;
import com.pongasoft.kiwidoc.model.resource.LVROverviewResource;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.*;

/**
 * @author yan@pongasoft.com
 */
public class LVROClassSerializer implements Serializer<LVROverviewModel.Class, LVROverviewResource>
{
  public static class FLVROClassSerializer
  {
    public static final String access = "a";
    public static final String name = "n";
    public static final String signature = "s";
    public static final String methods = "m";
    public static final String fields = "f";
  }

  private final CollectionSerializer<LVROverviewModel.Method, LVROverviewResource> _methodsSerializer;
  private final CollectionSerializer<LVROverviewModel.Field, LVROverviewResource> _fieldsSerializer;

  /**
   * Constructor
   */
  public LVROClassSerializer(Serializer<LVROverviewModel.Method, LVROverviewResource> methodSerializer,
                             Serializer<LVROverviewModel.Field, LVROverviewResource> fieldSerializer)
  {
    _methodsSerializer = new CollectionSerializer<LVROverviewModel.Method, LVROverviewResource>(methodSerializer);
    _fieldsSerializer = new CollectionSerializer<LVROverviewModel.Field, LVROverviewResource>(fieldSerializer);
  }

  public Object serialize(LVROverviewModel.Class c) throws SerializerException
  {
    if(c == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();
    putOnce(content, FLVROClassSerializer.access, c.getAccess());
    putOnce(content, FLVROClassSerializer.name, c.getName());
    putOnce(content, FLVROClassSerializer.signature, c.getSignature());
    putOnce(content, FLVROClassSerializer.methods, _methodsSerializer.serialize(c.getMethods()));
    putOnce(content, FLVROClassSerializer.fields, _fieldsSerializer.serialize(c.getFields()));
    return content;
  }

  public LVROverviewModel.Class deserialize(LVROverviewResource context,
                                            Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    return new LVROverviewModel.Class(SerializerUtils.<Integer>req(content, FLVROClassSerializer.access),
                                      (String) req(content, FLVROClassSerializer.name),
                                      (String) req(content, FLVROClassSerializer.signature),
                                      _methodsSerializer.deserialize(context, opt(content,
                                                                                  FLVROClassSerializer.methods)),
                                      _fieldsSerializer.deserialize(context, opt(content,
                                                                                 FLVROClassSerializer.fields)));
  }
}

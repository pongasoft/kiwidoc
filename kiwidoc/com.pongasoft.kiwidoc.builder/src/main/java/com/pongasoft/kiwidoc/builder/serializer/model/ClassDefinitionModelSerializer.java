
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
import com.pongasoft.kiwidoc.model.ClassDefinitionModel;
import com.pongasoft.kiwidoc.model.type.GenericType;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.putOnce;
import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.req;

/**
 * @author yan@pongasoft.com
 */
public class ClassDefinitionModelSerializer implements Serializer<ClassDefinitionModel, Object>
{
  public static class FClassDefinitionModel
  {
    public static final String access = "a";
    public static final String type = "t";
  }

  private final Serializer<GenericType, Object> _typeSerializer;

  /**
   * Constructor
   */
  public ClassDefinitionModelSerializer(Serializer<GenericType, Object> typeSerializer)
  {
    _typeSerializer = typeSerializer;
  }

  public Object serialize(ClassDefinitionModel classModel) throws SerializerException
  {
    if(classModel == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();
    putOnce(content, FClassDefinitionModel.access, classModel.getAccess());
    putOnce(content, FClassDefinitionModel.type, _typeSerializer.serialize(classModel.getType()));

    return content;
  }

  public ClassDefinitionModel deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    @SuppressWarnings("unchecked")
    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    return new ClassDefinitionModel(SerializerUtils.<Integer>req(content, FClassDefinitionModel.access),
                                    _typeSerializer.deserialize(context, req(content, FClassDefinitionModel.type)));
  }
}
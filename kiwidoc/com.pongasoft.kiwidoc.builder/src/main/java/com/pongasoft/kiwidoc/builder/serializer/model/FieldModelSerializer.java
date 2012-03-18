
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
import com.pongasoft.kiwidoc.model.FieldModel;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.resource.FieldResource;
import com.pongasoft.kiwidoc.model.type.Type;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.*;

/**
 * @author yan@pongasoft.com
 */
public class FieldModelSerializer implements Serializer<FieldModel, ClassResource>
{
  private final ClassEntryModelSerializer _classEntrySerializer;
  private final Serializer<Type, Object> _typeSerializer;

  public static class FFieldModel
  {
    public static final String type = "t";
    public static final String value = "v";
  }

  /**
   * Constructor
   */
  public FieldModelSerializer(ClassEntryModelSerializer classEntrySerializer,
                              Serializer<Type, Object> typeSerializer)
  {
    _classEntrySerializer = classEntrySerializer;
    _typeSerializer = typeSerializer;
  }

  public Object serialize(FieldModel field) throws SerializerException
  {
    if(field == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();
    putAllOnce((Map<String, Object>) _classEntrySerializer.serialize(field), content);
    putOnce(content, FFieldModel.type, _typeSerializer.serialize(field.getType()));
    // TODO HIGH YP: problem when trying to store infinity... is it ok to have field be a string ?
    if(field.getValue() != null)
      putOnce(content, FFieldModel.value, String.valueOf(field.getValue()));
    return content;
  }

  public FieldModel deserialize(ClassResource context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    FieldResource fieldResource = new FieldResource(context, _classEntrySerializer.deserializeName(content));

    return new FieldModel(_classEntrySerializer.deserialize(fieldResource, content),
                          _typeSerializer.deserialize(fieldResource, req(content,
                                                                         FFieldModel.type)),
                          opt(content, FFieldModel.value));
  }
}

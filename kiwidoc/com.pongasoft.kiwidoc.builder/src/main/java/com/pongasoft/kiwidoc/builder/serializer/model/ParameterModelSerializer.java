
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
import com.pongasoft.kiwidoc.model.AnnotationModel;
import com.pongasoft.kiwidoc.model.AnnotationsModel;
import com.pongasoft.kiwidoc.model.ParameterModel;
import com.pongasoft.kiwidoc.model.type.Type;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.*;

/**
 * @author yan@pongasoft.com
 */
public class ParameterModelSerializer implements Serializer<ParameterModel, Object>
{
  public static class FParameterModel
  {
    public static final String name = "n";
    public static final String type = "t";
    public static final String annotations = "w";
  }

  private final Serializer<Type, Object> _typeSerializer;
  private final CollectionSerializer<AnnotationModel, Object> _annotationsSerializer;

  /**
   * Constructor
   */
  public ParameterModelSerializer(Serializer<Type, Object> typeSerializer,
                                  Serializer<AnnotationModel, Object> annotationSerializer)
  {
    _typeSerializer = typeSerializer;
    _annotationsSerializer = new CollectionSerializer<AnnotationModel, Object>(annotationSerializer);
  }

  public Object serialize(ParameterModel parameter) throws SerializerException
  {
    if(parameter == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();
    putOnce(content, FParameterModel.name, parameter.getName());
    putOnce(content, FParameterModel.type, _typeSerializer.serialize(parameter.getType()));
    putOnce(content, FParameterModel.annotations, _annotationsSerializer.serialize(parameter.getAnnotations().getAnnotations()));

    return content;
  }

  public ParameterModel deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    return new ParameterModel(optString(content, FParameterModel.name),
                              _typeSerializer.deserialize(context, req(content,
                                                                       FParameterModel.type)),
                              new AnnotationsModel(_annotationsSerializer.deserialize(context, opt(
                                content,
                                FParameterModel.annotations))));
  }
}

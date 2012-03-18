
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
import com.pongasoft.kiwidoc.model.ClassModel;
import com.pongasoft.kiwidoc.model.MethodModel;
import com.pongasoft.kiwidoc.model.ParameterModel;
import com.pongasoft.kiwidoc.model.ParametersModel;
import com.pongasoft.kiwidoc.model.annotation.AnnotationValue;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.resource.MethodResource;
import com.pongasoft.kiwidoc.model.type.GenericTypeVariables;
import com.pongasoft.kiwidoc.model.type.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.*;

/**
 * @author yan@pongasoft.com
 */
public class MethodModelSerializer implements Serializer<MethodModel, ClassResource>
{
  public static class FMethodModel
  {
    public static final String parameters = "p";
    public static final String genericTypeVariables = "g";
    public static final String returnType = "r";
    public static final String exceptions = "e";
    public static final String annotationDefaultValue = "z";
  }

  private final ClassEntryModelSerializer _classEntrySerializer;
  private final Serializer<GenericTypeVariables, Object> _gtvSerializer;
  private final Serializer<Type, Object> _typeSerializer;
  private final Serializer<AnnotationValue, Object> _annotationValueSerializer;
  private final CollectionSerializer<Type, Object> _typesSerializer;
  private final CollectionSerializer<ParameterModel, Object> _parametersSerializer;

  /**
   * Constructor
   */
  public MethodModelSerializer(ClassEntryModelSerializer classEntrySerializer,
                               Serializer<GenericTypeVariables, Object> gtvSerializer,
                               Serializer<Type, Object> typeSerializer,
                               Serializer<ParameterModel, Object> parameterSerializer,
                               Serializer<AnnotationValue, Object> annotationValueSerializer)
  {
    _classEntrySerializer = classEntrySerializer;
    _gtvSerializer = gtvSerializer;
    _typeSerializer = typeSerializer;
    _annotationValueSerializer = annotationValueSerializer;
    _parametersSerializer = new CollectionSerializer<ParameterModel, Object>(parameterSerializer);
    _typesSerializer = new CollectionSerializer<Type, Object>(typeSerializer);
  }

  public Object serialize(MethodModel method) throws SerializerException
  {
    if(method == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();
    putAllOnce((Map<String, Object>) _classEntrySerializer.serialize(method), content);
    putOnce(content, FMethodModel.genericTypeVariables, _gtvSerializer.serialize(method.getGenericTypeVariables()));
    putOnce(content, FMethodModel.returnType, _typeSerializer.serialize(method.getReturnType()));
    putOnce(content, FMethodModel.exceptions, _typesSerializer.serialize(method.getExceptions()));
    putOnce(content, FMethodModel.parameters, _parametersSerializer.serialize(method.getParameters()));
    putOnce(content, FMethodModel.annotationDefaultValue, _annotationValueSerializer.serialize(method.getAnnotationDefaultValue()));
    return content;
  }

  public MethodModel deserialize(ClassResource context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    ArrayList<ParameterModel> parameters =
      new ArrayList<ParameterModel>(_parametersSerializer.deserialize(context,
                                                                      opt(content,
                                                                          FMethodModel.parameters)));

    String memberName = ClassModel.computeMemberName(_classEntrySerializer.deserializeName(content),
                                                     parameters);

    MethodResource methodResource = new MethodResource(context, memberName);

    return new MethodModel(_classEntrySerializer.deserialize(methodResource, content),
                           methodResource,
                           _gtvSerializer.deserialize(methodResource, opt(content,
                                                                          FMethodModel.genericTypeVariables)),
                           new ParametersModel(parameters),
                           _typeSerializer.deserialize(methodResource, opt(content,
                                                                           FMethodModel.returnType)),
                           _typesSerializer.deserialize(methodResource, opt(content,
                                                                            FMethodModel.exceptions)),
                           _annotationValueSerializer.deserialize(methodResource, opt(content,
                                                                                      FMethodModel.annotationDefaultValue)));
  }
}

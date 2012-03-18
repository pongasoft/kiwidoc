
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

package com.pongasoft.kiwidoc.builder.serializer.annotation;

import com.pongasoft.kiwidoc.builder.serializer.CollectionSerializer;
import com.pongasoft.kiwidoc.builder.serializer.Serializer;
import com.pongasoft.kiwidoc.builder.serializer.SerializerException;
import com.pongasoft.kiwidoc.model.annotation.Annotation;
import com.pongasoft.kiwidoc.model.annotation.AnnotationElement;
import com.pongasoft.kiwidoc.model.type.GenericType;
import com.pongasoft.kiwidoc.model.type.Type;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.*;

/**
 * @author yan@pongasoft.com
 */
public class AnnotationSerializer implements Serializer<Annotation, Object>
{
  private final Serializer<Type, Object> _typeSerializer;
  private final CollectionSerializer<AnnotationElement, Object> _annotationElementsSerializer;

  public static class FAnnotations
  {
    public static final String type = "t";
    public static final String values = "v";
  }

  public AnnotationSerializer(Serializer<Type, Object> typeSerializer,
                              Serializer<AnnotationElement, Object> annotationElementSerializer)
  {
    _typeSerializer = typeSerializer;
    _annotationElementsSerializer = new CollectionSerializer<AnnotationElement, Object>(annotationElementSerializer);
  }

  public Object serialize(Annotation annotation) throws SerializerException
  {
    if(annotation == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();

    putOnce(content, FAnnotations.type, _typeSerializer.serialize(annotation.getType()));
    putOnce(content, FAnnotations.values, _annotationElementsSerializer.serialize(annotation.getAnnotationElements()));

    return content;
  }

  public Annotation deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    @SuppressWarnings("unchecked")
    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    return new Annotation((GenericType) _typeSerializer.deserialize(context, req(content,
                                                                                 FAnnotations.type)),
                          _annotationElementsSerializer.deserialize(context, opt(content,
                                                                                 FAnnotations.values)));
  }
}
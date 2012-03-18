
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

import com.pongasoft.kiwidoc.builder.serializer.EnumKindSerializer;
import com.pongasoft.kiwidoc.builder.serializer.Serializer;
import com.pongasoft.kiwidoc.builder.serializer.type.TypeSerializer;
import com.pongasoft.kiwidoc.model.annotation.Annotation;
import com.pongasoft.kiwidoc.model.annotation.AnnotationValue;

import java.util.EnumMap;

/**
 * @author yan@pongasoft.com
 */
public class AnnotationValueSerializer extends EnumKindSerializer<AnnotationValue.Kind, AnnotationValue, Object>
{
  public Serializer<Annotation, Object> _annotationsSerializer;

  /**
   * Constructor
   */
  public AnnotationValueSerializer()
  {
    super(AnnotationValue.Kind.class);
  }

  public Serializer<Annotation, Object> getAnnotationsSerializer()
  {
    return _annotationsSerializer;
  }

  @Override
  protected EnumMap<AnnotationValue.Kind, Serializer<? extends AnnotationValue, Object>> buildMap()
  {
    EnumMap<AnnotationValue.Kind, Serializer<? extends AnnotationValue, Object>> res =
      new EnumMap<AnnotationValue.Kind, Serializer<? extends AnnotationValue, Object>>(AnnotationValue.Kind.class);

    TypeSerializer typeSerializer = new TypeSerializer();
    AnnotationElementSerializer annotationElementSerializer = new AnnotationElementSerializer(this);

    _annotationsSerializer = new AnnotationSerializer(typeSerializer, annotationElementSerializer);

    res.put(AnnotationValue.Kind.PRIMITIVE,
            new PrimitiveAnnotationValueSerializer(typeSerializer));
    res.put(AnnotationValue.Kind.STRING, new StringAnnotationValueSerializer());
    res.put(AnnotationValue.Kind.CLASS, new ClassAnnotationValueSerializer(typeSerializer));
    res.put(AnnotationValue.Kind.ENUM, new EnumAnnotationValueSerializer(typeSerializer));
    res.put(AnnotationValue.Kind.ANNOTATION, new AnnotationAnnotationValueSerializer(
      _annotationsSerializer));
    res.put(AnnotationValue.Kind.ARRAY, new ArrayAnnotationValueSerializer(this));

    return res;
  }
}
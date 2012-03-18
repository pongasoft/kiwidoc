
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

import com.pongasoft.kiwidoc.builder.serializer.Serializer;
import com.pongasoft.kiwidoc.builder.serializer.SerializerException;
import com.pongasoft.kiwidoc.model.annotation.AnnotationElement;
import com.pongasoft.kiwidoc.model.annotation.AnnotationValue;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.*;

/**
 * @author yan@pongasoft.com
 */
public class AnnotationElementSerializer implements Serializer<AnnotationElement, Object>
{
  private final Serializer<AnnotationValue, Object> _annotationValueSerializer;

  public static class FAnnotation
  {
    public static final String name = "n";
    public static final String value = "v";
  }


  public AnnotationElementSerializer(Serializer<AnnotationValue, Object> annotationValueSerializer)
  {
    _annotationValueSerializer = annotationValueSerializer;
  }

  public Object serialize(AnnotationElement annotationElement) throws SerializerException
  {
    if(annotationElement == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();

    putOnce(content, FAnnotation.name, annotationElement.getName());
    putOnce(content, FAnnotation.value, _annotationValueSerializer.serialize(annotationElement.getAnnotationValue()));

    return content;


  }

  public AnnotationElement deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    @SuppressWarnings("unchecked")
    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    return new AnnotationElement((String) req(content, FAnnotation.name),
                                 _annotationValueSerializer.deserialize(context,
                                                                        opt(content, FAnnotation.value)));
  }
}
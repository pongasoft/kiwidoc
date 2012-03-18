
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
import com.pongasoft.kiwidoc.model.AnnotationModel;
import com.pongasoft.kiwidoc.model.annotation.Annotation;

/**
 * @author yan@pongasoft.com
 */
public class AnnotationModelSerializer implements Serializer<AnnotationModel, Object>
{
  private final Serializer<Annotation, Object> _annotationsSerializer;

  public AnnotationModelSerializer(Serializer<Annotation, Object> annotationsSerializer)
  {
    _annotationsSerializer = annotationsSerializer;
  }

  public Object serialize(AnnotationModel annotation) throws SerializerException
  {
    if(annotation == null)
      return null;

    return _annotationsSerializer.serialize(annotation.getAnnotation());
  }

  public AnnotationModel deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    return new AnnotationModel(_annotationsSerializer.deserialize(context, objectToDeserialize));
  }
}

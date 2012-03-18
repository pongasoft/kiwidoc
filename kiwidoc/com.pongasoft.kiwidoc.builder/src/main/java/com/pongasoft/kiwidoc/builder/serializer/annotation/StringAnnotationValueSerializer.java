
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
import com.pongasoft.kiwidoc.model.annotation.StringAnnotationValue;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.optString;
import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.putOnce;

/**
 * @author yan@pongasoft.com
 */
public class StringAnnotationValueSerializer implements Serializer<StringAnnotationValue, Object>
{
  public static class FStringAnnotationValue
  {
    public static final String value = "v";
  }

  public StringAnnotationValueSerializer()
  {
  }

  public Object serialize(StringAnnotationValue annotation) throws SerializerException
  {
    if(annotation == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();
    putOnce(content, FStringAnnotationValue.value, annotation.getValue());
    return content;
  }

  public StringAnnotationValue deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    @SuppressWarnings("unchecked")
    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    return new StringAnnotationValue(optString(content, FStringAnnotationValue.value));
  }
}
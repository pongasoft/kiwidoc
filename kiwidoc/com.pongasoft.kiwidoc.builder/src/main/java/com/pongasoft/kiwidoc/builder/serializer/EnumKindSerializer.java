
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

package com.pongasoft.kiwidoc.builder.serializer;

import com.pongasoft.kiwidoc.model.Extensible;

import java.util.EnumMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.opt;
import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.putOnce;

/**
 * @author yan@pongasoft.com
 */
public class EnumKindSerializer<K extends Enum<K>, T extends Extensible<K>, C>
  implements Serializer<T, C>
{
  public static final String KIND = "k";

  private final Serializer<K, C> _enumKindSerializer;
  private final EnumMap<K, Serializer<? extends T, C>> _serializers;

  /**
   * Constructor
   * @param serializers
   */
  public EnumKindSerializer(Class<K> enumClass, EnumMap<K, Serializer<? extends T, C>> serializers)
  {
    _enumKindSerializer = new EnumSerializer<K, C>(enumClass);
    _serializers = serializers;
  }

  /**
   * Constructor
   */
  public EnumKindSerializer(Class<K> enumClass)
  {
    _enumKindSerializer = new EnumSerializer<K, C>(enumClass);
    _serializers = buildMap();
  }

  protected EnumMap<K, Serializer<? extends T, C>> buildMap()
  {
    return null;
  }

  @SuppressWarnings("unchecked")
  public Object serialize(T objectToSerialize) throws SerializerException
  {
    if(objectToSerialize == null)
      return null;
    
    Serializer serializer = _serializers.get(objectToSerialize.getKind());
    
    if(serializer == null)
      throw new SerializerException("no serializer found for " + objectToSerialize.getKind());

    Object res = serializer.serialize(objectToSerialize);

    if(res == null)
      return null;

    if(res instanceof Map)
    {
      Map<String, Object> jsonObject = (Map<String, Object>) res;

      putOnce(jsonObject, KIND, _enumKindSerializer.serialize(objectToSerialize.getKind()));

      res = postSerialize(jsonObject);
    }
    
    return res;
  }

  protected Map<String, Object> postSerialize(Map<String, Object> content) throws SerializerException
  {
    return content;
  }

  @SuppressWarnings("unchecked")
  public T deserialize(C context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    String value = null;
    if(objectToDeserialize instanceof Map)
    {
      Map<String, Object> jsonObject = (Map<String, Object>) objectToDeserialize;
      jsonObject = preDeserialize(jsonObject);
      value = opt(jsonObject, KIND);
    }
    
    K kind = _enumKindSerializer.deserialize(context, value);

    Serializer serializer = _serializers.get(kind);

    if(serializer == null)
      throw new SerializerException("no serializer found for " + objectToDeserialize);

    return (T) serializer.deserialize(context, objectToDeserialize);
  }

  protected Map<String, Object> preDeserialize(Map<String, Object> content) throws SerializerException
  {
    return content;
  }
}

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

package com.pongasoft.util.core.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yan@pongasoft.com
 */
class EnumMappingMap implements EnumCodec
{
  /* Use ConcurrentHashMap to avoid coarse-grained locking */
  private final Map<Class<? extends Enum>, EnumMapping> _map = 
    new ConcurrentHashMap<Class<? extends Enum>, EnumMapping>();

  /**
   * Constructor
   */
  public EnumMappingMap()
  {
  }

  /**
   * Given an enum, encodes it as a <code>String</code>. Uses the mapping to do that.
   *
   * @param myEnum
   * @return the value as extracted by the mapping.
   */
  public <T extends Enum<T>> String encode(T myEnum)
  {
    return getValue(myEnum);
  }

  /**
   * Encodes all enumerated values of an Enum into a String[]
   *
   * @param enumClass the class to decode
   * @return all decoded enumerated values of the Enum (ordering not specified)
   */
  public <T extends Enum<T>> String[] encode(Class<T> enumClass)
  {
    return getEnumMapping(enumClass).getValues();
  }

  /**
   * Reverse operation from {@link #encode(Enum)}
   *
   * @param enumClass type of the enum
   * @param value     the value to convert into the enum
   * @return the enum associated to the type of the enum and the value.
   */
  public <T extends Enum<T>> T decode(Class<T> enumClass, String value)
  {
    return getEnum(enumClass, value);
  }

  /**
   * @param enumClass type of the enum
   * @return the default enum associated to this enum class.
   */
  public <T extends Enum<T>> T getDefaultEnum(Class<T> enumClass)
  {
    return getEnumMapping(enumClass).getDefaultEnum();
  }

  /**
   * Given an enum, returns the value as extracted by the mapping.
   *
   * @param myEnum
   * @return the value as extracted by the mapping.  */
  public <T extends Enum<T>> String getValue(T myEnum)
  {
    if(myEnum == null)
      return null;

    return getEnumMapping(myEnum.getDeclaringClass()).getValue(myEnum);
  }

  /**
   * @param enumClass type of the enum
   * @param value the value to convert into the enum
   * @return the enum associated to the type of the enum and the value.  */
  public <T extends Enum<T>> T getEnum(Class<T> enumClass, String value)
  {
    return getEnumMapping(enumClass).getEnum(value);
  }

  /**
   * Given the enum class, returns the enum mapping.
   *
   * @param enumClass
   * @return the enum mapping associated to the enum  */
  @SuppressWarnings("unchecked")
  public <T extends Enum<T>> EnumMapping<T> getEnumMapping(Class<T> enumClass)
  {
    EnumMapping<T> enumMapping = _map.get(enumClass);

    if(enumMapping == null)
    {
      // we check for @Value
      enumMapping = AnnotationEnumMapping.checkAndCreate(enumClass, Value.class);

      // if not found then we check for methods
      if(enumMapping == null)
        enumMapping = MethodEnumMapping.checkAndCreate(enumClass);

      // if still not found we always create a default basic one
      if(enumMapping == null)
        enumMapping = new BasicEnumMapping<T>(enumClass);

      _map.put(enumClass, enumMapping);
    }

    return enumMapping;
  }
}

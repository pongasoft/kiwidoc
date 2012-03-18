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

/**
 * @author yan@pongasoft.com
 */
public interface EnumCodec
{
  /**
   * Default one */
  public final static EnumCodec INSTANCE = new EnumMappingMap();

  /**
   * Given an enum, encodes it as a <code>String</code>. Uses the mapping to do that.
   *
   * @param myEnum
   * @return the value as extracted by the mapping.  */
  <T extends Enum<T>> String encode(T myEnum);

  /**
   * Encodes all enumerated values of an Enum into a String[]
   *
   * @param enumClass the class to decode
   * @return all decoded enumerated values of the Enum (ordering not specified)
   */
  <T extends Enum<T>> String[] encode(Class<T> enumClass);

  /**
   * Reverse operation from {@link #encode(Enum)}
   *
   * @param enumClass type of the enum
   * @param value the value to convert into the enum
   * @return the enum associated to the type of the enum and the value.  */
  <T extends Enum<T>> T decode(Class<T> enumClass, String value);

  /**
   * @param enumClass type of the enum
   * @return the default enum associated to this enum class.  */
  <T extends Enum<T>> T getDefaultEnum(Class<T> enumClass);
}
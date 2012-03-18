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
 * Defines a mapping for an enum: how to go from an enum to a string representation
 * and the other way around.
 *
 * @author yan@pongasoft.com
 */
public interface EnumMapping<T extends Enum>
{
  /**
   * From a value returns the enum.
   *
   * @param value
   * @return {@link #getDefaultEnum()} if value is <code>null</code>. otherwise the enum
   * @throws IllegalArgumentException if the value cannot be 'converted' to an enum  */
  T getEnum(String value);

  /**
   * @return the same as <code>getEnum(null)</code> (can be <code>null</code>) */
  T getDefaultEnum();

  /**
   * From the enum returns the value
   *
   * @param myEnum
   * @return <code>null</code> if myEnum is <code>null</code> otherwise the string representation
   * of the enum. */
  String getValue(T myEnum);
  
  /**
   * Gets all Enum values
   *  
   * @return String representation of all enumerated values in the Enum
   */
  String[] getValues();
}

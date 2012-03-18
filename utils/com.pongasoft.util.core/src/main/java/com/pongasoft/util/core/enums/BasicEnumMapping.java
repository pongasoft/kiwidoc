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
 * Implementation based on <code>Enum.name()</code>.
 *
 * @author yan@pongasoft.com
 */
public class BasicEnumMapping<T extends Enum<T>> implements EnumMapping<T>
{
  private final Class<T> _enumClass;
  private T _defaultEnum;
  private final String[] _values;

  /**
   * Constructor
   */
  public BasicEnumMapping(Class<T> enumClass)
  {
    _enumClass = enumClass;
    _defaultEnum = AnnotationEnumMapping.extractDefaultEnum(enumClass);
    
    T[] enumConstants = _enumClass.getEnumConstants();
    _values = new String[enumConstants.length];
    for (int i = 0; i < enumConstants.length; i++)
    {
      _values[i] = enumConstants[i].name();
    }
  }
 
  /**
   * From a value returns the enum.
   *
   * @param value
   * @return {@link #getDefaultEnum()} if value is <code>null</code>. otherwise the enum
   * @throws IllegalArgumentException if the value cannot be 'converted' to an enum
   */
  public T getEnum(String value)
  {
    if(value == null)
      return getDefaultEnum();

    return Enum.valueOf(_enumClass, value);
  }

  /**
   * @return the same as <code>getEnum(null)</code> (can be <code>null</code>)
   */
  public T getDefaultEnum()
  {
    return _defaultEnum;
  }

  /**
   * From the enum returns the value
   *
   * @param myEnum
   * @return <code>null</code> if myEnum is <code>null</code> otherwise the string representation of
   *         the enum.
   */
  public String getValue(T myEnum)
  {
    return myEnum.name();
  }
  
  /**
   * @see com.pongasoft.util.core.enums.EnumMapping#getValues()
   */
  public String[] getValues()
  {
    return _values;
  }
}

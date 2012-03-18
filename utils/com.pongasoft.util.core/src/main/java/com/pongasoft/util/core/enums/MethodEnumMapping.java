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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;

/**
 * Implementation based on method. It looks for 2 methods using reflection
 *
 * <ul>
 * <li>a static method: <code>Enum createFromValue(String value)</code></li>
 * <li>a public method: <code>String getValue()</code></li>
 * </ul>
 *
 * @author yan@pongasoft.com
 */
public class MethodEnumMapping<T extends Enum<T>> implements EnumMapping<T>
{
  private final Method _createFromValueMethod;
  private final Method _getValueMethod;
  private final String[] _values;

  /**
   * Constructor
   */
  public MethodEnumMapping(Class<T> enumClass) throws NoSuchMethodException
  {
    _createFromValueMethod = enumClass.getDeclaredMethod("createFromValue", String.class);
    if(!Modifier.isStatic(_createFromValueMethod.getModifiers()))
      throw new IllegalArgumentException("createFromValue must be abstract");

    _getValueMethod = enumClass.getDeclaredMethod("getValue");
    
    T[] enumConstants = enumClass.getEnumConstants();
    _values = new String[enumConstants.length];
    for (int i = 0; i < enumConstants.length; i++)
    {
      _values[i] = getValue( enumConstants[i] );
    }
  }

  /**
   * From a value returns the enum.
   *
   * @param value
   * @return {@link #getDefaultEnum()} if value is <code>null</code>. otherwise the enum
   * @throws IllegalArgumentException if the value cannot be 'converted' to an enum
   */
  @SuppressWarnings("unchecked")
  public T getEnum(String value)
  {
    try
    {
      return (T) _createFromValueMethod.invoke(null, value);
    }
    catch(IllegalAccessException e)
    {
      throw new RuntimeException(e);
    }
    catch(InvocationTargetException e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * @return the same as <code>getEnum(null)</code> (can be <code>null</code>)
   */
  public T getDefaultEnum()
  {
    return getEnum(null);
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
    try
    {
      return (String) _getValueMethod.invoke(myEnum);
    }
    catch(IllegalAccessException e)
    {
      throw new RuntimeException(e);
    }
    catch(InvocationTargetException e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * Checks that the enum class contains the 2 methods. If that is the case then
   * creates an instance of this class otherwise returns <code>null</code>.
   *
   * @param enumClass
   * @return <code>null</code> if check fails.
   */
  public static <T extends Enum<T>> MethodEnumMapping<T> checkAndCreate(Class<T> enumClass)
  {
    try
    {
      return new MethodEnumMapping<T>(enumClass);
    }
    catch(NoSuchMethodException e)
    {
      return null;
    }
  }
  
  /**
   * @see com.pongasoft.util.core.enums.EnumMapping#getValues()
   */
  public String[] getValues()
  {
    return _values;
  }
}

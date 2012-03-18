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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.HashMap;

/**
 * This implementation is based on annotations. It looks for fields that have the given annotation
 * and extract the value from it.
 *
 * @author yan@pongasoft.com
 */
public class AnnotationEnumMapping<T extends Enum<T>> implements EnumMapping<T>
{
  private final Map<String, T> _valueMapping = new HashMap<String, T>();
  private final Map<T, String> _enumMapping = new HashMap<T, String>();
  private final String[] _values;

  private final T _defaultEnum;

  /**
  * Constructor
  */
  public <A extends Annotation> AnnotationEnumMapping(Class<T> enumClass, Class<A> annotationClass)
  {
    Field[] fields = enumClass.getFields();
    for(Field field : fields)
    {
      if(field.isEnumConstant())
      {
        A annotation = field.getAnnotation(annotationClass);
        if(annotation == null)
          throw new IllegalArgumentException("Couldn't find annotation " +
                                             annotationClass.getName() +
                                             " for field " +
                                             field.getName() +
                                             " for enum " + enumClass.getName());

        T myEnum = T.valueOf(enumClass, field.getName());
        String value = getValue(annotation);

        _valueMapping.put(value, myEnum);
        _enumMapping.put(myEnum, value);
      }
    }

    _defaultEnum = extractDefaultEnum(enumClass);
    
    T[] enumConstants = enumClass.getEnumConstants();
    _values = new String[enumConstants.length];
    for (int i = 0; i < enumConstants.length; i++)
    {
      _values[i] = getValue( enumConstants[i] );
    }
  }

  /**
   * @return the same as <code>getEnum(null)</code> (can be <code>null</code>)
   */
  public T getDefaultEnum()
  {
    return _defaultEnum;
  }

  /**
   * From a value returns the enum.
   *
   * @param value
   * @return <code>_defaultEnum</code> if value is <code>null</code>. otherwise the enum
   * @throws IllegalArgumentException if the value cannot be 'converted' to an enum  */
  public T getEnum(String value)
  {
    if(value == null)
      return getDefaultEnum();

    T myEnum = _valueMapping.get(value);

    if(myEnum == null)
      throw new IllegalArgumentException(value);

    return myEnum;
  }

  /**
   * From the enum returns the value
   *
   * @param myEnum
   * @return <code>null</code> if myEnum is <code>null</code> otherwise the string representation
   * of the enum. */
  public String getValue(T myEnum)
  {
    if(myEnum == null)
      return null;

    String value = _enumMapping.get(myEnum);
    if(value == null)
      // this should not happen
      throw new RuntimeException(myEnum.name());

    return value;
  }

  private static String getValue(Annotation annotation)
  {
    try
    {
      return (String) annotation.getClass().getMethod("value").invoke(annotation);
    }
    catch(IllegalAccessException e)
    {
      throw new RuntimeException(e);
    }
    catch(InvocationTargetException e)
    {
      throw new RuntimeException(e);
    }
    catch(NoSuchMethodException e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * Extracts the enum that is annotated as the default one.
   *
   * @param enumClass
   * @return the default enum (can be <code>null</code>)
   */
  static <T extends Enum<T>> T extractDefaultEnum(Class<T> enumClass)
  {
    T defaultEnum = null;

    Field[] fields = enumClass.getFields();
    for(Field field : fields)
    {
      if(field.isEnumConstant())
      {
        boolean isDefault = field.getAnnotation(IsDefault.class) != null;
        if(isDefault)
        {
          if(defaultEnum != null)
            throw new IllegalArgumentException("only one default value is allowed for enum " + enumClass.getName());
          defaultEnum = T.valueOf(enumClass, field.getName());
        }
      }
    }

    return defaultEnum;
  }

  /**
   * Checks that the enum class contains a field with the given annotation. If that is the case then
   * creates an instance of this class otherwise returns <code>null</code>.
   *
   * @param enumClass
   * @param annotationClass
   * @return <code>null</code> if check fails.
   */
  public static <T extends Enum<T>, A extends Annotation> AnnotationEnumMapping<T>
    checkAndCreate(Class<T> enumClass, Class<A> annotationClass)
  {
    Field[] fields = enumClass.getFields();
    for(Field field : fields)
    {
      if(field.isEnumConstant())
      {
        if(field.getAnnotation(annotationClass) != null)
          return new AnnotationEnumMapping<T>(enumClass, annotationClass);
      }
    }

    return null;
  }
  
  /**
   * @see com.pongasoft.util.core.enums.EnumMapping#getValues()
   */
  public String[] getValues()
  {
    return _values;
  }
}

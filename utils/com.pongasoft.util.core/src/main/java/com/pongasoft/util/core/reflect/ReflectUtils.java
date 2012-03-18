
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

package com.pongasoft.util.core.reflect;

/**
 * @author yan@pongasoft.com
 */
public class ReflectUtils
{
  /**
   * Sometimes a method requires a generic class but you cannot write <code>Class<Foo<R>></code>
   * if R is a generic type... this method goes around this problem. You would call this method
   * like this instead: <code>ReflectUtils.<Foo<R>>toGenericClass(Foo.class)</code>.
   */
  @SuppressWarnings("unchecked")
  public static <T> Class<T> toGenericClass(Class c)
  {
    return c;
  }

  /**
   * Constructor
   */
  private ReflectUtils()
  {
  }
}

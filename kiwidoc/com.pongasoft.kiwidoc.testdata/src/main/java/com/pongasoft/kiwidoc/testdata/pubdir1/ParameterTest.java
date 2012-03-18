
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

package com.pongasoft.kiwidoc.testdata.pubdir1;

import java.util.Iterator;

/**
 * @author yan@pongasoft.com
 */
public class ParameterTest<K>
{
  public ParameterTest() {}

  public ParameterTest(int p1) {}

  public ParameterTest(@AI2(1) double p2)  {}

  public ParameterTest(int p1, @AI2(1) double p2)  {}

  public ParameterTest(int p1, @AI2(1) double p2, K k)  {}

  public ParameterTest(@AI2(2) int p1, @AI2(1) double p2, K k, int n)  {}

  public ParameterTest(int p1, K k)  {}

  public void method() {}

  public void method(int p1) {}

  public void method(@AI2(1) double p2) {}

  public void method(@AI2(1) double p2, K k) {}

  public void method(K k) {}

  public static void staticMethod() {}

  public static void staticMethod(int p1) {}

  public static void staticMethod(@AI2(1) double p2) {}

  public static <K> void staticMethod(@AI2(1) double p2, K k) {}

  public static <K> void staticMethod(K k) {}

  public static class InnerStaticClass<K>
  {
    public InnerStaticClass() {}

    public InnerStaticClass(int p1) {}

    public InnerStaticClass(@AI2(1) double p2)  {}

    public InnerStaticClass(int p1, @AI2(1) double p2)  {}

    public InnerStaticClass(int p1, @AI2(1) double p2, K k)  {}

    public InnerStaticClass(int p1, K k)  {}

    public void method() {}

    public void method(int p1) {}

    public void method(@AI2(1) double p2) {}

    public void method(@AI2(1) double p2, K k) {}

    public void method(K k) {}

    public static void staticMethod() {}

    public static void staticMethod(int p1) {}

    public static void staticMethod(@AI2(1) double p2) {}

    public static <K> void staticMethod(@AI2(1) double p2, K k) {}

    public static <K> void staticMethod(K k) {}
  }

  public class InnerClass
  {
    public InnerClass() {}

    public InnerClass(int p1) {}

    public InnerClass(@AI2(1) double p2)  {}

    public InnerClass(int p1, @AI2(1) double p2)  {}

    public InnerClass(int p1, @AI2(1) double p2, K k)  {}

    public InnerClass(int p1, K k)  {}

    public void method() {}

    public void method(int p1) {}

    public void method(@AI2(1) double p2) {}

    public void method(@AI2(1) double p2, K k) {}

    public void method(K k) {}
  }

  private abstract class HashIterator<E> implements Iterator<E>
  {
    public boolean hasNext()
    {
      return false;
    }

    public E next()
    {
      return null;
    }

    public void remove()
    {
    }
  }
}

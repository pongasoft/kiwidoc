
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

import java.util.List;

/**
 * @author yan@pongasoft.com
 */
public class Pub1Class4<V> extends Pub1Class2<Integer, Float> implements Pub1Interface2<V>
{
  public static @interface NestedAnnotation
  {
    int value() default 0;
  }

  public static class InnerPub1Class4
  {
    public static class InnerInnerPub1Class4
    {

    }
  }

  public class Inner2Pub1Class4<K>
  {
    public class InnerInner2Pub1Class4<W>
    {

    }
  }

  public class Inner3Pub1Class4
  {
    public class InnerInner3Pub1Class4<W>
    {

    }
  }

  /**
   * Constructor
   */
  public Pub1Class4()
  {
  }

  /**
   * This is the first line
   * and the second one.
   *
   * @param p1 parameter 1
   * @param p2 parameter 2
   * @return a type {@link V} and you should check {@link Pub1Class4<String>}
   */
  public V method1(V p1, List<? extends V> p2)
  {
    return p1;
  }

  /**
   * In Pub1Class4 [ {@inheritDoc} ] 
   */
  public void imethod1(V v)
  {
  }

  public void useInnerClass(InnerPub1Class4 p1)
  {
    
  }

  public void useInnerClass2(Inner2Pub1Class4<Integer> p1)
  {

  }

  public static void staticMethod(Pub1Class4<String>.Inner2Pub1Class4<Integer> p)
  {

  }

  public static void staticMethod2(Pub1Class4<String>.Inner2Pub1Class4<Integer>.InnerInner2Pub1Class4<Double> p)
  {

  }

  public static void staticMethod2(Pub1Class4<String>.Inner3Pub1Class4.InnerInner3Pub1Class4<Double> p)
  {

  }
}

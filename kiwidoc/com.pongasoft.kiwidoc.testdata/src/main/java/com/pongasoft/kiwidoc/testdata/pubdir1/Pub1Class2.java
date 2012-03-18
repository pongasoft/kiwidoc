
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
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yan@pongasoft.com
 */
public class Pub1Class2<E extends Number, F>
{
  private final List<? extends E> _f1 = new ArrayList<E>();

  /**
   * Constructor
   */
  public Pub1Class2()
  {
  }

  public E method1(E p1, List<? extends E> p2)
  {
    return p1;
  }

  public <V extends F> E method2(Pub1Class2<AtomicInteger, V> p1,
                                 Pub1Class2<?, ? super E> p2)
  {
    return null;
  }
}

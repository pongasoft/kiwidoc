
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

/**
 * @author yan@pongasoft.com
 */
public class Pub1Class1
{
  public static final int PSFF1 = 10;
  public static final Integer PSFF2 = 20;

  private final List _f1 = new ArrayList();

  /**
   * Constructor
   */
  public Pub1Class1()
  {
  }

  /**
   * Javadoc for method1.
   *
   * @param p1 parameter 1
   * @param p2 parameter 2
   * @return return value (from Pub1Class1)
   */
  public Integer method1(Integer p1, List p2)
  {
    return p1;
  }

  /**
   * Javadoc for method1.
   *
   * @param p1 parameter 1
   * @param p2 parameter 2
   * @return return value
   */
  public void method2(@Deprecated Integer p1, Integer p2, @Deprecated Integer p3)
  {
  }
}

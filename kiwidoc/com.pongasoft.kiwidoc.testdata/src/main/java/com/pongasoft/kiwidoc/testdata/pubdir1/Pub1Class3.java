
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
public class Pub1Class3 extends Pub1Class1 implements Pub1Interface1
{
  /**
   * Constructor
   */
  public Pub1Class3()
  {
  }

  /**
   * This is the javadoc in Pub1Class3
   * 
   * @return {@inheritDoc}
   */
  @Override
  public Integer method1(Integer p1, List p2)
  {
    return p1;
  }

  public void imethod1(int i)
  {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return super.toString();
  }
}

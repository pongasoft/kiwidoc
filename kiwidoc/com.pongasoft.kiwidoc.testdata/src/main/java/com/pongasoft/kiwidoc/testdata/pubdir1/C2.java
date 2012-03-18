
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

/**
 * @author yan@pongasoft.com
 */
public class C2 extends C1 implements I1, I1_1
{
  private final int _f11 = 11;

  /**
   * Constructor
   */
  public C2()
  {
  }

  public int getF1_1()
  {
    return _f11;
  }

  @Deprecated
  @Override
  @AI2(value = 34, other = 45)
  @AS3(other = "otherAS3")
  @AE4(AE4.E4.B)
  public int getF1()
  {
    return super.getF1();
  }

  @AI2(value = 34, other = 45)
  @AS3(other = "otherAS3")
  @AE4(AE4.E4.B)
  public int getF2()
  {
    return 0;
  }
}

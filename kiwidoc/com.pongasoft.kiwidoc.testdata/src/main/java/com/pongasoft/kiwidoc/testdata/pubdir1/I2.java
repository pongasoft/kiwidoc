
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
import java.util.Set;
import java.util.Enumeration;
import java.math.BigDecimal;

/**
 * @author yan@pongasoft.com
 */
public interface I2<E, F extends Number, G extends List<? extends BigDecimal> & Comparable<? super F> & Enumeration<E>> extends I1, I1_1
{
  int getF2();
}
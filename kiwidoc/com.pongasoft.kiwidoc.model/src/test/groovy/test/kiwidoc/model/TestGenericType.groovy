
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

package test.kiwidoc.model

import com.pongasoft.kiwidoc.model.resource.ClassResource
import com.pongasoft.kiwidoc.model.type.GenericType


/**
 * @author yan@pongasoft.com
*/
class TestGenericType extends GroovyTestCase
{
  void testInnerClasses()
  {
    def cr = new ClassResource('test.C1$Inner1$Inner2')
    def gt = GenericType.create(cr)

    assertEquals(cr, gt.classResource)
    assertEquals(3, gt.typeParts.size())

    assertEquals(new ClassResource("test.C1"), gt.typeParts[0].classResource)
    assertEquals(new ClassResource('test.C1$Inner1'), gt.typeParts[1].classResource)
    assertEquals(cr, gt.typeParts[2].classResource)
  }
}

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

package test.util.html

import com.pongasoft.util.html.SafeHtmlCleaner

/**
 * @author yan@pongasoft.com
*/
public class TestSafeHtmlCleaner extends GroovyTestCase
{
  void testForbiddenTags()
  {
    // handle forbidden tags properly
    assertEquals('this is a <a href="http://nowhere">test</a>',
                 SafeHtmlCleaner.cleanHtml('<body><html>this is a <a href="http://nowhere">test</a></html></body>'))

    // handle wrong tags appropriately
    assertEquals('<ul><li><a>hello</a></li></ul>',
                 SafeHtmlCleaner.cleanHtml('<ul><li><a>hello</li></a>'))

    assertEquals('<ul><li>hello<code></code></li></ul>',
                 SafeHtmlCleaner.cleanHtml('<ul><li></code>hello<code></li></a>'))
  }

  void testForbiddenAttributes()
  {
    assertEquals('<img>',
                 SafeHtmlCleaner.cleanHtml('<img>'))

    assertEquals('<img>',
                 SafeHtmlCleaner.cleanHtml('<img/>'))

    assertEquals('<img src="abc">',
                 SafeHtmlCleaner.cleanHtml('<img src="abc">'))

    assertEquals('<img src="abc">',
                 SafeHtmlCleaner.cleanHtml('<img src="abc" onclick="javascript">'))

    assertEquals('<img src="abc">',
                 SafeHtmlCleaner.cleanHtml('<img onclick="javascript" src="abc">'))

    assertEquals('<img src="abc">',
                 SafeHtmlCleaner.cleanHtml('<img onclick="javascript" src="abc" onload="javascript">'))
  }
}
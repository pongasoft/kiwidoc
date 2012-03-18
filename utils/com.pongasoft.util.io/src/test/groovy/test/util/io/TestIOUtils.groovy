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

package test.util.io

import org.linkedin.groovy.util.io.fs.FileSystemImpl
import org.linkedin.groovy.util.io.fs.FileSystem
import com.pongasoft.util.io.IOUtils
import org.linkedin.util.io.resource.Resource

/**
 * @author yan@pongasoft.com */
public class TestIOUtils extends GroovyTestCase
{
  public void testSerializeToFile()
  {
    FileSystemImpl.createTempFileSystem { FileSystem fs ->
      Resource resource = fs.toResource('myfile.txt')
      assertFalse(resource.exists())
      IOUtils.serializeToFile("abcdef", resource.file)
      assertTrue(resource.exists())
      assertEquals("abcdef", IOUtils.deserializeFromFile(resource.file))

      IOUtils.serializeToFile("klm", resource.file)
      assertTrue(resource.exists())
      assertEquals("klm", IOUtils.deserializeFromFile(resource.file))
    }
  }
}
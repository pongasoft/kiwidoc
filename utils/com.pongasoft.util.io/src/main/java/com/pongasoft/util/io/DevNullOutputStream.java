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

package com.pongasoft.util.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Simulate /dev/null => everything written to it is ignored
 * 
 * @author yan@pongasoft.com
 */
public class DevNullOutputStream extends OutputStream
{
  public static final DevNullOutputStream INSTANCE = new DevNullOutputStream();

  public static DevNullOutputStream instance()
  {
    return INSTANCE;
  }

  /**
   * Constructor
   */
  public DevNullOutputStream()
  {
  }

  @Override
  public void write(int i) throws IOException
  {
    // do nothing
  }
}

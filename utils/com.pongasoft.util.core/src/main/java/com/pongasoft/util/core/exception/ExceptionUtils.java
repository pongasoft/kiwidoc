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

package com.pongasoft.util.core.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exception utilities
 * @author yan@pongasoft.com
 */
public class ExceptionUtils
{
  /**
   * Returns the stack trace of the throwable as a string
   *
   * @param th the throwable
   * @return the stack trace as a string */
  public static String getStackTrace(Throwable th)
  {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    th.printStackTrace(pw);
    return sw.toString();
  }

  /**
   * Returns the throwable as a string. Do not display the stack trace
   *
   * @param th the throwable
   * @return the throwable as a string */
  public static String getThrowableString(Throwable th)
  {
    StringBuffer sb = new StringBuffer(th.getClass().getName());
    sb.append(":\"");
    sb.append(th.getMessage());
    sb.append('"');
    th = th.getCause();
    while(th != null)
    {
      sb.append(" cause: ");
      sb.append(th.getClass().getName());
      sb.append(":\"");
      sb.append(th.getMessage());
      sb.append('"');
      th = th.getCause();
    }

    return sb.toString();
  }

  private ExceptionUtils() {}
}

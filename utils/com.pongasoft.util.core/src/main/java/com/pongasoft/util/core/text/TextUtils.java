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

package com.pongasoft.util.core.text;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Text utilities
 *
 * @author yan@pongasoft.com
 */
public class TextUtils
{
  /**
   * A convenient call to generate a string using the format and arguments.
   *
   * @param format
   * @param args
   * @return the formatted string
   */
  public static String printf(String format, Object... args)
  {
    // TODO MED YP: kind of ineficient...  
    StringWriter sw = new StringWriter();

    PrintWriter pw = new PrintWriter(sw);
    pw.printf(format, args);
    pw.close();

    return sw.toString();
  }

  /**
   * Does left trim. The code is inpsired by the {@link String#trim()} code, but it cannot
   * be as efficient...
   *
   * @param s the string to trim
   * @return the string left trimmed (equivalent to {@link String#trim()} but only on the left.
   * Handles <code>null</code> value properly.
   */
  public static String ltrim(String s)
  {
    if(s == null)
      return null;

    int len = s.length();
    int st = 0;

    while((st < len) && (s.charAt(st) <= ' '))
    {
      st++;
    }
    return ((st > 0) ? s.substring(st) : s);
  }

  private TextUtils()
  {
  }
}

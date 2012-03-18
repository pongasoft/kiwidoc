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

package com.pongasoft.kiwidoc.builder;

import org.linkedin.groovy.util.json.JsonUtils;

import java.util.List;
import java.util.Map;

/**
 * This class is a simple wrapper around json to avoid repeating the same code over and over
 * @author yan@pongasoft.com
 */
public class JsonCodec
{
  /**
   * Constructor
   */
  public JsonCodec()
  {
  }

  @SuppressWarnings("unchecked")
  public static Map<String, Object> fromJsonStringToMap(String json) throws StoreException
  {
    try
    {
      return (Map<String, Object>) JsonUtils.fromJSON(json);
    }
    catch(Throwable e)
    {
      throw new StoreException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static List<Object> fromJsonStringToList(String json) throws StoreException
  {
    try
    {
      return (List<Object>) JsonUtils.fromJSON(json);
    }
    catch(Throwable e)
    {
      throw new StoreException(e);
    }
  }

  public static String toJsonString(Object json)
  {
    return JsonUtils.compactPrint(json);
  }
}

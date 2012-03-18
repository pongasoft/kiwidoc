
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

package com.pongasoft.kiwidoc.builder.serializer;

import java.util.Map;

/**
 * @author yan@pongasoft.com
 */
public class SerializerUtils
{
  /**
   * Puts all the entries from source into dest with 'once' meaning 
   * (see {@link #putOnce(Map, String, Object)})
   * 
   * @param source ok to be <code>null</code>
   */
  public static void putAllOnce(Map<String, Object> source,
                                Map<String, Object> dest) throws SerializerException
  {
    if(source == null)
      return;

    for(String key : source.keySet())
    {
      putOnce(dest, key, source.get(key));
    }
  }

  /**
   * Somehow this method is defined in the javadoc API online, but is not implemented! Put
   * a key/value pair in the Map<String, Object>, but only if the key and the value are both non-null,
   * and only if there is not already a member with that name
   */
  public static void putOnce(Map<String, Object> jsonObject,
                             String key,
                             Object value) throws SerializerException
  {
    if(jsonObject.containsKey(key))
      throw new SerializerException("duplicate key " + key);
    
    jsonObject.put(key, value);
  }

  /**
   * @return type or <code>null</code>
   */
  public static <T> T opt(Map<String, Object> jsonObject, String key)
  {
    return (T) jsonObject.get(key);
  }

  /**
   * @return type or <code>defaultValue</code>
   */
  public static String opt(Map<String, Object> jsonObject, String key, String defaultValue)
  {
    if(jsonObject.containsKey(key))
      return (String) jsonObject.get(key);
    else
      return defaultValue;
  }

  /**
   * @return type or empty string
   */
  public static String optString(Map<String, Object> jsonObject, String key)
  {
    return opt(jsonObject, key, "");
  }

  /**
   * @return type or <code>null</code>
   */
  public static boolean opt(Map<String, Object> jsonObject, String key, boolean defaultValue)
  {
    if(jsonObject.containsKey(key))
      return (Boolean) jsonObject.get(key);
    else
      return defaultValue;
  }

  /**
   * @return type or <code>null</code>
   */
  public static <T> T req(Map<String, Object> jsonObject, String key) throws SerializerException
  {
    if(!jsonObject.containsKey(key))
      throw new SerializerException("no such key " + key);

    return (T) jsonObject.get(key);
  }


  /**
   * Constructor
   */
  private SerializerUtils()
  {
  }
}

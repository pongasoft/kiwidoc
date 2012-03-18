
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

package com.pongasoft.kiwidoc.model;

import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;
import java.util.LinkedHashMap;

/**
 * @author yan@pongasoft.com
 */
public class OSGiModel
{
  public static Set<String> OSGI_HEADERS =
    new HashSet<String>(Arrays.asList("Bundle-ActivationPolicy", "Bundle-Activator", "Bundle-Category",
                                      "Bundle-ClassPath", "Bundle-ContactAddress", "Bundle-Copyright",
                                      "Bundle-Description", "Bundle-DocURL", "Bundle-Localization",
                                      "Bundle-ManifestVersion", "Bundle-Name", "Bundle-NativeCode",
                                      "Bundle-RequiredExecutionEnvironment", "Bundle-SymbolicName",
                                      "Bundle-UpdateLocation", "Bundle-Vendor", "Bundle-Version",
                                      "DynamicImport-Package", "Export-Package", "Export-Service",
                                      "Fragment-Host", "Import-Package", "Import-Service", "Require-Bundle"));
  
  
  public static class Header
  {
    private final String _name;
    private final Map<String, Map<String, String>> _value;

    public Header(String name, Map<String, Map<String, String>> value)
    {
      _name = name;
      _value = value;
    }

    public String getName()
    {
      return _name;
    }

    public Map<String, Map<String, String>> getValue()
    {
      return _value;
    }

    public String getValueAsString()
    {
      return OSGiModel.getValueAsString(_value);
    }
  }

  private final Map<String, Header> _headers;

  /**
   * Constructor
   */
  public OSGiModel(Collection<Header> headers)
  {
    Map<String, Header> map = new LinkedHashMap<String, Header>();

    for(Header header : headers)
    {
      map.put(header.getName(), header);
    }

    _headers = Collections.unmodifiableMap(map);
  }

  public Map<String, Header> getHeaders()
  {
    return _headers;
  }

  public Header findHeader(String name)
  {
    return _headers.get(name);
  }

  public String getBundleSymbolicName()
  {
    return getHeaderValueAsString("Bundle-SymbolicName");
  }

  public String getHeaderValueAsString(String name)
  {
    Header header = findHeader(name);
    if(header == null)
      return null;

    return header.getValueAsString();
  }

  public static String getValueAsString(Map<String, Map<String, String>> value)
  {
    if(value == null)
      return null;

    StringBuilder sb = new StringBuilder();

    for(Map.Entry<String, Map<String, String>> clause : value.entrySet())
    {
      if(sb.length() > 0)
        sb.append(',');

      sb.append(clause.getKey());

      if(clause.getValue() != null)
      {
        for(Map.Entry<String, String> path : clause.getValue().entrySet())
        {
          sb.append(';');
          sb.append(path.getKey()).append("=\"").append(path.getValue()).append('"');
        }
      }
    }

    return sb.toString();
  }
}

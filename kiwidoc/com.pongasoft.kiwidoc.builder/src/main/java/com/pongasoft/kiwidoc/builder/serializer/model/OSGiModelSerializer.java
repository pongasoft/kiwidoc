
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

package com.pongasoft.kiwidoc.builder.serializer.model;

import aQute.libg.header.OSGiHeader;
import com.pongasoft.kiwidoc.builder.serializer.Serializer;
import com.pongasoft.kiwidoc.builder.serializer.SerializerException;
import com.pongasoft.kiwidoc.model.OSGiModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.optString;

/**
 * @author yan@pongasoft.com
 */
public class OSGiModelSerializer implements Serializer<OSGiModel, Object>
{
  /**
   * Constructor
   */
  public OSGiModelSerializer()
  {
  }

  public Object serialize(OSGiModel model) throws SerializerException
  {
    if(model == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();

    for(String headerName : model.getHeaders().keySet())
    {
      content.put(headerName, model.getHeaderValueAsString(headerName));
    }

    return content;
  }

  public OSGiModel deserialize(Object context, Object objectToDeserialize)
    throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    Collection<OSGiModel.Header> headers = new ArrayList<OSGiModel.Header>();

    for(String headerName : content.keySet())
    {
      Map<String, Map<String, String>> headerValue =
        OSGiHeader.parseHeader(optString(content, headerName));

      headers.add(new OSGiModel.Header(headerName, headerValue));
    }

    return new OSGiModel(headers);

  }
}


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

import com.pongasoft.kiwidoc.builder.serializer.CollectionSerializer;
import com.pongasoft.kiwidoc.builder.serializer.Serializer;
import com.pongasoft.kiwidoc.builder.serializer.SerializerException;
import com.pongasoft.kiwidoc.model.LVROverviewModel;
import com.pongasoft.kiwidoc.model.resource.LVROverviewResource;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.*;

/**
 * @author yan@pongasoft.com
 */
public class LVROPackageSerializer implements Serializer<LVROverviewModel.Package, LVROverviewResource>
{
  public static class FLVROPackageSerializer
  {
    public static final String access = "a";
    public static final String name = "n";
    public static final String classes = "c";
  }

  private final CollectionSerializer<LVROverviewModel.Class, LVROverviewResource> _classesSerializer;

  /**
   * Constructor
   */
  public LVROPackageSerializer(Serializer<LVROverviewModel.Class, LVROverviewResource> classSerializer)
  {
    _classesSerializer = new CollectionSerializer<LVROverviewModel.Class, LVROverviewResource>(classSerializer);
  }

  public Object serialize(LVROverviewModel.Package c) throws SerializerException
  {
    if(c == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();
    putOnce(content, FLVROPackageSerializer.access, c.getAccess());
    putOnce(content, FLVROPackageSerializer.name, c.getName());
    putOnce(content, FLVROPackageSerializer.classes, _classesSerializer.serialize(c.getClasses()));
    return content;
  }

  public LVROverviewModel.Package deserialize(LVROverviewResource context,
                                            Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    return new LVROverviewModel.Package((Integer) req(content, FLVROPackageSerializer.access),
                                        (String) req(content, FLVROPackageSerializer.name),
                                        _classesSerializer.deserialize(context, opt(content,
                                                                                    FLVROPackageSerializer.classes)));
  }
}

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
import com.pongasoft.kiwidoc.model.SimplePackageModel;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.PackageResource;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.*;

/**
 * @author yan@pongasoft.com
 */
public class SimplePackageModelSerializer implements Serializer<SimplePackageModel, LibraryVersionResource>
{
  public static class FPackageModel
  {
    public static final String name = "n";
    public static final String exportedClasses = "c";
    public static final String privateClasses = "x";
  }

  private final CollectionSerializer<String, Object> _stringsSerializer;

  /**
   * Constructor
   */
  public SimplePackageModelSerializer(Serializer<String, Object> stringsSerializer)
  {
    _stringsSerializer = new CollectionSerializer<String, Object>(stringsSerializer);
  }

  public Object serialize(SimplePackageModel packageModel) throws SerializerException
  {
    if(packageModel == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();
    putOnce(content, FPackageModel.name, packageModel.getName());
    putOnce(content, FPackageModel.exportedClasses, _stringsSerializer.serialize(packageModel.getExportedClasses()));
    putOnce(content, FPackageModel.privateClasses, _stringsSerializer.serialize(packageModel.getPrivateClasses()));

    return content;
  }

  public SimplePackageModel deserialize(LibraryVersionResource context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    String packageName = req(content, FPackageModel.name);

    return new SimplePackageModel(new PackageResource(context, packageName),
                                  null,
                                  _stringsSerializer.deserialize(context, opt(content,
                                                                              FPackageModel.exportedClasses)),
                                  _stringsSerializer.deserialize(context, opt(content,
                                                                              FPackageModel.privateClasses)));
  }
}
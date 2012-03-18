
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
import com.pongasoft.kiwidoc.model.ClassDefinitionModel;
import com.pongasoft.kiwidoc.model.DocModel;
import com.pongasoft.kiwidoc.model.PackageModel;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.PackageResource;
import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.*;

/**
 * @author yan@pongasoft.com
 */
public class PackageModelSerializer implements Serializer<PackageModel, Object>
{
  public static class FPackageModel
  {
    public static final String access = "a";
    public static final String library = "l";
    public static final String info = "i";
    public static final String name = "n";
    public static final String classes = "c";
  }

  private final Serializer<LibraryVersionResource, Object> _libraryResourceSerializer;
  private final Serializer<DocModel, Resource> _docSerializer;
  private final CollectionSerializer<ClassDefinitionModel, Object> _classDefinitionsSerializer;

  /**
   * Constructor
   */
  public PackageModelSerializer(Serializer<LibraryVersionResource, Object> libraryResourceSerializer,
                                Serializer<ClassDefinitionModel, Object> classDefinitionSerializer,
                                Serializer<DocModel, Resource> docSerializer)
  {
    _libraryResourceSerializer = libraryResourceSerializer;
    _classDefinitionsSerializer = new CollectionSerializer<ClassDefinitionModel, Object>(classDefinitionSerializer);
    _docSerializer = docSerializer;
  }

  public Object serialize(PackageModel packageModel) throws SerializerException
  {
    if(packageModel == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();
    putOnce(content, FPackageModel.access, packageModel.getAccess());
    putOnce(content, FPackageModel.library, _libraryResourceSerializer.serialize(packageModel.getResource().getLibraryVersionResource()));
    putOnce(content, FPackageModel.name, packageModel.getName());
    putOnce(content, FPackageModel.info, _docSerializer.serialize(packageModel.getPackageInfo()));
    putOnce(content, FPackageModel.classes, _classDefinitionsSerializer.serialize(packageModel.getAllClasses()));

    return content;
  }

  public PackageModel deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    LibraryVersionResource libraryVersionResource =
      _libraryResourceSerializer.deserialize(context, req(content, FPackageModel.library));
    String packageName = req(content, FPackageModel.name);
      
    return new PackageModel((Integer) req(content, FPackageModel.access),
                            new PackageResource(libraryVersionResource, packageName),
                            _docSerializer.deserialize(libraryVersionResource, opt(content,
                                                                                   FPackageModel.info)),
                            _classDefinitionsSerializer.deserialize(context, opt(content,
                                                                                 FPackageModel.classes)));
  }
}

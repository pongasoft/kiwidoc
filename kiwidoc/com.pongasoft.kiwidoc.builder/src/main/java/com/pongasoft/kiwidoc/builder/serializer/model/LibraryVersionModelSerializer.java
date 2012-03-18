
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
import com.pongasoft.kiwidoc.model.BuiltFrom;
import com.pongasoft.kiwidoc.model.DependenciesModel;
import com.pongasoft.kiwidoc.model.DocModel;
import com.pongasoft.kiwidoc.model.LibraryVersionModel;
import com.pongasoft.kiwidoc.model.OSGiModel;
import com.pongasoft.kiwidoc.model.SimplePackageModel;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.*;

/**
 * @author yan@pongasoft.com
 */
public class LibraryVersionModelSerializer implements Serializer<LibraryVersionModel, Object>
{
  public static class FLibraryVersionModel
  {
    public static final String library = "l";
    public static final String jdkVersion = "j";
    public static final String builtFrom = "b";
    public static final String dependencies = "d";
    public static final String exportedPackages = "p";
    public static final String privatePackages = "x";
    public static final String hasManifest = "m";
    public static final String OSGiBundle = "z";
    public static final String overview = "o";
  }

  private final Serializer<LibraryVersionResource, Object> _libraryResourceSerializer;
  private final Serializer<DependenciesModel, Object> _dependenciesModelSerializer;
  private final Serializer<DocModel, Resource> _docSerializer;
  private final Serializer<OSGiModel, Object> _OSGiModelSerializer;
  private final CollectionSerializer<BuiltFrom, Object> _builtFromsSerializer;
  private final CollectionSerializer<SimplePackageModel, LibraryVersionResource> _simplePackagesSerializer;

  /**
   * Constructor
   */
  public LibraryVersionModelSerializer(Serializer<LibraryVersionResource, Object> libraryResourceSerializer,
                                       Serializer<DependenciesModel, Object> dependenciesModelSerializer,
                                       Serializer<BuiltFrom, Object> builtFromSerializer,
                                       Serializer<DocModel, Resource> docSerializer,
                                       Serializer<SimplePackageModel, LibraryVersionResource> simplePackageSerializer,
                                       Serializer<OSGiModel, Object> OSGiModelSerializer)
  {
    _libraryResourceSerializer = libraryResourceSerializer;
    _dependenciesModelSerializer = dependenciesModelSerializer;
    _docSerializer = docSerializer;
    _OSGiModelSerializer = OSGiModelSerializer;
    _builtFromsSerializer = new CollectionSerializer<BuiltFrom, Object>(builtFromSerializer);
    _simplePackagesSerializer = new CollectionSerializer<SimplePackageModel, LibraryVersionResource>(simplePackageSerializer);
  }

  public Object serialize(LibraryVersionModel lib) throws SerializerException
  {
    if(lib == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();
    putOnce(content, FLibraryVersionModel.library, _libraryResourceSerializer.serialize(lib.getResource()));
    putOnce(content, FLibraryVersionModel.jdkVersion, lib.getJdkVersion());
    putOnce(content, FLibraryVersionModel.builtFrom, _builtFromsSerializer.serialize(lib.getBuiltFrom()));
    putOnce(content, FLibraryVersionModel.overview, _docSerializer.serialize(lib.getOverview()));
    putOnce(content, FLibraryVersionModel.exportedPackages, _simplePackagesSerializer.serialize(lib.getExportedPackages()));
    putOnce(content, FLibraryVersionModel.privatePackages, _simplePackagesSerializer.serialize(lib.getPrivatePackages()));
    putOnce(content, FLibraryVersionModel.dependencies, _dependenciesModelSerializer.serialize(lib.getDependencies()));
    if(lib.isOSGiBundle())
      putOnce(content, FLibraryVersionModel.OSGiBundle, _OSGiModelSerializer.serialize(lib.getOSGiModel()));
    if(lib.getHasManifest())
      putOnce(content, FLibraryVersionModel.hasManifest, lib.getHasManifest());
    return content;
  }

  public LibraryVersionModel deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    LibraryVersionResource libraryVersionResource;
    libraryVersionResource =
      _libraryResourceSerializer.deserialize(context, req(content, FLibraryVersionModel.library));

    return new LibraryVersionModel(libraryVersionResource,
                                   (Integer) req(content, FLibraryVersionModel.jdkVersion),
                                   _builtFromsSerializer.deserialize(context, opt(content,
                                                                                  FLibraryVersionModel.builtFrom)),
                                   _docSerializer.deserialize(libraryVersionResource, opt(content,
                                                                                          FLibraryVersionModel.overview)),
                                   _dependenciesModelSerializer.deserialize(context, opt(content,
                                                                                         FLibraryVersionModel.dependencies)),
                                   _simplePackagesSerializer.deserialize(libraryVersionResource, opt(
                                     content,
                                     FLibraryVersionModel.exportedPackages)),
                                   _simplePackagesSerializer.deserialize(libraryVersionResource, opt(
                                     content,
                                     FLibraryVersionModel.privatePackages)),
                                   _OSGiModelSerializer.deserialize(libraryVersionResource, opt(
                                     content,
                                     FLibraryVersionModel.OSGiBundle)),
                                   opt(content, FLibraryVersionModel.hasManifest, false));
  }
}

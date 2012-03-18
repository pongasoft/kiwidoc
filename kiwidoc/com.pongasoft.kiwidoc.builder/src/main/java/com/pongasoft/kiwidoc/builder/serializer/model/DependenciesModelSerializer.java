
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
import com.pongasoft.kiwidoc.model.DependenciesModel;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.opt;
import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.putOnce;

/**
 * @author yan@pongasoft.com
 */
public class DependenciesModelSerializer implements Serializer<DependenciesModel, Object>
{
  public static class FDependenciesModel
  {
    public static final String direct = "d";
    public static final String transitive = "t";
    public static final String optional = "o";
  }

  private final CollectionSerializer<LibraryVersionResource, Object> _libraryResourcesSerializer;

  /**
   * Constructor
   */
  public DependenciesModelSerializer(Serializer<LibraryVersionResource, Object> libraryResourceSerializer)
  {
    _libraryResourcesSerializer =
      new CollectionSerializer<LibraryVersionResource, Object>(libraryResourceSerializer);
  }

  public Object serialize(DependenciesModel dependencies) throws SerializerException
  {
    if(dependencies == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();
    putOnce(content, FDependenciesModel.direct, _libraryResourcesSerializer.serialize(dependencies.getDirectDependencies()));
    putOnce(content, FDependenciesModel.transitive, _libraryResourcesSerializer.serialize(dependencies.getTransitiveDependencies()));
    putOnce(content, FDependenciesModel.optional, _libraryResourcesSerializer.serialize(dependencies.getOptionalDependencies()));
    return content;
  }

  public DependenciesModel deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return DependenciesModel.NO_DEPENDENCIES;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    return new DependenciesModel(_libraryResourcesSerializer.deserialize(context, opt(content,
                                                                                      FDependenciesModel.direct)),
                                 _libraryResourcesSerializer.deserialize(context, opt(content,
                                                                                      FDependenciesModel.transitive)),
                                 _libraryResourcesSerializer.deserialize(context, opt(content,
                                                                                      FDependenciesModel.optional)));
  }
}
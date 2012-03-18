
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
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.*;

/**
 * @author yan@pongasoft.com
 */
public class LVROverviewModelSerializer implements Serializer<LVROverviewModel, Object>
{
  public static class FLVROverviewModel
  {
    public static final String library = "l";
    public static final String packages = "packages";
  }

  private final Serializer<LibraryVersionResource, Object> _lvrSerializer;
  private final CollectionSerializer<LVROverviewModel.Package, LVROverviewResource> _packagesSerializer;

  /**
   * Constructor
   */
  public LVROverviewModelSerializer(Serializer<LibraryVersionResource, Object> lvrSerializer,
                                    Serializer<LVROverviewModel.Package, LVROverviewResource> packageSerializer)
  {
    _lvrSerializer = lvrSerializer;
    _packagesSerializer = new CollectionSerializer<LVROverviewModel.Package, LVROverviewResource>(packageSerializer);
  }

  public Object serialize(LVROverviewModel lib) throws SerializerException
  {
    if(lib == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();
    putOnce(content, FLVROverviewModel.library, _lvrSerializer.serialize(lib.getResource().getLibraryVersionResource()));
    putOnce(content, FLVROverviewModel.packages, _packagesSerializer.serialize(lib.getPackages()));
    return content;
  }

  public LVROverviewModel deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    LibraryVersionResource libraryVersionResource;
    libraryVersionResource =
      _lvrSerializer.deserialize(context, req(content, FLVROverviewModel.library));

    LVROverviewResource lvro = new LVROverviewResource(libraryVersionResource);

    return new LVROverviewModel(lvro,
                                _packagesSerializer.deserialize(lvro, opt(content,
                                                                          FLVROverviewModel.packages)));
  }
}
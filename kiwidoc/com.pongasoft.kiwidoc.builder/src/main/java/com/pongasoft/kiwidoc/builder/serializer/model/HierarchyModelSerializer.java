
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
import com.pongasoft.kiwidoc.model.HierarchyModel;
import com.pongasoft.kiwidoc.model.ParentHierarchyModel;
import com.pongasoft.kiwidoc.model.resource.HierarchyResource;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.*;

/**
 * @author yan@pongasoft.com
 */
public class HierarchyModelSerializer implements Serializer<HierarchyModel, Object>
{
  private final Serializer<LibraryVersionResource, Object> _libraryResourceSerializer;
  private final CollectionSerializer<ParentHierarchyModel, Object> _parentHierarchiesSerializer;

  public static class FHierarchyModel
  {
    public static final String library = "l";
    public static final String parents = "p";
  }

  /**
   * Constructor
   */
  public HierarchyModelSerializer(Serializer<LibraryVersionResource, Object> libraryResourceSerializer,
                                  Serializer<ParentHierarchyModel, Object> parentHierarchySerializer)
  {
    _libraryResourceSerializer = libraryResourceSerializer;
    _parentHierarchiesSerializer = new CollectionSerializer<ParentHierarchyModel, Object>(parentHierarchySerializer);
  }

  public Object serialize(HierarchyModel hierarchy) throws SerializerException
  {
    if(hierarchy == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();
    putOnce(content, FHierarchyModel.library, _libraryResourceSerializer.serialize(hierarchy.getResource().getParent()));
    putOnce(content, FHierarchyModel.parents, _parentHierarchiesSerializer.serialize(hierarchy.getParentHierarchy()));
    return content;
  }

  public HierarchyModel deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    LibraryVersionResource libraryVersionResource;
    libraryVersionResource =
      _libraryResourceSerializer.deserialize(context, req(content, FHierarchyModel.library));

    return new HierarchyModel(new HierarchyResource(libraryVersionResource),
                              _parentHierarchiesSerializer.deserialize(context, opt(content,
                                                                                    FHierarchyModel.parents)));
  }
}
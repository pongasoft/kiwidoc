
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
import com.pongasoft.kiwidoc.model.ParentHierarchyModel;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.*;

/**
 * @author yan@pongasoft.com
 */
public class ParentHierarchyModelSerializer implements Serializer<ParentHierarchyModel, Object>
{
  private final Serializer<ClassDefinitionModel, Object> _classDefinitionSerializer;
  private final CollectionSerializer<ClassDefinitionModel, Object> _classDefinitionsSerializer;

  public static class FParentHierarchyModel
  {
    public static final String classDefinition = "c";
    public static final String parents = "p";
  }

  /**
   * Constructor
   */
  public ParentHierarchyModelSerializer(Serializer<ClassDefinitionModel, Object> classDefinitionSerializer)
  {
    _classDefinitionSerializer = classDefinitionSerializer;
    _classDefinitionsSerializer = new CollectionSerializer<ClassDefinitionModel, Object>(classDefinitionSerializer);
  }

  public Object serialize(ParentHierarchyModel parentHierarchy) throws SerializerException
  {
    if(parentHierarchy == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();
    putOnce(content, FParentHierarchyModel.classDefinition, _classDefinitionSerializer.serialize(parentHierarchy.getClassDefinition()));
    putOnce(content, FParentHierarchyModel.parents, _classDefinitionsSerializer.serialize(parentHierarchy.getParents()));
    return content;
  }

  public ParentHierarchyModel deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    return new ParentHierarchyModel(_classDefinitionSerializer.deserialize(context, req(content,
                                                                                        FParentHierarchyModel.classDefinition)),
                                    _classDefinitionsSerializer.deserialize(context, opt(content,
                                                                                         FParentHierarchyModel.parents)));
  }
}

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
import com.pongasoft.kiwidoc.builder.serializer.SerializerUtils;
import com.pongasoft.kiwidoc.model.AnnotationModel;
import com.pongasoft.kiwidoc.model.AnnotationsModel;
import com.pongasoft.kiwidoc.model.ClassEntryModel;
import com.pongasoft.kiwidoc.model.DocModel;
import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.*;

/**
 * @author yan@pongasoft.com
 */
public class ClassEntryModelSerializer implements Serializer<ClassEntryModel, Resource>
{
  private final CollectionSerializer<AnnotationModel, Object> _annotationsSerializer;
  private final Serializer<DocModel, Resource> _docSerializer;

  public static class FClassModelEntry
  {
    public static final String access = "a";
    public static final String name = "n";
    public static final String annotations = "w";
    public static final String doc = "d";
  }

  /**
   * Constructor
   */
  public ClassEntryModelSerializer(Serializer<AnnotationModel, Object> annotationSerializer,
                                   Serializer<DocModel, Resource> docSerializer)
  {
    _annotationsSerializer = new CollectionSerializer<AnnotationModel, Object>(annotationSerializer);
    _docSerializer = docSerializer;
  }

  public Object serialize(ClassEntryModel classEntry) throws SerializerException
  {
    if(classEntry == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();
    putOnce(content, FClassModelEntry.access, classEntry.getAccess());
    putOnce(content, FClassModelEntry.name, classEntry.getName());
    putOnce(content, FClassModelEntry.doc, _docSerializer.serialize(classEntry.getDoc()));
    putOnce(content, FClassModelEntry.annotations, _annotationsSerializer.serialize(classEntry.getAnnotationsModel().getAnnotations()));
    return content;
  }

  public ClassEntryModel deserialize(Resource context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    @SuppressWarnings("unchecked")
    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

      return new ClassEntryModel(SerializerUtils.<Integer>req(content, FClassModelEntry.access),
                                 deserializeName(content),
                                 _docSerializer.deserialize(context, opt(content, FClassModelEntry.doc)),
                                 new AnnotationsModel(_annotationsSerializer.deserialize(context, opt(
                                   content,
                                   FClassModelEntry.annotations))));
  }

  protected String deserializeName(Map<String, Object> content) throws SerializerException
  {
    return req(content, FClassModelEntry.name);
  }
}

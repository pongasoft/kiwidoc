
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
import com.pongasoft.kiwidoc.model.ClassDefinitionModel;
import com.pongasoft.kiwidoc.model.ClassModel;
import com.pongasoft.kiwidoc.model.FieldModel;
import com.pongasoft.kiwidoc.model.MethodModel;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.type.GenericType;
import com.pongasoft.kiwidoc.model.type.GenericTypeVariables;
import com.pongasoft.util.core.enums.EnumCodec;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.*;

/**
 * @author yan@pongasoft.com
 */
public class ClassModelSerializer implements Serializer<ClassModel, Object>
{
  public static class FClassModel
  {
    public static final String library = "l";
    public static final String classKind= "c";
    public static final String genericTypeVariables = "g";
    public static final String interfaces = "i";
    public static final String superclass = "s";
    public static final String methods = "m";
    public static final String fields = "f";
    public static final String outerClass = "o";
    public static final String innerClasses = "r";
    public static final String allLibrarySubclasses = "u";
  }

  private final ClassEntryModelSerializer  _classEntrySerializer;
  private final Serializer<LibraryVersionResource, Object> _libraryResourceSerializer;
  private final Serializer<GenericType, Object> _typeSerializer;
  private final Serializer<ClassDefinitionModel, Object> _classDefinitionSerializer;
  private final CollectionSerializer<GenericType, Object> _typesSerializer;
  private final Serializer<GenericTypeVariables, Object> _gtvSerializer;
  private final CollectionSerializer<MethodModel, ClassResource> _methodsSerializer;
  private final CollectionSerializer<FieldModel, ClassResource> _fieldsSerializer;
  private final CollectionSerializer<ClassDefinitionModel, Object> _classDefinitionsSerializer;

  /**
   * Constructor
   */
  public ClassModelSerializer(ClassEntryModelSerializer classEntrySerializer,
                              Serializer<LibraryVersionResource, Object> libraryResourceSerializer,
                              Serializer<GenericType, Object> typeSerializer,
                              Serializer<GenericTypeVariables, Object> gtvSerializer,
                              Serializer<MethodModel, ClassResource> methodSerializer,
                              Serializer<FieldModel, ClassResource> fieldSerializer,
                              Serializer<ClassDefinitionModel, Object> classDefinitionSerializer)
  {
    _classEntrySerializer = classEntrySerializer;
    _libraryResourceSerializer = libraryResourceSerializer;
    _typeSerializer = typeSerializer;
    _classDefinitionSerializer = classDefinitionSerializer;
    _typesSerializer = new CollectionSerializer<GenericType, Object>(typeSerializer);
    _gtvSerializer = gtvSerializer;
    _methodsSerializer = new CollectionSerializer<MethodModel, ClassResource>(methodSerializer);
    _fieldsSerializer = new CollectionSerializer<FieldModel, ClassResource>(fieldSerializer);
    _classDefinitionsSerializer = new CollectionSerializer<ClassDefinitionModel, Object>(classDefinitionSerializer);
  }

  public Object serialize(ClassModel classModel) throws SerializerException
  {
    if(classModel == null)
      return null;

    Map<String, Object> content = new HashMap<String, Object>();
    putAllOnce((Map<String, Object>) _classEntrySerializer.serialize(classModel), content);
    putOnce(content, FClassModel.library, _libraryResourceSerializer.serialize(classModel.getResource().getLibraryVersionResource()));
    putOnce(content, FClassModel.genericTypeVariables,
            _gtvSerializer.serialize(classModel.getGenericTypeVariables()));
    putOnce(content, FClassModel.classKind, EnumCodec.INSTANCE.encode(classModel.getClassKind()));
    putOnce(content, FClassModel.superclass, _typeSerializer.serialize(classModel.getSuperClass()));
    putOnce(content, FClassModel.interfaces, _typesSerializer.serialize(classModel.getInterfaces()));
    putOnce(content, FClassModel.methods, _methodsSerializer.serialize(classModel.getAllMethods()));
    putOnce(content, FClassModel.fields, _fieldsSerializer.serialize(classModel.getAllFields()));
    putOnce(content, FClassModel.outerClass, _classDefinitionSerializer.serialize(classModel.getOuterClass()));
    putOnce(content, FClassModel.innerClasses, _classDefinitionsSerializer.serialize(classModel.getInnerClasses().values()));
    putOnce(content, FClassModel.allLibrarySubclasses, _classDefinitionsSerializer.serialize(classModel.getAllLibrarySubclasses()));

    return content;
  }

  public ClassModel deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    LibraryVersionResource libraryVersionResource;
    libraryVersionResource =
      _libraryResourceSerializer.deserialize(context, req(content, FClassModel.library));

    ClassResource classResource = new ClassResource(libraryVersionResource,
                                                    _classEntrySerializer.deserializeName(content));

    return new ClassModel(_classEntrySerializer.deserialize(classResource, content),
                          libraryVersionResource,
                          EnumCodec.INSTANCE.decode(ClassModel.ClassKind.class, SerializerUtils.<String>opt(content, FClassModel.classKind)),
                          _gtvSerializer.deserialize(classResource, opt(content,
                                                                        FClassModel.genericTypeVariables)),
                          _typeSerializer.deserialize(classResource, opt(content,
                                                                         FClassModel.superclass)),
                          _typesSerializer.deserialize(classResource, opt(content,
                                                                          FClassModel.interfaces)),
                          _methodsSerializer.deserialize(classResource, opt(content,
                                                                            FClassModel.methods)),
                          _fieldsSerializer.deserialize(classResource, opt(content,
                                                                           FClassModel.fields)),
                          _classDefinitionSerializer.deserialize(classResource, opt(content,
                                                                                    FClassModel.outerClass)),
                          _classDefinitionsSerializer.deserialize(classResource, opt(content,
                                                                                     FClassModel.innerClasses)),
                          _classDefinitionsSerializer.deserialize(classResource, opt(content,
                                                                                     FClassModel.allLibrarySubclasses)));
  }
}


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

import com.pongasoft.kiwidoc.builder.serializer.EnumKindSerializer;
import com.pongasoft.kiwidoc.builder.serializer.EnumSerializer;
import com.pongasoft.kiwidoc.builder.serializer.Serializer;
import com.pongasoft.kiwidoc.builder.serializer.SerializerException;
import com.pongasoft.kiwidoc.builder.serializer.StringSerializer;
import com.pongasoft.kiwidoc.builder.serializer.annotation.AnnotationValueSerializer;
import com.pongasoft.kiwidoc.builder.serializer.tag.InlineTagSerializer;
import com.pongasoft.kiwidoc.builder.serializer.tag.TagSerializer;
import com.pongasoft.kiwidoc.builder.serializer.type.GenericTypeVariablesSerializer;
import com.pongasoft.kiwidoc.builder.serializer.type.TypeSerializer;
import com.pongasoft.kiwidoc.model.BuiltFrom;
import com.pongasoft.kiwidoc.model.Model;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;

import java.util.EnumMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.opt;

/**
 * @author yan@pongasoft.com
 */
public class ModelSerializer extends EnumKindSerializer<Model.Kind, Model<?>, Object>
{
  public static final String CONTENT_VERSION = "1.0.0";

  public static class FModel
  {
    public static final String contentVersion = "V";
  }

  /**
   * Constructor
   */
  public ModelSerializer()
  {
    super(Model.Kind.class, doBuildMap());
  }

  @Override
  protected Map<String, Object> postSerialize(Map<String, Object> content) throws SerializerException
  {
    content.put(FModel.contentVersion, CONTENT_VERSION);
    return content;
  }

  @Override
  protected Map<String, Object> preDeserialize(Map<String, Object> content) throws SerializerException
  {
    if(!CONTENT_VERSION.equals(opt(content, FModel.contentVersion)))
      throw new SerializerException("unsupported version: " +
                                    opt(content, FModel.contentVersion));

    return super.preDeserialize(content);
  }

  @SuppressWarnings("unchecked")
  private static EnumMap<Model.Kind, Serializer<? extends Model<?>, Object>> doBuildMap()
  {
    EnumMap<Model.Kind, Serializer<? extends Model<?>, Object>> res =
      new EnumMap<Model.Kind, Serializer<? extends Model<?>, Object>>(Model.Kind.class);

    Serializer<LibraryVersionResource, Object> libraryVersionResourceSerializer =
      new LibraryVersionResourceSerializer();
    Serializer<String, Object> stringSerializer = new StringSerializer();
    TypeSerializer typeSerializer = new TypeSerializer();
    ClassDefinitionModelSerializer classDefinitionSerializer =
      new ClassDefinitionModelSerializer(typeSerializer);
    GenericTypeVariablesSerializer gtvSerializer = new GenericTypeVariablesSerializer();
    AnnotationValueSerializer avs = new AnnotationValueSerializer();
    DocModelSerializer docSerializer = new DocModelSerializer(new InlineTagSerializer(),
                                                              new TagSerializer());

    DependenciesModelSerializer dependenciesModelSerializer =
      new DependenciesModelSerializer(libraryVersionResourceSerializer);
    
    res.put(Model.Kind.LIBRARY_VERSION, new LibraryVersionModelSerializer(libraryVersionResourceSerializer,
                                                                          dependenciesModelSerializer,
                                                                          new EnumSerializer<BuiltFrom, Object>(BuiltFrom.class),
                                                                          docSerializer,
                                                                          new SimplePackageModelSerializer(stringSerializer),
                                                                          new OSGiModelSerializer()));
    res.put(Model.Kind.MANIFEST, new ManifestModelSerializer(libraryVersionResourceSerializer));
    res.put(Model.Kind.PACKAGE, new PackageModelSerializer(libraryVersionResourceSerializer,
                                                           classDefinitionSerializer,
                                                           docSerializer));

    ClassEntryModelSerializer classEntrySerializer =
      new ClassEntryModelSerializer(new AnnotationModelSerializer(avs.getAnnotationsSerializer()),
                                    docSerializer);

    ParameterModelSerializer parameterModelSerializer =
      new ParameterModelSerializer(typeSerializer,
                                   new AnnotationModelSerializer(avs.getAnnotationsSerializer()));

    res.put(Model.Kind.CLASS, new ClassModelSerializer(classEntrySerializer,
                                                       libraryVersionResourceSerializer,
                                                       typeSerializer,
                                                       gtvSerializer,
                                                       new MethodModelSerializer(classEntrySerializer,
                                                                                 gtvSerializer,
                                                                                 typeSerializer,
                                                                                 parameterModelSerializer,
                                                                                 avs),
                                                       new FieldModelSerializer(classEntrySerializer, 
                                                                                typeSerializer),
                                                       classDefinitionSerializer));
    res.put(Model.Kind.HIERARCHY, new HierarchyModelSerializer(libraryVersionResourceSerializer,
                                                               new ParentHierarchyModelSerializer(classDefinitionSerializer)));

    LVROClassSerializer lvroc = new LVROClassSerializer(new LVROMethodSerializer(),
                                                       new LVROFieldSerializer());

    LVROPackageSerializer lvrop = new LVROPackageSerializer(lvroc);

    res.put(Model.Kind.OVERVIEW, new LVROverviewModelSerializer(libraryVersionResourceSerializer, lvrop));
    
    return res;
  }

}

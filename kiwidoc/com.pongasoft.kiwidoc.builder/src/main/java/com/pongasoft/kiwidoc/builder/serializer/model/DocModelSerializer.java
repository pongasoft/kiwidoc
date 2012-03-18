
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
import com.pongasoft.kiwidoc.model.DocModel;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.kiwidoc.model.tag.InlineTag;
import com.pongasoft.kiwidoc.model.tag.MainTag;
import com.pongasoft.kiwidoc.model.tag.Tag;

import java.util.HashMap;
import java.util.Map;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.opt;
import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.putOnce;

/**
 * @author yan@pongasoft.com
 */
public class DocModelSerializer implements Serializer<DocModel, Resource>
{
  public static class FDocModel
  {
    public static final String doc = "d";
    public static final String tags = "t";
  }

  private final CollectionSerializer<InlineTag, Object> _inlineTagsSerializer;
  private final CollectionSerializer<Tag, Resource> _tagsSerializer;

  /**
   * Constructor
   */
  public DocModelSerializer(Serializer<InlineTag, Object> inlineTagSerializer,
                            Serializer<Tag, Resource> tagSerializer)
  {
    _inlineTagsSerializer = new CollectionSerializer<InlineTag, Object>(inlineTagSerializer);
    _tagsSerializer = new CollectionSerializer<Tag, Resource>(tagSerializer);
  }

  public Object serialize(DocModel docModel) throws SerializerException
  {
    if(docModel == null)
      return null;

    if(docModel.hasDoc())
    {
      Map<String, Object> content = new HashMap<String, Object>();
      putOnce(content, FDocModel.doc, _inlineTagsSerializer.serialize(docModel.getDoc().getInlineTags()));
      putOnce(content, FDocModel.tags, _tagsSerializer.serialize(docModel.getTags()));
      return content;
    }
    else
    {
      return null;
    }
  }

  public DocModel deserialize(Resource resource, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    return new DocModel(new MainTag(resource,
                                    _inlineTagsSerializer.deserialize(resource, opt(content,
                                                                                    FDocModel.doc))),
                        _tagsSerializer.deserialize(resource, opt(content, FDocModel.tags)));
  }
}

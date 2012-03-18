
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

package com.pongasoft.kiwidoc.index.impl.keyword.impl;

import com.pongasoft.kiwidoc.index.impl.ResourceEncoder;
import com.pongasoft.kiwidoc.model.ManifestModel;
import com.pongasoft.util.core.annotations.ObjectInitializer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author yan@pongasoft.com
 */
public class ManifestModelDocumentFactory extends AbstractDocumentFactory<ManifestModel>
{
  /**
   * Constructor
   */
  @ObjectInitializer
  public ManifestModelDocumentFactory()
  {
  }

  public ManifestModelDocumentFactory(ResourceEncoder<String> resourceEncoder)
  {
    super(resourceEncoder);
  }

  /**
   * @param model the model to process
   * @return the document for the given model
   */
  public Document createDocument(ManifestModel model)
  {
    Document doc = doCreateDocument(model);

    // library version to be able to unindex a library
    addFieldResource(doc, LIBRARY_VERSION_FIELD, model.getResource().getLibraryVersionResource());

    // content
    doc.add(new Field(BODY_FIELD,
                      buildBody(model),
                      Field.Store.NO,
                      Field.Index.ANALYZED));
    return doc;
  }

  private String buildBody(ManifestModel model)
  {
    StringBuilder sb = new StringBuilder();

    Manifest manifest = model.getManifest();

    addAttributes(sb, manifest.getMainAttributes());

    Map<String, Attributes> entries = manifest.getEntries();
    for(Map.Entry<String, Attributes> entry : entries.entrySet())
    {
      sb.append(entry.getKey());
      sb.append(FIELD_SEPARATOR);
      addAttributes(sb, entry.getValue());
    }

    return sb.toString();
  }

  private void addAttributes(StringBuilder sb, Attributes attributes)
  {
    for(Map.Entry<Object, Object> entry : attributes.entrySet())
    {
      sb.append(entry.getKey()).append(":").append(entry.getValue());
      sb.append(FIELD_SEPARATOR);
    }
  }
}
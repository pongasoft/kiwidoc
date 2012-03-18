
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

import com.pongasoft.kiwidoc.model.Model;
import com.pongasoft.kiwidoc.model.DocModel;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.kiwidoc.index.impl.ResourceEncoder;
import com.pongasoft.kiwidoc.index.impl.StringResourceEncoder;
import com.pongasoft.util.core.annotations.ObjectInitializer;
import com.pongasoft.util.core.annotations.FieldInitializer;
import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.TextExtractor;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * @author yan@pongasoft.com
 */
public abstract class AbstractDocumentFactory<M extends Model> implements DocumentFactory<M>
{
  private ResourceEncoder<String> _resourceEncoder = new StringResourceEncoder();

  /**
   * Constructor
   */
  @ObjectInitializer
  public AbstractDocumentFactory()
  {
  }

  public AbstractDocumentFactory(ResourceEncoder<String> resourceEncoder)
  {
    _resourceEncoder = resourceEncoder;
  }

  public ResourceEncoder<String> getResourceEncoder()
  {
    return _resourceEncoder;
  }

  @FieldInitializer
  public void setResourceEncoder(ResourceEncoder<String> resourceEncoder)
  {
    _resourceEncoder = resourceEncoder;
  }

  /**
   * Filters out the javadoc to remove html tags before indexing.
   */
  protected String filterDoc(DocModel docModel)
  {
    Source source = new Source(docModel.toString());
    return new TextExtractor(source).toString();
  }

  protected Document doCreateDocument(Model model)
  {
    return addFieldResource(new Document(), ID_FIELD, model.getResource());
  }

  protected Document addFieldResource(Document doc, String id, Resource resource)
  {
    return addStoredUnanalyzed(doc, id, getResourceEncoder().encodeResource(resource));
  }

  protected Document addStoredUnanalyzed(Document doc, String id, String value)
  {
    doc.add(new Field(id,
                      value,
                      Field.Store.YES,
                      Field.Index.NOT_ANALYZED));

    return doc;
  }
}
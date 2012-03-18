
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

package com.pongasoft.kiwidoc.model.tag;

import com.pongasoft.kiwidoc.model.resource.ResolvableResource;
import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.Collection;

/**
 * Represents a @see/@link tag.
 * 
 * @author yan@pongasoft.com
 */
public class SeeTag extends DocType
{
  private final LinkReference _linkReference;

  /**
   * Constructor
   */
  public SeeTag(Resource origin,
                String name,
                Collection<InlineTag> inlineTags,
                LinkReference linkReference)
  {
    super(origin, name, inlineTags);
    _linkReference = linkReference;
  }

  /**
   * @return the kind of this tag
   * @see Kind
   */
  public Kind getKind()
  {
    return Kind.SEE;
  }

  /**
   * Replaces the inheritDoc tag with the tag provided
   */
  public Tag inheritDoc(Tag inheritedDoc)
  {
    return new SeeTag(inheritedDoc.getOrigin(),
                      getName(),
                      doInheritDoc(inheritedDoc), 
                      getLinkReference());
  }

  public LinkReference getLinkReference()
  {
    return _linkReference;
  }

  /**
   * Collect the dependencies in the collection.
   *
   * @param dependencies to add the dependencies to it
   */
  @Override
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    super.collectDependencies(dependencies);
    _linkReference.collectDependencies(dependencies);
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append(getName());
    sb.append(' ');
    sb.append(getLinkReference().getRawReference());
    String text = getText();
    if(!("".equals(text)))
      sb.append(' ').append(text);
    return sb.toString();
  }
}

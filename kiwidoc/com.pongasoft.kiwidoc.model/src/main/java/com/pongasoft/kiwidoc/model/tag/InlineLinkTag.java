
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

import java.util.Collection;

/**
 * Represents a @see/@link tag.
 *
 * @author yan@pongasoft.com
 */
public class InlineLinkTag implements InlineTag
{
  private final String _text;
  private final LinkReference _linkReference;

  /**
   * Constructor
   */
  public InlineLinkTag(String text,
                       LinkReference linkReference)
  {
    _text = text;
    _linkReference = linkReference;
  }

  /**
   * @return the kind of this tag
   * @see Kind
   */
  public Kind getKind()
  {
    return Kind.LINK;
  }

  /**
   * @return the text
   */
  public String getText()
  {
    return _text;
  }

  /**
   * @return <code>true</code> if it corresponds to the inheritDoc tag from javadoc
   */
  public boolean isInheritDoc()
  {
    return false;
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
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    _linkReference.collectDependencies(dependencies);
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("{@link ");
    sb.append(getLinkReference().getRawReference());
    String text = getText();
    if(!("".equals(text)))
      sb.append(' ').append(text);
    sb.append('}');
    return sb.toString();
  }
}
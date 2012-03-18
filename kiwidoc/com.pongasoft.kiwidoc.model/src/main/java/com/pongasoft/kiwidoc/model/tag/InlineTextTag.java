
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
 * This tag is a pseudo tag and is returned in {@link Tag#getInlineTags()}.
 *
 * @author yan@pongasoft.com
 */
public class InlineTextTag implements InlineTag
{
  private final String _text;

  /**
   * Constructor
   */
  public InlineTextTag(String text)
  {
    _text = text;
  }

  /**
   * @return the text of the tag (unprocessed)
   */
  public String getText()
  {
    return _text;
  }

  /**
   * @return the kind of this tag
   * @see Kind
   */
  public Kind getKind()
  {
    return Kind.TEXT;
  }

  /**
   * @return <code>true</code> if it corresponds to the inheritDoc tag from javadoc
   */
  public boolean isInheritDoc()
  {
    return false;
  }

  /**
   * Collect the dependencies in the collection.
   *
   * @param dependencies to add the dependencies to it
   */
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    // nothing to do
  }

  @Override
  public String toString()
  {
    return _text;
  }
}

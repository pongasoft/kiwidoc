
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

import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.Collection;

/**
 * Represents a non specialized tag.
 *
 * @author yan@pongasoft.com
 */
public class MainTag extends DocType
{
  /**
   * Constructor
   */
  public MainTag(Resource origin, Collection<InlineTag> inlineTags)
  {
    super(origin, null, inlineTags);
  }

  /**
   * Replaces the inheritDoc tag with the tag provided
   */
  public Tag inheritDoc(Tag inheritedDoc)
  {
    return new MainTag(inheritedDoc.getOrigin(), doInheritDoc(inheritedDoc));
  }

  /**
   * @return <code>true</code> if this documentation is empty
   */
  public boolean isMissing()
  {
    return getInlineTags().isEmpty();
  }

  /**
   * @return the kind of this tag
   * @see Kind
   */
  public Kind getKind()
  {
    return Kind.MAIN;
  }
}
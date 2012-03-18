
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

import com.pongasoft.kiwidoc.model.Extensible;
import com.pongasoft.kiwidoc.model.resource.DependenciesCollector;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.util.core.enums.Value;

import java.util.Collections;
import java.util.Collection;

/**
 * @author yan@pongasoft.com
 */
public interface Tag extends Extensible<Tag.Kind>, DependenciesCollector
{
  public static final Collection<Tag> NO_TAGS = Collections.emptyList();

  public static enum Kind
  {
    @Value("M") MAIN, // the main description
    @Value("O") ORDINARY,
    @Value("S") SEE,
    @Value("P") PARAM,
    @Value("E") THROWS
  }

  /**
   * @return the name of the tag (ex: @see)
   */
  String getName();

  /**
   * @return the text of the tag (unprocessed)
   */
  String getText();

  /**
   * @return the text split in tags of kinds {@link InlineTag.Kind} */
  Collection<InlineTag> getInlineTags();

  /**
   * @return the origin of the tag (class, method...) (may be <code>null</code> if not initialized)
   */
  Resource getOrigin();

  /**
   * @return <code>true</code> if this tag contains the inheritDoc tag
   */
  boolean containsInheritDoc();

  /**
   * Replaces the inheritDoc tag with the tag provided
   */
  Tag inheritDoc(Tag inheritedDoc);
}
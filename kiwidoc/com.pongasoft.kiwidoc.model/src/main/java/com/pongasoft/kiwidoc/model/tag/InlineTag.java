
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
import com.pongasoft.util.core.enums.Value;
import com.pongasoft.util.core.enums.IsDefault;

import java.util.List;
import java.util.Collections;

/**
 * Represents an inline tag.
 * 
 * @author yan@pongasoft.com
 */
public interface InlineTag extends Extensible<InlineTag.Kind>, DependenciesCollector
{
  public static final List<InlineTag> NO_INLINE_TAGS = Collections.emptyList();

  public static enum Kind
  {
    @Value("O") ORDINARY,
    @Value("L") LINK,
    @IsDefault @Value("T") TEXT
  }

  /**
   * @return the text
   */
  String getText();

  /**
   * @return <code>true</code> if it corresponds to the inheritDoc tag from javadoc
   */
  boolean isInheritDoc();
}
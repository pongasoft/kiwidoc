
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
 * @author yan@pongasoft.com
 */
public class InlineOrdinaryTag implements InlineTag
{
  private final String _name;
  private final String _text;

  /**
   * Constructor
   */
  public InlineOrdinaryTag(String name, String text)
  {
    _name = name;
    _text = text;
  }

  /**
   * @return the kind of this tag
   * @see Kind
   */
  public Kind getKind()
  {
    return Kind.ORDINARY;
  }

  /**
   * @return the text
   */
  public String getText()
  {
    return _text;
  }

  public String getName()
  {
    return _name;
  }

  /**
   * @return <code>true</code> if it corresponds to the inheritDoc tag from javadoc
   */
  public boolean isInheritDoc()
  {
    return "@inheritDoc".equals(getName());
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
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append('{');
    sb.append(getName());
    if(!"".equals(getText()))
      sb.append(' ').append(getText());
    sb.append('}');
    return sb.toString();
  }
}

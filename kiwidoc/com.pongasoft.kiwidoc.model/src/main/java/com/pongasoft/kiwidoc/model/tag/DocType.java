
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
import com.pongasoft.kiwidoc.model.resource.DependenciesCollector;
import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.Collection;
import java.util.ArrayList;

/**
 * @author yan@pongasoft.com
 */
public abstract class DocType implements DependenciesCollector, Tag
{
  private final String _name;
  private final Collection<InlineTag> _inlineTags;
  // origin of the tag
  private final Resource _origin;

  /**
   * Constructor
   */
  public DocType(Resource origin, String name, Collection<InlineTag> inlineTags)
  {
    _origin = origin;
    _name = name;
    _inlineTags = inlineTags;
  }

  /**
   * @return the origin of the tag (class, method, field...) 
   */
  public Resource getOrigin()
  {
    return _origin;
  }

  /**
   * @return the name of the tag (ex: @see)
   */
  public String getName()
  {
    return _name;
  }

  /**
   * @return the text of the tag (unprocessed)
   */
  public String getText()
  {
    StringBuilder sb = new StringBuilder();
    for(InlineTag tag : getInlineTags())
    {
      sb.append(tag);
    }
    return sb.toString();
  }

  /**
   * @return the text split in tags of kinds {@link InlineTag.Kind} */
  public Collection<InlineTag> getInlineTags()
  {
    return _inlineTags;
  }

  /**
   * @return <code>true</code> if this tag contains the inheritDoc tag
   */
  public boolean containsInheritDoc()
  {
    for(InlineTag inlineTag : getInlineTags())
    {
      if(inlineTag.isInheritDoc())
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Replaces the inheritDoc tag with the inline tags provided
   */
  protected Collection<InlineTag> doInheritDoc(Tag inheritedDoc)
  {
    Collection<InlineTag> res = new ArrayList<InlineTag>();
    for(InlineTag inlineTag : getInlineTags())
    {
      if(inlineTag.isInheritDoc())
        res.addAll(inheritedDoc.getInlineTags());
      else
        res.add(inlineTag);
    }
    return res;
  }

  /**
   * Collect the dependencies in the collection.
   *
   * @param dependencies to add the dependencies to it
   */
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    for(InlineTag inlineTag : getInlineTags())
    {
      inlineTag.collectDependencies(dependencies);
    }
  }

  @Override
  public String toString()
  {
    if(getName() != null)
      return getName() + ' ' + getText();
    else
      return getText();
  }
}


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

package com.pongasoft.kiwidoc.model;

import com.pongasoft.kiwidoc.model.resource.DependenciesCollector;
import com.pongasoft.kiwidoc.model.resource.ResolvableResource;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.kiwidoc.model.tag.InlineTag;
import com.pongasoft.kiwidoc.model.tag.MainTag;
import com.pongasoft.kiwidoc.model.tag.Tag;
import com.pongasoft.kiwidoc.model.tag.ParamTag;
import com.pongasoft.kiwidoc.model.tag.OrdinaryTag;
import com.pongasoft.kiwidoc.model.tag.ThrowsTag;
import com.pongasoft.kiwidoc.model.type.Type;
import com.pongasoft.util.core.misc.Utils;

import java.util.Collection;
import java.util.Collections;

/**
 * @author yan@pongasoft.com
 */
public class DocModel implements DependenciesCollector
{
  private final MainTag _doc;
  private final Collection<Tag> _tags;

  /**
   * Constructor
   */
  public DocModel(MainTag doc, Collection<Tag> tags)
  {
    if(doc == null)
      throw new IllegalArgumentException("doc should not be null");
    if(tags == null)
      throw new IllegalArgumentException("tags should not be null");

    _doc = doc;
    _tags = Collections.unmodifiableCollection(tags);
  }

  /**
   * Constructor
   */
  public DocModel(Resource origin, Collection<Tag> tags)
  {
    if(origin == null)
      throw new IllegalArgumentException("origin should not be null");
    if(tags == null)
      throw new IllegalArgumentException("tags should not be null");

    _doc = new MainTag(origin, InlineTag.NO_INLINE_TAGS);
    _tags = Collections.unmodifiableCollection(tags);
  }

  public MainTag getDoc()
  {
    return _doc;
  }

  public boolean hasDoc()
  {
    return !_doc.isMissing() || _tags.size() > 0;
  }

  public Collection<Tag> getTags()
  {
    return _tags;
  }

  /**
   * @return <code>true</code> if this tag contains the inheritDoc tag
   */
  public boolean containsInheritDoc()
  {
    if(_doc.containsInheritDoc())
      return true;

    for(Tag tag : _tags)
    {
      if(tag.containsInheritDoc())
        return true;
    }

    return false;
  }

  /**
   * @return a tag that matches this one (in kind and name)
   */
  public Tag findSimilarTag(Tag tag)
  {
    if(tag == null)
      return null;

    switch(tag.getKind())
    {
      case MAIN:
        return _doc;

      case PARAM:
        return findParamTag(((ParamTag) tag).getParameterName());

      case THROWS:
        return findThrowsTag(((ThrowsTag) tag).getExceptionType());

      default:
        for(Tag t : _tags)
        {
          if(t.getKind() == tag.getKind() && Utils.isEqual(t.getName(), tag.getName()))
            return t;
        }
        break;
    }

    return null;
  }

  /**
   * @return the param tag with the given (parameter) name or <code>null</code> if not defined
   */
  public ParamTag findParamTag(String name)
  {
    if(name == null)
      return null;

    for(Tag tag : _tags)
    {
      if(tag instanceof ParamTag)
      {
        ParamTag paramTag = (ParamTag) tag;
        if(name.equals(paramTag.getParameterName()))
          return paramTag;
      }
    }

    return null;
  }

  /**
   * @return the return tag or <code>null</code> if none defined
   */
  public OrdinaryTag findReturnTag()
  {
    for(Tag tag : _tags)
    {
      if(tag instanceof OrdinaryTag)
      {
        OrdinaryTag ordinaryTag = (OrdinaryTag) tag;
        if("@return".equals(ordinaryTag.getName()))
          return ordinaryTag;
      }
    }

    return null;
  }

  /**
   * @return the tag that corresponds to the given exception
   */
  public ThrowsTag findThrowsTag(Type exceptionType)
  {
    if(exceptionType == null)
      return null;

    for(Tag tag : _tags)
    {
      if(tag instanceof ThrowsTag)
      {
        ThrowsTag throwsTag = (ThrowsTag) tag;
        Type type = throwsTag.getExceptionType();
        if(type != null && type.toErasureDescriptor().equals(exceptionType.toErasureDescriptor()))
          return throwsTag;
      }
    }

    return null;
  }

  /**
   * Collect the dependencies in the collection.
   *
   * @param dependencies to add the dependencies to it
   */
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    _doc.collectDependencies(dependencies);
    for(Tag tag : _tags)
    {
      tag.collectDependencies(dependencies);
    }
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append(_doc);
    for(Tag tag : _tags)
    {
      sb.append('\n').append(tag);
    }
    return sb.toString();
  }
}


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

import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.tag.MainTag;
import com.pongasoft.kiwidoc.model.tag.Tag;
import com.pongasoft.kiwidoc.model.tag.OrdinaryTag;
import com.pongasoft.kiwidoc.model.tag.InlineTag;
import com.pongasoft.kiwidoc.model.tag.ParamTag;
import com.pongasoft.kiwidoc.model.tag.ThrowsTag;
import com.pongasoft.kiwidoc.model.type.Type;

import java.util.Collection;
import java.util.ArrayList;

/**
 * @author yan@pongasoft.com
 */
class InheritDocHandler
{
  private final InheritanceModel _inheritanceModel;
  private final ClassResource _parent;
  private final MethodModel _method;

  private Collection<MethodModel> _inheritedMethods = null;

  /**
   * Constructor
   */
  public InheritDocHandler(InheritanceModel inheritanceModel,
                           ClassResource parent,
                           MethodModel method)
  {
    _inheritanceModel = inheritanceModel;
    _parent = parent;
    _method = method;
  }

  public MethodModel inheritDoc()
  {
    // we do not handle constructors or static methods...
    if(_method.isConstructor() || 
       _method.is(BaseEntryModel.Access.STATIC) ||
       _method.is(BaseEntryModel.Access.PRIVATE))
      return _method;


    DocModel doc = _method.getDoc();

    // first we handle the {@inheritDoc} javadoc tag by replacing it...
    doc = handleInheritDoc(doc);

    // shortcut... no inherited methods... no need to continue really
    if(_inheritedMethods != null && _inheritedMethods.isEmpty())
      return _method;

    if(doc == null)
      doc = new DocModel(_parent, Tag.NO_TAGS);

    // second we handle automatic copying of missing text
    doc = handleMissingText(doc);

    if(!doc.hasDoc())
      return _method;
    
    if(doc != _method.getDoc())
      return new MethodModel(_method.getAccess(),
                             _method.getResource(), 
                             _method.getName(),
                             doc,
                             _method.getAnnotationsModel(),
                             _method.getGenericTypeVariables(),
                             _method.getParametersModel(),
                             _method.getReturnType(),
                             _method.getExceptions(),
                             _method.getAnnotationDefaultValue());

    return _method;
  }

  /*
   * first we handle the {@inheritDoc} javadoc tag by replacing it...
   */
  private DocModel handleInheritDoc(DocModel doc)
  {
    boolean changed = false;

    if(doc != null && doc.containsInheritDoc())
    {
      if(!getInheritedMethods().isEmpty())
      {
        MainTag mainTag = (MainTag) doInheritDoc(doc.getDoc());
        if(mainTag != doc.getDoc())
        {
          changed = true;
        }

        Collection<Tag> newTags = new ArrayList<Tag>();

        for(Tag tag : doc.getTags())
        {
          Tag newTag = doInheritDoc(tag);
          newTags.add(newTag);
          if(newTag != tag)
          {
            changed = true;
          }
        }

        if(changed)
          doc = new DocModel(mainTag, newTags);
      }
    }

    return doc;
  }

  /**
   * second we handle automatic copying of missing text
   */
  private DocModel handleMissingText(DocModel doc)
  {
    MainTag mainTag = doc.getDoc();

    // missing main description
    if(mainTag.isMissing())
    {
      mainTag = (MainTag) doMissingDoc(mainTag);
      if(mainTag == null)
        mainTag = doc.getDoc();
    }

    Collection<Tag> newTags = new ArrayList<Tag>(doc.getTags());
    
    // missing return type
    doMissingDoc(newTags,
                 doc.findReturnTag(),
                 new OrdinaryTag(null, "@return", InlineTag.NO_INLINE_TAGS));

    // missing parameter
    for(ParameterModel parameterModel : _method.getParameters())
    {
      doMissingDoc(newTags,
                   doc.findParamTag(parameterModel.getName()),
                   new ParamTag(null, "@param", InlineTag.NO_INLINE_TAGS, parameterModel.getName(), false));
    }

    // missing exception
    for(Type exceptionType : _method.getExceptions())
    {
      doMissingDoc(newTags,
                   doc.findThrowsTag(exceptionType),
                   new ThrowsTag(null, "@throws", InlineTag.NO_INLINE_TAGS, null, exceptionType));
    }

    if(newTags.size() != doc.getTags().size() || mainTag != doc.getDoc())
    {
      doc = new DocModel(mainTag, newTags);
    }

    return doc;
  }

  private Collection<MethodModel> getInheritedMethods()
  {
    if(_inheritedMethods == null)
    {
      _inheritedMethods = _inheritanceModel.findInheritedMethod(_method);
    }

    return _inheritedMethods;
  }

  private Tag doInheritDoc(Tag tag)
  {
    if(tag.containsInheritDoc())
    {
      for(MethodModel inheritedMethod : getInheritedMethods())
      {
        DocModel doc = inheritedMethod.getDoc();
        if(doc != null)
        {
          Tag similarTag = doc.findSimilarTag(tag);
          if(similarTag != null)
            return tag.inheritDoc(similarTag);
        }
      }
    }

    return tag;
  }

  private Tag doMissingDoc(Tag tag)
  {
    if(tag == null)
      return null;

    for(MethodModel inheritedMethod : getInheritedMethods())
    {
      DocModel doc = inheritedMethod.getDoc();
      if(doc != null)
      {
        Tag similarTag = doc.findSimilarTag(tag);
        if(similarTag != null)
          return similarTag;
      }
    }

    return null;
  }

  private boolean doMissingDoc(Collection<Tag> newTags, Tag tag, Tag similarTag)
  {
    if(tag != null)
      return false;
    
    Tag newTag = doMissingDoc(similarTag);
    if(newTag != null)
    {
      newTags.add(newTag);
      return true;
    }
    else
      return false;
  }
}


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
import com.pongasoft.kiwidoc.model.resource.ClassResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author yan@pongasoft.com
 */
public class AnnotationsModel implements DependenciesCollector
{
  public static final AnnotationsModel NO_ANNOTATIONS = 
    new AnnotationsModel(Collections.<AnnotationModel>emptyList());

  private final Collection<AnnotationModel> _annotations;

  /**
   * Constructor
   */
  public AnnotationsModel(Collection<AnnotationModel> annotations)
  {
    _annotations = annotations;
  }

  /**
   * @return <code>true</code> if all annotations are documented
   */
  public boolean containsOnlyPublicAnnotations()
  {
    for(AnnotationModel annotation : _annotations)
    {
      if(!annotation.isDocumented())
        return false;
    }

    return true;
  }

  /**
   * @return the annotation described by the class resource 
   */
  public AnnotationModel findAnnotation(ClassResource classResource)
  {
    for(AnnotationModel annotation : _annotations)
    {
      if(annotation.getType().isSameFqcn(classResource))
        return annotation;
    }

    return null;
  }

  /**
   * @return a model that has been filtered for public annotations (keep only the ones
   *         that are documented)
   */
  public AnnotationsModel toPublicAPI()
  {
    if(containsOnlyPublicAnnotations())
      return this;

    Collection<AnnotationModel> annotations = new ArrayList<AnnotationModel>();
    for(AnnotationModel annotation : _annotations)
    {
      if(annotation.isDocumented())
        annotations.add(annotation);
    }

    return new AnnotationsModel(annotations);
  }

  public Collection<AnnotationModel> getAnnotations()
  {
    return _annotations;
  }

  /**
   * Collect the dependencies in the collection.
   *
   * @param dependencies to add the dependencies to it
   */
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    for(AnnotationModel annotation : _annotations)
    {
      annotation.collectDependencies(dependencies);
    }
  }
}

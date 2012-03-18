
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

import java.util.List;
import java.util.Collections;
import java.util.Collection;

/**
 * @author yan@pongasoft.com
 */
public class ClassEntryModel extends BaseEntryModel implements DependenciesCollector
{
  public static final List<AnnotationModel> NO_ANNOTATIONS = Collections.emptyList();
  private static final ClassResource DEPRECATED = new ClassResource(Deprecated.class.getName());
  
  private final DocModel _doc;
  private final AnnotationsModel _annotations;

  /**
   * Constructor
   */
  public ClassEntryModel(int access,
                         String name,
                         DocModel doc,
                         AnnotationsModel annotations)
  {
    super(access, name);
    _doc = doc;
    _annotations = annotations;
  }

  public boolean containsOnlyPublicEntries()
  {
    return _annotations.containsOnlyPublicAnnotations();
  }

  public DocModel getDoc()
  {
    return _doc;
  }

  public AnnotationsModel getAnnotationsModel()
  {
    return _annotations;
  }

  public Collection<AnnotationModel> getAnnotations()
  {
    return _annotations.getAnnotations();
  }

  public boolean isDeprecated()
  {
    return _annotations.findAnnotation(DEPRECATED) != null;
  }

  protected ClassEntryModel toPubliAPI()
  {
    if(containsOnlyPublicEntries())
      return this;
    else
      return new ClassEntryModel(getAccess(),
                                 getName(),
                                 getDoc(), 
                                 _annotations.toPublicAPI());
  }

  /**
   * Collect the dependencies in the collection.
   *
   * @param dependencies to add the dependencies to it
   */
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    if(_doc != null)
    {
      _doc.collectDependencies(dependencies);
    }
    _annotations.collectDependencies(dependencies);
  }
}

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

import com.pongasoft.kiwidoc.model.annotation.Annotation;
import com.pongasoft.kiwidoc.model.annotation.AnnotationValue;
import com.pongasoft.kiwidoc.model.resource.DependenciesCollector;
import com.pongasoft.kiwidoc.model.resource.ResolvableResource;
import com.pongasoft.kiwidoc.model.type.GenericType;

import java.util.Collection;

/**
 * @author yan@pongasoft.com
 */
public class AnnotationModel implements DependenciesCollector
{
  private final Annotation _annotation;

  /**
   * Constructor
   */
  public AnnotationModel(Annotation annotation)
  {
    _annotation = annotation;
  }

  public GenericType getType()
  {
    return _annotation.getType();
  }

  public Annotation getAnnotation()
  {
    return _annotation;
  }

  public boolean isDocumented()
  {
    // TODO HIGH YP: need to handle this... (during resolve time...)
    return true;
  }

  public Collection<AnnotationValue> getAnnotationValues()
  {
    return _annotation.getAnnotationValues();
  }

  public AnnotationValue getAnnotationValue(String name)
  {
    return _annotation.getAnnotationValue(name);
  }

  /**
   * Collect the dependencies in the collection.
   *
   * @param dependencies to add the dependencies to it
   */
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    _annotation.collectDependencies(dependencies);
  }

  @Override
  public String toString()
  {
    return _annotation.toString();
  }
}

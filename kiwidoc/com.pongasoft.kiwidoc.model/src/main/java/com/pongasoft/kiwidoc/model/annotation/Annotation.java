
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

package com.pongasoft.kiwidoc.model.annotation;

import com.pongasoft.kiwidoc.model.type.GenericType;
import com.pongasoft.kiwidoc.model.resource.ResolvableResource;
import com.pongasoft.kiwidoc.model.resource.DependenciesCollector;

import java.util.Collection;
import java.util.Map;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author yan@pongasoft.com
 */
public class Annotation implements DependenciesCollector
{
  private static final Map<String, AnnotationValue> NO_ANNOTATION_VALUES = Collections.emptyMap();

  private final GenericType _type;
  private final Collection<AnnotationElement> _annotationElements;
  private final Map<String, AnnotationValue> _annotationValues;

  /**
   * Constructor
   */
  public Annotation(GenericType type, Collection<AnnotationElement> annotationElements)
  {
    _type = type;
    _annotationElements = Collections.unmodifiableList(new ArrayList<AnnotationElement>(
      annotationElements));

    Map<String, AnnotationValue> annotationValueMap = NO_ANNOTATION_VALUES;
    if(!annotationElements.isEmpty())
    {
      annotationValueMap = new HashMap<String, AnnotationValue>();
      for(AnnotationElement annotationElement : annotationElements)
      {
        annotationValueMap.put(annotationElement.getName(), annotationElement.getAnnotationValue());
      }
      annotationValueMap = Collections.unmodifiableMap(annotationValueMap);
    }
    _annotationValues = annotationValueMap;
  }

  public GenericType getType()
  {
    return _type;
  }

  public Collection<AnnotationElement> getAnnotationElements()
  {
    return _annotationElements;
  }

  public Collection<AnnotationValue> getAnnotationValues()
  {
    return _annotationValues.values();
  }

  public AnnotationValue getAnnotationValue(String name)
  {
    return _annotationValues.get(name);
  }

  /**
   * Collect the dependencies in the collection.
   *
   * @param dependencies to add the dependencies to it
   */
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    _type.collectDependencies(dependencies);
    for(AnnotationValue annotationValue : _annotationValues.values())
    {
      annotationValue.collectDependencies(dependencies);
    }
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("@").append(_type);
    sb.append('(');
    int i = 0;
    for(AnnotationElement annotationElement : _annotationElements)
    {
      if(i > 0)
        sb.append(", ");
      sb.append(annotationElement);
      i++;
    }
    sb.append(')');
    return sb.toString();
  }
}

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

import com.pongasoft.util.core.enums.Value;
import com.pongasoft.kiwidoc.model.Extensible;
import com.pongasoft.kiwidoc.model.resource.DependenciesCollector;

import java.util.Collection;
import java.util.Collections;

/**
 * @author yan@pongasoft.com
 */
public interface AnnotationValue extends Extensible<AnnotationValue.Kind>, DependenciesCollector
{
  public static final Collection<AnnotationValue> NO_ANNOTATION_VALUES = Collections.emptyList();
  
  public static enum Kind
  {
    @Value("P") PRIMITIVE,
    @Value("S") STRING,
    @Value("C") CLASS,
    @Value("E") ENUM,
    @Value("A") ANNOTATION,
    @Value("X") ARRAY
  }
}

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

import com.pongasoft.kiwidoc.model.type.Type;
import com.pongasoft.kiwidoc.model.resource.DependenciesCollector;
import com.pongasoft.kiwidoc.model.resource.ResolvableResource;

import java.util.Collection;

/**
 * Represents an argument/parameter in a method call.
 *
 * @author yan@pongasoft.com
 */
public class ParameterModel implements DependenciesCollector
{
  private final String _name;
  private final Type _type;
  private final AnnotationsModel _annotations;

  /**
   * Constructor
   */
  public ParameterModel(String name,
                        Type type,
                        AnnotationsModel annotations)
  {
    _name = name;
    _type = type;
    _annotations = annotations;
  }

  public String getName()
  {
    return _name;
  }

  public Type getType()
  {
    return _type;
  }

  public boolean containsOnlyPublicEntries()
  {
    return _annotations.containsOnlyPublicAnnotations();
  }

  /**
   * This method returns a version of the parameter model containing only entries that are part of the
   * public api.If this parameter is entirely public, then it returns <code>this</code>.
   * Otherwise a new instance will be created and populated with only the public api entries.
   */
  public ParameterModel toPublicAPI()
  {
    if(containsOnlyPublicEntries())
      return this;

    return new ParameterModel(getName(),
                              getType(), 
                              _annotations.toPublicAPI());
  }


  public AnnotationsModel getAnnotations()
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
    _type.collectDependencies(dependencies);
    _annotations.collectDependencies(dependencies);
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append(_type).append(" ").append(_name);
    return sb.toString();
  }

  /**
   * Assigns the generics to the generic variables (ex: E =&gt; Number)
   * @return the new model (or <code>this</code> if no generics...)
   */
  public ParameterModel assignGenericTypeVariable(String name, Type value)
  {
    Type newType = _type.assignGenericTypeVariable(name, value);
    if(newType != _type)
      return new ParameterModel(name, newType, _annotations);
    else
      return this;
  }
}
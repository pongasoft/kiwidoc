
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
import com.pongasoft.kiwidoc.model.type.Type;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

/**
 * @author yan@pongasoft.com
 */
public class ParametersModel implements DependenciesCollector
{
  private final List<ParameterModel> _parameters;

  /**
   * Constructor
   */
  public ParametersModel(List<ParameterModel> parameters)
  {
    _parameters = parameters;
  }

  public List<ParameterModel> getParameters()
  {
    return _parameters;
  }

  public boolean containsOnlyPublicEntries()
  {
    for(ParameterModel parameter : _parameters)
    {
      if(!parameter.containsOnlyPublicEntries())
        return false;
    }
    
    return true;
  }

  /**
   * This method returns a version of the parameter model containing only entries that are part of the
   * public api.If this parameter is entirely public, then it returns <code>this</code>.
   * Otherwise a new instance will be created and populated with only the public api entries.
   */
  public ParametersModel toPublicAPI()
  {
    if(containsOnlyPublicEntries())
      return this;

    List<ParameterModel> list = new ArrayList<ParameterModel>();
    for(ParameterModel parameter : _parameters)
    {
      list.add(parameter.toPublicAPI());
    }

    return new ParametersModel(list);
  }


  /**
   * Collect the dependencies in the collection.
   *
   * @param dependencies to add the dependencies to it
   */
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    for(ParameterModel parameter : _parameters)
    {
      parameter.collectDependencies(dependencies);
    }
  }

  /**
   * Assigns the generics to the generic variables (ex: E =&gt; Number)
   * @return the new model (or <code>this</code> if no generics...)
   */
  public ParametersModel assignGenericTypeVariable(String name, Type value)
  {
    boolean changed = false;

    List<ParameterModel> newParameters = new ArrayList<ParameterModel>();
    for(ParameterModel parameter : _parameters)
    {
      ParameterModel newParameter = parameter.assignGenericTypeVariable(name, value);
      changed |= newParameter != parameter;
    }

    if(changed)
      return new ParametersModel(newParameters);
    else
      return this;
  }
}

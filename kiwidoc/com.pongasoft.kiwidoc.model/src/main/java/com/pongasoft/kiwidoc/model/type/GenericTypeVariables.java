
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

package com.pongasoft.kiwidoc.model.type;

import com.pongasoft.kiwidoc.model.resource.DependenciesCollector;
import com.pongasoft.kiwidoc.model.resource.ResolvableResource;

import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;

/**
 * @author yan@pongasoft.com
 */
public class GenericTypeVariables implements DependenciesCollector
{
  public static final GenericTypeVariables NO_GENERIC_TYPE_VARIABLES =
    new GenericTypeVariables(Collections.<GenericTypeVariable>emptyList());
  
  private final List<GenericTypeVariable> _genericTypeVariables;

  /**
   * Constructor
   */
  public GenericTypeVariables(List<GenericTypeVariable> genericTypeVariables)
  {
    _genericTypeVariables = genericTypeVariables;
  }

  public List<GenericTypeVariable> getGenericTypeVariables()
  {
    return _genericTypeVariables;
  }

  /**
   * @return the generic type variable with the given name or <code>null</code> if not found
   */
  public GenericTypeVariable findGenericTypeVariable(String name)
  {
    for(GenericTypeVariable genericTypeVariable : _genericTypeVariables)
    {
      if(genericTypeVariable.getName().equals(name))
        return genericTypeVariable;
    }

    return null;
  }

  /**
   * Instantiates the generic type variable of the given name with the given type. The contract
   * is that the type should return <code>this</code> if unchanged, otherwise the new value...
   */
  public GenericTypeVariables assignGenericTypeVariable(String name, Type type)
  {
    if(_genericTypeVariables.isEmpty())
      return this;

    List<GenericTypeVariable> res = new ArrayList<GenericTypeVariable>();
    boolean changed = false;

    for(GenericTypeVariable genericTypeVariable : _genericTypeVariables)
    {
      GenericTypeVariable gtv =
        (GenericTypeVariable) genericTypeVariable.assignGenericTypeVariable(name, type);
      changed |= gtv != genericTypeVariable;
      res.add(gtv);
    }

    if(changed)
      return new GenericTypeVariables(res);
    else
      return this;
  }

  /**
   * Instantiates the generic type variable of the given name with the given type. The contract
   * is that the type should return <code>this</code> if unchanged, otherwise the new value...
   */
  public GenericTypeVariables assignGenerics(List<? extends Type> generics)
  {
    if(generics.size() != _genericTypeVariables.size())
    {
      throw new IllegalArgumentException("generics mismatch " + generics +
                                         " != " + _genericTypeVariables);

    }

    if(_genericTypeVariables.isEmpty())
      return this;

    List<GenericTypeVariable> newGTVs = new ArrayList<GenericTypeVariable>();

    int len = generics.size();
    for(int i = 0; i < len; i++)
    {
      GenericTypeVariable gtv = _genericTypeVariables.get(i);
      newGTVs.add((GenericTypeVariable) gtv.assignGenericTypeVariable(gtv.getName(),
                                                                      generics.get(i)));
    }

    return new GenericTypeVariables(newGTVs);
  }

  /**
   * Collect the dependencies in the collection.
   *
   * @param dependencies to add the dependencies to it
   */
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    for(GenericTypeVariable genericTypeVariable : _genericTypeVariables)
    {
      genericTypeVariable.collectDependencies(dependencies);
    }
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    if(!_genericTypeVariables.isEmpty())
    {
      sb.append('<');
      int i = 0;
      for(GenericTypeVariable genericTypeVariable : _genericTypeVariables)
      {
        if(i > 0)
          sb.append(", ");
        sb.append(genericTypeVariable);
        i++;
      }
      sb.append('>');
    }
    return sb.toString();
  }
}

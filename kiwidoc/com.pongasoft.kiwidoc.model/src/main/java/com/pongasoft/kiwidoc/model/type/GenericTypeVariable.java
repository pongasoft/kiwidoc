
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

import com.pongasoft.kiwidoc.model.resource.ResolvableResource;

import java.util.List;
import java.util.Collections;
import java.util.Collection;

/**
 * Represents a parameter type (ex: E) which can have bounds (ex: E extends Number)
 *
 * @author yan@pongasoft.com
 */
public class GenericTypeVariable implements Type
{
  public static final List<Type> NO_BOUNDS = Collections.emptyList();

  private final String _name;
  private final List<Type> _bounds;

  /**
   * Constructor
   */
  public GenericTypeVariable(String name, List<Type> bounds)
  {
    _name = name;
    _bounds = bounds;
  }

  public GenericTypeVariable(String name)
  {
    this(name, NO_BOUNDS);
  }

  public String getName()
  {
    return _name;
  }

  public List<Type> getBounds()
  {
    return _bounds;
  }

  /**
   * Instantiates the generic type variable of the given name with the given type. The contract is
   * that the type should return <code>this</code> if unchanged, otherwise the new value...
   */
  public Type assignGenericTypeVariable(String name, Type type)
  {
    if(_name.equals(name))
      return new AssignedGenericTypeVariable(name, _bounds, type);
    else
      return this;
  }

  /**
   * @return the erasure of the type (as it is stored in the byte code)
   */
  public String toErasureDescriptor()
  {
    return "Ljava/lang/Object;";
  }

  /**
   * Each type knows what its dependencies are.
   *
   * @param dependencies to add the dependencies to it
   */
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    for(Type bound : _bounds)
    {
      bound.collectDependencies(dependencies);
    }
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append(_name);
    if(!_bounds.isEmpty())
    {
      sb.append(" extends ");
      int i = 0;
      for(Type bound : _bounds)
      {
        if(i > 0)
          sb.append(" & ");
        sb.append(bound);
        i++;
      }
    }
    return sb.toString();
  }
}

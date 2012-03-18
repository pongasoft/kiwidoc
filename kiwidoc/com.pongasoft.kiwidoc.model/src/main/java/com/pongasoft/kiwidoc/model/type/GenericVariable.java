
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

import java.util.Collection;

/**
 * Represents a simple generic type variable (E).
 * 
 * @author yan@pongasoft.com
 */
public class GenericVariable implements Type
{
  private final String _name;

  /**
   * Constructor
   */
  public GenericVariable(String name)
  {
    _name = name;
  }

  public String getName()
  {
    return _name;
  }

  /**
   * Instantiates the generic type variable of the given name with the given type. The contract is
   * that the type should return <code>this</code> if unchanged, otherwise the new value...
   */
  public Type assignGenericTypeVariable(String name, Type type)
  {
    if(_name.equals(name))
      return type;
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
    // no dependencies
  }

  @Override
  public String toString()
  {
    return _name;
  }
}

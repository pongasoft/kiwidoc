
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
 * @author yan@pongasoft.com
 */
public class ArrayType implements Type
{
  private final int _dimension;
  private final Type _type;

  /**
   * Constructor
   */
  public ArrayType(int dimension, Type type)
  {
    if(dimension < 1)
      throw new IllegalArgumentException("dimension must be >= 1");

    _dimension = dimension;
    _type = type;
  }

  public int getDimension()
  {
    return _dimension;
  }

  public Type getType()
  {
    return _type;
  }

  /**
   * Instantiates the generic type variable of the given name with the given type. The contract is
   * that the type should return <code>this</code> if unchanged, otherwise the new value...
   */
  public Type assignGenericTypeVariable(String name, Type type)
  {
    Type newType = _type.assignGenericTypeVariable(name, type);

    if(newType == _type)
      return this;
    else
      return new ArrayType(_dimension, newType);
  }

  /**
   * @return the erasure of the type (as it is stored in the byte code)
   */
  public String toErasureDescriptor()
  {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < _dimension; i++)
    {
      sb.append('[');
    }
    sb.append(_type.toErasureDescriptor());
    return sb.toString();
  }

  /**
   * Each type knows what its dependencies are.
   *
   * @param dependencies to add the dependencies to it
   */
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    _type.collectDependencies(dependencies);
  }

  @Override
  public String toString()
  {
    return toString(false);
  }

  public String toString(boolean vararg)
  {
    final StringBuilder sb = new StringBuilder();
    sb.append(_type);
    if(vararg)
    {
      sb.append("...");
    }
    else
    {
      for(int i = 0; i < _dimension; i++)
      {
        sb.append("[]");
      }
    }
    return sb.toString();
  }
}

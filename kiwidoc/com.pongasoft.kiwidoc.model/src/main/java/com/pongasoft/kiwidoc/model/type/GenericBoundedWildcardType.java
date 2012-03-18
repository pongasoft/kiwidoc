
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
 * Represents a wildcard (? extends A, or ? super A)
 *
 * @author yan@pongasoft.com
 */
public class GenericBoundedWildcardType implements GenericWildcardType
{
  private final Type _bound;
  private final Kind _kind;

  public static enum Kind
  {
    SUPER,
    EXTENDS
  }

  /**
   * Constructor
   */
  public GenericBoundedWildcardType(Kind kind, Type bound)
  {
    _kind = kind;
    _bound = bound;
  }

  public Type getBound()
  {
    return _bound;
  }

  public Kind getKind()
  {
    return _kind;
  }

  public boolean isSuperKind()
  {
    return _kind == Kind.SUPER;
  }

  /**
   * Instantiates the generic type variable of the given name with the given type. The contract is
   * that the type should return <code>this</code> if unchanged, otherwise the new value...
   */
  public Type assignGenericTypeVariable(String name, Type type)
  {
    Type newBound = _bound.assignGenericTypeVariable(name, type);
    
    if(newBound == _bound)
      return this;
    else
      return new GenericBoundedWildcardType(_kind, newBound);
  }

  /**
   * @return the erasure of the type (as it is stored in the byte code)
   */
  public String toErasureDescriptor()
  {
    throw new UnsupportedOperationException("no erasure for wildcard");
  }

  /**
   * Each type knows what its dependencies are.
   *
   * @param dependencies to add the dependencies to it
   */
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    _bound.collectDependencies(dependencies);
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append('?');
    sb.append(_kind == Kind.SUPER ? " super " : " extends ");
    sb.append(_bound);
    return sb.toString();
  }
}
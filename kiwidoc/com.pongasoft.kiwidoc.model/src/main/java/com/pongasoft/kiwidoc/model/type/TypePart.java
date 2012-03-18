
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

import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.resource.DependenciesCollector;
import com.pongasoft.kiwidoc.model.resource.ResolvableResource;

import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;

/**
 * Represents a type part: name + (optional) bounds (ex: A&lt;T&gt;).
 * 
 * @author yan@pongasoft.com
 */
public class TypePart implements DependenciesCollector
{
  public static final List<? extends Type> NO_GENERICS = Collections.emptyList();
  
  private final List<? extends Type> _generics;
  private final ClassResource _classResource;

  /**
   * Constructor
   */
  public TypePart(ClassResource classResource, List<? extends Type> generics)
  {
    _classResource = classResource;
    _generics = generics == null ? NO_GENERICS : generics;
  }

  /**
   * Constructor
   */
  public TypePart(ClassResource classResource)
  {
    this(classResource, NO_GENERICS);
  }

  public String getName()
  {
    if(_classResource.isInnerClass())
      return _classResource.getInnerClassName();
    else
      return _classResource.getFqcn();
  }

  public List<? extends Type> getGenerics()
  {
    return _generics;
  }

  public boolean isGeneric()
  {
    return !_generics.isEmpty();
  }

  public ClassResource getClassResource()
  {
    return _classResource;
  }

  /**
   * Instantiates the generic type variable of the given name with the given type. The contract is
   * that the type should return <code>this</code> if unchanged, otherwise the new value...
   */
  public TypePart assignGenericTypeVariable(String name, Type type)
  {
    if(!_generics.isEmpty())
    {
      boolean changed = false;
      List<Type> generics = new ArrayList<Type>(_generics.size());
      for(Type generic : _generics)
      {
        Type newGeneric = generic.assignGenericTypeVariable(name, type);
        changed |= newGeneric != generic;
        generics.add(newGeneric);
      }
      if(changed)
        return new TypePart(_classResource, generics);
    }
    
    return this;
  }

  /**
   * @return the erasure of the type (as it is stored in the byte code)
   */
  public String toErasureDescriptor()
  {
    return ClassResource.computeInternalName(getName());
  }

  /**
   * Collect the dependencies in the collection.
   *
   * @param dependencies to add the dependencies to it
   */
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    dependencies.add(_classResource);
    
    if(_generics != null)
    {
      for(Type type : _generics)
      {
        type.collectDependencies(dependencies);
      }
    }
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append(getName());
    if(_generics != null && _generics.size() > 0)
    {
      sb.append('<');
      int i = 0;
      for(Type type : _generics)
      {
        if(i > 0)
          sb.append(", ");
        sb.append(type);
        i++;
      }
      sb.append('>');
    }
    return sb.toString();
  }
}

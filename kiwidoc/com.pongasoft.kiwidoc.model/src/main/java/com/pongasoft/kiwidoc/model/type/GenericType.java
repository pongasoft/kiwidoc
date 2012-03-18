
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
import com.pongasoft.kiwidoc.model.resource.ClassResource;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

/**
 * Represents a generic type (ex: java.util.Map&lt;java.lang.String,java.lang.String&gt;) (handles
 * inner classes and generics)
 *
 * @author yan@pongasoft.com
 */
public class GenericType implements Type
{
  private final ClassResource _classResource;
  private final List<TypePart> _typeParts;

  /**
   * Constructor
   */
  protected GenericType(ClassResource classResource, List<TypePart> typeParts)
  {
    _classResource = classResource;
    _typeParts = typeParts;
  }

  /**
   * Static creator... return a <code>SimpleType</code> when possible
   */
  public static GenericType create(List<TypePart> typeParts)
  {
    if(typeParts.size() == 0)
      throw new IllegalArgumentException("must have at least one part");

    return create(typeParts.get(typeParts.size() - 1).getClassResource(), typeParts);
  }

  /**
   * Static creator... return a <code>SimpleType</code> when possible
   */
  public static GenericType create(ClassResource classResource)
  {
    List<TypePart> typeParts = new ArrayList<TypePart>();
    if(classResource.isInnerClass())
    {
      ClassResource cr = classResource;
      while(cr.isInnerClass())
      {
        typeParts.add(0, new TypePart(cr));
        cr = cr.getOuterClassResource();
      }
      // adding the outer class...
      typeParts.add(0, new TypePart(cr));
    }
    else
    {
      typeParts.add(new TypePart(classResource));
    }

    return create(classResource, typeParts);
  }

  /**
   */
  private static GenericType create(ClassResource classResource, List<TypePart> typeParts)
  {
    if(typeParts.size() == 1)
    {
      if(!typeParts.get(0).isGeneric())
        return new SimpleType(classResource, typeParts);
    }

    return new GenericType(classResource, typeParts);
  }

  /**
   * @return the internal name
   */
  public String getInternalName()
  {
    return ClassResource.computeInternalName(_classResource.getFqcn());
  }

  public ClassResource getClassResource()
  {
    return _classResource;
  }

  /**
   * @return the fully qualified class name of the generic type (drops the generics)
   */
  public String getFqcn()
  {
    return getClassResource().getFqcn();
  }

  /**
   * @return <code>true</code> if this type and the classes represent the same class (note that
   * it disregard the library... it uses only fqcn)
   */
  public boolean isSameFqcn(ClassResource classResource)
  {
    if(classResource == null)
      return false;

    return classResource.getFqcn().equals(getFqcn());
  }

  public List<TypePart> getTypeParts()
  {
    return _typeParts;
  }

  public TypePart getFirstPart()
  {
    return _typeParts.get(0);
  }

  public TypePart getLastPart()
  {
    return _typeParts.get(_typeParts.size() - 1);
  }

  public boolean isObjectType()
  {
    return false;
  }

  /**
   * Instantiates the generic type variable of the given name with the given type. The contract is
   * that the type should return <code>this</code> if unchanged, otherwise the new value...
   */
  public Type assignGenericTypeVariable(String name, Type type)
  {
    List<TypePart> typeParts = new ArrayList<TypePart>();
    boolean changed = false;

    for(TypePart typePart : _typeParts)
    {
      TypePart newTypePart = typePart.assignGenericTypeVariable(name, type);
      changed |= newTypePart != typePart;
      typeParts.add(newTypePart);
    }

    if(changed)
      return new GenericType(_classResource, typeParts);
    else
      return this;
  }

  /**
   * @return the erasure of the type (as it is stored in the byte code)
   */
  public String toErasureDescriptor()
  {
    return "L" + getInternalName() + ";";
  }

  /**
   * Each type knows what its dependencies are.
   *
   * @param dependencies to add the dependencies to it
   */
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    dependencies.add(_classResource);
    for(TypePart typePart : _typeParts)
    {
      typePart.collectDependencies(dependencies);
    }
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    int i = 0;
    for(TypePart typePart : _typeParts)
    {
      if(i > 0)
        sb.append('$');
      sb.append(typePart);
      i++;
    }
    return sb.toString();
  }
}


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

import java.util.List;
import java.util.ArrayList;

/**
 * Simple type: no generics, no inner class...
 * 
 * @author yan@pongasoft.com
 */
public class SimpleType extends GenericType
{
  private static final ClassResource OBJECT_RESOURCE = new ClassResource(Object.class.getName()); 

  /**
   * Constructor
   *
   * @param classResource
   * @param typeParts
   */
  protected SimpleType(ClassResource classResource, List<TypePart> typeParts)
  {
    super(classResource, typeParts);
  }

  @Override
  public boolean isObjectType()
  {
    return isSameFqcn(OBJECT_RESOURCE);
  }

  /**
   * Instantiates the generic type variable of the given name with the given type. The contract is
   * that the type should return <code>this</code> if unchanged, otherwise the new value...
   */
  @Override
  public Type assignGenericTypeVariable(String name, Type type)
  {
    return this;
  }

  /**
   * Create a simple type
   */
  public static SimpleType create(ClassResource classResource)
  {
    if(classResource.isInnerClass())
      throw new IllegalArgumentException("only valid for non inner classes!");

    List<TypePart> typeParts = new ArrayList<TypePart>(1);
    typeParts.add(new TypePart(classResource));

    return new SimpleType(classResource, typeParts);
  }

}

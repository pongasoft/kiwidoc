
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

import com.pongasoft.util.core.enums.Value;
import com.pongasoft.util.core.enums.EnumCodec;
import com.pongasoft.kiwidoc.model.resource.ResolvableResource;

import java.util.Collection;

/**
 * @author yan@pongasoft.com
 */
public class PrimitiveType implements Type
{
  public enum Primitive
  {
    @Value("Z") BOOLEAN(Boolean.class),
    @Value("C") CHAR(Character.class),
    @Value("B") BYTE(Byte.class),
    @Value("S") SHORT(Short.class),
    @Value("I") INT(Integer.class),
    @Value("F") FLOAT(Float.class),
    @Value("J") LONG(Long.class),
    @Value("D") DOUBLE(Double.class),
    @Value("V") VOID(Void.class);

    private final Class _wrapperClass;

    private Primitive(Class wrapperClass)
    {
      _wrapperClass = wrapperClass;
    }

    public static Primitive fromWrapperClass(Class<?> wrapperClass)
    {
      for(Primitive primitive : values())
      {
        if(primitive._wrapperClass.equals(wrapperClass))
          return primitive;
      }
      throw new IllegalArgumentException("not a primitive wrapper class! " + wrapperClass);
    }
  }

  private final Primitive _primitive;

  /**
   * Constructor
   */
  public PrimitiveType(Primitive primitive)
  {
    _primitive = primitive;
  }

  /**
   * Constructor
   */
  public PrimitiveType(String primitive)
  {
    this(Enum.valueOf(Primitive.class, primitive.toUpperCase()));
  }

  /**
   * Constructor
   */
  public PrimitiveType(Class primitiveWrapperClass)
  {
    this(Primitive.fromWrapperClass(primitiveWrapperClass));
  }

  public Primitive getPrimitive()
  {
    return _primitive;
  }

  /**
   * Instantiates the generic type variable of the given name with the given type. The contract is
   * that the type should return <code>this</code> if unchanged, otherwise the new value...
   */
  public Type assignGenericTypeVariable(String name, Type type)
  {
    return this;
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

  /**
   * @return the erasure of the type (as it is stored in the byte code)
   */
  public String toErasureDescriptor()
  {
    return EnumCodec.INSTANCE.encode(getPrimitive());
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append(_primitive.toString().toLowerCase());
    return sb.toString();
  }
}

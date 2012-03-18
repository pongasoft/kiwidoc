
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

package com.pongasoft.kiwidoc.builder.serializer.type;

import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.type.ArrayType;
import com.pongasoft.kiwidoc.model.type.GenericBoundedWildcardType;
import com.pongasoft.kiwidoc.model.type.GenericType;
import com.pongasoft.kiwidoc.model.type.GenericTypeVariable;
import com.pongasoft.kiwidoc.model.type.GenericTypeVariables;
import com.pongasoft.kiwidoc.model.type.GenericUnboundedWildcardType;
import com.pongasoft.kiwidoc.model.type.GenericVariable;
import com.pongasoft.kiwidoc.model.type.PrimitiveType;
import com.pongasoft.kiwidoc.model.type.SimpleType;
import com.pongasoft.kiwidoc.model.type.Type;
import com.pongasoft.kiwidoc.model.type.TypePart;
import com.pongasoft.kiwidoc.model.type.UnresolvedType;
import com.pongasoft.util.core.enums.EnumCodec;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.signature.SignatureWriter;

import java.util.Arrays;
import java.util.List;

/**
 * @author yan@pongasoft.com
 */
public class TypeEncoder
{
  public static final Type OBJECT_TYPE = SimpleType.create(new ClassResource(Object.class.getName()));
  public static final List<Type> OBJECT_BOUNDS = Arrays.asList(OBJECT_TYPE);
  public static final String OBJECT_SIGNATURE = "Ljava/lang/Object;";
  public static final int OBJECT_SIGNATURE_LENGTH = OBJECT_SIGNATURE.length();

  /**
   * Constructor
   */
  public TypeEncoder()
  {
  }

  /**
   * @return the signature for a type
   */
  public String encodeType(Type type)
  {
    SignatureWriter sw = new SignatureWriter();

    buildType(sw, type);

    return sw.toString();
  }

  /**
   * @return the signature for a given generic type variable
   */
  public String encodeGenericTypeVariables(GenericTypeVariables type)
  {
    SignatureWriter sw = new SignatureWriter();

    for(GenericTypeVariable gtv : type.getGenericTypeVariables())
    {
      sw.visitFormalTypeParameter(gtv.getName());

      List<Type> bounds = gtv.getBounds();
      if(bounds.isEmpty())
        bounds = OBJECT_BOUNDS;
      int i = 0;
      for(Type bound : bounds)
      {
        SignatureVisitor sv;
        if(i > 0)
        {
          sv = sw.visitInterfaceBound();
        }
        else
        {
          sv = sw.visitClassBound();
        }
        buildType(sv, bound);
        i++;
      }
    }

    // the signature is not complete without visiting the superclass... so we visit it and then
    // we remove it from the signature
    SignatureVisitor sv = sw.visitSuperclass();
    buildType(sv, OBJECT_TYPE);
    String signature = sw.toString();

    signature = signature.substring(0, signature.length() - OBJECT_SIGNATURE_LENGTH);

    return signature;
  }

  private void buildType(SignatureVisitor sv, Type type)
  {
    if(type instanceof PrimitiveType)
    {
      PrimitiveType primitiveType = (PrimitiveType) type;
      sv.visitBaseType(EnumCodec.INSTANCE.encode(primitiveType.getPrimitive()).charAt(0));
      return;
    }

    if(type instanceof GenericVariable)
    {
      GenericVariable genericVariable = (GenericVariable) type;
      sv.visitTypeVariable(genericVariable.getName());
      return;
    }

    if(type instanceof ArrayType)
    {
      ArrayType arrayType = (ArrayType) type;
      for(int i = 0; i < arrayType.getDimension(); i++)
        sv = sv.visitArrayType();
      buildType(sv, arrayType.getType());
      return;
    }

    if(type instanceof GenericType)
    {
      GenericType genericType = (GenericType) type;
      int i = 0;
      for(TypePart typePart : genericType.getTypeParts())
      {
        if(i == 0)
        {
          sv.visitClassType(ClassResource.computeInternalName(typePart.getName()));
        }
        else
        {
          sv.visitInnerClassType(typePart.getName());
        }
        i++;
        buildGenericType(sv, typePart.getGenerics());
      }
      sv.visitEnd();
      return;
    }

    if(type instanceof UnresolvedType)
    {
      UnresolvedType unresolvedType = (UnresolvedType) type;
      sv.visitClassType(unresolvedType.getInternalName());
      sv.visitEnd();
      return;
    }

    throw new RuntimeException("unsupported type here..." + type.getClass().getName());
  }

  private void buildGenericType(SignatureVisitor sv, List<? extends Type> types)
  {
    if(types == null)
      return;
    for(Type type : types)
    {
      buildGenericType(sv, type);
    }
  }

  private void buildGenericType(SignatureVisitor sv, Type type)
  {
    if(type instanceof GenericUnboundedWildcardType)
    {
      sv.visitTypeArgument();
      return;
    }

    if(type instanceof GenericBoundedWildcardType)
    {
      GenericBoundedWildcardType gbwt = (GenericBoundedWildcardType) type;
      sv = sv.visitTypeArgument(gbwt.isSuperKind() ?
                                SignatureVisitor.SUPER : SignatureVisitor.EXTENDS);
      buildType(sv, gbwt.getBound());
      return;
    }

    buildType(sv.visitTypeArgument(SignatureVisitor.INSTANCEOF), type);
  }

}
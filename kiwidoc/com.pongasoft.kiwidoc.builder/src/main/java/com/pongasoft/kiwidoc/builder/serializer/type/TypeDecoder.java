
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
import com.pongasoft.kiwidoc.model.type.Type;
import com.pongasoft.kiwidoc.model.type.TypePart;
import com.pongasoft.util.core.enums.EnumCodec;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yan@pongasoft.com
 */
public class TypeDecoder
{
  private static interface TypeSetter
  {
    void setType(Type type);
  }

  private static class TypeHolder implements TypeSetter
  {
    private Type _type;

    public void setType(Type type)
    {
      _type = type;
    }

    public Type getType()
    {
      return _type;
    }
  }

  public interface ClassSignature
  {
    GenericTypeVariables getGenericTypeVariables();

    GenericType getSuperClass();

    Collection<GenericType> getInterfaces();
  }

  public static class ClassSignatureImpl implements ClassSignature
  {
    private final GenericTypeVariables _genericTypeVariables;
    private final GenericType _superclass;
    private final Collection<GenericType> _interfaces;

    public ClassSignatureImpl(GenericTypeVariables genericTypeVariables,
                              GenericType superclass,
                              Collection<GenericType> interfaces)
    {

      _genericTypeVariables = genericTypeVariables;
      _superclass = superclass;
      _interfaces = interfaces;
    }

    public GenericTypeVariables getGenericTypeVariables()
    {
      return _genericTypeVariables;
    }

    public Collection<GenericType> getInterfaces()
    {
      return _interfaces;
    }

    public GenericType getSuperClass()
    {
      return _superclass;
    }
  }

  private static class ClassSignatureVisitor implements SignatureVisitor, TypeSetter, ClassSignature
  {
    private enum State
    {
      NONE,
      VISITING_FORMAL_TYPE_PARAMETER,
      VISITING_SUPERCLASS,
      VISITING_INTERFACE
    }

    public final TypeBuilder _typeBuilder = new TypeBuilder(this);

    private State _state = State.NONE;
    
    private Map<String, List<Type>> _genericTypeVariables;
    private GenericType _superClass;
    private List<GenericType> _interfaces = new ArrayList<GenericType>();
    private String _currentFormalTypeParameter;

    public GenericTypeVariables getGenericTypeVariables()
    {
      if(_genericTypeVariables == null)
        return GenericTypeVariables.NO_GENERIC_TYPE_VARIABLES;

      List<GenericTypeVariable> list = new ArrayList<GenericTypeVariable>();
      for(Map.Entry<String, List<Type>> entry : _genericTypeVariables.entrySet())
      {
        list.add(new GenericTypeVariable(entry.getKey(), entry.getValue()));
      }

      return new GenericTypeVariables(list);
    }

    public GenericType getSuperClass()
    {
      return _superClass;
    }

    public Collection<GenericType> getInterfaces()
    {
      return _interfaces;
    }

    public void setType(Type type)
    {
      switch(_state)
      {
        case VISITING_SUPERCLASS:
          _superClass = (GenericType) type;
          break;

        case VISITING_INTERFACE:
          _interfaces.add((GenericType) type);
          break;

        case VISITING_FORMAL_TYPE_PARAMETER:
          List<Type> list = _genericTypeVariables.get(_currentFormalTypeParameter);
          list.add(type);
          break;

        default:
          throw new RuntimeException("unreached");
      }
    }

    public void visitFormalTypeParameter(String name)
    {
      _state = State.VISITING_FORMAL_TYPE_PARAMETER;
      if(_genericTypeVariables == null)
      {
        _genericTypeVariables = new LinkedHashMap<String, List<Type>>();
      }
      _genericTypeVariables.put(name, new ArrayList<Type>());
      _currentFormalTypeParameter = name;
    }

    public SignatureVisitor visitClassBound()
    {
      return _typeBuilder;
    }

    public SignatureVisitor visitInterfaceBound()
    {
      return _typeBuilder;
    }

    public SignatureVisitor visitSuperclass()
    {
      _state = State.VISITING_SUPERCLASS;
      return _typeBuilder;
    }

    public SignatureVisitor visitInterface()
    {
      _state = State.VISITING_INTERFACE;
      return _typeBuilder;
    }

    public SignatureVisitor visitParameterType()
    {
      throw new RuntimeException("should not called");
    }

    public SignatureVisitor visitReturnType()
    {
      throw new RuntimeException("should not called");
    }

    public SignatureVisitor visitExceptionType()
    {
      throw new RuntimeException("should not called");
    }

    public void visitBaseType(char c)
    {
      throw new RuntimeException("should not called");
    }

    public void visitTypeVariable(String s)
    {
      throw new RuntimeException("should not called");
    }

    public SignatureVisitor visitArrayType()
    {
      throw new RuntimeException("should not called");
    }

    public void visitClassType(String s)
    {
      throw new RuntimeException("should not called");
    }

    public void visitInnerClassType(String s)
    {
      throw new RuntimeException("should not called");
    }

    public void visitTypeArgument()
    {
      throw new RuntimeException("should not called");
    }

    public SignatureVisitor visitTypeArgument(char c)
    {
      throw new RuntimeException("should not called");
    }

    public void visitEnd()
    {
      throw new RuntimeException("should not called");
    }
  }

  private static class TypeBuilder implements SignatureVisitor, TypeSetter
  {
    private final TypeSetter _typeSetter;

    private ClassResource _currentClassResource = null;
    private List<TypePart> _typeParts = new ArrayList<TypePart>();
    private List<Type> _bounds;
    private char _typeArgument = 0;
    private int _dimension = 0;

    private TypeBuilder(TypeSetter typeSetter)
    {
      _typeSetter = typeSetter;
    }

    public void setType(Type type)
    {
      switch(_typeArgument)
      {
        case SignatureVisitor.EXTENDS:
          type = new GenericBoundedWildcardType(GenericBoundedWildcardType.Kind.EXTENDS, type);
          break;

        case SignatureVisitor.SUPER:
          type = new GenericBoundedWildcardType(GenericBoundedWildcardType.Kind.SUPER, type);
          break;

        case SignatureVisitor.INSTANCEOF:
        default:
          // nothing to do
          break;
      }

      if(_bounds == null)
        _bounds = new ArrayList<Type>();

      _bounds.add(type);
    }

    public void adjustArrayType(Type type)
    {
      if(_dimension > 0)
        _typeSetter.setType(new ArrayType(_dimension, type));
      else
        _typeSetter.setType(type);

      // resets the object
      _currentClassResource = null;
      _typeParts = new ArrayList<TypePart>();
      _bounds = null;
      _typeArgument = 0;
      _dimension = 0;
    }

    public void visitFormalTypeParameter(String s)
    {
      throw new RuntimeException("should not called");
    }

    public SignatureVisitor visitClassBound()
    {
      throw new RuntimeException("should not called");
    }

    public SignatureVisitor visitInterfaceBound()
    {
      throw new RuntimeException("should not called");
    }

    public SignatureVisitor visitSuperclass()
    {
      throw new RuntimeException("should not called");
    }

    public SignatureVisitor visitInterface()
    {
      throw new RuntimeException("should not called");
    }

    public SignatureVisitor visitParameterType()
    {
      throw new RuntimeException("should not called");
    }

    public SignatureVisitor visitReturnType()
    {
      throw new RuntimeException("should not called");
    }

    public SignatureVisitor visitExceptionType()
    {
      throw new RuntimeException("should not called");
    }

    public void visitBaseType(char c)
    {
      PrimitiveType.Primitive primitive =
        EnumCodec.INSTANCE.decode(PrimitiveType.Primitive.class, String.valueOf(c));
      adjustArrayType(new PrimitiveType(primitive));
    }

    public void visitTypeVariable(String name)
    {
      adjustArrayType(new GenericVariable(name));
    }

    public SignatureVisitor visitArrayType()
    {
      _dimension++;
      return this;
    }

    public void visitClassType(String name)
    {
      // YP note: handling the inconsistency in byte code (or asm...) that when there is
      // no generics, then it uses $ but when there is generics it calls visitInnerClassType...
      _currentClassResource = new ClassResource(ClassResource.computeFQCNFromInternalName(name));
      if(_currentClassResource.isInnerClass())
      {
        List<String> innerClassNames = new ArrayList<String>();
        while(_currentClassResource.isInnerClass())
        {
          innerClassNames.add(0, _currentClassResource.getInnerClassName());
          _currentClassResource = _currentClassResource.getOuterClassResource();
        }
        for(String innerClassName : innerClassNames)
        {
          visitInnerClassType(innerClassName);
        }
      }
    }

    public void visitInnerClassType(String name)
    {
      _typeParts.add(new TypePart(_currentClassResource, _bounds));
      _currentClassResource = _currentClassResource.createInnerClass(name);
      _bounds = null;
    }

    public void visitTypeArgument()
    {
      _bounds = new ArrayList<Type>();
      _bounds.add(GenericUnboundedWildcardType.INSTANCE);
    }

    public SignatureVisitor visitTypeArgument(char c)
    {
      _typeArgument = c;
      return new TypeBuilder(this);
    }

    public void visitEnd()
    {
      _typeParts.add(new TypePart(_currentClassResource, _bounds));

      adjustArrayType(GenericType.create(_typeParts));
    }
  }

  public interface MethodSignature
  {
    GenericTypeVariables getGenericTypeVariables();

    Type getReturnType();

    List<Type> getParameters();

    Collection<Type> getExceptions();
  }

  public static class MethodSignatureImpl implements MethodSignature
  {
    private final GenericTypeVariables _genericTypeVariables;
    private final Type _returnType;
    private final List<Type> _parameters;
    private final Collection<Type> _exceptions;

    public MethodSignatureImpl(GenericTypeVariables genericTypeVariables,
                               Type returnType,
                               List<Type> parameters,
                               Collection<Type> exceptions)
    {

      _genericTypeVariables = genericTypeVariables;
      _returnType = returnType;
      _parameters = parameters;
      _exceptions = exceptions;
    }

    public Collection<Type> getExceptions()
    {
      return _exceptions;
    }

    public GenericTypeVariables getGenericTypeVariables()
    {
      return _genericTypeVariables;
    }

    public List<Type> getParameters()
    {
      return _parameters;
    }

    public Type getReturnType()
    {
      return _returnType;
    }
  }

  private static class MethodSignatureVisitor implements SignatureVisitor, TypeSetter, MethodSignature
  {
    private enum State
    {
      NONE,
      VISITING_FORMAL_TYPE_PARAMETER,
      VISITING_PARAMETER_TYPE,
      VISITING_RETURN_TYPE,
      VISITING_EXCEPTION_TYPE
    }

    public final TypeBuilder _typeBuilder = new TypeBuilder(this);

    private State _state = State.NONE;

    private Map<String, List<Type>> _genericTypeVariables;
    private final List<Type> _parameterTypes = new ArrayList<Type>();
    private Type _returnType;
    private final List<Type> _exceptionTypes = new ArrayList<Type>();
    private String _currentFormalTypeParameter;
    
    public GenericTypeVariables getGenericTypeVariables()
    {
      if(_genericTypeVariables == null)
        return GenericTypeVariables.NO_GENERIC_TYPE_VARIABLES;

      List<GenericTypeVariable> list = new ArrayList<GenericTypeVariable>();
      for(Map.Entry<String, List<Type>> entry : _genericTypeVariables.entrySet())
      {
        list.add(new GenericTypeVariable(entry.getKey(), entry.getValue()));
      }

      return new GenericTypeVariables(list);
    }

    public Type getReturnType()
    {
      return _returnType;
    }

    public List<Type> getParameters()
    {
      return _parameterTypes;
    }

    public Collection<Type> getExceptions()
    {
      return _exceptionTypes;
    }

    public void setType(Type type)
    {
      switch(_state)
      {
        case VISITING_RETURN_TYPE:
          _returnType = type;
          break;

        case VISITING_PARAMETER_TYPE:
          _parameterTypes.add(type);
          break;

        case VISITING_FORMAL_TYPE_PARAMETER:
          List<Type> list = _genericTypeVariables.get(_currentFormalTypeParameter);
          list.add(type);
          break;

        case VISITING_EXCEPTION_TYPE:
          _exceptionTypes.add(type);
          break;

        default:
          throw new RuntimeException("unreached");
      }
    }
    
    public void visitFormalTypeParameter(String name)
    {
      _state = State.VISITING_FORMAL_TYPE_PARAMETER;
      if(_genericTypeVariables == null)
      {
        _genericTypeVariables = new LinkedHashMap<String, List<Type>>();
      }
      _genericTypeVariables.put(name, new ArrayList<Type>());
      _currentFormalTypeParameter = name;
    }

    public SignatureVisitor visitClassBound()
    {
      return _typeBuilder;
    }

    public SignatureVisitor visitInterfaceBound()
    {
      return _typeBuilder;
    }

    public SignatureVisitor visitSuperclass()
    {
      throw new RuntimeException("should not called");
    }

    public SignatureVisitor visitInterface()
    {
      throw new RuntimeException("should not called");
    }

    public SignatureVisitor visitParameterType()
    {
      _state = State.VISITING_PARAMETER_TYPE;
      return _typeBuilder;
    }

    public SignatureVisitor visitReturnType()
    {
      _state = State.VISITING_RETURN_TYPE;
      return _typeBuilder;
    }

    public SignatureVisitor visitExceptionType()
    {
      _state = State.VISITING_EXCEPTION_TYPE;
      return _typeBuilder;
    }

    public void visitBaseType(char c)
    {
      throw new RuntimeException("should not called");
    }

    public void visitTypeVariable(String s)
    {
      throw new RuntimeException("should not called");
    }

    public SignatureVisitor visitArrayType()
    {
      throw new RuntimeException("should not called");
    }

    public void visitClassType(String s)
    {
      throw new RuntimeException("should not called");
    }

    public void visitInnerClassType(String s)
    {
      throw new RuntimeException("should not called");
    }

    public void visitTypeArgument()
    {
      throw new RuntimeException("should not called");
    }

    public SignatureVisitor visitTypeArgument(char c)
    {
      throw new RuntimeException("should not called");
    }

    public void visitEnd()
    {
      throw new RuntimeException("should not called");
    }
  }

  /**
   * Constructor
   */
  public TypeDecoder()
  {
  }

  /**
   * @param signature the internal signature (stored in byte code)
   * @return the class signature as an interface
   */
  public ClassSignature decodeClassSignature(String signature)
  {
    SignatureReader sr = new SignatureReader(signature);

    ClassSignatureVisitor visitor = new ClassSignatureVisitor();

    try
    {
      sr.accept(visitor);
    }
    catch(Exception e)
    {
      throw new IllegalArgumentException("invalid signature " + signature, e);
    }

    return visitor;
  }

  /**
   * Decodes the signature for a single type (as was encoded by
   * {@link TypeEncoder#encodeType(Type)}). 
   */
  public Type decodeType(String signature)
  {
    if(signature == null || signature.equals(""))
      return null;
    
    SignatureReader sr = new SignatureReader(signature);

    TypeHolder typeHolder = new TypeHolder();

    TypeBuilder visitor = new TypeBuilder(typeHolder);

    try
    {
      sr.acceptType(visitor);
    }
    catch(Exception e)
    {
      throw new IllegalArgumentException("invalid signature " + signature, e);
    }

    return typeHolder.getType();
  }

  /**
   * Decodes the signature for generic type variables (as was encoded by
   * {@link TypeEncoder#encodeGenericTypeVariables(GenericTypeVariables)}). 
   */
  public GenericTypeVariables decodeGenericTypeVariables(String signature)
  {
    // the signature is not valid without the superclass... so we add it
    signature = signature + TypeEncoder.OBJECT_SIGNATURE;
    return decodeClassSignature(signature).getGenericTypeVariables();
  }

  /**
   * The signature represents only the method parameters.
   * @return the parameter types
   */
  public Collection<Type> decodeMethodParameters(String signature)
  {
    signature = "(" + signature + ")V";
    return decodeMethodSignature(signature).getParameters();
  }

  /**
   * @param signature the internal signature (stored in byte code)
   * @return the class signature as an interface
   */
  public MethodSignature decodeMethodSignature(String signature)
  {
    SignatureReader sr = new SignatureReader(signature);

    MethodSignatureVisitor visitor = new MethodSignatureVisitor();

    try
    {
      sr.accept(visitor);
    }
    catch(Exception e)
    {
      throw new IllegalArgumentException("invalid signature " + signature, e);
    }

    return visitor;
  }
}

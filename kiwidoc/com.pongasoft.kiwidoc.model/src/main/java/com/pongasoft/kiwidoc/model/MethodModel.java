
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

package com.pongasoft.kiwidoc.model;

import com.pongasoft.kiwidoc.model.annotation.AnnotationValue;
import com.pongasoft.kiwidoc.model.resource.ResolvableResource;
import com.pongasoft.kiwidoc.model.resource.MethodResource;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.type.GenericTypeVariables;
import com.pongasoft.kiwidoc.model.type.PrimitiveType;
import com.pongasoft.kiwidoc.model.type.Type;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author yan@pongasoft.com
 */
public class MethodModel extends ClassEntryModel implements ClassMember
{
  public static final List<ParameterModel> NO_PARAMETERS = Collections.emptyList();
  public static final List<Type> NO_EXCEPTIONS = Collections.emptyList();
  public static final String CONSTRUCTOR_NAME = "<init>";

  private final MethodResource _methodResource;
  private final GenericTypeVariables _genericTypeVariables;
  private final ParametersModel _parameters;
  private final Type _returnType;
  private final Collection<Type> _exceptions;
  private final AnnotationValue _annotationDefaultValue;

  public MethodModel(int access,
                     MethodResource methodResource,
                     String name,
                     DocModel docModel,
                     AnnotationsModel annotations,
                     GenericTypeVariables genericTypeVariables,
                     ParametersModel parameters,
                     Type returnType,
                     Collection<Type> exceptions,
                     AnnotationValue annotationDefaultValue)
  {
    super(access, name, docModel, annotations);
    _methodResource = methodResource;
    _genericTypeVariables = genericTypeVariables;
    _parameters = parameters;
    _returnType = returnType;
    _exceptions = exceptions;
    _annotationDefaultValue = annotationDefaultValue;
  }

  public MethodModel(ClassEntryModel cem,
                     MethodResource methodResource,
                     GenericTypeVariables genericTypeVariables,
                     ParametersModel parameters,
                     Type returnType,
                     Collection<Type> exceptions,
                     AnnotationValue annotationDefaultValue)
  {
    this(cem.getAccess(),
         methodResource,
         cem.getName(),
         cem.getDoc(),
         cem.getAnnotationsModel(),
         genericTypeVariables,
         parameters,
         returnType,
         exceptions,
         annotationDefaultValue);
  }

  /**
   * @return the unique member name in the class
   */
  public String getMemberName()
  {
    return _methodResource.getMethodName();
  }

  public MethodResource getResource()
  {
    return _methodResource;
  }

  public GenericTypeVariables getGenericTypeVariables()
  {
    return _genericTypeVariables;
  }

  public List<ParameterModel> getParameters()
  {
    return _parameters.getParameters();
  }

  public ParametersModel getParametersModel()
  {
    return _parameters;
  }

  public boolean isVarargs()
  {
    return is(Access.VARARGS);
  }

  public boolean isAnnotationMethod()
  {
    return is(Access.ANNOTATION); 
  }

  public Type getReturnType()
  {
    return _returnType;
  }

  public boolean isVoidReturnType()
  {
    if(_returnType != null)
    {
      if(_returnType instanceof PrimitiveType)
      {
        PrimitiveType primitiveType = (PrimitiveType) _returnType;
        return primitiveType.getPrimitive() == PrimitiveType.Primitive.VOID;
      }
    }
    
    return false;
  }

  public Collection<Type> getExceptions()
  {
    return _exceptions;
  }

  public AnnotationValue getAnnotationDefaultValue()
  {
    return _annotationDefaultValue;
  }

  public MethodResource getResource(ClassResource parent)
  {
    return new MethodResource(parent, getMemberName());
  }

  @Override
  public boolean containsOnlyPublicEntries()
  {
    if(!super.containsOnlyPublicEntries())
      return false;

    return _parameters.containsOnlyPublicEntries();
  }

  /**
   * This method returns a version of the method model containing only entries that are part of the
   * public api. If this method is not part of the public api itself, then it returns
   * <code>null</code>. If this method is entirely public, then it returns <code>this</code>.
   * Otherwise a new instance will be created and populated with only the public api entries.
   */
  public MethodModel toPublicAPI()
  {
    if(!isPublicAPI())
      return null;

    if(containsOnlyPublicEntries())
      return this;

    return new MethodModel(super.toPubliAPI(),
                           getResource(),
                           getGenericTypeVariables(),
                           getParametersModel().toPublicAPI(),
                           getReturnType(),
                           getExceptions(),
                           getAnnotationDefaultValue());
  }

  /**
   * Assigns the generics to the generic variables (ex: E =&gt; Number)
   * @return the new model (or <code>this</code> if no generics...)
   */
  public MethodModel assignGenericTypeVariable(String name, Type value)
  {
    if(is(Access.STATIC))
      return this;

    boolean changed = false;
    GenericTypeVariables newGTVS = _genericTypeVariables.assignGenericTypeVariable(name, value);
    changed = newGTVS != _genericTypeVariables;

    Type newReturnType = _returnType;
    if(newReturnType != null)
      newReturnType = newReturnType.assignGenericTypeVariable(name, value);
    changed |= newReturnType != _returnType;

    ParametersModel newParameters = _parameters.assignGenericTypeVariable(name, value);
    changed |= newParameters != _parameters;

    if(changed)
      return new MethodModel(this,
                             _methodResource,
                             newGTVS,
                             newParameters,
                             newReturnType,
                             _exceptions,
                             getAnnotationDefaultValue());
    else
      return this;
  }
  
  /**
   * Collect the dependencies in the collection.
   *
   * @param dependencies to add the dependencies to it
   */
  @Override
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    super.collectDependencies(dependencies);
    dependencies.add(_methodResource);
    if(_genericTypeVariables != null)
      _genericTypeVariables.collectDependencies(dependencies);
    if(_returnType != null)
      _returnType.collectDependencies(dependencies);
    for(Type exception : _exceptions)
    {
      exception.collectDependencies(dependencies);
    }
    _parameters.collectDependencies(dependencies);
  }

  public boolean isConstructor()
  {
    return getReturnType() == null;
  }

  public boolean isStatic()
  {
    return is(Access.STATIC);
  }

  @Override
  public String toString()
  {
    return getMemberName();
  }
}
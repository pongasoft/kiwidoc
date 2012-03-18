
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

package com.pongasoft.kiwidoc.builder;

import com.pongasoft.kiwidoc.model.ClassModel;
import com.pongasoft.kiwidoc.model.DependenciesModel;
import com.pongasoft.kiwidoc.model.InheritanceModel;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.type.AssignedGenericTypeVariable;
import com.pongasoft.kiwidoc.model.type.GenericType;
import com.pongasoft.kiwidoc.model.type.GenericTypeVariable;
import com.pongasoft.kiwidoc.model.type.GenericTypeVariables;
import com.pongasoft.kiwidoc.model.type.GenericVariable;
import com.pongasoft.kiwidoc.model.type.SimpleType;
import com.pongasoft.kiwidoc.model.type.Type;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author yan@pongasoft.com
 */
public class InheritanceDependencyResolver
{
  public static final Log log = LogFactory.getLog(InheritanceDependencyResolver.class);

  private final KiwidocLibraryStore _store;
  private final DependencyResolver<ClassModel> _dependencyResolver;
  private final ClassModel _classModel;
  private final HashMap<ClassResource, InheritanceModel> _resolvedModels =
    new HashMap<ClassResource, InheritanceModel>();

  private InheritanceModel _inheritanceModel;

  /**
   * Constructor
   */
  public InheritanceDependencyResolver(KiwidocLibraryStore store,
                                       DependenciesModel dependencies,
                                       ClassModel classModel)
  {
    _store = store;
    _classModel = classModel;
    _dependencyResolver = new DependencyResolver<ClassModel>(_store, dependencies, _classModel);
  }

  /**
   * Resolves the inheritance tree and returns it as a model. All resolution should have happen,
   * whatever cannot be resolved will remain unresolved....
   */
  public InheritanceModel resolve() throws StoreException
  {
    if(_inheritanceModel != null)
      return _inheritanceModel;

    _inheritanceModel = doResolve(_dependencyResolver);

    return _inheritanceModel;
  }

  /**
   * Recursive call which will resolve superclass and interfaces of the class provided
   * in the dependency resolver.
   */
  private InheritanceModel doResolve(DependencyResolver<ClassModel> dependencyResolver)
    throws StoreException
  {
    ClassModel classModel = dependencyResolver.resolve();

    InheritanceModel superclass = null;
    Collection<InheritanceModel> interfaces = new ArrayList<InheritanceModel>();

    // superclass
    GenericType superType = classModel.getSuperClass();
    if(superType != null)
    {
      if(!_resolvedModels.containsKey(superType.getClassResource()))
      {
        superclass = doResolve(dependencyResolver, classModel, superType);
        // if we cannot resolve the super class, we add object as a parent...
        if(superclass.getBaseClass() == null)
        {
          superclass =
            new InheritanceModel(dependencyResolver.getObjectResource(),
                                 doResolve(dependencyResolver,
                                           classModel,
                                           SimpleType.create(dependencyResolver.getObjectResource())),
                                 Collections.<InheritanceModel>emptyList());
        }
      }
    }

    for(GenericType ifaceType : classModel.getInterfaces())
    {
      if(!_resolvedModels.containsKey(ifaceType.getClassResource()))
        interfaces.add(doResolve(dependencyResolver, classModel, ifaceType));
    }

    InheritanceModel inheritanceModel = new InheritanceModel(classModel, superclass, interfaces);

    _resolvedModels.put(classModel.getResource(), inheritanceModel);
    
    return inheritanceModel;
  }

  /**
   * Each class (super or interface) is resolved using the dependency resolver of the base class
   * that way we inherit the 'classpath'...
   */
  private InheritanceModel doResolve(DependencyResolver<ClassModel> dependencyResolver,
                                     ClassModel baseType,
                                     GenericType type)
    throws StoreException
  {
    ClassResource classResource = type.getClassResource();

    InheritanceModel inheritanceModel = _resolvedModels.get(classResource);

    if(inheritanceModel == null && classResource.isResolved())
    {
      ClassModel classModel = loadModel(baseType, type);

      if(classModel != null)
      {
        inheritanceModel = doResolve(dependencyResolver.newResolver(classModel));
      }
    }

    if(inheritanceModel == null)
    {
      inheritanceModel =
        new InheritanceModel(classResource, null, Collections.<InheritanceModel>emptyList());
    }

    return inheritanceModel;
  }

  private ClassModel loadModel(ClassModel baseType, GenericType type)
    throws StoreException
  {
    ClassResource resource = type.getClassResource();
    InheritanceModel inheritanceModel = _resolvedModels.get(resource);
    if(inheritanceModel == null)
    {
      try
      {
        ClassModel classModel = _store.loadContent(resource);
        classModel = classModel.assignGenericTypeVariables(computeGenerics(baseType, type));
        return classModel;
      }
      catch(NoSuchContentException e)
      {
        // most likely a race condition...
        if(log.isDebugEnabled())
          log.debug("resource not found: " + resource);
      }
    }

    if(inheritanceModel != null)
      return inheritanceModel.getBaseClass();
    else
      return null;
  }

  private List<? extends Type> computeGenerics(ClassModel baseType, GenericType type)
  {
    List<? extends Type> typeGenerics = type.getLastPart().getGenerics();
    if(typeGenerics.isEmpty())
      return typeGenerics;

    List<Type> res = new ArrayList<Type>();

    for(Type typeGeneric : typeGenerics)
    {
      if(typeGeneric instanceof GenericVariable)
      {
        GenericVariable gtv = (GenericVariable) typeGeneric;
        typeGeneric = findAssignedGenericType(baseType.getGenericTypeVariables(),
                                              typeGeneric,
                                              gtv.getName());
      }

      res.add(typeGeneric);
    }

    return res;
  }

  private Type findAssignedGenericType(GenericTypeVariables gtvs,
                                       Type defaultTypeGeneric,
                                       String name)
  {
    GenericTypeVariable gtv = gtvs.findGenericTypeVariable(name);

    if(gtv != null)
    {
      if(gtv instanceof AssignedGenericTypeVariable)
      {
        AssignedGenericTypeVariable agtv = (AssignedGenericTypeVariable) gtv;
        return agtv.getAssignedType();
      }
    }

    return defaultTypeGeneric;
  }
}

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

import com.pongasoft.kiwidoc.model.resource.ClassResource;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * @author yan@pongasoft.com
 */
public class InheritanceModel
{
  public static final Collection<Entry> NO_INTERFACES = Collections.emptyList();

  /**
   * If the resolution does not work, then the base class will be <code>null</code> but not the
   * resource
   */
  public static class Entry
  {
    private final ClassModel _baseClass;
    private final ClassResource _baseResource;

    public Entry(ClassResource baseResource)
    {
      _baseClass = null;
      _baseResource = baseResource;
    }

    public Entry(ClassModel baseClass)
    {
      _baseClass = baseClass;
      _baseResource = baseClass.getResource();
    }

    public ClassModel getBaseClass()
    {
      return _baseClass;
    }

    public ClassResource getBaseResource()
    {
      return _baseResource;
    }

    public Entry toPublicAPI()
    {
      ClassModel baseClass = _baseClass;
      
      if(baseClass != null)
        baseClass = baseClass.toPublicAPI();

      if(baseClass != null)
        return new Entry(baseClass);
      else
        return null;
    }
  }

  private final Entry _entry;
  private final InheritanceModel _superClass;
  private final Collection<InheritanceModel> _interfaces;

  private volatile Collection<Entry> _flattenInterfaces = null;
  private volatile Collection<Entry> _flattenSuperClasses = null;


  /**
   * Constructor
   */
  public InheritanceModel(ClassModel baseClass,
                          InheritanceModel superClass,
                          Collection<InheritanceModel> interfaces)
  {
    _entry = new Entry(baseClass);
    _superClass = superClass;
    _interfaces = interfaces;
  }

  /**
   * Constructor
   */
  public InheritanceModel(ClassResource baseResource,
                          InheritanceModel superClass,
                          Collection<InheritanceModel> interfaces)
  {
    _entry = new Entry(baseResource);
    _superClass = superClass;
    _interfaces = interfaces;
  }

  private InheritanceModel(Entry entry,
                           InheritanceModel superClass,
                           Collection<InheritanceModel> interfaces)
  {
    _entry = entry;
    _superClass = superClass;
    _interfaces = interfaces;
  }

  public boolean isPublicAPI()
  {
    return _entry != null && _entry.getBaseClass() != null && _entry.getBaseClass().isPublicAPI();
  }

  public ClassModel getBaseClass()
  {
    return _entry.getBaseClass();
  }

  public ClassResource getBaseResource()
  {
    return _entry.getBaseResource();
  }

  public Entry getEntry()
  {
    return _entry;
  }

  public InheritanceModel getSuperClass()
  {
    return _superClass;
  }

  public Collection<InheritanceModel> getInterfaces()
  {
    return _interfaces;
  }

  public boolean isRoot()
  {
    return _superClass == null && (_interfaces == null || _interfaces.isEmpty());
  }

  /**
   * @return all the interfaces that are in the hierarchy flattened
   */
  public Collection<Entry> flattenInterfaces()
  {
    if(_flattenInterfaces == null)
    {
      Collection<Entry> interfaces = new ArrayList<Entry>();

      if(_interfaces != null)
      {
        // add 1st level
        for(InheritanceModel interfaceModel : _interfaces)
        {
          Entry entry = interfaceModel.getEntry();
          if(entry != null)
            interfaces.add(entry);
        }

        // add other levels
        for(InheritanceModel interfaceModel : _interfaces)
        {
          interfaces.addAll(interfaceModel.flattenInterfaces());
        }
      }

      // add super class
      if(_superClass != null)
      {
        interfaces.addAll(_superClass.flattenInterfaces());
      }

      // now we remove duplicates...
      Collection<ClassResource> classResources = new HashSet<ClassResource>();

      Iterator<Entry> iter = interfaces.iterator();
      while(iter.hasNext())
      {
        Entry entry = iter.next();
        if(entry != null)
        {
          if(classResources.contains(entry.getBaseResource()))
          {
            iter.remove();
          }
          else
          {
            classResources.add(entry.getBaseResource());
          }
        }
      }

      _flattenInterfaces = interfaces;
    }

    return _flattenInterfaces;
  }

  /**
   * @return all the superclasses in the hierarchy in reverse order of inheritance (meaning the
   * last one should always be <code>Object</code> unless something cannot resolve...)
   */
  public Collection<Entry> flattenSuperClasses()
  {
    if(_flattenSuperClasses == null)
    {
      Collection<Entry> superClasses = new ArrayList<Entry>();

      InheritanceModel model = _superClass;

      while(model != null)
      {
        Entry entry = model.getEntry();
        if(entry != null)
        {
          superClasses.add(entry);
        }

        model = model.getSuperClass();
      }
      
      _flattenSuperClasses = superClasses;
    }

    return _flattenSuperClasses;
  }

  /**
   * This method returns a version of the model containing only entries that are part of the
   * public api. Note that the hierarch may be rewritten to suit the contract (ex: <code>StringBuilder</code>
   * inherits from <code>AbstractStringBuilder</code> which inherits from <code>Object</code>... and
   * <code>AbstractStringBuilder</code> is not part of the public api!
   */
  public InheritanceModel toPublicAPI()
  {
    Collection<InheritanceModel> interfaces = null;
    if(_interfaces != null)
    {
      interfaces = new ArrayList<InheritanceModel>(_interfaces.size());
      for(InheritanceModel model : _interfaces)
      {
        interfaces.add(model.toPublicAPI());
      }
    }

    InheritanceModel superClass = _superClass;
    if(superClass != null)
      superClass = superClass.toPublicAPI();

    Entry entry = _entry;
    if(entry != null)
      entry = entry.toPublicAPI();

    return new InheritanceModel(entry, superClass, interfaces); 
  }

  /**
   * Find all methods that the provided methods inherits from.
   */
  public Collection<MethodModel> findInheritedMethod(MethodModel method)
  {
    Collection<MethodModel> inheritedMethods = new ArrayList<MethodModel>();

    collectMethods(inheritedMethods, method, flattenInterfaces());
    collectMethods(inheritedMethods, method, flattenSuperClasses());

    return inheritedMethods;
  }

  /**
   * @return all methods including inherited ones (unique names only)
   */
  public Collection<MethodModel> getAllMethods()
  {
    Collection<MethodModel> allMethods = new ArrayList<MethodModel>();

    Set<String> uniqueMethodNames = new HashSet<String>();

    // collect all methods from the base class 1st
    collectMethods(allMethods, uniqueMethodNames, getBaseClass(), false);
    // then all methods inherited
    collectMethods(allMethods, uniqueMethodNames, flattenInterfaces(), true);
    collectMethods(allMethods, uniqueMethodNames, flattenSuperClasses(), true);

    return allMethods;
  }

  private void collectMethods(Collection<MethodModel> allMethods,
                              Set<String> uniqueMethodNames,
                              Collection<Entry> entries,
                              boolean filterMethods)
  {
    for(Entry entry : entries)
    {
      collectMethods(allMethods, uniqueMethodNames, entry.getBaseClass(), filterMethods);
    }
  }

  private void collectMethods(Collection<MethodModel> allMethods,
                              Set<String> uniqueMethodNames,
                              ClassModel classModel,
                              boolean filterMethods)
  {
    if(classModel != null)
    {
      for(MethodModel methodModel : classModel.getAllMethods())
      {
        if(!filterMethods || filterMethod(methodModel))
        {
          String memberName = methodModel.getMemberName();
          if(!uniqueMethodNames.contains(memberName))
          {
            uniqueMethodNames.add(memberName);
            allMethods.add(methodModel);
          }
        }
      }
    }
  }

  /**
   * Find all the interfaces specifying the method
   */
  public Collection<ClassModel> findInterfacesSpecifying(MethodModel method)
  {
    return collectClasses(new ArrayList<ClassModel>(),
                          method,
                          flattenInterfaces());
  }

  /**
   * Find all the superclasses specifying the method
   */
  public Collection<ClassModel> findSuperClassesImplementing(MethodModel method)
  {
    return collectClasses(new ArrayList<ClassModel>(),
                          method,
                          flattenSuperClasses());
  }

  /**
   * Find the class or interface implementing the method
   */
  public ClassModel findClassImplementing(MethodModel method)
  {
    ClassModel classModel = findClass(method, flattenSuperClasses());
    if(classModel == null)
      classModel = findClass(method, flattenInterfaces());
    return classModel;
  }

  private void collectMethods(Collection<MethodModel> inheritedMethods,
                              MethodModel method,
                              Collection<Entry> collection)
  {
    for(Entry entry : collection)
    {
      ClassModel model = entry.getBaseClass();
      if(model != null)
      {
        MethodModel inheritedMethod = model.findMethod(method.getMemberName());
        if(isInheritedMethod(inheritedMethod))
        {
          inheritedMethods.add(inheritedMethod);
        }
      }
    }
  }

  private boolean filterMethod(MethodModel method)
  {
    if(method == null)
      return false;

    // no static or constructor...
    if(method.is(BaseEntryModel.Access.STATIC) ||
       method.isConstructor())
      return false;

    // public only
    if(method.is(BaseEntryModel.Access.PUBLIC))
      return true;

    return false;
  }

  private boolean isInheritedMethod(MethodModel method)
  {
    if(method == null)
      return false;

    // don't inherit static or final methods or constructor...
    if(method.is(BaseEntryModel.Access.STATIC) ||
       method.is(BaseEntryModel.Access.FINAL) || 
       method.isConstructor())
      return false;

    // no inherit from private
    if(method.is(BaseEntryModel.Access.PRIVATE))
      return false;

    // inherit public
    if(method.is(BaseEntryModel.Access.PUBLIC) || method.is(BaseEntryModel.Access.PROTECTED))
      return true;

    // here it means the method is package protected => inherit only if same package!
    return method.getResource().getPackageName().equals(getBaseResource().getPackageName());
  }

  private Collection<ClassModel> collectClasses(Collection<ClassModel> classes,
                                                MethodModel method,
                                                Collection<Entry> collection)
  {
    for(Entry entry : collection)
    {
      ClassModel model = entry.getBaseClass();
      if(model != null)
      {
        MethodModel inheritedMethod = model.findMethod(method.getMemberName());
        if(inheritedMethod != null)
          classes.add(model);
      }
    }

    return classes;
  }

  private ClassModel findClass(MethodModel method, Collection<Entry> entries)
  {
    for(Entry entry : entries)
    {
      ClassModel model = entry.getBaseClass();
      if(model != null)
      {
        MethodModel inheritedMethod = model.findMethod(method.getMemberName());
        if(inheritedMethod != null)
          return model;
      }
    }

    return null;
  }

  /**
   * This method returns a new model where the inheriting of javadoc has been processed.
   * @see <a href="http://java.sun.com/javase/6/docs/technotes/tools/solaris/javadoc.html">javadoc - The Java API Documentation Generator</a>
   */
  public InheritanceModel inheritDoc()
  {
    if(isRoot())
      return this;
    
    Collection<InheritanceModel> interfaces = null;
    if(_interfaces != null)
    {
      if(_interfaces.size() != 0)
      {
        boolean changed = false;
        interfaces = new ArrayList<InheritanceModel>(_interfaces.size());
        for(InheritanceModel model : _interfaces)
        {
          InheritanceModel newModel = model.inheritDoc();
          changed |= newModel != model;
          interfaces.add(newModel);
        }
        
        if(!changed)
          interfaces = _interfaces;
      }
      else
      {
        interfaces = _interfaces;
      }
    }

    InheritanceModel superClass = _superClass;
    if(superClass != null)
      superClass = superClass.inheritDoc();

    Entry entry = _entry;
    if(entry != null)
    {
      InheritanceModel inheritanceModel = this;
      if(superClass != _superClass || interfaces != _interfaces)
      {
        inheritanceModel = new InheritanceModel(_entry, superClass, interfaces);
      }
      entry = inheritDoc(entry, inheritanceModel);
    }

    if(entry != _entry || superClass != _superClass || interfaces != _interfaces)
      return new InheritanceModel(entry, superClass, interfaces);
    else
      return this;
  }

  private Entry inheritDoc(Entry entry, InheritanceModel inheritanceModel)
  {
    ClassModel model = entry.getBaseClass();
    if(model != null)
    {
      ClassModel newModel = model.inheritDoc(inheritanceModel);
      if(newModel != model)
        return new Entry(newModel);
    }
    
    return entry;
  }
}

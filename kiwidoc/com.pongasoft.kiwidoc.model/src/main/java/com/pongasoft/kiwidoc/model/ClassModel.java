
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

import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.kiwidoc.model.type.GenericTypeVariables;
import com.pongasoft.kiwidoc.model.type.Type;
import com.pongasoft.kiwidoc.model.type.GenericType;
import com.pongasoft.kiwidoc.model.type.TypePart;
import com.pongasoft.kiwidoc.model.type.GenericTypeVariable;
import com.pongasoft.kiwidoc.model.type.GenericVariable;
import com.pongasoft.kiwidoc.model.type.AssignedGenericTypeVariable;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.ResolvableResource;
import com.pongasoft.kiwidoc.model.resource.MethodResource;
import com.pongasoft.util.core.enums.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author yan@pongasoft.com
 */
public class ClassModel extends ClassEntryModel implements Model<ClassResource>, ResolvableModel
{
  public static final Log log = LogFactory.getLog(ClassModel.class);

  public static final List<MethodModel> NO_METHODS = Collections.emptyList();
  public static final List<FieldModel> NO_FIELDS = Collections.emptyList();
  public static final List<Type> NO_INTERFACES = Collections.emptyList();

  public static enum ClassKind
  {
    @Value("C") CLASS,
    @Value("I") INTERFACE,
    @Value("A") ANNOTATION,
    @Value("E") ERROR,
    @Value("X") EXCEPTION,
    @Value("B") ENUM
  }

  private final ClassResource _classResource;
  private final ClassKind _classKind;
  private final GenericTypeVariables _genericTypeVariables;
  private final ClassDefinitionModel _outerClass;
  private final Map<String, ClassDefinitionModel> _innerClasses;
  private final Collection<ClassDefinitionModel> _allLibrarySubclasses;
  private final GenericType _superclass;
  private final Collection<GenericType> _interfaces;
  private final Map<String, MethodModel> _allMethods;
  private final Map<String, List<MethodModel>> _methodsByName;
  private final Map<String, FieldModel> _fields;

  private volatile transient GenericType _genericType = null;

  public ClassModel(ClassResource classResource,
                    int access,
                    DocModel docModel,
                    AnnotationsModel annotations,
                    ClassKind classKind,
                    GenericTypeVariables genericTypeVariables,
                    GenericType superclass,
                    Collection<GenericType> interfaces,
                    Collection<MethodModel> methods,
                    Collection<FieldModel> fields,
                    ClassDefinitionModel outerClass,
                    Collection<ClassDefinitionModel> innerClasses,
                    Collection<ClassDefinitionModel> allLibrarySubclasses)
  {
    super(access, classResource.getFqcn(), docModel, annotations);
    _classResource = classResource;
    _classKind = classKind;
    _genericTypeVariables = genericTypeVariables;
    _outerClass = outerClass;
    _allLibrarySubclasses = allLibrarySubclasses == null ? Collections.<ClassDefinitionModel>emptyList() : allLibrarySubclasses;
    _innerClasses = new TreeMap<String, ClassDefinitionModel>();
    for(ClassDefinitionModel innerClass : innerClasses)
    {
      _innerClasses.put(innerClass.getName(), innerClass);
    }
    _superclass = superclass;
    _interfaces = interfaces;
    _allMethods = new TreeMap<String, MethodModel>();
    _methodsByName = new TreeMap<String, List<MethodModel>>();
    for(MethodModel method : methods)
    {
      _allMethods.put(method.getMemberName(), method);
      List<MethodModel> list = _methodsByName.get(method.getName());
      if(list == null)
      {
        list = new ArrayList<MethodModel>();
        _methodsByName.put(method.getName(), list);
      }
      list.add(method);
    }

    _fields = new TreeMap<String, FieldModel>();
    for(FieldModel field : fields)
    {
      _fields.put(field.getMemberName(), field);
    }
  }

  public ClassModel(ClassEntryModel cem,
                    LibraryVersionResource libraryVersionResource,
                    ClassKind classKind,
                    GenericTypeVariables genericTypeVariables,
                    GenericType superclass,
                    Collection<GenericType> interfaces,
                    Collection<MethodModel> methods,
                    Collection<FieldModel> fields,
                    ClassDefinitionModel outerClass,
                    Collection<ClassDefinitionModel> innerClasses,
                    Collection<ClassDefinitionModel> allLibrarySubclasses)
  {
    this(new ClassResource(libraryVersionResource, cem.getName()),
         cem.getAccess(),
         cem.getDoc(),
         cem.getAnnotationsModel(),
         classKind,
         genericTypeVariables,
         superclass,
         interfaces,
         methods,
         fields,
         outerClass,
         innerClasses,
         allLibrarySubclasses);
  }

  /**
   * @return the resource
   */
  public ClassResource getResource()
  {
    return _classResource;
  }

  /**
   * @return the model kind
   */
  public Model.Kind getKind()
  {
    return Model.Kind.CLASS;
  }

  @Override
  public Collection<? extends Resource> getChildren()
  {
    return Collections.emptyList();
  }

  public String getFQCN()
  {
    return getName();
  }

  public ClassKind getClassKind()
  {
    return _classKind;
  }

  public String getClassKindString()
  {
    if(is(Access.ENUM))
      return "Enum";

    if(is(Access.ANNOTATION))
      return "Annotation";

    if(is(Access.INTERFACE))
      return "Interface";

    switch(getClassKind())
    {
      case ERROR:
        return "Error";

      case EXCEPTION:
        return "Exception";

      default:
        return "Class";
    }
  }

  public GenericType getType()
  {
    if(_genericType == null)
    {
      List<TypePart> typeParts = new ArrayList<TypePart>();
      if(getOuterClass() != null)
      {
        typeParts.addAll(getOuterClass().getType().getTypeParts());
      }
      List<GenericTypeVariable> gtvs = _genericTypeVariables.getGenericTypeVariables();
      List<Type> generics = new ArrayList<Type>(gtvs.size());
      for(GenericTypeVariable gtv : gtvs)
      {
        if(gtv instanceof AssignedGenericTypeVariable)
        {
          AssignedGenericTypeVariable agtv = (AssignedGenericTypeVariable) gtv;
          generics.add(agtv.getAssignedType());
        }
        else
        {
          generics.add(new GenericVariable(gtv.getName()));
        }
      }
      typeParts.add(new TypePart(_classResource, generics));
      _genericType = GenericType.create(typeParts);
    }

    return _genericType;
  }

  public boolean isEnum()
  {
    return is(Access.ENUM) || getClassKind() == ClassKind.ENUM;
  }

  public boolean isAnnotation()
  {
    return is(Access.ANNOTATION) || getClassKind() == ClassKind.ANNOTATION;
  }

  public String getSimpleName()
  {
    return getResource().getSimpleClassName();
  }

  /**
   * @return <code>null</code> if it is the default package
   */
  public String getPackageName()
  {
    return getResource().getPackageName();
  }

  public GenericTypeVariables getGenericTypeVariables()
  {
    return _genericTypeVariables;
  }

  public GenericType getSuperClass()
  {
    return _superclass;
  }

  public Collection<GenericType> getInterfaces()
  {
    return _interfaces;
  }

  public Set<String> getFieldsMemberNames()
  {
    return Collections.unmodifiableSet(_fields.keySet());
  }

  /**
   * @return the field given its name (<code>null</code> if not found)
   */
  public FieldModel findField(String fieldName)
  {
    return _fields.get(fieldName);
  }

  /**
   * @return all the fields
   */
  public Collection<FieldModel> getAllFields()
  {
    return Collections.unmodifiableCollection(_fields.values());
  }

  /**
   * @return all the enum constants
   */
  public Collection<FieldModel> getAllEnumConstants()
  {
    if(isEnum())
    {
      Collection<FieldModel> res = new ArrayList<FieldModel>();
      for(FieldModel fieldModel : _fields.values())
      {
        if(fieldModel.isEnumConstant())
          res.add(fieldModel);
      }
      return res;
    }
    else
    {
      return Collections.emptyList();
    }
  }

  /**
   * @return all the non enum constants
   */
  public Collection<FieldModel> getAllNonEnumConstants()
  {
    if(isEnum())
    {
      Collection<FieldModel> res = new ArrayList<FieldModel>();
      for(FieldModel fieldModel : _fields.values())
      {
        if(!fieldModel.isEnumConstant())
          res.add(fieldModel);
      }
      return res;
    }
    else
    {
      return getAllFields();
    }
  }

  /**
   * @return the set of all the method names (using the unique member name)
   */
  public Set<String> getMethodsMemberNames()
  {
    return Collections.unmodifiableSet(_allMethods.keySet());
  }

  /**
   * @return the method given its unique member name (<code>null</code> if not found)
   */
  public MethodModel findMethod(String memberName)
  {
    return _allMethods.get(memberName);
  }

  /**
   * @return the method given its unique member name (<code>null</code> if not found)
   */
  public MethodResource findMethodResource(String memberName)
  {
    MethodModel model = findMethod(memberName);
    if(model != null)
      return new MethodResource(_classResource, memberName);
    else
      return null;
  }

  /**
   * @return all methods that have the same name or <code>null</code> if not found
   * (due to overloading, there can be several methods with the same name but with different 
   * arguments (they would have different member names))
   */
  public Collection<MethodModel> findMethods(String methodName)
  {
    List<MethodModel> list = _methodsByName.get(methodName);
    if(list != null)
      list = Collections.unmodifiableList(list);
    return list;
  }

  /**
   * @return all the methods
   */
  public Collection<MethodModel> getAllMethods()
  {
    return Collections.unmodifiableCollection(_allMethods.values());
  }

  /**
   * @return the class member given the uniquely identifying member name
   */
  public ClassMember findMember(String memberName)
  {
    if(memberName == null)
      return null;

    if(memberName.contains("("))
      return findMethod(memberName);
    else
      return findField(memberName);
  }

  /**
   * @return the outer class (or <code>null</code> if not an inner class)
   */
  public ClassDefinitionModel getOuterClass()
  {
    return _outerClass;
  }

  /**
   * @return the inner class (by name) or <code>null</code> if not found
   */
  public ClassDefinitionModel findInnerClass(String innerClassName)
  {
    return _innerClasses.get(innerClassName);
  }

  /**
   * @return all inner classes
   */
  public Map<String, ClassDefinitionModel> getInnerClasses()
  {
    return Collections.unmodifiableMap(_innerClasses);
  }

  /**
   * @return all subclasses from this library
   */
  public Collection<ClassDefinitionModel> getAllLibrarySubclasses()
  {
    return Collections.unmodifiableCollection(_allLibrarySubclasses);
  }

  /**
   * @return whether it is an exported class or not (meaning visible in javadoc / part of the api)
   *
   */
  public boolean isExportedClass()
  {
    return isExported();
  }

  @Override
  public boolean isPublicAPI()
  {
    return super.isPublicAPI() && isExportedClass();
  }

  /**
   * @return the library in which the model belongs
   */
  public LibraryVersionResource getLibraryVersionResource()
  {
    return getResource().getLibraryVersionResource();
  }

  /**
   * This method returns a version of the class model containing only entries that are part of the
   * public api. If this class is not part of the public api itself, then it returns
   * <code>null</code>. If this class is entirely public, then it returns <code>this</code>.
   * Otherwise a new instance will be created and populated with only the public api entries. 
   */
  public ClassModel toPublicAPI()
  {
    if(!isPublicAPI())
      return null;

    if(containsOnlyPublicEntryModels())
      return this;

    Collection<FieldModel> fields = new ArrayList<FieldModel>();
    for(FieldModel fieldModel : _fields.values())
    {
      fieldModel = fieldModel.toPublicAPI();
      if(fieldModel != null)
        fields.add(fieldModel);
    }

    Collection<MethodModel> methods = new ArrayList<MethodModel>();
    for(MethodModel methodModel : _allMethods.values())
    {
      methodModel = methodModel.toPublicAPI();
      if(methodModel != null)
        methods.add(methodModel);
    }

    Collection<ClassDefinitionModel> innerClasses = new ArrayList<ClassDefinitionModel>();
    for(ClassDefinitionModel innerClassModel : _innerClasses.values())
    {
      if(innerClassModel.isPublicAPI())
        innerClasses.add(innerClassModel);
    }

    Collection<ClassDefinitionModel> allKnownSubclasses = new ArrayList<ClassDefinitionModel>();
    for(ClassDefinitionModel allKnownSubclass : _allLibrarySubclasses)
    {
      if(allKnownSubclass.isPublicAPI())
        allKnownSubclasses.add(allKnownSubclass);
    }


    return new ClassModel(this,
                          getResource().getLibraryVersionResource(),
                          getClassKind(),
                          getGenericTypeVariables(),
                          getSuperClass(),
                          getInterfaces(),
                          methods,
                          fields,
                          getOuterClass(),
                          innerClasses,
                          allKnownSubclasses);
  }

  /**
   * Implements the algorithm described in {@link InheritanceModel#inheritDoc()}.
   * It is assumed that the inheritance model provided has already been recursively processed
   */
  public ClassModel inheritDoc(InheritanceModel inheritanceModel)
  {
    boolean changed = false;

    Collection<MethodModel> methods = new ArrayList<MethodModel>();
    for(MethodModel methodModel : _allMethods.values())
    {
      InheritDocHandler inheritDocHandler =
        new InheritDocHandler(inheritanceModel, _classResource, methodModel);

      MethodModel newMethodModel = inheritDocHandler.inheritDoc();
      changed |= newMethodModel != methodModel; 

      methods.add(newMethodModel);
    }

    if(changed)
      return new ClassModel(getResource(),
                            getAccess(),
                            getDoc(),
                            getAnnotationsModel(),
                            getClassKind(),
                            getGenericTypeVariables(),
                            getSuperClass(),
                            getInterfaces(),
                            methods,
                            getAllFields(),
                            getOuterClass(),
                            getInnerClasses().values(),
                            getAllLibrarySubclasses());
    else
      return this;
  }

  private boolean containsOnlyPublicEntryModels()
  {
    if(!containsOnlyPublicEntries())
      return false;

    for(FieldModel fieldModel : _fields.values())
    {
      if(!fieldModel.isPublicAPI() || !fieldModel.containsOnlyPublicEntries())
        return false;
    }

    for(MethodModel methodModel : _allMethods.values())
    {
      if(!methodModel.isPublicAPI() || !methodModel.containsOnlyPublicEntries())
        return false;
    }

    for(ClassDefinitionModel innerClassModel : _innerClasses.values())
    {
      if(!innerClassModel.isPublicAPI())
        return false;
    }

    for(ClassDefinitionModel allKnownSubclass : _allLibrarySubclasses)
    {
      if(!allKnownSubclass.isPublicAPI())
        return false;
    }
    
    return true;
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
    dependencies.add(_classResource);
    getGenericTypeVariables().collectDependencies(dependencies);
    if(_superclass != null)
      _superclass.collectDependencies(dependencies);
    for(Type type : _interfaces)
    {
      type.collectDependencies(dependencies);
    }

    for(FieldModel fieldModel : _fields.values())
    {
      fieldModel.collectDependencies(dependencies);
    }

    for(MethodModel methodModel : _allMethods.values())
    {
      methodModel.collectDependencies(dependencies);
    }

    if(_outerClass != null)
      _outerClass.collectDependencies(dependencies);
    
    for(ClassDefinitionModel innerClass : _innerClasses.values())
    {
      innerClass.collectDependencies(dependencies);
    }

    if(_allLibrarySubclasses != null)
    {
      for(ClassDefinitionModel subclass : _allLibrarySubclasses)
      {
        subclass.collectDependencies(dependencies);
      }
    }
  }

  /**
   * Assigns the generics to the generic variables (ex: E =&gt; Number)
   * @return the new model (or <code>this</code> if no generics...)
   */
  public ClassModel assignGenericTypeVariables(List<? extends Type> generics)
  {
    List<GenericTypeVariable> variables = _genericTypeVariables.getGenericTypeVariables();

    if(variables.size() != generics.size())
    {
      return this;
    }

    if(generics.isEmpty())
      return this;

    Map<String, Type> types = new HashMap<String, Type>();
    int len = generics.size();
    for(int i = 0; i < len; i++)
    {
      types.put(variables.get(i).getName(), generics.get(i));
    }
    
    GenericTypeVariables gtvs = _genericTypeVariables.assignGenerics(generics);

    Collection<MethodModel> methods = new ArrayList<MethodModel>();
    for(MethodModel methodModel : _allMethods.values())
    {
      for(Map.Entry<String, Type> entry : types.entrySet())
      {
        methodModel = methodModel.assignGenericTypeVariable(entry.getKey(), entry.getValue());
      }
      methods.add(methodModel);
    }

    Collection<FieldModel> fields = new ArrayList<FieldModel>();
    for(FieldModel fieldModel : fields)
    {
      for(Map.Entry<String, Type> entry : types.entrySet())
      {
        fieldModel = fieldModel.assignGenericTypeVariable(entry.getKey(), entry.getValue());
      }
      fields.add(fieldModel);
    }

    return new ClassModel(getResource(),
                          getAccess(),
                          getDoc(),
                          getAnnotationsModel(),
                          getClassKind(),
                          gtvs,
                          getSuperClass(),
                          getInterfaces(),
                          methods,
                          fields,
                          getOuterClass(),
                          getInnerClasses().values(),
                          getAllLibrarySubclasses());
  }

  /**
   * Computes the member name for a method. It is of the form
   * <code>methodName(erasure(params))</code>.
   * 
   * @return a member name which is unique in the namespace of a class
   */
  public static String computeMemberName(String methodName, Collection<ParameterModel> parameters)
  {
    StringBuilder sb = new StringBuilder(methodName);
    sb.append('(');
    for(ParameterModel parameter : parameters)
    {
      sb.append(parameter.getType().toErasureDescriptor());
    }
    sb.append(')');
    return sb.toString();
  }

  /**
   * Computes the simple name from the fully qualified class name
   */
  public static String computeSimpleName(String fqcn)
  {
    int idx = fqcn.lastIndexOf(".");
    if(idx == -1)
      return fqcn;
    else
      return fqcn.substring(idx + 1);
  }

  /**
   * Computes the package name from the fully qualified class name
   * @return "" if it is the default package
   */
  public static String computePackageName(String fqcn)
  {
    int idx = fqcn.lastIndexOf(".");
    if(idx == -1)
      return "";
    else
      return fqcn.substring(0, idx);
  }

  /**
   * Computes the internal name from the fully qualified class name (fqcn): replaces '.' with '/'
   *
   * @param internalName <code>null</code> is ok
   */
  public static String computeFQCN(String internalName)
  {
    if(internalName == null)
      return null;

    return internalName.replace('/', '.');
  }

  /**
   * Computes the fully qualified class name from the package name and the simple name
   */
  public static String computeFQCN(String packageName, String simpleName)
  {
    if(packageName == null || packageName.equals(""))
      return simpleName;
    
    return packageName + "." + simpleName;
  }

  /**
   * Computes the fully qualified class name (fqcn) from the internal name: replaces '/' with '.'
   *
   * @param fqcn <code>null</code>
   */
  public static String computeInternalName(String fqcn)
  {
    if(fqcn == null)
      return null;

    return fqcn.replace('.', '/');
  }
}
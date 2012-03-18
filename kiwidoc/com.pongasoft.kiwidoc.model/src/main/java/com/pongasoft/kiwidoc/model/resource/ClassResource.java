
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

package com.pongasoft.kiwidoc.model.resource;

import com.pongasoft.kiwidoc.model.ClassModel;
import com.pongasoft.kiwidoc.model.Model;

/**
 * @author yan@pongasoft.com
 */
public class ClassResource extends AbstractResource<ClassResource, PackageResource>
  implements ResolvableResource<ClassResource, PackageResource>
{
  private static final long serialVersionUID = 1L;

  private final String _simpleName;

  /**
   * Constructor
   */
  public ClassResource(LibraryVersionResource libraryVersionResource, String fqcn)
  {
    this(new PackageResource(libraryVersionResource, ClassModel.computePackageName(fqcn)),
         ClassModel.computeSimpleName(fqcn));
  }

  /**
   * Constructor
   */
  public ClassResource(PackageResource packageResource, String simpleName)
  {
    super(packageResource);
    if(packageResource == null || simpleName == null)
      throw new IllegalArgumentException("null not allowed");
    
    _simpleName = simpleName;
  }

  /**
   * @return the depth of the resource (root is 0, child of root is 1, etc...)
   */
  public int getDepth()
  {
    return 5;
  }

  /**
   * Constructor
   */
  public ClassResource(String fqcn)
  {
    this((LibraryVersionResource) null, fqcn);
  }

  public LibraryVersionResource getLibraryVersionResource()
  {
    return getPackageResource().getLibraryVersionResource();
  }

  public PackageResource getPackageResource()
  {
    return getParent();
  }

  public String getSimpleClassName()
  {
    return _simpleName;
  }

  public String getSimpleClassNameWithDot()
  {
    return withDot(getSimpleClassName());
  }

  /**
   * @return the name of the package
   */
  public String getPackageName()
  {
    return getPackageResource().getPackageName();
  }

  public String getName()
  {
    if(isInnerClass())
      return getInnerClassName();
    else
      return getSimpleClassName();
  }


  /**
   * Clones this resource using the parent provided.
   *
   * @param parent the parent to use
   * @return a new resource using the parent provided
   */
  public ClassResource clone(PackageResource parent)
  {
    return new ClassResource(parent, _simpleName);
  }

  /**
   * @return the name of the resource
   */
  public String getResourceName()
  {
    return getName();
  }

  /**
   * @return the version
   */
  public String getVersion()
  {
    return getPackageResource().getVersion();
  }

  /**
   * @return the library in which this versionable resource resides
   */
  public LibraryResource getLibraryResource()
  {
    return getPackageResource().getLibraryResource();
  }

  /**
   * @return the model kind associated to this resource
   */
  public Model.Kind getModelKind()
  {
    return Model.Kind.CLASS;
  }

  /**
   * Creates a resource that represents an inner class of this class resource.
   *
   * @param innerClassName the name of the inner class
   * @return the new resource representing the inner class
   */
  public ClassResource createInnerClass(String innerClassName)
  {
    return new ClassResource(getParent(), _simpleName + "$" + innerClassName);
  }

  public String getFqcn()
  {
    return ClassModel.computeFQCN(getPackageResource().getPackageName(), _simpleName);
  }

  public String getFqcnWithDot()
  {
    return withDot(getFqcn());
  }

  public boolean isResolved()
  {
    return getPackageResource().isResolved();
  }

  public void resolve(LibraryVersionResource libraryVersionResource)
  {
    getPackageResource().resolve(libraryVersionResource);
  }

  /**
   * @return <code>true</code> if this class resource represents an inner class
   */
  public boolean isInnerClass()
  {
    return _simpleName.contains("$");
  }

  /**
   * @return the name of the inner class or <code>null</code> if not an inner class
   */
  public String getInnerClassName()
  {
    return getInnerClassName(_simpleName);
  }

  /**
   * If it is an inner class, returns the outer class resource
   * 
   * @return <code>null</code> if not an inner class...
   */
  public ClassResource getOuterClassResource()
  {
    int idx = _simpleName.lastIndexOf('$');
    if(idx == -1)
      return null;
    else
      return new ClassResource(getPackageResource(), _simpleName.substring(0, idx));
  }

  /**
   * @return the name of the inner class or <code>null</code> if not an inner class
   */
  public static String getInnerClassName(String name)
  {
    int idx = name.lastIndexOf('$');
    if(idx == -1)
      return null;
    else
      return name.substring(idx + 1);
  }

  @Override
  public boolean equals(Object o)
  {
    if(this == o) return true;
    if(o == null || getClass() != o.getClass()) return false;
    if(!super.equals(o)) return false;

    ClassResource that = (ClassResource) o;

    if(!_simpleName.equals(that._simpleName)) return false;

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + _simpleName.hashCode();
    return result;
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder(getParent().toString());
    sb.append("/c/").append(_simpleName);
    return sb.toString();
  }

  /**
   * Handles nested classes by using '$' (like what appears in stack traces)
   */
  public static String computeFQCN(String packageName, String typeName)
  {
    return packageName + "." + typeName.replace('.', '$');
  }

  /**
   * @return the internal type name (ex: <code>java/lang/Object</code>)
   */
  public static String computeInternalName(String fqcn)
  {
    fqcn = fqcn.replace('.', '/');
    fqcn = withDot(fqcn);
    return fqcn;
  }

  /**
   * @return the fully qualified class name (ex: <code>java.lang.Object</code>)
   */
  public static String computeFQCNFromInternalName(String internalName)
  {
    internalName = internalName.replace('.', '$');
    return internalName.replace('/', '.');
  }

  /**
   * @param internalName
   * @return the class resource from the internal name
   */
  public static ClassResource fromInternalName(String internalName)
  {
    return new ClassResource(computeFQCNFromInternalName(internalName));
  }

  public static String withDot(String name)
  {
    if(name == null)
      return name;
    return name.replace('$', '.');
  }
}

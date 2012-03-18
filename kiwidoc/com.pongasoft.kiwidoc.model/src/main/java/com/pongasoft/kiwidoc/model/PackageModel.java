
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
import com.pongasoft.kiwidoc.model.resource.PackageResource;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.ResolvableResource;
import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yan@pongasoft.com
 */
public class PackageModel extends BaseEntryModel implements Model<PackageResource>, ResolvableModel
{
  private final static int NOT_A_CLASS =
    BaseEntryModel.Access.or(BaseEntryModel.Access.ANNOTATION,
                             BaseEntryModel.Access.ENUM,
                             BaseEntryModel.Access.INTERFACE);

  private final PackageResource _packageResource;
  private final DocModel _packageInfo;
  private final Map<String, ClassDefinitionModel> _allClasses;

  // caches...
  private volatile Collection<ClassDefinitionModel> _annotations;
  private volatile Collection<ClassDefinitionModel> _enums;
  private volatile Collection<ClassDefinitionModel> _interfaces;
  private volatile Collection<ClassDefinitionModel> _classes;

  /**
   * Constructor
   */
  public PackageModel(int access,
                      PackageResource packageResource,
                      DocModel packageInfo,
                      Collection<ClassDefinitionModel> classes)
  {
    super(access, packageResource.getPackageName());
    _packageResource = packageResource;
    _packageInfo = packageInfo;
    _allClasses = new HashMap<String, ClassDefinitionModel>();
    for(ClassDefinitionModel cdm : classes)
    {
      _allClasses.put(cdm.getName(), cdm);
    }
  }

  /**
   * @return the resource
   */
  public PackageResource getResource()
  {
    return _packageResource;
  }

  /**
   * @return the model kind
   */
  public Kind getKind()
  {
    return Kind.PACKAGE;
  }

  @Override
  public Collection<? extends Resource> getChildren()
  {
    Collection<ClassResource> res = new ArrayList<ClassResource>();

    for(ClassDefinitionModel model : _allClasses.values())
    {
      ClassResource classResource = model.getClassResource();
      if(!classResource.isResolved())
        classResource.resolve(getLibraryVersionResource());
      
      res.add(classResource);
    }

    return res;
  }

  public DocModel getPackageInfo()
  {
    return _packageInfo;
  }

  /**
   * @return the class or <code>null</code> if not found
   */
  public ClassDefinitionModel findClass(String simpleClassName)
  {
    return _allClasses.get(simpleClassName);
  }

  public Collection<ClassDefinitionModel> getAllClasses()
  {
    return _allClasses.values();
  }

  public Collection<ClassDefinitionModel> getAnnotations()
  {
    if(_annotations == null)
      _annotations = getClassesByAccess(BaseEntryModel.Access.ANNOTATION.getOpcode(), false);

    return _annotations;
  }

  public Collection<ClassDefinitionModel> getEnums()
  {
    if(_enums == null)
      _enums = getClassesByAccess(BaseEntryModel.Access.ENUM.getOpcode(), false);

    return _enums;
  }

  public Collection<ClassDefinitionModel> getInterfaces()
  {
    if(_interfaces == null)
      _interfaces = getClassesByAccess(BaseEntryModel.Access.INTERFACE.getOpcode(), false);

    return _interfaces;
  }

  public Collection<ClassDefinitionModel> getClasses()
  {
    if(_classes == null)
      _classes = getClassesByAccess(NOT_A_CLASS, true);
    
    return _classes;
  }

  private Collection<ClassDefinitionModel> getClassesByAccess(int access, boolean reverseLogic)
  {
    Collection<ClassDefinitionModel> res = new ArrayList<ClassDefinitionModel>();

    for(ClassDefinitionModel cdm : _allClasses.values())
    {
      if(reverseLogic)
      {
        if(!cdm.isAny(access))
          res.add(cdm);
      }
      else
      {
        if(cdm.isAny(access))
          res.add(cdm);
      }
    }

    return res;
  }

  /**
   * @return the library in which the model belongs
   */
  public LibraryVersionResource getLibraryVersionResource()
  {
    return getResource().getLibraryVersionResource();
  }

  /**
   * Collect the dependencies in the collection.
   *
   * @param dependencies to add the dependencies to it
   */
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    for(ClassDefinitionModel classDefinitionModel : _allClasses.values())
    {
      classDefinitionModel.collectDependencies(dependencies);
    }
    if(_packageInfo != null)
      _packageInfo.collectDependencies(dependencies);
  }

  /**
   * @return a version of this model with everything that is not part of the public api which has
   *         been stripped out. If the model itself is not part of the public api then
   *         <code>null</code> is returned!
   */
  public PackageModel toPublicAPI()
  {
    if(!isExportedPackage())
      return null;

    Collection<ClassDefinitionModel> classes = _allClasses.values();
    Collection<ClassDefinitionModel> newClasses = ExportableHelper.toPublicAPI(classes);

    if(newClasses == classes)
      return this;
    else
      return new PackageModel(getAccess(),
                              _packageResource,
                              _packageInfo,
                              newClasses);
  }

  /**
   * @return <code>true</code> if the package is exported, meaning there is at least one class
   * in the package that is exported.
   */
  public boolean isExportedPackage()
  {
    return isExported();
  }
}

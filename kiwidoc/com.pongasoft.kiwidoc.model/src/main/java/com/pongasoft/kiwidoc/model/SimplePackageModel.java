
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

import com.pongasoft.kiwidoc.model.resource.PackageResource;
import com.pongasoft.kiwidoc.model.resource.ClassResource;

import java.util.Set;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author yan@pongasoft.com
 */
public class SimplePackageModel
{
  private final PackageResource _packageResource;
  private final DocModel _packageInfo;
  private final Set<String> _exportedClasses;
  private final Set<String> _privateClasses;

  /**
   * Constructor
   */
  public SimplePackageModel(PackageResource packageResource,
                            DocModel packageInfo,
                            Collection<String> exportedClasses,
                            Collection<String> privateClasses)
  {
    _packageResource = packageResource;
    _packageInfo = packageInfo;
    _exportedClasses = Collections.unmodifiableSet(new HashSet<String>(exportedClasses));
    _privateClasses = Collections.unmodifiableSet(new HashSet<String>(privateClasses));
  }

  /**
   * @return the resource
   */
  public PackageResource getResource()
  {
    return _packageResource;
  }

  public String getName()
  {
    return _packageResource.getPackageName();
  }

  public DocModel getPackageInfo()
  {
    return _packageInfo;
  }

  /**
   * @return the classes that are exported (simple names only)
   */
  public Set<String> getExportedClasses()
  {
    return _exportedClasses;
  }

  /**
   * @return the classes that are exported (simple names only)
   */
  public Set<ClassResource> getExportedClassesResources()
  {
    return getClassesResources(_exportedClasses);
  }

  /**
   * @return the classes that are private (simple names only)
   */
  public Set<String> getPrivateClasses()
  {
    return _privateClasses;
  }

  /**
   * @return the classes that are exported (simple names only)
   */
  public Set<ClassResource> getPrivateClassesResources()
  {
    return getClassesResources(_privateClasses);
  }

  /**
   * @return all the classes (simple names), regardless of exported/private status
   */
  public Set<String> getAllClasses()
  {
    Set<String> res = new HashSet<String>();
    res.addAll(_exportedClasses);
    res.addAll(_privateClasses);
    return res;
  }

  /**
   * @return all the classes (simple names), regardless of exported/private status
   */
  public Set<ClassResource> getAllClassesResources()
  {
    return getClassesResources(getAllClasses());
  }

  /**
   * @return a version of this model with everything that is not part of the public api which has
   *         been stripped out. If the model itself is not part of the public api then
   *         <code>null</code> is returned!
   */
  public SimplePackageModel toPublicAPI()
  {
    if(!isExportedPackage())
      return null;

    if(_privateClasses.isEmpty())
      return this;
    else
      return new SimplePackageModel(_packageResource,
                                    _packageInfo,
                                    _exportedClasses,
                                    Collections.<String>emptyList());
  }

  private Set<ClassResource> getClassesResources(Set<String> classNames)
  {
    Set<ClassResource> res = new HashSet<ClassResource>(classNames.size());
    for(String className : classNames)
    {
      res.add(new ClassResource(_packageResource, className));
    }
    return res;
  }

  /**
   * @return <code>true</code> if the package is exported, meaning there is at least one class
   * in the package that is exported.
   */
  public boolean isExportedPackage()
  {
    return !_exportedClasses.isEmpty();
  }

  /**
   * @return <code>true</code> if this model is part of the public api
   */
  public boolean isPublicAPI()
  {
    return isExportedPackage();
  }

}
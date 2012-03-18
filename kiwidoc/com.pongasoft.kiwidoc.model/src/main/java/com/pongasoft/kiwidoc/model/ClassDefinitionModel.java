
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

import com.pongasoft.kiwidoc.model.type.GenericType;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.resource.ResolvableResource;
import com.pongasoft.kiwidoc.model.resource.DependenciesCollector;

import java.util.Collection;

/**
 * Represents a class definition only
 *
 * @author yan@pongasoft.com
 */
public class ClassDefinitionModel extends BaseEntryModel implements DependenciesCollector, Exportable<ClassDefinitionModel>
{
  private final GenericType _type;

  /**
   * Constructor
   */
  public ClassDefinitionModel(int access,
                              GenericType type)
  {
    super(access, type.getClassResource().getSimpleClassName());
    _type = type;
  }

  public ClassDefinitionModel(ClassEntryModel cem, GenericType type)
  {
    this(cem.getAccess(), type);
  }

  public GenericType getType()
  {
    return _type;
  }

  public ClassResource getClassResource()
  {
    return _type.getClassResource();
  }

  public boolean isInterface()
  {
    return is(Access.INTERFACE);
  }

  /**
   * @return whether it is an exported class or not (meaning visible in javadoc / part of the api)
   *
   */
  public boolean isExportedClass()
  {
    return isExported();
  }

  public ClassDefinitionModel toPublicAPI()
  {
    if(!isPublicAPI())
      return null;
    else
      return this;
  }

  /**
   * Collect the dependencies in the collection.
   *
   * @param dependencies to add the dependencies to it
   */
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    _type.collectDependencies(dependencies);
  }

  @Override
  public String toString()
  {
    return _type.toString();
  }
}
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

package com.pongasoft.kiwidoc.web.utils

import com.pongasoft.kiwidoc.model.type.Type
import java.lang.reflect.Modifier
import com.pongasoft.kiwidoc.model.type.GenericType
import com.pongasoft.kiwidoc.model.resource.Resource
import com.pongasoft.kiwidoc.model.resource.ClassResource
import com.pongasoft.kiwidoc.model.resource.PackageResource
import com.pongasoft.kiwidoc.model.BaseEntryModel
import com.pongasoft.kiwidoc.model.resource.RepositoryResource
import com.pongasoft.kiwidoc.model.BaseEntryModel.Access
import com.pongasoft.kiwidoc.model.Model
import com.pongasoft.kiwidoc.model.resource.ManifestResource

class ModelUtils
{

  def static RESOURCE_ICONS = [
    'RepositoryResource': 'RR',
    'OrganisationResource': 'RO',
    'LibraryResource': 'RL',
    'LibraryVersionResource': 'RV',
    'PackageResource': 'RP',
    'ManifestResource' : 'RM',
    'ClassResource': 'RC',
    'MethodResource': 'RO',
    'FieldResource': 'RL'
  ]

  def static MODEL_KIND_ICONS = [:]

  def static MODEL_KIND_NAMES = [:]

  static {
    MODEL_KIND_NAMES[Model.Kind.LIBRARY_VERSION] = 'Libraries'
    MODEL_KIND_NAMES[Model.Kind.PACKAGE] = 'Packages'
    MODEL_KIND_NAMES[Model.Kind.CLASS] = 'Classes'
    MODEL_KIND_NAMES[Model.Kind.MANIFEST] = 'Manifests'

    MODEL_KIND_ICONS[Model.Kind.REPOSITORY] = 'RR'
    MODEL_KIND_ICONS[Model.Kind.ORGANISATION] = 'RO'
    MODEL_KIND_ICONS[Model.Kind.LIBRARY] = 'RL'
    MODEL_KIND_ICONS[Model.Kind.LIBRARY_VERSION] = 'RV'
    MODEL_KIND_ICONS[Model.Kind.PACKAGE] = 'RP'
    MODEL_KIND_ICONS[Model.Kind.CLASS] = 'RC'
    MODEL_KIND_ICONS[Model.Kind.MANIFEST] = 'RM'
  }

  static def splitAccessForField(int access)
  {
    def modifiers = []
    if(access & Modifier.PUBLIC)
      modifiers << "public"

    if(access & Modifier.STATIC)
      modifiers << "static"

    if(access & Modifier.FINAL)
      modifiers << "final"

    if(access & Modifier.TRANSIENT)
      modifiers << "transient"

    if(access & Modifier.VOLATILE)
      modifiers << "volatile"

    if(access & Modifier.PROTECTED)
      modifiers << "protected"

    if(access & Modifier.PRIVATE)
      modifiers << "private"

    return modifiers
  }

  static def splitAccessForClass(int access)
  {
    def modifiers = []
    if(access & Modifier.PUBLIC)
      modifiers << "public"

    if(access & Modifier.STATIC)
      modifiers << "static"

    if(access & Modifier.FINAL)
      modifiers << "final"

    if(access & Modifier.ABSTRACT)
      modifiers << "abstract"

    if(access & Modifier.PROTECTED)
      modifiers << "protected"

    if(access & Modifier.PRIVATE)
      modifiers << "private"

    if(access & Modifier.STRICT)
      modifiers << "strict"

    return modifiers
  }

  static def splitAccessForMethod(int access)
  {
    def modifiers = []
    if(access & Modifier.PUBLIC)
      modifiers << "public"

    if(access & Modifier.STATIC)
      modifiers << "static"

    if(access & Modifier.FINAL)
      modifiers << "final"

    if(access & Modifier.SYNCHRONIZED)
      modifiers << "synchronized"

    if(access & Modifier.ABSTRACT)
      modifiers << "abstract"

    if(access & Modifier.NATIVE)
      modifiers << "native"

    if(access & Modifier.PROTECTED)
      modifiers << "protected"

    if(access & Modifier.PRIVATE)
      modifiers << "private"

    if(access & Modifier.STRICT)
      modifiers << "strict"

    return modifiers
  }

  static def inheritanceClass(method, inheritanceModel)
  {
    if(!inheritanceModel || !method)
      return ''

    def cm = inheritanceModel.findClassImplementing(method)

    def icon = ''

    if(cm)
    {
      if(cm.is(Access.INTERFACE))
      {
        icon = 'MS'
      }
      else
      {
        icon = 'MI'
      }
    }

    return "T-icon ${icon}"
  }

  static def modelKindName(Model.Kind kind)
  {
    return MODEL_KIND_NAMES[kind] ?: ''
  }

  static def resourceClass(resource)
  {
    if(!resource)
      return ''

    return "T-icon ${RESOURCE_ICONS[resource.getClass().simpleName]}"
  }

  static def resourceKind(Model.Kind kind)
  {
    if(!kind)
      return ''

    return "T-icon ${MODEL_KIND_ICONS[kind]}"
  }

  static def apiClass(model)
  {
    if(!model)
      return 'A-icon api-p'

    if(model.isPublicAPI())
    {
      return 'A-icon api-p'
    }
    else
    {
      return 'A-icon api-x'
    }
  }

  static def typeClass(model)
  {
    if(!model)
      return ''

    def closure = MODEL_ICONS[model.getClass().simpleName]
    if(closure)
      return closure(model)
    else
      return ''
  }

  private static def typeClassManifestModel = { model ->
    return resourceClass(model.resource)
  }

  private static def typeClassPackageModel = { model ->
    return resourceClass(model.resource)
  }

  private static def typeClassLibraryVersionModel = { model ->
    return resourceClass(model.resource)
  }

  private static def typeClassClassModel = { model ->
    def type

    switch(model)
    {
      case {it.is(BaseEntryModel.Access.ENUM)}:
        type = 'CE'
        break;

      case {it.is(BaseEntryModel.Access.ANNOTATION)}:
        type = 'CA'
        break;

      case {it.is(BaseEntryModel.Access.INTERFACE)}:
        type = 'CI'
        break;

      default:
        type = 'CC'
        break;
    }

    return "T-icon ${type}${typeBaseEntryModel(model)}"
  }

  private static def typeClassFieldModel = { model ->
    return "T-icon F${typeBaseEntryModel(model)}"
  }

  private static def typeClassMethodModel = { model ->
    return "T-icon M${typeBaseEntryModel(model)}"
  }

  def static MODEL_ICONS = [
    'LibraryVersionModel': typeClassLibraryVersionModel,
    'ManifestModel': typeClassManifestModel,
    'ClassModel': typeClassClassModel,
    'FieldModel': typeClassFieldModel,
    'MethodModel': typeClassMethodModel,
    'PackageModel': typeClassPackageModel
  ]

  private static def typeBaseEntryModel(model)
  {
    def access

    switch(model)
    {
      case {it.is(BaseEntryModel.Access.PUBLIC)}:
        access = 'P'
        break;

      case {it.is(BaseEntryModel.Access.PRIVATE)}:
        access = 'X'
        break;

      case {it.is(BaseEntryModel.Access.PROTECTED)}:
        access = 'O'
        break;

      default:
        access = 'D'
        break;
    }

    return access
  }

  static def isObject(Type type)
  {
    return (type instanceof GenericType) && (type.classResource.fqcn == 'java.lang.Object')
  }

  static def isString(Type type)
  {
    return (type instanceof GenericType) && (type.classResource.fqcn == 'java.lang.String')
  }

  static def sortByName(list)
  {
    list.sort() { e1, e2 ->
      return e1.name.compareTo(e2.name)
    }
  }

  static def sortBy(list, String propertyName)
  {
    list.sort() { e1, e2 ->
      return e1."${propertyName}".compareTo(e2."${propertyName}")
    }
  }

  static def sortBy(list, Closure closure)
  {
    list.sort() { e1, e2 ->
      return closure(e1).compareTo(closure(e2))
    }
  }

  static def toString(Resource resource)
  {
    if(!resource)
      return null

    if(resource instanceof ClassResource)
    {
      return "${resource.fqcnWithDot} (${resource.libraryVersionResource})"
    }

    if(resource instanceof PackageResource)
    {
      return "${resource.name} (${resource.libraryVersionResource})"
    }

    if(resource instanceof RepositoryResource)
    {
      return "root"
    }

    return resource.toString()
  }

  static def toShortString(Resource resource)
  {
    if(!resource)
      return null

    if(resource instanceof ClassResource)
    {
      return "${resource.simpleClassNameWithDot}"
    }

    if(resource instanceof PackageResource)
    {
      return "${resource.name}"
    }

    if(resource instanceof RepositoryResource)
    {
      return "root"
    }

    if(resource instanceof ManifestResource)
    {
      return "${resource.resourceName} (${toShortString(resource.parent)})"
    }

    return resource.toString()
  }

  static def oddOrEven(n)
  {
    if(n % 2 == 0)
      return 'even'
    else
      return 'odd'
  }
}
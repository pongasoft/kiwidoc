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

import com.pongasoft.kiwidoc.model.resource.Resource
import com.pongasoft.kiwidoc.model.type.Type
import com.pongasoft.kiwidoc.model.Model

class ModelUtils implements GroovyInterceptable
{
  static def splitAccessForField(int access)
  {
    return com.pongasoft.kiwidoc.web.utils.ModelUtils.splitAccessForField(access)
  }

  static def splitAccessForClass(int access)
  {
    return com.pongasoft.kiwidoc.web.utils.ModelUtils.splitAccessForClass(access)
  }

  static def splitAccessForMethod(int access)
  {
    return com.pongasoft.kiwidoc.web.utils.ModelUtils.splitAccessForMethod(access)
  }

  static def resourceClass(resource)
  {
    return com.pongasoft.kiwidoc.web.utils.ModelUtils.resourceClass(resource)
  }

  static def apiClass(model)
  {
    return com.pongasoft.kiwidoc.web.utils.ModelUtils.apiClass(model)
  }

  static def inheritanceClass(method, inheritanceModel)
  {
    return com.pongasoft.kiwidoc.web.utils.ModelUtils.inheritanceClass(method, inheritanceModel)
  }

  static def modelKindName(Model.Kind kind)
  {
    return com.pongasoft.kiwidoc.web.utils.ModelUtils.modelKindName(kind)
  }

  static def resourceKind(Model.Kind kind)
  {
    return com.pongasoft.kiwidoc.web.utils.ModelUtils.resourceKind(kind)
  }

  static def typeClass(model)
  {
    return com.pongasoft.kiwidoc.web.utils.ModelUtils.typeClass(model)
  }

  static def isObject(Type type)
  {
    return com.pongasoft.kiwidoc.web.utils.ModelUtils.isObject(type)
  }

  static def isString(Type type)
  {
    return com.pongasoft.kiwidoc.web.utils.ModelUtils.isString(type)
  }

  static def sortByName(list)
  {
    return com.pongasoft.kiwidoc.web.utils.ModelUtils.sortByName(list)
  }

  static def sortBy(list, String propertyName)
  {
    return com.pongasoft.kiwidoc.web.utils.ModelUtils.sortBy(list, propertyName)
  }

  static def sortBy(list, Closure closure)
  {
    return com.pongasoft.kiwidoc.web.utils.ModelUtils.sortBy(list, closure)
  }

  static def toString(Resource resource)
  {
    return com.pongasoft.kiwidoc.web.utils.ModelUtils.toString(resource)
  }

  static def toShortString(Resource resource)
  {
    return com.pongasoft.kiwidoc.web.utils.ModelUtils.toShortString(resource)
  }

  static def oddOrEven(n)
  {
    return com.pongasoft.kiwidoc.web.utils.ModelUtils.oddOrEven(n)
  }
}
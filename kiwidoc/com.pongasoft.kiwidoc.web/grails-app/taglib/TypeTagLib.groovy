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

import com.pongasoft.kiwidoc.model.type.PrimitiveType
import com.pongasoft.kiwidoc.model.type.GenericType
import com.pongasoft.kiwidoc.model.type.GenericTypeVariable
import com.pongasoft.kiwidoc.model.type.GenericVariable
import com.pongasoft.kiwidoc.model.type.GenericBoundedWildcardType
import com.pongasoft.kiwidoc.model.type.ArrayType
import com.pongasoft.kiwidoc.model.resource.ClassResource
import com.pongasoft.kiwidoc.model.type.TypePart
import com.pongasoft.kiwidoc.model.type.AssignedGenericTypeVariable

class TypeTagLib
{
  static namespace = 'pt'

  def pathManager

  /**
   * Called from the tag lib to render the type
   */
  def renderType = { attrs ->
    if (!attrs.type)
      return

    out << doRenderType(attrs)
  }

  /**
   * Renders primitive
   */
  def renderPrimitiveType = {attrs ->
    PrimitiveType type = attrs.type
    out << type.getPrimitive().name().toLowerCase();
  }

  def renderGenericType = {attrs ->
    def GenericType type = attrs.type
    def style = attrs.style ? new HashMap(attrs.style) : [:]
    if(style.oneLinkOnly)
    {
      def formatter = {
        style.dontRenderLink = true
        style.simpleNameOnly = true
        style.oneLinkOnly = false
        def label = new StringWriter()
        doRenderTypes(label, type.typeParts, '.', attrs, [style: style])
        return label.toString()
      }
      out << pt.renderClassResource([*:attrs,
                                    classResource: type.classResource, 
                                    formatter: formatter])
    }
    else
    {
      doRenderTypes(out, type.typeParts, '.', attrs)
    }
  }

  /**
   * Subclass is same as superclass
   */
  def renderSimpleType = renderGenericType

  def renderTypePart = { attrs ->
    TypePart type = attrs.type
    def style = attrs.style ? new HashMap(attrs.style) : [:]

    if(attrs.index > 0)
    {
      style.fqcnOnly = false
      style.simpleNameOnly = true
    }

    MiscUtils.saveAndRestore(attrs, [style: style]) {
      out << pt.renderClassResource([*:it, classResource: type.classResource])
    }

    if(type.generics && !style.dontRenderGenerics)
    {
      out << "&lt;"
      doRenderTypes(out, type.generics, ", ", attrs)
      out << "&gt;"
    }
  }

  def renderClassResource = { attrs ->
    ClassResource classResource = attrs.classResource
    def style = attrs.style ?: [:]
    def formatter = attrs.formatter ?: { it.encodeAsHTML() }

    def label = classResource.fqcn

    if(!style.fqcnOnly)
    {
      if(style.simpleNameOnly ||
         classResource.packageResource.packageName == 'java.lang' ||
         classResource.packageResource.packageName == 'java.lang.annotation' ||
         pageScope.lvr == classResource.libraryVersionResource)
      {
        if(classResource.isInnerClass())
          label = classResource.innerClassName
        else
          label = classResource.name
      }
    }

    if(attrs.isAnnotation)
    {
      label = '@' + label
    }

    if(classResource.isResolved() && !style.dontRenderLink)
    {
      def path = pathManager.computePath(classResource)
      out << '<a href="'
      out << createLink(controller: 'library',
                        action: 'showContent',
                        params: [uri: path, viewMode: params.viewMode]).encodeAsHTML()
      out << '" title="'
      out << ModelUtils.toString(classResource).encodeAsHTML()
      out << '">'
      out << formatter(label)
      out << '</a>'
    }
    else
    {
      out << formatter(label)
    }
  }

  def renderGenericTypeVariable = {attrs ->
    GenericTypeVariable type = attrs.type
    out << type.name
    if(type.bounds)
    {
      if(type.bounds.size() > 1 || !ModelUtils.isObject(type.bounds[0]))
      {
        out << " extends "
        doRenderTypes(out, type.bounds, " &amp; ", attrs)
      }
    }
  }

  /**
   * When the generic type variable has been assigned we use the assigned value instead
   */
  def renderAssignedGenericTypeVariable = { attrs ->
    AssignedGenericTypeVariable type = attrs.type
    out << doRenderType(attrs, [type: type.assignedType])
  }

  def renderGenericVariable = {attrs ->
    GenericVariable type = attrs.type
    out << type.name.encodeAsHTML()
  }

  def renderGenericBoundedWildcardType = {attrs ->
    GenericBoundedWildcardType type = attrs.type
    out << "? ${type.isSuperKind() ? ' super ' : ' extends '}"
    out << doRenderType(attrs, [type: type.bound])
  }

  def renderGenericUnboundedWildcardType = {attrs ->
    out << "?"
  }

  def renderArrayType = {attrs ->
    ArrayType type = attrs.type

    out << doRenderType(attrs, [type: type.type])

    if(attrs.isVarargs)
    {
      out << "..."
    }
    else
    {
      out << ("[]" * type.dimension)
    }
  }

  private def doRenderType(attrs)
  {
    doRenderType(attrs, [:])
  }

  private def doRenderType(attrs, newValues)
  {
    MiscUtils.saveAndRestore(attrs, newValues) {
      def p = properties["render${it.type.class.simpleName}"]
      if(p)
      {
        return pt."render${it.type.class.simpleName}"(it)
      }
      else
        return "---${it.type.class.simpleName}:${it.type.toString()}---".encodeAsHTML();
    }
  }

  void doRenderTypes(localOut, types, separator, attrs)
  {
    types.eachWithIndex { elt, i ->
      if (i > 0)
        localOut << separator
      localOut << doRenderType(attrs, [type: elt, index: i])
    }
  }

  void doRenderTypes(localOut, types, separator, attrs, newValues)
  {
    MiscUtils.saveAndRestore(attrs, newValues) {
      doRenderTypes(localOut, types, separator, it)
    }
  }
}

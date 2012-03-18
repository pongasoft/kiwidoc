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
import com.pongasoft.kiwidoc.web.BaseController
import com.pongasoft.kiwidoc.model.BaseEntryModel
import com.pongasoft.kiwidoc.model.resource.ClassResource
import com.pongasoft.kiwidoc.model.resource.PackageResource
import com.pongasoft.kiwidoc.model.resource.ManifestResource
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource

class KiwidocTagLib
{
  static namespace = 'pk'

  private static RENDER_METHOD_STYLES = [
    full: [
      separator : ', ',
      simpleNameOnly: true,
      dontRenderLink: false,
      renderParameterNames: true,
      renderReturnType: 'before',
      renderGenerics: true,
      renderExceptions: true,
      renderAccess: true
    ],
    summary: [
      separator : ',',
      simpleNameOnly: true,
      dontRenderLink: true,
      renderParameterNames: false,
      renderReturnType: 'after',
      renderGenerics: false,
      renderExceptions: false,
      renderAccess: false
    ],
    summaryTitle: [
      separator : ',',
      simpleNameOnly: true,
      dontRenderLink: true,
      renderParameterNames: true,
      renderReturnType: 'before',
      renderGenerics: true,
      renderExceptions: true,
      renderAccess: true
    ]
  ]

  private static RENDER_FIELD_STYLES = [
    full: [
      simpleNameOnly: false,
      dontRenderLink: false,
      renderType: 'before',
      renderValue: true,
      renderAccess: true
    ],
    summary: [
      simpleNameOnly: true,
      dontRenderLink: true,
      renderType: 'after',
      renderValue: false,
      renderAccess: false
    ],
    summaryTitle: [
      simpleNameOnly: true,
      dontRenderLink: true,
      renderType: 'before',
      renderValue: true,
      renderAccess: true
    ]
  ]

  def pathManager

  def lt = {
    out << "<"
  }

  def gt = {
    out << ">"
  }

  /**
   * Convenient call to generate an html tag (usually used from within an attribute)
   */
  def htmlTag = { attrs ->
    out << "<"
    out << attrs.name
    out << "/>"
  }

  /**
   * Takes a map and returns a properly formatted query string
   */
  def mapToQueryString = { attrs ->
    out << attrs?.collect { k,v ->
      "${k.encodeAsHTML()}=${v.toString().encodeAsHTML()}"
    }?.join('&')
  }

  /**
   * Renders the class kind (enumeration / annotation / interface / class)
   */
  def classKind = { attrs ->
    def model = attrs.model
    if(model.is(BaseEntryModel.Access.ENUM))
    {
      out << 'Enum'
      return
    }

    if(model.is(BaseEntryModel.Access.ANNOTATION))
    {
      out << 'Annotation'
      return
    }

    if(model.is(BaseEntryModel.Access.INTERFACE))
    {
      out << 'Interface'
      return
    }

    out << 'Class'
  }

  /**
   * Renders Public / Internal depending on public api...
   */
  def publicAPI = { attrs ->
    if(attrs.test)
      out << 'Public'
    else
      out << 'Internal'
  }

  /**
   * Simply returns the resource uri
   */
  def resourceURI = { attrs ->
    out << pathManager.computePath(attrs.resource)
  }

  def contentLink = {attrs, body ->
    def title = attrs.title
    if(attrs.resource)
    {
      if(!title)
        title = ModelUtils.toString(attrs.resource)
    }

    def writer = out
    writer << '<a href="'
    writer << pk.createContentLink(attrs).encodeAsHTML()
    writer << '"'
    if(title)
      writer << ' title="' << title << '"'
    writer << '>'
    // output the body
    writer << body()
    // close tag
    writer << '</a>'
  }

  def createContentLink = {attrs ->
    def viewMode = attrs.viewMode ?: params.viewMode
    def uri
    if(attrs.uri)
    {
      uri = attrs.uri
    }
    else if(attrs.resource)
    {
      uri = pathManager.computePath(attrs.resource)
    }
    else if(attrs.model)
    {
      uri = pathManager.computePath(attrs.model.resource)
    }

    if(!uri)
      throw new IllegalArgumentException("missing uri, resource or model");

    def fragment = uri.fragment

    if(fragment)
    {
      uri = new URI(uri.path)
    }

    def params = [controller: 'library', action: 'showContent', params: [uri: uri, viewMode: viewMode]]
    if(fragment)
      params.fragment = fragment
    if(attrs.absolute)
      params.absolute = attrs.absolute

    out << createLink(params)
  }


   /**
    * renders the resource (linked)
    */
  def renderResource = { attrs ->
    out << pk.contentLink(attrs) {
      out << ModelUtils.toString(attrs.resource).encodeAsHTML()
    }
  }

  /**
   * Builds the resource path
   */
  def resourcePath = { attrs ->
    Resource resource = attrs.resource

    def fullPath = [resource]
    def parent = resource.parent
    def currentResource = resource
    while(parent != null)
    {
      fullPath << parent
      currentResource = parent
      parent = currentResource.parent
    }

    def value = pk.split(in: fullPath.reverse(), begin: '<ul>', end: '</ul>', separator: '') { map ->
      def r = map.var
      out << "<li class=\"r${r.depth}\">"
      out << pk.contentLink(resource: r) {
      if(r.resourceName)
        out << r.resourceName.encodeAsHTML()
      else
        out << "root"
      }
      out << '</li>'
    }
    
    out << value
  }

  /**
   * Builds the resource path (stops at library version)
   */
  def resourcePath2 = { attrs ->
    Resource resource = attrs.resource

    def fullPath = [resource]
    def parent = resource.parent
    def currentResource = resource
    while(parent != null && parent.depth >= 3)
    {
      fullPath << parent
      currentResource = parent
      parent = currentResource.parent
    }

    def value = pk.split(in: fullPath.reverse(), begin: '<ul>', end: '</ul>', separator: '') { map ->
      def r = map.var
      out << "<li class=\"i${map.idx} ${ModelUtils.resourceClass(r)}\">"
      out << pk.contentLink(resource: r) {
        if(r.depth == 0)
        {
          out << 'root'
        }
        else
        {
          if(r.depth <= 3)
            out << r.toString().encodeAsHTML()
           else
            out << r.resourceName.encodeAsHTML()
        }
      }
      out << '</li>'
    }

    out << value
  }

  /**
   * Link to the repo
   */
  def repoLink = { attrs, body ->
    attrs.uri = '/'
    out << pk.contentLink(attrs, body)
  }

  /**
   * Display the lib
   */
  def lib = {attrs ->
    def writer = out
    writer << attrs.resource.organisation.encodeAsHTML() << '/'
    writer << attrs.resource.name.encodeAsHTML()
  }

  /**
   * Display the lib
   */
  def lvr = {attrs ->
    def writer = out
    writer << attrs.resource.organisation.encodeAsHTML() << '/'
    writer << attrs.resource.name.encodeAsHTML() << "/"
    writer << attrs.resource.resourceName.encodeAsHTML()
  }

  /**
   * Equivalent to g:each but allow for begin/end and separator attributes
   */
  def split = { attrs, body ->
    def var = attrs.var ?: "var"
    def idx = attrs.idx ?: "idx"

    def begin = attrs.begin ?: ""
    def end = attrs.end ?: ""
    def separator = attrs.separator ?: ""

    def writer = out

    if(attrs.in)
    {
      // not null and not empty (definition of truth in groovy)
      attrs.in.eachWithIndex { elt, i ->
        if(i == 0)
        {
          writer << begin
        }
        else
        {
          writer << separator
        }
        writer << body((var):elt, (idx): i)
      }

      writer << end
    }
    else
    {
      if(attrs.alwaysBeginEnd?.toString() == "true")
      {
        writer << begin << end
      }
    }
  }

  def dl = { attrs, body ->
    def var = attrs.var ?: "var"
    def clazz = attrs."class"
    def title = ''
    if(attrs.title)
    {
      title = "<dt>${attrs.title}</dt>"
    }
    def dl = "<dl>"
    if(clazz)
      dl = "<dl class=\"${clazz}\">"

    out << pk.split(in: attrs.in, begin: "${dl}${title}<dd>", end: "</dd></dl>", separator: "</dd><dd>") { map ->
      def elt = map.var
      out << body((var):elt)
    }
  }

  def ul = { attrs, body ->
    def var = attrs.var ?: "var"
    def title = attrs.title ?: ''

    if(attrs.in)
    {
      out << "<ul>"
      out << pk.split(in: attrs.in, begin: "${title}") { map ->
        out << "<li class=\"i${map.idx} ${ModelUtils.oddOrEven(map.idx)}\">"
        def elt = map.var
        out << body((var):elt)
        out << '</li>'
        }
      out << "</ul>"
    }
  }

  def renderMethod = { attrs ->
    def method = attrs.method
    def style = RENDER_METHOD_STYLES[attrs.style ?: 'full']

    // access modifiers
    if(style.renderAccess)
    {
      def access = ModelUtils.splitAccessForMethod(method.access)
      if(access)
      {
        out << access.join(' ')
        out << ' '
      }
    }

    // generics
    if(style.renderGenerics)
    {
      out << pk.split(in: method.genericTypeVariables.genericTypeVariables, begin: '&lt;', end: '&gt; ', separator: style.separator) { map ->
      def gtv = map.var
      out << pt.renderType(type: gtv, style: style)
      }
    }

    // return type
    if(method.returnType && style.renderReturnType == 'before')
    {
      out << pt.renderType(type: method.returnType, style: style)
      out << ' '
    }

    // method name
    out << method.name.encodeAsHTML()

    // method parameters
    if(!method.isAnnotationMethod())
    {
      out << pk.split(in: method.parameters, begin: '(', end: ')', alwaysBeginEnd: true, separator: style.separator) { map ->
        def parameter = map.var
        out << pt.renderType(type: parameter.type,
                             style: style,
                             isVarargs: method.isVarargs())
        if(style.renderParameterNames)
        {
          out << ' ' << parameter.name.encodeAsHTML()
        }
      }
    }

    // return type
    if(method.returnType && style.renderReturnType == 'after')
    {
      out << ':'
      out << pt.renderType(type: method.returnType, style: style)
    }

    // exceptions
    if(style.renderExceptions)
    {
      def begin = ' throws '
      def end = ''

      if(!style.dontRenderLink)
      {
        begin = "<span class=\"exception\">$begin"
        end = "${end}</span>"
      }

      out << pk.split(in: method.exceptions, begin: begin, end: end, separator: style.separator) { map ->
        def exception = map.var
        out << pt.renderType(type: exception, style: style)
      }
    }
  }

  def renderField = { attrs ->
    def field = attrs.field
    def style = RENDER_FIELD_STYLES[attrs.style ?: 'full']

    // access modifiers
    if(style.renderAccess)
    {
      def access = ModelUtils.splitAccessForField(field.access)
      if(access)
      {
        out << access.join(' ')
        out << ' '
      }
    }

    // type
    if(style.renderType == 'before')
    {
      out << pt.renderType(type: field.type, style: style)
      out << ' '
    }

    out << field.name.encodeAsHTML()

    // type
    if(style.renderType == 'after')
    {
      out << ':'
      out << pt.renderType(type: field.type, style: style)
    }

    // value
    if(field.value && style.renderValue)
    {
      def fieldValue = field.value.encodeAsHTML()
      if(ModelUtils.isString(field.type))
      fieldValue = "\"${fieldValue}\""
      out << ' = '
      out << fieldValue.encodeAsHTML()
    }
  }

  def renderModelResource = { attrs ->
    def model = attrs.model
    def resource = model.resource

    if(resource instanceof ClassResource)
    {
      out << "${model.classKindString}: ${resource.fqcnWithDot.encodeAsHTML()} ("
      out << pk.lvr(resource: resource.libraryVersionResource)
      out << ')'
      return
    }

    if(resource instanceof PackageResource)
    {
      out << "Package: ${resource.packageName.encodeAsHTML()} ("
      out << pk.lvr(resource: resource.libraryVersionResource)
      out << ')'
      return
    }

    if(resource instanceof ManifestResource)
    {
      out << 'Manifest: '
      out << pk.lvr(resource: resource.libraryVersionResource)
      return
    }

    if(resource instanceof LibraryVersionResource)
    {
      out << 'Library: '
      out << pk.lvr(resource: resource)
      return
    }

    out << resource
  }

  def renderSimpleModelResource = { attrs ->
    def model = attrs.model
    def resource = model.resource

    if(resource instanceof ClassResource)
    {
      out << "${model.classKindString}: ${resource.simpleClassNameWithDot.encodeAsHTML()}"
      return
    }

    if(resource instanceof PackageResource)
    {
      out << "Package: ${resource.packageName.encodeAsHTML()}"
      return
    }

    if(resource instanceof ManifestResource)
    {
      out << 'Manifest: '
      out << pk.lvr(resource: resource.libraryVersionResource)
      return
    }

    if(resource instanceof LibraryVersionResource)
    {
      out << 'Library: '
      out << pk.lvr(resource: resource)
      return
    }

    out << resource
  }

  def static SWITCH_REGEX = ~"^/java/([^/]+)/([^/]+)/(.*)\$"

  /**
   * used to switch from public view mode to private view mode...
   */
  def switchViewMode = { attrs ->
    def uri = RequestUtils.forwardURIWithQueryString(request)

    def viewMode
    if(params.viewMode == BaseController.VIEW_MODE_PUBLIC)
    {
      viewMode = BaseController.VIEW_MODE_PRIVATE
    }
    else
    {
      if(params.viewMode == BaseController.VIEW_MODE_PRIVATE)
      {
        viewMode = BaseController.VIEW_MODE_PUBLIC
      }
    }

    if(viewMode)
    {
      def m = SWITCH_REGEX.matcher(uri)
      if(m)
      {
        def matches = m[0]
        uri = "/java/${matches[1]}/${viewMode}/${matches[3]}"
      }
    }

    out << uri
  }

  /**
   * Workaround for bug in grails...
   */
  def remoteField= { attrs, body ->
    def params = attrs['params']?:null
    if(params){
      String pString = "\'"
      params.each { key, value ->
        pString += "${key}=${value}&"
      }
      pString += "\'"
      attrs['params'] = pString;
    }
    out << g.remoteField(attrs, body)
  }

  /**
   * This is sort of a hack to represent inheritance (not sure how to do better in css) :(
   */
  def inheritanceSpacer = { attrs ->
    def size = attrs.size
    if(size > 0)
    {
      if(size > 1)
      {
        (0..size-2).each { out << '<span class="ispacer"/>' }
      }
      out << '<span class="isymbol"/>'
    }
  }
}

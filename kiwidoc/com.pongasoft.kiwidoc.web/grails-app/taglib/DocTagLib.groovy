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

import com.pongasoft.kiwidoc.model.tag.InlineTextTag
import com.pongasoft.kiwidoc.model.tag.OrdinaryTag
import com.pongasoft.kiwidoc.model.tag.SeeTag
import com.pongasoft.kiwidoc.model.tag.LinkReference
import com.pongasoft.kiwidoc.model.tag.InlineLinkTag
import com.pongasoft.kiwidoc.model.tag.ParamTag
import com.pongasoft.kiwidoc.model.tag.InlineOrdinaryTag
import com.pongasoft.kiwidoc.model.resource.ResolvableResource
import com.pongasoft.kiwidoc.builder.serializer.type.TypeDecoder
import com.pongasoft.kiwidoc.model.tag.ThrowsTag
import com.pongasoft.kiwidoc.model.DocModel
import com.pongasoft.kiwidoc.model.resource.FieldOrMethodResource
import com.pongasoft.util.html.SafeHtmlCleaner

public class DocTagLib
{
  static namespace = 'pd'

  def pathManager
  private TypeDecoder _typeDecoder = new TypeDecoder()

  private static def METHOD_PATTERN = ~/([^(]+)\(([^)]*)\)$/ 

  private static def MISC_CATEGORY = 'Misc:'

  private static def GROUP_BY_STYLE = [
    methods: [
      '@param': 'Parameters:',
      '@return': 'Return:',
      '@throws': 'Exceptions:',
      '@exception': 'Exceptions:',
      '@deprecated': 'Deprecated:',
      '@see': 'See:',
      '@since': 'Since:',
      '*': 'Misc:'
    ],

    'class': [
      '@author': 'Authors:',
      '@param': 'Parameters:',
      '@see': 'See:',
      '@version': 'Version:',
      '@deprecated': 'Deprecated:',
      '@since': 'Since:',
      '*': 'Misc:'
    ]
  ]

  /**
   * called for a doc (doc model)
   */
  def renderDoc = { attrs ->
    DocModel doc = attrs.doc
    def groupBy = GROUP_BY_STYLE[attrs.groupByStyle] ?: [:]

    if(doc?.hasDoc())
    {
      def sw = new StringWriter()

      sw << '<div class="javadoc">'
      if(!doc.doc.missing)
      {
        sw << "<div class=\"mainDocContent${doc.doc.origin != attrs.resource ? ' inheritedDoc': ''}\">"
        doc.doc.inlineTags.each {
          sw << pd.renderTag(tag: it, resource: attrs.resource)
        }
        sw << '</div>'
      }

      if(doc.tags)
      {
        def tags = [:]

        doc.tags.each { tag ->
          def category = groupBy[tag.name] ?: MISC_CATEGORY
          def list = tags[category]
          if(!list)
          {
            list = []
            tags[category] = list
          }
          list << tag
        }

        sw << '<dl class="tags">'

        def values = groupBy.values().collect {it}.unique()

        values.each { category ->
          def categoriedTags = tags[category]
          if(categoriedTags)
          {
            sw << "<dt class=\"tagCategory\">${category}</dt>"
            categoriedTags.each { tag ->
              sw << "<dd class=\"tagDocContent${category == MISC_CATEGORY ? ' misc': ''}${tag.origin != attrs.resource ? ' inheritedDoc': ''}\">"
              sw << pd.renderTag(tag: tag, resource: attrs.resource)
              sw << '</dd>'
            }
          }
        }

        sw << '</dl>'
      }

      sw << '</div>'

      out << SafeHtmlCleaner.cleanHtml(sw.toString())
    }
  }

  /**
   * Called from the tag lib to render the tag
   */
  def renderTag = { attrs ->
    if (!attrs.tag)
      return

    out << doRenderTag(attrs)
  }

  /**
   * Renders inline text tag
   */
  def renderInlineTextTag = {attrs ->
    // TODO HIGH YP: need to 'sanitize' and remove javascript!!
    InlineTextTag tag = attrs.tag
    out << tag.text
  }

  /**
   * Renders inline link tag
   */
  def renderInlineLinkTag = {attrs ->
    InlineLinkTag tag = attrs.tag
    doRenderLink(attrs, tag.linkReference, tag.text)
  }

  /**
   * Renders param tag
   */
  def renderParamTag = {attrs ->
    ParamTag tag = attrs.tag
    out << '<span class="tagName">' << tag.name << '</span> '
    out << '<span class="paramName">'
    if(tag.isTypeParameter)
      out << '&lt;'
    out << tag.parameterName
    if(tag.isTypeParameter)
      out << '&gt;'
    out << '</span>'
    out << ' '
    doRenderTags(out, tag.inlineTags, ' ', attrs)
  }
  
  /**
   * Renders ordinary tag
   */
  def renderOrdinaryTag = {attrs ->
    OrdinaryTag tag = attrs.tag
    out << '<span class="tagName">' << tag.name << '</span> '
    doRenderTags(out, tag.inlineTags, ' ', attrs)
  }

  /**
   * Renders inline ordinary tag
   */
  def renderInlineOrdinaryTag = {attrs ->
    InlineOrdinaryTag tag = attrs.tag
    if(tag.name == '@code')
    {
      out << '<tt>' << tag.text?.encodeAsHTML() << '</tt>'
    }
    else
    {
      out << '{' << tag.name
      if(tag.text)
        out << ' ' << tag.text
      out << '}'
    }
  }

  /**
   * Renders see tag
   */
  def renderSeeTag = {attrs ->
    SeeTag tag = attrs.tag
    out << '<span class="tagName">' << tag.name << '</span> '
    def sw = new StringWriter()
    doRenderTags(sw, tag.inlineTags, ' ', attrs)
    doRenderLink(attrs, tag.linkReference, sw.toString())
  }

  def renderThrowsTag = { attrs ->
    ThrowsTag tag = attrs.tag
    out << '<span class="tagName">' << tag.name << '</span> '
    if(tag.exceptionType)
    {
      attrs.type = tag.exceptionType
      out << pt.renderType(attrs)
    }
    else
    {
      out << tag.exceptionName
    }
    out << ' '
    doRenderTags(out, tag.inlineTags, ' ', attrs)
  }

  /**
   * TODO HIGH YP: duplicate code because calling the other taglib pt.xxx although the call work
   * the result is not (see nabble post)
   */
  private def doRenderLink(attrs, LinkReference ref, String label)
  {
    if(ref.link == null)
    {
      out << label
    }
    else
    {
      ResolvableResource resource = ref.link

      if(!label)
      {
        if(resource.simpleClassName)
        {
          if(resource.packageName == 'java.lang' ||
             attrs.library == resource.libraryVersionResource)
          {
            label = resource.simpleClassName
          }
          else
          {
            label = resource.packageName + '.' + resource.simpleClassName
          }

          if(resource.properties.methodName)
            label += '.' + createFriendlyMethodName(resource.methodName)
        }
        else
        {
          label = resource.packageName
        }
      }

      if(resource.isResolved())
      {
        String fragment = null
        if(resource instanceof FieldOrMethodResource)
        {
          fragment = resource.name.encodeAsURL()
          resource = resource.parent
        }
        def path = pathManager.computePath(resource)
        out << '<a href="'
        out << createLink(controller: 'library',
                          action: 'showContent',
                          params: [uri: path, viewMode: params.viewMode])
        if(fragment)
          out << '#' << fragment
        out << '">' << label << '</a>'
      }
      else
      {
        out << label
      }
    }
  }

  private def createFriendlyMethodName(String methodName)
  {
    def matcher = METHOD_PATTERN.matcher(methodName)
    if(matcher)
    {
      def parameters = matcher[0][2]
      if(parameters)
      {
        parameters = _typeDecoder.decodeMethodParameters(parameters).join(', ')
      }
      return "${matcher[0][1]}(${parameters})"
    }
    else
    {
      return methodName
    }
  }

  private def doRenderTag(attrs)
  {
    def p = properties["render${attrs.tag.class.simpleName}"]
    if(p)
    {
      pd."render${attrs.tag.class.simpleName}"(attrs)
    }
    else
      out << "---${attrs.tag.class.simpleName}:${attrs.tag.toString()}---".encodeAsHTML();
  }

  private void doRenderTags(localOut, tags, separator, attrs)
  {
    tags.eachWithIndex { elt, i ->
      if (i > 0)
        localOut << separator
      attrs.tag = elt
      localOut << doRenderTag(attrs)
    }
  }
}
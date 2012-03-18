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

import com.pongasoft.kiwidoc.model.annotation.Annotation
import com.pongasoft.kiwidoc.model.annotation.AnnotationElement
import com.pongasoft.kiwidoc.model.annotation.PrimitiveAnnotationValue
import com.pongasoft.kiwidoc.model.annotation.StringAnnotationValue
import com.pongasoft.kiwidoc.model.annotation.ClassAnnotationValue
import com.pongasoft.kiwidoc.model.annotation.EnumAnnotationValue
import com.pongasoft.kiwidoc.model.annotation.AnnotationAnnotationValue
import com.pongasoft.kiwidoc.model.annotation.ArrayAnnotationValue

class AnnotationTagLib
{
  static namespace = 'pa'

  /**
   * Renders an annotation
   */
  def renderAnnotation = { attrs ->
    if (!attrs.annotation)
      return

    doRenderAnnotation(attrs)
  }

  /**
   * @A(a=b, c=d)
   */
  def renderAnnotationClass = { attrs ->
    Annotation annotation = attrs.annotation
    out << pt.renderType(*:attrs, type:annotation.type, isAnnotation:true)
    out << pk.split(in: annotation.annotationElements, begin: '(', end: ')', separator: ', ') { map ->
      attrs.annotation = map.var
      doRenderAnnotation(attrs)
    }    
  }

  /**
   * a=b
   */
  def renderAnnotationElementClass = { attrs ->
    AnnotationElement annotation = attrs.annotation
    out << annotation.name << '='
    attrs.annotation = annotation.annotationValue
    doRenderAnnotation(attrs)
  }

  /**
   * 10, 1.2
   */
  def renderPrimitiveAnnotationValueClass = { attrs ->
    PrimitiveAnnotationValue annotation = attrs.annotation
    out << annotation.value
  }

  /**
   * "abcdef"
   */
  def renderStringAnnotationValueClass = { attrs ->
    StringAnnotationValue annotation = attrs.annotation
    out << '"' << annotation.value << '"'
  }

  /**
   * p.C
   */
  def renderClassAnnotationValueClass = { attrs ->
    ClassAnnotationValue annotation = attrs.annotation
    out << pt.renderType(*:attrs, type:annotation.type)
  }

  /**
   * RetentionPolicy.RUNTIME
   */
  def renderEnumAnnotationValueClass = { attrs ->
    EnumAnnotationValue annotation = attrs.annotation
    out << pt.renderType(*:attrs, type:annotation.enumType) << '.'

    if(annotation.enumResource.isResolved())
    {
      out << pk.contentLink(resource: annotation.enumResource) {
        out << annotation.enumValue.encodeAsHTML()
      }
    }
    else
    {
      out << annotation.enumValue.encodeAsHTML()
    }
  }

  /**
   * another annotation @A()
   */
  def renderAnnotationAnnotationValueClass = { attrs ->
    AnnotationAnnotationValue annotation = attrs.annotation
    attrs.annotation = annotation.annotation
    doRenderAnnotation(attrs)
  }

  /**
   * {a,b,c}
   */
  def renderArrayAnnotationValueClass = { attrs ->
    ArrayAnnotationValue annotation = attrs.annotation
    out << pk.split(in: annotation.values, begin: '{', end: '}', separator: ', ', alwaysBeginEnd: true) { map ->
      attrs.annotation = map.var
      doRenderAnnotation(attrs)
    }
  }

  private def doRenderAnnotation(attrs)
  {
    def p = properties["render${attrs.annotation.class.simpleName}Class"]
    if(p)
    {
      p(attrs)
    }
    else
      out << "---${attrs.annotation.class.simpleName}:${attrs.annotation.toString()}---".encodeAsHTML();
  }

}

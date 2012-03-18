
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

package com.pongasoft.kiwidoc.builder.bytecode;

import com.pongasoft.kiwidoc.model.annotation.Annotation;
import com.pongasoft.kiwidoc.model.annotation.AnnotationAnnotationValue;
import com.pongasoft.kiwidoc.model.annotation.AnnotationElement;
import com.pongasoft.kiwidoc.model.annotation.AnnotationValue;
import com.pongasoft.kiwidoc.model.annotation.ArrayAnnotationValue;
import com.pongasoft.kiwidoc.model.annotation.ClassAnnotationValue;
import com.pongasoft.kiwidoc.model.annotation.EnumAnnotationValue;
import com.pongasoft.kiwidoc.model.annotation.PrimitiveAnnotationValue;
import com.pongasoft.kiwidoc.model.annotation.StringAnnotationValue;
import com.pongasoft.kiwidoc.model.type.GenericType;
import com.pongasoft.kiwidoc.model.type.PrimitiveType;
import com.pongasoft.kiwidoc.model.type.SimpleType;
import org.objectweb.asm.AnnotationVisitor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author yan@pongasoft.com
 */
public class KiwidocAnnotationVisitor implements AnnotationVisitor
{
  public static interface Setter
  {
    void setElements(Collection<AnnotationElement> elements);
  }

  private final Setter _setter;
  private final Collection<AnnotationElement> _elements =
    new ArrayList<AnnotationElement>();

  /**
   * Constructor
   */
  public KiwidocAnnotationVisitor(Setter setter)
  {
    _setter = setter;
  }

  public void visit(String name, Object value)
  {
    _elements.add(new AnnotationElement(name, toAnnotationValue(value)));
  }

  private AnnotationValue toAnnotationValue(Object value)
  {
    if(value instanceof org.objectweb.asm.Type)
    {
      org.objectweb.asm.Type asmType = (org.objectweb.asm.Type) value;
      return new ClassAnnotationValue(ByteCodeHelper.decodeType(asmType.getDescriptor()));
    }
    else
    {
      if(value instanceof String)
      {
        return new StringAnnotationValue(value.toString());
      }
      else
      {
        return new PrimitiveAnnotationValue(new PrimitiveType(value.getClass()),
                                            value.toString());
      }
    }
  }

  public AnnotationVisitor visitAnnotation(final String name, final String desc)
  {
    return new KiwidocAnnotationVisitor(new Setter()
    {
      public void setElements(Collection<AnnotationElement> elements)
      {
        Annotation annotation = 
          new Annotation((SimpleType) ByteCodeHelper.decodeType(desc), elements);
        _elements.add(new AnnotationElement(name, new AnnotationAnnotationValue(annotation)));
      }
    });
  }

  public AnnotationVisitor visitArray(final String name)
  {
    return new KiwidocAnnotationVisitor(new Setter()
    {
      public void setElements(Collection<AnnotationElement> elements)
      {
        Collection<AnnotationValue> values = new ArrayList<AnnotationValue>();
        for(AnnotationElement element : elements)
        {
          values.add(element.getAnnotationValue());
        }
        _elements.add(new AnnotationElement(name, new ArrayAnnotationValue(values)));
      }
    });
  }

  public void visitEnd()
  {
    _setter.setElements(_elements);
  }

  public void visitEnum(String name, String desc, String value)
  {
    _elements.add(new AnnotationElement(name,
                                        new EnumAnnotationValue((GenericType) ByteCodeHelper.decodeType(desc),
                                                                value)));
  }

  public Collection<AnnotationElement> getElements()
  {
    return _elements;
  }
}

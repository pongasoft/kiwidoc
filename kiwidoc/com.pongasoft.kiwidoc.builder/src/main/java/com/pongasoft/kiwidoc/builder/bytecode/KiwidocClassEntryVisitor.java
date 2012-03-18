
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

import com.pongasoft.kiwidoc.model.AnnotationModel;
import com.pongasoft.kiwidoc.model.AnnotationsModel;
import com.pongasoft.kiwidoc.model.annotation.Annotation;
import com.pongasoft.kiwidoc.model.annotation.AnnotationElement;
import com.pongasoft.kiwidoc.model.type.GenericType;
import com.pongasoft.kiwidoc.model.type.Type;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectweb.asm.AnnotationVisitor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Checking that the opcodes values matches... and that is the case...
 * Modifiers.values
 * <pre>
 * ABSTRACT=1024
 * ANNOTATION=8192
 * BRIDGE=64
 * ENUM=16384
 * FINAL=16
 * INTERFACE=512
 * NATIVE=256
 * PRIVATE=2
 * PROTECTED=4
 * PUBLIC=1
 * STATIC=8
 * STRICT=2048
 * SYNCHRONIZED=32
 * SYNTHETIC=4096
 * TRANSIENT=128
 * VARARGS=128
 * VOLATILE=64
 * </pre>
 *
 * <pre>
 * Opcodes.values=
 * ACC_ABSTRACT=1024
 * ACC_ANNOTATION=8192
 * ACC_BRIDGE=64
 * ACC_DEPRECATED=131072
 * ACC_ENUM=16384
 * ACC_FINAL=16
 * ACC_INTERFACE=512
 * ACC_NATIVE=256
 * ACC_PRIVATE=2
 * ACC_PROTECTED=4
 * ACC_PUBLIC=1
 * ACC_STATIC=8
 * ACC_STRICT=2048
 * ACC_SUPER=32
 * ACC_SYNCHRONIZED=32
 * ACC_SYNTHETIC=4096
 * ACC_TRANSIENT=128
 * ACC_VARARGS=128
 * ACC_VOLATILE=64
 </pre>
 * @author yan@pongasoft.com
 */
public class KiwidocClassEntryVisitor
{
  public static final Log log = LogFactory.getLog(KiwidocClassEntryVisitor.class);

  private final Collection<AnnotationModel> _annotations = new ArrayList<AnnotationModel>();
  private int _access;
  private String _name;

  /**
   * Constructor
   */
  public KiwidocClassEntryVisitor()
  {
  }

  /**
   * Constructor
   */
  public KiwidocClassEntryVisitor(int access, String name)
  {
    init(access, name);
  }

  public void init(int access, String name)
  {
    _access = access;
    _name = name;
  }

  public AnnotationVisitor visitAnnotation(final String desc, boolean visible)
  {
    return new KiwidocAnnotationVisitor(new KiwidocAnnotationVisitor.Setter()
    {
      public void setElements(Collection<AnnotationElement> elements)
      {
        Type type = ByteCodeHelper.decodeType(desc);
        if(!(type instanceof GenericType))
        {
          log.warn("Annotation not a simple type: " + desc + " | " + type + "... (ignored)");
        }
        else
        {
          Annotation annotation = new Annotation((GenericType) type, elements);
          _annotations.add(new AnnotationModel(annotation));
        }
      }
    });
  }

  public AnnotationsModel getAnnotations()
  {
    return new AnnotationsModel(_annotations);
  }

  public int getAccess()
  {
    return _access;
  }

  public String getName()
  {
    return _name;
  }
}
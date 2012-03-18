
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

import com.pongasoft.kiwidoc.model.FieldModel;
import com.pongasoft.kiwidoc.model.type.Type;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;

/**
 * @author yan@pongasoft.com
 */
public class KiwidocFieldVisitor extends KiwidocClassEntryVisitor implements FieldVisitor
{
  private final Type _fieldType;
  private final Object _value;

  private FieldModel _fieldModel;

  /**
   * Constructor
   */
  public KiwidocFieldVisitor(int access, String name, Type fieldType, Object value)
  {
    super(access, name);
    _fieldType = fieldType;
    _value = value;
  }

  public void visitAttribute(Attribute attribute)
  {
  }

  public void visitEnd()
  {
    _fieldModel = new FieldModel(getAccess(),
                                 getName(),
                                 null,
                                 getAnnotations(),
                                 _fieldType,
                                 _value);
  }

  public FieldModel getFieldModel()
  {
    return _fieldModel;
  }
}

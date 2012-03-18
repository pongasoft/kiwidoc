
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

import com.pongasoft.kiwidoc.model.type.Type;
import com.pongasoft.kiwidoc.model.resource.ResolvableResource;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.resource.FieldResource;

import java.util.Collection;

/**
 * @author yan@pongasoft.com
 */
public class FieldModel extends ClassEntryModel implements ClassMember
{
  private final Type _type;
  private final Object _value;

  public FieldModel(int access,
                    String name,
                    DocModel doc,
                    AnnotationsModel annotations,
                    Type type,
                    Object value)
  {
    super(access, name, doc, annotations);
    _type = type;
    _value = value;
  }

  public FieldModel(ClassEntryModel cem,
                    Type type,
                    Object value)
  {
    this(cem.getAccess(),
         cem.getName(),
         cem.getDoc(),
         cem.getAnnotationsModel(),
         type,
         value);
  }

  public boolean isEnumConstant()
  {
    return is(Access.ENUM);
  }

  /**
   * This method returns a version of the field model containing only entries that are part of the
   * public api. If this field is not part of the public api itself, then it returns
   * <code>null</code>. If this field is entirely public, then it returns <code>this</code>.
   * Otherwise a new instance will be created and populated with only the public api entries.
   */
  public FieldModel toPublicAPI()
  {
    if(!isPublicAPI())
      return null;

    if(containsOnlyPublicEntries())
      return this;

    return new FieldModel(super.toPubliAPI(),
                          getType(),
                          getValue());
  }

  public FieldResource getResource(ClassResource parent)
  {
    return new FieldResource(parent, getMemberName());
  }

  /**
   * Collect the dependencies in the collection.
   *
   * @param dependencies to add the dependencies to it
   */
  @Override
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    super.collectDependencies(dependencies);
    _type.collectDependencies(dependencies);
  }

  /**
   * @return the unique member name in the class
   */
  public String getMemberName()
  {
    return getName();
  }

  /**
   * Assigns the generics to the generic variables (ex: E =&gt; Number)
   * @return the new model (or <code>this</code> if no generics...)
   */
  public FieldModel assignGenericTypeVariable(String name, Type value)
  {
    // we do not replace static fields...
    if(is(Access.STATIC))
      return this;

    Type newType = _type.assignGenericTypeVariable(name, value);
    
    if(newType != _type)
      return new FieldModel(this,
                            newType,
                            value);
    else
      return this;
  }
  
  public Type getType()
  {
    return _type;
  }

  public Object getValue()
  {
    return _value;
  }
}
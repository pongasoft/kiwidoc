
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

package com.pongasoft.kiwidoc.model.tag;

import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.Collection;

/**
 * @author yan@pongasoft.com
 */
public class ParamTag extends DocType
{
  private final String _parameterName;
  private final boolean _typeParameter;

  /**
   * Constructor
   */
  public ParamTag(Resource origin,
                  String name,
                  Collection<InlineTag> inlineTags,
                  String parameterName,
                  boolean isTypeParameter)
  {
    super(origin, name, inlineTags);

    _parameterName = parameterName;
    _typeParameter = isTypeParameter;
  }

  /**
   * @return the kind of this tag
   * @see Kind
   */
  public Kind getKind()
  {
    return Kind.PARAM;
  }

  /**
   * Replaces the inheritDoc tag with the tag provided
   */
  public Tag inheritDoc(Tag inheritedDoc)
  {
    return new ParamTag(inheritedDoc.getOrigin(),
                        getName(),
                        doInheritDoc(inheritedDoc),
                        getParameterName(), 
                        getIsTypeParameter());
  }

  public String getParameterName()
  {
    return _parameterName;
  }

  public boolean getIsTypeParameter()
  {
    return _typeParameter;
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append(getName());
    sb.append(' ');
    if(getIsTypeParameter())
      sb.append('<');
    sb.append(getParameterName());
    if(getIsTypeParameter())
      sb.append('>');
    sb.append(' ').append(getText());
    return sb.toString();
  }
}

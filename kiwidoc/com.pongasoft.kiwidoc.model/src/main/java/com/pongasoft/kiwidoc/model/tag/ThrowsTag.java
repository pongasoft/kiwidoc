
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

import com.pongasoft.kiwidoc.model.type.Type;
import com.pongasoft.kiwidoc.model.resource.ResolvableResource;
import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.Collection;

/**
 * @author yan@pongasoft.com
 */
public class ThrowsTag extends DocType
{
  private final String _exceptionName;
  private final Type _exceptionType;

  /**
   * Constructor
   * @param exceptionType the type may be <code>null</code> if cannot be resolved by Javadoc...
   */
  public ThrowsTag(Resource origin,
                   String name,
                   Collection<InlineTag> inlineTags,
                   String exceptionName,
                   Type exceptionType)
  {
    super(origin, name, inlineTags);
    _exceptionName = exceptionName;
    _exceptionType = exceptionType;
  }

  /**
   * @return the kind of this tag
   * @see Kind
   */
  public Kind getKind()
  {
    return Kind.THROWS;
  }

  /**
   * Replaces the inheritDoc tag with the tag provided
   */
  public Tag inheritDoc(Tag inheritedDoc)
  {
    return new ThrowsTag(inheritedDoc.getOrigin(),
                         getName(),
                         doInheritDoc(inheritedDoc),
                         getExceptionName(), 
                         getExceptionType());
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
    if(_exceptionType != null)
      _exceptionType.collectDependencies(dependencies);
  }

  public String getExceptionName()
  {
    return _exceptionName;
  }

  public Type getExceptionType()
  {
    return _exceptionType;
  }
}

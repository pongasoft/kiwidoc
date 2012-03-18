
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

package com.pongasoft.kiwidoc.model.resource;

import java.io.Serializable;

/**
 * @author yan@pongasoft.com
 */
public abstract class AbstractResource<R extends Resource, P extends Resource>
  implements Resource<R, P>,
  Serializable
{
  private static final long serialVersionUID = 1L;

  private P _parentResource;

  /**
   * Constructor
   */
  public AbstractResource(P parentResource)
  {
    _parentResource = parentResource;
  }

  /**
   * @return the parent resource or <code>null</code> if there is no parent
   */
  public P getParent()
  {
    return _parentResource;
  }

  /**
   * Resolvable resources can set their parent after the fact.
   *
   * @param parent the parent resource
   */
  protected void initParent(P parent)
  {
    if(_parentResource != null)
      throw new IllegalStateException("alredy initialized");
    
    _parentResource = parent;
  }

  /**
   * @return <code>true</code> if it is the root
   */
  public boolean isRoot()
  {
    return getDepth() == 0;
  }

  @Override
  public boolean equals(Object o)
  {
    if(this == o) return true;
    if(!(o instanceof AbstractResource)) return false;

    AbstractResource that = (AbstractResource) o;

    if(_parentResource != null ?
      !_parentResource.equals(that._parentResource) :
      that._parentResource != null) return false;

    return true;
  }

  @Override
  public int hashCode()
  {
    return _parentResource != null ? _parentResource.hashCode() : 0;
  }
}

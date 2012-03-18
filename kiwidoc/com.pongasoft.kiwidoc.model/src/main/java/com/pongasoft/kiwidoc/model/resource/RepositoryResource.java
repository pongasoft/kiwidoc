
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

import com.pongasoft.kiwidoc.model.Model;

/**
 * @author yan@pongasoft.com
 */
public class RepositoryResource extends AbstractResource<RepositoryResource, RepositoryResource>
{
  private static final long serialVersionUID = 1L;

  public static final RepositoryResource INSTANCE = new RepositoryResource();

  public static RepositoryResource instance()
  {
    return INSTANCE;
  }

  /**
   * Constructor
   */
  public RepositoryResource()
  {
    super(null);
  }

  /**
   * @return the depth of the resource (root is 0, child of root is 1, etc...)
   */
  public int getDepth()
  {
    return 0;
  }

  /**
   * @return the name
   */
  public String getResourceName()
  {
    return "";
  }

  /**
   * Clones this resource using the parent provided.
   *
   * @param parent the parent to use
   * @return a new resource using the parent provided
   */
  public RepositoryResource clone(RepositoryResource parent)
  {
    return this;
  }

  /**
   * @return the model kind associated to this resource
   */
  public Model.Kind getModelKind()
  {
    return Model.Kind.REPOSITORY;
  }

  @Override
  public String toString()
  {
    return "/";
  }

  @Override
  public boolean equals(Object o)
  {
    if(this == o) return true;
    if(o == null || getClass() != o.getClass()) return false;
    if(!super.equals(o)) return false;

    return true;
  }
}

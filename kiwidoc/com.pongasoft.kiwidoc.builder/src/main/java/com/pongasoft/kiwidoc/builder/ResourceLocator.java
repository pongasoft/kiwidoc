
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

package com.pongasoft.kiwidoc.builder;

import com.pongasoft.kiwidoc.model.resource.Resource;
import org.apache.commons.vfs.FileObject;

/**
 * Abstraction to locate a resource.
 *
 * @author yan@pongasoft.com
 */
public interface ResourceLocator
{
  /**
   * Locates a resource.
   * 
   * @param resource the resource to locate
   * @return the located resource... note that if the resource does not exist, then it will
   * return a <code>FileObject</code> which does not exist... never <code>null</code>.
   * @throws StoreException if there is a problem
   */
  FileObject locateResource(Resource resource) throws StoreException;
}
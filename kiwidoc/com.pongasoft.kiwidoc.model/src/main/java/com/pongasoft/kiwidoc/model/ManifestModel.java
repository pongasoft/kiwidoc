
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

import com.pongasoft.kiwidoc.model.resource.ManifestResource;
import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.Collection;
import java.util.Collections;
import java.util.jar.Manifest;

/**
 * @author yan@pongasoft.com
 */
public class ManifestModel implements Model<ManifestResource>
{
  private final Manifest _manifest;
  private final ManifestResource _manifestResource;

  /**
   * Constructor
   */
  public ManifestModel(ManifestResource manifestResource,
                       Manifest manifest)
  {
    _manifestResource = manifestResource;
    _manifest = manifest;
  }

  /**
   * @return the resource
   */
  public ManifestResource getResource()
  {
    return _manifestResource;
  }

  /**
   * @return the model kind
   */
  public Kind getKind()
  {
    return Kind.MANIFEST;
  }

  public Manifest getManifest()
  {
    return _manifest;
  }

  /**
   * @return a version of this model with everything that is not part of the public api which has
   *         been stripped out. If the model itself is not part of the public api then
   *         <code>null</code> is returned!
   */
  public ManifestModel toPublicAPI()
  {
    // TODO HIGH YP:  todo...
    return this;
  }

  /**
   * @return <code>true</code> if this model is part of the public api
   */
  public boolean isPublicAPI()
  {
    return true;
  }

  @Override
  public Collection<? extends Resource> getChildren()
  {
    return Collections.emptyList();
  }
}

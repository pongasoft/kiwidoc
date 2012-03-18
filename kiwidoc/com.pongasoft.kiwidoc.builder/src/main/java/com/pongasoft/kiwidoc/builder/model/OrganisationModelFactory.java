
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

package com.pongasoft.kiwidoc.builder.model;

import com.pongasoft.kiwidoc.model.OrganisationModel;
import com.pongasoft.kiwidoc.model.resource.OrganisationResource;

import java.util.Collection;

/**
 * @author yan@pongasoft.com
 */
public class OrganisationModelFactory implements ModelFactory<OrganisationResource, OrganisationModel>
{
  /**
   * Constructor
   */
  public OrganisationModelFactory()
  {
  }

  /**
   * @return the model
   */
  public OrganisationModel buildModel(OrganisationResource resource,
                                      Collection<String> childResources)
  {
    return new OrganisationModel(resource, childResources);
  }
}
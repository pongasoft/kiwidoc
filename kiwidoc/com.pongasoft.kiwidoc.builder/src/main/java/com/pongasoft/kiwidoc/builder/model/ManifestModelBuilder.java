
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

import aQute.libg.header.OSGiHeader;
import com.pongasoft.kiwidoc.model.ManifestModel;
import com.pongasoft.kiwidoc.model.OSGiModel;
import com.pongasoft.kiwidoc.model.resource.ManifestResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author yan@pongasoft.com
 */
public class ManifestModelBuilder implements ModelBuilder<ManifestResource, ManifestModel>
{
  private Manifest _manifest;

  private LibraryModelBuilder _libraryModelBuilder;

  /**
   * Constructor
   */
  public ManifestModelBuilder()
  {
  }

  public LibraryModelBuilder getLibraryModelBuilder()
  {
    return _libraryModelBuilder;
  }

  public void setLibraryModelBuilder(LibraryModelBuilder libraryModelBuilder)
  {
    _libraryModelBuilder = libraryModelBuilder;
  }

  public Manifest getManifest()
  {
    return _manifest;
  }

  public void setManifest(Manifest manifest)
  {
    _manifest = manifest;
  }

  /**
   * @return the model
   */
  public ManifestModel buildModel()
  {
    return new ManifestModel(new ManifestResource(_libraryModelBuilder.getLibraryVersionResource()),
                             _manifest);
  }

  public OSGiModel buildOSGiModel()
  {
    if(!hasManifest())
      return null;

    Attributes attributes = getManifest().getMainAttributes();
    if(attributes == null)
      return null;

    Collection<OSGiModel.Header> headers = new ArrayList<OSGiModel.Header>();

    for(String headerName : OSGiModel.OSGI_HEADERS)
    {
      String value = attributes.getValue(headerName);
      if(value != null)
      {
        Map<String, Map<String,String>> headerValue = OSGiHeader.parseHeader(value);
        headers.add(new OSGiModel.Header(headerName, headerValue));
      }
    }

    if(headers.isEmpty())
      return null;

    OSGiModel model = new OSGiModel(headers);

    if(model.getBundleSymbolicName() == null)
      return null;

    return model;
  }

  public boolean hasManifest()
  {
    return _manifest != null;
  }
}


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

package com.pongasoft.kiwidoc.builder.serializer.model;

import com.pongasoft.kiwidoc.builder.serializer.Serializer;
import com.pongasoft.kiwidoc.builder.serializer.SerializerException;
import com.pongasoft.kiwidoc.model.ManifestModel;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.ManifestResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Manifest;

import static com.pongasoft.kiwidoc.builder.serializer.SerializerUtils.*;

/**
 * @author yan@pongasoft.com
 */
public class ManifestModelSerializer implements Serializer<ManifestModel, Object>
{
  private final Serializer<LibraryVersionResource, Object> _libraryResourceSerializer;

  public static class FManifestModel
  {
    public static final String library = "l";
    public static final String manifest = "m";
  }

  /**
   * Constructor
   */
  public ManifestModelSerializer(Serializer<LibraryVersionResource, Object> libraryResourceSerializer)
  {
    _libraryResourceSerializer = libraryResourceSerializer;
  }

  public Object serialize(ManifestModel manifest) throws SerializerException
  {
    if(manifest == null)
      return null;

    try
    {
      Map<String, Object> content = new HashMap<String, Object>();
      putOnce(content, FManifestModel.library, _libraryResourceSerializer.serialize(manifest.getResource().getLibraryVersionResource()));
      putOnce(content, FManifestModel.manifest, serializeManifest(manifest.getManifest()));
      return content;
    }
    catch(IOException e)
    {
      throw new SerializerException(e);
    }

  }

  public ManifestModel deserialize(Object context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return null;

    Map<String, Object> content = (Map<String, Object>) objectToDeserialize;

    LibraryVersionResource libraryVersionResource;
    libraryVersionResource =
      _libraryResourceSerializer.deserialize(context, req(content, FManifestModel.library));

    try
    {
      return new ManifestModel(new ManifestResource(libraryVersionResource),
                               deserializeManifest((String) opt(content, FManifestModel.manifest)));
    }
    catch(IOException e)
    {
      throw new SerializerException(e);
    }
  }

  private Manifest deserializeManifest(String string) throws IOException
  {
    return new Manifest(new ByteArrayInputStream(string.getBytes("UTF-8")));
  }

  private String serializeManifest(Manifest manifest) throws IOException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    manifest.write(baos);
    return new String(baos.toByteArray(), "UTF-8");
  }
}

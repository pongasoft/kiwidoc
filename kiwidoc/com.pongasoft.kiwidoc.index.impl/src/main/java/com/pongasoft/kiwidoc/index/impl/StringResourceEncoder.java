
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

package com.pongasoft.kiwidoc.index.impl;

import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.kiwidoc.model.resource.RepositoryResource;
import com.pongasoft.kiwidoc.model.resource.LibraryResource;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.ManifestResource;
import com.pongasoft.kiwidoc.model.resource.PackageResource;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.resource.MethodResource;
import com.pongasoft.kiwidoc.model.resource.OrganisationResource;
import com.pongasoft.util.core.net.URIPath;

import java.net.URISyntaxException;

/**
 * @author yan@pongasoft.com
 */
public class StringResourceEncoder implements ResourceEncoder<String>
{
  public static final String REPOSITORY      = "R";
  public static final String ORGANISATION    = "O";
  public static final String LIBRARY         = "L";
  public static final String LIBRARY_VERSION = "V";
  public static final String MANIFEST        = "M";
  public static final String PACKAGE         = "P";
  public static final String CLASS           = "C";
  public static final String METHOD          = "D";
  
  private final ResourceEncoder<Resource> _resourceEncoder;

  /**
   * Constructor
   */
  public StringResourceEncoder()
  {
    this(NoOpResourceEncoder.INSTANCE);
  }

  /**
   * Constructor
   */
  public StringResourceEncoder(ResourceEncoder<Resource> resourceEncoder)
  {
    _resourceEncoder = resourceEncoder;
  }

  /**
   * @param resource
   * @return the encoded resource (should be more compact that original resource...)
   */
  public String encodeResource(Resource resource)
  {
    resource = _resourceEncoder.encodeResource(resource);

    if(resource instanceof ClassResource)
    {
      return encode((ClassResource) resource, URIPath.createFromPathElement(CLASS)).toString();
    }

    if(resource instanceof ManifestResource)
    {
      return encode((ManifestResource) resource, URIPath.createFromPathElement(MANIFEST)).toString();
    }

    if(resource instanceof PackageResource)
    {
      return encode((PackageResource) resource, URIPath.createFromPathElement(PACKAGE)).toString();
    }

    if(resource instanceof LibraryResource)
    {
      return encode((LibraryResource) resource, URIPath.createFromPathElement(LIBRARY)).toString();
    }

    if(resource instanceof LibraryVersionResource)
    {
      return encode((LibraryVersionResource) resource, URIPath.createFromPathElement(LIBRARY_VERSION)).toString();
    }

    if(resource instanceof MethodResource)
    {
      return encode((MethodResource) resource, URIPath.createFromPathElement(METHOD)).toString();
    }

    if(resource instanceof OrganisationResource)
    {
      return encode((OrganisationResource) resource, URIPath.createFromPathElement(ORGANISATION)).toString();
    }

    if(resource instanceof RepositoryResource)
    {
      return REPOSITORY;
    }

    // not reached
    throw new RuntimeException("unknown resource " + resource.getClass().getName());
  }

  private URIPath encode(OrganisationResource resource, URIPath path)
  {
    return path.addPathElements(resource.getOrganisation());
  }

  private URIPath encode(LibraryResource resource, URIPath path)
  {
    return encode(resource.getParent(), path).addPathElement(resource.getName());
  }

  private URIPath encode(LibraryVersionResource resource, URIPath path)
  {
    return encode(resource.getLibraryResource(), path).addPathElement(resource.getVersion());
  }

  private URIPath encode(ManifestResource resource, URIPath path)
  {
    return encode(resource.getLibraryVersionResource(), path);
  }

  private URIPath encode(PackageResource resource, URIPath path)
  {
    return encode(resource.getLibraryVersionResource(), path).addPathElement(resource.getPackageName());
  }

  private URIPath encode(ClassResource resource, URIPath path)
  {
    return encode(resource.getPackageResource(), path).addPathElement(resource.getSimpleClassName());
  }

  private URIPath encode(MethodResource resource, URIPath path)
  {
    return encode(resource.getClassResource(), path).addPathElement(resource.getMethodName());
  }

  /**
   * Decodes the previously encoded resource.
   *
   * @param encodedResource the resource as encoded with {@link #encodeResource(Resource)}
   * @param <R>             type of the resource
   * @return the decoded resource
   */
  public <R extends Resource> R decodeResource(String encodedResource)
  {
    if(encodedResource == null)
      return null;

    try
    {
      URIPath uri = URIPath.createFromPath(encodedResource);
      String[] elements = uri.getPathElements();

      String type = elements[0];
      if(CLASS.equals(type))
      {
        @SuppressWarnings("unchecked")
        R res = (R) decodeClassResource(elements);
        return res;
      }

      if(MANIFEST.equals(type))
      {
        @SuppressWarnings("unchecked")
        R res = (R) decodeManifestResource(elements);
        return res;
      }

      if(PACKAGE.equals(type))
      {
        @SuppressWarnings("unchecked")
        R res = (R) decodePackageResource(elements);
        return res;
      }

      if(LIBRARY.equals(type))
      {
        @SuppressWarnings("unchecked")
        R res = (R) decodeLibraryResource(elements);
        return res;
      }

      if(LIBRARY_VERSION.equals(type))
      {
        @SuppressWarnings("unchecked")
        R res = (R) decodeLibraryVersionResource(elements);
        return res;
      }

      if(ORGANISATION.equals(type))
      {
        @SuppressWarnings("unchecked")
        R res = (R) decodeOrganisationResource(elements);
        return res;
      }

      if(METHOD.equals(type))
      {
        @SuppressWarnings("unchecked")
        R res = (R) decodeMethodResource(elements);
        return res;
      }

      if(REPOSITORY.equals(type))
      {
        @SuppressWarnings("unchecked")
        R res = (R) getResource(RepositoryResource.INSTANCE);
        return res;
      }
    }
    catch(URISyntaxException e)
    {
      throw new IllegalArgumentException("not properly encoded resource: " + encodedResource, e);
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      throw new IllegalArgumentException("not properly encoded resource: " + encodedResource, e);
    }

    // not reached
    throw new IllegalArgumentException("not properly encoded resource (unkown prefix): " + encodedResource);
  }

  /**
   * Decodes the previously encoded resources.
   *
   * @param encodedResources the resources as encoded with {@link #encodeResource(Resource)}
   * @return the decoded resources
   */
  public Resource[] decodeResources(String[] encodedResources)
  {
    if(encodedResources == null)
      return null;

    Resource[] res = new Resource[encodedResources.length];

    for(int i = 0; i < encodedResources.length; i++)
    {
      res[i] = decodeResource(encodedResources[i]);
    }

    return res;
  }

  private OrganisationResource decodeOrganisationResource(String[] elements)
  {
    return getResource(new OrganisationResource(elements[1]));
  }

  private LibraryResource decodeLibraryResource(String[] elements)
  {
    return getResource(new LibraryResource(decodeOrganisationResource(elements),
                                           elements[2]));
  }

  private LibraryVersionResource decodeLibraryVersionResource(String[] elements)
  {
    return getResource(new LibraryVersionResource(decodeLibraryResource(elements),
                                                  elements[3]));
  }

  private ManifestResource decodeManifestResource(String[] elements)
  {
    return getResource(new ManifestResource(decodeLibraryVersionResource(elements)));
  }

  private PackageResource decodePackageResource(String[] elements)
  {
    return getResource(new PackageResource(decodeLibraryVersionResource(elements),
                                           elements[4]));
  }

  private ClassResource decodeClassResource(String[] elements)
  {
    return getResource(new ClassResource(decodePackageResource(elements), elements[5]));
  }

  private MethodResource decodeMethodResource(String[] elements)
  {
    return getResource(new MethodResource(decodeClassResource(elements), elements[6]));
  }

  private <R extends Resource> R getResource(R resource)
  {
    return _resourceEncoder.<R>decodeResource(resource);
  }
}

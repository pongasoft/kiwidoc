
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

package com.pongasoft.kiwidoc.builder.path;

import com.pongasoft.kiwidoc.model.ClassModel;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.resource.FieldResource;
import com.pongasoft.kiwidoc.model.resource.LVROverviewResource;
import com.pongasoft.kiwidoc.model.resource.LibraryResource;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.ManifestResource;
import com.pongasoft.kiwidoc.model.resource.MethodResource;
import com.pongasoft.kiwidoc.model.resource.OrganisationResource;
import com.pongasoft.kiwidoc.model.resource.PackageResource;
import com.pongasoft.kiwidoc.model.resource.PathManager;
import com.pongasoft.kiwidoc.model.resource.RepositoryResource;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.util.core.net.URIPath;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author yan@pongasoft.com
 */
public class URLPathManager implements PathManager
{
  public static final String OVERVIEW = "o";
  public static final String MANIFEST = "m";
  public static final String PACKAGES = "p";
  public static final String CLASSES = "c";

  /**
   * Constructor
   */
  public URLPathManager()
  {
  }

  /**
   * Take into account the optional base path
   */
  private URIPath createPath()
  {
    URIPath path = new URIPath();
    return path;
  }

  /**
   * @param resource
   * @return the path to the resource
   */
  public URI computePath(Resource resource)
  {
    if(resource instanceof RepositoryResource)
    {
      return createPath().toURI();
    }

    if(resource instanceof OrganisationResource)
    {
      return computeOrganisationPath((OrganisationResource) resource);
    }

    if(resource instanceof LibraryResource)
    {
      return computeLibraryPath((LibraryResource) resource);
    }

    if(resource instanceof LibraryVersionResource)
    {
      return computeLibraryVersionPath((LibraryVersionResource) resource);
    }

    if(resource instanceof LVROverviewResource)
    {
      return computeLVROverviewPath((LVROverviewResource) resource);
    }

    if(resource instanceof ManifestResource)
    {
      return computeManifestPath((ManifestResource) resource);
    }

    if(resource instanceof PackageResource)
    {
      return computePackagePath((PackageResource) resource);
    }

    if(resource instanceof ClassResource)
    {
      return computeClassPath((ClassResource) resource);
    }

    if(resource instanceof MethodResource)
    {
      return computeMethodPath((MethodResource) resource);
    }

    if(resource instanceof FieldResource)
    {
      return computeFieldPath((FieldResource) resource);
    }

    // not reached
    throw new RuntimeException("unknown resource " + resource.getClass().getName());
  }

  /**
   * @return the path to an organisation
   */
  private URI computeOrganisationPath(OrganisationResource organisationResource)
  {
    URIPath path = createPath();

    path = path.addPathElement(organisationResource.getOrganisation());

    return path.toURI();
  }

  /**
   * @return the path to the library
   */
  private URI computeLibraryPath(LibraryResource libraryResource)
  {
    URIPath path = createPath();

    path = path.addPathElements(libraryResource.getOrganisation());
    path = path.addPathElement(libraryResource.getName());

    return path.toURI();
  }

  /**
   * @return the path to the library version
   */
  public URI computeLibraryVersionPath(LibraryVersionResource libraryVersionResource)
  {
    URIPath path = createPath();

    path = path.addPathElements(libraryVersionResource.getOrganisation());
    path = path.addPathElement(libraryVersionResource.getName());
    path = path.addPathElement(libraryVersionResource.getVersion());

    return path.toURI();
  }

  /**
   * @return the path to the lvr overview
   */
  public URI computeLVROverviewPath(LVROverviewResource lvrOverviewResource)
  {
    return computeLVROverviewPath(computeLibraryVersionPath(lvrOverviewResource.getLibraryVersionResource()));
  }

  /**
   * @return the path to the lvr overview
   */
  public URI computeLVROverviewPath(URI libraryPath)
  {
    URIPath path = createPath();

    path = path.addPath(URIPath.createFromURI(libraryPath));
    path = path.addPathElement(OVERVIEW);

    return path.toURI();
  }

  /**
   * @return the path to the manifest
   */
  public URI computeManifestPath(ManifestResource manifestResource)
  {
    return computeManifestPath(computeLibraryVersionPath(manifestResource.getLibraryVersionResource()));
  }

  /**
   * @return the path to the manifest
   */
  public URI computeManifestPath(URI libraryPath)
  {
    URIPath path = createPath();

    path = path.addPath(URIPath.createFromURI(libraryPath));
    path = path.addPathElement(MANIFEST);

    return path.toURI();
  }

  /**
   * @return the path to the package
   */
  public URI computePackagePath(PackageResource packageResource)
  {
    return computePackagePath(computeLibraryVersionPath(packageResource.getLibraryVersionResource()),
                              packageResource.getPackageName());
  }

  /**
   * @return the path to the package
   */
  private URI computePackagePath(URI libraryPath, String packageName)
  {
    URIPath path = createPath();

    path = path.addPath(URIPath.createFromURI(libraryPath));
    path = path.addPathElement(PACKAGES);
    path = path.addPathElement(packageName);

    return path.toURI();
  }

  /**
   * @return the path to the class
   */
  public URI computeClassPath(ClassResource classResource)
  {
    return computeClassPath(computePackagePath(computeLibraryVersionPath(classResource.getLibraryVersionResource()),
                                               ClassModel.computePackageName(classResource.getFqcn())),
                            ClassModel.computeSimpleName(classResource.getFqcn()));
  }

  /**
   * @param simpleClassName this is the name of the class <em>without!</em> the package
   * @return the path to the class
   */
  private URI computeClassPath(URI packagePath, String simpleClassName)
  {
    URIPath path = createPath();

    path = path.addPath(URIPath.createFromURI(packagePath));
    path = path.addPathElement(CLASSES);
    path = path.addPathElement(simpleClassName.replace('$', '.'));

    return path.toURI();
  }

  /**
   * Computes the path for a method (fragment addition!)
   */
  private URI computeMethodPath(MethodResource methodResource)
  {
    return computeMethodOrFieldPath(methodResource.getClassResource(),
                                    methodResource.getResourceName());
  }

  /**
   * Computes the path for a field (fragment addition!)
   */
  private URI computeFieldPath(FieldResource fieldResource)
  {
    return computeMethodOrFieldPath(fieldResource.getClassResource(),
                                    fieldResource.getResourceName());
  }

  /**
   * Computes the path for a method (fragment addition!)
   */
  private URI computeMethodOrFieldPath(ClassResource classResource, String name)
  {
    URI uri = computeClassPath(classResource);
    try
    {
      uri = new URI(null, null, uri.getPath(), name);
    }
    catch(URISyntaxException e)
    {
      // should not happen...
      throw new RuntimeException(e);
    }
    return uri;
  }

  /**
   * This method properly takes care of the basePath provided in the constructor. In other words
   * this method will work only if the URI was generated with the same basePath.
   *
   * @return the resource given its path (should have been generated with {@link #computePath(Resource)}.
   */
  public Resource computeResource(URI path)
  {
    if(path == null)
      return null;
    
    URIPath uriPath = URIPath.createFromURI(path);
    String[] pathElements = uriPath.getPathElements();

    if(pathElements.length == 0)
      return RepositoryResource.INSTANCE;

    if(pathElements.length == 1)
    {
      if(pathElements[0].equals(""))
        return RepositoryResource.INSTANCE;
      else
        return new OrganisationResource(pathElements[0]);
    }

    if(pathElements.length == 1)
      return null;

    LibraryResource libraryResource = new LibraryResource(pathElements[0], pathElements[1]);

    if(pathElements.length == 2)
    {
      return libraryResource;
    }

    LibraryVersionResource libraryVersionResource = new LibraryVersionResource(libraryResource,
                                                                               pathElements[2]);

    if(pathElements.length > 3)
    {
      String pathElement = pathElements[3];
      if(MANIFEST.equals(pathElement))
        return new ManifestResource(libraryVersionResource);
      if(OVERVIEW.equals(pathElement))
        return new LVROverviewResource(libraryVersionResource);
      if(PACKAGES.equals(pathElement))
        return computePackageOrClassResource(libraryVersionResource, pathElements);
    }

    return libraryVersionResource;
  }

  private Resource computePackageOrClassResource(LibraryVersionResource libraryVersionResource,
                                                 String[] pathElements)
  {
    if(pathElements.length > 4)
    {
      String packageName = pathElements[4];
      PackageResource packageResource = new PackageResource(libraryVersionResource, packageName);

      if(pathElements.length > 5 && CLASSES.equals(pathElements[5]))
      {
        if(pathElements.length > 6)
        {
          return new ClassResource(packageResource, pathElements[6].replace('.', '$'));
        }
      }
      return packageResource;
    }
    
    return libraryVersionResource;
  }
}
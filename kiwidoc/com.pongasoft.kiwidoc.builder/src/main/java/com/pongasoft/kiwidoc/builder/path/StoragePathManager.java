
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
import com.pongasoft.kiwidoc.model.resource.HierarchyResource;
import com.pongasoft.kiwidoc.model.resource.LVROverviewResource;
import com.pongasoft.kiwidoc.model.resource.LibraryResource;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.ManifestResource;
import com.pongasoft.kiwidoc.model.resource.OrganisationResource;
import com.pongasoft.kiwidoc.model.resource.PackageResource;
import com.pongasoft.kiwidoc.model.resource.PathManager;
import com.pongasoft.kiwidoc.model.resource.RepositoryResource;
import com.pongasoft.kiwidoc.model.resource.Resource;
import com.pongasoft.util.core.net.URIPath;

import java.net.URI;

/**
 * @author yan@pongasoft.com
 */
public class StoragePathManager implements PathManager
{
  public static final String PACKAGES_DIR_NAME  = "__packages";
  public static final String MANIFEST_DIR_NAME  = "__manifest";
  public static final String OVERVIEW_DIR_NAME  = "__overview";
  public static final String HIERARCHY_DIR_NAME  = "__hierarchy";
  public static final String CLASSES_DIR_NAME   = "__classes";

  private final URIPath _basePath;

  /**
   * Constructor
   */
  public StoragePathManager()
  {
    _basePath = null;
  }

  /**
   * Constructor
   */
  public StoragePathManager(URI basePath)
  {
    _basePath = URIPath.createFromURI(basePath);
  }

  /**
   * Take into account the optional base path
   */
  private URIPath createPath()
  {
    URIPath path = new URIPath();
    if(_basePath != null)
      path = path.addPath(_basePath);
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
      return computeOrganisationPath((OrganisationResource) resource).toURI();
    }

    if(resource instanceof LibraryResource)
    {
      return computeLibraryPath((LibraryResource) resource).toURI();
    }

    if(resource instanceof LibraryVersionResource)
    {
      return computeLibraryVersionPath((LibraryVersionResource) resource).toURI();
    }

    if(resource instanceof LVROverviewResource)
    {
      return computeLVROverviewPath((LVROverviewResource) resource).toURI();
    }

    if(resource instanceof ManifestResource)
    {
      return computeManifestPath((ManifestResource) resource).toURI();
    }

    if(resource instanceof HierarchyResource)
    {
      return computeHierarchyPath((HierarchyResource) resource).toURI();
    }

    if(resource instanceof PackageResource)
    {
      return computePackagePath((PackageResource) resource).toURI();
    }

    if(resource instanceof ClassResource)
    {
      return computeClassPath((ClassResource) resource).toURI();
    }

    // not reached
    throw new RuntimeException("unknown resource " + resource.getClass().getName());
  }

  /**
   * @return the path to an organisation
   */
  private URIPath computeOrganisationPath(OrganisationResource organisationResource)
  {
    URIPath path = createPath();

    path = path.addPathElement(organisationResource.getOrganisation());

    return path;
  }

  /**
   * @return the path to the library
   */
  private URIPath computeLibraryPath(LibraryResource libraryResource)
  {
    URIPath path = computeOrganisationPath(libraryResource.getParent());

    path = path.addPathElement(libraryResource.getName());

    return path;
  }

  /**
   * @return the path to the library version
   */
  public URIPath computeLibraryVersionPath(LibraryVersionResource libraryVersionResource)
  {
    URIPath path = computeLibraryPath(libraryVersionResource.getLibraryResource());

    path = path.addPathElement(libraryVersionResource.getVersion());

    return path;
  }

  /**
   * @return the path to the manifest
   */
  public URIPath computeLVROverviewPath(LVROverviewResource lvrOverviewResource)
  {
    return computeLVROverviewPath(computeLibraryVersionPath(lvrOverviewResource.getLibraryVersionResource()));
  }

  /**
   * @return the path to the manifest
   */
  public URIPath computeManifestPath(ManifestResource manifestResource)
  {
    return computeManifestPath(computeLibraryVersionPath(manifestResource.getLibraryVersionResource()));
  }

  /**
   * @return the path to the manifest
   */
  public URIPath computeLVROverviewPath(URIPath libraryPath)
  {
    URIPath path = createPath();

    path = path.addPath(libraryPath);
    path = path.addPathElement(OVERVIEW_DIR_NAME);

    return path;
  }

  /**
   * @return the path to the manifest
   */
  public URIPath computeManifestPath(URIPath libraryPath)
  {
    URIPath path = createPath();

    path = path.addPath(libraryPath);
    path = path.addPathElement(MANIFEST_DIR_NAME);

    return path;
  }

  /**
   * @return the path to the manifest
   */
  public URIPath computeHierarchyPath(HierarchyResource hierarchyResource)
  {
    return computeHierarchyPath(computeLibraryVersionPath(hierarchyResource.getParent()));
  }

  /**
   * @return the path to the manifest
   */
  public URIPath computeHierarchyPath(URIPath libraryPath)
  {
    URIPath path = createPath();

    path = path.addPath(libraryPath);
    path = path.addPathElement(HIERARCHY_DIR_NAME);

    return path;
  }


  /**
   * @return the path to the package
   */
  public URIPath computePackagePath(PackageResource packageResource)
  {
    return computePackagePath(computeLibraryVersionPath(packageResource.getLibraryVersionResource()),
                              packageResource.getPackageName());
  }

  /**
   * @return the path to the package
   */
  private URIPath computePackagePath(URIPath libraryPath, String packageName)
  {
    URIPath path = createPath();

    path = path.addPath(libraryPath);
    path = path.addPathElement(PACKAGES_DIR_NAME);
    path = path.addPathElement(packageName);

    return path;
  }

  /**
   * @return the path to the class
   */
  public URIPath computeClassPath(ClassResource classResource)
  {
    return computeClassPath(computePackagePath(computeLibraryVersionPath(classResource.getLibraryVersionResource()),
                                               ClassModel.computePackageName(classResource.getFqcn())),
                            ClassModel.computeSimpleName(classResource.getFqcn()));
  }

  /**
   * @param simpleClassName this is the name of the class <em>without!</em> the package
   * @return the path to the class
   */
  private URIPath computeClassPath(URIPath packagePath, String simpleClassName)
  {
    URIPath path = createPath();

    path = path.addPath(packagePath);
    path = path.addPathElement(CLASSES_DIR_NAME);
    path = path.addPathElement(simpleClassName);

    return path;
  }

  /**
   * This method properly takes care of the basePath provided in the constructor. In other words
   * this method will work only if the URI was generated with the same basePath.
   *
   * @return the resource given its path (should have been generated with {@link #computePath(Resource)}.
   */
  public Resource computeResource(URI path)
  {
    throw new UnsupportedOperationException("unclear how to do it");
  }
}

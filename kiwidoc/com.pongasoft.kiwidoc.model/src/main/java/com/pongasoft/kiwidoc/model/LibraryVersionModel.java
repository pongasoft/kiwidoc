
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

import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.ManifestResource;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.resource.ResolvableResource;
import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumSet;

/**
 * @author yan@pongasoft.com
 */
public class LibraryVersionModel implements Model<LibraryVersionResource>, ResolvableModel
{
  private final LibraryVersionResource _libraryVersionResource;
  private final int _jdkVersion;
  private final DocModel _overview;
  private final OSGiModel _OSGiModel;
  private final Set<BuiltFrom> _builtFrom;
  private final DependenciesModel _dependencies;
  private final boolean _hasManifest;
  private final Map<String, SimplePackageModel> _exportedPackages;
  private final Map<String, SimplePackageModel> _privatePackages;

  private final ClassResource _objectResource;

  // cache
  private volatile LibraryVersionStats _stats = null;

  /**
   * Constructor
   */
  public LibraryVersionModel(LibraryVersionResource libraryVersionResource,
                             int jdkVersion,
                             Collection<BuiltFrom> builtFrom,
                             DocModel overview,
                             DependenciesModel dependencies,
                             Collection<SimplePackageModel> exportedPackages,
                             Collection<SimplePackageModel> privatePackages,
                             OSGiModel OSGiModel,
                             boolean hasManifest)
  {
    _libraryVersionResource = libraryVersionResource;
    _jdkVersion = jdkVersion;
    _overview = overview;
    _OSGiModel = OSGiModel;
    if(builtFrom.isEmpty())
      _builtFrom = EnumSet.noneOf(BuiltFrom.class);
    else
      _builtFrom = EnumSet.copyOf(builtFrom);
    _dependencies = dependencies;
    _hasManifest = hasManifest;
    if(!Collections.disjoint(exportedPackages, privatePackages))
    {
      throw new IllegalArgumentException(exportedPackages + " and " +
                                         privatePackages +  " are not disjoint");
    }
    _exportedPackages = createMap(exportedPackages);
    _privatePackages = createMap(privatePackages);
    _objectResource = new ClassResource("java.lang.Object");
  }

  /**
   * private constructor for public api...
   */
  private LibraryVersionModel(LibraryVersionModel lvm,
                              Map<String, SimplePackageModel> exportedPackages)
  {
    _libraryVersionResource = lvm._libraryVersionResource;
    _jdkVersion = lvm._jdkVersion;
    _overview = lvm._overview;
    _builtFrom = lvm._builtFrom;
    _dependencies = lvm._dependencies;
    _OSGiModel = lvm._OSGiModel;
    _hasManifest = lvm._hasManifest;
    _exportedPackages = exportedPackages;
    _privatePackages = Collections.emptyMap();
    _objectResource = lvm._objectResource;
  }

  private static Map<String, SimplePackageModel> createMap(Collection<SimplePackageModel> packageModels)
  {
    Map<String, SimplePackageModel> res = new HashMap<String, SimplePackageModel>();

    for(SimplePackageModel packageModel : packageModels)
    {
      res.put(packageModel.getName(), packageModel);
    }

    return Collections.unmodifiableMap(res);
  }

  /**
   * @return the model kind
   */
  public Kind getKind()
  {
    return Kind.LIBRARY_VERSION;
  }

  @Override
  public Collection<? extends Resource> getChildren()
  {
    Collection<Resource> res = new ArrayList<Resource>();

    for(SimplePackageModel model : getAllPackages())
    {
      res.add(model.getResource());
    }

    if(_hasManifest)
      res.add(getManifestResource());

    return res;
  }

  public Set<BuiltFrom> getBuiltFrom()
  {
    return _builtFrom;
  }

  /**
   * @return the resource
   */
  public LibraryVersionResource getResource()
  {
    return _libraryVersionResource;
  }

  /**
   * @return <code>true</code> if this library is a jdk
   */
  public boolean isJDK()
  {
    return getResource().getParent().isJDK();
  }

  /**
   * @return which jdk version it was compiled with
   */
  public int getJdkVersion()
  {
    return _jdkVersion;
  }

  /**
   * @return the library in which the model belongs
   */
  public LibraryVersionResource getLibraryVersionResource()
  {
    return getResource();
  }

  /**
   * Collect the dependencies in the collection.
   *
   * @param dependencies to add the dependencies to it
   */
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    dependencies.add(_objectResource);
  }

  /**
   * @return the resource to the jdk (note that it will be <code>null</code> if the jdk
   * is not resolved...)
   */
  public LibraryVersionResource getJdkResource()
  {
    return _objectResource.getLibraryVersionResource();
  }

  public DocModel getOverview()
  {
    return _overview;
  }

  /**
   * Resolves the resource (determines whether this resource is part of this library and if yes
   * then call {@link ResolvableResource#resolve(LibraryVersionResource)}.
   */
  public void resolve(ResolvableResource resource)
  {
    if(resource.isResolved())
      return;

    String packageName = resource.getPackageName();
    SimplePackageModel packageModel = _exportedPackages.get(packageName);
    if(packageModel == null)
      packageModel = _privatePackages.get(packageName);

    if(packageModel != null)
    {
      String className = resource.getSimpleClassName();
      if(className == null ||
         packageModel.getExportedClasses().contains(className) ||
         packageModel.getPrivateClasses().contains(className))
      {
        resource.resolve(_libraryVersionResource);
      }
    }
  }

  public DependenciesModel getDependencies()
  {
    return _dependencies;
  }

  public Collection<SimplePackageModel> getExportedPackages()
  {
    return _exportedPackages.values();
  }

  public Collection<SimplePackageModel> getPrivatePackages()
  {
    return _privatePackages.values();
  }

  public Collection<SimplePackageModel> getAllPackages()
  {
    Set<SimplePackageModel> res = new HashSet<SimplePackageModel>();
    res.addAll(_exportedPackages.values());
    res.addAll(_privatePackages.values());
    return res;
  }

  public Set<ClassResource> getAllClassesResources()
  {
    Set<ClassResource> res = new HashSet<ClassResource>();
    for(SimplePackageModel exportedPackage : _exportedPackages.values())
    {
      res.addAll(exportedPackage.getAllClassesResources());
    }
    for(SimplePackageModel privatePackage : _privatePackages.values())
    {
      res.addAll(privatePackage.getAllClassesResources());
    }
    return res;
  }

  public LibraryVersionStats getStats()
  {
    if(_stats == null)
    {
      int exportedClassesCount = 0;
      int privateClassesCount = 0;

      for(SimplePackageModel spm : _exportedPackages.values())
      {
        exportedClassesCount += spm.getExportedClasses().size();
        privateClassesCount += spm.getPrivateClasses().size();
      }

      for(SimplePackageModel spm : _privatePackages.values())
      {
        privateClassesCount += spm.getExportedClasses().size() + spm.getPrivateClasses().size();
      }

      _stats = new LibraryVersionStats(_exportedPackages.size(),
                                       _privatePackages.size(),
                                       exportedClassesCount,
                                       privateClassesCount);
    }

    return _stats;
  }

  public boolean getHasManifest()
  {
    return _hasManifest;
  }

  public ManifestResource getManifestResource()
  {
    if(_hasManifest)
    {
      return new ManifestResource(_libraryVersionResource);
    }
    else
    {
      return null;
    }
  }

  public boolean isOSGiBundle()
  {
    return _OSGiModel != null;
  }

  public OSGiModel getOSGiModel()
  {
    return _OSGiModel;
  }

  /**
   * @return a version of this model with everything that is not part of the public api which has
   *         been stripped out. If the model itself is not part of the public api then
   *         <code>null</code> is returned!
   */
  public LibraryVersionModel toPublicAPI()
  {
    if(_exportedPackages.isEmpty())
      return null;

    boolean changed = false;
    Map<String, SimplePackageModel> exportedPackages = new HashMap<String, SimplePackageModel>();

    for(SimplePackageModel spm : _exportedPackages.values())
    {
      SimplePackageModel newSpm = spm.toPublicAPI();
      changed |= newSpm != spm;
      exportedPackages.put(spm.getName(), newSpm);
    }

    if(changed || !_privatePackages.isEmpty())
      return new LibraryVersionModel(this, exportedPackages);
    else
      return this;
  }

  /**
   * @return <code>true</code> if this model is part of the public api
   */
  public boolean isPublicAPI()
  {
    return !_exportedPackages.isEmpty();
  }
}

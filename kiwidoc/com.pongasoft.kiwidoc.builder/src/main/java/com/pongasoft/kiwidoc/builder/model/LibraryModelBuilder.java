
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

import com.pongasoft.kiwidoc.model.BuiltFrom;
import com.pongasoft.kiwidoc.model.ClassDefinitionModel;
import com.pongasoft.kiwidoc.model.DependenciesModel;
import com.pongasoft.kiwidoc.model.DocModel;
import com.pongasoft.kiwidoc.model.HierarchyModel;
import com.pongasoft.kiwidoc.model.LVROverviewModel;
import com.pongasoft.kiwidoc.model.LibraryVersionModel;
import com.pongasoft.kiwidoc.model.ParentHierarchyModel;
import com.pongasoft.kiwidoc.model.SimplePackageModel;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.resource.HierarchyResource;
import com.pongasoft.kiwidoc.model.resource.LVROverviewResource;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.type.GenericType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;

/**
 * @author yan@pongasoft.com
 */
public class LibraryModelBuilder implements ModelBuilder<LibraryVersionResource, LibraryVersionModel>
{
  private final Map<String, PackageModelBuilder> _packages = new HashMap<String, PackageModelBuilder>();
  private final Map<ClassResource, Collection<GenericType>> _parentClasses =
    new HashMap<ClassResource, Collection<GenericType>>();
  private final LibraryVersionResource _libraryVersionResource;
  private final Set<BuiltFrom> _builtFrom = EnumSet.noneOf(BuiltFrom.class);

  private DependenciesModel _dependencies = DependenciesModel.NO_DEPENDENCIES;
  private ManifestModelBuilder _manifest = new ManifestModelBuilder();
  private volatile HierarchyModel _hierarchyModel = null;
  private volatile LibraryVersionModel _model = null;
  private int _jdkVersion = 0;
  private DocModel _overview;

  /**
   * Constructor
   */
  public LibraryModelBuilder(LibraryVersionResource libraryVersionResource)
  {
    _libraryVersionResource = libraryVersionResource;
    _manifest.setLibraryModelBuilder(this);
  }

  public int getJdkVersion()
  {
    return _jdkVersion;
  }

  public void setJdkVersion(int jdkVersion)
  {
    _jdkVersion = jdkVersion;
  }

  public void addBuiltFrom(BuiltFrom builtFrom)
  {
    _builtFrom.add(builtFrom);
  }

  public void addBuiltFrom(Set<BuiltFrom> builtFroms)
  {
    _builtFrom.addAll(builtFroms);
  }

  public Set<BuiltFrom> getBuiltFrom()
  {
    return _builtFrom;
  }

  public LibraryVersionResource getLibraryVersionResource()
  {
    return _libraryVersionResource;
  }

  /**
   * @return all the packages regardless of whether they are exported or private.
   */
  public Collection<PackageModelBuilder> getAllPackages()
  {
    return _packages.values();
  }

  /**
   * @return the package given its name (<code>null</code> when not found)
   */
  public PackageModelBuilder findPackage(String name)
  {
    return _packages.get(name);
  }

  /**
   * @return the number of classes
   */
  public int getClassCount()
  {
    return _parentClasses.size();
  }

  public void addClasses(Collection<ClassModelBuilder> classModelBuilders)
  {
    for(ClassModelBuilder classModelBuilder : classModelBuilders)
    {
      addClass(classModelBuilder);
    }
  }

  public void addClass(ClassModelBuilder classModelBuilder)
  {
    if(_jdkVersion == 0)
    {
      _jdkVersion = classModelBuilder.getJdkVersion();
    }
    else
    {
      if(classModelBuilder.getJdkVersion() > 0)
        _jdkVersion = Math.max(_jdkVersion, classModelBuilder.getJdkVersion());
    }

    String packageName = classModelBuilder.getPackageName();

    PackageModelBuilder p = _packages.get(packageName);
    if(p == null)
    {
      p = new PackageModelBuilder(packageName);
      p.setLibraryModelBuilder(this);
      _packages.put(packageName, p);
    }
    p.addClass(classModelBuilder);

    Collection<GenericType> parents = new ArrayList<GenericType>();

    // superclass
    GenericType superclass = classModelBuilder.getSuperclass();
    if(superclass != null && !superclass.isObjectType())
      parents.add(superclass);
    parents.addAll(classModelBuilder.getInterfaces());
    
    _parentClasses.put(classModelBuilder.getClassResource(), parents);
  }

  public void setPackageInfo(String packageName, DocModel packageInfo)
  {
    PackageModelBuilder p = _packages.get(packageName);
    if(p == null)
      throw new IllegalArgumentException(packageName);
    p.setPackageInfo(packageInfo);
  }

  public void setDependencies(DependenciesModel dependencies)
  {
    _dependencies = dependencies;
  }

  public ManifestModelBuilder getManifest()
  {
    return _manifest;
  }

  public void setManifest(Manifest manifest)
  {
    _manifest.setManifest(manifest);
  }

  public HierarchyModel getHierarchyModel()
  {
    if(_hierarchyModel == null)
    {
      Collection<ParentHierarchyModel> parentHierarchy = new ArrayList<ParentHierarchyModel>();

      for(Map.Entry<ClassResource, Collection<GenericType>> entry : _parentClasses.entrySet())
      {
        Collection<GenericType> generics = entry.getValue();
        Collection<ClassDefinitionModel> parents =
          new ArrayList<ClassDefinitionModel>(generics.size());

        for(GenericType generic : generics)
        {
          ClassModelBuilder classModel = findClass(generic.getClassResource());
          ClassDefinitionModel parent;
          if(classModel != null)
          {
            parent = classModel.getClassDefinition();
          }
          else
          {
            parent = new ClassDefinitionModel(0, generic);
          }
          parents.add(parent);
        }

        ParentHierarchyModel parentHierarchyModel =
          new ParentHierarchyModel(findClass(entry.getKey()).getClassDefinition(), parents);

        parentHierarchy.add(parentHierarchyModel);
      }

      _hierarchyModel = new HierarchyModel(new HierarchyResource(getLibraryVersionResource()),
                                           parentHierarchy);
    }

    return _hierarchyModel;
  }

  private ClassModelBuilder findClass(ClassResource classResource)
  {
    PackageModelBuilder p = findPackage(classResource.getPackageName());
    if(p == null)
      return null;
    return p.findClass(classResource.getSimpleClassName());
  }

  public DocModel getOverview()
  {
    return _overview;
  }

  public void setOverview(DocModel overview)
  {
    _overview = overview;
  }

  /**
   * @return the model
   */
  public LibraryVersionModel buildModel()
  {
    if(_model == null)
    {
      Set<SimplePackageModel> exportedPackages = new HashSet<SimplePackageModel>();
      Set<SimplePackageModel> privatePackages = new HashSet<SimplePackageModel>();

      for(PackageModelBuilder packageModelBuilder : _packages.values())
      {
        if(packageModelBuilder.isExportedPackage())
        {
          exportedPackages.add(packageModelBuilder.buildSimpleModel());
        }
        else
        {
          privatePackages.add(packageModelBuilder.buildSimpleModel());
        }
      }

      HierarchyModel hierarchy = getHierarchyModel();

      for(ClassResource classWithChildren : hierarchy.getClassesWithChildren())
      {
        findClass(classWithChildren).setAllLibrarySubclasses(hierarchy.getAllChildrenClasses(classWithChildren));
      }

      _model = new LibraryVersionModel(_libraryVersionResource,
                                       getJdkVersion(),
                                       _builtFrom,
                                       _overview,
                                       _dependencies,
                                       exportedPackages,
                                       privatePackages,
                                       _manifest.buildOSGiModel(), 
                                       _manifest.hasManifest());
    }
    
    return _model;
  }

  public LVROverviewModel buildOverview()
  {
    Collection<LVROverviewModel.Package> packages = new ArrayList<LVROverviewModel.Package>();

    for(PackageModelBuilder packageModelBuilder : _packages.values())
    {
      packages.add(packageModelBuilder.buildOverview());
    }

    return new LVROverviewModel(new LVROverviewResource(getLibraryVersionResource()), packages);
  }
}


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

import com.pongasoft.kiwidoc.model.BaseEntryModel;
import com.pongasoft.kiwidoc.model.ClassDefinitionModel;
import com.pongasoft.kiwidoc.model.DocModel;
import com.pongasoft.kiwidoc.model.LVROverviewModel;
import com.pongasoft.kiwidoc.model.PackageModel;
import com.pongasoft.kiwidoc.model.SimplePackageModel;
import com.pongasoft.kiwidoc.model.resource.PackageResource;
import com.pongasoft.util.core.misc.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a package. Note that the class names are 'relative' to the package... this is why it
 * is called 'simple' class name (ex: <code>BufferedInputStream</code> is the simple class name)
 * 
 * @author yan@pongasoft.com
 */
public class PackageModelBuilder implements ModelBuilder<PackageResource, PackageModel>
{
  private final String _name;
  private final Map<String, ClassModelBuilder> _classes = new HashMap<String, ClassModelBuilder>();

  private LibraryModelBuilder _libraryModelBuilder;
  private DocModel _packageInfo;

  /**
   * Constructor
   */
  public PackageModelBuilder(String name)
  {
    _name = name;
  }

  public void addClass(ClassModelBuilder classModelBuilder)
  {
    if(!Utils.isEqual(classModelBuilder.getPackageName(), _name))
      throw new IllegalArgumentException(classModelBuilder + " is not a class of this package " + _name);
    classModelBuilder.setPackageModelBuilder(this);
    _classes.put(classModelBuilder.getSimpleName(), classModelBuilder);
  }

  public String getName()
  {
    return _name;
  }

  public LibraryModelBuilder getLibraryModelBuilder()
  {
    return _libraryModelBuilder;
  }

  public void setLibraryModelBuilder(LibraryModelBuilder libraryModelBuilder)
  {
    _libraryModelBuilder = libraryModelBuilder;
  }

  /**
   * @return all the classes in the package
   */
  public Collection<ClassModelBuilder> getAllClasses()
  {
    return _classes.values();
  }

  /**
   * @return the class model builder for the given name (<code>null</code> if not found)
   */
  public ClassModelBuilder findClass(String name)
  {
    return _classes.get(name);
  }

  /**
   * @return <code>true</code> if at least one class is exported
   */
  public boolean isExportedPackage()
  {
    for(ClassModelBuilder classModelBuilder : _classes.values())
    {
      if(classModelBuilder.isExportedClass())
        return true;
    }
    return false;
  }

  public DocModel getPackageInfo()
  {
    return _packageInfo;
  }

  public void setPackageInfo(DocModel packageInfo)
  {
    _packageInfo = packageInfo;
  }

  /**
   * @return the model
   */
  public PackageModel buildModel()
  {
    Collection<ClassDefinitionModel> list = new ArrayList<ClassDefinitionModel>();

    for(ClassModelBuilder cmb : _classes.values())
    {
      list.add(cmb.getClassDefinition());
    }

    return new PackageModel(getAccess(),
                            new PackageResource(_libraryModelBuilder.getLibraryVersionResource(), _name),
                            _packageInfo,
                            list);
  }

  public LVROverviewModel.Package buildOverview()
  {
    Collection<LVROverviewModel.Class> classes = new ArrayList<LVROverviewModel.Class>(_classes.size());

    for(ClassModelBuilder cmb : _classes.values())
    {
      classes.add(cmb.buildOverview());
    }

    return new LVROverviewModel.Package(getAccess(),
                                        getName(),
                                        classes);
  }

  public int getAccess()
  {
    if(isExportedPackage())
      return BaseEntryModel.Access.PUBLIC.getOpcode();
    else
      return BaseEntryModel.Access.NON_EXPORTED.getOpcode();
  }

  /**
   * @return the model
   */
  public SimplePackageModel buildSimpleModel()
  {
    Set<String> exportedClasses = new HashSet<String>();
    Set<String> privateClasses = new HashSet<String>();

    for(ClassModelBuilder cmb : _classes.values())
    {
      if(cmb.isExportedClass())
        exportedClasses.add(cmb.getSimpleName());
      else
        privateClasses.add(cmb.getSimpleName());
    }
    return new SimplePackageModel(new PackageResource(_libraryModelBuilder.getLibraryVersionResource(), _name),
                            _packageInfo,
                            exportedClasses,
                            privateClasses);
  }
}

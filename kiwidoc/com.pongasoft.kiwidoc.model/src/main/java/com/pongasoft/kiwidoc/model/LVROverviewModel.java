
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

import com.pongasoft.kiwidoc.model.resource.LVROverviewResource;
import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.Collection;

/**
 * This is a totally denormalized class meant for quick access. It contains resources only.
 *
 * @author yan@pongasoft.com
 */
public class LVROverviewModel implements Model<LVROverviewResource>
{
  private final LVROverviewResource _overviewResource;
  private final Collection<Package> _packages;

  public static class Package extends BaseEntryModel implements Exportable<Package>
  {
    private final Collection<Class> _classes;

    public Package(int access, String packageName, Collection<Class> classes)
    {
      super(access, packageName);
      _classes = classes;
    }

    public Collection<Class> getClasses()
    {
      return _classes;
    }

    public Package toPublicAPI()
    {
      if(!isPublicAPI())
        return null;

      Collection<Class> classes = ExportableHelper.toPublicAPI(_classes);

      if(classes == _classes)
        return this;
      else
        return new Package(getAccess(), getName(), classes);
    }
  }

  public static class Class extends BaseEntryModel implements Exportable<Class>
  {
    private final String _signature;

    private final Collection<Method> _methods;
    private final Collection<Field> _fields;

    public Class(int access,
                 String className,
                 String signature,
                 Collection<Method> methods,
                 Collection<Field> fields)
    {
      super(access, className);
      _signature = signature;
      _methods = methods;
      _fields = fields;
    }

    public String getSignature()
    {
      return _signature;
    }

    public Collection<Method> getMethods()
    {
      return _methods;
    }

    public Collection<Field> getFields()
    {
      return _fields;
    }

    public Class toPublicAPI()
    {
      if(!isPublicAPI())
        return null;

      Collection<Method> methods = ExportableHelper.toPublicAPI(_methods);
      Collection<Field> fields = ExportableHelper.toPublicAPI(_fields);

      // nothing has changed...
      if(methods == _methods && fields == _fields)
        return this;

      if(methods == _methods)
        methods = _methods;

      if(fields == _fields)
        fields = _fields;

      return new Class(getAccess(),
                       getName(),
                       getSignature(),
                       methods,
                       fields);
    }
  }

  public static class Method extends BaseEntryModel implements Exportable<Method>
  {
    private final String _signature;

    public Method(int access,
                  String memberName,
                  String signature)
    {
      super(access, memberName);
      _signature = signature;
    }

    public String getSignature()
    {
      return _signature;
    }

    public String getMethodName()
    {
      String methodName = getName();

      methodName = methodName.substring(0, methodName.indexOf('('));

      return methodName;
    }

    public Method toPublicAPI()
    {
      if(isPublicAPI())
        return this;
      else
        return null;
    }
  }

  public static class Field extends BaseEntryModel implements Exportable<Field>
  {
    private final String _signature;

    public Field(int access, String fieldName, String signature)
    {
      super(access, fieldName);
      _signature = signature;
    }

    public String getSignature()
    {
      return _signature;
    }

    public Field toPublicAPI()
    {
      if(isPublicAPI())
        return this;
      else
        return null;
    }
  }


  /**
   * Constructor
   */
  public LVROverviewModel(LVROverviewResource overviewResource,
                          Collection<Package> packages)
  {
    _overviewResource = overviewResource;
    _packages = packages;
  }

  public LVROverviewResource getResource()
  {
    return _overviewResource;
  }

  public Kind getKind()
  {
    return Kind.OVERVIEW;
  }

  @Override
  public Collection<? extends Resource> getChildren()
  {
    throw new UnsupportedOperationException("TODO");
  }

  public Collection<Package> getPackages()
  {
    return _packages;
  }

  public int getPackageCount()
  {
    return _packages.size();
  }

  public int getClassCount()
  {
    int classes = 0;

    for(Package p : _packages)
    {
      classes += p.getClasses().size();
    }

    return classes;
  }

  public int getMethodCount()
  {
    int methods = 0;

    for(Package p : _packages)
    {
      for(Class c : p.getClasses())
      {
        methods += c.getMethods().size();
      }
    }

    return methods;
  }

  public int getFieldCount()
  {
    int fields = 0;

    for(Package p : _packages)
    {
      for(Class c : p.getClasses())
      {
        fields += c.getFields().size();
      }
    }

    return fields;
  }

  public Model toPublicAPI()
  {
    Collection<Package> packages = ExportableHelper.toPublicAPI(_packages);

    if(packages == _packages)
      return this;
    else
      return new LVROverviewModel(getResource(), packages);
  }

  public boolean isPublicAPI()
  {
    return true;
  }
}

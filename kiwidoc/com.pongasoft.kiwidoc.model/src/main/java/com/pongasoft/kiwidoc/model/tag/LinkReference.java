
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

package com.pongasoft.kiwidoc.model.tag;

import com.pongasoft.kiwidoc.model.resource.PackageResource;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.resource.MethodResource;
import com.pongasoft.kiwidoc.model.resource.ResolvableResource;
import com.pongasoft.kiwidoc.model.resource.DependenciesCollector;

import java.util.Collection;

/**
 * @author yan@pongasoft.com
 */
public class LinkReference implements DependenciesCollector
{
  public static final LinkReference NO_LINK_REFERENCE =
    new LinkReference("", null, null, null);

  private final String _rawReference;
  private final String _packageName;
  private final String _simpleClassName;
  private final String _memberName;
  private final ResolvableResource _link;

  /**
   * Constructor
   */
  public LinkReference(String rawReference,
                       String packageName,
                       String simpleClassName,
                       String memberName)
  {
    _rawReference = rawReference;
    _packageName = packageName;
    _simpleClassName = simpleClassName;
    _memberName = memberName;

    _link = createLink(packageName, simpleClassName, memberName);
  }

  private static ResolvableResource createLink(String packageName,
                                               String simpleClassName,
                                               String memberName)
  {
    if(packageName != null && !packageName.equals(""))
    {
      PackageResource packageResource = new PackageResource(packageName);
      if(simpleClassName == null || simpleClassName.equals(""))
      {
        return packageResource;
      }

      ClassResource classResource = new ClassResource(packageResource, simpleClassName);
      if(memberName == null || memberName.equals(""))
      {
        return classResource;
      }

      return new MethodResource(classResource, memberName);
    }

    return null;
  }


  public String getRawReference()
  {
    return _rawReference;
  }

  public String getPackageName()
  {
    return _packageName;
  }

  public String getSimpleClassName()
  {
    return _simpleClassName;
  }

  public String getMemberName()
  {
    return _memberName;
  }

  public ResolvableResource getLink()
  {
    return _link;
  }

  /**
   * Collect the dependencies in the collection.
   *
   * @param dependencies to add the dependencies to it
   */
  public void collectDependencies(Collection<ResolvableResource> dependencies)
  {
    if(_link != null)
      dependencies.add(_link);
  }

  @Override
  public String toString()
  {
    return _rawReference;
  }
}

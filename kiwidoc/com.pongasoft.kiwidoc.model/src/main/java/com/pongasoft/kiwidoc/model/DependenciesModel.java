
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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author yan@pongasoft.com
 */
public class DependenciesModel
{
  public static final DependenciesModel NO_DEPENDENCIES =
    new DependenciesModel(Collections.<LibraryVersionResource>emptyList());

  private final Set<LibraryVersionResource> _directDependencies;
  private final Set<LibraryVersionResource> _transitiveDependencies;
  private final Set<LibraryVersionResource> _optionalDependencies;

  /**
   * Constructor
   */
  public DependenciesModel(Collection<LibraryVersionResource> directDependencies)
  {
    this(directDependencies,
         Collections.<LibraryVersionResource>emptyList(),
         Collections.<LibraryVersionResource>emptyList());
  }

  /**
   * Constructor.
   *
   * @param directDependencies     contains only the direct dependencies
   * @param transitiveDependencies contains only transitive dependencies
   * @param optionalDependencies   contains dependencies that are optional (should either be in
   *                               direct or transitive!)
   */
  public DependenciesModel(Collection<LibraryVersionResource> directDependencies,
                           Collection<LibraryVersionResource> transitiveDependencies,
                           Collection<LibraryVersionResource> optionalDependencies)
  {
    _directDependencies =
      Collections.unmodifiableSet(new LinkedHashSet<LibraryVersionResource>(directDependencies));
    _transitiveDependencies =
      Collections.unmodifiableSet(new LinkedHashSet<LibraryVersionResource>(transitiveDependencies));
    _optionalDependencies =
      Collections.unmodifiableSet(new HashSet<LibraryVersionResource>(optionalDependencies));
  }

  public Set<LibraryVersionResource> getDependencies()
  {
    Set<LibraryVersionResource> res = new LinkedHashSet<LibraryVersionResource>();
    res.addAll(_directDependencies);
    res.addAll(_transitiveDependencies);
    return res;
  }

  public Set<LibraryVersionResource> getDirectDependencies()
  {
    return _directDependencies;
  }

  public Set<LibraryVersionResource> getDirectDependencies(boolean optionalOnly)
  {
    Set<LibraryVersionResource> res = new HashSet<LibraryVersionResource>();

    for(LibraryVersionResource directDependency : _directDependencies)
    {
      boolean dependencyIsOptional = _optionalDependencies.contains(directDependency);

      if(optionalOnly)
      {
        if(dependencyIsOptional)
          res.add(directDependency);
      }
      else
      {
        if(!dependencyIsOptional)
          res.add(directDependency);
      }
    }

    return res;
  }

  public Set<LibraryVersionResource> getTransitiveDependencies()
  {
    return _transitiveDependencies;
  }

  public Set<LibraryVersionResource> getTransitiveDependencies(boolean optionalOnly)
  {
    Set<LibraryVersionResource> res = new HashSet<LibraryVersionResource>();

    for(LibraryVersionResource transitiveDependency : _transitiveDependencies)
    {
      boolean dependencyIsOptional = _optionalDependencies.contains(transitiveDependency);

      if(optionalOnly)
      {
        if(dependencyIsOptional)
          res.add(transitiveDependency);
      }
      else
      {
        if(!dependencyIsOptional)
          res.add(transitiveDependency);
      }
    }

    return res;
  }


  public Set<LibraryVersionResource> getOptionalDependencies()
  {
    return _optionalDependencies;
  }

  public boolean getHasDependencies()
  {
    return !_directDependencies.isEmpty();
  }

  public boolean isOptionalDependency(LibraryVersionResource lvr)
  {
    return _optionalDependencies.contains(lvr);
  }
}

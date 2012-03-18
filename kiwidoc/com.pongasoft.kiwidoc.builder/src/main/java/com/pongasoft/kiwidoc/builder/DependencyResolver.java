
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

package com.pongasoft.kiwidoc.builder;

import com.pongasoft.kiwidoc.model.BuiltFrom;
import com.pongasoft.kiwidoc.model.DependenciesModel;
import com.pongasoft.kiwidoc.model.LibraryVersionModel;
import com.pongasoft.kiwidoc.model.ResolvableModel;
import com.pongasoft.kiwidoc.model.SimplePackageModel;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource;
import com.pongasoft.kiwidoc.model.resource.ResolvableResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author yan@pongasoft.com
 */
public class DependencyResolver<T extends ResolvableModel>
{
  public static final Log log = LogFactory.getLog(DependencyResolver.class);

  private static final LibraryVersionModel NULL_LVR =
    new LibraryVersionModel(new LibraryVersionResource("n", "u", "ll"),
                            0,
                            EnumSet.noneOf(BuiltFrom.class),
                            null,
                            DependenciesModel.NO_DEPENDENCIES,
                            Collections.<SimplePackageModel>emptyList(),
                            Collections.<SimplePackageModel>emptyList(),
                            null,
                            false);

  private final KiwidocLibraryStore _store;
  private final T _resolvableModel;
  private final DependenciesModel _providedDependencies;
  private final Map<LibraryVersionResource, LibraryVersionModel> _computedDependencies;
  private LibraryVersionModel _jdk;
  
  private boolean _resolved = false;
  private ClassResource _objectResource;

  /**
   * Constructor
   */
  public DependencyResolver(KiwidocLibraryStore store,
                            DependenciesModel providedDependencies,
                            T resolvableModel)
  {
    _store = store;
    _resolvableModel = resolvableModel;
    _computedDependencies =
      new LinkedHashMap<LibraryVersionResource, LibraryVersionModel>();
    _providedDependencies = providedDependencies;
    _objectResource = new ClassResource("java.lang.Object");
  }

  /**
   * Constructor
   */
  private DependencyResolver(KiwidocLibraryStore store,
                             DependenciesModel providedDependencies,
                             T resolvableModel,
                             Map<LibraryVersionResource, LibraryVersionModel> computedDependencies,
                             LibraryVersionModel jdk,
                             ClassResource objectResource)
  {
    _store = store;
    _providedDependencies = providedDependencies;
    _resolvableModel = resolvableModel;
    _computedDependencies = computedDependencies;
    _jdk = jdk;
    _objectResource = objectResource;
  }

  /**
   * @return a new resolver for the given class keeping the resolution information from the previous
   *         resolution
   */
  public DependencyResolver<T> newResolver(T resolvableModel) throws StoreException
  {
    if(!_resolved)
      resolve();

    return new DependencyResolver<T>(_store,
                                     _providedDependencies,
                                     resolvableModel,
                                     new LinkedHashMap<LibraryVersionResource, LibraryVersionModel>(_computedDependencies),
                                     _jdk,
                                     _objectResource);
  }

  /**
   * @return the object resource
   */
  public ClassResource getObjectResource() throws StoreException
  {
    if(!_resolved)
      resolve();

    return _objectResource;
  }

  /**
   * Resolves the class.
   */
  public T resolve() throws StoreException
  {
    if(_resolved)
      return _resolvableModel;
    
    addDependencies(_providedDependencies);

    LibraryVersionModel library;

    // no need to load it twice!
    if(_resolvableModel instanceof LibraryVersionModel)
      library = (LibraryVersionModel) _resolvableModel;
    else
      library = loadDependency(_resolvableModel.getLibraryVersionResource());

    if(library == null)
    {
      // ok we don't know this library
      if(log.isDebugEnabled())
        log.debug("Library dependency not found: " + _resolvableModel.getLibraryVersionResource());

      _resolved = true;
      return _resolvableModel;
    }

    if(library.isJDK() && _jdk == null)
    {
      _jdk = library;
    }

    // we must load the dependencies and the JDK because of the overview which may contain
    // clickable links
    addDependencies(library.getDependencies());

    // finally JDK
    setJdk(library.getJdkVersion());

    Collection<ResolvableResource> resources = new ArrayList<ResolvableResource>();

    _resolvableModel.collectDependencies(resources);
    resources.add(_objectResource);

    doResolve(resources);

    _resolved = true;
    return _resolvableModel;
  }


  private void doResolve(Collection<ResolvableResource> resources) throws StoreException
  {
    for(LibraryVersionResource lvr : _computedDependencies.keySet())
    {
      LibraryVersionModel libraryVersionModel = loadDependency(lvr);

      if(libraryVersionModel != null)
      {
        for(ResolvableResource resource : resources)
        {
          if(!resource.isResolved())
            libraryVersionModel.resolve(resource);
        }
      }
    }

    if(_jdk != null)
    {
      for(ResolvableResource resource : resources)
      {
        if(!resource.isResolved())
          _jdk.resolve(resource);
      }
    }
  }

  private void setJdk(int jdkVersion)
  {
    if(_jdk == null)
    {
      try
      {
        _jdk = _store.findJdk(jdkVersion);
      }
      catch(StoreException e)
      {
        if(log.isDebugEnabled())
          log.debug("Problem with jdk: " + jdkVersion, e);
        // ok... remains what it was
      }
    }
  }

  private void addDependencies(DependenciesModel dependencies)
    throws StoreException
  {
    Set<LibraryVersionResource> optionals = dependencies.getOptionalDependencies();

    // non optional direct
    for(LibraryVersionResource resource : dependencies.getDirectDependencies())
    {
      if(!optionals.contains(resource))
        _computedDependencies.put(resource, null);
    }

    // non optional transitive
    for(LibraryVersionResource resource : dependencies.getTransitiveDependencies())
    {
      if(!optionals.contains(resource))
        _computedDependencies.put(resource, null);
    }

    // optional transitive
    for(LibraryVersionResource resource : dependencies.getDirectDependencies())
    {
      if(optionals.contains(resource))
        _computedDependencies.put(resource, null);
    }

    // optional transitive
    for(LibraryVersionResource resource : dependencies.getTransitiveDependencies())
    {
      if(optionals.contains(resource))
        _computedDependencies.put(resource, null);
    }
  }

  private LibraryVersionModel loadDependency(LibraryVersionResource dependency)
    throws StoreException
  {
    if(dependency == null)
      return null;

    LibraryVersionModel res = _computedDependencies.get(dependency);
    if(res == null)
    {
      try
      {
        res = (LibraryVersionModel) _store.loadContent(dependency);
      }
      catch(NoSuchContentException e)
      {
        // ok we don't know this library
        if(log.isDebugEnabled())
          log.debug("Provided dependency not found: " + dependency);
        res = NULL_LVR;
      }
      _computedDependencies.put(dependency, res);
    }

    if(res == NULL_LVR)
      return null;
    else
      return res;
  }
}

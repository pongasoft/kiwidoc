
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

import com.pongasoft.kiwidoc.model.resource.HierarchyResource;
import com.pongasoft.kiwidoc.model.resource.ClassResource;
import com.pongasoft.kiwidoc.model.resource.Resource;

import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.Collections;
import java.util.ArrayList;

/**
 * @author yan@pongasoft.com
 */
public class HierarchyModel implements Model<HierarchyResource>
{
  private final HierarchyResource _hierarchyResource;
  private final Map<ClassResource, ChildHierarchyModel> _childHierarchy;
  private final Map<ClassResource, ParentHierarchyModel> _parentHierarchy;

  public HierarchyModel(HierarchyResource hierarchyResource,
                        Collection<ParentHierarchyModel> parentHierarchy)
  {

    _hierarchyResource = hierarchyResource;

    Map<ClassResource, ParentHierarchyModel> map =
      new HashMap<ClassResource, ParentHierarchyModel>();

    Map<ClassResource, Collection<ClassDefinitionModel>> childHierarchy =
      new HashMap<ClassResource, Collection<ClassDefinitionModel>>();

    for(ParentHierarchyModel model : parentHierarchy)
    {
      map.put(model.getClassResource(), model);

      for(ClassDefinitionModel classDefinitionModel : model.getParents())
      {
        Collection<ClassDefinitionModel> list =
          childHierarchy.get(classDefinitionModel.getClassResource());
        if(list == null)
        {
          list = new ArrayList<ClassDefinitionModel>();
          childHierarchy.put(classDefinitionModel.getClassResource(), list);
        }
        list.add(model.getClassDefinition());
      }
    }

    Map<ClassResource, ChildHierarchyModel> map2 =
      new HashMap<ClassResource, ChildHierarchyModel>(childHierarchy.size());
    for(Map.Entry<ClassResource, Collection<ClassDefinitionModel>> entry : childHierarchy.entrySet())
    {
      ClassResource child = entry.getKey();
      // some classes may not be in this library!
      ParentHierarchyModel parentHierarchyModel = map.get(child);
      if(parentHierarchyModel != null)
      {
        map2.put(child,
                 new ChildHierarchyModel(parentHierarchyModel.getClassDefinition(),
                                         entry.getValue()));
      }
    }

    _parentHierarchy = Collections.unmodifiableMap(map);
    _childHierarchy = Collections.unmodifiableMap(map2);
  }

  /**
   * @return the resource
   */
  public HierarchyResource getResource()
  {
    return _hierarchyResource;
  }

  /**
   * @return the model kind
   */
  public Kind getKind()
  {
    return Kind.HIERARCHY;
  }

  @Override
  public Collection<? extends Resource> getChildren()
  {
    // TODO HIGH YP:  TODO...
    return Collections.emptyList();
  }

  /**
   * @return the parent hierarchy
   */
  public Collection<ParentHierarchyModel> getParentHierarchy()
  {
    return _parentHierarchy.values();
  }

  /**
   * @return the child hierarchy
   */
  public Collection<ChildHierarchyModel> getChildHierarchy()
  {
    return _childHierarchy.values();
  }

  /**
   * @return all the classes that have children or in other words all the classes so that
   * {@link #getChildrenClasses(ClassResource)} will not return an empty result
   */
  public Collection<ClassResource> getClassesWithChildren()
  {
    return _childHierarchy.keySet();
  }

  /**
   * @return all the classes that have parents or in other words all the classes so that
   * {@link #getParentClasses(ClassResource)} will not return an empty result
   */
  public Collection<ClassResource> getClassesWithParents()
  {
    return _parentHierarchy.keySet();
  }

  /**
   * @return returns only the direct subclasses of the given class resource
   */
  public Collection<ClassDefinitionModel> getChildrenClasses(ClassResource classResource)
  {
    return getClasses(_childHierarchy, classResource);
  }

  /**
   * @return returns all subclasses of the class resource (recursively...)
   */
  public Collection<ClassDefinitionModel> getAllChildrenClasses(ClassResource classResource)
  {
    return getAllClasses(_childHierarchy, classResource);
  }

  /**
   * @return returns only the direct parent classes of the given class resource
   */
  public Collection<ClassDefinitionModel> getParentClasses(ClassResource classResource)
  {
    return getClasses(_parentHierarchy, classResource);
  }

  /**
   * @return returns all parent classes of the class resource (recursively...)
   */
  public Collection<ClassDefinitionModel> getAllParentClasses(ClassResource classResource)
  {
    return getAllClasses(_parentHierarchy, classResource);
  }

  /**
   * @return a version of this model with everything that is not part of the public api which has
   *         been stripped out. If the model itself is not part of the public api then
   *         <code>null</code> is returned!
   */
  public HierarchyModel toPublicAPI()
  {
    // TODO HIGH YP:  TODO...
    return this;
  }

  /**
   * @return <code>true</code> if this model is part of the public api
   */
  public boolean isPublicAPI()
  {
    // TODO HIGH YP:  TODO...
    return true;
  }

  /**
   * @return returns only the direct subclasses of the given class resource
   */
  private Collection<ClassDefinitionModel> getClasses(Map<ClassResource, ? extends ClassHierarchyModel> hierarchy,
                                                      ClassResource classResource)
  {
    ClassHierarchyModel model = hierarchy.get(classResource);
    if(model == null)
      return ClassHierarchyModel.NO_CLASSES;
    else
      return model.getClasses();
  }

  /**
   * @return returns all subclasses of the class resource (recursively...)
   */
  private Collection<ClassDefinitionModel> getAllClasses(Map<ClassResource, ? extends ClassHierarchyModel> hierarchy,
                                                         ClassResource classResource)
  {
    ClassHierarchyModel model = hierarchy.get(classResource);
    if(model == null)
      return ClassHierarchyModel.NO_CLASSES;
    else
    {
      Map<ClassResource, ClassDefinitionModel> classes =
        new HashMap<ClassResource, ClassDefinitionModel>();

      getAllClasses(hierarchy, classes, model);

      return classes.values();
    }
  }

  /**
   * Recursively collects all subclasses
   */
  private void getAllClasses(Map<ClassResource, ? extends ClassHierarchyModel> hierarchy,
                             Map<ClassResource, ClassDefinitionModel> classes,
                             ClassHierarchyModel model)
  {
    if(model != null)
    {
      for(ClassDefinitionModel cdm : model.getClasses())
      {
        ClassResource resource = cdm.getClassResource();

        if(!classes.containsKey(resource))
        {
          classes.put(resource, cdm);
          getAllClasses(hierarchy, classes, hierarchy.get(resource));
        }
      }
    }
  }

}

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

package com.pongasoft.kiwidoc.index.impl;

import com.pongasoft.util.core.annotations.FieldInitializer;
import com.pongasoft.util.core.annotations.ObjectInitializer;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author yan@pongasoft.com
 */
public class IndexableResource<T>
{
  private static final long serialVersionUID = 1L;
  
  public static class IRComparator<T> implements Comparator<IndexableResource<T>>, Serializable
  {
    private static final long serialVersionUID = 1L;

    private Comparator<T> _resourceComparator;

    @ObjectInitializer
    public IRComparator()
    {
    }

    public IRComparator(Comparator<T> resourceComparator)
    {
      _resourceComparator = resourceComparator;
    }

    public Comparator<T> getResourceComparator()
    {
      return _resourceComparator;
    }

    @FieldInitializer
    public void setResourceComparator(Comparator<T> resourceComparator)
    {
      _resourceComparator = resourceComparator;
    }

    public int compare(IndexableResource<T> o1, IndexableResource<T> o2)
    {
      if (o1 == null && o2 == null)
      {
        return 0;
      }
      else if (o1 == null)
      {
        return 1;
      }
      else if (o2 == null)
      {
        return -1;
      }
      else
      {
        int res = o1._name.compareTo(o2._name);
        if(res == 0)
        {
          res = _resourceComparator.compare(o1._resource, o2._resource);
        }
        return res;
      }
    }
  }

  private final T _resource;
  private final String _name;

  /**
   * Constructor
   */
  public IndexableResource(T resource, String name)
  {
    _resource = resource;
    _name = name;
  }

  public T getResource()
  {
    return _resource;
  }

  /**
   * @return the name (to be indexed in a search tree)
   */
  public String getSearchableName()
  {
    return _name;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();

    sb.append(getSearchableName()).append(" [").append(_resource).append("]");

    return sb.toString();
  }
}

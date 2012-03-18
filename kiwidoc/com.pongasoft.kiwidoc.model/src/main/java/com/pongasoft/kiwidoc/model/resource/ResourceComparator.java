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

package com.pongasoft.kiwidoc.model.resource;

import com.pongasoft.util.core.misc.Utils;

import java.io.Serializable;
import java.util.Comparator;

/**
 * The purpose of this comparator is to favor more recent versions when comparing 2 resources
 * in the same library.
 * 
 * @author yan@pongasoft.com
 */
public class ResourceComparator implements Comparator<Resource>, Serializable
{
  private static final long serialVersionUID = 1L;

  public static final ResourceComparator INSTANCE = new ResourceComparator();

  public static ResourceComparator instance()
  {
    return INSTANCE;
  }

  /**
   * Constructor
   */
  public ResourceComparator()
  {
  }

  public int compare(Resource r1, Resource r2)
  {
    int res = Utils.compareForNulls(r1, r2);
    if(res == Utils.NOT_NULL)
    {
      res = 0;

      if(r1 instanceof VersionableResource && r2 instanceof VersionableResource)
      {
        res = doCompare((VersionableResource) r1, (VersionableResource) r2);
      }

      if(res == 0)
        res = r1.toString().compareToIgnoreCase(r2.toString());
    }

    return res;
  }

  private int doCompare(VersionableResource r1, VersionableResource r2)
  {
    if(Utils.isEqual(r1.getLibraryResource(), r2.getLibraryResource()))
    {
      return Utils.compare(r2.getVersion(), r1.getVersion());
    }
    else
    {
      return 0;
    }
  }
}

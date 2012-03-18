
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

import java.util.Collection;
import java.util.ArrayList;

/**
 * Helper class for exportables.
 *
 * @author yan@pongasoft.com
 */
public class ExportableHelper
{
  /**
   * Process a collection of exportable with the same contract as the {@link Exportable#toPublicAPI()}
   * method: returns the same collection if not changed, otherwise returns a new collection.
   */
  public static <E extends Exportable<E>> Collection<E> toPublicAPI(Collection<E> collection)
  {
    if(collection == null)
      return null;
    
    boolean changed = false;

    Collection<E> newCollection = new ArrayList<E>(collection.size());
    for(E exportable : collection)
    {
      E newExportable = exportable.toPublicAPI();

      if(newExportable != null)
        newCollection.add(newExportable);

      changed |= newExportable != exportable;
    }

    if(!changed)
      return collection;
    else
      return newCollection;
  }
}

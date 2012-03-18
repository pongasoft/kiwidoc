
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

package com.pongasoft.kiwidoc.builder.serializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author yan@pongasoft.com
 */
public class CollectionSerializer<T, C> implements Serializer<Collection<T>, C>
{
  private final Serializer<T, C> _serializer;

  public CollectionSerializer(Serializer<T, C> serializer)
  {
    _serializer = serializer;
  }

  public Object serialize(Collection<T> collection) throws SerializerException
  {
    if(collection == null || collection.size() == 0)
      return null;

    if(collection.size() == 1)
      return _serializer.serialize(collection.iterator().next());

    Collection<Object> res = new ArrayList<Object>();
    for(T element : collection)
    {
      res.add(_serializer.serialize(element));
    }

    return res;
  }

  @SuppressWarnings("unchecked")
  public Collection<T> deserialize(C context, Object objectToDeserialize) throws SerializerException
  {
    if(objectToDeserialize == null)
      return Collections.EMPTY_LIST;

    List<T> res;
    if(objectToDeserialize instanceof List)
    {
      List jsonArray = (List) objectToDeserialize;
      res = new ArrayList<T>(jsonArray.size());
      for(Object o : jsonArray)
      {
        res.add(_serializer.deserialize(context, o));
      }
    }
    else
    {
      res = new ArrayList<T>(1);
      res.add(_serializer.deserialize(context, objectToDeserialize));
    }

    return res;
  }
}

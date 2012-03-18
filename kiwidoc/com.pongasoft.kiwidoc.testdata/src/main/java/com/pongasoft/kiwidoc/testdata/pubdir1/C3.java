
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

package com.pongasoft.kiwidoc.testdata.pubdir1;

import java.util.Iterator;

/**
 * Same as HashMap to check inner classes with generics...
 * 
 * @author yan@pongasoft.com
 */
public class C3<K extends Number,V extends C1>
{
  private C3<Number, C1>.KeyIterator<Iterator<Number>> _myField;
  
  public Iterator<K> keyIterator()
  {
    return new KeyIterator<Iterator<K>>();
  }

  public Iterator<V> valuesIterator()
  {
    return new ValuesIterator();
  }

  private abstract class HashIterator<E> implements Iterator<E>
  {
    public boolean hasNext()
    {
      return false;
    }

    public E next()
    {
      return null;
    }

    public void remove()
    {
    }
  }

  public class KeyIterator<E extends Iterator<? extends Number>> extends HashIterator<K>
  {
  }

  public class ValuesIterator extends HashIterator<V>
  {
  }
}

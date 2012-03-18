
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

package com.pongasoft.util.core.sort;

import java.util.Comparator;
import java.util.Arrays;
import java.lang.reflect.Array;

/**
 * This class implements a merge/sort algorithm to sort the elements while
 * accumulating them. The implementation uses 2 arrays that will accumulate
 * the results and they are merge sorted when they exceed the capacity. This
 * is particularly useful when the size of the output is known beforehand. You
 * use this class using the following pattern:
 * <pre>
 *  acc.add(o1);
 *  acc.add(o2);
 *  ...
 *  MyType[] res = (MyType[]) acc.close(MyType.class);
 * </pre>
 *
 * <p>The
 * resulting array is sorted in ascending order as provided by the comparator.
 * So if you want the 'biggest' results, you need to provide a descending
 * comparator.
 *
 * <p>If you provide only a comparator in the constructor, then the objects
 * provided in the {@link #add(Object)} method will simply be the one kept. If
 * you provide a copier, then the copier will be use to keep those objects. So
 * for all the objects that are discarded because they are already too big, you
 * can avoid the creation of a new object.
 *
 * @author yan@pongasoft.com */
public class MergeSortAccumulator<T>
{
  /**
   * Interface which defines how to copy an object */
  public static interface ObjectCopier<T>
  {
    T copy(T o);
  }

  /**
   * This trivial implemenation simply returns the object in the copy operation */
  private static class TrivialObjectCopier<T> implements ObjectCopier<T>
  {
    // since no state we can have a unique instance
    private final static TrivialObjectCopier INSTANCE = new TrivialObjectCopier();

    @SuppressWarnings("unchecked")
    private static <T> TrivialObjectCopier<T> instance()
    {
      return INSTANCE;
    }

    public T copy(T o)
    {
      return o;
    }
  }

  /**
   * The maximum number of elements to return */
  private final int _max;

  /**
   * The class of an element
   */
  private final Class<T> _elementClass;

  /**
   * The comparator used to sort the elements */
  private final Comparator<T> _comparator;

  /**
   * The copier used to make a copy of the object */
  private final ObjectCopier<T> _copier;

  /**
   * First bucket of size <code>_max</code>  */
  private T[] _b1;

  /**
   * Number of elements in <code>_b1</code>  */
  private int _b1Count = 0;

  /**
   * Second bucket of size <code>_max</code>  */
  private final T[] _b2;

  /**
   * Number of elements in <code>_b2</code>  */
  private int _b2Count = 0;

  /**
   * Keeps the minimum element to get in  */
  private T _minToGetIn = null;

  /**
   * Constructor
   *
   * @param comparator the objects are compared with this comparator and are
   * not copied */
  public MergeSortAccumulator(Class<T> elementClass, int max, Comparator<T> comparator)
  {
    this(elementClass, max, comparator, TrivialObjectCopier.<T>instance());
  }

  /**
   * Constructor
   *
   * @param comparator the objects are compared with this comparator
   * @param copier the objects retained are copied with this copier */
  @SuppressWarnings("unchecked")
  public MergeSortAccumulator(Class<T> elementClass, int max, Comparator<T> comparator, ObjectCopier<T> copier)
  {
    if(max <= 0)
      throw new IllegalArgumentException("max must be > 0");
    if(max == Integer.MAX_VALUE)
      throw new IllegalArgumentException("max cannot be Integer.MAX_VALUE");
    if(comparator == null)
      throw new IllegalArgumentException("comparator must not be null");

    _max = max;
    _comparator = comparator;
    _copier = copier;

    _elementClass = elementClass;
    _b1 = (T[]) Array.newInstance(_elementClass, max);
    _b2 = (T[]) Array.newInstance(_elementClass, max);
  }

  /**
   * Adds the element to the results. The method {@link ObjectCopier#copy(Object)} will
   * be called only when the element need to be kept, which can improve the creation
   * of objects.
   *
   * @param elt the element to add */
  public void add(T elt)
  {
    if(_b1Count < _max)
    {
      // if first bucket non empty, simply add the element to it
      _b1[_b1Count++] = _copier.copy(elt);
      if(_b1Count == _max)
      {
        // first bucket is now full: sort it and extract the minToGetIn
        Arrays.sort(_b1, _comparator);
        _minToGetIn = _b1[_max - 1];
      }
    }
    else
    {
      if(_comparator.compare(_minToGetIn, elt) > 0 )
      {
        // if the elt is greater than minToGetIn then simply add it to bucket 2
        _b2[_b2Count++] = _copier.copy(elt);
        if(_b2Count == _max)
        {
          // bucket 2 is full: sort it, merge b1 and b2 and extract minToGetIn
          Arrays.sort(_b2, _comparator);
          mergeSort();
          _minToGetIn = _b1[_max - 1];
        }
      }
    }
  }

  /**
   * This is the final method that needs to be called to get the result. The
   * resulting array is sorted in ascending order as provided by the comparator.
   * So if you want the 'biggest' results, you need to provide a descending
   * comparator.
   *
   * @return the array. You have to downcast it to the proper type.  */
  public T[] close()
  {
    if(_b2Count > 0)
    {
      Arrays.sort(_b2, 0, _b2Count, _comparator);
      mergeSort();
    }
    else
    {
      if(_b1Count  < _max)
        Arrays.sort(_b1, 0, _b1Count, _comparator);
    }

    @SuppressWarnings("unchecked")
    T[] res = (T[]) Array.newInstance(_elementClass, _b1Count);
    System.arraycopy(_b1, 0, res, 0, _b1Count);

    return res;
  }

  /**
   * Merge b1 and b2. When this method is called, b1 and b2 are already sorted.
   * At the end of this call b2 is empty and b1 contains the result of the
   * operation. */
  private void mergeSort()
  {
    @SuppressWarnings("unchecked")
    T[] b3 = (T[]) Array.newInstance(_elementClass, _max);

    int ib1 = 0;
    int ib2 = 0;
    int ib3 = 0;

    while (ib3 < _max)
    {
      if(_comparator.compare(_b2[ib2], _b1[ib1] ) >= 0)
      {
        b3[ib3++] = _b1[ib1++];
        if(ib1 == _b1Count )
        {
          System.arraycopy(_b2, ib2, b3, ib3, _max - ib3);
          break;
        }
      }
      else
      {
        b3[ib3++] = _b2[ib2++];
        if(ib2 == _b2Count)
        {
          System.arraycopy(_b1, ib1, b3, ib3, _max - ib3);
          break;
        }
      }
    }
    _b1 = b3;
    _b2Count = 0;
  }
}

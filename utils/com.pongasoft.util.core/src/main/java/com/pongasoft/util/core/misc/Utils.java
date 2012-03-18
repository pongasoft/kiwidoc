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

package com.pongasoft.util.core.misc;

import java.util.Date;
import java.lang.reflect.Array;

/**
 * Utils that are hard to categorize...
 * @author yan@pongasoft.com
 */
public class Utils
{
  public static final int NOT_NULL = -2;

  /**
   * Compares 2 values. Calls equals when they are not null
   *
   * @param o1
   * @param o2
   * @return <code>true</code> if they are equals or both <code>null</code> */
  public static boolean isEqual(Object o1, Object o2)
  {
    if(o1 == null)
      return o2 == null;

    return o1.equals(o2);
  }

  /**
   * Compares 2 values for <code>null</code> values only
   *
   * @return 0, 1 or -1 if o1 or o2 (or both) is <code>null</code>. {@link #NOT_NULL} otherwise */
  public static int compareForNulls(Object o1, Object o2)
  {
    if(o1 == null)
    {
      if(o2 == null)
        return 0;
      else
        return -1;
    }

    if(o2 == null)
    {
      // here d1 is not null (already tested..)
      return 1;
    }

    return NOT_NULL;
  }

  /**
   * Compares 2 values. Calls compareTo when they are not null
   *
   * @param o1
   * @param o2
   * @return the result of compareTo */
  public static <T extends Comparable<T>> int compare(T o1, T o2)
  {
    if(o1 == null)
    {
      if(o2 == null)
        return 0;
      else
        return -1;
    }

    if(o2 == null)
    {
      // here d1 is not null (already tested..)
      return 1;
    }

    return o1.compareTo(o2);
  }

  /**
   * Compares 2 ints. The purpose of this method is to return 0,1,-1 depending on how
   * o1 and o2 compares
   *
   * @param o1
   * @param o2
   * @return 0 if o1==o2, -1 if o1 &lt; o2, 1, if o1 &gt; o2 */
  public static int compare(int o1, int o2)
  {
    if(o1 == o2)
      return 0;

    if(o1 < o2)
      return -1;

    return 1;
  }

  /**
   * Compares 2 longs. The purpose of this method is to return 0,1,-1 depending on how
   * o1 and o2 compares
   *
   * @param o1
   * @param o2
   * @return 0 if o1==o2, -1 if o1 &lt; o2, 1, if o1 &gt; o2 */
  public static int compare(long o1, long o2)
  {
    if(o1 == o2)
      return 0;

    if(o1 < o2)
      return -1;

    return 1;
  }

  /**
   * Compares 2 floats. The purpose of this method is to return 0,1,-1 depending on how
   * o1 and o2 compares
   *
   * @param o1
   * @param o2
   * @return 0 if o1==o2, -1 if o1 &lt; o2, 1, if o1 &gt; o2 */
  public static int compare(float o1, float o2)
  {
    if(o1 == o2)
      return 0;

    if(o1 < o2)
      return -1;

    return 1;
  }

  /**
   * This method compares 2 dates. It is mainly written to account for a bug
   * in jdk15 which throws a ClassCastException when calling d1.compareTo(d2).
   * It also adds handling of <code>null</code> values.
   *
   * @param d1
   * @param d2
   * @return 0 if equal, &lt; 0 if d1 &lt; d2 and &gt; 0 if d1 &gt; d2
   */
  public static int compareTo(Date d1, Date d2)
  {
    if(d1 == null)
    {
      if(d2 == null)
        return 0;
      else
        return -1;
    }

    if(d2 == null)
    {
      // here d1 is not null (already tested..)
      return 1;
    }

    // here d1 and d2 are not null
    long delta = d1.getTime() - d2.getTime();

    if (delta > 0)
    {
      return 1;
    }
    else if (delta < 0)
    {
      return -1;
    }
    else
    {
      return 0;
    }
  }

  /**
   * Expands the array provided by one using idx as the point where room should
   * be made. After this call res[idx] will be the empty slot. All other values
   * have been copied from the original array.
   *
   * @param array the array to expand
   * @param idx the index where to make room in the array
   * @return the new expanded array */
  public static <T> T expandArray(T array, int idx)
  {
    return expandArray(array, idx, 1);
  }

  /**
   * Expands the array provided by <code>size</code> using idx as the point
   * where room should be made. After this call {res[idx]..res[idx+size-1]} will
   * be the empty slots. All other values have been copied from the original array.
   *
   * @param array the array to expand
   * @param idx the index where to make room in the array
   * @return the new expanded array */
  @SuppressWarnings("unchecked")
  public static <T> T expandArray(T array, int idx, int size)
  {
    int len = Array.getLength(array);
    T res = (T) Array.newInstance(array.getClass().getComponentType(), len + size);

    if(len == 0)
    {
      if(idx != 0)
        throw new IllegalArgumentException("idx should be 0 when empty array");
    }
    else
    {
      if(idx == 0)
      {
        // insert at beginning of the array: we shift everything by size
        System.arraycopy(array,  0, res, size, len);
      }
      else
      {
        if(idx == len)
        {
          // insert at end of the array: we simply copy the array
          System.arraycopy(array,  0, res, 0, len);
        }
        else
        {
          // insert in the middle (most complex case): we have to make 2 copies
          System.arraycopy(array,  0, res, 0, idx);
          System.arraycopy(array,  idx, res, idx + size, len - idx);
        }
      }
    }

    return res;
  }
}

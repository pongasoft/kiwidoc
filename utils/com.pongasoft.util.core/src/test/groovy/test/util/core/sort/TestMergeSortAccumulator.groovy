
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

package test.util.core.sort

import com.pongasoft.util.core.sort.MergeSortAccumulator

/**
 * Compares 2 integer */
private class IntegerComparator implements Comparator<Integer>
{
  public final static IntegerComparator ASCENDING_INSTANCE =
    new IntegerComparator(true);
  public final static IntegerComparator DESCENDING_INSTANCE =
    new IntegerComparator(false);

  private final boolean _ascending;

  public IntegerComparator(boolean ascending)
  {
    _ascending = ascending;
  }

  public int compare(Integer o1, Integer o2)
  {
    if(_ascending)
      return o1.intValue() - o2.intValue();

    return o2.intValue() - o1.intValue();
  }
}

/**
 * Implements the {@link MergeSortAccumulator.ObjectCopier} interface
 * to keep track of when copy is called and the fact that the object are
 * actually copied by returning a new object.
 */
private static class MyObjectCopier implements MergeSortAccumulator.ObjectCopier
{
  private int _copyCount = 0;

  public Object copy(Object o)
  {
    _copyCount++;
    Integer i = (Integer) o;
    return new Integer(i.intValue());
  }

  public int getCopyCount()
  {
    return _copyCount;
  }
}


/**
 * @author yan@pongasoft.com
*/
 public class TestMergeSortAccumulator extends GroovyTestCase
{
  /**
   * Test of merge sort */
  public void testMergeSortAccumulator()
  {
    int N = 5;
    int M = 23;

    MergeSortAccumulator acc =
      new MergeSortAccumulator(Integer.class, N, IntegerComparator.DESCENDING_INSTANCE);

    (0..M).each { acc.add(it) }

    check((M..M-4), acc);

    acc =
      new MergeSortAccumulator(Integer.class, N, IntegerComparator.DESCENDING_INSTANCE);

    (M..0).each { acc.add(it) }

    check((M..M-4), acc);

    acc =
      new MergeSortAccumulator(Integer.class, N, IntegerComparator.ASCENDING_INSTANCE);

    (0..M).each { acc.add(it) }

    check((0..4), acc);

    acc =
      new MergeSortAccumulator(Integer.class, N, IntegerComparator.ASCENDING_INSTANCE);

    (M..0).each { acc.add(it) }

    check((0..4), acc);

    // empty array
    acc =
      new MergeSortAccumulator(Integer.class, N, IntegerComparator.ASCENDING_INSTANCE);

    check([], acc);

    // 1 value
    acc =
      new MergeSortAccumulator(Integer.class, N, IntegerComparator.ASCENDING_INSTANCE);
    acc.add(4);
    check([4], acc);

    // 2 values
    acc =
      new MergeSortAccumulator(Integer.class, N, IntegerComparator.ASCENDING_INSTANCE);
    [4,3].each { acc.add(it) }
    check([3,4], acc);

    // 3 values
    acc =
      new MergeSortAccumulator(Integer.class, N, IntegerComparator.ASCENDING_INSTANCE);
    [4,3,2].each { acc.add(it) }
    check([2,3,4], acc);

    // random values
    int[] array = new int[M + 1];
    for(int i = 0; i <= M; i++)
    {
      array[i] = i;
    }

    Random rnd = new Random();
    for(int i = 0; i < 100; i++)
    {
      shuffle(array, rnd);
      acc =
        new MergeSortAccumulator(Integer.class, N, IntegerComparator.DESCENDING_INSTANCE);

      for(int j = 0; j < array.length; j++)
      {
        acc.add(array[j]);

      }

      check((M..M-4), acc);
    }

  }

  private Integer[] check(expected, MergeSortAccumulator<Integer> acc)
  {
    Integer[] res = acc.close()
    assertEquals(expected.collect {it}, res.collect {it})
    return res
  }

  /**
   * Test of merge sort with reduce copy */
  public void testMergeSortAccumulatorCopy()
  {
    int N = 2;

    Integer i1 = new Integer(1);
    Integer i2 = new Integer(2);
    Integer i3 = new Integer(3);

    // first we check that when not using a copier the objects are the same
    MergeSortAccumulator acc =
      new MergeSortAccumulator(Integer.class, N,
                               IntegerComparator.DESCENDING_INSTANCE);

    [i1,i2,i3].each { acc.add(it) }

    Integer[] sorted = check([3,2], acc);
    assertEquals(i3, sorted[0]);
    assertEquals(i2, sorted[1]);

    // now we check that when we use a copier then the objects are copied
    MyObjectCopier copier = new MyObjectCopier();
    acc =
      new MergeSortAccumulator(Integer.class, N,
                               IntegerComparator.DESCENDING_INSTANCE,
                               copier);

    [i1,i2,i3].each { acc.add(it) }

    sorted = check([3,2], acc);
    assertNotSame(i3, sorted[0]);
    assertNotSame(i2, sorted[1]);
    assertEquals(3, copier.getCopyCount());

    // now we check that when we use a copier then the objects are discarded appropriately
    copier = new MyObjectCopier();
    acc =
      new MergeSortAccumulator(Integer.class, N,
                               IntegerComparator.DESCENDING_INSTANCE,
                               copier);

    [i3,i2,i1].each { acc.add(it) }

    (0..10).each { acc.add(-it) }

    sorted = check([3,2], acc);
    assertNotSame(i3, sorted[0]);
    assertNotSame(i2, sorted[1]);
    // there should be only 2 copies because we insert them in the righ order =>
    // i3 and i2 are copied but then all other entries are discarded
    assertEquals(2, copier.getCopyCount());
  }

  /**
   * Shuffles the array using the random generator provided
   *
   * @param array the array to shuffle
   * @param random the random number generator
   * @return the shuffled array */
  public static int[] shuffle(int[] array, Random random)
  {
    for(int i = array.length - 1; i > 2; --i)
    {
      int idx = random.nextInt(i);
      int tmp = array[idx];
      array[idx] = array[i];
      array[i] = tmp;
    }

    return array;
  }
}

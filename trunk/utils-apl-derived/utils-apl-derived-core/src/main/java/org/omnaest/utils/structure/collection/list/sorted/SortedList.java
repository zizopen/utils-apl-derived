/*******************************************************************************
 * Copyright 2012 Danny Kunz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.omnaest.utils.structure.collection.list.sorted;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;

/**
 * Extension of the {@link List} interface for sorted lists.
 * 
 * @see List
 * @see SortedSet
 * @author Omnaest
 * @param <E>
 */
public interface SortedList<E> extends List<E>
{
  /**
   * Returns the {@link Comparator} used by this {@link SortedList}
   * 
   * @return
   */
  Comparator<? super E> comparator();
  
  /**
   * Returns a sublist view of the current {@link SortedList} starting by the given <b>fromElement</b> which will be included in
   * the sublist and ending by the given <b>toElement</b> which will be excluded from the sublist.
   * 
   * @param fromElement
   *          lower point (inclusive)
   * @param toElement
   *          higher pint (exclusive)
   */
  SortedList<E> subList( E fromElement, E toElement );
  
  /**
   * Returns a view to the current {@link SortedList} which starts at the beginning and ends by the given element which itself is
   * excluded from the head list.
   * 
   * @param toElement
   *          end element (exclusive)
   * @return
   */
  SortedList<E> headList( E toElement );
  
  /**
   * Returns a view to the current {@link SortedList} starting by the given element up to the end including the given element.
   * 
   * @param fromElement
   * @return
   */
  SortedList<E> tailList( E fromElement );
  
  /**
   * Returns the first (lowest) element currently in this {@link SortedList}.
   */
  E first();
  
  /**
   * Returns the last (highest) element currently in this {@link SortedList}.
   */
  E last();
  
  /**
   * Similar to List#subList(int, int)
   */
  @Override
  public SortedList<E> subList( int fromIndex, int toIndex );
}

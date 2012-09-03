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

import org.omnaest.utils.structure.collection.list.decorator.ListDispatcher;

/**
 * Similar to {@link ListDispatcher} but for {@link SortedList}s
 * 
 * @author Omnaest
 * @param <E>
 */
public abstract class SortedListDispatcher<E> extends ListDispatcher<E> implements SortedSplitableList<E>
{
  
  /**
   * @see SortedListDispatcher
   * @param lists
   */
  public SortedListDispatcher( SortedList<E>... lists )
  {
    super( lists );
  }
  
  @Override
  protected SortedSplitableList<E> getList()
  {
    // 
    return (SortedSplitableList<E>) super.getList();
  }
  
  @Override
  public Comparator<? super E> comparator()
  {
    return this.getList().comparator();
  }
  
  @Override
  public SortedList<E> subList( E fromElement, E toElement )
  {
    return this.getList().subList( fromElement, toElement );
  }
  
  @Override
  public SortedList<E> headList( E toElement )
  {
    return this.getList().headList( toElement );
  }
  
  @Override
  public SortedList<E> tailList( E fromElement )
  {
    return this.getList().tailList( fromElement );
  }
  
  @Override
  public E first()
  {
    return this.getList().first();
  }
  
  @Override
  public E last()
  {
    return this.getList().last();
  }
  
  @Override
  public SortedList<E> splitAt( int index )
  {
    return this.getList().splitAt( index );
  }
  
  @Override
  public SortedList<E> subList( int fromIndex, int toIndex )
  {
    // 
    return (SortedList<E>) super.subList( fromIndex, toIndex );
  }
  
}

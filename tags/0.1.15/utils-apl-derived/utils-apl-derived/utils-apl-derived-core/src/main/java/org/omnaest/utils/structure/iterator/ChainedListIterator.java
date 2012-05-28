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
package org.omnaest.utils.structure.iterator;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A {@link ListIterator} which is based on a chain of other {@link ListIterator} instances. All {@link ListIterator}s are
 * traversed in their order.<br>
 * <br>
 * The {@link #hasNext()} will be true as long as at least one remaining {@link ListIterator} instance returns true for its
 * {@link ListIterator#hasNext()} function.
 * 
 * @see ListIterator
 * @see ChainedIterator
 * @author Omnaest
 * @param <E>
 */
public class ChainedListIterator<E> implements ListIterator<E>
{
  /* ********************************************** Variables ********************************************** */
  protected final ListIteratorDecoratorSwitchable<E> listIteratorDecoratorSwitchable;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see ChainedListIterator
   * @param iterators
   *          {@link Iterator}
   */
  public ChainedListIterator( ListIterator<E>... iterators )
  {
    super();
    this.listIteratorDecoratorSwitchable = new ListIteratorDecoratorSwitchable<E>( iterators );
  }
  
  /**
   * @see ChainedListIterator
   * @param lists
   *          {@link List}
   */
  public ChainedListIterator( List<E>... lists )
  {
    this( IteratorUtils.valueOf( lists ) );
  }
  
  @Override
  public boolean hasNext()
  {
    //
    this.listIteratorDecoratorSwitchable.switchToNextIteratorWhichHasNext();
    return this.listIteratorDecoratorSwitchable.hasNext();
  }
  
  @Override
  public E next()
  {
    //
    this.listIteratorDecoratorSwitchable.switchToNextIteratorWhichHasNext();
    return this.listIteratorDecoratorSwitchable.next();
  }
  
  @Override
  public void remove()
  {
    this.listIteratorDecoratorSwitchable.remove();
  }
  
  @Override
  public boolean hasPrevious()
  {
    //
    this.listIteratorDecoratorSwitchable.switchToPreviousIteratorWhichHasPrevious();
    return this.listIteratorDecoratorSwitchable.hasPrevious();
  }
  
  @Override
  public E previous()
  {
    //
    this.listIteratorDecoratorSwitchable.switchToPreviousIteratorWhichHasPrevious();
    return this.listIteratorDecoratorSwitchable.next();
  }
  
  @Override
  public int nextIndex()
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public int previousIndex()
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public void set( E e )
  {
    this.listIteratorDecoratorSwitchable.set( e );
  }
  
  @Override
  public void add( E e )
  {
    this.listIteratorDecoratorSwitchable.add( e );
  }
  
}

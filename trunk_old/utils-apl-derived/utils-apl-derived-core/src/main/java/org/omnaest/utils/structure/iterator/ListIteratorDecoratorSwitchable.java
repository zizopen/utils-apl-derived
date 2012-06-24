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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.omnaest.utils.structure.collection.list.ListUtils;

/**
 * This is a switchable {@link ListIterator} decorator, which holds a {@link List} of {@link ListIterator} instances which it can
 * address. Per default the first available {@link ListIterator} instance is active.
 * 
 * @author Omnaest
 */
public class ListIteratorDecoratorSwitchable<E> implements ListIterator<E>
{
  /* ********************************************** Variables ********************************************** */
  protected final List<ListIterator<E>> listIteratorList;
  protected int                         currentActiveListIteratorIndexPosition = 0;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see ListIteratorDecoratorSwitchable
   * @param iteratorList
   */
  public ListIteratorDecoratorSwitchable( Iterable<ListIterator<E>> iteratorList )
  {
    super();
    this.listIteratorList = ListUtils.valueOf( iteratorList );
  }
  
  public ListIteratorDecoratorSwitchable( ListIterator<E>... iterators )
  {
    this( Arrays.asList( iterators ) );
  }
  
  @Override
  public boolean hasNext()
  {
    // 
    final Iterator<E> activeIterator = this.getActiveListIterator();
    return activeIterator != null && activeIterator.hasNext();
  }
  
  @Override
  public E next()
  {
    //
    final Iterator<E> activeIterator = this.getActiveListIterator();
    return activeIterator != null ? activeIterator.next() : null;
  }
  
  @Override
  public boolean hasPrevious()
  {
    //
    final ListIterator<E> activeIterator = this.getActiveListIterator();
    return activeIterator != null && activeIterator.hasPrevious();
  }
  
  @Override
  public E previous()
  {
    //
    final ListIterator<E> activeIterator = this.getActiveListIterator();
    return activeIterator != null ? activeIterator.previous() : null;
  }
  
  @Override
  public int nextIndex()
  {
    final ListIterator<E> activeIterator = this.getActiveListIterator();
    return activeIterator != null ? activeIterator.nextIndex() : -1;
  }
  
  @Override
  public int previousIndex()
  {
    //
    final ListIterator<E> activeIterator = this.getActiveListIterator();
    return activeIterator != null ? activeIterator.previousIndex() : -1;
  }
  
  @Override
  public void remove()
  {
    //
    final Iterator<E> activeIterator = this.getActiveListIterator();
    if ( activeIterator != null )
    {
      activeIterator.remove();
    }
  }
  
  @Override
  public void set( E e )
  {
    //
    final ListIterator<E> activeIterator = this.getActiveListIterator();
    if ( activeIterator != null )
    {
      activeIterator.set( e );
    }
  }
  
  @Override
  public void add( E e )
  {
    //
    final ListIterator<E> activeIterator = this.getActiveListIterator();
    if ( activeIterator != null )
    {
      activeIterator.add( e );
    }
  }
  
  /**
   * Returns the currently active {@link ListIterator} instance or null, if no instance is active.
   * 
   * @return
   */
  public ListIterator<E> getActiveListIterator()
  {
    return this.currentActiveListIteratorIndexPosition >= 0
           && this.currentActiveListIteratorIndexPosition < this.listIteratorList.size() ? this.listIteratorList.get( this.currentActiveListIteratorIndexPosition )
                                                                                        : null;
  }
  
  /**
   * Switches to the {@link ListIterator} instance which has the given list iterator index position within the
   * {@link #getListIteratorList()}
   * 
   * @param listIteratorIndexPosition
   * @return this
   */
  public ListIteratorDecoratorSwitchable<E> switchTo( int listIteratorIndexPosition )
  {
    //
    this.currentActiveListIteratorIndexPosition = listIteratorIndexPosition;
    
    //
    return this;
  }
  
  /**
   * Similar to {@link #switchTo(int)} but resolving the right {@link ListIterator} instance using the given {@link ListIterator}.
   * This means if the given {@link ListIterator} instance is not contained within the internal {@link List}, it will not be
   * activated.
   * 
   * @see #getListIteratorList()
   * @param listIterator
   * @return this
   */
  public ListIteratorDecoratorSwitchable<E> switchTo( ListIterator<E> listIterator )
  {
    //
    int iteratorIndexPosition = this.listIteratorList.indexOf( listIterator );
    return this.switchTo( iteratorIndexPosition );
  }
  
  /**
   * Switches the active {@link ListIterator} to the next {@link ListIterator}
   * 
   * @return this
   */
  public ListIteratorDecoratorSwitchable<E> switchToNext()
  {
    //
    if ( this.currentActiveListIteratorIndexPosition < this.listIteratorList.size() )
    {
      this.currentActiveListIteratorIndexPosition++;
    }
    
    //
    return this;
  }
  
  /**
   * Switches the active {@link ListIterator} to the previous {@link ListIterator}
   * 
   * @return this
   */
  public ListIteratorDecoratorSwitchable<E> switchToPrevious()
  {
    //
    if ( this.currentActiveListIteratorIndexPosition >= 0 )
    {
      this.currentActiveListIteratorIndexPosition--;
    }
    
    //
    return this;
  }
  
  /**
   * Switches to the next {@link ListIterator} instance which returns true for {@link ListIterator#hasNext()}, but only if the
   * {@link #getActiveListIterator()} does not return true for {@link ListIterator#hasNext()} itself already.
   * 
   * @return this
   */
  public ListIteratorDecoratorSwitchable<E> switchToNextIteratorWhichHasNext()
  {
    //
    while ( ( this.hasActiveListIterator() && !this.getActiveListIterator().hasNext() || !this.hasActiveListIterator() )
            && this.hasNextListIteratorToSwitchTo() )
    {
      this.switchToNext();
    }
    
    //
    return this;
  }
  
  /**
   * Switches to the next {@link ListIterator} instance which returns true for {@link ListIterator#hasNext()}, but only if the
   * {@link #getActiveListIterator()} does not return true for {@link ListIterator#hasNext()} itself already.
   * 
   * @return this
   */
  public ListIteratorDecoratorSwitchable<E> switchToPreviousIteratorWhichHasPrevious()
  {
    //
    while ( ( this.hasActiveListIterator() && !this.getActiveListIterator().hasPrevious() || !this.hasActiveListIterator() )
            && this.hasPreviousListIteratorToSwitchTo() )
    {
      this.switchToPrevious();
    }
    
    //
    return this;
  }
  
  /**
   * Returns true if {@link #switchToPrevious()} will find another active {@link ListIterator}
   * 
   * @return
   */
  public boolean hasPreviousListIteratorToSwitchTo()
  {
    //
    return this.currentActiveListIteratorIndexPosition > 0;
  }
  
  /**
   * Returns true if {@link #switchToNext()} will find another active {@link ListIterator}
   * 
   * @return
   */
  public boolean hasNextListIteratorToSwitchTo()
  {
    //
    return this.currentActiveListIteratorIndexPosition < this.listIteratorList.size() - 1;
  }
  
  /**
   * Returns true, if there is an active {@link Iterator} instance set
   * 
   * @return
   */
  public boolean hasActiveListIterator()
  {
    return this.getActiveListIterator() != null;
  }
  
  /**
   * @return the iteratorList
   */
  public List<ListIterator<E>> getListIteratorList()
  {
    return this.listIteratorList;
  }
  
}

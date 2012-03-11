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

import org.omnaest.utils.structure.collection.list.ListUtils;

/**
 * This is a switchable {@link Iterator} decorator, which holds a {@link List} of {@link Iterator} instances which it can address.
 * Per default the first available {@link Iterator} instance is active.
 * 
 * @see #switchTo(int)
 * @author Omnaest
 */
public class IteratorDecoratorSwitchable<E> implements Iterator<E>
{
  /* ********************************************** Variables ********************************************** */
  protected final List<Iterator<E>> iteratorList;
  protected int                     currentActiveIteratorIndexPosition = 0;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see IteratorDecoratorSwitchable
   * @param iteratorList
   */
  public IteratorDecoratorSwitchable( Iterable<Iterator<E>> iteratorList )
  {
    super();
    this.iteratorList = ListUtils.valueOf( iteratorList );
  }
  
  public IteratorDecoratorSwitchable( Iterator<E>... iterators )
  {
    this( Arrays.asList( iterators ) );
  }
  
  @Override
  public boolean hasNext()
  {
    // 
    final Iterator<E> activeIterator = this.getActiveIterator();
    return activeIterator != null && activeIterator.hasNext();
  }
  
  @Override
  public E next()
  {
    //
    final Iterator<E> activeIterator = this.getActiveIterator();
    return activeIterator != null ? activeIterator.next() : null;
  }
  
  @Override
  public void remove()
  {
    //
    final Iterator<E> activeIterator = this.getActiveIterator();
    if ( activeIterator != null )
    {
      this.getActiveIterator().remove();
    }
  }
  
  /**
   * Returns the currently active {@link Iterator} instance or null, if no instance is active.
   * 
   * @return
   */
  public Iterator<E> getActiveIterator()
  {
    return this.currentActiveIteratorIndexPosition >= 0 && this.currentActiveIteratorIndexPosition < this.iteratorList.size() ? this.iteratorList.get( this.currentActiveIteratorIndexPosition )
                                                                                                                             : null;
  }
  
  /**
   * Switches to the {@link Iterator} instance which has the given iterator index position within the {@link #getIteratorList()}
   * 
   * @param iteratorIndexPosition
   * @return this
   */
  public IteratorDecoratorSwitchable<E> switchTo( int iteratorIndexPosition )
  {
    //
    this.currentActiveIteratorIndexPosition = iteratorIndexPosition;
    
    //
    return this;
  }
  
  /**
   * Similar to {@link #switchTo(int)} but resolving the right {@link Iterator} instance using the given {@link Iterator}. This
   * means if the given {@link Iterator} instance is not contained within the internal list, it will not be activated.
   * 
   * @see #getIteratorList()
   * @param iterator
   * @return this
   */
  public IteratorDecoratorSwitchable<E> switchTo( Iterator<E> iterator )
  {
    //
    int iteratorIndexPosition = this.iteratorList.indexOf( iterator );
    return this.switchTo( iteratorIndexPosition );
  }
  
  /**
   * Switches the active {@link Iterator} to the next {@link Iterator}
   * 
   * @return this
   */
  public IteratorDecoratorSwitchable<E> switchToNext()
  {
    //
    if ( this.currentActiveIteratorIndexPosition < this.iteratorList.size() )
    {
      this.currentActiveIteratorIndexPosition++;
    }
    
    //
    return this;
  }
  
  /**
   * Switches the active {@link Iterator} to the previous {@link Iterator}
   * 
   * @return this
   */
  public IteratorDecoratorSwitchable<E> switchToPrevious()
  {
    //
    if ( this.currentActiveIteratorIndexPosition >= 0 )
    {
      this.currentActiveIteratorIndexPosition--;
    }
    
    //
    return this;
  }
  
  /**
   * Switches to the next {@link Iterator} instance which returns true for {@link Iterator#hasNext()}, but only if the
   * {@link #getActiveIterator()} does not return true for {@link Iterator#hasNext()} itself already.
   * 
   * @return this
   */
  public IteratorDecoratorSwitchable<E> switchToNextIteratorWhichHasNext()
  {
    //
    while ( this.hasActiveIterator() && !this.getActiveIterator().hasNext() )
    {
      this.switchToNext();
    }
    
    //
    return this;
  }
  
  /**
   * Returns true, if there is an active {@link Iterator} instance set
   * 
   * @return
   */
  public boolean hasActiveIterator()
  {
    return this.getActiveIterator() != null;
  }
  
  /**
   * @return the iteratorList
   */
  public List<Iterator<E>> getIteratorList()
  {
    return this.iteratorList;
  }
  
}

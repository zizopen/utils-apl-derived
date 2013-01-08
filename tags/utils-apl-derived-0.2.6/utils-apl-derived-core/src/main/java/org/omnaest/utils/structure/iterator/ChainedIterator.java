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

/**
 * A {@link Iterator} which is based on a chain of other {@link Iterator} instances. All {@link Iterator}s are traversed in their
 * order.<br>
 * <br>
 * The {@link #hasNext()} will be true as long as at least one remaining {@link Iterator} instance returns true for its
 * {@link Iterator#hasNext()} function.
 * 
 * @see Iterator
 * @see ChainedListIterator
 * @author Omnaest
 * @param <E>
 */
public class ChainedIterator<E> implements Iterator<E>
{
  /* ********************************************** Variables ********************************************** */
  protected final IteratorDecoratorSwitchable<E> iteratorDecoratorSwitchable;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see ChainedIterator
   * @param iterators
   *          {@link Iterator}
   */
  public ChainedIterator( Iterator<E>... iterators )
  {
    super();
    this.iteratorDecoratorSwitchable = new IteratorDecoratorSwitchable<E>( iterators );
  }
  
  /**
   * @see ChainedIterator
   * @param iterables
   *          {@link Iterable}
   */
  public ChainedIterator( Iterable<E>... iterables )
  {
    this( IteratorUtils.valueOf( iterables ) );
  }
  
  @Override
  public boolean hasNext()
  {
    //
    this.iteratorDecoratorSwitchable.switchToNextIteratorWhichHasNext();
    
    // 
    return this.iteratorDecoratorSwitchable.hasNext();
  }
  
  @Override
  public E next()
  {
    this.iteratorDecoratorSwitchable.switchToNextIteratorWhichHasNext();
    return this.iteratorDecoratorSwitchable.next();
  }
  
  @Override
  public void remove()
  {
    this.iteratorDecoratorSwitchable.remove();
  }
  
}

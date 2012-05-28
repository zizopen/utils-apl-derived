/*******************************************************************************
 * Copyright 2011 Danny Kunz
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

import java.util.Queue;

/**
 * Adapter to use a given {@link Queue} as {@link CircularIterator}.<br>
 * <br>
 * This adapter does not support the {@link #remove()} method and throws an {@link UnsupportedOperationException} therefore.
 * 
 * @author Omnaest
 */
public class QueueToCircularIteratorAdapter<E> implements CircularIterator<E>
{
  /* ********************************************** Variables ********************************************** */
  protected Queue<E> queue = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see QueueToCircularIteratorAdapter
   * @param queue
   */
  public QueueToCircularIteratorAdapter( Queue<E> queue )
  {
    super();
    this.queue = queue;
  }
  
  @Override
  public boolean hasNext()
  {
    return this.queue != null && !this.queue.isEmpty();
  }
  
  @Override
  public E next()
  {
    //
    E retval = null;
    
    //
    if ( this.hasNext() )
    {
      retval = this.queue.poll();
      this.queue.add( retval );
    }
    
    //
    return retval;
  }
  
  @Override
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
  
}

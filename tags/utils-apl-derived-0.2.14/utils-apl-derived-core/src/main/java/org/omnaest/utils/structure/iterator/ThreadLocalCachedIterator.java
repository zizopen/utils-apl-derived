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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.omnaest.utils.structure.element.ElementHolderUnmodifiable;
import org.omnaest.utils.structure.element.accessor.AccessorReadable;

/**
 * {@link Iterator} decorator which uses a {@link ThreadLocal} to store any element within an internal {@link ThreadLocal}
 * instance. This means that when calling the {@link #hasNext()} method the {@link #next()} method of the decorated and internal
 * available {@link Iterator} is called and the result {@link Object} is stored within the {@link ThreadLocal}. <br>
 * Based on this behavior any client have to ensure that the same {@link Thread} is calling the {@link #next()} function, if it
 * has called the {@link #hasNext()} beforehand. Otherwise elements returned from the original {@link Iterator} can get lost. <br>
 * <br>
 * The big advantage of this implementation is the ability of high multithreaded performance and reliability of the
 * {@link Iterator} contract. <br>
 * The underlying {@link Iterator} instance will only be locked between the immediately following {@link #hasNext()} and
 * {@link #next()} function calls.
 * 
 * @author Omnaest
 * @param <E>
 */
public class ThreadLocalCachedIterator<E> implements Iterator<E>
{
  /* ********************************************** Variables ********************************************** */
  private final Iterator<E>                      iterator;
  private final ThreadLocal<AccessorReadable<E>> threadLocal = new ThreadLocal<AccessorReadable<E>>();
  private final Lock                             lock        = new ReentrantLock( true );
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @see ThreadLocalCachedIterator
   * @param iterator
   */
  public ThreadLocalCachedIterator( Iterator<E> iterator )
  {
    super();
    this.iterator = IteratorUtils.lockedIterator( iterator, this.lock );
  }
  
  @Override
  public boolean hasNext()
  {
    //
    return this.getOrResolveNextElement() != null;
  }
  
  @Override
  public E next()
  {
    //
    E retval = this.getOrResolveNextElement();
    this.clearCurrentCache();
    
    // 
    return retval;
  }
  
  /**
   * @return
   */
  private E getOrResolveNextElement()
  {
    //
    E retval = null;
    
    //
    final AccessorReadable<E> accessorReadable = this.threadLocal.get();
    if ( accessorReadable != null )
    {
      retval = accessorReadable.getElement();
    }
    else
    {
      //
      this.lock.lock();
      try
      {
        //
        if ( this.iterator.hasNext() )
        {
          retval = this.iterator.next();
          this.threadLocal.set( new ElementHolderUnmodifiable<E>( retval ) );
        }
      }
      finally
      {
        this.lock.unlock();
      }
    }
    
    //
    return retval;
  }
  
  /**
   * 
   */
  private void clearCurrentCache()
  {
    //
    this.threadLocal.remove();
  }
  
  @Override
  public void remove()
  {
    throw new UnsupportedOperationException(
                                             "ThreadLocalIterator does not support the remove() method, since it cannot guarantee the cursor position in a concurrent environment" );
    
  }
  
}

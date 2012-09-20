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
package org.omnaest.utils.structure.iterator.decorator;

import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.omnaest.utils.structure.iterator.IterableUtils;

/**
 * {@link IteratorDecorator} which uses a {@link Lock} instance to synchronize its methods
 * 
 * @see IterableUtils#lockedIterator(Iterator, Lock)
 * @see IterableUtils#lockedByReentrantLockIterator(Iterator)
 * @author Omnaest
 * @param <E>
 */
public class LockingIteratorDecorator<E> extends IteratorDecorator<E>
{
  /* ********************************************** Variables ********************************************** */
  protected final Lock lock;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @see LockingIteratorDecorator
   * @param iterator
   * @param lock
   */
  public LockingIteratorDecorator( Iterator<E> iterator, Lock lock )
  {
    super( iterator );
    this.lock = lock;
  }
  
  /**
   * Uses a new {@link ReentrantLock} instance as {@link Lock}
   * 
   * @see LockingIteratorDecorator
   * @param iterator
   */
  public LockingIteratorDecorator( Iterator<E> iterator )
  {
    super( iterator );
    this.lock = new ReentrantLock();
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.iterator.decorator.IteratorDecorator#hasNext()
   */
  @Override
  public boolean hasNext()
  {
    //
    boolean retval = false;
    
    //
    this.lock.lock();
    try
    {
      retval = super.hasNext();
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.iterator.decorator.IteratorDecorator#next()
   */
  @Override
  public E next()
  {
    //
    E retval = null;
    
    //
    this.lock.lock();
    try
    {
      retval = super.next();
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.iterator.decorator.IteratorDecorator#remove()
   */
  @Override
  public void remove()
  {
    //
    this.lock.lock();
    try
    {
      super.remove();
    }
    finally
    {
      this.lock.unlock();
    }
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.iterator.decorator.IteratorDecorator#toString()
   */
  @Override
  public String toString()
  {
    //
    String retval = null;
    
    //
    this.lock.lock();
    try
    {
      retval = super.toString();
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
}

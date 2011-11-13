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
package org.omnaest.utils.structure.collection;

import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread safe {@link Iterator} delegate which makes use of an internal {@link ReentrantLock} to avoid concurrent access of the
 * underlying {@link Iterator}.
 * 
 * @author Omnaest
 * @param <E>
 */
public class ThreadSafeIterator<E> implements Iterator<E>
{
  
  /* ********************************************** Variables ********************************************** */
  protected Iterator<E>   iterator      = null;
  protected ReentrantLock reentrantLock = new ReentrantLock();
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see ThreadSafeIterator
   * @param iterator
   */
  public ThreadSafeIterator( Iterator<E> iterator )
  {
    super();
    this.iterator = iterator;
  }
  
  @Override
  public boolean hasNext()
  {
    //
    boolean retval = false;
    
    //
    this.reentrantLock.lock();
    try
    {
      //
      retval = this.iterator != null && this.iterator.hasNext();
    }
    finally
    {
      this.reentrantLock.unlock();
    }
    
    // 
    return retval;
  }
  
  @Override
  public E next()
  {
    //
    E retval = null;
    
    //
    this.reentrantLock.lock();
    try
    {
      //
      retval = this.iterator == null ? null : this.iterator.next();
    }
    finally
    {
      this.reentrantLock.unlock();
    }
    
    // 
    return retval;
  }
  
  @Override
  public void remove()
  {
    //
    this.reentrantLock.lock();
    try
    {
      if ( this.iterator != null )
      {
        //
        this.iterator.remove();
      }
    }
    finally
    {
      this.reentrantLock.unlock();
    }
  }
  
}

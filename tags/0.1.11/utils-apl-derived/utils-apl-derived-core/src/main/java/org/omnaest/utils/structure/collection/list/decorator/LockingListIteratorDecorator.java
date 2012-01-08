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
package org.omnaest.utils.structure.collection.list.decorator;

import java.util.ListIterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A {@link ListIteratorDecorator} which uses a {@link Lock} to synchronize all of its methods.
 * 
 * @author Omnaest
 * @param <E>
 */
public class LockingListIteratorDecorator<E> extends ListIteratorDecorator<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 9184804009754659323L;
  
  /* ********************************************** Variables ********************************************** */
  protected final Lock      lock;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see LockingListIteratorDecorator
   * @param listIterator
   * @param lock
   */
  public LockingListIteratorDecorator( ListIterator<E> listIterator, Lock lock )
  {
    super( listIterator );
    this.lock = lock;
  }
  
  /**
   * Uses a new {@link ReentrantLock} instance as {@link Lock}
   * 
   * @see LockingListIteratorDecorator
   * @param listIterator
   */
  public LockingListIteratorDecorator( ListIterator<E> listIterator )
  {
    super( listIterator );
    this.lock = new ReentrantLock();
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.list.decorator.ListIteratorDecorator#hasNext()
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
   * @see org.omnaest.utils.structure.collection.list.decorator.ListIteratorDecorator#next()
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
   * @see org.omnaest.utils.structure.collection.list.decorator.ListIteratorDecorator#hasPrevious()
   */
  @Override
  public boolean hasPrevious()
  {
    //
    boolean retval = false;
    
    //
    this.lock.lock();
    try
    {
      retval = super.hasPrevious();
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.list.decorator.ListIteratorDecorator#previous()
   */
  @Override
  public E previous()
  {
    //
    E retval = null;
    
    //
    this.lock.lock();
    try
    {
      retval = super.previous();
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.list.decorator.ListIteratorDecorator#nextIndex()
   */
  @Override
  public int nextIndex()
  {
    //
    int retval = 0;
    
    //
    this.lock.lock();
    try
    {
      retval = super.nextIndex();
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.list.decorator.ListIteratorDecorator#previousIndex()
   */
  @Override
  public int previousIndex()
  {
    //
    int retval = 0;
    
    //
    this.lock.lock();
    try
    {
      retval = super.previousIndex();
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.list.decorator.ListIteratorDecorator#remove()
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
   * @see org.omnaest.utils.structure.collection.list.decorator.ListIteratorDecorator#set(java.lang.Object)
   */
  @Override
  public void set( E e )
  {
    //
    this.lock.lock();
    try
    {
      super.set( e );
    }
    finally
    {
      this.lock.unlock();
    }
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.list.decorator.ListIteratorDecorator#add(java.lang.Object)
   */
  @Override
  public void add( E e )
  {
    //
    this.lock.lock();
    try
    {
      super.add( e );
    }
    finally
    {
      this.lock.unlock();
    }
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.list.decorator.ListIteratorDecorator#hashCode()
   */
  @Override
  public int hashCode()
  {
    //
    int retval = 0;
    
    //
    this.lock.lock();
    try
    {
      retval = super.hashCode();
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.list.decorator.ListIteratorDecorator#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object obj )
  {
    //
    boolean retval = false;
    
    //
    this.lock.lock();
    try
    {
      retval = super.equals( obj );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.list.decorator.ListIteratorDecorator#toString()
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

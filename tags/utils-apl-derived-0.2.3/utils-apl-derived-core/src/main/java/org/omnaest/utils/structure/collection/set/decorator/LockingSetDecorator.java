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
package org.omnaest.utils.structure.collection.set.decorator;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.iterator.IteratorUtils;

/**
 * {@link SetDecorator} which uses a {@link Lock} to synchronize all of its methods. The {@link #iterator()} method will return a
 * locked {@link Iterator} which uses the same {@link Lock} instance.
 * 
 * @see SetUtils#locked(Set, Lock)
 * @see SetUtils#lockedByReentrantLock(Set)
 * @author Omnaest
 * @param <E>
 */
public class LockingSetDecorator<E> extends SetDecorator<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 7421737609130259773L;
  
  /* ********************************************** Variables ********************************************** */
  protected final Lock      lock;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see LockingSetDecorator
   * @param set
   * @param lock
   */
  public LockingSetDecorator( Set<E> set, Lock lock )
  {
    super( set );
    this.lock = lock;
  }
  
  /**
   * Uses a new {@link ReentrantLock} instance as {@link Lock}
   * 
   * @see LockingSetDecorator
   * @param set
   */
  public LockingSetDecorator( Set<E> set )
  {
    super( set );
    this.lock = new ReentrantLock();
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.decorator.CollectionDecorator#size()
   */
  @Override
  public int size()
  {
    //
    int retval = 0;
    
    //
    this.lock.lock();
    try
    {
      retval = super.size();
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.decorator.CollectionDecorator#isEmpty()
   */
  @Override
  public boolean isEmpty()
  {
    //
    boolean retval = false;
    
    //
    this.lock.lock();
    try
    {
      retval = super.isEmpty();
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.decorator.CollectionDecorator#contains(java.lang.Object)
   */
  @Override
  public boolean contains( Object o )
  {
    //
    boolean retval = false;
    
    //
    this.lock.lock();
    try
    {
      retval = super.contains( o );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.decorator.CollectionDecorator#iterator()
   */
  @Override
  public Iterator<E> iterator()
  {
    return IteratorUtils.lockedIterator( super.iterator(), this.lock );
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.decorator.CollectionDecorator#toArray()
   */
  @Override
  public Object[] toArray()
  {
    //
    Object[] retval = null;
    
    //
    this.lock.lock();
    try
    {
      retval = super.toArray();
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.decorator.CollectionDecorator#toArray(T[])
   */
  @Override
  public <T> T[] toArray( T[] a )
  {
    //
    T[] retval = null;
    
    //
    this.lock.lock();
    try
    {
      retval = super.toArray( a );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.decorator.CollectionDecorator#add(java.lang.Object)
   */
  @Override
  public boolean add( E e )
  {
    //
    boolean retval = false;
    
    //
    this.lock.lock();
    try
    {
      retval = super.add( e );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.decorator.CollectionDecorator#remove(java.lang.Object)
   */
  @Override
  public boolean remove( Object o )
  {
    //
    boolean retval = false;
    
    //
    this.lock.lock();
    try
    {
      retval = super.remove( o );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.decorator.CollectionDecorator#containsAll(java.util.Collection)
   */
  @Override
  public boolean containsAll( Collection<?> c )
  {
    //
    boolean retval = false;
    
    //
    this.lock.lock();
    try
    {
      retval = super.containsAll( c );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.decorator.CollectionDecorator#addAll(java.util.Collection)
   */
  @Override
  public boolean addAll( Collection<? extends E> c )
  {
    //
    boolean retval = false;
    
    //
    this.lock.lock();
    try
    {
      retval = super.addAll( c );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.decorator.CollectionDecorator#removeAll(java.util.Collection)
   */
  @Override
  public boolean removeAll( Collection<?> c )
  {
    //
    boolean retval = false;
    
    //
    this.lock.lock();
    try
    {
      retval = super.removeAll( c );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.decorator.CollectionDecorator#retainAll(java.util.Collection)
   */
  @Override
  public boolean retainAll( Collection<?> c )
  {
    //
    boolean retval = false;
    
    //
    this.lock.lock();
    try
    {
      retval = super.retainAll( c );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.decorator.CollectionDecorator#clear()
   */
  @Override
  public void clear()
  {
    //
    this.lock.lock();
    try
    {
      super.clear();
    }
    finally
    {
      this.lock.unlock();
    }
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.decorator.CollectionDecorator#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object o )
  {
    //
    boolean retval = false;
    
    //
    this.lock.lock();
    try
    {
      retval = super.equals( o );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.decorator.CollectionDecorator#hashCode()
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
   * @see org.omnaest.utils.structure.collection.decorator.CollectionDecorator#toString()
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

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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.iterator.IteratorUtils;

/**
 * A {@link ListDecorator} which uses a given {@link Lock} instance to synchronize all of its methods. The {@link #iterator()} and
 * {@link #listIterator()} will return a locked {@link Iterator} which uses the same {@link Lock} instance. The
 * {@link #subList(int, int)} will return a new locked {@link List} with the given {@link Lock}, too.
 * 
 * @author Omnaest
 * @param <E>
 */
public class LockingListDecorator<E> extends ListDecorator<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -203141602643867326L;
  
  /* ********************************************** Variables ********************************************** */
  protected final Lock      lock;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @see LockingListDecorator
   * @param list
   * @param lock
   */
  public LockingListDecorator( List<E> list, Lock lock )
  {
    super( list );
    this.lock = lock;
  }
  
  /**
   * Uses a new {@link ReentrantLock} instance as {@link Lock}
   * 
   * @see LockingListDecorator
   * @param list
   */
  public LockingListDecorator( List<E> list )
  {
    super( list );
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
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.list.decorator.ListDecorator#addAll(int, java.util.Collection)
   */
  @Override
  public boolean addAll( int index, Collection<? extends E> c )
  {
    //
    boolean retval = false;
    
    //
    this.lock.lock();
    try
    {
      retval = super.addAll( index, c );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.list.decorator.ListDecorator#get(int)
   */
  @Override
  public E get( int index )
  {
    //
    E retval = null;
    
    //
    this.lock.lock();
    try
    {
      retval = super.get( index );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.list.decorator.ListDecorator#set(int, java.lang.Object)
   */
  @Override
  public E set( int index, E element )
  {
    //
    E retval = null;
    
    //
    this.lock.lock();
    try
    {
      retval = super.set( index, element );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.list.decorator.ListDecorator#add(int, java.lang.Object)
   */
  @Override
  public void add( int index, E element )
  {
    //
    this.lock.lock();
    try
    {
      super.add( index, element );
    }
    finally
    {
      this.lock.unlock();
    }
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.list.decorator.ListDecorator#remove(int)
   */
  @Override
  public E remove( int index )
  {
    //
    E retval = null;
    
    //
    this.lock.lock();
    try
    {
      retval = super.remove( index );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.list.decorator.ListDecorator#indexOf(java.lang.Object)
   */
  @Override
  public int indexOf( Object o )
  {
    //
    int retval = 0;
    
    //
    this.lock.lock();
    try
    {
      retval = super.indexOf( o );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.list.decorator.ListDecorator#lastIndexOf(java.lang.Object)
   */
  @Override
  public int lastIndexOf( Object o )
  {
    //
    int retval = 0;
    
    //
    this.lock.lock();
    try
    {
      retval = super.lastIndexOf( o );
    }
    finally
    {
      this.lock.unlock();
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.list.decorator.ListDecorator#listIterator()
   */
  @Override
  public ListIterator<E> listIterator()
  {
    return ListUtils.locked( super.listIterator(), this.lock );
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.list.decorator.ListDecorator#listIterator(int)
   */
  @Override
  public ListIterator<E> listIterator( int index )
  {
    return ListUtils.locked( super.listIterator( index ), this.lock );
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.structure.collection.list.decorator.ListDecorator#subList(int, int)
   */
  @Override
  public List<E> subList( int fromIndex, int toIndex )
  {
    return ListUtils.locked( super.subList( fromIndex, toIndex ), this.lock );
  }
  
}

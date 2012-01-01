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
package org.omnaest.utils.structure.collection.list;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Size limited {@link Queue} which can be configured to ignore further adding of elements if the size limit is reached, or to
 * dump the oldest entry.
 * 
 * @see #setRemoveFirstElementByExceedingSize(boolean)
 * @author Omnaest
 * @param <E>
 */
public class LimitedLinkedList<E> extends LinkedList<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID                  = 8220551181941161150L;
  /* ********************************************** Variables ********************************************** */
  private int               sizeMax                           = -1;
  private boolean           removeFirstElementByExceedingSize = true;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param collection
   * @param sizeMax
   */
  public LimitedLinkedList( Collection<? extends E> collection, int sizeMax )
  {
    super( collection );
    this.sizeMax = sizeMax;
    this.ensureMaximumSize();
  }
  
  /**
   * @param collection
   * @param sizeMax
   */
  public LimitedLinkedList( int sizeMax )
  {
    this();
    this.sizeMax = sizeMax;
  }
  
  /**
   * @see #setSizeMax(int)
   */
  public LimitedLinkedList()
  {
    super();
  }
  
  /**
   * @see #setSizeMax(int)
   * @param collection
   */
  public LimitedLinkedList( Collection<? extends E> collection )
  {
    super( collection );
  }
  
  @Override
  public void addFirst( E e )
  {
    super.addFirst( e );
    this.ensureMaximumSize();
  }
  
  /**
   * Ensures the {@link List} does not exceed the {@link #sizeMax}
   */
  private void ensureMaximumSize()
  {
    while ( this.size() > this.sizeMax )
    {
      if ( this.removeFirstElementByExceedingSize )
      {
        this.removeFirst();
      }
      else
      {
        this.removeLast();
      }
    }
  }
  
  @Override
  public void addLast( E e )
  {
    super.addLast( e );
    this.ensureMaximumSize();
  }
  
  @Override
  public boolean add( E e )
  {
    boolean retval = super.add( e );
    this.ensureMaximumSize();
    return retval;
  }
  
  @Override
  public boolean addAll( Collection<? extends E> c )
  {
    boolean retval = super.addAll( c );
    this.ensureMaximumSize();
    return retval;
  }
  
  @Override
  public boolean addAll( int index, Collection<? extends E> c )
  {
    boolean retval = super.addAll( index, c );
    this.ensureMaximumSize();
    return retval;
  }
  
  @Override
  public void add( int index, E element )
  {
    super.add( index, element );
    this.ensureMaximumSize();
  }
  
  /**
   * Sets the maximum size the {@link LimitedLinkedList} can have.
   * 
   * @param sizeMax
   */
  public void setSizeMax( int sizeMax )
  {
    this.sizeMax = sizeMax;
    this.ensureMaximumSize();
  }
  
  /**
   * Returns the maximum size threshold
   * 
   * @return
   */
  public int getSizeMax()
  {
    return this.sizeMax;
  }
  
  /**
   * @see #setRemoveFirstElementByExceedingSize(boolean)
   * @return
   */
  public boolean isRemoveFirstElementByExceedingSize()
  {
    return this.removeFirstElementByExceedingSize;
  }
  
  /**
   * If set to true always the {@link #removeFirst()} is called as long as the {@link #size()} exceeds the {@link #getSizeMax()}
   * otherwise the {@link #removeLast()}. Default is true.
   * 
   * @param removeFirstElementByExceedingSize
   */
  public void setRemoveFirstElementByExceedingSize( boolean removeFirstElementByExceedingSize )
  {
    this.removeFirstElementByExceedingSize = removeFirstElementByExceedingSize;
  }
  
}

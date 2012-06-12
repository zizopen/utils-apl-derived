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
package org.omnaest.utils.structure.collection.list.adapter;

import java.io.Serializable;
import java.util.List;
import java.util.ListIterator;

/**
 * Adapter to a given list. The modifications made to the iterator will be populated to the list and vice versa.
 * 
 * @see ListIterator
 * @author Omnaest
 */
public class ListToListIteratorAdapter<E> implements ListIterator<E>, Serializable
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID  = -1675622469124924790L;
  
  /* ********************************************** Variables ********************************************** */
  private List<E>           list              = null;
  private int               currentIndex      = -1;
  private int               lastReturnedIndex = -1;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * Creates a new instance of a list iterator.
   */
  public ListToListIteratorAdapter( List<E> list )
  {
    this( list, 0 );
  }
  
  /**
   * Creates a new list iterator which returns with the first call of {@link #next()} the element at the given start index
   * position of the list.
   * 
   * @param list
   * @param startIndex
   */
  public ListToListIteratorAdapter( List<E> list, int startIndex )
  {
    this.list = list;
    this.currentIndex = startIndex - 1;
  }
  
  private boolean isListNotNull()
  {
    if ( this.list == null )
    {
      throw new NullPointerException( "The list adapter has no valid source list." );
    }
    return true;
  }
  
  @Override
  public void add( E element )
  {
    if ( this.isListNotNull() )
    {
      this.list.add( ++this.currentIndex, element );
      this.lastReturnedIndex = -1;
    }
  }
  
  @Override
  public boolean hasNext()
  {
    return this.isListNotNull() && this.currentIndex + 1 >= 0 && this.currentIndex + 1 < this.list.size();
  }
  
  @Override
  public boolean hasPrevious()
  {
    return this.isListNotNull() && this.currentIndex >= 0 && this.currentIndex < this.list.size();
  }
  
  @Override
  public E next()
  {
    //
    E retval = null;
    
    //
    if ( this.hasNext() )
    {
      retval = this.list.get( ++this.currentIndex );
      this.lastReturnedIndex = this.currentIndex;
    }
    
    //
    return retval;
  }
  
  @Override
  public int nextIndex()
  {
    return this.currentIndex + 1;
  }
  
  @Override
  public E previous()
  {
    //
    E retval = null;
    
    //
    if ( this.hasPrevious() )
    {
      retval = this.list.get( this.currentIndex );
      this.lastReturnedIndex = this.currentIndex;
      this.currentIndex--;
    }
    
    //
    return retval;
  }
  
  @Override
  public int previousIndex()
  {
    return this.currentIndex;
  }
  
  @Override
  public void remove()
  {
    if ( this.isListNotNull() && ( this.lastReturnedIndex >= 0 ) )
    {
      this.list.remove( this.lastReturnedIndex );
      this.currentIndex--;
      this.lastReturnedIndex = -1;
    }
  }
  
  @Override
  public void set( E element )
  {
    if ( this.isListNotNull() && ( this.lastReturnedIndex >= 0 ) )
    {
      this.list.set( this.lastReturnedIndex, element );
    }
  }
  
  public List<E> getList()
  {
    return this.list;
  }
  
  public void setList( List<E> list )
  {
    this.list = list;
  }
  
  @Override
  public String toString()
  {
    return this.list.toString();
  }
  
}

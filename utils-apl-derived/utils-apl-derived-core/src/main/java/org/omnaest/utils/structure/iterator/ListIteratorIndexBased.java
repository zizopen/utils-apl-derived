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

import java.util.List;
import java.util.ListIterator;

/**
 * Abstract {@link ListIterator} implementation which translates the navigation and actions to index position based operations
 * 
 * @author Omnaest
 */
public class ListIteratorIndexBased<E> implements ListIterator<E>
{
  /* ********************************************** Variables ********************************************** */
  protected int                             indexPosition                = -1;
  protected int                             indexPositionNext            = 0;
  protected int                             indexPositionPrevious        = -1;
  protected boolean                         isIndexPositionValid         = true;
  protected boolean                         directionForward             = true;
  
  /* ********************************************** Beans / Services / References ********************************************** */
  protected ListIteratorIndexBasedSource<E> listIteratorIndexBasedSource = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see ListIteratorIndexBased
   * @author Omnaest
   * @param <E>
   */
  public static interface ListIteratorIndexBasedSource<E>
  {
    
    /**
     * Returns the size of the underlying structure which is the highest index position added by one
     * 
     * @return
     */
    public int size();
    
    /**
     * Returns the element at the given index position
     * 
     * @param indexPosition
     * @return
     */
    public E get( int indexPosition );
    
    /**
     * Removes the element at the given index position
     * 
     * @param indexPosition
     */
    public void remove( int indexPosition );
    
    /**
     * Sets the element at the given index position
     * 
     * @param indexPosition
     * @param element
     */
    public void set( int indexPosition, E element );
    
    /**
     * Adds a new element at the given index position moving the old element on that position and all following elements one
     * position further.
     * 
     * @param indexPosition
     * @param element
     */
    public void add( int indexPosition, E element );
  }
  
  /**
   * Adapter for any {@link List} implementation to act as a {@link ListIteratorIndexBasedSource}
   * 
   * @author Omnaest
   * @param <E>
   */
  public static class ListToListIteratorSourceAdapter<E> implements ListIteratorIndexBasedSource<E>
  {
    /* ********************************************** Beans / Services / References ********************************************** */
    protected List<E> list = null;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @param list
     */
    public ListToListIteratorSourceAdapter( List<E> list )
    {
      super();
      this.list = list;
    }
    
    @Override
    public int size()
    {
      return this.list.size();
    }
    
    @Override
    public E get( int indexPosition )
    {
      return this.list.get( indexPosition );
    }
    
    @Override
    public void remove( int indexPosition )
    {
      this.list.remove( indexPosition );
    }
    
    @Override
    public void set( int indexPosition, E element )
    {
      this.list.set( indexPosition, element );
    }
    
    @Override
    public void add( int indexPosition, E element )
    {
      this.list.add( indexPosition, element );
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see ListIteratorIndexBasedSource
   * @param listIteratorSource
   */
  public ListIteratorIndexBased( ListIteratorIndexBasedSource<E> listIteratorSource )
  {
    super();
    this.listIteratorIndexBasedSource = listIteratorSource;
  }
  
  /**
   * @param list
   */
  public ListIteratorIndexBased( List<E> list )
  {
    super();
    this.listIteratorIndexBasedSource = new ListToListIteratorSourceAdapter<E>( list );
  }
  
  /**
   * Calculates the {@link #indexPositionPrevious} , the {@link #indexPositionNext} and the {@link #isIndexPositionValid}
   */
  protected void calculateIndexPositionContext()
  {
    //
    if ( this.directionForward )
    {
      this.indexPositionNext = this.indexPosition + 1;
      this.indexPositionPrevious = this.indexPosition;
    }
    else
    {
      this.indexPositionNext = this.indexPosition;
      this.indexPositionPrevious = this.indexPosition - 1;
    }
    
    //
    this.isIndexPositionValid = this.isValidIndexPosition( this.indexPosition );
  }
  
  @Override
  public boolean hasNext()
  {
    return this.isValidIndexPosition( this.indexPositionNext );
  }
  
  /**
   * @param indexPosition
   * @return
   */
  protected boolean isValidIndexPosition( int indexPosition )
  {
    return indexPosition >= 0 && indexPosition < this.listIteratorIndexBasedSource.size();
  }
  
  @Override
  public E next()
  {
    //
    this.indexPosition = this.indexPositionNext;
    this.directionForward = true;
    
    //
    this.calculateIndexPositionContext();
    
    //
    return this.isIndexPositionValid ? this.listIteratorIndexBasedSource.get( this.indexPosition ) : null;
  }
  
  @Override
  public void remove()
  {
    //
    if ( this.isIndexPositionValid )
    {
      //
      this.listIteratorIndexBasedSource.remove( this.indexPosition );
      
      //
      this.isIndexPositionValid = false;
      this.indexPositionNext = this.indexPosition;
    }
  }
  
  @Override
  public boolean hasPrevious()
  {
    return this.isValidIndexPosition( this.indexPositionPrevious );
  }
  
  @Override
  public E previous()
  {
    //
    this.directionForward = false;
    this.indexPosition = this.indexPositionPrevious;
    
    //
    this.calculateIndexPositionContext();
    
    //
    return this.isIndexPositionValid ? this.listIteratorIndexBasedSource.get( this.indexPosition ) : null;
  }
  
  @Override
  public int nextIndex()
  {
    return this.directionForward ? this.indexPositionNext : this.indexPosition;
  }
  
  @Override
  public int previousIndex()
  {
    return !this.directionForward ? this.indexPositionPrevious : this.indexPosition;
  }
  
  @Override
  public void set( E element )
  {
    //
    if ( element != null && this.isIndexPositionValid )
    {
      this.listIteratorIndexBasedSource.set( this.indexPosition, element );
    }
  }
  
  @Override
  public void add( E element )
  {
    //
    if ( element != null && this.isIndexPositionValid )
    {
      //
      this.listIteratorIndexBasedSource.add( this.indexPosition, element );
      
      //
      this.indexPositionNext++;
    }
  }
}

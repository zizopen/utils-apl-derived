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
package org.omnaest.utils.structure.collection.list.sorted;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * {@link SortedList} implementation using insertion sort algorithm based on a simple {@link ArrayList}
 * 
 * @author Omnaest
 * @param <E>
 */
public class InsertionSortedList<E> extends SortedListAbstract<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -4290221104699140741L;
  /* ********************************************** Variables ********************************************** */
  protected final List<E>   list             = this.newInternalList();
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see InsertionSortedList
   */
  public InsertionSortedList()
  {
    super();
  }
  
  /**
   * @see InsertionSortedList
   * @param collection
   * @param comparator
   */
  public InsertionSortedList( Collection<E> collection, Comparator<E> comparator )
  {
    super( comparator );
    this.addAll( collection );
  }
  
  /**
   * @see InsertionSortedList
   * @param collection
   */
  public InsertionSortedList( Collection<E> collection )
  {
    super();
    this.addAll( collection );
  }
  
  /**
   * @see InsertionSortedList
   * @param comparator
   */
  public InsertionSortedList( Comparator<E> comparator )
  {
    super( comparator );
  }
  
  @Override
  public boolean add( E element )
  {
    //
    int insertionIndexPosition = Collections.binarySearch( this.list, element, this.comparator );
    if ( insertionIndexPosition < 0 )
    {
      insertionIndexPosition = -( insertionIndexPosition + 1 );
    }
    
    //
    this.list.add( insertionIndexPosition, element );
    
    //
    return true;
  }
  
  @Override
  public int size()
  {
    return this.list.size();
  }
  
  @Override
  public E get( int index )
  {
    return this.list.get( index );
  }
  
  @Override
  public E remove( int index )
  {
    return this.list.remove( index );
  }
  
  @Override
  public int indexOf( final Object object )
  {
    //
    int retval = -1;
    
    //
    try
    {
      //
      @SuppressWarnings("unchecked")
      final E element = (E) object;
      
      //
      List<E> list = this.list;
      while ( list != null )
      {
        //
        final int indexPosition = Collections.binarySearch( list, element, this.comparator );
        if ( indexPosition >= 0 )
        {
          retval = indexPosition;
          list = list.subList( 0, indexPosition );
        }
        else
        {
          list = null;
        }
      }
    }
    catch ( Exception e )
    {
    }
    
    //
    return retval;
  }
  
  @Override
  public int lastIndexOf( final Object object )
  {
    //
    int retval = -1;
    
    //
    try
    {
      //
      @SuppressWarnings("unchecked")
      final E element = (E) object;
      
      //
      List<E> list = this.list;
      int indexPositionDelta = 0;
      while ( list != null )
      {
        //
        final int indexPosition = Collections.binarySearch( list, element, this.comparator );
        if ( indexPosition >= 0 )
        {
          retval = indexPositionDelta + indexPosition;
          list = list.subList( indexPosition + 1, list.size() );
          indexPositionDelta += indexPosition + 1;
        }
        else
        {
          list = null;
        }
      }
    }
    catch ( Exception e )
    {
    }
    
    //
    return retval;
  }
  
  /**
   * Returns a new unsorted {@link List} instance. Per default the {@link ArrayList} implementation is used, but this can be
   * overriden by subclasses
   * 
   * @return new {@link List}
   */
  protected ArrayList<E> newInternalList()
  {
    return new ArrayList<E>();
  }
  
  @Override
  public void clear()
  {
    this.list.clear();
  }
  
  @Override
  protected SortedList<E> newInstance( Collection<E> collection )
  {
    return new InsertionSortedList<E>( collection );
  }
}

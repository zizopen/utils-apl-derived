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
package org.omnaest.utils.structure.array;

import java.lang.reflect.Array;
import java.util.List;

import org.omnaest.utils.structure.collection.list.ListAbstract;

/**
 * Adapter to use an {@link Array} instance as {@link List}. Any changes to the adapter will be reflected to the {@link Array} and
 * vice versa.<br>
 * <br>
 * Due to the immutable length of an {@link Array} any invocations of methods which change the size will throw an
 * {@link UnsupportedOperationException}.
 * 
 * @param <E>
 * @see Array
 * @see List
 * @author Omnaest
 */
public class ArrayToListAdapter<E> extends ListAbstract<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 2716942711754587491L;
  /* ********************************************** Variables ********************************************** */
  protected final E[]       array;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @see ArrayToListAdapter
   * @param array
   */
  public ArrayToListAdapter( E[] array )
  {
    super();
    this.array = array;
  }
  
  @Override
  public int size()
  {
    return this.array.length;
  }
  
  @Override
  public boolean add( E e )
  {
    throwUnsupportedOperationException();
    return false;
  }
  
  /**
   * @throws UnsupportedOperationException
   */
  private static void throwUnsupportedOperationException() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException( "Can not modify size of an array, please use the set(int,E) method instead" );
  }
  
  @Override
  public E get( int index )
  {
    return this.array[index];
  }
  
  @Override
  public E set( int index, E element )
  {
    //
    final E retval = this.array[index];
    
    //
    this.array[index] = element;
    
    //
    return retval;
  }
  
  @Override
  public void add( int index, E element )
  {
    throwUnsupportedOperationException();
  }
  
  @Override
  public E remove( int index )
  {
    throwUnsupportedOperationException();
    return null;
  }
  
  @Override
  public int indexOf( Object o )
  {
    //
    int retval = -1;
    
    //
    for ( int index = 0; index < this.size(); index++ )
    {
      final E element = this.get( index );
      if ( element == o || ( element != null && element.equals( o ) ) )
      {
        retval = index;
        break;
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public int lastIndexOf( Object o )
  {
    //
    int retval = -1;
    
    //
    for ( int index = this.size() - 1; index >= 0; index-- )
    {
      final E element = this.get( index );
      if ( element == o || ( element != null && element.equals( o ) ) )
      {
        retval = index;
        break;
      }
    }
    
    //
    return retval;
  }
  
}

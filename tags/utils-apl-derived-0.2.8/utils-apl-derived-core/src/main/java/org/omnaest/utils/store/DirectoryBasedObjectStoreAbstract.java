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
package org.omnaest.utils.store;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.omnaest.utils.events.exception.ExceptionHandlerSerializable;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverter;

/**
 * @author Omnaest
 * @param <E>
 */
abstract class DirectoryBasedObjectStoreAbstract<E> implements List<E>, Serializable, DirectoryBasedObjectStore<E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = 3437464263177930309L;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  protected final List<E>   elementList;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @param elementBidirectionalConverter
   * @param baseDirectory
   * @param exceptionHandler
   * @see DirectoryBasedObjectStoreAbstract
   */
  DirectoryBasedObjectStoreAbstract( ElementBidirectionalConverter<ByteArrayContainer, E> elementBidirectionalConverter,
                                     File baseDirectory, ExceptionHandlerSerializable exceptionHandler )
  {
    super();
    
    this.elementList = ListUtils.adapter( new NestedDirectoryToByteArrayContainerListAdapter( baseDirectory, exceptionHandler ),
                                          elementBidirectionalConverter );
  }
  
  @Override
  public boolean add( E e )
  {
    return this.elementList.add( e );
  }
  
  @Override
  public void add( int index, E element )
  {
    this.elementList.add( index, element );
  }
  
  @Override
  public boolean addAll( Collection<? extends E> c )
  {
    return this.elementList.addAll( c );
  }
  
  @Override
  public boolean addAll( int index, Collection<? extends E> c )
  {
    return this.elementList.addAll( index, c );
  }
  
  @Override
  public void clear()
  {
    this.elementList.clear();
  }
  
  @Override
  public boolean contains( Object o )
  {
    return this.elementList.contains( o );
  }
  
  @Override
  public boolean containsAll( Collection<?> c )
  {
    return this.elementList.containsAll( c );
  }
  
  @Override
  public E get( int index )
  {
    return this.elementList.get( index );
  }
  
  @Override
  public int indexOf( Object o )
  {
    return this.elementList.indexOf( o );
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.elementList.isEmpty();
  }
  
  @Override
  public Iterator<E> iterator()
  {
    return this.elementList.iterator();
  }
  
  @Override
  public int lastIndexOf( Object o )
  {
    return this.elementList.lastIndexOf( o );
  }
  
  @Override
  public ListIterator<E> listIterator()
  {
    return this.elementList.listIterator();
  }
  
  @Override
  public ListIterator<E> listIterator( int index )
  {
    return this.elementList.listIterator( index );
  }
  
  @Override
  public E remove( int index )
  {
    return this.elementList.remove( index );
  }
  
  @Override
  public boolean remove( Object o )
  {
    return this.elementList.remove( o );
  }
  
  @Override
  public boolean removeAll( Collection<?> c )
  {
    return this.elementList.removeAll( c );
  }
  
  @Override
  public boolean retainAll( Collection<?> c )
  {
    return this.elementList.retainAll( c );
  }
  
  @Override
  public E set( int index, E element )
  {
    return this.elementList.set( index, element );
  }
  
  @Override
  public int size()
  {
    return this.elementList.size();
  }
  
  @Override
  public List<E> subList( int fromIndex, int toIndex )
  {
    return this.elementList.subList( fromIndex, toIndex );
  }
  
  @Override
  public Object[] toArray()
  {
    return this.elementList.toArray();
  }
  
  @Override
  public <T> T[] toArray( T[] a )
  {
    return this.elementList.toArray( a );
  }
  
  @Override
  public boolean equals( Object o )
  {
    return this.elementList.equals( o );
  }
  
  @Override
  public int hashCode()
  {
    return this.elementList.hashCode();
  }
  
}

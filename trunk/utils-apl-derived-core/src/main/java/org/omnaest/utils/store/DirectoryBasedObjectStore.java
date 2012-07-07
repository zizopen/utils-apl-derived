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
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverterSerializable;

/**
 * A simple {@link Object} store based on a nested directory structure using Java serialization. All elements have to subclass the
 * {@link Serializable} interface.
 * 
 * @author Omnaest
 * @param <E>
 */
public class DirectoryBasedObjectStore<E extends Serializable> implements List<E>, Serializable
{
  private static final long serialVersionUID = 6969251214756015290L;
  private final List<E>     elementList;
  
  /**
   * @see DirectoryBasedObjectStore
   * @param baseDirectory
   *          {@link File}
   * @param exceptionHandler
   *          {@link ExceptionHandlerSerializable}
   */
  public DirectoryBasedObjectStore( File baseDirectory, ExceptionHandlerSerializable exceptionHandler )
  {
    super();
    final List<ByteArrayContainer> byteArrayContainerList = new NestedDirectoryToByteArrayContainerListAdapter( baseDirectory,
                                                                                                                exceptionHandler );
    final ElementBidirectionalConverter<ByteArrayContainer, E> elementBidirectionalConverter = new ElementBidirectionalConverterSerializable<ByteArrayContainer, E>()
    {
      private static final long serialVersionUID = -7311719190343731231L;
      
      @Override
      public E convert( ByteArrayContainer byteArrayContainer )
      {
        return byteArrayContainer.toDeserializedElement();
      }
      
      @Override
      public ByteArrayContainer convertBackwards( E element )
      {
        return new ByteArrayContainer().copyFromSerialized( element );
      }
    };
    this.elementList = ListUtils.adapter( byteArrayContainerList, elementBidirectionalConverter );
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
  public boolean equals( Object o )
  {
    return this.elementList.equals( o );
  }
  
  @Override
  public E get( int index )
  {
    return this.elementList.get( index );
  }
  
  @Override
  public int hashCode()
  {
    return this.elementList.hashCode();
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
  
}

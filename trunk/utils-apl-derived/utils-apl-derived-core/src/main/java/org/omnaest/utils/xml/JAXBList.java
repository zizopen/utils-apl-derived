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
package org.omnaest.utils.xml;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * {@link List} wrapper which acts as an {@link XmlRootElement} for any {@link List}. Since the exact type of the internal
 * {@link List} instance is determined at runtime, each of the objects have its own schema definition. This will cause some
 * overhead in comparison to a {@link List} which is wrapped not by its interface.
 * 
 * @see #newInstance(List)
 * @author Omnaest
 * @param <E>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JAXBList<E> implements List<E>
{
  /* ********************************************** Variables ********************************************** */
  @XmlElement
  protected List<E> list = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Creates a new instance of a {@link JAXBList} for a given {@link List}.
   * 
   * @param <E>
   * @param list
   * @return new instance or null if list param is null
   */
  public static <E> JAXBList<E> newInstance( List<E> list )
  {
    //
    JAXBList<E> result = null;
    
    //
    if ( list != null )
    {
      result = new JAXBList<E>( list );
    }
    
    //
    return result;
  }
  
  /**
   * @see #newInstance(Collection)
   * @param collection
   */
  protected JAXBList( List<E> collection )
  {
    super();
    this.list = collection;
  }
  
  /**
   * Used internally when JAXB does create a new default instance.
   */
  protected JAXBList()
  {
    super();
  }
  
  @Override
  public boolean add( E arg0 )
  {
    return this.list.add( arg0 );
  }
  
  @Override
  public boolean addAll( Collection<? extends E> arg0 )
  {
    return this.list.addAll( arg0 );
  }
  
  @Override
  public void clear()
  {
    this.list.clear();
  }
  
  @Override
  public boolean contains( Object arg0 )
  {
    return this.list.contains( arg0 );
  }
  
  @Override
  public boolean containsAll( Collection<?> arg0 )
  {
    return this.list.containsAll( arg0 );
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.list.isEmpty();
  }
  
  @Override
  public Iterator<E> iterator()
  {
    return this.list.iterator();
  }
  
  @Override
  public boolean remove( Object arg0 )
  {
    return this.list.remove( arg0 );
  }
  
  @Override
  public boolean removeAll( Collection<?> arg0 )
  {
    return this.list.removeAll( arg0 );
  }
  
  @Override
  public boolean retainAll( Collection<?> arg0 )
  {
    return this.list.retainAll( arg0 );
  }
  
  @Override
  public int size()
  {
    return this.list.size();
  }
  
  @Override
  public Object[] toArray()
  {
    return this.list.toArray();
  }
  
  @Override
  public <T> T[] toArray( T[] arg0 )
  {
    return this.list.toArray( arg0 );
  }
  
  @Override
  public void add( int index, E element )
  {
    this.list.add( index, element );
  }
  
  @Override
  public boolean addAll( int index, Collection<? extends E> c )
  {
    return this.list.addAll( index, c );
  }
  
  @Override
  public E get( int index )
  {
    return this.list.get( index );
  }
  
  @Override
  public int indexOf( Object o )
  {
    return this.list.indexOf( o );
  }
  
  @Override
  public int lastIndexOf( Object o )
  {
    return this.list.lastIndexOf( o );
  }
  
  @Override
  public ListIterator<E> listIterator()
  {
    return this.list.listIterator();
  }
  
  @Override
  public ListIterator<E> listIterator( int index )
  {
    return this.list.listIterator( index );
  }
  
  @Override
  public E remove( int index )
  {
    return this.list.remove( index );
  }
  
  @Override
  public E set( int index, E element )
  {
    return this.list.set( index, element );
  }
  
  @Override
  public List<E> subList( int fromIndex, int toIndex )
  {
    return this.list.subList( fromIndex, toIndex );
  }
  
}

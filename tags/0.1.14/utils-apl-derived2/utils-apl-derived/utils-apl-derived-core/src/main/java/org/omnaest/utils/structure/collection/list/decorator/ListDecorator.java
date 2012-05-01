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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The {@link ListDecorator} is intended to decorate an existing {@link List} instance. It can also be used to intercept only
 * partial invocation by overriding the respective methods.
 * 
 * @author Omnaest
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ListDecorator<E> implements List<E>, Serializable
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 2497897937009557014L;
  /* ********************************************** Variables ********************************************** */
  @XmlElement
  protected List<E>         list             = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see ListDecorator
   * @param list
   */
  public ListDecorator( List<E> list )
  {
    super();
    this.list = list;
  }
  
  /**
   * @see ListDecorator
   */
  protected ListDecorator()
  {
    super();
  }
  
  /**
   * @return
   * @see java.util.List#size()
   */
  public int size()
  {
    return this.list.size();
  }
  
  /**
   * @return
   * @see java.util.List#isEmpty()
   */
  public boolean isEmpty()
  {
    return this.list.isEmpty();
  }
  
  /**
   * @param o
   * @return
   * @see java.util.List#contains(java.lang.Object)
   */
  public boolean contains( Object o )
  {
    return this.list.contains( o );
  }
  
  /**
   * @return
   * @see java.util.List#iterator()
   */
  public Iterator<E> iterator()
  {
    return this.list.iterator();
  }
  
  /**
   * @return
   * @see java.util.List#toArray()
   */
  public Object[] toArray()
  {
    return this.list.toArray();
  }
  
  /**
   * @param a
   * @return
   * @see java.util.List#toArray(T[])
   */
  public <T> T[] toArray( T[] a )
  {
    return this.list.toArray( a );
  }
  
  /**
   * @param e
   * @return
   * @see java.util.List#add(java.lang.Object)
   */
  public boolean add( E e )
  {
    return this.list.add( e );
  }
  
  /**
   * @param o
   * @return
   * @see java.util.List#remove(java.lang.Object)
   */
  public boolean remove( Object o )
  {
    return this.list.remove( o );
  }
  
  /**
   * @param c
   * @return
   * @see java.util.List#containsAll(java.util.Collection)
   */
  public boolean containsAll( Collection<?> c )
  {
    return this.list.containsAll( c );
  }
  
  /**
   * @param c
   * @return
   * @see java.util.List#addAll(java.util.Collection)
   */
  public boolean addAll( Collection<? extends E> c )
  {
    return this.list.addAll( c );
  }
  
  /**
   * @param index
   * @param c
   * @return
   * @see java.util.List#addAll(int, java.util.Collection)
   */
  public boolean addAll( int index, Collection<? extends E> c )
  {
    return this.list.addAll( index, c );
  }
  
  /**
   * @param c
   * @return
   * @see java.util.List#removeAll(java.util.Collection)
   */
  public boolean removeAll( Collection<?> c )
  {
    return this.list.removeAll( c );
  }
  
  /**
   * @param c
   * @return
   * @see java.util.List#retainAll(java.util.Collection)
   */
  public boolean retainAll( Collection<?> c )
  {
    return this.list.retainAll( c );
  }
  
  /**
   * @see java.util.List#clear()
   */
  public void clear()
  {
    this.list.clear();
  }
  
  /**
   * @param o
   * @return
   * @see java.util.List#equals(java.lang.Object)
   */
  public boolean equals( Object o )
  {
    return this.list.equals( o );
  }
  
  /**
   * @return
   * @see java.util.List#hashCode()
   */
  public int hashCode()
  {
    return this.list.hashCode();
  }
  
  /**
   * @param index
   * @return
   * @see java.util.List#get(int)
   */
  public E get( int index )
  {
    return this.list.get( index );
  }
  
  /**
   * @param index
   * @param element
   * @return
   * @see java.util.List#set(int, java.lang.Object)
   */
  public E set( int index, E element )
  {
    return this.list.set( index, element );
  }
  
  /**
   * @param index
   * @param element
   * @see java.util.List#add(int, java.lang.Object)
   */
  public void add( int index, E element )
  {
    this.list.add( index, element );
  }
  
  /**
   * @param index
   * @return
   * @see java.util.List#remove(int)
   */
  public E remove( int index )
  {
    return this.list.remove( index );
  }
  
  /**
   * @param o
   * @return
   * @see java.util.List#indexOf(java.lang.Object)
   */
  public int indexOf( Object o )
  {
    return this.list.indexOf( o );
  }
  
  /**
   * @param o
   * @return
   * @see java.util.List#lastIndexOf(java.lang.Object)
   */
  public int lastIndexOf( Object o )
  {
    return this.list.lastIndexOf( o );
  }
  
  /**
   * @return
   * @see java.util.List#listIterator()
   */
  public ListIterator<E> listIterator()
  {
    return this.list.listIterator();
  }
  
  /**
   * @param index
   * @return
   * @see java.util.List#listIterator(int)
   */
  public ListIterator<E> listIterator( int index )
  {
    return this.list.listIterator( index );
  }
  
  /**
   * @param fromIndex
   * @param toIndex
   * @return
   * @see java.util.List#subList(int, int)
   */
  public List<E> subList( int fromIndex, int toIndex )
  {
    return this.list.subList( fromIndex, toIndex );
  }
  
  /**
   * @return the list
   */
  protected List<E> getList()
  {
    return this.list;
  }
  
  /**
   * @param list
   *          the list to set
   */
  protected void setList( List<E> list )
  {
    this.list = list;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( this.list );
    return builder.toString();
  }
  
}

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
package org.omnaest.utils.structure.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The {@link CollectionDecorator} is intended to decorate an existing {@link Collection} instance. If it is subclassed method
 * calls to the decorator can be intercepted before they are invoked for the underlying {@link Collection}.
 * 
 * @author Omnaest
 * @param <E>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionDecorator<E> implements Collection<E>, Serializable
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -6479331644328513970L;
  /* ********************************************** Variables ********************************************** */
  @XmlElement
  protected Collection<E>   collection       = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see CollectionDecorator
   * @param collection
   */
  public CollectionDecorator( Collection<E> collection )
  {
    super();
    this.collection = collection;
  }
  
  /**
   * @return
   * @see java.util.Collection#size()
   */
  public int size()
  {
    return this.collection.size();
  }
  
  /**
   * @return
   * @see java.util.Collection#isEmpty()
   */
  public boolean isEmpty()
  {
    return this.collection.isEmpty();
  }
  
  /**
   * @param o
   * @return
   * @see java.util.Collection#contains(java.lang.Object)
   */
  public boolean contains( Object o )
  {
    return this.collection.contains( o );
  }
  
  /**
   * @return
   * @see java.util.Collection#iterator()
   */
  public Iterator<E> iterator()
  {
    return this.collection.iterator();
  }
  
  /**
   * @return
   * @see java.util.Collection#toArray()
   */
  public Object[] toArray()
  {
    return this.collection.toArray();
  }
  
  /**
   * @param a
   * @return
   * @see java.util.Collection#toArray(T[])
   */
  public <T> T[] toArray( T[] a )
  {
    return this.collection.toArray( a );
  }
  
  /**
   * @param e
   * @return
   * @see java.util.Collection#add(java.lang.Object)
   */
  public boolean add( E e )
  {
    return this.collection.add( e );
  }
  
  /**
   * @param o
   * @return
   * @see java.util.Collection#remove(java.lang.Object)
   */
  public boolean remove( Object o )
  {
    return this.collection.remove( o );
  }
  
  /**
   * @param c
   * @return
   * @see java.util.Collection#containsAll(java.util.Collection)
   */
  public boolean containsAll( Collection<?> c )
  {
    return this.collection.containsAll( c );
  }
  
  /**
   * @param c
   * @return
   * @see java.util.Collection#addAll(java.util.Collection)
   */
  public boolean addAll( Collection<? extends E> c )
  {
    return this.collection.addAll( c );
  }
  
  /**
   * @param c
   * @return
   * @see java.util.Collection#removeAll(java.util.Collection)
   */
  public boolean removeAll( Collection<?> c )
  {
    return this.collection.removeAll( c );
  }
  
  /**
   * @param c
   * @return
   * @see java.util.Collection#retainAll(java.util.Collection)
   */
  public boolean retainAll( Collection<?> c )
  {
    return this.collection.retainAll( c );
  }
  
  /**
   * @see java.util.Collection#clear()
   */
  public void clear()
  {
    this.collection.clear();
  }
  
  /**
   * @param o
   * @return
   * @see java.util.Collection#equals(java.lang.Object)
   */
  public boolean equals( Object o )
  {
    return this.collection.equals( o );
  }
  
  /**
   * @return
   * @see java.util.Collection#hashCode()
   */
  public int hashCode()
  {
    return this.collection.hashCode();
  }
  
  /**
   * @return the collection
   */
  protected Collection<E> getCollection()
  {
    return this.collection;
  }
  
  /**
   * @param collection
   *          the collection to set
   */
  protected void setCollection( Collection<E> collection )
  {
    this.collection = collection;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( this.collection );
    return builder.toString();
  }
  
}

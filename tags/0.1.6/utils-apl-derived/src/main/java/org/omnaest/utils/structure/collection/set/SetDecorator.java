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
package org.omnaest.utils.structure.collection.set;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A {@link SetDecorator} decorates an existing {@link Set} instance. If it is subclassed method invocations can be intercepted by
 * overriding methods partially.
 * 
 * @author Omnaest
 * @param <E>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SetDecorator<E> implements Set<E>, Serializable
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -3453576867764866929L;
  /* ********************************************** Variables ********************************************** */
  @XmlElement
  protected Set<E>          set              = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see SetDecorator
   * @param set
   */
  public SetDecorator( Set<E> set )
  {
    super();
    this.set = set;
  }
  
  /**
   * @see SetDecorator
   */
  protected SetDecorator()
  {
    super();
  }
  
  /**
   * @return
   * @see java.util.Set#size()
   */
  public int size()
  {
    return this.set.size();
  }
  
  /**
   * @return
   * @see java.util.Set#isEmpty()
   */
  public boolean isEmpty()
  {
    return this.set.isEmpty();
  }
  
  /**
   * @param o
   * @return
   * @see java.util.Set#contains(java.lang.Object)
   */
  public boolean contains( Object o )
  {
    return this.set.contains( o );
  }
  
  /**
   * @return
   * @see java.util.Set#iterator()
   */
  public Iterator<E> iterator()
  {
    return this.set.iterator();
  }
  
  /**
   * @return
   * @see java.util.Set#toArray()
   */
  public Object[] toArray()
  {
    return this.set.toArray();
  }
  
  /**
   * @param a
   * @return
   * @see java.util.Set#toArray(T[])
   */
  public <T> T[] toArray( T[] a )
  {
    return this.set.toArray( a );
  }
  
  /**
   * @param e
   * @return
   * @see java.util.Set#add(java.lang.Object)
   */
  public boolean add( E e )
  {
    return this.set.add( e );
  }
  
  /**
   * @param o
   * @return
   * @see java.util.Set#remove(java.lang.Object)
   */
  public boolean remove( Object o )
  {
    return this.set.remove( o );
  }
  
  /**
   * @param c
   * @return
   * @see java.util.Set#containsAll(java.util.Collection)
   */
  public boolean containsAll( Collection<?> c )
  {
    return this.set.containsAll( c );
  }
  
  /**
   * @param c
   * @return
   * @see java.util.Set#addAll(java.util.Collection)
   */
  public boolean addAll( Collection<? extends E> c )
  {
    return this.set.addAll( c );
  }
  
  /**
   * @param c
   * @return
   * @see java.util.Set#retainAll(java.util.Collection)
   */
  public boolean retainAll( Collection<?> c )
  {
    return this.set.retainAll( c );
  }
  
  /**
   * @param c
   * @return
   * @see java.util.Set#removeAll(java.util.Collection)
   */
  public boolean removeAll( Collection<?> c )
  {
    return this.set.removeAll( c );
  }
  
  /**
   * @see java.util.Set#clear()
   */
  public void clear()
  {
    this.set.clear();
  }
  
  /**
   * @param o
   * @return
   * @see java.util.Set#equals(java.lang.Object)
   */
  public boolean equals( Object o )
  {
    return this.set.equals( o );
  }
  
  /**
   * @return
   * @see java.util.Set#hashCode()
   */
  public int hashCode()
  {
    return this.set.hashCode();
  }
  
  /**
   * @return the set
   */
  protected Set<E> getSet()
  {
    return this.set;
  }
  
  /**
   * @param set
   *          the set to set
   */
  protected void setSet( Set<E> set )
  {
    this.set = set;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( this.set );
    return builder.toString();
  }
  
}

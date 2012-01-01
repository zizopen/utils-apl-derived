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
import java.util.ListIterator;

/**
 * Decorator for a {@link ListIterator} instance. Overwrite methods in subclasses wherever needed.
 * 
 * @author Omnaest
 * @param <E>
 */
public class ListIteratorDecorator<E> implements ListIterator<E>, Serializable
{
  /* ********************************************** Constants ********************************************** */
  private static final long       serialVersionUID = 360102117020680103L;
  /* ********************************************** Variables ********************************************** */
  protected final ListIterator<E> listIterator;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see ListIteratorDecorator
   * @param listIterator
   */
  public ListIteratorDecorator( ListIterator<E> listIterator )
  {
    super();
    this.listIterator = listIterator;
  }
  
  /**
   * @return
   * @see java.util.ListIterator#hasNext()
   */
  public boolean hasNext()
  {
    return this.listIterator.hasNext();
  }
  
  /**
   * @return
   * @see java.util.ListIterator#next()
   */
  public E next()
  {
    return this.listIterator.next();
  }
  
  /**
   * @return
   * @see java.util.ListIterator#hasPrevious()
   */
  public boolean hasPrevious()
  {
    return this.listIterator.hasPrevious();
  }
  
  /**
   * @return
   * @see java.util.ListIterator#previous()
   */
  public E previous()
  {
    return this.listIterator.previous();
  }
  
  /**
   * @return
   * @see java.util.ListIterator#nextIndex()
   */
  public int nextIndex()
  {
    return this.listIterator.nextIndex();
  }
  
  /**
   * @return
   * @see java.util.ListIterator#previousIndex()
   */
  public int previousIndex()
  {
    return this.listIterator.previousIndex();
  }
  
  /**
   * @see java.util.ListIterator#remove()
   */
  public void remove()
  {
    this.listIterator.remove();
  }
  
  /**
   * @param e
   * @see java.util.ListIterator#set(java.lang.Object)
   */
  public void set( E e )
  {
    this.listIterator.set( e );
  }
  
  /**
   * @param e
   * @see java.util.ListIterator#add(java.lang.Object)
   */
  public void add( E e )
  {
    this.listIterator.add( e );
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.listIterator == null ) ? 0 : this.listIterator.hashCode() );
    return result;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( obj == null )
    {
      return false;
    }
    if ( !( obj instanceof ListIterator ) )
    {
      return false;
    }
    ListIterator<?> other = (ListIterator<?>) obj;
    if ( this.listIterator == null )
    {
      return false;
    }
    else if ( !this.listIterator.equals( other ) )
    {
      return false;
    }
    return true;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( this.listIterator );
    return builder.toString();
  }
  
}

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
package org.omnaest.utils.beans.autowired;

import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Decorator for an {@link AutowiredContainer}
 * 
 * @author Omnaest
 * @param <E>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AutowiredContainerDecorator<E> implements AutowiredContainer<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long       serialVersionUID   = 3043215321074462819L;
  /* ********************************************** Variables ********************************************** */
  @XmlElement(type = ClassMapToAutowiredContainerAdapter.class)
  protected AutowiredContainer<E> autowiredContainer = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see AutowiredContainerDecorator
   * @param autowiredContainer
   */
  public AutowiredContainerDecorator( AutowiredContainer<E> autowiredContainer )
  {
    super();
    this.autowiredContainer = autowiredContainer;
  }
  
  /**
   * @see AutowiredContainerDecorator
   */
  protected AutowiredContainerDecorator()
  {
    super();
  }
  
  /**
   * @return
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<E> iterator()
  {
    return this.autowiredContainer.iterator();
  }
  
  /**
   * @param type
   * @return
   * @see org.omnaest.utils.beans.autowired.AutowiredContainer#getValue(java.lang.Class)
   */
  @Override
  public <O extends E> O getValue( Class<? extends O> type )
  {
    return this.autowiredContainer.getValue( type );
  }
  
  /**
   * @param type
   * @return
   * @see org.omnaest.utils.beans.autowired.AutowiredContainer#getValueSet(java.lang.Class)
   */
  @Override
  public <O extends E> Set<O> getValueSet( Class<? extends O> type )
  {
    return this.autowiredContainer.getValueSet( type );
  }
  
  /**
   * @param type
   * @return
   * @see org.omnaest.utils.beans.autowired.AutowiredContainer#containsAssignable(java.lang.Class)
   */
  @Override
  public <O extends E> boolean containsAssignable( Class<O> type )
  {
    return this.autowiredContainer.containsAssignable( type );
  }
  
  /**
   * @return
   * @see org.omnaest.utils.beans.autowired.AutowiredContainer#isEmpty()
   */
  @Override
  public boolean isEmpty()
  {
    return this.autowiredContainer.isEmpty();
  }
  
  /**
   * @param object
   * @return
   * @see org.omnaest.utils.beans.autowired.AutowiredContainer#put(java.lang.Object)
   */
  @Override
  public AutowiredContainer<E> put( E object )
  {
    return this.autowiredContainer.put( object );
  }
  
  /**
   * @param iterable
   * @return
   * @see org.omnaest.utils.beans.autowired.AutowiredContainer#putAll(java.lang.Iterable)
   */
  @Override
  public AutowiredContainer<E> putAll( Iterable<E> iterable )
  {
    return this.autowiredContainer.putAll( iterable );
  }
  
  /**
   * @param object
   * @param types
   * @return
   * @see org.omnaest.utils.beans.autowired.AutowiredContainer#put(java.lang.Object, java.lang.Class<? extends O>[])
   */
  @Override
  public <O extends E> AutowiredContainer<E> put( O object, Class<? extends O>... types )
  {
    return this.autowiredContainer.put( object, types );
  }
  
  /**
   * @param object
   * @return
   * @see org.omnaest.utils.beans.autowired.AutowiredContainer#remove(java.lang.Object)
   */
  @Override
  public <O extends E> AutowiredContainer<E> remove( O object )
  {
    return this.autowiredContainer.remove( object );
  }
  
  /**
   * @param type
   * @return
   * @see org.omnaest.utils.beans.autowired.AutowiredContainer#removeAllHavingExactTypeOf(java.lang.Class)
   */
  @Override
  public AutowiredContainer<E> removeAllHavingExactTypeOf( Class<? extends E> type )
  {
    return this.autowiredContainer.removeAllHavingExactTypeOf( type );
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( this.autowiredContainer );
    return builder.toString();
  }
  
  /**
   * @return the autowiredContainer
   */
  protected AutowiredContainer<E> getAutowiredContainer()
  {
    return this.autowiredContainer;
  }
  
  /**
   * @param autowiredContainer
   *          the autowiredContainer to set
   */
  protected void setAutowiredContainer( AutowiredContainer<E> autowiredContainer )
  {
    this.autowiredContainer = autowiredContainer;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.autowiredContainer == null ) ? 0 : this.autowiredContainer.hashCode() );
    return result;
  }
  
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
    if ( !( obj instanceof AutowiredContainer<?> ) )
    {
      return false;
    }
    AutowiredContainer<?> other = (AutowiredContainer<?>) obj;
    if ( this.autowiredContainer == null )
    {
      return false;
    }
    else if ( !this.autowiredContainer.equals( other ) )
    {
      return false;
    }
    return true;
  }
  
  /**
   * @return
   * @see org.omnaest.utils.beans.autowired.AutowiredContainer#size()
   */
  @Override
  public int size()
  {
    return this.autowiredContainer.size();
  }
  
  @Override
  public AutowiredContainer<E> removeAllAssignableTo( Class<? extends E> type )
  {
    return this.autowiredContainer.removeAllAssignableTo( type );
  }
  
}

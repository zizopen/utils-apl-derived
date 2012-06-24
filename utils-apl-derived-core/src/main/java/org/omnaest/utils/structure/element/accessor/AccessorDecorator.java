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
package org.omnaest.utils.structure.element.accessor;

import org.omnaest.utils.structure.element.ElementHolder;

/**
 * Decorator for any {@link Accessor} instance
 * 
 * @author Omnaest
 * @param <E>
 */
public class AccessorDecorator<E> implements Accessor<E>
{
  /* ********************************************** Beans / Services / References ********************************************** */
  protected Accessor<E> accessor = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see AccessorDecorator
   * @param accessor
   */
  public AccessorDecorator( Accessor<E> accessor )
  {
    super();
    this.setAccessor( accessor );
  }
  
  /**
   * @see AccessorDecorator
   * @param accessorReadable
   */
  public AccessorDecorator( AccessorReadable<E> accessorReadable )
  {
    super();
    this.setAccessor( accessorReadable );
  }
  
  /**
   * @see AccessorDecorator
   * @param accessorWritable
   */
  public AccessorDecorator( AccessorWritable<E> accessorWritable )
  {
    super();
    this.setAccessor( accessorWritable );
  }
  
  /**
   * Uses an {@link ElementHolder} to wrap a given element
   * 
   * @see AccessorDecorator
   * @param element
   */
  public AccessorDecorator( E element )
  {
    super();
    this.accessor = new ElementHolder<E>( element );
  }
  
  @Override
  public E getElement()
  {
    return this.accessor != null ? this.accessor.getElement() : null;
  }
  
  @Override
  public Accessor<E> setElement( E element )
  {
    //
    if ( this.accessor != null )
    {
      this.accessor.setElement( element );
    }
    
    //
    return this;
  }
  
  /**
   * @param accessor
   *          the accessor to set
   */
  public void setAccessor( Accessor<E> accessor )
  {
    this.accessor = accessor;
  }
  
  /**
   * @param accessorReadable
   */
  public void setAccessor( final AccessorReadable<E> accessorReadable )
  {
    this.accessor = accessorReadable == null ? null : new Accessor<E>()
    {
      @Override
      public E getElement()
      {
        return accessorReadable.getElement();
      }
      
      @Override
      public Accessor<E> setElement( E element )
      {
        throw new UnsupportedOperationException();
      }
    };
  }
  
  /**
   * @param accessorWritable
   */
  public void setAccessor( final AccessorWritable<E> accessorWritable )
  {
    this.accessor = accessorWritable == null ? null : new Accessor<E>()
    {
      @Override
      public E getElement()
      {
        throw new UnsupportedOperationException();
      }
      
      @Override
      public Accessor<E> setElement( E element )
      {
        accessorWritable.setElement( element );
        return this;
      }
    };
  }
  
  /**
   * @return the accessor
   */
  public Accessor<E> getAccessor()
  {
    return this.accessor;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "AccessorDecorator [accessor=" );
    builder.append( this.accessor );
    builder.append( "]" );
    return builder.toString();
  }
  
}

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
package org.omnaest.utils.threads;

import java.io.Serializable;
import java.util.concurrent.Callable;

import org.omnaest.utils.assertion.Assert;

/**
 * Decorator for a {@link Callable}. Please override the {@link #call()} method if necessary.
 * 
 * @author Omnaest
 * @param <V>
 */
public abstract class CallableDecorator<V> implements Callable<V>, Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long   serialVersionUID = 6691986461223970211L;
  /* ********************************************** Variables ********************************************** */
  protected final Callable<V> callable;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see CallableDecorator
   * @param callable
   */
  protected CallableDecorator( Callable<V> callable )
  {
    super();
    this.callable = callable;
    Assert.isNotNull( callable, "callable must not be null to be used by the CallableDecorator" );
  }
  
  @Override
  public V call() throws Exception
  {
    return this.callable.call();
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "CallableDecorator [callable=" );
    builder.append( this.callable );
    builder.append( "]" );
    return builder.toString();
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.callable == null ) ? 0 : this.callable.hashCode() );
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
    if ( !( obj instanceof CallableDecorator ) )
    {
      return false;
    }
    @SuppressWarnings("rawtypes")
    CallableDecorator other = (CallableDecorator) obj;
    if ( this.callable == null )
    {
      if ( other.callable != null )
      {
        return false;
      }
    }
    else if ( !this.callable.equals( other.callable ) )
    {
      return false;
    }
    return true;
  }
  
}

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

import org.omnaest.utils.assertion.Assert;

/**
 * Decorator for a {@link Runnable}. Please override the {@link #run()} method if necessary.
 * 
 * @author Omnaest
 */
public abstract class RunnableDecorator implements Runnable
{
  /* ********************************************** Variables ********************************************** */
  protected final Runnable runnable;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see RunnableDecorator
   * @param runnable
   */
  protected RunnableDecorator( Runnable runnable )
  {
    super();
    this.runnable = runnable;
    Assert.isNotNull( runnable, "runnable must not be null to be used by the RunnableDecorator" );
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "RunnableDecorator [runnable=" );
    builder.append( this.runnable );
    builder.append( "]" );
    return builder.toString();
  }
  
  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run()
  {
    this.runnable.run();
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.runnable == null ) ? 0 : this.runnable.hashCode() );
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
    if ( !( obj instanceof RunnableDecorator ) )
    {
      return false;
    }
    RunnableDecorator other = (RunnableDecorator) obj;
    if ( this.runnable == null )
    {
      if ( other.runnable != null )
      {
        return false;
      }
    }
    else if ( !this.runnable.equals( other.runnable ) )
    {
      return false;
    }
    return true;
  }
  
}

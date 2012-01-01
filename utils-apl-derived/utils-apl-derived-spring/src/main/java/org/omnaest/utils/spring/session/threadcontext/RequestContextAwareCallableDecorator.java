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
package org.omnaest.utils.spring.session.threadcontext;

import java.util.concurrent.Callable;

import org.omnaest.utils.spring.session.threadcontext.internal.RequestContextManagerForCallableAndRunnableDecorator;
import org.omnaest.utils.threads.CallableDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Special {@link CallableDecorator} which will use the {@link RequestContextHolder} to set the request attributes from the
 * current thread during the run period of the enclosed {@link Callable}.
 * 
 * @see RequestContextAwareRunnableDecorator
 * @author Omnaest
 * @param <V>
 */
public class RequestContextAwareCallableDecorator<V> extends CallableDecorator<V>
{
  /* ********************************************** Variables ********************************************** */
  private final RequestContextManagerForCallableAndRunnableDecorator requestContextManagerForCallableAndRunnableDecorator;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @see RequestContextAwareCallableDecorator
   * @param callable
   * @param inheritable
   *          {@link RequestContextHolder#setRequestAttributes(RequestAttributes, boolean)}
   */
  public RequestContextAwareCallableDecorator( Callable<V> callable, boolean inheritable )
  {
    super( callable );
    this.requestContextManagerForCallableAndRunnableDecorator = new RequestContextManagerForCallableAndRunnableDecorator(
                                                                                                                          inheritable );
  }
  
  /**
   * @see RequestContextAwareCallableDecorator
   * @param callable
   */
  public RequestContextAwareCallableDecorator( Callable<V> callable )
  {
    this( callable, false );
  }
  
  /* (non-Javadoc)
   * @see org.omnaest.utils.threads.CallableDecorator#call()
   */
  @Override
  public V call() throws Exception
  {
    //
    V retval = null;
    
    //
    final RequestAttributes requestAttributesBackup = this.requestContextManagerForCallableAndRunnableDecorator.setRequestAttributesResolvedAtCreationTime();
    try
    {
      retval = super.call();
    }
    finally
    {
      this.requestContextManagerForCallableAndRunnableDecorator.setRequestAttributes( requestAttributesBackup );
    }
    
    //
    return retval;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "RequestContextAwareCallableDecorator [requestContextManagerForCallableAndRunnableDecorator=" );
    builder.append( this.requestContextManagerForCallableAndRunnableDecorator );
    builder.append( ", callable=" );
    builder.append( this.callable );
    builder.append( "]" );
    return builder.toString();
  }
  
}

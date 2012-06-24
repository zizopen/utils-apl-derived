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
import org.omnaest.utils.threads.RunnableDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Special {@link RunnableDecorator} which will use the {@link RequestContextHolder} to set the request attributes from the
 * current thread during the run period of the enclosed {@link Callable}.
 * 
 * @see RequestContextAwareCallableDecorator
 * @author Omnaest
 */
public class RequestContextAwareRunnableDecorator extends RunnableDecorator
{
  /* ************************************************** Constants *************************************************** */
  private static final long                                          serialVersionUID = -875522683350855050L;
  /* ********************************************** Variables ********************************************** */
  private final RequestContextManagerForCallableAndRunnableDecorator requestContextManagerForCallableAndRunnableDecorator;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @see RequestContextAwareRunnableDecorator
   * @param runnable
   * @param inheritable
   *          {@link RequestContextHolder#setRequestAttributes(RequestAttributes, boolean)}
   */
  public RequestContextAwareRunnableDecorator( Runnable runnable, boolean inheritable )
  {
    super( runnable );
    this.requestContextManagerForCallableAndRunnableDecorator = new RequestContextManagerForCallableAndRunnableDecorator(
                                                                                                                          inheritable );
  }
  
  /**
   * Similar to {@link #RequestContextAwareRunnableDecorator(Runnable, boolean)} with inheritable flag set to false.
   * 
   * @see RequestContextAwareRunnableDecorator
   * @param runnable
   */
  public RequestContextAwareRunnableDecorator( Runnable runnable )
  {
    this( runnable, false );
  }
  
  @Override
  public void run()
  {
    //
    final RequestAttributes requestAttributesBackup = this.requestContextManagerForCallableAndRunnableDecorator.setRequestAttributesResolvedAtCreationTime();
    try
    {
      super.run();
    }
    finally
    {
      this.requestContextManagerForCallableAndRunnableDecorator.setRequestAttributes( requestAttributesBackup );
    }
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "RequestContextAwareRunnableDecorator [requestContextManagerForCallableAndRunnableDecorator=" );
    builder.append( this.requestContextManagerForCallableAndRunnableDecorator );
    builder.append( ", runnable=" );
    builder.append( this.runnable );
    builder.append( "]" );
    return builder.toString();
  }
  
}

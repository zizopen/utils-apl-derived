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
package org.omnaest.utils.dispatcher;

import java.lang.reflect.Method;
import java.util.List;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerIgnoring;
import org.omnaest.utils.proxy.StubCreator;
import org.omnaest.utils.proxy.handler.MethodCallCapture;
import org.omnaest.utils.proxy.handler.MethodInvocationHandler;
import org.omnaest.utils.structure.element.ObjectUtils;

/**
 * A {@link ProxyDispatcherFactory} generates a proxy which will dispatch to a given {@link List} of instances implementing the
 * same shared type
 * 
 * @author Omnaest
 * @param <T>
 */
public class ProxyDispatcherFactory<T>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final StubCreator<T> stubCreator;
  
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  private ExceptionHandler     exceptionHandler = new ExceptionHandlerIgnoring();
  
  /* *************************************************** Methods **************************************************** */
  
  public ProxyDispatcherFactory( Class<? extends T> type )
  {
    super();
    this.stubCreator = new StubCreator<T>( type );
    
  }
  
  public T newDispatcher( final List<? extends T> instanceList )
  {
    MethodInvocationHandler methodInvocationHandler = new MethodInvocationHandler()
    {
      @Override
      public Object handle( MethodCallCapture methodCallCapture ) throws Throwable
      {
        for ( T instance : instanceList )
        {
          try
          {
            Method method = methodCallCapture.getMethod();
            method.invoke( instance, methodCallCapture.getArguments() );
          }
          catch ( Exception e )
          {
            ProxyDispatcherFactory.this.exceptionHandler.handleException( e );
          }
        }
        return null;
      }
    };
    
    return this.stubCreator.build( methodInvocationHandler );
  }
  
  /**
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   * @return this
   */
  public ProxyDispatcherFactory<T> setExceptionHandler( ExceptionHandler exceptionHandler )
  {
    this.exceptionHandler = ObjectUtils.defaultIfNull( exceptionHandler, new ExceptionHandlerIgnoring() );
    return this;
  }
}

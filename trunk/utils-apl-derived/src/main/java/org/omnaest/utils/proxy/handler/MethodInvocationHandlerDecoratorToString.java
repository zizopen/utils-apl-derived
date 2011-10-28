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
package org.omnaest.utils.proxy.handler;

/**
 * Decorator {@link MethodInvocationHandler} which captures a call for the {@link #toString()} method and forwards all other
 * invocations.
 * 
 * @author Omnaest
 */
public abstract class MethodInvocationHandlerDecoratorToString implements MethodInvocationHandler
{
  /* ********************************************** Variables ********************************************** */
  protected MethodInvocationHandler methodInvocationHandler = null;
  
  /* ********************************************** Methods ********************************************** */
  public MethodInvocationHandlerDecoratorToString( MethodInvocationHandler methodInvocationHandler )
  {
    super();
    this.methodInvocationHandler = methodInvocationHandler;
  }
  
  @Override
  public Object handle( MethodCallCapture methodCallCapture ) throws Throwable
  {
    //    
    Object retval = null;
    
    //
    String methodName = methodCallCapture.getMethodName();
    boolean hasArguments = methodCallCapture.hasArguments();
    if ( "toString".equals( methodName ) && !hasArguments )
    {
      retval = handleToString();
    }
    else if ( this.methodInvocationHandler != null )
    {
      retval = this.methodInvocationHandler.handle( methodCallCapture );
    }
    
    //
    return retval;
  }
  
  /**
   * Handles the invocation of the {@link #toString()} method
   * 
   * @return
   */
  public abstract String handleToString();
  
}

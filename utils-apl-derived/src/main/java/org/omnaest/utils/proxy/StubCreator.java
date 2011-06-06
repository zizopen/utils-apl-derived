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
package org.omnaest.utils.proxy;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Simple stub creator.
 * 
 * @author Omnaest
 */
public class StubCreator
{
  
  /* ********************************************** Classes/Interfaces ********************************************** */

  /**
   * Handles a method invocation.
   * 
   * @see #handle(MethodCallCapture)
   * @author Omnaest
   */
  public static interface MethodInvocationHandler
  {
    /**
     * Handles a method invocation.
     * 
     * @see MethodCallCapture
     * @param methodCallCapture
     * @return
     */
    public Object handle( MethodCallCapture methodCallCapture );
  }
  
  /* ********************************************** Methods ********************************************** */
  @SuppressWarnings("unchecked")
  public static <E> E newStubInstance( Class<? extends E> clazz, final MethodInvocationHandler methodInvocationHandler )
  {
    //
    E retval = null;
    
    //
    if ( clazz != null && methodInvocationHandler != null )
    {
      try
      {
        //      
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass( clazz );
        Callback callback = new MethodInterceptor()
        {
          @Override
          public Object intercept( Object obj, Method method, Object[] args, MethodProxy proxy ) throws Throwable
          {
            return methodInvocationHandler.handle( new MethodCallCapture( obj, method, args, proxy ) );
          }
        };
        enhancer.setCallback( callback );
        
        //
        retval = (E) enhancer.create();
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
}

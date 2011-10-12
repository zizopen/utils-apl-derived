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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
    public Object handle( MethodCallCapture methodCallCapture ) throws Throwable;
  }
  
  /* ********************************************** Methods ********************************************** */

  /**
   * Returns a new proxy stub for the given class or interface.
   * 
   * @param <E>
   * @param clazz
   * @param methodInvocationHandler
   * @return
   */
  public static <E> E newStubInstance( final Class<? extends E> clazz, final MethodInvocationHandler methodInvocationHandler )
  {
    return StubCreator.newStubInstance( clazz, null, methodInvocationHandler );
  }
  
  public static <E> E newStubInstance( final Class<? extends E> clazz, final MethodInterceptor methodInterceptor )
  {
    return StubCreator.newStubInstance( clazz, null, methodInterceptor );
  }
  
  /**
   * Same as {@link #newStubInstance(Class, Class[], MethodInterceptor)} but uses a {@link MethodInvocationHandler} instead.
   * 
   * @param <E>
   * @param clazz
   * @param interfaces
   * @param methodInvocationHandler
   * @return
   */
  public static <E> E newStubInstance( Class<? extends E> clazz,
                                       Class<?>[] interfaces,
                                       final MethodInvocationHandler methodInvocationHandler )
  {
    return StubCreator.newStubInstance( clazz, interfaces, new MethodInterceptor()
    {
      @Override
      public Object intercept( Object obj, Method method, Object[] args, MethodProxy proxy ) throws Throwable
      {
        return methodInvocationHandler.handle( new MethodCallCapture( obj, method, args, proxy ) );
      }
    } );
  }
  
  /**
   * Returns a new proxy stub for the given class or interface but takes additional interfaces.
   * 
   * @param <E>
   * @param clazz
   * @param interfaces
   * @param methodInterceptor
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <E> E newStubInstance( Class<? extends E> clazz, Class<?>[] interfaces, final MethodInterceptor methodInterceptor )
  {
    //
    E retval = null;
    
    //
    if ( clazz != null && methodInterceptor != null )
    {
      try
      {
        //
        Set<Class<?>> interfaceSet = new HashSet<Class<?>>();
        {
          if ( interfaces != null )
          {
            interfaceSet.addAll( Arrays.asList( interfaces ) );
          }
          if ( clazz.isInterface() )
          {
            interfaceSet.add( clazz );
          }
        }
        
        //      
        Enhancer enhancer = new Enhancer();
        if ( interfaceSet.size() > 0 )
        {
          enhancer.setInterfaces( interfaceSet.toArray( new Class[0] ) );
        }
        if ( !clazz.isInterface() )
        {
          enhancer.setSuperclass( clazz );
        }
        
        //
        enhancer.setCallback( methodInterceptor );
        
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

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

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerIgnoring;
import org.omnaest.utils.proxy.handler.MethodCallCapture;
import org.omnaest.utils.proxy.handler.MethodInvocationHandler;

/**
 * Helper to create stubs easily based on CGLIB.<br>
 * <br>
 * There are several static creation methods but the {@link StubCreator} can be instantiated also. If instantiated it will cache a
 * prototype of a generated stub for the given type and its interfaces and creates any new instance based on this prototype
 * instance which is much more faster.
 * 
 * @author Omnaest
 */
public class StubCreator<E>
{
  /* ********************************************** Variables ********************************************** */
  private final Factory factory;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @see StubCreator
   * @param type
   */
  public StubCreator( Class<? extends E> type )
  {
    this( type, (Class<?>[]) null );
  }
  
  /**
   * @see StubCreator
   * @param type
   * @param exceptionHandler
   */
  public StubCreator( Class<? extends E> type, ExceptionHandler exceptionHandler )
  {
    this( type, (Class<?>[]) null, exceptionHandler );
  }
  
  /**
   * @see StubCreator
   * @param type
   * @param interfaces
   */
  public StubCreator( Class<? extends E> type, Class<?>[] interfaces )
  {
    this( type, interfaces, new ExceptionHandlerIgnoring() );
  }
  
  /**
   * @see StubCreator
   * @param type
   * @param interfaces
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   */
  public StubCreator( Class<? extends E> type, Class<?>[] interfaces, ExceptionHandler exceptionHandler )
  {
    //
    super();
    
    //    
    MethodInvocationHandler methodInvocationHandler = null;
    this.factory = (Factory) newStubInstance( type, interfaces, methodInvocationHandler, exceptionHandler );
    Assert.isNotNull( this.factory, "Failed to create a stub factory" );
  }
  
  /**
   * @see StubCreator#newStubInstance(Class, Class[], MethodInvocationHandler)
   * @param methodInvocationHandler
   * @return
   */
  @SuppressWarnings("unchecked")
  public E build( final MethodInvocationHandler methodInvocationHandler )
  {
    //
    E retval = null;
    
    //
    final Callback callback = StubCreator.adapter( methodInvocationHandler );
    retval = (E) this.factory.newInstance( callback );
    
    //
    return retval;
  }
  
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
  
  /**
   * @param clazz
   * @param methodInterceptor
   * @return
   */
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
    return StubCreator.newStubInstance( clazz, interfaces, adapter( methodInvocationHandler ) );
  }
  
  /**
   * Same as {@link #newStubInstance(Class, Class[], MethodInterceptor)} but uses a {@link MethodInvocationHandler} instead.
   * 
   * @param <E>
   * @param clazz
   * @param interfaces
   * @param methodInvocationHandler
   * @param exceptionHandler
   * @return
   */
  public static <E> E newStubInstance( Class<? extends E> clazz,
                                       Class<?>[] interfaces,
                                       final MethodInvocationHandler methodInvocationHandler,
                                       ExceptionHandler exceptionHandler )
  {
    return StubCreator.newStubInstance( clazz, interfaces, adapter( methodInvocationHandler ), exceptionHandler );
  }
  
  /**
   * Returns a adapter which acts as {@link MethodInterceptor} for a given {@link MethodInvocationHandler}
   * 
   * @param methodInvocationHandler
   * @return
   */
  protected static MethodInterceptor adapter( final MethodInvocationHandler methodInvocationHandler )
  {
    return new MethodInterceptor()
    {
      @Override
      public Object intercept( Object obj, Method method, Object[] args, MethodProxy proxy ) throws Throwable
      {
        return methodInvocationHandler.handle( new MethodCallCapture( obj, method, args, proxy ) );
      }
    };
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
  public static <E> E newStubInstance( Class<? extends E> clazz, Class<?>[] interfaces, final MethodInterceptor methodInterceptor )
  {
    final ExceptionHandler exceptionHandler = null;
    return newStubInstance( clazz, interfaces, methodInterceptor, exceptionHandler );
  }
  
  /**
   * Returns a new proxy stub for the given class or interface but takes additional interfaces.
   * 
   * @param <E>
   * @param clazz
   * @param interfaces
   * @param methodInterceptor
   * @param exceptionHandler
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <E> E newStubInstance( Class<? extends E> clazz,
                                       Class<?>[] interfaces,
                                       final MethodInterceptor methodInterceptor,
                                       ExceptionHandler exceptionHandler )
  {
    //
    E retval = null;
    
    //
    if ( clazz != null && methodInterceptor != null )
    {
      try
      {
        //
        final Set<Class<?>> interfaceSet = new HashSet<Class<?>>();
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
        final Enhancer enhancer = new Enhancer();
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
        if ( exceptionHandler != null )
        {
          exceptionHandler.handleException( e );
        }
      }
    }
    
    //
    return retval;
  }
}

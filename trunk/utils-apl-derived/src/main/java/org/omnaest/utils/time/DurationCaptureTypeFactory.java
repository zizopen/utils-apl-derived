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
package org.omnaest.utils.time;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.omnaest.utils.proxy.StubCreator;

/**
 * Proxy creator the measure the time for methods calls of an arbitrary object.
 * 
 * @see #newStubInstance(Object)
 * @see DurationCapture
 * @author Omnaest
 */
public class DurationCaptureTypeFactory
{
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * Defines the interface which proxied Objects will implement, too.
   * 
   * @see DurationCaptureTypeFactory
   * @author Omnaest
   */
  public static interface DurationCaptureAware
  {
    /**
     * Returns the underlying {@link DurationCapture} instance which measures the time of method calls.
     * 
     * @return
     */
    public DurationCapture getDurationCapture();
  }
  
  protected static class MethodInterceptorDurationCapture implements MethodInterceptor
  {
    /* ********************************************** Variables ********************************************** */
    protected DurationCapture durationCapture = DurationCapture.newInstance();
    
    /* ********************************************** Methods ********************************************** */

    @Override
    public Object intercept( Object object, Method method, Object[] args, MethodProxy proxy ) throws Throwable
    {
      //
      Object retval = null;
      
      //
      String methodName = method.getName();
      
      //
      DurationCapture durationCapture = this.durationCapture;
      if ( "getDurationCapture".equals( methodName ) )
      {
        retval = durationCapture;
      }
      else
      {
        //
        durationCapture.startTimeMeasurement( methodName );
        
        //
        retval = proxy.invokeSuper( object, args );
        
        //
        durationCapture.stopTimeMeasurement( methodName );
      }
      
      //
      return retval;
    }
  }
  
  /* ********************************************** Methods ********************************************** */

  /**
   * Creates a proxy stub for a given object. The object will be still called like always, but the time for the method execution
   * will be measured and can be accessed by casting the stub to the {@link DurationCaptureAware} interface and retrieving the
   * {@link DurationCapture} by {@link DurationCaptureAware#getDurationCapture()}.
   */
  @SuppressWarnings("unchecked")
  public static <E> E newStubInstance( E object )
  {
    //
    E retval = null;
    
    //
    if ( object != null )
    {
      //
      Class<? extends Object> objectClass = object.getClass();
      MethodInterceptor methodInterceptor = new MethodInterceptorDurationCapture();
      retval = (E) StubCreator.newStubInstance( objectClass, new Class<?>[] { DurationCaptureAware.class }, methodInterceptor );
    }
    
    //
    return retval;
  }
  
}

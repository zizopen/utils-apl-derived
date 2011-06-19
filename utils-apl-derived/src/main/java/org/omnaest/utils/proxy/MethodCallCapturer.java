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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.cglib.proxy.MethodInterceptor;

import org.omnaest.utils.proxy.StubCreator.MethodInvocationHandler;

/**
 * A {@link MethodCallCapturer} allows to create stubs for given java types which capture the calls of methods of this stub.
 * 
 * @see #newCapturedTypeInstance(Class)
 * @see #getMethodCallCaptureList()
 * @see MethodCallCapture
 * @author Omnaest
 */
public class MethodCallCapturer
{
  /* ********************************************** Variables ********************************************** */
  protected List<MethodCallCapture> methodCallCaptureList = Collections.synchronizedList( new ArrayList<MethodCallCapture>() );
  
  /* ********************************************** Classes/Interfaces ********************************************** */

  /**
   * Interface stubs are implementing when they are created by
   * {@link MethodCallCapturer#newCapturedTypeInstanceMethodCallCapturerAware(Class)}.
   * 
   * @see MethodCallCapturer
   */
  public static interface MethodCallCapturerAware
  {
    /**
     * Returns the underlying {@link MethodCallCapturer}
     * 
     * @return
     */
    public MethodCallCapturer getMethodCallCapturer();
  }
  
  /**
   * {@link MethodInterceptor} for the {@link MethodCallCapturer}.
   * 
   * @author Omnaest
   */
  protected class MethodCaptureMethodInvocationHandler implements MethodInvocationHandler
  {
    /* ********************************************** Variables ********************************************** */
    protected boolean isMethodCallCapturerAware = false;
    
    /* ********************************************** Methods ********************************************** */
    public MethodCaptureMethodInvocationHandler( boolean isMethodCallCapturerAware )
    {
      this.isMethodCallCapturerAware = isMethodCallCapturerAware;
    }
    
    @Override
    public Object handle( MethodCallCapture methodCallCapture )
    {
      //
      Object retval = null;
      
      //
      if ( !"getMethodCallCapturer".equals( methodCallCapture.getMethod().getName() ) )
      {
        MethodCallCapturer.this.methodCallCaptureList.add( methodCallCapture );
      }
      else
      {
        retval = MethodCallCapturer.this;
      }
      
      // 
      return retval;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */

  /**
   * Creates a new stub instance which records all method invocations to this {@link MethodCallCapturer}.
   * 
   * @see MethodCallCapturer#getMethodCallCaptureList()
   */
  public <E> E newCapturedTypeInstance( Class<? extends E> clazz )
  {
    return StubCreator.newStubInstance( clazz, new MethodCaptureMethodInvocationHandler( false ) );
  }
  
  public <E> E newCapturedTypeInstanceMethodCallCapturerAware( Class<? extends E> clazz )
  {
    return StubCreator.newStubInstance( clazz, new Class[] { MethodCallCapturerAware.class },
                                        new MethodCaptureMethodInvocationHandler( true ) );
  }
  
  /**
   * Returns a new list instance of all {@link MethodCallCapture} instances.
   * 
   * @return
   */
  public List<MethodCallCapture> getMethodCallCaptureList()
  {
    return new ArrayList<MethodCallCapture>( this.methodCallCaptureList );
  }
  
  public MethodCallCapture getLastMethodCallCapture()
  {
    return this.methodCallCaptureList.size() > 0 ? this.methodCallCaptureList.get( this.methodCallCaptureList.size() - 1 ) : null;
  }
  
  /**
   * Resets the {@link MethodCallCapturer}.
   * 
   * @return this
   */
  public MethodCallCapturer reset()
  {
    //
    this.methodCallCaptureList.clear();
    
    //
    return this;
  }
}

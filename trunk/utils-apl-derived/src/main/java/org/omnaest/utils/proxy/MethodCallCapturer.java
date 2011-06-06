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
 * @see #newCaptureTypeInstance(Class)
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
   * {@link MethodInterceptor} for the {@link MethodCallCapturer}.
   * 
   * @author Omnaest
   */
  protected class MethodCaptureMethodInvocationHandler implements MethodInvocationHandler
  {
    
    @Override
    public Object handle( MethodCallCapture methodCallCapture )
    {
      //
      MethodCallCapturer.this.methodCallCaptureList.add( methodCallCapture );
      
      // 
      return null;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */

  /**
   * Creates a new stub instance which records all method invocations to this {@link MethodCallCapturer}.
   * 
   * @see MethodCallCapturer#getMethodCallCaptureList()
   */
  public <E> E newCaptureTypeInstance( Class<? extends E> clazz )
  {
    return StubCreator.newStubInstance( clazz, new MethodCaptureMethodInvocationHandler() );
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

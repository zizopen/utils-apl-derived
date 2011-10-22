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

import net.sf.cglib.proxy.MethodProxy;

/**
 * Result of a captured method call.
 */
public class MethodCallCapture
{
  /* ********************************************** Variables ********************************************** */
  private Object      object    = null;
  private Method      method    = null;
  private Object[]    arguments = null;
  private MethodProxy proxy     = null;
  
  /* ********************************************** Methods ********************************************** */
  
  MethodCallCapture( Object obj, Method method, Object[] args, MethodProxy proxy )
  {
    super();
    this.object = obj;
    this.method = method;
    this.arguments = args;
    this.proxy = proxy;
  }
  
  /**
   * @return the enhanced object / stub / proxy
   */
  public Object getObject()
  {
    return this.object;
  }
  
  /**
   * Intercepted method
   * 
   * @return
   */
  public Method getMethod()
  {
    return this.method;
  }
  
  /**
   * Arguments of the method call
   * 
   * @return
   */
  public Object[] getArguments()
  {
    return this.arguments;
  }
  
  /**
   * A secondary proxy used to invoke the non-intercepted method.
   * 
   * @see MethodProxy#invokeSuper(Object, Object[])
   * @return
   */
  public MethodProxy getProxy()
  {
    return this.proxy;
  }
  
  /**
   * Returns the name of the called {@link Method}.
   * 
   * @see Method#getName()
   * @return name of the method or null if no method has been resolved.
   */
  public String getMethodName()
  {
    return this.method != null ? this.method.getName() : null;
  }
}

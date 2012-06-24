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
package org.omnaest.utils.spring.scope;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * The {@link BeanScopeAwareCallableDecorator} does call the {@link BeanScopeThreadContextManager#addCurrentThreadToBeanScope()}
 * and {@link BeanScopeThreadContextManager#removeCurrentThreadFromBeanScope()} at the appropriate times
 * 
 * @see Callable
 * @author Omnaest
 * @param <V>
 */
public class BeanScopeAwareCallableDecorator<V> implements Callable<V>,Serializable
{
  private static final long serialVersionUID = 1884003978722439198L;
  /* ********************************************** Variables ********************************************** */
  private Callable<V>                   callable                      = null;
  private BeanScopeThreadContextManager beanScopeThreadContextManager = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param callable
   *          {@link Callable} : must not be null
   * @param beanScopeThreadContextManager
   *          {@link BeanScopeThreadContextManager}: must not be null
   */
  public BeanScopeAwareCallableDecorator( Callable<V> callable, BeanScopeThreadContextManager beanScopeThreadContextManager )
  {
    super();
    this.callable = callable;
    this.beanScopeThreadContextManager = beanScopeThreadContextManager;
  }
  
  @Override
  public V call() throws Exception
  {
    //    
    V retval = null;
    
    //
    try
    {
      //
      if ( this.beanScopeThreadContextManager != null )
      {
        this.beanScopeThreadContextManager.addCurrentThreadToBeanScope();
      }
      
      //
      if ( this.callable != null )
      {
        retval = this.callable.call();
      }
    }
    finally
    {
      //      
      if ( this.beanScopeThreadContextManager != null )
      {
        this.beanScopeThreadContextManager.removeCurrentThreadFromBeanScope();
      }
    }
    
    //
    return retval;
  }
  
}

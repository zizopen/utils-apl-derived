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
package org.omnaest.utils.spring;

/**
 * The {@link BeanScopeAwareCallableDecorator} does call the {@link BeanScopeThreadContextManager#addCurrentThreadToBeanScope()}
 * and {@link BeanScopeThreadContextManager#removeCurrentThreadFromBeanScope()} at the appropriate times
 * 
 * @see Runnable
 * @author Omnaest
 * @param <V>
 */
public class BeanScopeAwareRunnableDecorator implements Runnable
{
  /* ********************************************** Variables ********************************************** */
  private Runnable                      runnable                      = null;
  private BeanScopeThreadContextManager beanScopeThreadContextManager = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param runnable
   *          {@link Runnable} : must not be null
   * @param beanScopeThreadContextManager
   *          {@link BeanScopeThreadContextManager}: must not be null
   */
  public BeanScopeAwareRunnableDecorator( Runnable runnable, BeanScopeThreadContextManager beanScopeThreadContextManager )
  {
    super();
    this.runnable = runnable;
    this.beanScopeThreadContextManager = beanScopeThreadContextManager;
  }
  
  @Override
  public void run()
  {
    //
    try
    {
      //
      if ( this.beanScopeThreadContextManager != null )
      {
        this.beanScopeThreadContextManager.addCurrentThreadToBeanScope();
      }
      
      //
      if ( this.runnable != null )
      {
        this.runnable.run();
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
  }
  
}

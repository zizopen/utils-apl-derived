/*******************************************************************************
 * Copyright 2012 Danny Kunz
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
package org.omnaest.utils.beans.replicator.adapter;

import java.util.Set;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.beans.replicator.BeanReplicator.AdapterInternal;
import org.omnaest.utils.beans.replicator.BeanReplicator.TransitiveBeanReplicationInvocationHandler;

/**
 * Decorator of a {@link AdapterInternal} instance
 * 
 * @author Omnaest
 */
public class AdapterDecorator implements AdapterInternal
{
  /* ********************************************** Variables ********************************************** */
  protected final AdapterInternal adapterInternal;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see AdapterDecorator
   * @param adapterInternal
   */
  public AdapterDecorator( AdapterInternal adapterInternal )
  {
    //
    super();
    this.adapterInternal = adapterInternal;
    
    //
    Assert.isNotNull( adapterInternal, "The given adapterInternal instance must not be null" );
  }
  
  /**
   * @param transitiveBeanReplicationInvocationHandler
   * @return
   * @see AdapterInternal#newHandlerSet(org.omnaest.utils.beans.replicator.BeanReplicator.TransitiveBeanReplicationInvocationHandler)
   */
  @Override
  public Set<Handler> newHandlerSet( TransitiveBeanReplicationInvocationHandler transitiveBeanReplicationInvocationHandler )
  {
    return this.adapterInternal.newHandlerSet( transitiveBeanReplicationInvocationHandler );
  }
  
}

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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.beans.replicator.BeanReplicator.Adapter;
import org.omnaest.utils.beans.replicator.BeanReplicator.AdapterInternal;
import org.omnaest.utils.beans.replicator.BeanReplicator.TransitiveBeanReplicationInvocationHandler;
import org.omnaest.utils.operation.Operation;
import org.omnaest.utils.operation.foreach.ForEach;
import org.omnaest.utils.structure.collection.list.ListUtils;

/**
 * Composite of multiple {@link AdapterInternal} instances
 * 
 * @author Omnaest
 */
public class AdapterComposite implements AdapterInternal
{
  /* ********************************************** Variables ********************************************** */
  protected final AdapterInternal[] adapterInternals;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see AdapterComposite
   * @param adapters
   */
  public AdapterComposite( Adapter... adapters )
  {
    super();
    this.adapterInternals = new ForEach<Adapter, AdapterInternal>( ListUtils.valueOf( adapters ) ).execute( new Operation<AdapterInternal, Adapter>()
                                                                                                            {
                                                                                                              @Override
                                                                                                              public AdapterInternal execute( Adapter adapter )
                                                                                                              {
                                                                                                                Assert.isTrue( adapter instanceof AdapterInternal );
                                                                                                                return (AdapterInternal) adapter;
                                                                                                              }
                                                                                                            } )
                                                                                                  .toArray( new AdapterInternal[0] );
  }
  
  @Override
  public Set<Handler> newHandlerSet( TransitiveBeanReplicationInvocationHandler transitiveBeanReplicationInvocationHandler )
  {
    //
    final Set<Handler> retset = new LinkedHashSet<Handler>();
    
    //
    for ( AdapterInternal adapterInternal : this.adapterInternals )
    {
      if ( adapterInternal != null )
      {
        retset.addAll( adapterInternal.newHandlerSet( transitiveBeanReplicationInvocationHandler ) );
      }
    }
    
    // 
    return retset;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "AdapterComposite [adapterInternals=" );
    builder.append( Arrays.toString( this.adapterInternals ) );
    builder.append( "]" );
    return builder.toString();
  }
  
}

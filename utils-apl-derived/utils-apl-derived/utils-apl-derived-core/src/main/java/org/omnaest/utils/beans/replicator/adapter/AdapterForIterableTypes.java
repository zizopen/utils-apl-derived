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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.omnaest.utils.beans.replicator.BeanReplicator;
import org.omnaest.utils.beans.replicator.BeanReplicator.Adapter;
import org.omnaest.utils.beans.replicator.BeanReplicator.AdapterInternal;
import org.omnaest.utils.beans.replicator.BeanReplicator.TransitiveBeanReplicationInvocationHandler;
import org.omnaest.utils.structure.collection.set.SetUtils;

/**
 * {@link Adapter} which replicates basic {@link Iterable} derivates.<br>
 * <br>
 * Every value {@link Object} will be replicated using the underlying {@link BeanReplicator} again before it is put into the new
 * {@link Set} instance.<br>
 * <br>
 * If a {@link Iterable} source type cannot be identified exactly a {@link ArrayList} is used as default instance.
 * 
 * @see Adapter
 * @author Omnaest
 */
public class AdapterForIterableTypes implements AdapterInternal
{
  @SuppressWarnings("unchecked")
  @Override
  public Set<Handler> newHandlerSet( final TransitiveBeanReplicationInvocationHandler transitiveBeanReplicationInvocationHandler )
  {
    return SetUtils.<Handler> valueOf( new Handler()
    {
      @Override
      public Object createNewTargetObjectInstance( Class<?> sourceObjectType, Object sourceObject )
      {
        //          
        final List<Object> retlist = new ArrayList<Object>();
        
        //
        if ( sourceObject instanceof Iterable )
        {
          //
          final Iterable<Object> set = (Iterable<Object>) sourceObject;
          for ( final Object value : set )
          {
            //
            final Object replicatedValue = transitiveBeanReplicationInvocationHandler.replicate( value );
            
            //
            retlist.add( replicatedValue );
          }
        }
        
        // 
        return retlist;
      }
      
      @Override
      public boolean canHandle( Class<? extends Object> sourceObjectType )
      {
        return sourceObjectType != null && Iterable.class.isAssignableFrom( sourceObjectType );
      }
      
      @Override
      public String toString()
      {
        StringBuilder builder = new StringBuilder();
        builder.append( "Handler of AdapterForIterableTypes []" );
        return builder.toString();
      }
    } );
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "AdapterForIterableTypes []" );
    return builder.toString();
  }
  
}

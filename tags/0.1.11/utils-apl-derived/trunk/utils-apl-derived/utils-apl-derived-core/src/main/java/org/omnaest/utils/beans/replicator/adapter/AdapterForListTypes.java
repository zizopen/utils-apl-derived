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
import java.util.concurrent.CopyOnWriteArrayList;

import org.omnaest.utils.beans.replicator.BeanReplicator;
import org.omnaest.utils.beans.replicator.BeanReplicator.Adapter;
import org.omnaest.utils.beans.replicator.BeanReplicator.AdapterInternal;
import org.omnaest.utils.beans.replicator.BeanReplicator.TransitiveBeanReplicationInvocationHandler;
import org.omnaest.utils.structure.collection.set.SetUtils;

/**
 * {@link Adapter} which replicates basic {@link List} derivates like {@link ArrayList} and {@link CopyOnWriteArrayList}<br>
 * <br>
 * Every value {@link Object} will be replicated using the underlying {@link BeanReplicator} again before it is put into the new
 * {@link Set} instance.<br>
 * <br>
 * If a {@link List} source type cannot be identified exactly a {@link ArrayList} is used as default instance.
 * 
 * @see Adapter
 * @author Omnaest
 */
public class AdapterForListTypes implements AdapterInternal
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
        List<Object> retlist = null;
        
        //
        if ( ArrayList.class.isAssignableFrom( sourceObjectType ) )
        {
          retlist = new ArrayList<Object>();
        }
        else if ( CopyOnWriteArrayList.class.isAssignableFrom( sourceObjectType ) )
        {
          retlist = new CopyOnWriteArrayList<Object>();
        }
        else
        {
          retlist = new ArrayList<Object>();
        }
        
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
        return sourceObjectType != null && List.class.isAssignableFrom( sourceObjectType );
      }
      
      @Override
      public String toString()
      {
        StringBuilder builder = new StringBuilder();
        builder.append( "Handler of AdapterForListTypes []" );
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
    builder.append( "AdapterForListTypes []" );
    return builder.toString();
  }
  
}

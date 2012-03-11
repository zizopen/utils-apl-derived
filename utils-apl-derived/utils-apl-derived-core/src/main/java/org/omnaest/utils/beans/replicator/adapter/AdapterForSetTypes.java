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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.omnaest.utils.beans.replicator.BeanReplicator;
import org.omnaest.utils.beans.replicator.BeanReplicator.Adapter;
import org.omnaest.utils.beans.replicator.BeanReplicator.AdapterInternal;
import org.omnaest.utils.beans.replicator.BeanReplicator.TransitiveBeanReplicationInvocationHandler;
import org.omnaest.utils.structure.collection.set.SetUtils;

/**
 * {@link Adapter} which replicates basic {@link Set} derivates like {@link HashSet}, {@link LinkedHashSet}, {@link TreeSet} and
 * {@link ConcurrentSkipListSet}<br>
 * <br>
 * Every value {@link Object} will be replicated using the underlying {@link BeanReplicator} again before it is put into the new
 * {@link Set} instance.<br>
 * <br>
 * If a {@link Set} source type cannot be identified exactly {@link LinkedHashSet} is used as default for {@link Set}s and
 * {@link TreeSet} for {@link SortedSet}s.
 * 
 * @see Adapter
 * @author Omnaest
 */
public class AdapterForSetTypes implements AdapterInternal
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
        Set<Object> retset = null;
        
        //
        if ( SortedSet.class.isAssignableFrom( sourceObjectType ) )
        {
          //
          if ( TreeSet.class.isAssignableFrom( sourceObjectType ) )
          {
            retset = new TreeSet<Object>();
          }
          else if ( ConcurrentSkipListSet.class.isAssignableFrom( sourceObjectType ) )
          {
            retset = new ConcurrentSkipListSet<Object>();
          }
          else
          {
            retset = new ConcurrentSkipListSet<Object>();
          }
        }
        else
        {
          //
          if ( HashSet.class.isAssignableFrom( sourceObjectType ) )
          {
            retset = new HashSet<Object>();
          }
          else if ( LinkedHashSet.class.isAssignableFrom( sourceObjectType ) )
          {
            retset = new LinkedHashSet<Object>();
          }
          else
          {
            retset = new LinkedHashSet<Object>();
          }
        }
        
        //
        if ( sourceObject instanceof Set )
        {
          //
          final Set<Object> set = (Set<Object>) sourceObject;
          for ( final Object value : set )
          {
            //
            final Object replicatedValue = transitiveBeanReplicationInvocationHandler.replicate( value );
            
            //
            retset.add( replicatedValue );
          }
        }
        
        // 
        return retset;
      }
      
      @Override
      public boolean canHandle( Class<? extends Object> sourceObjectType )
      {
        return sourceObjectType != null && Set.class.isAssignableFrom( sourceObjectType );
      }
      
      @Override
      public String toString()
      {
        StringBuilder builder = new StringBuilder();
        builder.append( "Handler of AdapterForSetTypes []" );
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
    builder.append( "AdapterForSetTypes []" );
    return builder.toString();
  }
  
}

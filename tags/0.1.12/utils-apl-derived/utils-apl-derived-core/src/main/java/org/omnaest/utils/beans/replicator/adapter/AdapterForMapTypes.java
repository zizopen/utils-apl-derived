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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.omnaest.utils.beans.replicator.BeanReplicator;
import org.omnaest.utils.beans.replicator.BeanReplicator.Adapter;
import org.omnaest.utils.beans.replicator.BeanReplicator.AdapterInternal;
import org.omnaest.utils.beans.replicator.BeanReplicator.TransitiveBeanReplicationInvocationHandler;
import org.omnaest.utils.structure.collection.set.SetUtils;

import com.google.common.collect.HashBiMap;

/**
 * {@link Adapter} which replicates basic {@link Map} derivates like {@link HashMap}, {@link LinkedHashMap},
 * {@link ConcurrentHashMap}, {@link TreeMap}, {@link ConcurrentSkipListMap}, {@link HashBiMap} and {@link WeakHashMap}.<br>
 * <br>
 * Every key and value object will be replicated using the underlying {@link BeanReplicator} again before it is put into the new
 * {@link Map} instance.<br>
 * <br>
 * If a {@link Map} source type cannot be identified exactly {@link LinkedHashMap} is used as default for {@link Map}s and
 * {@link TreeMap} for {@link SortedMap}s.
 * 
 * @see Adapter
 * @author Omnaest
 */
public class AdapterForMapTypes implements AdapterInternal
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
        Map<Object, Object> retmap = null;
        
        //
        if ( SortedMap.class.isAssignableFrom( sourceObjectType ) )
        {
          //
          if ( TreeMap.class.isAssignableFrom( sourceObjectType ) )
          {
            retmap = new TreeMap<Object, Object>();
          }
          else if ( ConcurrentSkipListMap.class.isAssignableFrom( sourceObjectType ) )
          {
            retmap = new ConcurrentSkipListMap<Object, Object>();
          }
          else
          {
            retmap = new TreeMap<Object, Object>();
          }
        }
        else
        {
          //
          if ( HashMap.class.isAssignableFrom( sourceObjectType ) )
          {
            retmap = new HashMap<Object, Object>();
          }
          else if ( LinkedHashMap.class.isAssignableFrom( sourceObjectType ) )
          {
            retmap = new LinkedHashMap<Object, Object>();
          }
          else if ( ConcurrentHashMap.class.isAssignableFrom( sourceObjectType ) )
          {
            retmap = new ConcurrentHashMap<Object, Object>();
          }
          else if ( WeakHashMap.class.isAssignableFrom( sourceObjectType ) )
          {
            retmap = new WeakHashMap<Object, Object>();
          }
          else if ( HashBiMap.class.isAssignableFrom( sourceObjectType ) )
          {
            retmap = HashBiMap.<Object, Object> create();
          }
          else
          {
            retmap = new LinkedHashMap<Object, Object>();
          }
        }
        
        //
        if ( sourceObject instanceof Map )
        {
          
          final Map<Object, Object> map = (Map<Object, Object>) sourceObject;
          for ( Object key : map.keySet() )
          {
            //
            final Object value = map.get( key );
            
            //
            Object replicateKey = transitiveBeanReplicationInvocationHandler.replicate( key );
            Object replicateValue = transitiveBeanReplicationInvocationHandler.replicate( value );
            
            //
            retmap.put( replicateKey, replicateValue );
          }
        }
        
        // 
        return retmap;
      }
      
      @Override
      public boolean canHandle( Class<? extends Object> sourceObjectType )
      {
        return sourceObjectType != null && Map.class.isAssignableFrom( sourceObjectType );
      }
      
      @Override
      public String toString()
      {
        StringBuilder builder = new StringBuilder();
        builder.append( "Handler of AdapterForMapTypes []" );
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
    builder.append( "AdapterForMapTypes []" );
    return builder.toString();
  }
  
}

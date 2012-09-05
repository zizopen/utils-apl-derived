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
package org.omnaest.utils.structure.hierarchy.nodemap;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @see NodeMap
 * @author Omnaest
 * @param <K>
 * @param <M>
 */
class NodeMapImpl<K, M> implements NodeMap<K, M>
{
  private final Map<K, NodeMap<K, M>> keyToChildrenMap = new LinkedHashMap<K, NodeMap<K, M>>();
  private M                           model;
  
  @Override
  public M getModel()
  {
    return this.model;
  }
  
  @Override
  public void setModel( M model )
  {
    this.model = model;
  }
  
  public int size()
  {
    return this.keyToChildrenMap.size();
  }
  
  public boolean isEmpty()
  {
    return this.keyToChildrenMap.isEmpty();
  }
  
  public boolean containsKey( Object key )
  {
    return this.keyToChildrenMap.containsKey( key );
  }
  
  public boolean containsValue( Object value )
  {
    return this.keyToChildrenMap.containsValue( value );
  }
  
  public NodeMap<K, M> get( Object key )
  {
    return this.keyToChildrenMap.get( key );
  }
  
  public NodeMap<K, M> put( K key, NodeMap<K, M> value )
  {
    return this.keyToChildrenMap.put( key, value );
  }
  
  public NodeMap<K, M> remove( Object key )
  {
    return this.keyToChildrenMap.remove( key );
  }
  
  public void putAll( Map<? extends K, ? extends NodeMap<K, M>> m )
  {
    this.keyToChildrenMap.putAll( m );
  }
  
  public void clear()
  {
    this.keyToChildrenMap.clear();
  }
  
  public Set<K> keySet()
  {
    return this.keyToChildrenMap.keySet();
  }
  
  public Collection<NodeMap<K, M>> values()
  {
    return this.keyToChildrenMap.values();
  }
  
  public Set<java.util.Map.Entry<K, NodeMap<K, M>>> entrySet()
  {
    return this.keyToChildrenMap.entrySet();
  }
  
  public boolean equals( Object o )
  {
    return this.keyToChildrenMap.equals( o );
  }
  
  public int hashCode()
  {
    return this.keyToChildrenMap.hashCode();
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "NodeMapImpl [model=" );
    builder.append( this.model );
    builder.append( ", keyToChildrenMap=" );
    builder.append( this.keyToChildrenMap );
    builder.append( "]" );
    return builder.toString();
  }
  
}

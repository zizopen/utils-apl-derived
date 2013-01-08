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
package org.omnaest.utils.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;

/**
 * {@link Cache} implementation using a synchronized {@link WeakHashMap}. It is thread safe but has a bad performance in
 * concurrent access situations. Stored elements can vanish at any time the underlying JVM decides to clear them.
 * 
 * @see ConcurrentWeakReferenceCache
 * @see Cache
 * @author Omnaest
 * @param <K>
 * @param <V>
 */
public class SynchronizedWeakReferenceCache<K, V> extends CacheAbstract<K, V>
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = -3985243797516127152L;
  /* ********************************************** Variables ********************************************** */
  private final Map<K, V>   map              = Collections.synchronizedMap( new WeakHashMap<K, V>() );
  
  /* ********************************************** Methods ********************************************** */
  @Override
  public int size()
  {
    return this.map.size();
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.map.isEmpty();
  }
  
  @Override
  public boolean containsKey( Object key )
  {
    return this.map.containsKey( key );
  }
  
  @Override
  public boolean containsValue( Object value )
  {
    return this.map.containsValue( value );
  }
  
  @Override
  public V get( Object key )
  {
    return this.map.get( key );
  }
  
  @Override
  public V put( K key, V value )
  {
    return this.map.put( key, value );
  }
  
  @Override
  public V remove( Object key )
  {
    return this.map.remove( key );
  }
  
  @Override
  public void putAll( Map<? extends K, ? extends V> m )
  {
    this.map.putAll( m );
  }
  
  @Override
  public void clear()
  {
    this.map.clear();
  }
  
  @Override
  public Set<K> keySet()
  {
    synchronized ( this.map )
    {
      return Collections.unmodifiableSet( SetUtils.valueOf( this.map.keySet() ) );
    }
  }
  
  @Override
  public Collection<V> values()
  {
    synchronized ( this.map )
    {
      return Collections.unmodifiableCollection( ListUtils.valueOf( this.map.values() ) );
    }
  }
  
  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet()
  {
    synchronized ( this.map )
    {
      return Collections.unmodifiableSet( SetUtils.valueOf( this.map.entrySet() ) );
    }
  }
  
  @Override
  public boolean equals( Object o )
  {
    return this.map.equals( o );
  }
  
  @Override
  public int hashCode()
  {
    return this.map.hashCode();
  }
  
}

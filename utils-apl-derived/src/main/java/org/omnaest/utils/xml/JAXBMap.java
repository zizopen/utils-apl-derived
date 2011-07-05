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
package org.omnaest.utils.xml;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The {@link JAXBMap} is a artificial {@link XmlRootElement} for an arbitrary {@link Map} instance. It just stores an internal
 * map instance and delegates all {@link Map} based methods to it.
 * 
 * @see #newInstance(Map)
 * @author Omnaest
 * @param <K>
 * @param <V>
 */
@XmlRootElement(name = "map")
public class JAXBMap<K, V> implements Map<K, V>
{
  /* ********************************************** Variables ********************************************** */
  @XmlElementWrapper(name = "entries")
  protected Map<K, V> map = null;
  
  /* ********************************************** Methods ********************************************** */

  protected JAXBMap()
  {
    this.map = new HashMap<K, V>();
  }
  
  protected JAXBMap( Map<K, V> map )
  {
    super();
    this.map = map;
  }
  
  /**
   * Creates a new {@link Map} wrapper.
   * 
   * @param <M>
   * @param <K>
   * @param <V>
   * @param map
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <M extends JAXBMap<K, V>, K, V> M newInstance( Map<K, V> map )
  {
    //
    M retmap = null;
    
    //
    if ( map != null )
    {
      retmap = (M) new JAXBMap<K, V>( map );
    }
    
    //
    return retmap;
  }
  
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
    return this.map.keySet();
  }
  
  @Override
  public Collection<V> values()
  {
    return this.map.values();
  }
  
  @Override
  public Set<Entry<K, V>> entrySet()
  {
    return this.map.entrySet();
  }
}

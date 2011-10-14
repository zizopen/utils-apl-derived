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
package org.omnaest.utils.structure.map;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.structure.element.Factory;

/**
 * A {@link ThreadLocalMap} is a {@link Map} stored independently for each {@link Thread} by using {@link ThreadLocal}. As
 * underlying {@link Map} a {@link LinkedHashMap} is used as default. To use other implementations set the {@link Map} explicit
 * for each {@link Thread} using {@link #setMap(Map)} or provide another {@link Factory} for the {@link Map} creation using
 * {@link #setMapFactory(Factory)}
 * 
 * @author Omnaest
 */
public class ThreadLocalMap<K, V> implements Map<K, V>
{
  /* ********************************************** Variables ********************************************** */
  protected ThreadLocal<Map<K, V>> threadLocalMap = new ThreadLocal<Map<K, V>>();
  protected Factory<Map<K, V>>     mapFactory     = new Factory<Map<K, V>>()
                                                  {
                                                    @Override
                                                    public Map<K, V> newElement()
                                                    {
                                                      return new LinkedHashMap<K, V>();
                                                    }
                                                  };
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see java.util.Map#clear()
   */
  @Override
  public void clear()
  {
    this.getMap().clear();
  }
  
  /**
   * @param key
   * @return
   * @see java.util.Map#containsKey(java.lang.Object)
   */
  @Override
  public boolean containsKey( Object key )
  {
    return this.getMap().containsKey( key );
  }
  
  /**
   * @param value
   * @return
   * @see java.util.Map#containsValue(java.lang.Object)
   */
  @Override
  public boolean containsValue( Object value )
  {
    return this.getMap().containsValue( value );
  }
  
  /**
   * @return
   * @see java.util.Map#entrySet()
   */
  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet()
  {
    return this.getMap().entrySet();
  }
  
  /**
   * @param o
   * @return
   * @see java.util.Map#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object o )
  {
    return this.getMap().equals( o );
  }
  
  /**
   * @param key
   * @return
   * @see java.util.Map#get(java.lang.Object)
   */
  @Override
  public V get( Object key )
  {
    return this.getMap().get( key );
  }
  
  /**
   * Gets the underlying {@link Map} for the current {@link Thread} context
   * 
   * @return
   */
  public Map<K, V> getMap()
  {
    //
    Map<K, V> retmap = this.threadLocalMap.get();
    
    //
    if ( retmap == null )
    {
      //
      retmap = this.mapFactory.newElement();
      this.threadLocalMap.set( retmap );
    }
    
    //
    return retmap;
  }
  
  /**
   * @return
   * @see java.util.Map#hashCode()
   */
  @Override
  public int hashCode()
  {
    return this.getMap().hashCode();
  }
  
  /**
   * @return
   * @see java.util.Map#isEmpty()
   */
  @Override
  public boolean isEmpty()
  {
    return this.getMap().isEmpty();
  }
  
  /**
   * @return
   * @see java.util.Map#keySet()
   */
  @Override
  public Set<K> keySet()
  {
    return this.getMap().keySet();
  }
  
  /**
   * @param key
   * @param value
   * @return
   * @see java.util.Map#put(java.lang.Object, java.lang.Object)
   */
  @Override
  public V put( K key, V value )
  {
    return this.getMap().put( key, value );
  }
  
  /**
   * @param m
   * @see java.util.Map#putAll(java.util.Map)
   */
  @Override
  public void putAll( Map<? extends K, ? extends V> m )
  {
    this.getMap().putAll( m );
  }
  
  /**
   * @param key
   * @return
   * @see java.util.Map#remove(java.lang.Object)
   */
  @Override
  public V remove( Object key )
  {
    return this.getMap().remove( key );
  }
  
  /**
   * Sets the underlying {@link Map} for the current {@link Thread}
   * 
   * @param map
   * @return
   */
  public ThreadLocalMap<K, V> setMap( Map<K, V> map )
  {
    //
    if ( map != null )
    {
      this.threadLocalMap.set( map );
    }
    
    //
    return this;
  }
  
  /**
   * Sets the {@link Factory} for new {@link Map} instances. This factory is used initially if no {@link Map} instance exists for
   * the current {@link Thread}
   * 
   * @param mapFactory
   */
  public void setMapFactory( Factory<Map<K, V>> mapFactory )
  {
    this.mapFactory = mapFactory;
  }
  
  /**
   * @return
   * @see java.util.Map#size()
   */
  @Override
  public int size()
  {
    return this.getMap().size();
  }
  
  /**
   * @return
   * @see java.util.Map#values()
   */
  @Override
  public Collection<V> values()
  {
    return this.getMap().values();
  }
  
}

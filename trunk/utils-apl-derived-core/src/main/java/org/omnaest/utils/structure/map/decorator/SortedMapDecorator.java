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
package org.omnaest.utils.structure.map.decorator;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A decorator for any {@link SortedMap} implementation
 * 
 * @see MapDecorator
 * @author Omnaest
 */
@XmlRootElement(name = "sortedmap")
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class SortedMapDecorator<K, V> implements SortedMap<K, V>
{
  /* ********************************************** Variables ********************************************** */
  @XmlElementWrapper(name = "entries")
  protected SortedMap<K, V> sortedMap = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see SortedMapDecorator
   * @param sortedMap
   */
  public SortedMapDecorator( SortedMap<K, V> sortedMap )
  {
    super();
    this.sortedMap = sortedMap;
  }
  
  /**
   * @see SortedMapDecorator
   */
  protected SortedMapDecorator()
  {
    super();
  }
  
  /**
   * @return
   * @see java.util.Map#size()
   */
  @Override
  public int size()
  {
    return this.sortedMap.size();
  }
  
  /**
   * @return
   * @see java.util.Map#isEmpty()
   */
  @Override
  public boolean isEmpty()
  {
    return this.sortedMap.isEmpty();
  }
  
  /**
   * @param key
   * @return
   * @see java.util.Map#containsKey(java.lang.Object)
   */
  @Override
  public boolean containsKey( Object key )
  {
    return this.sortedMap.containsKey( key );
  }
  
  /**
   * @param value
   * @return
   * @see java.util.Map#containsValue(java.lang.Object)
   */
  @Override
  public boolean containsValue( Object value )
  {
    return this.sortedMap.containsValue( value );
  }
  
  /**
   * @param key
   * @return
   * @see java.util.Map#get(java.lang.Object)
   */
  @Override
  public V get( Object key )
  {
    return this.sortedMap.get( key );
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
    return this.sortedMap.put( key, value );
  }
  
  /**
   * @param key
   * @return
   * @see java.util.Map#remove(java.lang.Object)
   */
  @Override
  public V remove( Object key )
  {
    return this.sortedMap.remove( key );
  }
  
  /**
   * @param m
   * @see java.util.Map#putAll(java.util.Map)
   */
  @Override
  public void putAll( Map<? extends K, ? extends V> m )
  {
    this.sortedMap.putAll( m );
  }
  
  /**
   * @see java.util.Map#clear()
   */
  @Override
  public void clear()
  {
    this.sortedMap.clear();
  }
  
  /**
   * @return
   * @see java.util.Map#keySet()
   */
  @Override
  public Set<K> keySet()
  {
    return this.sortedMap.keySet();
  }
  
  /**
   * @return
   * @see java.util.Map#values()
   */
  @Override
  public Collection<V> values()
  {
    return this.sortedMap.values();
  }
  
  /**
   * @return
   * @see java.util.Map#entrySet()
   */
  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet()
  {
    return this.sortedMap.entrySet();
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( obj == null )
    {
      return false;
    }
    if ( !( obj instanceof Map ) )
    {
      return false;
    }
    Map<?, ?> other = (Map<?, ?>) obj;
    if ( this.sortedMap == null )
    {
      return false;
    }
    else if ( !this.sortedMap.equals( other ) )
    {
      return false;
    }
    return true;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.sortedMap == null ) ? 0 : this.sortedMap.hashCode() );
    return result;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( this.sortedMap );
    return builder.toString();
  }
  
  /**
   * @return the map
   */
  protected Map<K, V> getMap()
  {
    return this.sortedMap;
  }
  
  /**
   * @param sortedMap
   *          the map to set
   */
  protected void setMap( SortedMap<K, V> sortedMap )
  {
    this.sortedMap = sortedMap;
  }
  
  /**
   * @return
   * @see java.util.SortedMap#comparator()
   */
  public Comparator<? super K> comparator()
  {
    return this.sortedMap.comparator();
  }
  
  /**
   * @param fromKey
   * @param toKey
   * @return
   * @see java.util.SortedMap#subMap(java.lang.Object, java.lang.Object)
   */
  public SortedMap<K, V> subMap( K fromKey, K toKey )
  {
    return this.sortedMap.subMap( fromKey, toKey );
  }
  
  /**
   * @param toKey
   * @return
   * @see java.util.SortedMap#headMap(java.lang.Object)
   */
  public SortedMap<K, V> headMap( K toKey )
  {
    return this.sortedMap.headMap( toKey );
  }
  
  /**
   * @param fromKey
   * @return
   * @see java.util.SortedMap#tailMap(java.lang.Object)
   */
  public SortedMap<K, V> tailMap( K fromKey )
  {
    return this.sortedMap.tailMap( fromKey );
  }
  
  /**
   * @return
   * @see java.util.SortedMap#firstKey()
   */
  public K firstKey()
  {
    return this.sortedMap.firstKey();
  }
  
  /**
   * @return
   * @see java.util.SortedMap#lastKey()
   */
  public K lastKey()
  {
    return this.sortedMap.lastKey();
  }
  
  /**
   * @param sortedMap
   *          the sortedMap to set
   */
  public void setSortedMap( SortedMap<K, V> sortedMap )
  {
    this.sortedMap = sortedMap;
  }
  
}

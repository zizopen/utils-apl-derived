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

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A decorator for any {@link Map} implementation
 * 
 * @see MapDecorator
 * @author Omnaest
 */
public abstract class MapDecoratorAbstract<K, V> implements Map<K, V>, Serializable
{
  private static final long serialVersionUID = -2062958271682639706L;
  
  /**
   * @see MapDecoratorAbstract
   */
  protected MapDecoratorAbstract()
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
    return this.getMap().size();
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
   * @param m
   * @see java.util.Map#putAll(java.util.Map)
   */
  @Override
  public void putAll( Map<? extends K, ? extends V> m )
  {
    this.getMap().putAll( m );
  }
  
  /**
   * @see java.util.Map#clear()
   */
  @Override
  public void clear()
  {
    this.getMap().clear();
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
   * @return
   * @see java.util.Map#values()
   */
  @Override
  public Collection<V> values()
  {
    return this.getMap().values();
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
    if ( this.getMap() == null )
    {
      return false;
    }
    else if ( !this.getMap().equals( other ) )
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
    result = prime * result + ( ( this.getMap() == null ) ? 0 : this.getMap().hashCode() );
    return result;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( this.getMap() );
    return builder.toString();
  }
  
  /**
   * @return the map
   */
  protected abstract Map<K, V> getMap();
  
}

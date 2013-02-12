/*******************************************************************************
 * Copyright 2013 Danny Kunz
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverter;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverterSerializable;
import org.omnaest.utils.structure.element.converter.ElementConverter;

/**
 * The {@link AggregatedMap} allows to combine a {@link List} of {@link Map}s into a single one. The rule is, that keys are
 * searched within {@link Map}s at the beginning of the {@link List} towards the end. This means if an earlier {@link Map}
 * contains a key, the other {@link Map}s are ignored, even if there is a similar key available also.
 * 
 * @author Omnaest
 * @param <K>
 * @param <V>
 */
public class AggregatedMap<K, V> implements Map<K, V>
{
  private final Iterable<Map<K, V>> mapIterable;
  
  /**
   * @see AggregatedMap
   * @param mapIterable
   */
  @SuppressWarnings("unchecked")
  public AggregatedMap( Iterable<? extends Map<K, V>> mapIterable )
  {
    super();
    Assert.isNotNull( mapIterable, "mapIterable must not be null" );
    this.mapIterable = (Iterable<Map<K, V>>) mapIterable;
  }
  
  /**
   * @see AggregatedMap
   * @param maps
   */
  public AggregatedMap( Map<K, V>... maps )
  {
    this( Arrays.asList( maps ) );
  }
  
  /**
   * @see AggregatedMap
   * @param map
   */
  @SuppressWarnings("unchecked")
  public AggregatedMap( Map<K, V> map )
  {
    this( Arrays.asList( map ) );
  }
  
  @Override
  public int size()
  {
    return this.keySet().size();
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.size() == 0;
  }
  
  @Override
  public boolean containsKey( Object key )
  {
    boolean retval = false;
    for ( Map<K, V> map : this.mapIterable )
    {
      if ( map != null )
      {
        retval = map.containsKey( key );
        if ( retval )
        {
          break;
        }
      }
    }
    return retval;
  }
  
  @Override
  public boolean containsValue( Object value )
  {
    boolean retval = false;
    for ( Map<K, V> map : this.mapIterable )
    {
      if ( map != null )
      {
        retval = map.containsValue( value );
        if ( retval )
        {
          break;
        }
      }
    }
    return retval;
  }
  
  @Override
  public V get( Object key )
  {
    V retval = null;
    for ( Map<K, V> map : this.mapIterable )
    {
      if ( map != null && map.containsKey( key ) )
      {
        retval = map.get( key );
        if ( retval != null )
        {
          break;
        }
      }
    }
    return retval;
  }
  
  /**
   * Puts the value into all {@link Map}s which contains the given key already, or into the first {@link Map} otherwise
   */
  @Override
  public V put( K key, V value )
  {
    V retval = null;
    
    boolean successful = false;
    for ( Map<K, V> map : this.mapIterable )
    {
      if ( map != null && map.containsKey( key ) )
      {
        retval = map.put( key, value );
        successful = true;
      }
    }
    
    if ( !successful )
    {
      Iterator<Map<K, V>> iterator = this.mapIterable.iterator();
      if ( iterator.hasNext() )
      {
        Map<K, V> map = iterator.next();
        if ( map != null )
        {
          retval = map.put( key, value );
        }
      }
    }
    
    return retval;
  }
  
  /**
   * Removes the key from all the {@link Map}s returning the value of the first occuring key value pair.
   */
  @Override
  public V remove( Object key )
  {
    V retval = null;
    for ( Map<K, V> map : this.mapIterable )
    {
      if ( map != null && map.containsKey( key ) )
      {
        V removedValue = map.remove( key );
        if ( retval == null )
        {
          retval = removedValue;
        }
      }
    }
    return retval;
  }
  
  @Override
  public void putAll( Map<? extends K, ? extends V> m )
  {
    if ( m != null )
    {
      for ( K key : m.keySet() )
      {
        V value = m.get( key );
        this.put( key, value );
      }
    }
    
  }
  
  @Override
  public void clear()
  {
    for ( Map<K, V> map : this.mapIterable )
    {
      if ( map != null )
      {
        map.clear();
      }
    }
  }
  
  @Override
  public Set<K> keySet()
  {
    return SetUtils.composite( ListUtils.convert( this.mapIterable, new ElementConverter<Map<K, V>, Set<K>>()
    {
      @Override
      public Set<K> convert( Map<K, V> map )
      {
        return map != null ? map.keySet() : null;
      }
    } ) );
  }
  
  @Override
  public Collection<V> values()
  {
    ElementBidirectionalConverter<Entry<K, V>, V> elementBidirectionalConverter = new ElementBidirectionalConverterSerializable<Map.Entry<K, V>, V>()
    {
      private static final long serialVersionUID = -7294690631488549801L;
      
      @Override
      public java.util.Map.Entry<K, V> convertBackwards( V element )
      {
        throw new UnsupportedOperationException();
      }
      
      @Override
      public V convert( java.util.Map.Entry<K, V> element )
      {
        return element.getValue();
      }
    };
    return CollectionUtils.adapter( this.entrySet(), elementBidirectionalConverter );
  }
  
  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet()
  {
    ElementBidirectionalConverter<K, Entry<K, V>> elementBidirectionalConverter = new ElementBidirectionalConverterSerializable<K, Map.Entry<K, V>>()
    {
      private static final long serialVersionUID = 3353283940310108373L;
      
      @Override
      public K convertBackwards( java.util.Map.Entry<K, V> element )
      {
        return element != null ? element.getKey() : null;
      }
      
      @Override
      public java.util.Map.Entry<K, V> convert( final K key )
      {
        return key != null ? new Entry<K, V>()
        {
          @Override
          public K getKey()
          {
            return key;
          }
          
          @Override
          public V getValue()
          {
            return get( key );
          }
          
          @Override
          public V setValue( V value )
          {
            return put( key, value );
          }
        } : null;
      }
    };
    return SetUtils.adapter( this.keySet(), elementBidirectionalConverter );
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "AggregatedMap " + MapUtils.toString( this ) );
    return builder.toString();
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.mapIterable == null ) ? 0 : ListUtils.valueOf( this.mapIterable ).hashCode() );
    return result;
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
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
    Map other = (Map) obj;
    return MapUtils.delta( this, other ).areEqual();
  }
  
}

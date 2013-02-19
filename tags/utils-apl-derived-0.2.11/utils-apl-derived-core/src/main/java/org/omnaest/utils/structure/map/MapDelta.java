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
package org.omnaest.utils.structure.map;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.structure.collection.set.SetDelta;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.element.factory.concrete.HashMapFactory;
import org.omnaest.utils.tuple.Tuple2;

/**
 * A {@link MapDelta} will calculate the {@link SetDelta} of the {@link Map#keySet()} and the delta between the values of the
 * retained keyset.
 * 
 * @see MapUtils#delta(Map, Map)
 * @see SetUtils#delta(Set, Set)
 * @author Omnaest
 * @param <K>
 * @param <V>
 */
public class MapDelta<K, V>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final SetDelta<K>                setDelta;
  private final Map<K, MapDelta.Values<V>> retainedKeyToUnequalValuesMap = new LinkedHashMap<K, MapDelta.Values<V>>();
  private final Map<K, V>                  retainedKeyToEqualValueMap    = new LinkedHashMap<K, V>();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Holds the value of the first and second map
   * 
   * @author Omnaest
   * @param <V>
   */
  public static class Values<V> extends Tuple2<V, V>
  {
    private static final long serialVersionUID = -468215748811694668L;
    
    public Values( V valueFirst, V valueSecond )
    {
      super( valueFirst, valueSecond );
    }
    
  }
  
  /**
   * @see MapDelta
   * @param firstMap
   * @param secondMap
   */
  public MapDelta( Map<K, V> firstMap, Map<K, V> secondMap )
  {
    firstMap = ObjectUtils.defaultIfNull( firstMap, new HashMapFactory<K, V>() );
    secondMap = ObjectUtils.defaultIfNull( secondMap, new HashMapFactory<K, V>() );
    
    this.setDelta = SetUtils.delta( firstMap.keySet(), secondMap.keySet() );
    
    final Set<K> retainedElementSet = this.setDelta.getRetainedElementSet();
    if ( retainedElementSet != null )
    {
      for ( K key : retainedElementSet )
      {
        V valueFirst = firstMap.get( key );
        V valueSecond = secondMap.get( key );
        
        if ( org.apache.commons.lang3.ObjectUtils.equals( valueFirst, valueSecond ) )
        {
          this.retainedKeyToEqualValueMap.put( key, valueFirst );
        }
        else
        {
          this.retainedKeyToUnequalValuesMap.put( key, new MapDelta.Values<V>( valueFirst, valueSecond ) );
        }
      }
    }
  }
  
  /**
   * Returns a {@link Map} with all retained keys between the first and second map which have changed their value
   * 
   * @see Values
   * @return
   */
  public Map<K, MapDelta.Values<V>> getRetainedKeyToUnequalValuesMap()
  {
    return Collections.unmodifiableMap( this.retainedKeyToUnequalValuesMap );
  }
  
  /**
   * Returns a {@link Map} with all retained keys between the first and second map which have not changed their value
   * 
   * @return
   */
  public Map<K, V> getRetainedKeyToEqualValueMap()
  {
    return Collections.unmodifiableMap( this.retainedKeyToEqualValueMap );
  }
  
  /**
   * Returns the {@link SetDelta} of the {@link Map#keySet()}
   * 
   * @see SetDelta
   * @return
   */
  public SetDelta<K> getKeySetDelta()
  {
    return this.setDelta;
  }
  
  /**
   * Returns true, if the given {@link Map}s are {@link #equals(Object)} in key and values
   * 
   * @return
   */
  public boolean areEqual()
  {
    return this.setDelta.getAddedElementSet().isEmpty() && this.setDelta.getRemovedElementSet().isEmpty()
           && this.retainedKeyToUnequalValuesMap.isEmpty();
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "MapDelta [setDelta=" );
    builder.append( this.setDelta );
    builder.append( ", retainedKeyToUnequalValuesMap=" );
    builder.append( this.retainedKeyToUnequalValuesMap );
    builder.append( ", retainedKeyToEqualValueMap=" );
    builder.append( this.retainedKeyToEqualValueMap );
    builder.append( "]" );
    return builder.toString();
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.retainedKeyToEqualValueMap == null ) ? 0 : this.retainedKeyToEqualValueMap.hashCode() );
    result = prime * result
             + ( ( this.retainedKeyToUnequalValuesMap == null ) ? 0 : this.retainedKeyToUnequalValuesMap.hashCode() );
    result = prime * result + ( ( this.setDelta == null ) ? 0 : this.setDelta.hashCode() );
    return result;
  }
  
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
    if ( !( obj instanceof MapDelta ) )
    {
      return false;
    }
    @SuppressWarnings("rawtypes")
    MapDelta other = (MapDelta) obj;
    if ( this.retainedKeyToEqualValueMap == null )
    {
      if ( other.retainedKeyToEqualValueMap != null )
      {
        return false;
      }
    }
    else if ( !this.retainedKeyToEqualValueMap.equals( other.retainedKeyToEqualValueMap ) )
    {
      return false;
    }
    if ( this.retainedKeyToUnequalValuesMap == null )
    {
      if ( other.retainedKeyToUnequalValuesMap != null )
      {
        return false;
      }
    }
    else if ( !this.retainedKeyToUnequalValuesMap.equals( other.retainedKeyToUnequalValuesMap ) )
    {
      return false;
    }
    if ( this.setDelta == null )
    {
      if ( other.setDelta != null )
      {
        return false;
      }
    }
    else if ( !this.setDelta.equals( other.setDelta ) )
    {
      return false;
    }
    return true;
  }
  
}

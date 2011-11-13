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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link Map} adapter which allows to define a mapping for the keys of the underlying {@link Map}
 * 
 * @author Omnaest
 * @param <K>
 * @param <V>
 */
public class MapWithKeyMappingAdapter<KEY_TO, KEY_FROM, V> implements Map<KEY_TO, V>
{
  /* ********************************************** Variables ********************************************** */
  private Map<KEY_FROM, V>          map                                = null;
  private DualMap<KEY_FROM, KEY_TO> underlyingMapKeyToAdapterMapKeyMap = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param map
   * @param underlyingMapKeyToAdapterMapKeyMap
   *          : {@link DualMap} with a mapping from the keys of the given source map to the keys of the new created
   *          {@link MapWithKeyMappingAdapter}
   */
  public MapWithKeyMappingAdapter( Map<KEY_FROM, V> map, DualMap<KEY_FROM, KEY_TO> underlyingMapKeyToAdapterMapKeyMap )
  {
    super();
    this.map = map;
    this.underlyingMapKeyToAdapterMapKeyMap = underlyingMapKeyToAdapterMapKeyMap;
  }
  
  /**
   * Translates the key from the {@link MapWithKeyMappingAdapter} keys to the original {@link #map} keys
   * 
   * @param key
   * @return
   */
  @SuppressWarnings("unchecked")
  private KEY_FROM translateToUnderlyingMapKey( Object key )
  {
    //
    KEY_FROM retval = null;
    
    //
    try
    {
      //
      retval = this.underlyingMapKeyToAdapterMapKeyMap.invert().get( key );
      
      //
      if ( retval == null )
      {
        retval = (KEY_FROM) key;
      }
    }
    catch ( ClassCastException e )
    {
    }
    
    //
    return retval;
  }
  
  /**
   * Translates the key from the original {@link #map} to the {@link MapWithKeyMappingAdapter} keys
   * 
   * @param key
   * @return
   */
  @SuppressWarnings("unchecked")
  private KEY_TO translateToNewKey( KEY_FROM key )
  {
    //
    KEY_TO retval = this.underlyingMapKeyToAdapterMapKeyMap.get( key );
    
    if ( retval == null )
    {
      retval = (KEY_TO) key;
    }
    
    //
    return retval;
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
    return this.map.containsKey( this.translateToUnderlyingMapKey( key ) );
  }
  
  @Override
  public boolean containsValue( Object value )
  {
    return this.map.containsValue( value );
  }
  
  @Override
  public V get( Object key )
  {
    V retval = null;
    Object translatedKey = this.translateToUnderlyingMapKey( key );
    retval = this.map.get( translatedKey );
    return retval;
  }
  
  @Override
  public V put( KEY_TO key, V value )
  {
    return this.map.put( this.translateToUnderlyingMapKey( key ), value );
  }
  
  @Override
  public V remove( Object key )
  {
    return this.map.remove( this.translateToUnderlyingMapKey( key ) );
  }
  
  @Override
  public void putAll( Map<? extends KEY_TO, ? extends V> m )
  {
    this.putAll( m );
  }
  
  @Override
  public void clear()
  {
    this.map.clear();
  }
  
  @Override
  public Set<KEY_TO> keySet()
  {
    //
    Set<KEY_TO> retset = new LinkedHashSet<KEY_TO>();
    
    //
    for ( KEY_FROM key : this.map.keySet() )
    {
      //
      KEY_TO translatedKey = this.translateToNewKey( key );
      retset.add( translatedKey );
    }
    
    //
    return retset;
  }
  
  @Override
  public Collection<V> values()
  {
    return this.map.values();
  }
  
  @Override
  public Set<Entry<KEY_TO, V>> entrySet()
  {
    //
    Set<Entry<KEY_TO, V>> retset = new LinkedHashSet<Entry<KEY_TO, V>>();
    
    //
    for ( final Entry<KEY_FROM, V> entry : this.map.entrySet() )
    {
      retset.add( new Entry<KEY_TO, V>()
      {
        
        @Override
        public KEY_TO getKey()
        {
          return MapWithKeyMappingAdapter.this.translateToNewKey( entry.getKey() );
        }
        
        @Override
        public V getValue()
        {
          return entry.getValue();
        }
        
        @Override
        public V setValue( V value )
        {
          return entry.setValue( value );
        }
      } );
    }
    
    //
    return retset;
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
  
  @Override
  public String toString()
  {
    return String.format( "MapWithKeyMappingAdapter [map=%s, keyToKeyMap=%s]", this.map, this.underlyingMapKeyToAdapterMapKeyMap );
  }
  
}

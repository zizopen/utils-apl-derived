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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;

/**
 * {@link Map} implementation which is based on a given {@link List} of further {@link Map} instances. <br>
 * <br>
 * The {@link MapComposite} is thread safe itself, but to be fully thread safe the given further {@link Map} instances have to be
 * ,too.
 * 
 * @author Omnaest
 * @param <K>
 * @param <V>
 */
public class MapComposite<K, V> extends MapAbstract<K, V>
{
  /* ********************************************** Variables ********************************************** */
  private final List<Map<K, V>> mapList;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see MapComposite
   * @param mapList
   */
  public MapComposite( List<Map<K, V>> mapList )
  {
    super();
    this.mapList = new CopyOnWriteArrayList<Map<K, V>>( mapList );
  }
  
  /**
   * @see MapComposite
   * @param maps
   */
  public MapComposite( Map<K, V>... maps )
  {
    this( Arrays.asList( maps ) );
  }
  
  @Override
  public V get( Object key )
  {
    //
    V retval = null;
    
    //
    for ( Map<K, V> map : this.mapList )
    {
      //
      retval = map.get( key );
      
      //
      if ( retval != null )
      {
        break;
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public V put( K key, V value )
  {
    //
    final V retval = this.remove( key );
    
    //
    Map<K, V> currentMap = null;
    {
      //
      int currentMapSize = Integer.MAX_VALUE;
      for ( Map<K, V> map : this.mapList )
      {
        //
        int size = map.size();
        if ( size < currentMapSize )
        {
          currentMap = map;
          currentMapSize = size;
        }
      }
    }
    
    //
    if ( currentMap != null )
    {
      currentMap.put( key, value );
    }
    
    // 
    return retval;
  }
  
  @Override
  public V remove( Object key )
  {
    //    
    V retval = null;
    
    //
    for ( Map<K, V> map : this.mapList )
    {
      //
      V removedValue = map.remove( key );
      if ( removedValue != null )
      {
        retval = removedValue;
      }
    }
    
    // 
    return retval;
  }
  
  @Override
  public Set<K> keySet()
  {
    //     
    final ElementConverter<Map<K, V>, Set<K>> elementConverter = new ElementConverter<Map<K, V>, Set<K>>()
    {
      @Override
      public Set<K> convert( Map<K, V> element )
      {
        return element == null ? null : element.keySet();
      }
    };
    final List<Set<K>> keySetList = ListUtils.convert( this.mapList, elementConverter );
    return SetUtils.composite( keySetList );
  }
  
  @Override
  public Collection<V> values()
  {
    //     
    final ElementConverter<Map<K, V>, Collection<V>> elementConverter = new ElementConverter<Map<K, V>, Collection<V>>()
    {
      @Override
      public Collection<V> convert( Map<K, V> element )
      {
        return element == null ? null : element.values();
      }
    };
    final List<Collection<V>> keySetList = ListUtils.convert( this.mapList, elementConverter );
    return CollectionUtils.composite( keySetList );
  }
  
}

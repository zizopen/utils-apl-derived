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

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.reflection.ReflectionUtils;

/**
 * Builder for {@link Map} instances filled with keys and values.<br>
 * <br>
 * The {@link MapBuilder} is not thread safe, since it uses an temporary not thread safe internal {@link Map}.<br>
 * The order of elements is respected when put into the {@link Map}. A {@link Map} implementation which supports ordering will
 * contain the {@link Entry}s in the right order afterwards.<br>
 * <br>
 * Example:
 * 
 * <pre>
 * Map&lt;String, String&gt; map = new MapBuilder&lt;String, String&gt;().linkedHashMap().put( &quot;key1&quot;, &quot;value1&quot; ).put( &quot;key2&quot;, &quot;value2&quot; ).build()
 * </pre>
 * 
 * @see Map
 * @see SortedMap
 * @author Omnaest
 * @param <K>
 * @param <V>
 * @param <M>
 */
public class MapBuilder<K, V>
{
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Factory for a {@link Map} instance
   * 
   * @author Omnaest
   */
  public abstract static class MapFactory<K, V>
  {
    /**
     * Creates a new {@link Map} instance
     * 
     * @return
     */
    public abstract Map<K, V> newInstance();
  }
  
  /**
   * A {@link MapBuilder} which has a declared {@link Map} type and now allows to modify and build a {@link Map}
   * 
   * @see MapBuilder
   * @author Omnaest
   */
  public class MapBuilderWithMap
  {
    /* ********************************************** Variables ********************************************** */
    protected final Map<K, V>        map = new LinkedHashMap<K, V>();
    protected final MapFactory<K, V> mapFactory;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see MapBuilderWithMap
     * @param mapFactory
     */
    protected MapBuilderWithMap( MapFactory<K, V> mapFactory )
    {
      super();
      this.mapFactory = mapFactory;
    }
    
    /**
     * Builds a new instance of a {@link Map}.
     * 
     * @return
     */
    public <M extends Map<K, V>> M build()
    {
      //
      @SuppressWarnings("unchecked")
      M retmap = (M) this.mapFactory.newInstance();
      retmap.putAll( this.map );
      
      //
      return retmap;
    }
    
    /**
     * @see java.util.Map#size()
     * @return
     */
    public int size()
    {
      return this.map.size();
    }
    
    /**
     * @see java.util.Map#isEmpty()
     * @return
     */
    public boolean isEmpty()
    {
      return this.map.isEmpty();
    }
    
    /**
     * @see java.util.Map#containsKey(java.lang.Object)
     * @param key
     * @return
     */
    public boolean containsKey( Object key )
    {
      return this.map.containsKey( key );
    }
    
    /**
     * @see java.util.Map#containsValue(java.lang.Object)
     * @param value
     * @return
     */
    public boolean containsValue( Object value )
    {
      return this.map.containsValue( value );
    }
    
    /**
     * @see java.util.Map#get(java.lang.Object)
     * @param key
     * @return
     */
    public V get( Object key )
    {
      return this.map.get( key );
    }
    
    /**
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     * @param key
     * @param value
     * @return this
     */
    public MapBuilderWithMap put( K key, V value )
    {
      this.map.put( key, value );
      return this;
    }
    
    /**
     * @see java.util.Map#remove(java.lang.Object)
     * @param key
     * @return this
     */
    public MapBuilderWithMap remove( Object key )
    {
      this.map.remove( key );
      return this;
    }
    
    /**
     * @see java.util.Map#putAll(java.util.Map)
     * @param m
     * @return this
     */
    public MapBuilderWithMap putAll( Map<? extends K, ? extends V> m )
    {
      this.map.putAll( m );
      return this;
    }
    
    /**
     * @see java.util.Map#clear()
     * @return this
     */
    public MapBuilderWithMap clear()
    {
      this.map.clear();
      return this;
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see MapBuilder
   */
  public MapBuilder()
  {
    super();
  }
  
  /**
   * @see HashMap
   * @return {@link MapBuilderWithMap}
   */
  public MapBuilderWithMap hashMap()
  {
    //
    MapFactory<K, V> mapFactory = new MapFactory<K, V>()
    {
      @Override
      public Map<K, V> newInstance()
      {
        return new HashMap<K, V>();
      }
    };
    return new MapBuilderWithMap( mapFactory );
  }
  
  /**
   * @see LinkedHashMap
   * @return {@link MapBuilderWithMap}
   */
  public MapBuilderWithMap linkedHashMap()
  {
    return new MapBuilderWithMap( new MapFactory<K, V>()
    {
      @Override
      public Map<K, V> newInstance()
      {
        return new LinkedHashMap<K, V>();
      }
    } );
  }
  
  /**
   * @see ConcurrentHashMap
   * @return {@link MapBuilderWithMap}
   */
  public MapBuilderWithMap concurrentHashMap()
  {
    return new MapBuilderWithMap( new MapFactory<K, V>()
    {
      @Override
      public Map<K, V> newInstance()
      {
        return new ConcurrentHashMap<K, V>();
      }
    } );
  }
  
  /**
   * @see ConcurrentHashMap
   * @param initialCapacity
   * @return {@link MapBuilderWithMap}
   */
  public MapBuilderWithMap concurrentHashMap( final int initialCapacity )
  {
    return new MapBuilderWithMap( new MapFactory<K, V>()
    {
      @Override
      public Map<K, V> newInstance()
      {
        
        return new ConcurrentHashMap<K, V>( initialCapacity );
      }
    } );
  }
  
  /**
   * @see TreeMap
   * @see SortedMap
   * @return {@link MapBuilderWithMap}
   */
  public MapBuilderWithMap treeMap()
  {
    return new MapBuilderWithMap( new MapFactory<K, V>()
    {
      @Override
      public Map<K, V> newInstance()
      {
        return new TreeMap<K, V>();
      }
    } );
  }
  
  /**
   * @see TreeMap
   * @see SortedMap
   * @param comparator
   * @return {@link MapBuilderWithMap}
   */
  public MapBuilderWithMap treeMap( final Comparator<? super K> comparator )
  {
    return new MapBuilderWithMap( new MapFactory<K, V>()
    {
      @Override
      public Map<K, V> newInstance()
      {
        
        return new TreeMap<K, V>( comparator );
      }
    } );
  }
  
  /**
   * {@link MapBuilder} using a {@link MapFactory} to create a {@link Map} instance at {@link MapBuilderWithMap#build()} time
   * 
   * @see MapFactory
   * @see MapBuilder
   * @param mapFactory
   */
  public MapBuilderWithMap map( MapFactory<K, V> mapFactory )
  {
    //
    Assert.isNotNull( mapFactory, "MapFactory must not be null" );
    return new MapBuilderWithMap( mapFactory );
  }
  
  /**
   * Generic {@link MapBuilder} instance creator method which creates a {@link MapBuilder} with a {@link Map} instance of the
   * given {@link Class} by using reflection. The given {@link Map} implementation type has to have a default constructor or a
   * constructor which has the same parameter signature as the additionally given arguments.
   * 
   * @param mapType
   * @param arguments
   * @throws IllegalArgumentException
   *           if no map instance could be created
   * @return {@link MapBuilderWithMap}
   */
  public MapBuilderWithMap map( @SuppressWarnings("rawtypes") final Class<? extends Map> mapType, final Object... arguments )
  {
    //
    MapFactory<K, V> mapFactory = null;
    
    //
    if ( mapType != null && ReflectionUtils.hasConstructorFor( mapType, arguments ) )
    {
      mapFactory = new MapFactory<K, V>()
      {
        @SuppressWarnings("unchecked")
        @Override
        public Map<K, V> newInstance()
        {
          //
          Map<K, V> retmap = null;
          
          try
          {
            //
            retmap = ReflectionUtils.createInstanceOf( mapType, arguments );
          }
          catch ( Exception e )
          {
          }
          
          //
          return retmap;
        }
      };
    }
    
    //
    Assert.isNotNull( mapFactory, "The given type of Map does not have a constructor for the given arguments." );
    return new MapBuilderWithMap( mapFactory );
  }
  
}

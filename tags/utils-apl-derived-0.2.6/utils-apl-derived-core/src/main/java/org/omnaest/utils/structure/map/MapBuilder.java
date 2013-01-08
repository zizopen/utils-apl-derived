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

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.reflection.ReflectionUtils;

/**
 * Builder for {@link Map} instances filled with keys and values.<br>
 * <br>
 * The {@link MapBuilder} is not thread safe, since it uses an temporary not thread safe internal {@link Map}.<br>
 * The order of elements is respected when put into the result {@link Map}, so a {@link Map} implementation which supports
 * ordering will contain the {@link Entry}s in the right order afterwards.<br>
 * <br>
 * Example:
 * 
 * <pre>
 * Map&lt;String, String&gt; map = MapUtils.builder()
 *                                   .put( &quot;key1&quot;, &quot;value1&quot; )
 *                                   .put( &quot;key2&quot;, &quot;value2&quot; )
 *                                   .put( &quot;key3&quot;, &quot;value3&quot; )
 *                                   .buildAs()
 *                                   .linkedHashMap();
 * </pre>
 * 
 * @see Map
 * @see SortedMap
 * @author Omnaest
 */
public class MapBuilder
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private MapRootComposer mapRootComposer = new MapRootComposerImpl();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Factory for a {@link Map} instance
   * 
   * @author Omnaest
   */
  public static interface MapFactory<M extends Map<K, V>, K, V>
  {
    /**
     * Creates a new {@link Map} instance
     * 
     * @return
     */
    public M newInstance();
  }
  
  /**
   * See {@link MapComposer}
   * 
   * @see #put(Object, Object)
   * @see #putAll(Map)
   * @author Omnaest
   */
  public static interface MapRootComposer
  {
    /**
     * @see MapComposer#put(Object, Object)
     * @param key
     * @param value
     * @return
     */
    public <K, V> MapComposer<K, V> put( K key, V value );
    
    /**
     * @see MapComposer#putAll(Map)
     * @param map
     * @return
     */
    public <K, V> MapComposer<K, V> putAll( Map<? extends K, ? extends V> map );
    
  }
  
  /**
   * A {@link MapComposer} allows to compose a {@link Map} by adding key value pairs.<br>
   * <br>
   * A {@link MapComposer} is closed by calling {@link #buildAs()} where a {@link ClosedMapComposer} allows to create a specific
   * {@link Map} instance.
   * 
   * @author Omnaest
   * @param <K>
   * @param <V>
   */
  
  public static interface MapComposer<K, V>
  {
    /**
     * @param key
     * @param value
     * @return this
     */
    public MapComposer<K, V> put( K key, V value );
    
    /**
     * @see #put(Object, Object)
     * @param map
     *          {@link Map}
     * @return this
     */
    public MapComposer<K, V> putAll( Map<? extends K, ? extends V> map );
    
    /**
     * @see ClosedMapComposer
     * @return
     */
    public ClosedMapComposer<K, V> buildAs();
  }
  
  /**
   * A {@link ClosedMapComposer} allows to create specific {@link Map} instances containing the key value pairs defined with the
   * {@link MapComposer}
   * 
   * @author Omnaest
   * @param <K>
   * @param <V>
   */
  public static interface ClosedMapComposer<K, V>
  {
    
    public SortedMap<K, V> treeMap();
    
    public SortedMap<K, V> treeMap( final Comparator<? super K> comparator );
    
    public SortedMap<K, V> concurrentSkipListMap();
    
    public SortedMap<K, V> concurrentSkipListMap( final Comparator<? super K> comparator );
    
    public Map<K, V> hashMap();
    
    public Map<K, V> linkedHashMap();
    
    public Map<K, V> concurrentHashMap();
    
    public Map<K, V> concurrentHashMap( int initialCapacity );
    
    public <M extends Map<K, V>> M map( MapFactory<M, K, V> mapFactory );
    
    public <M extends Map<K, V>> M map( Class<M> mapType, Object... arguments );
    
  }
  
  /**
   * @see MapRootComposer
   * @author Omnaest
   */
  private static class MapRootComposerImpl implements MapRootComposer
  {
    @Override
    public <K, V> MapComposer<K, V> put( K key, V value )
    {
      return new MapComposerImpl<K, V>( key, value );
    }
    
    @Override
    public <K, V> MapComposer<K, V> putAll( Map<? extends K, ? extends V> map )
    {
      final LinkedHashMap<K, V> initialMap = map != null ? new LinkedHashMap<K, V>( map ) : new LinkedHashMap<K, V>();
      return new MapComposerImpl<K, V>( initialMap );
    }
  }
  
  /**
   * @see MapComposer
   * @author Omnaest
   * @param <K>
   * @param <V>
   */
  private static class MapComposerImpl<K, V> implements MapComposer<K, V>
  {
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private final Map<K, V> map;
    
    /* *************************************************** Methods **************************************************** */
    
    /**
     * @see MapComposerImpl
     * @param key
     * @param value
     */
    private MapComposerImpl( K key, V value )
    {
      super();
      this.map = new LinkedHashMap<K, V>();
      this.map.put( key, value );
    }
    
    /**
     * @see MapComposerImpl
     * @param map
     */
    private MapComposerImpl( Map<K, V> map )
    {
      super();
      this.map = map;
    }
    
    @Override
    public MapComposer<K, V> put( K key, V value )
    {
      this.map.put( key, value );
      return this;
    }
    
    @Override
    public MapComposer<K, V> putAll( Map<? extends K, ? extends V> map )
    {
      if ( map != null )
      {
        this.map.putAll( map );
      }
      return this;
    }
    
    @Override
    public ClosedMapComposer<K, V> buildAs()
    {
      return new CloseMapComposerImpl<K, V>( this.map );
    }
    
  }
  
  /**
   * @see ClosedMapComposer
   * @author Omnaest
   * @param <K>
   * @param <V>
   */
  private static class CloseMapComposerImpl<K, V> implements ClosedMapComposer<K, V>
  {
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private final Map<K, V> map;
    
    /* *************************************************** Methods **************************************************** */
    
    /**
     * @see CloseMapComposerImpl
     * @param map
     */
    private CloseMapComposerImpl( Map<K, V> map )
    {
      super();
      this.map = map;
    }
    
    @Override
    public SortedMap<K, V> treeMap()
    {
      return new TreeMap<K, V>( this.map );
    }
    
    @Override
    public SortedMap<K, V> treeMap( Comparator<? super K> comparator )
    {
      final SortedMap<K, V> retmap = new TreeMap<K, V>( comparator );
      retmap.putAll( this.map );
      return retmap;
    }
    
    @Override
    public SortedMap<K, V> concurrentSkipListMap()
    {
      return new ConcurrentSkipListMap<K, V>( this.map );
    }
    
    @Override
    public SortedMap<K, V> concurrentSkipListMap( Comparator<? super K> comparator )
    {
      final SortedMap<K, V> retmap = new ConcurrentSkipListMap<K, V>( comparator );
      retmap.putAll( this.map );
      return retmap;
    }
    
    @Override
    public Map<K, V> hashMap()
    {
      return new HashMap<K, V>( this.map );
    }
    
    @Override
    public Map<K, V> linkedHashMap()
    {
      return new LinkedHashMap<K, V>( this.map );
    }
    
    @Override
    public Map<K, V> concurrentHashMap()
    {
      return new ConcurrentHashMap<K, V>( this.map );
    }
    
    @Override
    public Map<K, V> concurrentHashMap( int initialCapacity )
    {
      final Map<K, V> retmap = new ConcurrentHashMap<K, V>( initialCapacity );
      retmap.putAll( this.map );
      return retmap;
    }
    
    @Override
    public <M extends Map<K, V>> M map( MapFactory<M, K, V> mapFactory )
    {
      Assert.isNotNull( mapFactory, "MapFactory must not be null" );
      
      final M retmap = mapFactory.newInstance();
      if ( retmap != null )
      {
        retmap.putAll( this.map );
      }
      return retmap;
    }
    
    @Override
    public <M extends Map<K, V>> M map( final Class<M> mapType, final Object... arguments )
    {
      Assert.isNotNull( mapType, "mapType must not be null" );
      Assert.isTrue( ReflectionUtils.hasConstructorFor( mapType, arguments ),
                     "No constructor available for the given arguments.[" + String.valueOf( mapType ) + "("
                         + Arrays.toString( arguments ) + ")]" );
      
      final MapFactory<M, K, V> mapFactory = new MapFactory<M, K, V>()
      {
        @SuppressWarnings("unchecked")
        @Override
        public M newInstance()
        {
          Map<K, V> retmap = null;
          try
          {
            retmap = ReflectionUtils.newInstanceOf( mapType, arguments );
          }
          catch ( Exception e )
          {
            Assert.fails( "The map factory failed to create an instance of a map", e );
          }
          return (M) retmap;
        }
      };
      return this.map( mapFactory );
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
   * @see MapComposer#put(Object, Object)
   * @param key
   * @param value
   * @return {@link MapComposer} instance
   */
  public <K, V> MapComposer<K, V> put( K key, V value )
  {
    return this.mapRootComposer.put( key, value );
  }
  
  /**
   * @see MapComposer#putAll(Map)
   * @param map
   * @return {@link MapComposer} instance
   */
  public <K, V> MapComposer<K, V> putAll( Map<? extends K, ? extends V> map )
  {
    return this.mapRootComposer.putAll( map );
  }
  
}

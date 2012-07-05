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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.element.KeyExtractor;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverter;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterElementToMapEntry;
import org.omnaest.utils.structure.element.converter.ElementConverterIdentity;
import org.omnaest.utils.structure.element.factory.Factory;
import org.omnaest.utils.structure.element.factory.FactoryParameterized;
import org.omnaest.utils.structure.element.filter.ElementFilter;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.structure.map.adapter.MapToMapAdapter;
import org.omnaest.utils.structure.map.adapter.SortedMapToSortedMapAdapter;
import org.omnaest.utils.structure.map.decorator.LockingMapDecorator;
import org.omnaest.utils.structure.map.decorator.MapDecorator;
import org.omnaest.utils.structure.map.decorator.SortedMapDecorator;
import org.omnaest.utils.tuple.TupleTwo;

/**
 * Helper class for {@link Map} operations.
 * 
 * @author Omnaest
 */
public class MapUtils
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * A {@link MapElementMergeOperation} defines a {@link #merge(Object, Object, Map)} operation to merge {@link Map} elements into
   * a merged {@link Map} instance.
   * 
   * @author Omnaest
   * @param <K>
   * @param <V>
   */
  public static interface MapElementMergeOperation<K, V>
  {
    /**
     * Merge operation, which should implement the logic which merges the given key and value pair into the given mergedMap.
     * 
     * @param key
     * @param value
     * @param mergedMap
     */
    public void merge( K key, V value, Map<K, V> mergedMap );
  }
  
  /**
   * Converted to transform a given {@link Entry} of a {@link Map} to a single {@link List} element
   * 
   * @see #convert(Entry)
   * @author Omnaest
   * @param <TO>
   * @param <K>
   * @param <V>
   */
  public static interface MapEntryToElementConverter<TO, K, V> extends ElementConverter<Entry<K, V>, TO>
  {
    /**
     * Converts a {@link Entry} of a {@link Map} to a single element for a {@link List}
     * 
     * @param entry
     * @return
     */
    @Override
    public TO convert( Entry<K, V> entry );
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Merges all given {@link Map} instances into a single {@link LinkedHashMap} using the given {@link MapElementMergeOperation}.
   * 
   * @see #mergeAll(Map...)
   * @see #mergeAll(Collection, MapElementMergeOperation)
   * @param <K>
   * @param <V>
   * @param mapElementMergeOperation
   * @param maps
   * @return
   */
  public static <K, V> Map<K, V> mergeAll( MapElementMergeOperation<K, V> mapElementMergeOperation,
                                           Map<? extends K, ? extends V>... maps )
  {
    return MapUtils.mergeAll( Arrays.asList( maps ), mapElementMergeOperation );
  }
  
  /**
   * Merges all given {@link Map} instances into a single {@link LinkedHashMap} using the given {@link MapElementMergeOperation}.
   * 
   * @see #mergeAll(Map...)
   * @param <K>
   * @param <V>
   * @param mapCollection
   * @param mapElementMergeOperation
   * @return
   */
  public static <K, V> Map<K, V> mergeAll( Collection<Map<? extends K, ? extends V>> mapCollection,
                                           MapElementMergeOperation<K, V> mapElementMergeOperation )
  {
    //
    final Map<K, V> retmap = new LinkedHashMap<K, V>();
    
    //
    if ( mapCollection != null && mapElementMergeOperation != null )
    {
      for ( Map<? extends K, ? extends V> map : mapCollection )
      {
        if ( map != null )
        {
          for ( K key : map.keySet() )
          {
            //
            V value = map.get( key );
            
            //
            mapElementMergeOperation.merge( key, value, retmap );
          }
        }
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Merges all given {@link Map} instances into a single {@link LinkedHashMap}.
   * 
   * @see #mergeAll(Map...)
   * @param <K>
   * @param <V>
   * @param mapCollection
   * @return
   */
  public static <K, V> Map<K, V> mergeAll( Collection<Map<K, V>> mapCollection )
  {
    //
    Map<K, V> retmap = new LinkedHashMap<K, V>();
    
    //
    for ( Map<K, V> map : mapCollection )
    {
      retmap.putAll( map );
    }
    
    //
    return retmap;
  }
  
  /**
   * Merges all given {@link Map} instances into a single {@link LinkedHashMap}.
   * 
   * @see #mergeAll(Collection)
   * @see #mergeAll(Collection, MapElementMergeOperation)
   * @param <K>
   * @param <V>
   * @param maps
   * @return
   */
  public static <K, V> Map<K, V> mergeAll( Map<K, V>... maps )
  {
    return MapUtils.mergeAll( Arrays.asList( maps ) );
  }
  
  /**
   * Returns a {@link Map} with the matching keys and the respective value instances within a of {@link TupleTwo} wrapper, which
   * has always the value of the first map and the value of the second map. Only the keys which are contained in both {@link Map}s
   * will be returned.
   * 
   * @param <K>
   * @param <VA>
   * @param <VB>
   * @param mapA
   * @param mapB
   * @return
   */
  public static <K, VA, VB> Map<K, TupleTwo<VA, VB>> innerJoinMapByKey( Map<K, VA> mapA, Map<K, VB> mapB )
  {
    //
    final Map<K, TupleTwo<VA, VB>> retmap = new LinkedHashMap<K, TupleTwo<VA, VB>>();
    
    //
    if ( mapA != null && mapB != null )
    {
      //
      for ( K key : mapA.keySet() )
      {
        if ( mapB.containsKey( key ) )
        {
          //
          VA valueA = mapA.get( key );
          VB valueB = mapB.get( key );
          
          //
          retmap.put( key, new TupleTwo<VA, VB>( valueA, valueB ) );
        }
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Returns a new {@link MapJoiner} instance
   * 
   * @return
   */
  public static MapJoiner joiner()
  {
    return new MapJoiner();
  }
  
  /**
   * Returns a new {@link Map} instance with converted keys using the given {@link ElementConverter}
   * 
   * @see #convertMap(Map, ElementConverter, ElementConverter)
   * @see #convertMapValue(Map, ElementConverter)
   * @param <KeyFrom>
   * @param <KeyTo>
   * @param <Value>
   * @param map
   * @param keyElementConverter
   * @return new ordered {@link Map}
   */
  public static <KeyFrom, KeyTo, Value> Map<KeyTo, Value> convertMapKey( Map<? extends KeyFrom, ? extends Value> map,
                                                                         ElementConverter<KeyFrom, KeyTo> keyElementConverter )
  {
    return MapUtils.convertMap( map, keyElementConverter, new ElementConverterIdentity<Value>() );
  }
  
  /**
   * Returns a new {@link Map} instance with all values converted using the given {@link ElementConverter}
   * 
   * @see #convertMapKey(Map, ElementConverter)
   * @see #convertMap(Map, ElementConverter, ElementConverter)
   * @param <Key>
   * @param <ValueFrom>
   * @param <ValueTo>
   * @param map
   * @param valueElementConverter
   * @return new ordered {@link Map}
   */
  public static <Key, ValueFrom, ValueTo> Map<Key, ValueTo> convertMapValue( Map<? extends Key, ? extends ValueFrom> map,
                                                                             ElementConverter<ValueFrom, ValueTo> valueElementConverter )
  {
    return MapUtils.convertMap( map, new ElementConverterIdentity<Key>(), valueElementConverter );
  }
  
  /**
   * Returns a new {@link Map} instance with converted keys and values using the given {@link ElementConverter}s
   * 
   * @see #convertMapKey(Map, ElementConverter)
   * @see #convertMapValue(Map, ElementConverter)
   * @see ElementConverter
   * @param map
   * @param keyElementConverter
   * @param valueElementConverter
   * @return new ordered {@link Map}
   */
  public static <KeyFrom, KeyTo, ValueFrom, ValueTo> Map<KeyTo, ValueTo> convertMap( Map<? extends KeyFrom, ? extends ValueFrom> map,
                                                                                     ElementConverter<KeyFrom, KeyTo> keyElementConverter,
                                                                                     ElementConverter<ValueFrom, ValueTo> valueElementConverter )
  {
    //
    Map<KeyTo, ValueTo> retmap = null;
    
    //
    if ( map != null && keyElementConverter != null && valueElementConverter != null )
    {
      //
      retmap = new LinkedHashMap<KeyTo, ValueTo>( map.size() );
      
      //
      for ( KeyFrom keyFrom : map.keySet() )
      {
        KeyTo keyTo = keyElementConverter.convert( keyFrom );
        ValueTo valueTo = valueElementConverter.convert( map.get( keyFrom ) );
        
        retmap.put( keyTo, valueTo );
      }
      
    }
    
    //
    return retmap;
  }
  
  /**
   * Similar to {@link #convertMap(Map, ElementConverter, ElementConverter)} using a single {@link ElementConverter} for an
   * {@link Entry}
   * 
   * @param map
   *          {@link Map}
   * @param entryElementConverter
   *          {@link ElementConverter}
   * @return new {@link Map} instance
   */
  @SuppressWarnings("unchecked")
  public static <KeyFrom, KeyTo, ValueFrom, ValueTo> Map<KeyTo, ValueTo> convertMap( Map<? extends KeyFrom, ? extends ValueFrom> map,
                                                                                     ElementConverter<Entry<KeyFrom, ValueFrom>, Entry<KeyTo, ValueTo>> entryElementConverter )
  {
    //
    Map<KeyTo, ValueTo> retmap = null;
    
    //
    if ( map != null && entryElementConverter != null )
    {
      //
      retmap = new LinkedHashMap<KeyTo, ValueTo>( map.size() );
      
      //
      for ( Entry<? extends KeyFrom, ? extends ValueFrom> entry : map.entrySet() )
      {
        //
        Entry<KeyTo, ValueTo> convertedEntry = entryElementConverter.convert( (Entry<KeyFrom, ValueFrom>) entry );
        
        final KeyTo keyTo = convertedEntry.getKey();
        final ValueTo valueTo = convertedEntry.getValue();
        retmap.put( keyTo, valueTo );
      }
      
    }
    
    //
    return retmap;
  }
  
  /**
   * Similar to {@link #printMapHierarchical(PrintStream, Map)} but returning a {@link String} value
   * 
   * @param map
   * @return
   */
  public static <K, V> String toStringUsingHierarchy( Map<K, V> map )
  {
    //
    final ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    
    //
    final PrintStream printStream = byteArrayContainer.getPrintStreamWriter();
    printMapHierarchical( printStream, map );
    
    //
    return byteArrayContainer.toString();
  }
  
  /**
   * Returns a {@link String} representation for a {@link Map}
   * 
   * @param map
   * @return
   */
  public static <K, V> String toString( Map<K, V> map )
  {
    //
    StringBuilder retval = new StringBuilder();
    
    //
    if ( map != null )
    {
      //
      retval.append( "[\n" );
      
      //
      Iterator<Entry<K, V>> iterator = map.entrySet().iterator();
      while ( iterator != null && iterator.hasNext() )
      {
        try
        {
          Entry<K, V> entry = iterator.next();
          retval.append( "  " + entry.toString() + "\n" );
        }
        catch ( Exception e )
        {
        }
      }
      
      //
      retval.append( "]" );
    }
    
    //
    return retval.toString();
  }
  
  /**
   * Transforms a given {@link Map} to a {@link List} using the given {@link MapEntryToElementConverter} to create single elements
   * for the {@link List} based on the {@link Entry}s of the given {@link Map}
   * 
   * @param map
   * @param mapEntryToElementConverter
   * @return {@link List}
   */
  public static <TO, K, V> List<TO> toList( Map<K, V> map, MapEntryToElementConverter<TO, K, V> mapEntryToElementConverter )
  {
    //
    List<TO> retlist = new ArrayList<TO>();
    
    //
    if ( map != null && mapEntryToElementConverter != null )
    {
      for ( Entry<K, V> entry : map.entrySet() )
      {
        retlist.add( mapEntryToElementConverter.convert( entry ) );
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns a new filtered {@link Map} instance. All keys of the given {@link Map} are filtered using the given
   * {@link ElementFilter}
   * 
   * @see #filteredMap(Map, Iterable)
   * @param map
   * @param keyElementFilter
   * @return
   */
  public static <K, V> Map<K, V> filteredMap( Map<K, V> map, ElementFilter<K> keyElementFilter )
  {
    Set<K> filterKeySet = SetUtils.filter( map.keySet(), keyElementFilter );
    return filteredMap( map, filterKeySet );
  }
  
  /**
   * Filters a given {@link Map} by its keys. Only keys which are contained within the given key {@link Iterable} will be
   * returned.
   * 
   * @see #filteredMap(Map, ElementFilter)
   * @param map
   * @param filterKeyIterable
   * @return new {@link LinkedHashMap} instance
   */
  public static <K, V> Map<K, V> filteredMap( Map<K, V> map, Iterable<K> filterKeyIterable )
  {
    //
    Map<K, V> retmap = new LinkedHashMap<K, V>();
    
    //
    if ( map != null && filterKeyIterable != null )
    {
      //
      for ( K key : filterKeyIterable )
      {
        //
        if ( map.containsKey( key ) )
        {
          retmap.put( key, map.get( key ) );
        }
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Filters a given {@link Map} by values which are null. Only keys which have a value not null will be retained.
   * 
   * @param map
   * @return new {@link LinkedHashMap} instance
   */
  public static <K, V> Map<K, V> filteredMapExcludingNullValues( Map<K, V> map )
  {
    //
    Map<K, V> retmap = new LinkedHashMap<K, V>();
    
    //
    if ( map != null )
    {
      //
      for ( Entry<K, V> entry : map.entrySet() )
      {
        if ( entry.getValue() != null )
        {
          retmap.put( entry.getKey(), entry.getValue() );
        }
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Prints out a given {@link Map} using {@link String#valueOf(Object)} and all submaps indented to a new column.<br>
   * <br>
   * Example:<br>
   * 
   * <pre>
   * -+
   *  |-- valueDouble=1.234
   *  |-+ testClassCopy
   *  | |-- valueDouble=5.678
   *  | |-- testClassCopy=null
   *  | |-- privateField=privateValue0.16433438667207334
   *  | |-+ future
   *  | | |-- countDownLatch=java.util.concurrent.CountDownLatch@1f4384c2[Count = 1]
   *  | | |-- shouldCancel=false
   *  | | |-- isCancelled=false
   *  | | |-- value=null
   *  | | |-- clazz=org.omnaest.utils.structure.element.FutureSimple
   * </pre>
   * 
   * @param printStream
   * @param map
   */
  public static void printMapHierarchical( final PrintStream printStream, @SuppressWarnings("rawtypes") Map map )
  {
    //
    class MapPrinter
    {
      @SuppressWarnings("rawtypes")
      public void printMap( Map map, int indentation )
      {
        if ( map != null )
        {
          String indentationString = StringUtils.repeat( " |", indentation / 2 );
          printStream.append( indentation == 0 ? indentationString + "-+\n" : "" );
          for ( Object key : map.keySet() )
          {
            //
            Object value = map.get( key );
            
            //
            if ( value instanceof Map )
            {
              //
              printStream.append( indentationString + " |-+ " + String.valueOf( key ) + "\n" );
              this.printMap( (Map) value, indentation + 2 );
            }
            else
            {
              //
              printStream.append( indentationString + " |-- " + String.valueOf( key ) + "=" + String.valueOf( map.get( key ) )
                                  + "\n" );
            }
          }
          printStream.append( indentationString + "\n" );
        }
      }
    }
    
    //
    new MapPrinter().printMap( map, 0 );
  }
  
  /**
   * Returns a view of the given {@link Map} using the given {@link Lock} to synchronize all of its methods
   * 
   * @see #lockedByReentrantLock(Map)
   * @param map
   * @param lock
   * @return
   */
  public static <K, V> Map<K, V> locked( Map<K, V> map, Lock lock )
  {
    return new LockingMapDecorator<K, V>( map, lock );
  }
  
  /**
   * Returns a view of the given {@link Map} using a new {@link ReentrantLock} instance to synchronize all of its methods
   * 
   * @see #locked(Map, Lock)
   * @param map
   * @return
   */
  public static <K, V> Map<K, V> lockedByReentrantLock( Map<K, V> map )
  {
    Lock lock = new ReentrantLock();
    return locked( map, lock );
  }
  
  /**
   * Returns the inverted {@link Map} for the given one.<br>
   * <br>
   * The given {@link Map} has to be <b>bidirectional</b>, which means that the key value pairs inverted into value to key pairs
   * have unique values, too.<br>
   * <br>
   * If the given {@link Map} reference is null, null is returned.<br>
   * <br>
   * For non bidirectional {@link Map}s use {@link #invert(Map)} instead. <br>
   * <br>
   * If a given {@link Map} has some non unique values the first occurring value wins and all further values are dropped.
   * 
   * @param map
   * @return {@link LinkedHashMap}
   */
  public static <K, V> Map<V, K> invertedBidirectionalMap( final Map<? extends K, ? extends V> map )
  {
    //
    final Map<V, K> retmap = map == null ? null : new LinkedHashMap<V, K>();
    
    //
    if ( retmap != null )
    {
      //
      for ( Entry<? extends K, ? extends V> entry : map.entrySet() )
      {
        //
        final K key = entry.getKey();
        final V value = entry.getValue();
        
        //
        if ( !retmap.containsKey( value ) )
        {
          retmap.put( value, key );
        }
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Returns the inverted {@link Map} for the given one. If the given {@link Map} reference is null, null is returned.<br>
   * <br>
   * Since Multiple key of the original {@link Map} can be mapped to the same value, the inverted {@link Map} will contain a
   * {@link Set} of original key values per original value. <br>
   * <br>
   * For bidirectional {@link Map}s consider using {@link #invertedBidirectionalMap(Map)}
   * 
   * @param map
   * @return {@link LinkedHashMap} and {@link LinkedHashSet}
   */
  public static <K, V> Map<V, Set<K>> invert( final Map<? extends K, ? extends V> map )
  {
    //
    final Map<V, Set<K>> retmap = map == null ? null : new LinkedHashMap<V, Set<K>>();
    
    //
    if ( retmap != null )
    {
      //
      for ( Entry<? extends K, ? extends V> entry : map.entrySet() )
      {
        //
        final K key = entry.getKey();
        final V value = entry.getValue();
        
        //
        if ( !retmap.containsKey( value ) )
        {
          retmap.put( value, new LinkedHashSet<K>() );
        }
        
        //
        retmap.get( value ).add( key );
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Returns an {@link EnumMap} filled with all available values of the given {@link Enum} type as keys and the result of the
   * {@link Factory} as value for each {@link Enum} key.
   * 
   * @param enumType
   * @param factory
   * @return {@link EnumMap}
   */
  public static <K extends Enum<K>, V> EnumMap<K, V> initializedEnumMap( Class<K> enumType, Factory<V> factory )
  {
    //    
    final EnumMap<K, V> retmap = enumType != null ? new EnumMap<K, V>( enumType ) : null;
    
    //
    if ( retmap != null )
    {
      for ( K key : EnumUtils.getEnumList( enumType ) )
      {
        V value = factory != null ? factory.newInstance() : null;
        retmap.put( key, value );
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Similar to {@link #initializeMap(Map, Iterable, Factory, boolean)} but does not overwrite values of already existing keys.
   * 
   * @param map
   * @param keyIterable
   * @param valueFactory
   */
  public static <K, V> void initializeMap( Map<K, V> map, Iterable<K> keyIterable, Factory<V> valueFactory )
  {
    boolean overwriteValuesOfExistingKeys = false;
    initializeMap( map, keyIterable, valueFactory, overwriteValuesOfExistingKeys );
  }
  
  /**
   * Initializes the given {@link Map} for all keys from the key {@link Iterable} with values created by the value
   * {@link Factory#newInstance()} method. If the overwrite values of existing keys flag is set to true, any value of an already
   * existing key is overwritten.
   * 
   * @see #initializedMap(Map, Factory)
   * @param map
   * @param keyIterable
   * @param valueFactory
   * @param overwriteValuesOfExistingKeys
   */
  public static <K, V> void initializeMap( Map<K, V> map,
                                           Iterable<K> keyIterable,
                                           Factory<V> valueFactory,
                                           boolean overwriteValuesOfExistingKeys )
  {
    //
    if ( map != null && keyIterable != null && valueFactory != null )
    {
      //
      final Set<K> keySet = SetUtils.valueOf( keyIterable );
      if ( !overwriteValuesOfExistingKeys )
      {
        keySet.removeAll( map.keySet() );
      }
      
      //
      for ( K key : keySet )
      {
        map.put( key, valueFactory.newInstance() );
      }
    }
  }
  
  /**
   * Similar to {@link #initializedMap(Map, Factory)} using a new {@link LinkedHashMap} instance
   * 
   * @param valueFactory
   * @return
   */
  public static <K, V> Map<K, V> initializedMap( final Factory<V> valueFactory )
  {
    Map<K, V> map = new LinkedHashMap<K, V>();
    return initializedMap( map, valueFactory );
  }
  
  /**
   * Similar to {@link #initializedMap(Map, FactoryParameterized)} using a new {@link LinkedHashMap} instance
   * 
   * @param valueFactory
   * @return
   */
  public static <K, V> Map<K, V> initializedMap( final FactoryParameterized<V, K> valueFactory )
  {
    Map<K, V> map = new LinkedHashMap<K, V>();
    return initializedMap( map, valueFactory );
  }
  
  /**
   * Returns a {@link MapDecorator} which ensures that all {@link Map#get(Object)} invocations with a valid key type will return a
   * value. If the underlying {@link Map} would return a null value the value {@link Factory#newInstance()} is invoked and the new
   * value is stored within the {@link Map}.<br>
   * <br>
   * This is e.g. useful for scenarios where a {@link Map} contains a {@link Collection} as value and the {@link Collection}
   * should always be present.<br>
   * <br>
   * Be aware of the fact, that {@link Map#containsKey(Object)} will still return false for any non existing key.
   * 
   * @see #initializeMap(Map, Iterable, Factory)
   * @param map
   * @param valueFactory
   * @return {@link Map} decorator
   */
  public static <K, V> Map<K, V> initializedMap( Map<K, V> map, final Factory<V> valueFactory )
  {
    Assert.isNotNull( valueFactory, "Factory must be not null" );
    Assert.isNotNull( map, "Map must be not null" );
    return new MapDecorator<K, V>( map )
    {
      @SuppressWarnings("unchecked")
      @Override
      public V get( Object key )
      {
        //
        V value = super.get( key );
        
        //
        if ( value == null )
        {
          value = valueFactory.newInstance();
          this.put( (K) key, value );
        }
        
        //
        return value;
      }
      
    };
  }
  
  /**
   * Returns a {@link MapDecorator} which ensures that all {@link Map#get(Object)} invocations with a valid key type will return a
   * value. If the underlying {@link Map} would return a null value the value {@link FactoryParameterized#newInstance(Object)}
   * with the key as argument is invoked and the new value is stored within the {@link Map}.<br>
   * <br>
   * This is e.g. useful for scenarios where a {@link Map} contains a {@link Collection} as value and the {@link Collection}
   * should always be present.<br>
   * <br>
   * Be aware of the fact, that {@link Map#containsKey(Object)} will still return false for any non existing key.
   * 
   * @see #initializeMap(Map, Iterable, Factory)
   * @see #initializedMap(Map, Factory)
   * @param map
   * @param valueFactory
   *          {@link FactoryParameterized}
   * @return {@link Map} decorator
   */
  public static <K, V> Map<K, V> initializedMap( Map<K, V> map, final FactoryParameterized<V, K> valueFactory )
  {
    Assert.isNotNull( valueFactory, "Factory must be not null" );
    Assert.isNotNull( map, "Map must be not null" );
    return new MapDecorator<K, V>( map )
    {
      @SuppressWarnings("unchecked")
      @Override
      public V get( Object keyObject )
      {
        //
        V value = super.get( keyObject );
        
        //
        if ( value == null )
        {
          //
          final K key = (K) keyObject;
          value = valueFactory.newInstance( key );
          this.put( key, value );
        }
        
        //
        return value;
      }
      
    };
  }
  
  /**
   * Similar to {@link #initializedMap(Map, Factory)} but for any {@link SortedMap} instance
   * 
   * @param sortedMap
   * @param valueFactory
   * @return {@link SortedMap} decorator
   */
  public static <K, V> SortedMap<K, V> initializedSortedMap( SortedMap<K, V> sortedMap, final Factory<V> valueFactory )
  {
    Assert.isNotNull( valueFactory, "Factory must be not null" );
    Assert.isNotNull( sortedMap, "Map must be not null" );
    return new SortedMapDecorator<K, V>( sortedMap )
    {
      @SuppressWarnings("unchecked")
      @Override
      public V get( Object key )
      {
        //
        V value = super.get( key );
        
        //
        if ( value == null )
        {
          value = valueFactory.newInstance();
          this.put( (K) key, value );
        }
        
        //
        return value;
      }
    };
  }
  
  /**
   * Returns the value of the first key within a {@link Map} which matches a given regular expression.<br>
   * <br>
   * 
   * <pre>
   * MapUtils.getValueByRegex( null, anyRegex ) = null
   * MapUtils.getValueByRegex( map, null ) = null
   * MapUtils.getValueByRegex( map, regex ) = value for first key matching the regex
   * MapUtils.getValueByRegex( map, ".*" ) = first value of the map for {@link LinkedHashMap}, random value for {@link HashMap}
   * 
   * </pre>
   * 
   * <br>
   * Example:
   * 
   * <pre>
   * //
   * final Map&lt;String, String&gt; map = new LinkedHashMap&lt;String, String&gt;();
   * map.put( &quot;key1&quot;, &quot;value1&quot; );
   * map.put( &quot;key2&quot;, &quot;value2&quot; );
   * map.put( &quot;thisKey&quot;, &quot;value3&quot; );
   * 
   * //
   * String regex = &quot;this.*&quot;;
   * String value = MapUtils.getValueByRegex( map, regex );
   * 
   * //
   * assertEquals( &quot;value3&quot;, value );
   * </pre>
   * 
   * @param map
   * @param regex
   * @return
   */
  public static <E> E getValueByRegex( Map<String, ? extends E> map, String regex )
  {
    //
    E retval = null;
    
    //
    if ( map != null )
    {
      //
      retval = map.get( regex );
      
      //
      if ( retval == null )
      {
        //
        for ( String key : map.keySet() )
        {
          if ( key != null && key.matches( regex ) )
          {
            retval = map.get( key );
            break;
          }
        }
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the first {@link Entry} of a given {@link Map} or null if the {@link Map} reference is null or the {@link Map} is
   * empty
   * 
   * @see #lastEntry(Map)
   * @see #entryAt(Map, int)
   * @param map
   * @return
   */
  public static <K, V> Entry<K, V> firstEntry( Map<K, V> map )
  {
    //    
    Entry<K, V> retval = null;
    
    //
    if ( map != null && !map.isEmpty() )
    {
      retval = IterableUtils.firstElement( map.entrySet() );
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the last {@link Entry} of the given {@link Map}. If the given {@link Map} reference is null or the {@link Map} is
   * empty, null will be returned.
   * 
   * @see #firstEntry(Map)
   * @see #entryAt(Map, int)
   * @param map
   * @return
   */
  public static <K, V> Entry<K, V> lastEntry( Map<K, V> map )
  {
    //    
    Entry<K, V> retval = null;
    
    //
    if ( map != null && !map.isEmpty() )
    {
      retval = IterableUtils.lastElement( map.entrySet() );
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the {@link Entry} at the given index position of the given {@link Map}. If null is given as {@link Map} null will be
   * returned. If the index position is invalid null is returned, too.
   * 
   * @see #firstEntry(Map)
   * @see #lastEntry(Map)
   * @param map
   * @param indexPosition
   * @return
   */
  public static <K, V> Entry<K, V> entryAt( Map<K, V> map, int indexPosition )
  {
    //
    Entry<K, V> retval = map != null ? IterableUtils.elementAt( map.entrySet(), indexPosition ) : null;
    return retval;
  }
  
  /**
   * Returns a new {@link Map} instance for the given {@link Iterable} using the given {@link KeyExtractor} to extract every key
   * one by one from the {@link Iterable}
   * 
   * @param keyExtractor
   *          {@link KeyExtractor}
   * @param iterable
   *          {@link Iterable}
   * @return new {@link Map}
   */
  public static <K, V> Map<K, V> valueOf( KeyExtractor<? extends K, V> keyExtractor, Iterable<? extends V> iterable )
  {
    //
    final Map<K, V> retmap = new LinkedHashMap<K, V>();
    
    //
    if ( keyExtractor != null && iterable != null )
    {
      for ( V element : iterable )
      {
        //
        K key = keyExtractor.extractKey( element );
        retmap.put( key, element );
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Returns a new {@link Map} instance for the given {@link Iterable} using the {@link ElementConverterElementToMapEntry}
   * instance to convert every element to an {@link Entry}
   * 
   * @param iterable
   * @param elementToMapEntryTransformer
   * @return
   */
  public static <K, V, E> Map<K, V> valueOf( Iterable<E> iterable,
                                             ElementConverterElementToMapEntry<E, K, V> elementToMapEntryTransformer )
  {
    //
    final Map<K, V> retmap = new LinkedHashMap<K, V>();
    
    //
    if ( iterable != null && elementToMapEntryTransformer != null )
    {
      for ( E element : iterable )
      {
        //
        Entry<K, V> entry = elementToMapEntryTransformer.convert( element );
        if ( entry != null )
        {
          retmap.put( entry.getKey(), entry.getValue() );
        }
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Returns a new {@link MapComposite} instance for the given {@link Map}s
   * 
   * @param maps
   * @return
   */
  public static <K, V> Map<K, V> composite( Map<K, V>... maps )
  {
    return new MapComposite<K, V>( maps );
  }
  
  /**
   * Returns a new {@link MapComposite} instance for the given {@link Map}s
   * 
   * @param mapList
   * @return
   */
  public static <K, V> Map<K, V> composite( List<Map<K, V>> mapList )
  {
    return new MapComposite<K, V>( mapList );
  }
  
  /**
   * Returns a {@link MapToMapAdapter} for the given source {@link Map}
   * 
   * @param sourceMap
   *          {@link Map}
   * @param elementBidirectionalConverterKey
   *          {@link ElementBidirectionalConverter}
   * @param elementBidirectionalConverterValue
   *          {@link ElementBidirectionalConverter}
   * @return new {@link MapToMapAdapter} instance
   */
  public static <KEY_FROM, VALUE_FROM, KEY_TO, VALUE_TO> Map<KEY_TO, VALUE_TO> adapter( Map<KEY_FROM, VALUE_FROM> sourceMap,
                                                                                        ElementBidirectionalConverter<KEY_FROM, KEY_TO> elementBidirectionalConverterKey,
                                                                                        ElementBidirectionalConverter<VALUE_FROM, VALUE_TO> elementBidirectionalConverterValue )
  {
    return new MapToMapAdapter<KEY_FROM, VALUE_FROM, KEY_TO, VALUE_TO>( sourceMap, elementBidirectionalConverterKey,
                                                                        elementBidirectionalConverterValue );
  }
  
  /**
   * Returns a {@link SortedMapToSortedMapAdapter} for the given source {@link SortedMap}
   * 
   * @param sourceMap
   *          {@link SortedMap}
   * @param elementBidirectionalConverterValue
   *          {@link ElementBidirectionalConverter}
   * @return new {@link SortedMapToSortedMapAdapter} instance
   */
  public static <KEY, VALUE_FROM, VALUE_TO> SortedMap<KEY, VALUE_TO> adapter( SortedMap<KEY, VALUE_FROM> sourceMap,
                                                                              ElementBidirectionalConverter<VALUE_FROM, VALUE_TO> elementBidirectionalConverterValue )
  {
    return new SortedMapToSortedMapAdapter<KEY, VALUE_FROM, VALUE_TO>( sourceMap, elementBidirectionalConverterValue );
  }
  
  /**
   * Similar to {@link #parseString(String, String, String)}
   * 
   * @param content
   * @return
   */
  public static Map<String, String> parseString( String content )
  {
    final String entityDelimiterRegEx = null;
    final String keyValueDelimiterRegEx = null;
    return parseString( content, entityDelimiterRegEx, keyValueDelimiterRegEx );
  }
  
  /**
   * Parses a given text into a {@link LinkedHashMap} instance. <br>
   * <br>
   * Example:<br>
   * 
   * <pre>
   * key1=value1;key2=value2
   * </pre>
   * 
   * would be parsed into a {@link Map} with the keys "key1" and "key2" and "key1" would have the value "value1" and similar for
   * "key2". <br>
   * <br>
   * 
   * @param content
   * @param entityDelimiterRegEx
   *          defaults to ";" or "|"
   * @param keyValueDelimiterRegEx
   *          defaults to "=" or ":"
   * @return
   */
  public static Map<String, String> parseString( String content, String entityDelimiterRegEx, String keyValueDelimiterRegEx )
  {
    final Map<String, String> retmap = new LinkedHashMap<String, String>();
    
    if ( content != null )
    {
      entityDelimiterRegEx = StringUtils.defaultString( entityDelimiterRegEx, "[;\\|]" );
      keyValueDelimiterRegEx = StringUtils.defaultString( keyValueDelimiterRegEx, "[=:]" );
      
      final String[] entityTokens = content.split( entityDelimiterRegEx );
      for ( String entityToken : entityTokens )
      {
        if ( StringUtils.isNotEmpty( entityToken ) )
        {
          final String[] keyAndValueTokens = entityToken.split( keyValueDelimiterRegEx );
          if ( keyAndValueTokens.length == 1 )
          {
            final String key = keyAndValueTokens[0];
            final String value = null;
            retmap.put( key, value );
          }
          else if ( keyAndValueTokens.length == 2 )
          {
            final String key = keyAndValueTokens[0];
            final String value = keyAndValueTokens[1];
            retmap.put( key, value );
          }
        }
      }
    }
    
    return retmap;
  }
}

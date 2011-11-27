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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterIdentity;
import org.omnaest.utils.structure.map.decorator.LockingMapDecorator;
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
   * @see #transform(Entry)
   * @author Omnaest
   * @param <TO>
   * @param <K>
   * @param <V>
   */
  public static interface MapEntryToElementTransformer<TO, K, V>
  {
    /**
     * Converts a {@link Entry} of a {@link Map} to a single element for a {@link List}
     * 
     * @param entry
     * @return
     */
    public TO transform( Entry<K, V> entry );
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
  public static <K, V> Map<K, V> mergeAll( MapElementMergeOperation<K, V> mapElementMergeOperation, Map<K, V>... maps )
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
  public static <K, V> Map<K, V> mergeAll( Collection<Map<K, V>> mapCollection,
                                           MapElementMergeOperation<K, V> mapElementMergeOperation )
  {
    //
    Map<K, V> retmap = new LinkedHashMap<K, V>();
    
    //
    if ( mapCollection != null && mapElementMergeOperation != null )
    {
      for ( Map<K, V> map : mapCollection )
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
   * Returns a list of {@link TupleTwo} instances which have always the value of the first map and the value of the second map
   * which share the same key over both maps.
   * 
   * @param <K>
   * @param <VA>
   * @param <VB>
   * @param mapA
   * @param mapB
   * @return
   */
  public static <K, VA, VB> List<TupleTwo<VA, VB>> innerJoinMapByKey( Map<K, VA> mapA, Map<K, VB> mapB )
  {
    //
    List<TupleTwo<VA, VB>> retlist = new ArrayList<TupleTwo<VA, VB>>();
    
    //
    if ( mapA != null && mapB != null )
    {
      //
      for ( K key : mapA.keySet() )
      {
        //
        VA valueA = mapA.get( key );
        VB valueB = mapB.get( key );
        
        //
        retlist.add( new TupleTwo<VA, VB>( valueA, valueB ) );
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Converts the key of a map into another key.
   * 
   * @param <KeyFrom>
   * @param <KeyTo>
   * @param <Value>
   * @param map
   * @param keyElementConverter
   * @return
   */
  public static <KeyFrom, KeyTo, Value> Map<KeyTo, Value> convertMapKey( Map<KeyFrom, Value> map,
                                                                         ElementConverter<KeyFrom, KeyTo> keyElementConverter )
  {
    return MapUtils.convertMap( map, keyElementConverter, new ElementConverterIdentity<Value>() );
  }
  
  /**
   * Converts the value of a map into another key.
   * 
   * @param <Key>
   * @param <ValueFrom>
   * @param <ValueTo>
   * @param map
   * @param valueElementConverter
   * @return
   */
  public static <Key, ValueFrom, ValueTo> Map<Key, ValueTo> convertMapValue( Map<Key, ValueFrom> map,
                                                                             ElementConverter<ValueFrom, ValueTo> valueElementConverter )
  {
    return MapUtils.convertMap( map, new ElementConverterIdentity<Key>(), valueElementConverter );
  }
  
  /**
   * Converts a given map into a new map with new types. Makes use of element converters.
   * 
   * @see ElementConverter
   * @param map
   * @param keyElementConverter
   * @param valueElementConverter
   * @return
   */
  public static <KeyFrom, KeyTo, ValueFrom, ValueTo> Map<KeyTo, ValueTo> convertMap( Map<KeyFrom, ValueFrom> map,
                                                                                     ElementConverter<KeyFrom, KeyTo> keyElementConverter,
                                                                                     ElementConverter<ValueFrom, ValueTo> valueElementConverter )
  {
    //
    Map<KeyTo, ValueTo> retmap = null;
    
    //
    if ( map != null )
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
   * Transforms a given {@link Map} to a {@link List} using the given {@link MapEntryToElementTransformer} to create single
   * elements for the {@link List} based on the {@link Entry}s of the given {@link Map}
   * 
   * @param map
   * @param mapEntryToElementTransformer
   * @return {@link List}
   */
  public static <TO, K, V> List<TO> toList( Map<K, V> map, MapEntryToElementTransformer<TO, K, V> mapEntryToElementTransformer )
  {
    //
    List<TO> retlist = new ArrayList<TO>();
    
    //
    if ( map != null && mapEntryToElementTransformer != null )
    {
      for ( Entry<K, V> entry : map.entrySet() )
      {
        retlist.add( mapEntryToElementTransformer.transform( entry ) );
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Filters a given {@link Map} by its keys. Only keys which are contained within the given key {@link Set} will be returned.
   * 
   * @param map
   * @param filterKeySet
   * @return new {@link LinkedHashMap} instance
   */
  public static <K, V> Map<K, V> filteredMap( Map<K, V> map, Set<K> filterKeySet )
  {
    //
    Map<K, V> retmap = new LinkedHashMap<K, V>();
    
    //
    if ( map != null && filterKeySet != null )
    {
      //
      for ( K key : filterKeySet )
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
  
}

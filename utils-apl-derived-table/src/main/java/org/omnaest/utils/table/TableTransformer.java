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
package org.omnaest.utils.table;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;

/**
 * Transforms a {@link Table} into instances of other types.
 * 
 * @see ImmutableTable#to()
 * @author Omnaest
 * @param <E>
 */
public interface TableTransformer<E>
{
  /**
   * Returns an array containing the {@link Table} elements
   * 
   * @return
   */
  public E[][] array();
  
  /**
   * Returns a {@link String} representation for the {@link Table}
   * 
   * @return
   */
  public String string();
  
  /**
   * Returns a new {@link Map} instance which will contain the first {@link Column} as key and all elements of each {@link Row} as
   * array as map value.<br>
   * <br>
   * The {@link Map} instance is not backed by the {@link Table} in any form, so any modifications to it will keep the
   * {@link Table} unaffected and vice versa.
   * 
   * @see #map(int)
   * @see #map(Map)
   * @see #sortedMap()
   * @return new {@link Map} instance
   */
  public Map<E, E[]> map();
  
  /**
   * Similar to {@link #map()} but allows to specify the {@link Column} which should act as key source
   * 
   * @param columnIndex
   * @return new {@link Map} instance
   */
  public Map<E, E[]> map( int columnIndex );
  
  /**
   * Returns a new {@link Map} instance which will contain the {@link Column} element for the first column index as key and the
   * element of the {@link Column} with the second column index as value array as map value.<br>
   * <br>
   * The {@link Map} instance is not backed by the {@link Table} in any form, so any modifications to it will keep the
   * {@link Table} unaffected and vice versa.
   * 
   * @param columnIndexKey
   * @param columnIndexValue
   * @return new {@link Map} instance
   */
  public Map<E, E> map( int columnIndexKey, int columnIndexValue );
  
  /**
   * Similar to {@link #map(int, int)} but allows to specify a {@link Map} instance which is used
   * 
   * @param map
   * @param columnIndexKey
   * @param columnIndexValue
   * @return the given {@link Map} instance or a new one if the given instance is null
   */
  public <M extends Map<E, E>> M map( M map, int columnIndexKey, int columnIndexValue );
  
  /**
   * Similar to {@link #map()} but returning a {@link SortedMap}. <br>
   * Note that the {@link Table} type has to implement {@link Comparable}
   * 
   * @see #sortedMap(Comparator)
   * @return new {@link SortedMap} instance
   */
  public SortedMap<E, E[]> sortedMap();
  
  /**
   * Similar to {@link #sortedMap()} and {@link #map(int)}
   * 
   * @see #sortedMap()
   * @param columnIndex
   * @return new {@link SortedMap} instance
   */
  public SortedMap<E, E[]> sortedMap( int columnIndex );
  
  /**
   * Similar to {@link #sortedMap()} and {@link #map(int, int)}
   * 
   * @see #sortedMap()
   * @param columnIndexKey
   * @param columnIndexValue
   * @return new {@link SortedMap} instance
   */
  public SortedMap<E, E> sortedMap( int columnIndexKey, int columnIndexValue );
  
  /**
   * Similar to {@link #sortedMap()} but allows to specify a {@link Comparator}
   * 
   * @see #sortedMap()
   * @param comparator
   * @return new {@link SortedMap} instance
   */
  public SortedMap<E, E[]> sortedMap( Comparator<E> comparator );
  
  /**
   * Similar to {@link #sortedMap(Comparator)} and {@link #map(int)}
   * 
   * @see #sortedMap()
   * @param comparator
   * @param columnIndex
   * @return new {@link SortedMap} instance
   */
  public SortedMap<E, E[]> sortedMap( Comparator<E> comparator, int columnIndex );
  
  /**
   * Similar to {@link #sortedMap(Comparator)} and {@link #map(int, int)}
   * 
   * @see #sortedMap()
   * @param comparator
   * @param columnIndexKey
   * @param columnIndexValue
   * @return new {@link SortedMap} instance
   */
  public SortedMap<E, E> sortedMap( Comparator<E> comparator, int columnIndexKey, int columnIndexValue );
  
  /**
   * Returns the given {@link Map} instance enriched by the {@link Table} data. See {@link #map()} for more details.
   * 
   * @see #map()
   * @see #map(Map, int)
   * @param map
   * @return the given {@link Map} instance
   */
  public <M extends Map<E, E[]>> M map( M map );
  
  /**
   * Returns the given {@link Map} instance enriched by the {@link Table} data. See {@link #map(int)} for more details.
   * 
   * @see #map(Map)
   * @param map
   * @param columnIndex
   * @return the given {@link Map} instance or a new one if the given instance is null
   */
  public <M extends Map<E, E[]>> M map( M map, int columnIndex );
}

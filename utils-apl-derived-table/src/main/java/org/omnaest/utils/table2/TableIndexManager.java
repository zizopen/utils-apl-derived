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
package org.omnaest.utils.table2;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedMap;

import org.omnaest.utils.structure.element.KeyExtractor;
import org.omnaest.utils.structure.element.ValueExtractor;

/**
 * Manager for the index structures of a particular {@link Table} instance
 * 
 * @author Omnaest
 * @param <E>
 * @param <C>
 */
public interface TableIndexManager<E, C extends ImmutableCell<E>> extends Serializable
{
  /**
   * Returns the {@link TableIndex} for the given column index position
   * 
   * @param columnIndex
   * @return
   */
  public TableIndex<E, C> of( int columnIndex );
  
  /**
   * Returns the {@link TableIndex} related to the given {@link ImmutableColumn}
   * 
   * @param column
   *          {@link ImmutableColumn}
   * @return
   */
  public TableIndex<E, C> of( ImmutableColumn<E> column );
  
  /**
   * Returns a {@link SortedMap} over the key extracted from the given {@link KeyExtractor} from the {@link Row}s
   * 
   * @param keyExtractor
   *          {@link KeyExtractor}
   * @return {@link SortedMap} backed by the {@link Table}
   */
  public <K> SortedMap<K, Set<Row<E>>> of( KeyExtractor<K, RowDataReader<E>> keyExtractor );
  
  /**
   * Similar to {@link #of(KeyExtractor)} allowing to specify a {@link Comparator}
   * 
   * @param keyExtractor
   *          {@link KeyExtractor}
   * @param comparator
   *          {@link Comparator}
   * @return {@link SortedMap} backed by the {@link Table}
   */
  public <K> SortedMap<K, Set<Row<E>>> of( KeyExtractor<K, RowDataReader<E>> keyExtractor, Comparator<K> comparator );
  
  /**
   * Similar to {@link #of(KeyExtractor, Comparator)} additionally allowing to transform the elements to a specific
   * {@link SortedMap} value
   * 
   * @param keyExtractor
   *          {@link KeyExtractor}
   * @param valueExtractor
   *          {@link ValueExtractor}
   * @param comparator
   *          {@link Comparator}
   * @return {@link SortedMap} backed by the {@link Table}
   */
  public <K, V> SortedMap<K, V> of( KeyExtractor<K, RowDataReader<E>> keyExtractor,
                                    ValueExtractor<V, Set<E[]>> valueExtractor,
                                    Comparator<K> comparator );
  
  /**
   * Similar to {@link #of(KeyExtractor)} and #of(KeyExtractor, ValueExtractor, Comparator)
   * 
   * @param keyExtractor
   *          {@link KeyExtractor}
   * @param valueExtractor
   *          {@link ValueExtractor}
   * @return {@link SortedMap} backed by the {@link Table}
   */
  public <K, V> SortedMap<K, V> of( KeyExtractor<K, RowDataReader<E>> keyExtractor,
                                    final ValueExtractor<V, Set<E[]>> valueExtractor );
  
  public <K, B> SortedMap<K, Set<B>> of( KeyExtractor<K, B> keyExtractor, Class<B> beanType );
  
  public <K, B> SortedMap<K, Set<B>> of( KeyExtractor<K, B> keyExtractor, Class<B> beanType, Comparator<K> comparator );
}

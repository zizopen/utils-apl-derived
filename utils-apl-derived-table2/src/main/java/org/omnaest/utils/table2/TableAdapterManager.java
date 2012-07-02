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
import java.util.BitSet;
import java.util.Map;
import java.util.Set;

/**
 * Manager for any adapter which allows to access a {@link Table} from any other type
 * 
 * @author Omnaest
 * @param <E>
 */
public interface TableAdapterManager<E> extends Serializable
{
  /**
   * Returns a {@link Map} backed by the {@link Table} which has the elements of the key column as key and the set of elements of
   * all matching {@link Row}s at the given column value index
   * 
   * @param columnIndexKey
   * @param columnIndexValue
   * @return
   */
  public Map<E, Set<E>> map( int columnIndexKey, int columnIndexValue );
  
  /**
   * Returns a {@link Map} backed by the {@link Table} which has the elements of the key column as key and the set of all matching
   * {@link Row}s
   * 
   * @param columnIndexKey
   * @return
   */
  public Map<E, Set<Row<E>>> map( int columnIndexKey );
  
  /**
   * Returns a {@link Map} backed by the {@link Table} which has the elements of the key column as {@link Map} keys and the
   * {@link Row} index positions econded as {@link BitSet} as {@link Map} value.<br>
   * <br>
   * This allows faster {@link Map} joins, since you can retrieve multiple row index {@link Map}s, make an intersection of all
   * keys within them, and just {@link BitSet#and(BitSet)} the results to get all {@link Row} index positions with the same
   * values.
   * 
   * @param columnIndexKey
   * @return
   */
  public Map<E, BitSet> rowIndexMap( int columnIndexKey );
}

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

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.beans.replicator.BeanReplicator;
import org.omnaest.utils.beans.replicator.BeanReplicator.Declaration;

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
  
  /**
   * Returns a {@link List} of Java beans where the {@link List} as well the returned Java beans are backed by the {@link Table}.
   * The mapping is done by column titles to property name.<br>
   * <br>
   * Be aware that this performs very slowly, since the beans are mapped to the columns of the table by proxy instances created on
   * the fly.<br>
   * A list of <b>1000 instances</b> can be created and read in <b>about 1-3 seconds</b>.
   * 
   * @param type
   * @return new {@link List} adapter instance
   */
  public <B> List<B> managedBeanList( Class<? extends B> type );
  
  /**
   * Returns a {@link List} of Java beans which as {@link List} is backed by the {@link Table} but having independent bean
   * instances.<br>
   * <br>
   * The mapping is done by column titles to property name.<br>
   * 
   * @param type
   * @return backed {@link List}
   */
  public <B> List<B> beanList( Class<? extends B> type );
  
  /**
   * Similar to {@link #beanList(Class)} allowing to specify a bean mapping {@link Declaration}
   * 
   * @param type
   * @param declaration
   *          {@link Declaration}
   * @return backed {@link List}
   */
  public <B> List<B> beanList( Class<? extends B> type, Declaration declaration );
  
  /**
   * Similar to {@link #managedBeanList(Class)} allowing to specify a bean mapping {@link Declaration}
   * 
   * @see BeanReplicator
   * @see Declaration
   * @param type
   * @param declaration
   *          {@link Declaration}
   * @return new {@link List} adapter instance
   */
  public <B> List<B> managedBeanList( Class<? extends B> type, Declaration declaration );
  
  /**
   * Returns a {@link ResultSet} backed by the underlying {@link Table}
   * 
   * @return
   */
  public ResultSet resultSet();
  
}

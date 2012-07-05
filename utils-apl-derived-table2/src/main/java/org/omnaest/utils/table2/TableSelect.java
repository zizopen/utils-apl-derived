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

import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.omnaest.utils.table2.ImmutableColumn.ColumnIdentity;

/**
 * The {@link TableSelect} allows to select sub areas of a {@link Table} or join with other {@link Table} instances
 * 
 * @author Omnaest
 * @param <E>
 */
public interface TableSelect<E>
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see TableSelect
   * @author Omnaest
   * @param <E>
   */
  public static interface Predicate<E>
  {
    /**
     * A virtual row which can be filtered. It allows to retrieve the data by column index and {@link ImmutableTable} or the
     * {@link ColumnIdentity}
     * 
     * @author Omnaest
     * @param <E>
     */
    public static interface FilterRow<E>
    {
      public E getElement( ColumnIdentity<E> columnIdentity );
      
      public E getElement( ImmutableTable<E> table, int columnIndex );
      
      /**
       * Returns true, if the current {@link TableSelect.Predicate.FilterRow} contains information for the given
       * {@link ColumnIdentity}
       * 
       * @param columnIdentity
       * @return
       */
      public boolean hasColumn( ColumnIdentity<E> columnIdentity );
    }
    
    /**
     * Returns true, if the given {@link TableSelect.Predicate.FilterRow} should be included into the result
     * 
     * @param row
     * @return true if the {@link TableSelect.Predicate.FilterRow} instance should be included
     */
    public boolean isIncluding( FilterRow<E> row );
    
  }
  
  /**
   * @see TableSelect
   * @author Omnaest
   * @param <E>
   */
  public static interface TableJoin<E> extends TableSelect<E>
  {
    
    /**
     * Declares all {@link Column}s of the joined {@link Table} to be selected
     */
    public TableJoin<E> allColumns();
    
    /**
     * Declares on ore more selected {@link Column}s of the joined {@link Table}
     * 
     * @param columnIndex
     * @param columnIndexes
     */
    public TableJoin<E> columns( int columnIndex, int... columnIndexes );
    
    /**
     * Declares the column indices from the left and right tables on which the inner join will test for equality
     * 
     * @param columnLeft
     * @param columnRight
     * @return
     */
    public TableJoin<E> onEqual( ImmutableColumn<E> columnLeft, ImmutableColumn<E> columnRight );
    
    /**
     * {@link TableSelect.Predicate} which is only applied to the joined {@link Table}
     * 
     * @param predicate
     * @return this
     */
    public TableJoin<E> on( Predicate<E> predicate );
    
    @Override
    public TableJoin<E> withTableLock( boolean lockEnabled );
  }
  
  /**
   * @see TableSelect
   * @author Omnaest
   * @param <E>
   */
  public static interface TableSelectExecution<E>
  {
    /**
     * Returns the result as new {@link Table} instance
     * 
     * @return new {@link Table} instance
     */
    public Table<E> table();
    
    /**
     * Returns the result as new {@link SortedMap} instance which has the keys of the first selected {@link Column}
     * 
     * @return
     */
    public SortedMap<E, Set<Row<E>>> sortedMap();
  }
  
  /**
   * Selects all {@link Column}s of the last specified {@link Table}
   * 
   * @return this
   */
  public TableSelect<E> allColumns();
  
  /**
   * Selects all {@link Column}s of the given {@link ImmutableTable}
   * 
   * @return this
   */
  public TableSelect<E> allColumns( ImmutableTable<E> table );
  
  /**
   * Selects one {@link Column}s by its index related to the last specified {@link Table}
   * 
   * @param columnIndex
   * @return this
   */
  public TableSelect<E> column( int columnIndex );
  
  /**
   * Selects one {@link Column}s by its index related to the given {@link Table}
   * 
   * @param table
   * @param columnIndex
   * @return this
   */
  public TableSelect<E> column( ImmutableTable<E> table, int columnIndex );
  
  /**
   * Selects one ore more {@link Column}s by its index related to the last specified {@link Table}
   * 
   * @param columnIndex
   * @param columnIndices
   * @return this
   */
  public TableSelect<E> columns( int columnIndex, int... columnIndices );
  
  /**
   * Selects one ore more {@link Column}s related to the last specified {@link Table}
   * 
   * @param column
   * @return this
   */
  public TableSelect<E> column( ImmutableColumn<E> column );
  
  /**
   * @param table
   * @return {@link TableJoin}
   */
  public TableSelect.TableJoin<E> join( ImmutableTable<E> table );
  
  /**
   * Allows to add one or more {@link Predicate}s which will filter the result of the cross product of {@link Row}s
   * 
   * @param predicate
   * @param predicates
   * @return this
   */
  public TableSelect<E> where( TableSelect.Predicate<E> predicate, TableSelect.Predicate<E>... predicates );
  
  /**
   * Returns a {@link TableSelectExecution} instance which will calculate the result
   * 
   * @return
   */
  public TableSelect.TableSelectExecution<E> as();
  
  /**
   * If set to true, the select will set the {@link ReadLock} on the last declared {@link ImmutableTable}<br>
   * This means for a join only the last {@link ImmutableTable} will be locked during the select.
   * 
   * @param lockEnabled
   * @return this
   */
  public TableSelect<E> withTableLock( boolean lockEnabled );
}

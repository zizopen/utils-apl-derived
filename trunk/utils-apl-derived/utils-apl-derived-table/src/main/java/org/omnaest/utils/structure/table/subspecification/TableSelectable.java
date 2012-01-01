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
package org.omnaest.utils.structure.table.subspecification;

import java.io.Serializable;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.concrete.internal.selection.join.Join;
import org.omnaest.utils.structure.table.view.TableView;

/**
 * Defines the {@link TableSelectable#select()} method of a {@link Table}
 * 
 * @see Selection
 * @see Table
 * @author Omnaest
 * @param <E>
 */
public interface TableSelectable<E>
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * {@link Selection} from a {@link Table}. The {@link Selection} uses a builder pattern to create a {@link TableView} or a new
   * {@link Table} instance based on parameters like {@link Table}s, {@link Predicate}s and {@link Order}
   * 
   * @see Table#select()
   * @see Selection#columns(Column...)
   * @see Selection#allColumns()
   * @see Selection#from(Table)
   * @see Selection#where(Predicate...)
   * @see Selection#orderBy(Order)
   * @see Selection#asTableView()
   * @see Selection#asTable()
   * @see Selection#top(int)
   * @see SelectionJoin
   * @see Predicate
   * @see Order
   * @see TableView
   * @see TableSelectable
   * @see Table
   */
  public static interface Selection<E>
  {
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    /**
     * @see Selection
     * @see Selection#orderBy(Column, Order)
     * @author Omnaest
     */
    public static enum Order
    {
      ASCENDING,
      DESCENDING;
    }
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * Sets the {@link Column}s of the {@link Selection}.
     * 
     * @param columns
     * @return this
     */
    public Selection<E> columns( Column<E>... columns );
    
    /**
     * Sets all {@link Column}s to be selected
     * 
     * @return this
     */
    public Selection<E> allColumns();
    
    /**
     * Sets the {@link Table}
     * 
     * @param tables
     * @return this
     */
    public Selection<E> from( Table<E>... tables );
    
    /**
     * {@link Join} clause of a {@link Selection}
     * 
     * @param table
     * @return this as {@link SelectionJoin}
     */
    public SelectionJoin<E> innerJoin( Table<E> table );
    
    /**
     * Where clause of a {@link Selection} which declares one ore multiple {@link Predicate}s
     * 
     * @param predicates
     * @return this
     */
    public Selection<E> where( Predicate<E>... predicates );
    
    /**
     * Adds a {@link Column} and {@link Order} declaration for a {@link Selection} which results in an ordered {@link Table} after
     * the given {@link Column} by the given {@link Order}. If this method is called multiple times the {@link Column}s and
     * {@link Order} will be chained together.
     * 
     * @see #orderBy(Column)
     * @param column
     * @param order
     * @return this
     */
    public Selection<E> orderBy( Column<E> column, Order order );
    
    /**
     * Orders the {@link Selection} after the given {@link Column} by {@link Order#ASCENDING}
     * 
     * @see #orderBy(Column, Order)
     * @param column
     * @return
     */
    public Selection<E> orderBy( Column<E> column );
    
    /**
     * Creates a {@link TableView} of the {@link Selection}. This should be called after all other configurations have been set.
     * 
     * @return {@link TableView}
     */
    public TableView<E> asTableView();
    
    /**
     * Merges {@link Row}s if there {@link Cell} elements are all equal
     * 
     * @return this
     */
    public Selection<E> distinct();
    
    /**
     * Selects the given number of {@link Row}s
     * 
     * @param numberOfRows
     * @return this
     */
    public Selection<E> top( int numberOfRows );
    
    /**
     * Returns the declared {@link Selection} as a new {@link Table} instance. . This should be called after all other
     * configurations have been set.
     * 
     * @return
     */
    public Table<E> asTable();
  }
  
  /**
   * A {@link Predicate} defines the condition which has to full filled by a {@link Row} to make her included into the result of a
   * {@link Selection}
   * 
   * @param <E>
   * @see Where
   * @author Omnaest
   */
  public static interface Predicate<E> extends Serializable
  {
  }
  
  /**
   * A {@link SelectionJoin} represents the underlying {@link Selection} and allows additionally to add further {@link Predicate}s
   * to a join using the {@link #on(Predicate...)} method.
   * 
   * @param
   * @author Omnaest
   */
  public static interface SelectionJoin<E> extends Selection<E>
  {
    /**
     * Adds further {@link Predicate}s to an {@link SelectionJoin}
     * 
     * @param predicate
     * @return this
     */
    public SelectionJoin<E> on( Predicate<E>... predicates );
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * A {@link Selection} offers methods to select a subset of {@link Row}s and {@link Column}s of a {@link Table} and provide the
   * result as further {@link Table} or {@link TableView}
   * 
   * @see Selection
   * @see Table
   */
  public Selection<E> select();
  
}

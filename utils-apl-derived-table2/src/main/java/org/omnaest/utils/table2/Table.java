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
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.regex.Pattern;

import org.omnaest.utils.events.exception.ExceptionHandler;

/**
 * A {@link Table} represents a two dimensional container
 * 
 * @see ImmutableTable
 * @author Omnaest
 * @param <E>
 */
public interface Table<E> extends ImmutableTable<E>, Serializable
{
  
  /**
   * Adds new elements as {@link Row} to the {@link Table}
   * 
   * @param elements
   * @return this
   */
  public Table<E> addRowElements( E[] elements );
  
  /**
   * Adds new elements as {@link Row} to the {@link Table} at the specific row index position.
   * 
   * @param rowIndex
   * @param elements
   * @return this
   */
  public Table<E> addRowElements( int rowIndex, E... elements );
  
  /**
   * Returns a {@link TableAdapterManager} instance which allows to craeate adapter instances
   * 
   * @return
   */
  public TableAdapterManager<E> as();
  
  /**
   * Returns an {@link Cell} instance for the given row and column index position
   * 
   * @param rowIndex
   * @param columnIndex
   * @return new {@link Cell} instance
   */
  @Override
  public Cell<E> cell( int rowIndex, int columnIndex );
  
  /**
   * Returns an {@link Iterable} over all {@link Cell}s which traverses through the {@link Row}s traversing through their
   * {@link Row#cells()}
   * 
   * @return
   */
  @Override
  public Iterable<Cell<E>> cells();
  
  /**
   * Clears the {@link Table}
   * 
   * @return
   */
  public Table<E> clear();
  
  /**
   * Returns a new {@link Column} currently related to the given column index position
   * 
   * @param columnIndex
   * @return new {@link Column} instance
   */
  @Override
  public Column<E> column( int columnIndex );
  
  /**
   * Similar to {@link #column(int)} based on the first matching column title
   * 
   * @param columnTitle
   * @return
   */
  @Override
  public Column<E> column( String columnTitle );
  
  /**
   * Returns an {@link Iterable} over all {@link Column}s
   * 
   * @return
   */
  @Override
  public Iterable<Column<E>> columns();
  
  /**
   * Returns an {@link Iterable} over all {@link Column}s which have a column title matched by the given {@link Pattern}
   * 
   * @return
   */
  @Override
  public Iterable<Column<E>> columns( Pattern columnTitlePattern );
  
  /**
   * Returns an {@link Iterable} over all {@link Column}s which have a column title included in the given {@link Set} of titles
   * 
   * @return
   */
  @Override
  public Iterable<Column<E>> columns( Set<String> columnTitleSet );
  
  /**
   * Returns all {@link Column}s which have a column title included in the given titles
   * 
   * @param columnTitles
   * @return
   */
  @Override
  public Iterable<Column<E>> columns( String... columnTitles );
  
  /**
   * Copies the elements from an array
   * 
   * @param elementMatrix
   * @return this
   */
  public Table<E> copyFrom( E[][] elementMatrix );
  
  /**
   * Executes a {@link TableExecution} with a table wide {@link WriteLock}
   * 
   * @param tableExecution
   *          {@link TableExecution}
   * @return this
   */
  public Table<E> executeWithWriteLock( TableExecution<Table<E>, E> tableExecution );
  
  /**
   * Returns the {@link TableIndexManager} instance which allows to create {@link TableIndex} instances based on {@link Column}s
   * of the {@link Table}
   * 
   * @return
   */
  @Override
  public TableIndexManager<E, Cell<E>> index();
  
  /**
   * Removes the {@link Column} for the given column index position
   * 
   * @param columnIndex
   * @return this
   */
  public Table<E> removeColumn( int columnIndex );
  
  /**
   * Removes a {@link Row} at the given row index position
   * 
   * @param rowIndex
   * @return this
   */
  public Table<E> removeRow( int rowIndex );
  
  /**
   * Returns a new {@link Row} related to the given row index position
   * 
   * @param rowIndex
   * @return
   */
  @Override
  public Row<E> row( int rowIndex );
  
  /**
   * Similar to {@link #row(int)} based on the first matching row title
   * 
   * @param rowTitle
   * @return
   */
  @Override
  public Row<E> row( String rowTitle );
  
  /**
   * Returns an {@link Iterable} instance over all {@link Row}s of the {@link Table}
   * 
   * @return
   */
  @Override
  public Iterable<Row<E>> rows();
  
  /**
   * Returns an {@link Iterable} over all {@link Row}s where the row index position has an enabled bit within the filter
   * {@link BitSet}
   * 
   * @param indexFilter
   * @return new {@link Iterable}
   */
  @Override
  public Iterable<Row<E>> rows( BitSet indexFilter );
  
  /**
   * Returns a {@link TableSerializer} instance
   * 
   * @return
   */
  @Override
  public TableSerializer<E> serializer();
  
  /**
   * Sets the element for a given row and column index position
   * 
   * @param rowIndex
   * @param columnIndex
   * @param element
   * @return
   */
  public Table<E> setCellElement( int rowIndex, int columnIndex, E element );
  
  /**
   * Sets the title of the {@link Column} with the given column index position
   * 
   * @param columnIndex
   * @param columnTitle
   * @return this
   */
  public Table<E> setColumnTitle( int columnIndex, String columnTitle );
  
  /**
   * Clears and sets the names of the {@link Column}s to the given values
   * 
   * @param columnTitleIterable
   * @return this
   */
  public Table<E> setColumnTitles( Iterable<String> columnTitleIterable );
  
  /**
   * Sets the {@link ExceptionHandler} instance
   * 
   * @param exceptionHandler
   * @return this
   */
  public Table<E> setExceptionHandler( ExceptionHandler exceptionHandler );
  
  /**
   * Sets the elements of the {@link Row} at the given row index position
   * 
   * @param rowIndex
   * @param elements
   * @return this
   */
  public Table<E> setRowElements( int rowIndex, E... elements );
  
  /**
   * Sets the title of the {@link Row} with the given row index position
   * 
   * @param rowIndex
   * @param rowTitle
   * @return this
   */
  public Table<E> setRowTitle( int rowIndex, String rowTitle );
  
  /**
   * Clears and sets the names of the {@link Row}s to the given values
   * 
   * @param rowTitleIterable
   * @return this
   */
  public Table<E> setRowTitles( Iterable<String> rowTitleIterable );
  
  /**
   * Sets the name of the {@link Table}
   * 
   * @param tableName
   * @return this
   */
  public Table<E> setTableName( String tableName );
  
}

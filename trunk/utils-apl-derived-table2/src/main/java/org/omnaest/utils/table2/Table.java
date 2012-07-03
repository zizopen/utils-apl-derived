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
   * Returns a new {@link Row} related to the given row index position
   * 
   * @param rowIndex
   * @return
   */
  public Row<E> row( int rowIndex );
  
  /**
   * Returns a new {@link Column} currently related to the given column index position
   * 
   * @param columnIndex
   * @return new {@link Column} instance
   */
  public Column<E> column( int columnIndex );
  
  /**
   * Returns an {@link Cell} instance for the given row and column index position
   * 
   * @param rowIndex
   * @param columnIndex
   * @return new {@link Cell} instance
   */
  public Cell<E> cell( int rowIndex, int columnIndex );
  
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
   * Sets the elements of the {@link Row} at the given row index position
   * 
   * @param rowIndex
   * @param elements
   * @return this
   */
  public Table<E> setRowElements( int rowIndex, E... elements );
  
  /**
   * Copies the elements from an array
   * 
   * @param elementMatrix
   * @return this
   */
  public Table<E> copyFrom( E[][] elementMatrix );
  
  /**
   * Clears the {@link Table}
   * 
   * @return
   */
  public Table<E> clear();
  
  /**
   * Returns a {@link TableSerializer} instance
   * 
   * @return
   */
  public TableSerializer<E> serializer();
  
  /**
   * Returns the {@link TableIndexManager} instance which allows to create {@link TableIndex} instances based on {@link Column}s
   * of the {@link Table}
   * 
   * @return
   */
  public TableIndexManager<E, Cell<E>> index();
  
  /**
   * Returns an {@link Iterable} instance over all {@link Row}s of the {@link Table}
   * 
   * @return
   */
  public Iterable<Row<E>> rows();
  
  /**
   * Returns an {@link Iterable} over all {@link Row}s where the row index position has an enabled bit within the filter
   * {@link BitSet}
   * 
   * @param indexFilter
   * @return new {@link Iterable}
   */
  public Iterable<Row<E>> rows( BitSet indexFilter );
  
  /**
   * Returns an {@link Iterable} over all {@link Column}s
   * 
   * @return
   */
  public Iterable<Column<E>> columns();
  
  /**
   * Sets the name of the {@link Table}
   * 
   * @param tableName
   * @return this
   */
  public Table<E> setTableName( String tableName );
  
  /**
   * Clears and sets the names of the {@link Column}s to the given values
   * 
   * @param columnTitleIterable
   * @return this
   */
  public Table<E> setColumnTitles( Iterable<String> columnTitleIterable );
  
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
   * Sets the title of the {@link Column} with the given column index position
   * 
   * @param columnIndex
   * @param columnTitle
   * @return this
   */
  public Table<E> setColumnTitle( int columnIndex, String columnTitle );
  
  /**
   * Sets the {@link ExceptionHandler} instance
   * 
   * @param exceptionHandler
   * @return this
   */
  public Table<E> setExceptionHandler( ExceptionHandler exceptionHandler );
  
  /**
   * Returns a {@link TableAdapterManager} instance which allows to craeate adapter instances
   * 
   * @return
   */
  public TableAdapterManager<E> as();
  
  /**
   * Removes a {@link Row} at the given row index position
   * 
   * @param rowIndex
   * @return this
   */
  public Table<E> removeRow( int rowIndex );
  
}

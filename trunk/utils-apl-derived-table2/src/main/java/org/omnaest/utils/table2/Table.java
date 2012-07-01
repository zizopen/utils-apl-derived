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

/**
 * A {@link Table} represents a two dimensional container
 * 
 * @see ImmutableTable
 * @author Omnaest
 * @param <E>
 */
public interface Table<E> extends ImmutableTable<E>
{
  /**
   * Returns a new {@link Row} related to the given row index position
   * 
   * @param rowIndex
   * @return
   */
  public Row<E> getRow( int rowIndex );
  
  /**
   * Returns a new {@link Column} currently related to the given column index position
   * 
   * @param columnIndex
   * @return new {@link Column} instance
   */
  public Column<E> getColumn( int columnIndex );
  
  /**
   * Returns an {@link Cell} instance for the given row and column index position
   * 
   * @param rowIndex
   * @param columnIndex
   * @return new {@link Cell} instance
   */
  public Cell<E> getCell( int rowIndex, int columnIndex );
  
  /**
   * Sets the element for a given row and column index position
   * 
   * @param element
   * @param rowIndex
   * @param columnIndex
   * @return
   */
  public Table<E> setCellElement( E element, int rowIndex, int columnIndex );
  
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
   * Returns an {@link Iterable} over all {@link Column}s
   * 
   * @return
   */
  public Iterable<Column<E>> columns();
  
}

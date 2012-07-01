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

import java.util.List;

/**
 * Immutable {@link Table}
 * 
 * @see Table
 * @author Omnaest
 * @param <E>
 */
public interface ImmutableTable<E> extends Iterable<ImmutableRow<E>>
{
  
  /**
   * Returns a new {@link ImmutableRow} currently related to the given row index position
   * 
   * @param rowIndex
   * @return new {@link ImmutableRow} instance
   */
  public ImmutableRow<E> getRow( int rowIndex );
  
  /**
   * Returns a new {@link ImmutableColumn} currently related to the given column index position
   * 
   * @param columnIndex
   * @return new {@link ImmutableColumn} instance
   */
  public ImmutableColumn<E> getColumn( int columnIndex );
  
  /**
   * Returns the element at the given row and column index position
   * 
   * @param rowIndex
   * @param columnIndex
   * @return
   */
  public E getCellElement( int rowIndex, int columnIndex );
  
  /**
   * Returns an {@link ImmutableCell} instance for the given row and column index position
   * 
   * @param rowIndex
   * @param columnIndex
   * @return new {@link ImmutableCell} instance
   */
  public ImmutableCell<E> getCell( int rowIndex, int columnIndex );
  
  /**
   * Returns the number of {@link Row}s
   * 
   * @return
   */
  public int rowSize();
  
  /**
   * Returns the number of {@link Column}s
   * 
   * @return
   */
  public int columnSize();
  
  /**
   * Returns the component type of the {@link Table}
   * 
   * @return
   */
  public Class<E> elementType();
  
  /**
   * Returns a {@link TableTransformer} instance
   * 
   * @return
   */
  public TableTransformer<E> to();
  
  /**
   * Returns a {@link ImmutableTableSerializer} instance
   * 
   * @return
   */
  public ImmutableTableSerializer<E> serializer();
  
  /**
   * Returns the {@link TableIndexManager} instance which allows to create {@link TableIndex} instances based on
   * {@link ImmutableColumn}s of the {@link ImmutableTable}
   * 
   * @return
   */
  public TableIndexManager<E, ? extends ImmutableCell<E>> index();
  
  /**
   * Returns a new {@link TableSelect} instance which allows to select areas or joining with other {@link ImmutableTable}
   * instances
   * 
   * @return
   */
  public TableSelect<E> select();
  
  /**
   * Returns an {@link Iterable} instance over all {@link ImmutableRow}s of the {@link Table}
   * 
   * @return
   */
  public Iterable<? extends ImmutableRow<E>> rows();
  
  /**
   * Returns an {@link Iterable} over all {@link ImmutableColumn}s
   * 
   * @return
   */
  public Iterable<? extends ImmutableColumn<E>> columns();
  
  /**
   * Returns true if the content of this and the given {@link ImmutableTable} are {@link #equals(Object)}
   * 
   * @param table
   *          {@link ImmutableTable}
   * @return
   */
  public boolean equalsInContent( ImmutableTable<E> table );
  
  /**
   * Returns true if the content of this and the given {@link ImmutableTable} are {@link #equals(Object)}, and if the table name,
   * the row and column titles are equal
   * 
   * @param table
   *          {@link ImmutableTable}
   * @return
   */
  public boolean equalsInContentAndMetaData( ImmutableTable<E> table );
  
  /**
   * Returns the table name
   * 
   * @return
   */
  public String getTableName();
  
  /**
   * Returns the title of the row with the given index position
   * 
   * @param rowIndex
   * @return
   */
  public String getRowTitle( int rowIndex );
  
  /**
   * Returns the title of the column with the given index position
   * 
   * @param columnIndex
   * @return
   */
  public String getColumnTitle( int columnIndex );
  
  /**
   * Returns a {@link List} of all column titles
   * 
   * @return
   */
  public List<String> getColumnTitleList();
  
  /**
   * Returns a {@link List} of all row titles
   * 
   * @return
   */
  public List<String> getRowTitleList();
  
  /**
   * Returns true if the {@link ImmutableTable} has column titles
   * 
   * @return
   */
  public boolean hasColumnTitles();
  
  /**
   * Returns true if the {@link ImmutableTable} has row titles
   * 
   * @return
   */
  public boolean hasRowTitles();
  
  /**
   * Returns true if the {@link ImmutableTable} has a table name
   * 
   * @return
   */
  public boolean hasTableName();
}

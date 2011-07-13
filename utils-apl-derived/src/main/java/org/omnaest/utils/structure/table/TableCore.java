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
package org.omnaest.utils.structure.table;

import java.util.List;

import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;

/**
 * {@link TableCore} representation. Allows to create arbitrary {@link Table} structures. Offers rudimentary methods accessing the
 * data structure.
 * 
 * @see Table
 * @param <E>
 * @author Omnaest
 */
public interface TableCore<E> extends TableCoreImmutable<E>
{
  
  /**
   * Puts a foreign table into the current table at the given index position. This means, if there are already filled cells, they
   * will be overwritten.
   * 
   * @param insertIndexedTable
   * @param rowIndexPosition
   * @param columnIndexPosition
   * @return
   */
  public Table<E> putTable( Table<E> insertIndexedTable, int rowIndexPosition, int columnIndexPosition );
  
  /**
   * Puts an array into the table. If there are already filled cells on the given position, they will be overwritten.
   * 
   * @param elementArray
   * @param rowIndexPosition
   * @param columnIndexPosition
   * @return
   */
  public Table<E> putArray( E[][] elementArray, int rowIndexPosition, int columnIndexPosition );
  
  /**
   * Transposes the whole table, which means to swap rows and columns.
   * 
   * @return this
   */
  public Table<E> transpose();
  
  /**
   * Sets the title for a row with the given index position.
   * 
   * @see #getRowTitleValueList()
   * @param titleValue
   * @param rowIndexPosition
   * @return
   */
  public Table<E> setRowTitleValue( Object titleValue, int rowIndexPosition );
  
  /**
   * Sets the title for the rows. This means the visual identifiers at the left of the table.
   * 
   * @see #setRowTitles(Enum[])
   * @see #setRowTitles(String[])
   * @param titleList
   * @return
   */
  public Table<E> setRowTitleValues( List<?> titleList );
  
  /**
   * Sets the title of a column for a given column index position.
   * 
   * @param titleValue
   * @param columnIndexPosition
   * @return
   */
  public Table<E> setColumnTitleValue( Object titleValue, int columnIndexPosition );
  
  /**
   * Returns the name for the whole table.
   * 
   * @return
   */
  public Object getTableName();
  
  /**
   * Sets the name of the whole table.
   * 
   * @param tableTitle
   * @return this
   */
  public Table<E> setTableName( Object tableName );
  
  /**
   * Clears the table, so that there are no elements left.<br>
   * This removes all indexes set.
   * 
   * @return
   */
  public Table<E> clear();
  
  /**
   * Adds a {@link Column} to the {@link Table} with the given {@link Cell#getElement()}s at the end of the {@link Column}s.
   * 
   * @param columnCellElementList
   * @return this
   */
  public Table<E> addColumnCellElements( List<? extends E> columnCellElementList );
  
  /**
   * Adds a {@link Column} to the {@link Table} with the given {@link Cell#getElement()}s at the given index position of the
   * {@link Column}s. Already existing {@link Column}s at this index position will be moved one index position further. If the
   * index position is above the size of the current {@link Column}s, as many {@link Column}s are created as necessary to make the
   * {@link Column} available at the given index position.
   * 
   * @param columnIndexPosition
   * @param columnCellElementList
   * @return
   */
  public Table<E> addColumnCellElements( int columnIndexPosition, List<? extends E> columnCellElementList );
  
  /**
   * Adds a new row to the table.<br>
   * If the row has more elements, than the table has columns, the table will be expanded with new empty columns to match the row
   * element number.
   * 
   * @param rowCellElementList
   * @return this
   */
  public Table<E> addRowCellElements( List<? extends E> rowCellElementList );
  
  /**
   * Adds a new {@link Row} to the {@link Table} at the given index position. If there is already a {@link Row} or following
   * {@link Row}s for the given index position, they are moved one index position forward. If the index position is out of bounds
   * as many {@link Row}s are created to make it possible to access the {@link Row} at the given index position.
   * 
   * @param rowIndexPosition
   * @param rowCellElementList
   * @return this
   */
  public Table<E> addRowCellElements( int rowIndexPosition, List<? extends E> rowCellElementList );
  
  /**
   * Removes a {@link Row} at the given index position from the {@link Table}.
   * 
   * @param rowIndexPosition
   * @return {@link List} of {@link Cell#getElement()} instances of the removed {@link Row}
   */
  public List<E> removeRow( int rowIndexPosition );
  
  /**
   * Removes a {@link Column} at the given index position from the {@link Table}.
   * 
   * @param columnIndexPosition
   * @return {@link List} of {@link Cell#getElement()} instances of the removed {@link Column}
   */
  public List<E> removeColumn( int columnIndexPosition );
  
  /**
   * Removes all the given rows.
   * 
   * @param rowIndexPositions
   * @return
   */
  public List<List<E>> removeRows( int[] rowIndexPositions );
  
  /**
   * Takes the first row of the table converts it to text and removes it from the table.
   * 
   * @return this
   */
  public Table<E> convertFirstRowToTitle();
  
  /**
   * Converts the first column to row titles. The first column will be removed from the table data.
   * 
   * @return
   */
  public Table<E> convertFirstColumnToTitle();
  
  /**
   * Sets the number of {@link Column}s
   * 
   * @param numberOfColumns
   * @return this
   */
  public Table<E> setNumberOfColumns( int numberOfColumns );
  
  /**
   * Ensures the number of {@link Column}s to be present
   * 
   * @param numberOfColumns
   * @return this
   */
  public Table<E> ensureNumberOfColumns( int numberOfColumns );
  
  /**
   * Sets the number of {@link Row}s
   * 
   * @param numberOfRows
   * @return
   */
  public Table<E> setNumberOfRows( int numberOfRows );
  
  /**
   * Ensures the number of {@link Row}s to be present
   * 
   * @param numberOfRows
   * @return
   */
  public Table<E> ensureNumberOfRows( int numberOfRows );
}

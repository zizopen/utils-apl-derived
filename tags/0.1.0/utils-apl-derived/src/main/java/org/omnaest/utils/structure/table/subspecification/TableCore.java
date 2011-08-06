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

import java.util.List;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.Table.Stripe.Title;

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
   * @param tableCellElementSource
   * @param rowIndexPosition
   * @param columnIndexPosition
   * @return this
   */
  public Table<E> putTable( TableDataSource<E> tableCellElementSource, int rowIndexPosition, int columnIndexPosition );
  
  /**
   * Puts an array into the table. If there are already filled cells on the given position, they will be overwritten.
   * 
   * @param elementArray
   * @param rowIndexPosition
   * @param columnIndexPosition
   * @return this
   */
  public Table<E> putArray( E[][] elementArray, int rowIndexPosition, int columnIndexPosition );
  
  /**
   * Transposes the whole table, which means to swap rows and columns.
   * 
   * @return this
   */
  public Table<E> transpose();
  
  /**
   * Sets the {@link Title} for a {@link Row} with the given index position.
   * 
   * @see #getRowTitleValue(int)
   * @param titleValue
   * @param rowIndexPosition
   * @return this
   */
  public Table<E> setRowTitleValue( Object titleValue, int rowIndexPosition );
  
  /**
   * Sets the {@link Title} for the {@link Row}s. This means the visual identifiers at the left of the {@link Table}.
   * 
   * @see #setRowTitleValue(Object, int)
   * @see #setRowTitleValues(Object...)
   * @param titleValueList
   * @return this
   */
  public Table<E> setRowTitleValues( List<?> titleValueList );
  
  /**
   * @see #setRowTitleValues(List)
   * @param titleValues
   * @return
   */
  public Table<E> setRowTitleValues( Object... titleValues );
  
  /**
   * Sets the {@link Title}s of the {@link Column}s. The {@link Title}s can be used to identify a {@link Column}, or together with
   * a {@link Row} a single {@link Cell}.
   * 
   * @see #setColumnTitleValues(Object...)
   * @param titleValueList
   * @return
   */
  public Table<E> setColumnTitleValues( List<?> titleValueList );
  
  /**
   * @see #setColumnTitleValues(List)
   * @param titleValues
   * @return
   */
  public Table<E> setColumnTitleValues( Object... titleValues );
  
  /**
   * Sets the title of a column for a given column index position.
   * 
   * @param titleValue
   * @param columnIndexPosition
   * @return this
   */
  public Table<E> setColumnTitleValue( Object titleValue, int columnIndexPosition );
  
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
  public Column<E> addColumnCellElements( List<? extends E> columnCellElementList );
  
  /**
   * Puts a new element to the table at the defined index positions.
   */
  public Table<E> setCellElement( int rowIndexPosition, int columnIndexPosition, E element );
  
  /**
   * Puts a new element to the table at the defined cell index position.
   */
  public Table<E> setCellElement( int cellIndexPosition, E element );
  
  /**
   * Puts a row at the given row index position
   * 
   * @param rowIndexPosition
   * @param rowCellElementList
   * @return this
   */
  public Table<E> setRowCellElements( int rowIndexPosition, List<? extends E> rowCellElementList );
  
  /**
   * Puts a column at the given column index position.
   * 
   * @param columnIndexPosition
   * @param columnCellElementList
   * @return
   */
  public Table<E> setColumnCellElements( int columnIndexPosition, List<? extends E> columnCellElementList );
  
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
  public Column<E> addColumnCellElements( int columnIndexPosition, List<? extends E> columnCellElementList );
  
  /**
   * Adds a new row to the table.<br>
   * If the row has more elements, than the table has columns, the table will be expanded with new empty columns to match the row
   * element number.
   * 
   * @param rowCellElementList
   * @return this
   */
  public Row<E> addRowCellElements( List<? extends E> rowCellElementList );
  
  /**
   * Adds a new {@link Row} to the {@link Table} at the given index position. If there is already a {@link Row} or following
   * {@link Row}s for the given index position, they are moved one index position forward. If the index position is out of bounds
   * as many {@link Row}s are created to make it possible to access the {@link Row} at the given index position.
   * 
   * @param rowIndexPosition
   * @param rowCellElementList
   * @return this
   */
  public Row<E> addRowCellElements( int rowIndexPosition, List<? extends E> rowCellElementList );
  
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
   * @return this
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
   * @return this
   */
  public Table<E> setNumberOfRows( int numberOfRows );
  
  /**
   * Ensures the number of {@link Row}s to be present
   * 
   * @param numberOfRows
   * @return this
   */
  public Table<E> ensureNumberOfRows( int numberOfRows );
  
  /**
   * Deletes all {@link Row}s including the given {@link Row} index position to the end of the {@link Table}
   * 
   * @param rowIndexPosition
   * @return
   */
  public Table<E> truncateRows( int rowIndexPosition );
  
  /**
   * Removes the given {@link Row} from the {@link Table}
   * 
   * @param row
   * @return
   */
  public List<E> removeRow( Row<E> row );
  
  /**
   * Removes the given {@link Column} from the {@link Table}
   * 
   * @param column
   * @return
   */
  public List<E> removeColumn( Column<E> column );
  
}

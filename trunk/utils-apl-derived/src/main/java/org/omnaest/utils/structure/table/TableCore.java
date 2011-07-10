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

import java.util.Iterator;
import java.util.List;

import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.Table.TableCellConverter;
import org.omnaest.utils.structure.table.Table.TableCellVisitor;
import org.omnaest.utils.structure.table.Table.TableSize;

/**
 * {@link TableCore} representation. Allows to create arbitrary {@link Table} structures. Offers rudimentary methods accessing the
 * data structure.
 * 
 * @see Table
 * @param <E>
 * @author Omnaest
 */
public interface TableCore<E>
{
  
  /**
   * Inserts a given array at the given index position into the table. If there are already filled cells, they are moved as much
   * rows down the table as new rows are inserted.
   * 
   * @param elementArray
   * @return this
   */
  public Table<E> insertArray( E[][] elementArray, int rowIndexPosition, int columnIndexPosition );
  
  /**
   * Inserts a table to the given table. If the insert positions are pointing out of the boundaries, the current table will be
   * extended and new created rows or columns will have the titles of the inserted table. Already existing titles will not be
   * overwritten.<br>
   * Indexes will not be set, even if the inserted table has that done for several columns.<br>
   * If there are filled cells at the given index position, they will be moved down the table, to not be overwritten by the new
   * data.
   * 
   * @param insertIndexedTable
   * @return the current table
   */
  public Table<E> insertTable( Table<E> insertIndexedTable, int rowIndexPosition, int columnIndexPosition );
  
  /**
   * Transposes the whole table, which means to swap rows and columns.
   * 
   * @return this
   */
  public Table<E> transpose();
  
  /**
   * Compares the data of two {@link Table} instances, if they are the same true is returned.<br>
   * Titles are not compared.
   * 
   * @return
   */
  public boolean equals( Table<E> table );
  
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
   * Sets the titles of the columns. The titles can be used to identify a column, or together with a row a single cell.
   * 
   * @see #setColumnTitles(Enum[])
   * @see #setColumnTitles(String[])
   * @param titleValueList
   * @return
   */
  public Table<E> setColumnTitleValues( List<?> titleValueList );
  
  /**
   * Returns the row titles for the table.
   * 
   * @see #getRowTitleValue(int)
   * @return
   */
  public List<Object> getRowTitleValueList();
  
  /**
   * @see #getRowTitleValueList()
   * @param rowIndexPosition
   * @return
   */
  public Object getRowTitleValue( int rowIndexPosition );
  
  /**
   * Returns the column titles for the table.
   * 
   * @see #getColumnTitleValue(int)
   * @return
   */
  public List<Object> getColumnTitleValueList();
  
  /**
   * @see #getColumnTitleValueList()
   * @param columnIndexPosition
   * @return
   */
  public Object getColumnTitleValue( int columnIndexPosition );
  
  /**
   * Sets the name of the whole table.
   * 
   * @param tableTitle
   * @return this
   */
  public Table<E> setTableName( Object tableName );
  
  /**
   * Returns the name for the whole table.
   * 
   * @return
   */
  public Object getTableName();
  
  /**
   * Returns a {@link TableSize} object for the table. The object will have always actual results, that will change if the data of
   * the {@link Table} is changed.
   * 
   * @return
   */
  public TableSize getTableSize();
  
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
   * Resolves an element from the table at the given index positions.
   * 
   * @see #getCellElement(int)
   * @param rowIndexPosition
   * @param columnIndexPosition
   * @return
   */
  public E getCellElement( int rowIndexPosition, int columnIndexPosition );
  
  /**
   * Returns the {@link Cell#getElement()} for the given {@link Cell} index position
   * 
   * @see #getCellElement(int, int)
   * @see #getCellElementList()
   * @param cellIndexPosition
   * @return
   */
  public E getCellElement( int cellIndexPosition );
  
  /**
   * Returns a serialized {@link List} of all {@link Cell} instances of the {@link Table}.
   * 
   * @see #getCellElement(int)
   * @return
   */
  public List<E> getCellElementList();
  
  /**
   * Resolves the {@link Cell} for the given index positions.
   * 
   * @param rowIndexPosition
   * @param columnIndexPosition
   * @return
   */
  public Cell<E> getCell( int rowIndexPosition, int columnIndexPosition );
  
  /**
   * Returns the {@link Cell} determined by the given {@link Cell} index position. The {@link Cell} index position starts with 0
   * and increases horizontally with each {@link Column} and this through every single {@link Row} down to the bottom of the
   * {@link Table}. Returns null if the {@link Cell} index position is out of {@link Table} bounds.
   * 
   * @param cellIndexPosition
   * @return
   */
  public Cell<E> getCell( int cellIndexPosition );
  
  /**
   * @see #getCell(int, int)
   * @param rowTitleValue
   * @param columnTitleValue
   * @return
   */
  public Cell<E> getCell( String rowTitleValue, String columnTitleValue );
  
  /**
   * @see #getCell(int, int)
   * @param rowTitleValue
   * @param columnIndexPosition
   * @return
   */
  public Cell<E> getCell( Object rowTitleValue, int columnIndexPosition );
  
  /**
   * @see #getCell(int, int)
   * @param rowIndexPosition
   * @param columnTitleValue
   * @return
   */
  public Cell<E> getCell( int rowIndexPosition, Object columnTitleValue );
  
  /**
   * Returns a to the table backed list of all cells. The cells are ordered from the first row from the left column to right
   * column, to the last row from the left column to the right column.
   * 
   * @see TableCore
   * @return
   */
  public List<E> getCellList();
  
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
   * Returns a new list that holds the object of the row pointed to.
   * 
   * @param rowIndexPosition
   * @return
   */
  public Row<E> getRow( int rowIndexPosition );
  
  /**
   * Returns a {@link Row} of the {@link Table} identified by the title of a row. Before this can be used, the titles have to be
   * set by {@link #setRowTitleValues(List)}
   * 
   * @see #getRow(int)
   * @param rowTitleValue
   * @return
   */
  public Row<E> getRow( Object rowTitleValue );
  
  /**
   * Returns a {@link Column} for the given {@link Column} index position.
   * 
   * @see #getColumn(Object)
   * @param columnIndexPosition
   * @return
   */
  public Column<E> getColumn( int columnIndexPosition );
  
  /**
   * Returns a {@link Column} for the given {@link Column} title value. The {@link Column} titles have to be set before with
   * {@link #setColumnTitleValues(List)}
   * 
   * @see #getColumn(int)
   * @param columnTitleValue
   * @return
   */
  public Column<E> getColumn( Object columnTitleValue );
  
  /**
   * Returns true, if the whole table contains the given element.
   * 
   * @param element
   * @return
   */
  public boolean contains( E element );
  
  /**
   * Clears the table, so that there are no elements left.<br>
   * This removes all indexes set.
   * 
   * @return
   */
  public Table<E> clear();
  
  /**
   * Clones the current table.
   */
  public Table<E> clone();
  
  /**
   * Clones the table structue like table name, titles of columns and rows, and the indexes.
   * 
   * @return
   */
  public Table<E> cloneTableStructure();
  
  /**
   * Returns an {@link Iterator} that goes through every {@link Column} for every {@link Row}. Starting from left to right and
   * then downwards all the {@link Row}s.
   * 
   * @see #iteratorRow()
   * @see #iterator()
   * @return
   */
  public Iterator<Cell<E>> iteratorCell();
  
  /**
   * {@link Iterable} for the {@link Table}s {@link Cell}s.
   * 
   * @see #iteratorCell()
   * @return
   */
  public Iterable<Cell<E>> cells();
  
  /**
   * Returns an iterator over all rows.
   * 
   * @see #iterator()
   * @see #iteratorCell()
   * @return
   */
  public Iterator<Row<E>> iteratorRow();
  
  /**
   * The same as {@link #iteratorRow()}
   * 
   * @see #iteratorRow()
   * @see #iteratorCell()
   * @return
   */
  public Iterator<Row<E>> iterator();
  
  /**
   * Converts the current {@link Table} into a {@link Table} with another element type.
   * 
   * @see TableCellConverter
   * @param tableCellConverter
   * @return
   */
  public <TO> Table<TO> convert( final TableCellConverter<E, TO> tableCellConverter );
  
  /**
   * Helper method to do arbitrary stuff on the table cells. The processor loops through every column of each row, giving a
   * visitor the possibility to do various actions.
   * 
   * @see TableCellVisitor
   * @param tableCellVisitor
   */
  public Table<E> processTableCells( TableCellVisitor<E> tableCellVisitor );
  
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
  
}
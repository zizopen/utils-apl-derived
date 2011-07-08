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
import java.util.ListIterator;

import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.CellIndexPosition;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.Table.RowList;
import org.omnaest.utils.structure.table.Table.TableCellConverter;
import org.omnaest.utils.structure.table.Table.TableCellVisitor;
import org.omnaest.utils.structure.table.Table.TableSize;
import org.omnaest.utils.structure.table.concrete.ArrayTable;

/**
 * Table representation. Allows to create arbitrary table structures. Offers rudimentary methods for joining.
 * 
 * @see IndexTable
 * @see ArrayTable
 * @author Omnaest
 */
public interface TableCore<E, T extends Table<E>>
{
  
  /**
   * Returns the rows corresponding to the interval of the given indexes as a new table object. The from and to index position
   * will be included.
   * 
   * @see #getSubTableByRows(int[])
   * @param rowIndexPositionFrom
   * @param rowIndexPositionTo
   */
  public T getSubTableByRows( int rowIndexPositionFrom, int rowIndexPositionTo );
  
  /**
   * Returns a part of the rows from the table identified by an array of row index positions.<br>
   * Indexes will be kept as they are in this table, as well as titles will be copied to the new subtable object.<br>
   * 
   * @see #getSubTableByRows(int, int)
   * @see #getSubTableByRows(List)
   * @param rowIndexPositions
   * @return
   */
  public T getSubTableByRows( int[] rowIndexPositions );
  
  /**
   * @see #getSubTableByRows(int[])
   * @param rowIndexPositionList
   * @return
   */
  public T getSubTableByRows( List<Integer> rowIndexPositionList );
  
  /**
   * Returns a new table object, with the columns of this object, that are defined by the given index positions.<br>
   * Indexes and titles are copied to the new table object.
   * 
   * @see #getSubTableByColumns(List)
   * @param columnIndexPositions
   * @return
   */
  public T getSubTableByColumns( int[] columnIndexPositions );
  
  /**
   * @see #getSubTableByColumns(int[])
   * @param columnIndexPositionList
   * @return
   */
  public T getSubTableByColumns( List<Integer> columnIndexPositionList );
  
  /**
   * Returns a new table object, with the columns and rows of this object, that are defined by the given crossings of the row and
   * column index positions.<br>
   * Indexes and titles are copied to the new table object.
   * 
   * @param columnIndexPositions
   * @return
   */
  public T getSubTable( int[] rowIndexPositions, int[] columnIndexPositions );
  
  /**
   * Returns a segment of the current table as a new table object with the given boundaries.<br>
   * The boundary index positions are all included in the new table.<br>
   * Indexes and titles are copied as well.
   * 
   * @see #getSubTable(int[], int[])
   * @param rowIndexPositionFrom
   * @param rowIndexPositionTo
   * @param columnIndexPositionFrom
   * @param columnIndexPositionTo
   * @return
   */
  public T getSubTable( int rowIndexPositionFrom, int rowIndexPositionTo, int columnIndexPositionFrom, int columnIndexPositionTo );
  
  /**
   * Returns the table columns interval as a new table object. The interval boundaries are included in the new table.
   * 
   * @see #getSubTableByColumns(int[])
   * @param colunmIndexPositionFrom
   * @param colunmIndexPositionTo
   * @return
   */
  public T getSubTableByColumns( int colunmIndexPositionFrom, int colunmIndexPositionTo );
  
  /**
   * Inserts a given array at the given index position into the table. If there are already filled cells, they are moved as much
   * rows down the table as new rows are inserted.
   * 
   * @param elementArray
   * @return this
   */
  public T insertArray( E[][] elementArray, int rowIndexPosition, int columnIndexPosition );
  
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
  public T insertTable( T insertIndexedTable, int rowIndexPosition, int columnIndexPosition );
  
  /**
   * Transposes the whole table, which means to swap rows and columns.
   * 
   * @return this
   */
  public T transpose();
  
  /**
   * Compares the data of two table, if they are the same true is returned.<br>
   * Titles are not compared.
   * 
   * @return true:data of the table is equal
   */
  public boolean equals( T indexedTable );
  
  /**
   * @see #setRowTitles(List)
   * @see #setRowTitles(String[])
   * @param rowTitleEnums
   */
  public T setRowTitles( Enum<?>[] rowTitleEnums );
  
  /**
   * @see #setRowTitles(Enum[])
   * @see #setRowTitles(List)
   * @param titles
   * @return
   */
  public T setRowTitles( String[] titles );
  
  /**
   * Sets the title for a row with the given index position.
   * 
   * @see #getRowTitleList()
   * @param titleValue
   * @param rowIndexPosition
   * @return
   */
  public T setRowTitle( Object titleValue, int rowIndexPosition );
  
  /**
   * Sets the title for the rows. This means the visual identifiers at the left of the table.
   * 
   * @see #setRowTitles(Enum[])
   * @see #setRowTitles(String[])
   * @param titleList
   * @return
   */
  public T setRowTitles( List<String> titleList );
  
  /**
   * @see #setColumnTitles(List)
   * @see #setColumnTitles(String[])
   * @param titleEnumerations
   * @return
   */
  public T setColumnTitles( Enum<?>[] titleEnumerations );
  
  /**
   * @see #setColumnTitles(List)
   * @see #setColumnTitles(Enum[])
   * @param titles
   * @return
   */
  public T setColumnTitles( String... titles );
  
  /**
   * Determines the javabean property names and sets them as column titles. This will only include bean properties, that are at
   * least readable.
   * 
   * @param beanClass
   * @return this
   */
  public T setColumnTitles( Class<?> beanClass );
  
  /**
   * Sets the title of a column for a given column index position.
   * 
   * @param titleValue
   * @param columnIndexPosition
   * @return
   */
  public T setColumnTitle( Object titleValue, int columnIndexPosition );
  
  /**
   * Sets the titles of the columns. The titles can be used to identify a column, or together with a row a single cell.
   * 
   * @see #setColumnTitles(Enum[])
   * @see #setColumnTitles(String[])
   * @param titleList
   * @return
   */
  public T setColumnTitles( List<String> titleList );
  
  /**
   * Returns the row titles for the table.
   * 
   * @see #getRowTitle(int)
   * @return
   */
  public List<Object> getRowTitleList();
  
  /**
   * @see #getRowTitleList()
   * @param rowIndexPosition
   * @return
   */
  public Object getRowTitle( int rowIndexPosition );
  
  /**
   * Returns the column titles for the table.
   * 
   * @see #getColumnTitle(int)
   * @return
   */
  public List<Object> getColumnTitleList();
  
  /**
   * Returns the column titles for the table.
   * 
   * @return
   */
  public String[] getColumnTitles();
  
  /**
   * @see #getColumnTitleList()
   * @param columnIndexPosition
   * @return
   */
  public Object getColumnTitle( int columnIndexPosition );
  
  /**
   * Sets the name of the whole table.
   * 
   * @param tableTitle
   * @return this
   */
  public T setTableName( String tableName );
  
  /**
   * Returns the name for the whole table.
   * 
   * @return
   */
  public String getTableName();
  
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
  public T setCellElement( int rowIndexPosition, int columnIndexPosition, E element );
  
  /**
   * Puts a new element to the table at the defined cell index position.
   */
  public T setCell( int cellIndexPosition, E element );
  
  /**
   * Puts a row at the given row index position
   * 
   * @param rowIndexPosition
   * @param row
   * @return this
   */
  public T setRow( int rowIndexPosition, List<E> row );
  
  /**
   * Puts a row at the given row index position. The row will be build by retrieving the values of all getter methods of the given
   * javaBean, that are matching the column titles. If no column titles are set, they will be set with the property names of the
   * bean getters.
   * 
   * @param rowIndexPosition
   * @param beanObject
   * @return
   */
  public T setRow( int rowIndexPosition, Object beanObject );
  
  /**
   * Puts a column at the given column index position.
   * 
   * @param columnIndexPosition
   * @param column
   * @return
   */
  public T setColumn( int columnIndexPosition, List<E> column );
  
  /**
   * Puts a foreign table into the current table at the given index position. This means, if there are already filled cells, they
   * will be overwritten.
   * 
   * @param insertIndexedTable
   * @param rowIndexPosition
   * @param columnIndexPosition
   * @return
   */
  public T putTable( T insertIndexedTable, int rowIndexPosition, int columnIndexPosition );
  
  /**
   * Puts an array into the table. If there are already filled cells on the given position, they will be overwritten.
   * 
   * @param elementArray
   * @param rowIndexPosition
   * @param columnIndexPosition
   * @return
   */
  public T putArray( E[][] elementArray, int rowIndexPosition, int columnIndexPosition );
  
  /**
   * Resolves an element from the table at the given index positions.
   * 
   * @param rowIndexPosition
   * @param columnIndexPosition
   * @return
   */
  public E getCellElement( int rowIndexPosition, int columnIndexPosition );
  
  /**
   * Resolves the {@link Cell} for the given index positions.
   * 
   * @param rowIndexPosition
   * @param columnIndexPosition
   * @return
   */
  public Cell<E> getCell( int rowIndexPosition, int columnIndexPosition );
  
  /**
   * Returns the cell element determined by the given cell index position. The cell index position starts with 0 and increases
   * with each cell of a row and this through every single row.
   * 
   * @param cellIndexPosition
   * @return
   */
  public E getCell( int cellIndexPosition );
  
  /**
   * @see #getCell(int, int)
   * @param rowTitle
   * @param columnTitle
   * @return
   */
  public E getCell( String rowTitle, String columnTitle );
  
  /**
   * @see #getCell(int, int)
   * @param rowTitle
   * @param columnIndexPosition
   * @return
   */
  public E getCell( String rowTitle, int columnIndexPosition );
  
  /**
   * @see #getCell(int, int)
   * @param rowIndexPosition
   * @param columnTitle
   * @return
   */
  public E getCell( int rowIndexPosition, String columnTitle );
  
  /**
   * @see #getCell(int, int)
   * @param rowTitleEnum
   * @param columnTitleEnum
   * @return
   */
  public E getCell( Enum<?> rowTitleEnum, Enum<?> columnTitleEnum );
  
  /**
   * @see #getCell(int, int)
   * @param rowTitleEnum
   * @param columnIndexPosition
   * @return
   */
  public E getCell( Enum<?> rowTitleEnum, int columnIndexPosition );
  
  /**
   * @see #getCell(int, int)
   * @param rowIndexPosition
   * @param columnTitleEnumeration
   * @return
   */
  public E getCell( int rowIndexPosition, Enum<?> columnTitleEnumeration );
  
  /**
   * Returns a to the table backed list of all cells. The cells are ordered from the first row from the left column to right
   * column, to the last row from the left column to the right column.
   * 
   * @see TableCore
   * @return
   */
  public List<E> getCellList();
  
  /**
   * Adds a column to the table.<br>
   * If the table has less rows, than the given column, the table will be expanded to the number of rows that are necessary to
   * cover the new column.
   * 
   * @param column
   * @return
   */
  public T addColumn( List<E> column );
  
  /**
   * Adds a new row to the table.<br>
   * If the row has more elements, than the table has columns, the table will be expanded with new empty columns to match the row
   * element number.
   * 
   * @param row
   * @return this
   */
  public T addRow( List<E> row );
  
  /**
   * Adds a new row to the table at the given index position.
   * 
   * @param rowIndexPosition
   * @param row
   * @return this
   */
  public T addRow( int rowIndexPosition, List<E> row );
  
  /**
   * Adds a new row to the table.<br>
   * This is done by using available javabean getter methods, to determine a new column for every javabean property. <br>
   * If there are column titles set for the table, only properties matching the column titles will be set.<br>
   * For example, if a bean has the getter method: "getProperty1", and the table has a column like "property1", the corresponding
   * column field will get the value retrieved from the getter method.
   * 
   * @param beanObject
   * @return this
   */
  public T addRow( Object beanObject );
  
  /**
   * Removes a row at the given index position from the table.
   * 
   * @param rowIndexPosition
   * @return the removed row
   */
  public List<E> removeRow( int rowIndexPosition );
  
  /**
   * Removes all the given rows.
   * 
   * @param rowIndexPositions
   * @return
   */
  public List<E>[] removeRows( int[] rowIndexPositions );
  
  /**
   * Returns a new list that holds the object of the row pointed to.
   * 
   * @param rowIndexPosition
   * @return
   */
  public Row<E> getRow( int rowIndexPosition );
  
  /**
   * Returns the given java bean object with the values of the row indicated by the given row index position.
   * 
   * @param beanObject
   * @param rowIndexPosition
   * @return
   */
  public <B> B getRowAsBean( B beanObject, int rowIndexPosition );
  
  /**
   * Returns the given javaBean object filled with the data of the table row pointed at with the given index position.
   * 
   * @param <C>
   * @param rowIndexPosition
   * @param emptyBeanObject
   *          : has to be a new created object, that will be filled with the data from the row.
   * @return
   */
  public <C> C getRow( int rowIndexPosition, C emptyBeanObject );
  
  /**
   * Returns a row of the table identified by the title of a row. Before this can be used, the titles have to be set by
   * {@link #setRowTitles(List)}
   * 
   * @see #getRow(Enum)
   * @see #getRow(int)
   * @param rowTitle
   * @return
   */
  public Row<E> getRow( String rowTitle );
  
  /**
   * Returns a to the table backed list of rows.
   * 
   * @return
   */
  public RowList<E> getRowList();
  
  /**
   * Returns a row of the table definded by a given name of a row. Before this can be used, row titles have to be set with
   * {@link #setRowTitles(Enum[])}
   * 
   * @see #getRow(int)
   * @see #getRow(String)
   * @param rowTitleEnum
   * @return
   */
  public Row<E> getRow( Enum<?> rowTitleEnum );
  
  /**
   * Returns true if the table contains a row, which has the same elements as the given row in the same order.
   * 
   * @param row
   * @return
   */
  public boolean containsRow( List<E> row );
  
  /**
   * Returns true, if the row created by the java bean properties with the same name as the table titles, is contained by the
   * table.
   * 
   * @param beanObject
   * @return
   */
  public boolean containsRow( Object beanObject );
  
  /**
   * Returns the row index position of a row, which has the same elements as the given row in the same order.
   * 
   * @param row
   * @return
   */
  public int indexOfRow( List<E> row );
  
  /**
   * Returns the row index position of the last row which equals the given row in elements and elements order.
   * 
   * @param row
   * @return
   */
  public int lastIndexOfRow( List<E> row );
  
  /**
   * Returns a new list that holds the object of the row pointed to.
   * 
   * @see #getColumn(Enum)
   * @see #getColumn(String)
   * @param rowIndexPosition
   * @return
   */
  public List<E> getColumn( int columnIndexPosition );
  
  /**
   * Returns a column identified by a enumeration value. Before the column titles have to be set with @link
   * {@link #setColumnTitles(Enum[])}
   * 
   * @see #getColumn(int)
   * @see #getColumn(String)
   * @param columnTitleEnum
   * @return
   */
  public List<E> getColumn( Enum<?> columnTitleEnum );
  
  /**
   * Returns a column for the given column title. The column titles have to be set before with @link
   * {@link #setColumnTitles(List)}
   * 
   * @see #getColumn(Enum)
   * @see #getColumn(int)
   * @param columnTitle
   * @return
   */
  public List<E> getColumn( String columnTitle );
  
  /**
   * Returns the index position of a given element.
   * 
   * @see CellIndexPosition
   * @param element
   * @return
   */
  public CellIndexPosition indexOf( E element );
  
  /**
   * Returns the index position of a given element.
   * 
   * @param element
   * @return
   */
  public CellIndexPosition lastIndexOf( E element );
  
  /**
   * Returns true, if the whole table contains the given element.
   * 
   * @param element
   * @return
   */
  public boolean contains( E element );
  
  /**
   * Returns the index position of the first occurrence for an element within a given row.
   * 
   * @param columnIndexPosition
   */
  public int indexOfFirstColumnWithElementEquals( int rowIndexPosition, E element );
  
  /**
   * Returns the index position of the first occurrence of an element within a given column.
   * 
   * @param columnIndexPosition
   * @param element
   * @return
   */
  public int indexOfFirstRowWithElementEquals( int columnIndexPosition, E element );
  
  /**
   * Returns all indexes, that are matching the given element.
   * 
   * @param columnIndexPosition
   * @param element
   * @return
   */
  public int[] indexesOfRowsWithElementsEquals( int columnIndexPosition, E element );
  
  /**
   * Returns the index position of the last occurrence for an element within a given row.
   * 
   * @param columnIndexPosition
   */
  public int lastIndexOfElementWithinRow( int rowIndexPosition, E element );
  
  /**
   * Clears the table, so that there are no elements left.<br>
   * This removes all indexes set.
   * 
   * @return
   */
  public T clear();
  
  /**
   * Returns the index position of the last occurrence of an element within a given column.
   * 
   * @param columnIndexPosition
   * @param element
   * @return
   */
  public int lastIndexOfElementWithinColumn( int columnIndexPosition, E element );
  
  /**
   * Clones the current table.
   */
  public T clone();
  
  /**
   * Clones the table structue like table name, titles of columns and rows, and the indexes.
   * 
   * @return
   */
  public T cloneTableStructure();
  
  /**
   * Returns an iterator that goes through every line and every column within every line. This means it returns the complete first
   * line than the complete second line, ... .
   * 
   * @see #cellListIterator()
   * @see #rowIterator()
   * @see #iterator()
   * @return
   */
  public Iterator<E> cellIterator();
  
  /**
   * Returns an iterator that goes through every line and every column within every line. This means it returns the complete first
   * line than the complete second line, ... .
   * 
   * @see #cellIterator()
   * @see #rowIterator()
   * @see #iterator()
   * @return
   */
  public ListIterator<E> cellListIterator();
  
  /**
   * Returns an iterator over all rows.
   * 
   * @see #iterator()
   * @see #cellIterator()
   * @return
   */
  public Iterator<Row<E>> rowIterator();
  
  /**
   * Returns a list iterator over all rows.
   * 
   * @see #getRowList()
   * @see #rowIterator()
   * @see #cellIterator()
   * @return
   */
  public ListIterator<Row<E>> rowListIterator();
  
  /**
   * The same as {@link #rowIterator()}
   * 
   * @see #rowIterator()
   * @see #cellIterator()
   * @return
   */
  public Iterator<Row<E>> iterator();
  
  /**
   * The same as {@link #rowIterator()}
   * 
   * @see #rowIterator()
   * @see #cellIterator()
   * @return
   */
  public ListIterator<Row<E>> listIterator();
  
  /**
   * Converts the current table into a table with another type.
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
  public T processTableCells( TableCellVisitor<E> tableCellVisitor );
  
  /**
   * Takes the first row of the table converts it to text and removes it from the table.
   * 
   * @return this
   */
  public T convertFirstRowToTitle();
  
  /**
   * Converts the first column to row titles. The first column will be removed from the table data.
   * 
   * @return
   */
  public T convertFirstColumnToTitle();
  
  /**
   * Creates a new table containing only one row of a set of duplicate rows in the current table.
   * 
   * @return new table
   */
  public T distinct();
  
}

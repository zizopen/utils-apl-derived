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
import java.util.Iterator;
import java.util.List;

import org.omnaest.utils.structure.iterator.ListIterable;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.Table.Stripe.Title;
import org.omnaest.utils.structure.table.Table.TableCellConverter;
import org.omnaest.utils.structure.table.Table.TableCellVisitor;
import org.omnaest.utils.structure.table.Table.TableSize;

/**
 * This {@link TableCoreImmutable} provides core methods which do not change the {@link Table} structure.
 * 
 * @author Omnaest
 * @param <E>
 */
public interface TableCoreImmutable<E> extends TableCloneable<E>, TableDataSource<E>, Serializable
{
  
  /**
   * Compares the data of two {@link Table} instances, if they are the same true is returned.<br>
   * Titles are not compared.
   * 
   * @return
   */
  public boolean equals( TableCoreImmutable<E> table );
  
  /**
   * Returns a new {@link List} with all the {@link Row#getTitleValue()} for the {@link Table}.
   * 
   * @see #getRowTitleValue(int)
   * @return
   */
  @Override
  public List<Object> getRowTitleValueList();
  
  /**
   * @see #getRowTitleValueList()
   * @param rowIndexPosition
   * @return
   */
  public Object getRowTitleValue( int rowIndexPosition );
  
  /**
   * Returns a new {@link List} of all {@link Column}s {@link Title#getValue()} for the {@link Table}.
   * 
   * @see #getColumnTitleValue(int)
   * @return
   */
  @Override
  public List<Object> getColumnTitleValueList();
  
  /**
   * @see #getColumnTitleValueList()
   * @param columnIndexPosition
   * @return
   */
  public Object getColumnTitleValue( int columnIndexPosition );
  
  /**
   * Returns a {@link TableSize} object for the table. The object will have always actual results, that will change if the data of
   * the {@link Table} is changed.
   * 
   * @return
   */
  public TableSize getTableSize();
  
  /**
   * Returns the name for the whole table.
   * 
   * @return
   */
  @Override
  public Object getTableName();
  
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
   * Returns a new {@link List} of all {@link Cell} instances of the {@link Table} serialized .
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
  public List<Cell<E>> getCellList();
  
  /**
   * Returns a new list that holds the object of the row pointed to.
   * 
   * @param rowIndexPosition
   * @return
   */
  public Row<E> getRow( int rowIndexPosition );
  
  /**
   * Returns a {@link List} of all {@link Row}s
   */
  public List<Row<E>> getRowList();
  
  /**
   * Returns a {@link Row} of the {@link Table} identified by the title of a row. Before this can be used, the titles have to be
   * set by {@link TableCore#setRowTitleValues(List)}
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
   * Returns a {@link List} of all {@link Column}s
   * 
   * @return
   */
  public List<Column<E>> getColumnList();
  
  /**
   * Returns a {@link Column} for the given {@link Column} title value. The {@link Column} titles have to be set before with
   * {@link TableCore#setColumnTitleValues(List)}
   * 
   * @see #getColumn(int)
   * @param columnTitleValue
   * @return
   */
  public Column<E> getColumn( Object columnTitleValue );
  
  /**
   * Similar to {@link #getColumn(Object)}
   * 
   * @param columnTitleValue
   * @return
   */
  public Column<E> getColumn( String columnTitleValue );
  
  /**
   * Returns the last {@link Row} of the {@link Table}
   * 
   * @return
   */
  public Row<E> getLastRow();
  
  /**
   * Returns the last {@link Column} of the {@link Table}
   * 
   * @return
   */
  public Column<E> getLastColumn();
  
  /**
   * Returns the first {@link Row} of the {@link Table}
   * 
   * @return
   */
  public Row<E> getFirstRow();
  
  /**
   * Returns the first {@link Column} of the {@link Table}
   * 
   * @return
   */
  public Column<E> getFirstColumn();
  
  /**
   * Returns true, if the whole {@link Table} contains the given element.
   * 
   * @param element
   * @return
   */
  public boolean contains( E element );
  
  /**
   * Returns an {@link Iterator} that goes through every {@link Column} for every {@link Row}. Starting from left to right and
   * then downwards all the {@link Row}s.
   * 
   * @see TableCoreImmutable#iterator()
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
   * {@link ListIterable} over all {@link Row}s
   * 
   * @return
   */
  public ListIterable<Row<E>> rows();
  
  /**
   * {@link Iterable} over all {@link Column}s
   * 
   * @return
   */
  public Iterable<Column<E>> columns();
  
  /**
   * Returns an {@link Iterator} over all {@link Column}s
   * 
   * @return
   */
  public Iterator<Column<E>> iteratorColumn();
  
  /**
   * {@link Iterator} over the {@link Row}s of the {@link Table}
   * 
   * @see #iterator()
   * @see #iteratorCell()
   * @return
   */
  public Iterator<Row<E>> iterator();
  
  /**
   * Converts the current {@link Table} into a new {@link Table} instance with another element type.
   * 
   * @see TableCellConverter
   * @param tableCellConverter
   * @return
   */
  public <TO> Table<TO> convert( final TableCellConverter<E, TO> tableCellConverter );
  
  /**
   * Helper method to do arbitrary stuff on the {@link Table} {@link Cell}s. The processor loops through every {@link Column} of
   * each {@link Row}, giving a visitor the possibility to do various actions.
   * 
   * @see TableCellVisitor
   * @param tableCellVisitor
   */
  public Table<E> processTableCells( TableCellVisitor<E> tableCellVisitor );
  
  /**
   * Returns true if at least one {@link Column} has a {@link Title}
   * 
   * @return
   */
  public boolean hasColumnTitles();
  
  /**
   * Returns true if at least one {@link Row} has a {@link Title}
   * 
   * @return
   */
  public boolean hasRowTitles();
  
  /**
   * Returns true if the {@link Table#getTableName()} is not null
   * 
   * @return
   */
  public boolean hasTableName();
}

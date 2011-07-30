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
package org.omnaest.utils.structure.table.view.concrete;

import java.util.Iterator;
import java.util.List;

import org.omnaest.utils.structure.collection.list.iterator.ListIterable;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.Table.TableCellConverter;
import org.omnaest.utils.structure.table.Table.TableCellVisitor;
import org.omnaest.utils.structure.table.Table.TableSize;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Result;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Selection;
import org.omnaest.utils.structure.table.view.TableView;

/**
 * @see TableView
 * @see Table
 * @author Omnaest
 */
public class TableViewImpl<E> implements TableView<E>
{
  /* ********************************************** Variables ********************************************** */
  protected Selection<E> selection = null;
  protected Result<E>    result    = null;
  
  /* ********************************************** Methods ********************************************** */
  
  @Override
  public boolean equals( TableView<E> tableView )
  {
    return this.result.equals( tableView );
  }
  
  @Override
  public boolean equals( Table<E> table )
  {
    return this.result.equals( table );
  }
  
  @Override
  public Table<E> setColumnTitleValues( List<?> titleValueList )
  {
    return this.result.setColumnTitleValues( titleValueList );
  }
  
  @Override
  public List<Object> getRowTitleValueList()
  {
    return this.result.getRowTitleValueList();
  }
  
  @Override
  public Object getRowTitleValue( int rowIndexPosition )
  {
    return this.result.getRowTitleValue( rowIndexPosition );
  }
  
  @Override
  public List<Object> getColumnTitleValueList()
  {
    return this.result.getColumnTitleValueList();
  }
  
  @Override
  public Object getColumnTitleValue( int columnIndexPosition )
  {
    return this.result.getColumnTitleValue( columnIndexPosition );
  }
  
  @Override
  public TableSize getTableSize()
  {
    return this.result.getTableSize();
  }
  
  @Override
  public Table<E> setCellElement( int rowIndexPosition, int columnIndexPosition, E element )
  {
    return this.result.setCellElement( rowIndexPosition, columnIndexPosition, element );
  }
  
  @Override
  public Table<E> setCellElement( int cellIndexPosition, E element )
  {
    return this.result.setCellElement( cellIndexPosition, element );
  }
  
  @Override
  public Table<E> setRowCellElements( int rowIndexPosition, List<? extends E> rowCellElementList )
  {
    return this.result.setRowCellElements( rowIndexPosition, rowCellElementList );
  }
  
  @Override
  public Table<E> setColumnCellElements( int columnIndexPosition, List<? extends E> columnCellElementList )
  {
    return this.result.setColumnCellElements( columnIndexPosition, columnCellElementList );
  }
  
  @Override
  public E getCellElement( int rowIndexPosition, int columnIndexPosition )
  {
    return this.result.getCellElement( rowIndexPosition, columnIndexPosition );
  }
  
  @Override
  public E getCellElement( int cellIndexPosition )
  {
    return this.result.getCellElement( cellIndexPosition );
  }
  
  @Override
  public List<E> getCellElementList()
  {
    return this.result.getCellElementList();
  }
  
  @Override
  public Cell<E> getCell( int rowIndexPosition, int columnIndexPosition )
  {
    return this.result.getCell( rowIndexPosition, columnIndexPosition );
  }
  
  @Override
  public Cell<E> getCell( int cellIndexPosition )
  {
    return this.result.getCell( cellIndexPosition );
  }
  
  @Override
  public Cell<E> getCell( String rowTitleValue, String columnTitleValue )
  {
    return this.result.getCell( rowTitleValue, columnTitleValue );
  }
  
  @Override
  public Cell<E> getCell( Object rowTitleValue, int columnIndexPosition )
  {
    return this.result.getCell( rowTitleValue, columnIndexPosition );
  }
  
  @Override
  public Cell<E> getCell( int rowIndexPosition, Object columnTitleValue )
  {
    return this.result.getCell( rowIndexPosition, columnTitleValue );
  }
  
  @Override
  public List<Cell<E>> getCellList()
  {
    return this.result.getCellList();
  }
  
  @Override
  public Row<E> getRow( int rowIndexPosition )
  {
    return this.result.getRow( rowIndexPosition );
  }
  
  @Override
  public List<Row<E>> getRowList()
  {
    return this.result.getRowList();
  }
  
  @Override
  public Row<E> getRow( Object rowTitleValue )
  {
    return this.result.getRow( rowTitleValue );
  }
  
  @Override
  public Column<E> getColumn( int columnIndexPosition )
  {
    return this.result.getColumn( columnIndexPosition );
  }
  
  @Override
  public List<Column<E>> getColumnList()
  {
    return this.result.getColumnList();
  }
  
  @Override
  public Column<E> getColumn( Object columnTitleValue )
  {
    return this.result.getColumn( columnTitleValue );
  }
  
  @Override
  public boolean contains( E element )
  {
    return this.result.contains( element );
  }
  
  @Override
  public Table<E> cloneStructure()
  {
    return this.result.cloneStructure();
  }
  
  @Override
  public Table<E> cloneStructureWithContent()
  {
    return this.result.cloneStructureWithContent();
  }
  
  @Override
  public Iterator<Cell<E>> iteratorCell()
  {
    return this.result.iteratorCell();
  }
  
  @Override
  public Iterable<Cell<E>> cells()
  {
    return this.result.cells();
  }
  
  @Override
  public ListIterable<Row<E>> rows()
  {
    return this.result.rows();
  }
  
  @Override
  public Iterable<Column<E>> columns()
  {
    return this.result.columns();
  }
  
  @Override
  public Iterator<Row<E>> iteratorRow()
  {
    return this.result.iteratorRow();
  }
  
  @Override
  public Iterator<Column<E>> iteratorColumn()
  {
    return this.result.iteratorColumn();
  }
  
  @Override
  public Iterator<Row<E>> iterator()
  {
    return this.result.iterator();
  }
  
  @Override
  public <TO> Table<TO> convert( TableCellConverter<E, TO> tableCellConverter )
  {
    return this.result.convert( tableCellConverter );
  }
  
  @Override
  public Table<E> processTableCells( TableCellVisitor<E> tableCellVisitor )
  {
    return this.result.processTableCells( tableCellVisitor );
  }
  
  @Override
  public boolean hasColumnTitles()
  {
    return this.result.hasColumnTitles();
  }
  
  @Override
  public boolean hasRowTitles()
  {
    return this.result.hasRowTitles();
  }
  
  @Override
  public boolean hasTableName()
  {
    return this.result.hasTableName();
  }
  
}

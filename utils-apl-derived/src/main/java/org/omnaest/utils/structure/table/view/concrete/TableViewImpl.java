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
  protected Selection<E> selection   = null;
  protected Table<E>     resultTable = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param selection
   */
  public TableViewImpl( Selection<E> selection )
  {
    super();
    this.selection = selection;
  }
  
  @Override
  public TableView<E> refresh()
  {
    //
    this.resultTable = this.selection.asTable();
    
    //
    return this;
  }
  
  @Override
  public boolean equals( TableView<E> tableView )
  {
    return this.resultTable.equals( tableView );
  }
  
  @Override
  public boolean equals( Table<E> table )
  {
    return this.resultTable.equals( table );
  }
  
  @Override
  public Table<E> setColumnTitleValues( List<?> titleValueList )
  {
    return this.resultTable.setColumnTitleValues( titleValueList );
  }
  
  @Override
  public List<Object> getRowTitleValueList()
  {
    return this.resultTable.getRowTitleValueList();
  }
  
  @Override
  public Object getRowTitleValue( int rowIndexPosition )
  {
    return this.resultTable.getRowTitleValue( rowIndexPosition );
  }
  
  @Override
  public List<Object> getColumnTitleValueList()
  {
    return this.resultTable.getColumnTitleValueList();
  }
  
  @Override
  public Object getColumnTitleValue( int columnIndexPosition )
  {
    return this.resultTable.getColumnTitleValue( columnIndexPosition );
  }
  
  @Override
  public TableSize getTableSize()
  {
    return this.resultTable.getTableSize();
  }
  
  @Override
  public Table<E> setCellElement( int rowIndexPosition, int columnIndexPosition, E element )
  {
    return this.resultTable.setCellElement( rowIndexPosition, columnIndexPosition, element );
  }
  
  @Override
  public Table<E> setCellElement( int cellIndexPosition, E element )
  {
    return this.resultTable.setCellElement( cellIndexPosition, element );
  }
  
  @Override
  public Table<E> setRowCellElements( int rowIndexPosition, List<? extends E> rowCellElementList )
  {
    return this.resultTable.setRowCellElements( rowIndexPosition, rowCellElementList );
  }
  
  @Override
  public Table<E> setColumnCellElements( int columnIndexPosition, List<? extends E> columnCellElementList )
  {
    return this.resultTable.setColumnCellElements( columnIndexPosition, columnCellElementList );
  }
  
  @Override
  public E getCellElement( int rowIndexPosition, int columnIndexPosition )
  {
    return this.resultTable.getCellElement( rowIndexPosition, columnIndexPosition );
  }
  
  @Override
  public E getCellElement( int cellIndexPosition )
  {
    return this.resultTable.getCellElement( cellIndexPosition );
  }
  
  @Override
  public List<E> getCellElementList()
  {
    return this.resultTable.getCellElementList();
  }
  
  @Override
  public Cell<E> getCell( int rowIndexPosition, int columnIndexPosition )
  {
    return this.resultTable.getCell( rowIndexPosition, columnIndexPosition );
  }
  
  @Override
  public Cell<E> getCell( int cellIndexPosition )
  {
    return this.resultTable.getCell( cellIndexPosition );
  }
  
  @Override
  public Cell<E> getCell( String rowTitleValue, String columnTitleValue )
  {
    return this.resultTable.getCell( rowTitleValue, columnTitleValue );
  }
  
  @Override
  public Cell<E> getCell( Object rowTitleValue, int columnIndexPosition )
  {
    return this.resultTable.getCell( rowTitleValue, columnIndexPosition );
  }
  
  @Override
  public Cell<E> getCell( int rowIndexPosition, Object columnTitleValue )
  {
    return this.resultTable.getCell( rowIndexPosition, columnTitleValue );
  }
  
  @Override
  public List<Cell<E>> getCellList()
  {
    return this.resultTable.getCellList();
  }
  
  @Override
  public Row<E> getRow( int rowIndexPosition )
  {
    return this.resultTable.getRow( rowIndexPosition );
  }
  
  @Override
  public List<Row<E>> getRowList()
  {
    return this.resultTable.getRowList();
  }
  
  @Override
  public Row<E> getRow( Object rowTitleValue )
  {
    return this.resultTable.getRow( rowTitleValue );
  }
  
  @Override
  public Column<E> getColumn( int columnIndexPosition )
  {
    return this.resultTable.getColumn( columnIndexPosition );
  }
  
  @Override
  public List<Column<E>> getColumnList()
  {
    return this.resultTable.getColumnList();
  }
  
  @Override
  public Column<E> getColumn( Object columnTitleValue )
  {
    return this.resultTable.getColumn( columnTitleValue );
  }
  
  @Override
  public boolean contains( E element )
  {
    return this.resultTable.contains( element );
  }
  
  @Override
  public Table<E> cloneStructure()
  {
    return this.resultTable.cloneStructure();
  }
  
  @Override
  public Table<E> cloneStructureWithContent()
  {
    return this.resultTable.cloneStructureWithContent();
  }
  
  @Override
  public Iterator<Cell<E>> iteratorCell()
  {
    return this.resultTable.iteratorCell();
  }
  
  @Override
  public Iterable<Cell<E>> cells()
  {
    return this.resultTable.cells();
  }
  
  @Override
  public ListIterable<Row<E>> rows()
  {
    return this.resultTable.rows();
  }
  
  @Override
  public Iterable<Column<E>> columns()
  {
    return this.resultTable.columns();
  }
  
  @Override
  public Iterator<Column<E>> iteratorColumn()
  {
    return this.resultTable.iteratorColumn();
  }
  
  @Override
  public Iterator<Row<E>> iterator()
  {
    return this.resultTable.iterator();
  }
  
  @Override
  public <TO> Table<TO> convert( TableCellConverter<E, TO> tableCellConverter )
  {
    return this.resultTable.convert( tableCellConverter );
  }
  
  @Override
  public Table<E> processTableCells( TableCellVisitor<E> tableCellVisitor )
  {
    return this.resultTable.processTableCells( tableCellVisitor );
  }
  
  @Override
  public boolean hasColumnTitles()
  {
    return this.resultTable.hasColumnTitles();
  }
  
  @Override
  public boolean hasRowTitles()
  {
    return this.resultTable.hasRowTitles();
  }
  
  @Override
  public boolean hasTableName()
  {
    return this.resultTable.hasTableName();
  }
  
}

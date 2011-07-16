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

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.Table.TableCellConverter;
import org.omnaest.utils.structure.table.Table.TableCellVisitor;
import org.omnaest.utils.structure.table.Table.TableSize;
import org.omnaest.utils.structure.table.view.TableView;

/**
 * @see TableView
 * @see Table
 * @author Omnaest
 */
public class TableViewImpl<E> implements TableView<E>
{

  @Override
  public boolean equals( Table<E> table )
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Table<E> setColumnTitleValues( List<?> titleValueList )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Object> getRowTitleValueList()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object getRowTitleValue( int rowIndexPosition )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Object> getColumnTitleValueList()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object getColumnTitleValue( int columnIndexPosition )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TableSize getTableSize()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Table<E> setCellElement( int rowIndexPosition, int columnIndexPosition, E element )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Table<E> setCellElement( int cellIndexPosition, E element )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Table<E> setRowCellElements( int rowIndexPosition, List<? extends E> rowCellElementList )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Table<E> setColumnCellElements( int columnIndexPosition, List<? extends E> columnCellElementList )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public E getCellElement( int rowIndexPosition, int columnIndexPosition )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public E getCellElement( int cellIndexPosition )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<E> getCellElementList()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Cell<E> getCell( int rowIndexPosition, int columnIndexPosition )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Cell<E> getCell( int cellIndexPosition )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Cell<E> getCell( String rowTitleValue, String columnTitleValue )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Cell<E> getCell( Object rowTitleValue, int columnIndexPosition )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Cell<E> getCell( int rowIndexPosition, Object columnTitleValue )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Cell<E>> getCellList()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Row<E> getRow( int rowIndexPosition )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Row<E>> getRowList()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Row<E> getRow( Object rowTitleValue )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Column<E> getColumn( int columnIndexPosition )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Column<E>> getColumnList()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Column<E> getColumn( Object columnTitleValue )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean contains( E element )
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Table<E> cloneStructure()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Table<E> cloneStructureWithContent()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Iterator<Cell<E>> iteratorCell()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Iterable<Cell<E>> cells()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Iterable<Row<E>> rows()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Iterable<Column<E>> columns()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Iterator<Row<E>> iteratorRow()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Iterator<Column<E>> iteratorColumn()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Iterator<Row<E>> iterator()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <TO> Table<TO> convert( TableCellConverter<E, TO> tableCellConverter )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Table<E> processTableCells( TableCellVisitor<E> tableCellVisitor )
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean hasColumnTitles()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean hasRowTitles()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean hasTableName()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean equals( TableView<E> tableView )
  {
    // TODO Auto-generated method stub
    return false;
  }
  
  
  
}

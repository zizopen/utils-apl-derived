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
import org.omnaest.utils.structure.table.subspecification.TableCoreImmutable;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Selection;
import org.omnaest.utils.structure.table.view.TableView;

/**
 * @see TableView
 * @see Table
 * @author Omnaest
 */
public class TableViewImpl<E> implements TableView<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 9098713365809240981L;
  /* ********************************************** Variables ********************************************** */
  protected Selection<E>    selection        = null;
  protected Table<E>        table            = null;
  
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
    this.table = this.selection.asTable();
    
    //
    return this;
  }
  
  @Override
  public boolean equals( TableView<E> tableView )
  {
    return this.table.equals( tableView );
  }
  
  @Override
  public boolean equals( TableCoreImmutable<E> table )
  {
    return this.table.equals( table );
  }
  
  @Override
  public List<Object> getRowTitleValueList()
  {
    return this.table.getRowTitleValueList();
  }
  
  @Override
  public Object getRowTitleValue( int rowIndexPosition )
  {
    return this.table.getRowTitleValue( rowIndexPosition );
  }
  
  @Override
  public List<Object> getColumnTitleValueList()
  {
    return this.table.getColumnTitleValueList();
  }
  
  @Override
  public Object getColumnTitleValue( int columnIndexPosition )
  {
    return this.table.getColumnTitleValue( columnIndexPosition );
  }
  
  @Override
  public TableSize getTableSize()
  {
    return this.table.getTableSize();
  }
  
  @Override
  public E getCellElement( int rowIndexPosition, int columnIndexPosition )
  {
    return this.table.getCellElement( rowIndexPosition, columnIndexPosition );
  }
  
  @Override
  public E getCellElement( int cellIndexPosition )
  {
    return this.table.getCellElement( cellIndexPosition );
  }
  
  @Override
  public List<E> getCellElementList()
  {
    return this.table.getCellElementList();
  }
  
  @Override
  public Cell<E> getCell( int rowIndexPosition, int columnIndexPosition )
  {
    return this.table.getCell( rowIndexPosition, columnIndexPosition );
  }
  
  @Override
  public Cell<E> getCell( int cellIndexPosition )
  {
    return this.table.getCell( cellIndexPosition );
  }
  
  @Override
  public Cell<E> getCell( String rowTitleValue, String columnTitleValue )
  {
    return this.table.getCell( rowTitleValue, columnTitleValue );
  }
  
  @Override
  public Cell<E> getCell( Object rowTitleValue, int columnIndexPosition )
  {
    return this.table.getCell( rowTitleValue, columnIndexPosition );
  }
  
  @Override
  public Cell<E> getCell( int rowIndexPosition, Object columnTitleValue )
  {
    return this.table.getCell( rowIndexPosition, columnTitleValue );
  }
  
  @Override
  public List<Cell<E>> getCellList()
  {
    return this.table.getCellList();
  }
  
  @Override
  public Row<E> getRow( int rowIndexPosition )
  {
    return this.table.getRow( rowIndexPosition );
  }
  
  @Override
  public List<Row<E>> getRowList()
  {
    return this.table.getRowList();
  }
  
  @Override
  public Row<E> getRow( Object rowTitleValue )
  {
    return this.table.getRow( rowTitleValue );
  }
  
  @Override
  public Column<E> getColumn( int columnIndexPosition )
  {
    return this.table.getColumn( columnIndexPosition );
  }
  
  @Override
  public List<Column<E>> getColumnList()
  {
    return this.table.getColumnList();
  }
  
  @Override
  public Column<E> getColumn( Object columnTitleValue )
  {
    return this.table.getColumn( columnTitleValue );
  }
  
  @Override
  public boolean contains( E element )
  {
    return this.table.contains( element );
  }
  
  @Override
  public Table<E> cloneStructure()
  {
    return this.table.cloneStructure();
  }
  
  @Override
  public Table<E> cloneStructureWithContent()
  {
    return this.table.cloneStructureWithContent();
  }
  
  @Override
  public Iterator<Cell<E>> iteratorCell()
  {
    return this.table.iteratorCell();
  }
  
  @Override
  public Iterable<Cell<E>> cells()
  {
    return this.table.cells();
  }
  
  @Override
  public ListIterable<Row<E>> rows()
  {
    return this.table.rows();
  }
  
  @Override
  public Iterable<Column<E>> columns()
  {
    return this.table.columns();
  }
  
  @Override
  public Iterator<Column<E>> iteratorColumn()
  {
    return this.table.iteratorColumn();
  }
  
  @Override
  public Iterator<Row<E>> iterator()
  {
    return this.table.iterator();
  }
  
  @Override
  public <TO> Table<TO> convert( TableCellConverter<E, TO> tableCellConverter )
  {
    return this.table.convert( tableCellConverter );
  }
  
  @Override
  public Table<E> processTableCells( TableCellVisitor<E> tableCellVisitor )
  {
    return this.table.processTableCells( tableCellVisitor );
  }
  
  @Override
  public boolean hasColumnTitles()
  {
    return this.table.hasColumnTitles();
  }
  
  @Override
  public boolean hasRowTitles()
  {
    return this.table.hasRowTitles();
  }
  
  @Override
  public boolean hasTableName()
  {
    return this.table.hasTableName();
  }
  
  @Override
  public Object getTableName()
  {
    return this.table.getTableName();
  }
  
  /**
   * @return
   * @see org.omnaest.utils.structure.table.subspecification.TableCoreImmutable#getLastRow()
   */
  @Override
  public Row<E> getLastRow()
  {
    return this.table.getLastRow();
  }
  
  /**
   * @return
   * @see org.omnaest.utils.structure.table.subspecification.TableCoreImmutable#getLastColumn()
   */
  @Override
  public Column<E> getLastColumn()
  {
    return this.table.getLastColumn();
  }
  
  /**
   * @return
   * @see org.omnaest.utils.structure.table.subspecification.TableCoreImmutable#getFirstRow()
   */
  @Override
  public Row<E> getFirstRow()
  {
    return this.table.getFirstRow();
  }
  
  /**
   * @return
   * @see org.omnaest.utils.structure.table.subspecification.TableCoreImmutable#getFirstColumn()
   */
  @Override
  public Column<E> getFirstColumn()
  {
    return this.table.getFirstColumn();
  }
  
  @Override
  public String toString()
  {
    return this.table == null ? "[table=null]" : this.table.toString();
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.table == null ) ? 0 : this.table.hashCode() );
    return result;
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public boolean equals( Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( obj == null )
    {
      return false;
    }
    if ( !( obj instanceof TableViewImpl ) )
    {
      return false;
    }
    TableViewImpl other = (TableViewImpl) obj;
    if ( this.table == null )
    {
      if ( other.table != null )
      {
        return false;
      }
    }
    else if ( !this.table.equals( other.table ) )
    {
      return false;
    }
    return true;
  }
  
}

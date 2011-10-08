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
package org.omnaest.utils.structure.table.concrete;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.omnaest.utils.structure.collection.ListUtils;
import org.omnaest.utils.structure.table.Table;

/**
 * Implements all facade methods which are only operating on the facade itself
 * 
 * @see Table
 * @see TableSerializer
 * @author Omnaest
 * @param <E>
 */
public abstract class TableAbstract<E> implements Table<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 8793100402385732535L;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * 
   */
  protected TableAbstract()
  {
    super();
  }
  
  @Override
  public E getCellElement( int rowIndexPosition, int columnIndexPosition )
  {
    //
    E retval = null;
    
    //
    Cell<E> cell = this.getCell( rowIndexPosition, columnIndexPosition );
    if ( cell != null )
    {
      retval = cell.getElement();
    }
    
    //
    return retval;
  }
  
  @Override
  public List<List<E>> removeRows( int[] rowIndexPositions )
  {
    //
    List<List<E>> retlist = new ArrayList<List<E>>();
    
    //
    for ( int rowIndexPosition : rowIndexPositions )
    {
      //
      List<E> rowElementList = this.removeRow( rowIndexPosition );
      
      //
      retlist.add( rowElementList );
    }
    
    //
    return retlist;
  }
  
  @Override
  public Table<E> truncateRows( int rowIndexPosition )
  {
    //
    rowIndexPosition = rowIndexPosition >= 0 ? rowIndexPosition : 0;
    
    //
    TableSize tableSize = this.getTableSize();
    while ( tableSize.getRowSize() > rowIndexPosition )
    {
      this.removeRow( rowIndexPosition );
    }
    
    //
    return this;
  }
  
  @Override
  public Table<E> setRowCellElements( int rowIndexPosition, List<? extends E> rowCellElementList )
  {
    //
    if ( rowCellElementList != null )
    {
      for ( int columnIndexPosition = 0; columnIndexPosition < rowCellElementList.size(); columnIndexPosition++ )
      {
        this.setCellElement( rowIndexPosition, columnIndexPosition, rowCellElementList.get( columnIndexPosition ) );
      }
    }
    
    //
    return this;
  }
  
  @Override
  public Table<E> setRowCellElements( int rowIndexPosition, Map<Object, ? extends E> columnTitleValueToRowCellElementMap )
  {
    //
    if ( columnTitleValueToRowCellElementMap != null )
    {
      for ( Object columnTitleValue : columnTitleValueToRowCellElementMap.keySet() )
      {
        this.setCellElement( rowIndexPosition, columnTitleValue, columnTitleValueToRowCellElementMap.get( columnTitleValue ) );
      }
    }
    
    //
    return this;
  }
  
  @Override
  public Table<E> setCellElement( int rowIndexPosition, Object columnTitleValue, E element )
  {
    //
    Column<E> column = this.getColumn( columnTitleValue );
    
    //
    if ( column != null )
    {
      column.setCellElement( rowIndexPosition, element );
    }
    
    //
    return this;
  }
  
  @Override
  public Table<E> setColumnCellElements( int columnIndexPosition, List<? extends E> columnCellElementList )
  {
    //
    if ( columnCellElementList != null )
    {
      for ( int rowIndexPosition = 0; rowIndexPosition < columnCellElementList.size(); rowIndexPosition++ )
      {
        this.setCellElement( rowIndexPosition, columnIndexPosition, columnCellElementList.get( rowIndexPosition ) );
      }
    }
    
    //
    return this;
  }
  
  @Override
  public boolean contains( E element )
  {
    //
    boolean retval = false;
    
    //
    for ( Cell<E> cell : this.cells() )
    {
      if ( cell.hasElement( element ) )
      {
        retval = true;
        break;
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public Table<E> convertFirstRowToTitle()
  {
    //
    final int rowIndexPosition = 0;
    for ( int columnIndexPosition = 0; columnIndexPosition < this.getTableSize().getColumnSize(); columnIndexPosition++ )
    {
      //
      Object titleValue = this.getCellElement( rowIndexPosition, columnIndexPosition );
      this.setColumnTitleValue( titleValue, columnIndexPosition );
    }
    
    //
    this.removeRow( 0 );
    
    // 
    return this;
  }
  
  @Override
  public Table<E> convertFirstColumnToTitle()
  {
    //
    final int columnIndexPosition = 0;
    for ( int rowIndexPosition = 0; rowIndexPosition < this.getTableSize().getRowSize(); rowIndexPosition++ )
    {
      //
      Object titleValue = this.getCellElement( rowIndexPosition, columnIndexPosition );
      this.setRowTitleValue( titleValue, rowIndexPosition );
    }
    
    //
    this.removeColumn( 0 );
    
    // 
    return this;
  }
  
  @Override
  public List<Cell<E>> getCellList()
  {
    return ListUtils.iteratorAsList( this.iteratorCell() );
  }
  
  @Override
  public List<E> getCellElementList()
  {
    //    
    List<E> retlist = new ArrayList<E>();
    
    //
    for ( Cell<E> cell : this.cells() )
    {
      retlist.add( cell != null ? cell.getElement() : null );
    }
    
    // 
    return retlist;
  }
  
  @Override
  public List<Row<E>> getRowList()
  {
    return ListUtils.iteratorAsList( this.rows().iterator() );
  }
  
  @Override
  public List<Column<E>> getColumnList()
  {
    return ListUtils.iteratorAsList( this.iteratorColumn() );
  }
  
  @Override
  public boolean hasColumnTitles()
  {
    //
    boolean retval = false;
    
    //
    for ( Column<E> column : this.columns() )
    {
      if ( column.hasTitle() )
      {
        retval = true;
        break;
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public boolean hasTableName()
  {
    return this.getTableName() != null;
  }
  
  @Override
  public boolean hasRowTitles()
  {
    //
    boolean retval = false;
    
    //
    for ( Row<E> row : this.rows() )
    {
      if ( row.hasTitle() )
      {
        retval = true;
        break;
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public Table<E> setNumberOfColumns( int numberOfColumns )
  {
    //
    for ( int columnIndexPosition = this.getTableSize().getColumnSize() - 1; columnIndexPosition >= numberOfColumns; columnIndexPosition-- )
    {
      this.removeColumn( columnIndexPosition );
    }
    
    //
    this.ensureNumberOfColumns( numberOfColumns );
    
    // 
    return this;
  }
  
  @Override
  public Table<E> ensureNumberOfColumns( int numberOfColumns )
  {
    //
    if ( numberOfColumns > 0 && this.getTableSize().getColumnSize() < numberOfColumns )
    {
      //
      int rowIndexPosition = 0;
      int columnIndexPosition = numberOfColumns - 1;
      E element = null;
      this.setCellElement( rowIndexPosition, columnIndexPosition, element );
    }
    
    // 
    return this;
  }
  
  @Override
  public Table<E> setNumberOfRows( int numberOfRows )
  {
    //
    for ( int rowIndexPosition = this.getTableSize().getColumnSize() - 1; rowIndexPosition >= numberOfRows; rowIndexPosition-- )
    {
      this.removeRow( rowIndexPosition );
    }
    
    //
    this.ensureNumberOfRows( numberOfRows );
    
    //
    return this;
  }
  
  @Override
  public Table<E> ensureNumberOfRows( int numberOfRows )
  {
    //
    if ( numberOfRows > 0 && this.getTableSize().getRowSize() < numberOfRows )
    {
      //
      int rowIndexPosition = numberOfRows - 1;
      int columnIndexPosition = 0;
      E element = null;
      this.setCellElement( rowIndexPosition, columnIndexPosition, element );
    }
    
    // 
    return this;
  }
  
  @Override
  public List<E> removeColumn( Column<E> column )
  {
    //
    List<E> retlist = null;
    
    //
    if ( column != null )
    {
      //
      int columnIndexPosition = column.determineColumnIndexPosition();
      
      //
      retlist = this.removeColumn( columnIndexPosition );
    }
    
    //
    return retlist;
  }
  
  @Override
  public List<E> removeRow( Row<E> row )
  {
    //
    List<E> retlist = null;
    
    //
    if ( row != null )
    {
      //
      int rowIndexPosition = row.determineRowIndexPosition();
      
      //
      retlist = this.removeRow( rowIndexPosition );
    }
    
    //
    return retlist;
  }
  
  @Override
  public Row<E> getLastRow()
  {
    return this.getRow( this.getTableSize().getRowSize() - 1 );
  }
  
  @Override
  public Column<E> getLastColumn()
  {
    return this.getColumn( this.getTableSize().getColumnSize() - 1 );
  }
  
  @Override
  public Row<E> getFirstRow()
  {
    return this.getRow( 0 );
  }
  
  @Override
  public Column<E> getFirstColumn()
  {
    return this.getColumn( 0 );
  }
  
  @Override
  public TableCloner<E> clone() throws CloneNotSupportedException
  {
    throw new CloneNotSupportedException();
  }
}

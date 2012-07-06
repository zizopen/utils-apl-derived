/*******************************************************************************
 * Copyright 2012 Danny Kunz
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
package org.omnaest.utils.table2.impl;

import java.util.BitSet;

import org.omnaest.utils.table2.Cell;
import org.omnaest.utils.table2.Column;
import org.omnaest.utils.table2.Row;
import org.omnaest.utils.table2.Table;

/**
 * @see Cell
 * @author Omnaest
 * @param <E>
 */
class CellImpl<E> implements Cell<E>, TableEventHandler<E>
{
  
  private final static class PositionImplementation implements Position
  {
    private final int columnIndex;
    private final int rowIndex;
    
    public PositionImplementation( int columnIndex, int rowIndex )
    {
      super();
      this.columnIndex = columnIndex;
      this.rowIndex = rowIndex;
    }
    
    @Override
    public int columnIndex()
    {
      return this.columnIndex;
    }
    
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
      if ( !( obj instanceof PositionImplementation ) )
      {
        return false;
      }
      PositionImplementation other = (PositionImplementation) obj;
      if ( this.columnIndex != other.columnIndex )
      {
        return false;
      }
      if ( this.rowIndex != other.rowIndex )
      {
        return false;
      }
      return true;
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + this.columnIndex;
      result = prime * result + this.rowIndex;
      return result;
    }
    
    @Override
    public int rowIndex()
    {
      return this.rowIndex;
    }
    
  }
  
  private static final long serialVersionUID = 6804665993728136898L;
  private volatile int      columnIndex;
  private volatile boolean  isDeleted        = false;
  private volatile boolean  isModified       = false;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private volatile int      rowIndex;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  private final Table<E>    table;
  
  /* *************************************************** Methods **************************************************** */
  
  public CellImpl( int rowIndex, int columnIndex, Table<E> table )
  {
    super();
    this.rowIndex = rowIndex;
    this.columnIndex = columnIndex;
    this.table = table;
  }
  
  @Override
  public E clear()
  {
    final E element = this.getElement();
    this.setElement( null );
    return element;
  }
  
  @Override
  public Column<E> column()
  {
    return this.table.column( this.columnIndex );
  }
  
  @Override
  public int columnIndex()
  {
    return !this.isDeleted ? this.columnIndex : -1;
  }
  
  @Override
  public E getElement()
  {
    return this.isDeleted ? null : this.table.getCellElement( this.rowIndex, this.columnIndex );
  }
  
  @Override
  public org.omnaest.utils.table2.ImmutableCell.Position getPosition()
  {
    return !this.isDeleted ? new PositionImplementation( this.columnIndex, this.rowIndex ) : new PositionImplementation( -1, -1 );
  }
  
  public Row<E> getRow()
  {
    return !this.isDeleted ? this.table.row( this.rowIndex ) : null;
  }
  
  @Override
  public void handleAddedColumn( int columnIndex, E... elements )
  {
    if ( !this.isDeleted && this.columnIndex >= columnIndex )
    {
      this.columnIndex++;
    }
  }
  
  @Override
  public void handleAddedRow( int rowIndex, E... elements )
  {
    if ( !this.isDeleted && this.rowIndex <= rowIndex )
    {
      this.rowIndex++;
    }
  }
  
  @Override
  public void handleClearTable()
  {
    this.markAsDeleted();
  }
  
  @Override
  public void handleRemovedColumn( int columnIndex, E[] previousElements )
  {
    if ( this.columnIndex == columnIndex )
    {
      this.markAsDeleted();
    }
  }
  
  @Override
  public void handleRemovedRow( int rowIndex, E[] previousElements )
  {
    if ( this.rowIndex == rowIndex )
    {
      this.markAsDeleted();
    }
  }
  
  @Override
  public void handleUpdatedCell( int rowIndex, int columnIndex, E element, E previousElement )
  {
    if ( this.rowIndex == rowIndex && this.columnIndex == columnIndex )
    {
      this.isModified = true;
    }
  }
  
  @Override
  public void handleUpdatedRow( int rowIndex, E[] elements, E[] previousElements, BitSet modifiedIndices )
  {
    if ( this.rowIndex == rowIndex )
    {
      this.isModified |= modifiedIndices.get( this.columnIndex );
    }
  }
  
  @Override
  public boolean isDeleted()
  {
    return this.isDeleted;
  }
  
  @Override
  public boolean isModified()
  {
    return this.isModified;
  }
  
  private void markAsDeleted()
  {
    this.isDeleted = true;
    this.columnIndex = -1;
    this.rowIndex = -1;
  }
  
  @Override
  public Row<E> row()
  {
    return this.table.row( this.rowIndex );
  }
  
  @Override
  public int rowIndex()
  {
    return !this.isDeleted ? this.rowIndex : -1;
  }
  
  @Override
  public Cell<E> setElement( E element )
  {
    if ( !this.isDeleted )
    {
      this.table.setCellElement( this.rowIndex, this.columnIndex, element );
    }
    return this;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "CellImpl [rowIndex=" );
    builder.append( this.rowIndex );
    builder.append( ", columnIndex=" );
    builder.append( this.columnIndex );
    builder.append( ", isDeleted=" );
    builder.append( this.isDeleted );
    builder.append( ", isModified=" );
    builder.append( this.isModified );
    builder.append( ", getElement()=" );
    builder.append( this.getElement() );
    builder.append( "]" );
    return builder.toString();
  }
  
}

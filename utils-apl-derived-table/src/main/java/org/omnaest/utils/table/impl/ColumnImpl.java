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
package org.omnaest.utils.table.impl;

import java.util.Arrays;
import java.util.BitSet;

import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.table.Cell;
import org.omnaest.utils.table.Column;
import org.omnaest.utils.table.ImmutableColumn;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.TableEventHandler;

/**
 * @see Column
 * @author Omnaest
 * @param <E>
 */
class ColumnImpl<E> extends StripeImpl<E> implements Column<E>, TableEventHandler<E>
{
  private static final long serialVersionUID = -2490537330526784385L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private volatile int      columnIndex;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see ColumnImpl
   * @param columnIndex
   * @param table
   * @param isDetached
   */
  ColumnImpl( int columnIndex, Table<E> table, boolean isDetached )
  {
    super( table, isDetached );
    this.columnIndex = columnIndex;
  }
  
  @Override
  public Column<E> add( E element )
  {
    final int rowIndex = this.size();
    Cell<E> cell = this.table.cell( rowIndex, this.columnIndex );
    cell.setElement( element );
    return this;
  }
  
  @Override
  public Cell<E> cell( int rowIndex )
  {
    return this.table.cell( rowIndex, this.columnIndex );
  }
  
  @Override
  public E getElement( int rowIndex )
  {
    return this.table.getElement( rowIndex, this.columnIndex );
  }
  
  @Override
  public E getElement( String title )
  {
    return this.table.getElement( title, this.columnIndex );
  }
  
  @Override
  public String getTitle()
  {
    return this.table.getColumnTitle( this.columnIndex );
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
    this.isModified = true;
  }
  
  @Override
  public void handleClearTable()
  {
    this.markAsDeleted();
  }
  
  @Override
  public void handleRemovedColumn( int columnIndex, E[] previousElements, String columnTitle )
  {
    if ( columnIndex == this.columnIndex )
    {
      this.markAsDeleted();
    }
    else if ( columnIndex < this.columnIndex )
    {
      this.columnIndex--;
    }
  }
  
  @Override
  public void handleRemovedRow( int rowIndex, E[] previousElements, String rowTitle )
  {
    this.isModified = true;
  }
  
  @Override
  public void handleUpdatedCell( int rowIndex, int columnIndex, E element, E previousElement )
  {
    if ( this.columnIndex == columnIndex )
    {
      this.isModified = true;
    }
  }
  
  @Override
  public void handleUpdatedRow( int rowIndex, E[] elements, E[] previousElements, BitSet modifiedIndices )
  {
    this.isModified |= modifiedIndices.get( this.columnIndex );
  }
  
  @Override
  public ImmutableColumn.ColumnIdentity<E> id()
  {
    return new ColumnIdentityImpl<E>( this.table, this );
  }
  
  @Override
  public int index()
  {
    return this.columnIndex;
  }
  
  private void markAsDeleted()
  {
    this.isDeleted = true;
    this.columnIndex = -1;
  }
  
  @Override
  public Column<E> remove()
  {
    this.table.removeColumn( this.columnIndex );
    return this;
  }
  
  @Override
  public Column<E> setCellElement( int rowIndex, E element )
  {
    this.table.setElement( rowIndex, this.columnIndex, element );
    return this;
  }
  
  @Override
  public Column<E> setTitle( String columnTitle )
  {
    this.table.setColumnTitle( this.columnIndex, columnTitle );
    return this;
  }
  
  @Override
  public int size()
  {
    return this.table.rowSize();
  }
  
  @Override
  protected String[] getOrthogonalTitles()
  {
    return ArrayUtils.valueOf( this.table.getRowTitleList(), String.class );
  }
  
  @Override
  public Column<E> apply( ElementConverter<E, E> elementConverter )
  {
    super.apply( elementConverter );
    return this;
  }
  
  @Override
  public Column<E> setElements( E... elements )
  {
    this.clear();
    for ( int ii = 0; ii < elements.length; ii++ )
    {
      this.setElement( ii, elements[ii] );
    }
    return this;
  }
  
  @Override
  public Column<E> clear()
  {
    final int size = this.size();
    for ( int ii = 0; ii < size; ii++ )
    {
      this.cell( ii ).clear();
    }
    return this;
  }
  
  @Override
  public Column<E> setElement( int rowIndex, E element )
  {
    this.table.setElement( rowIndex, this.columnIndex, element );
    return this;
  }
  
  @Override
  public Column<E> setElement( String rowTitle, E element )
  {
    this.table.setElement( rowTitle, this.columnIndex, element );
    return this;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "ColumnImpl [columnIndex=" );
    builder.append( this.columnIndex );
    builder.append( ", isDeleted=" );
    builder.append( this.isDeleted );
    builder.append( ", isModified=" );
    builder.append( this.isModified );
    builder.append( ", isDetached=" );
    builder.append( this.isDetached );
    builder.append( ", getElements()=" );
    builder.append( Arrays.toString( this.getElements() ) );
    builder.append( "]" );
    return builder.toString();
  }
  
  @Override
  public void handleModifiedColumnTitle( int columnIndex, String columnTitle, String columnTitlePrevious )
  {
  }
  
  @Override
  public void handleModifiedRowTitle( int rowIndex, String rowTitle, String rowTitlePrevious )
  {
  }
  
  @Override
  public void handleModifiedColumnTitles( String[] columnTitles, String[] columnTitlesPrevious )
  {
  }
  
  @Override
  public void handleModifiedRowTitles( String[] rowTitles, String[] rowTitlesPrevious )
  {
  }
  
  @Override
  public void handleModifiedTableName( String tableName, String tableNamePrevious )
  {
  }
  
}

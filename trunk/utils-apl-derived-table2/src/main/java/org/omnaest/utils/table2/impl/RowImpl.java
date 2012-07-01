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
import org.omnaest.utils.table2.Row;
import org.omnaest.utils.table2.Table;

/**
 * @see Row
 * @author Omnaest
 * @param <E>
 */
class RowImpl<E> extends StripeImpl<E> implements Row<E>, TableEventHandler<E>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private volatile int rowIndex;
  
  /* *************************************************** Methods **************************************************** */
  
  protected RowImpl( int rowIndex, Table<E> table )
  {
    super( table );
    this.rowIndex = rowIndex;
  }
  
  @Override
  public E getCellElement( int columnIndex )
  {
    return this.table.getCellElement( this.rowIndex, columnIndex );
  }
  
  @Override
  public int size()
  {
    return this.table.columnSize();
  }
  
  @Override
  public Row<E> add( E element )
  {
    int columnIndex = this.size();
    this.table.setCellElement( this.rowIndex, columnIndex, element );
    return this;
  }
  
  @Override
  public int index()
  {
    return this.rowIndex;
  }
  
  @Override
  public Cell<E> getCell( int columnIndex )
  {
    return this.table.getCell( this.rowIndex, columnIndex );
  }
  
  @Override
  public void handleAddedRow( int rowIndex, E... elements )
  {
    if ( this.rowIndex <= rowIndex )
    {
      this.rowIndex++;
    }
  }
  
  @Override
  public void handleUpdatedCell( int rowIndex, int columnIndex, E element, E previousElement )
  {
    if ( this.rowIndex == rowIndex )
    {
      this.isModified = true;
    }
  }
  
  @Override
  public void handleUpdatedRow( int rowIndex, E[] elements, E[] previousElements, BitSet modifiedIndices )
  {
    if ( this.rowIndex == rowIndex )
    {
      this.isModified = true;
    }
  }
  
  @Override
  public void handleClearTable()
  {
    this.isDeleted = true;
  }
  
  @Override
  public Row<E> setCellElement( int columnIndex, E element )
  {
    this.table.setCellElement( this.rowIndex, columnIndex, element );
    return this;
  }
  
  @Override
  public String getTitle()
  {
    return this.table.getRowTitle( this.rowIndex );
  }
  
  @Override
  public Row<E> setTitle( String rowTitle )
  {
    this.table.setRowTitle( this.rowIndex, rowTitle );
    return this;
  }
  
}

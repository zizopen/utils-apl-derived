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

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.internal.TableInternal;

/**
 * @see Table
 * @author Omnaest
 * @param <E>
 */
public abstract class TableAbstract<E> implements TableInternal<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 413819072598819746L;
  
  /* ********************************************** Methods ********************************************** */
  @Override
  public abstract Table<E> clone();
  
  @SuppressWarnings("unchecked")
  @Override
  public boolean equals( Object object )
  {
    return object instanceof Table && this.equals( (Table<E>) object );
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
  
}

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

import org.omnaest.utils.table2.ImmutableRow;
import org.omnaest.utils.table2.ImmutableRow.RowIdentity;
import org.omnaest.utils.table2.ImmutableTable;
import org.omnaest.utils.table2.Table;

/**
 * @see RowIdentity
 * @author Omnaest
 * @param <T>
 * @param <E>
 */
class RowIdentityImpl<E> implements RowIdentity<E>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Table<E> table;
  private final int      rowIndex;
  
  /* *************************************************** Methods **************************************************** */
  /**
   * @see RowIdentityImpl
   * @param table
   * @param rowIndex
   */
  RowIdentityImpl( Table<E> table, int rowIndex )
  {
    super();
    this.table = table;
    this.rowIndex = rowIndex;
  }
  
  @Override
  public ImmutableTable<E> getTable()
  {
    return this.table;
  }
  
  @Override
  public ImmutableRow<E> row()
  {
    return this.table.row( this.rowIndex );
  }
  
  @Override
  public int getRowIndex()
  {
    return this.rowIndex;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "RowIdentityImpl [rowIndex=" );
    builder.append( this.rowIndex );
    builder.append( ", table=" );
    builder.append( this.table );
    builder.append( "]" );
    return builder.toString();
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.rowIndex;
    result = prime * result + ( ( this.table == null ) ? 0 : this.table.hashCode() );
    return result;
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
    if ( !( obj instanceof RowIdentityImpl ) )
    {
      return false;
    }
    @SuppressWarnings("rawtypes")
    RowIdentityImpl other = (RowIdentityImpl) obj;
    if ( this.rowIndex != other.rowIndex )
    {
      return false;
    }
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

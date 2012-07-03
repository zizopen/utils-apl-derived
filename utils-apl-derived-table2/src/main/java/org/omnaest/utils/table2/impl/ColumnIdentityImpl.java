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

import org.omnaest.utils.table2.ImmutableColumn;
import org.omnaest.utils.table2.ImmutableColumn.ColumnIdentity;
import org.omnaest.utils.table2.ImmutableTable;
import org.omnaest.utils.table2.Table;

/**
 * @author Omnaest
 * @param <E>
 */
class ColumnIdentityImpl<E> implements ColumnIdentity<E>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final int      columnIndex;
  private final Table<E> table;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see ColumnIdentityImpl
   * @param columnIndex
   * @param table
   */
  ColumnIdentityImpl( int columnIndex, Table<E> table )
  {
    this.columnIndex = columnIndex;
    this.table = table;
  }
  
  @Override
  public ImmutableTable<E> getTable()
  {
    return this.table;
  }
  
  @Override
  public ImmutableColumn<E> column()
  {
    return this.table.column( this.columnIndex );
  }
  
  @Override
  public int getColumnIndex()
  {
    return this.columnIndex;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.columnIndex;
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
    if ( !( obj instanceof ColumnIdentityImpl ) )
    {
      return false;
    }
    @SuppressWarnings("rawtypes")
    ColumnIdentityImpl other = (ColumnIdentityImpl) obj;
    if ( this.columnIndex != other.columnIndex )
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
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "ColumnIdentityImpl [columnIndex=" );
    builder.append( this.columnIndex );
    builder.append( ", table=" );
    builder.append( this.table );
    builder.append( "]" );
    return builder.toString();
  }
  
}

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

import org.omnaest.utils.table2.Column;
import org.omnaest.utils.table2.ImmutableColumn;
import org.omnaest.utils.table2.ImmutableColumn.ColumnIdentity;
import org.omnaest.utils.table2.ImmutableTable;
import org.omnaest.utils.table2.Table;

class ColumnIdentityImpl<E> implements ColumnIdentity<E>
{
  private final int       columnIndex;
  private final Column<E> column;
  private final Table<E>  table;
  
  ColumnIdentityImpl( int columnIndex, Column<E> column, Table<E> table )
  {
    this.columnIndex = columnIndex;
    this.column = column;
    this.table = table;
  }
  
  @Override
  public ImmutableTable<E> getTable()
  {
    return this.table;
  }
  
  @Override
  public ImmutableColumn<E> getColumn()
  {
    return this.column;
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

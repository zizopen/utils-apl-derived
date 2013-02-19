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

import org.omnaest.utils.table.ImmutableRow;
import org.omnaest.utils.table.ImmutableTable;
import org.omnaest.utils.table.Row;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.ImmutableRow.RowIdentity;

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
  private final Row<E>   row;
  
  /* *************************************************** Methods **************************************************** */
  /**
   * @see RowIdentityImpl
   * @param table
   * @param row
   */
  RowIdentityImpl( Table<E> table, Row<E> row )
  {
    super();
    this.table = table;
    this.row = row;
  }
  
  @Override
  public ImmutableTable<E> getTable()
  {
    return this.table;
  }
  
  @Override
  public ImmutableRow<E> row()
  {
    return this.row;
  }
  
  @Override
  public int getRowIndex()
  {
    return this.row.index();
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "RowIdentityImpl [rowIndex=" );
    builder.append( this.getRowIndex() );
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
    result = prime * result + this.getRowIndex();
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
    if ( this.getRowIndex() != other.getRowIndex() )
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

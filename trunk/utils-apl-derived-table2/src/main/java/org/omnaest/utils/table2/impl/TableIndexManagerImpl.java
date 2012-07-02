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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.omnaest.utils.table2.Cell;
import org.omnaest.utils.table2.Column;
import org.omnaest.utils.table2.ImmutableColumn;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TableIndex;
import org.omnaest.utils.table2.TableIndexManager;

/**
 * @see TableIndexManager
 * @author Omnaest
 * @param <E>
 */
final class TableIndexManagerImpl<E> implements TableIndexManager<E, Cell<E>>
{
  /* ************************************************** Constants *************************************************** */
  private static final long                  serialVersionUID = 902507185120046828L;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final List<TableIndex<E, Cell<E>>> tableIndexList   = new CopyOnWriteArrayList<TableIndex<E, Cell<E>>>();
  
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  private final TableDataAccessor<E>         tableDataAccessor;
  private final Table<E>                     table;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see TableIndexManagerImpl
   * @param tableDataAccessor
   * @param table
   */
  @SuppressWarnings("javadoc")
  public TableIndexManagerImpl( TableDataAccessor<E> tableDataAccessor, Table<E> table )
  {
    super();
    this.tableDataAccessor = tableDataAccessor;
    this.table = table;
  }
  
  @Override
  public TableIndex<E, Cell<E>> of( int columnIndex )
  {
    if ( columnIndex >= 0 && columnIndex < this.table.columnSize() )
    {
      return getOrCreateTableIndexForColumn( columnIndex );
    }
    return null;
  }
  
  private TableIndex<E, Cell<E>> getOrCreateTableIndexForColumn( int columnIndex )
  {
    TableIndex<E, Cell<E>> retval = null;
    
    for ( TableIndex<E, Cell<E>> tableIndex : this.tableIndexList )
    {
      int index = tableIndex.index();
      if ( index == columnIndex )
      {
        retval = tableIndex;
        break;
      }
    }
    
    if ( retval == null )
    {
      Column<E> column = this.table.column( columnIndex );
      retval = this.tableDataAccessor.register( new TableIndexImpl<E>( column ) );
      this.tableIndexList.add( retval );
    }
    
    return retval;
  }
  
  @Override
  public TableIndex<E, Cell<E>> of( ImmutableColumn<E> columnImmutable )
  {
    if ( columnImmutable != null )
    {
      int columnIndex = columnImmutable.index();
      return this.of( columnIndex );
    }
    return null;
  }
  
}

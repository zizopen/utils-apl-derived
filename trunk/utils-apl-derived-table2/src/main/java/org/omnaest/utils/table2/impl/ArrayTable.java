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

import java.util.Iterator;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.table2.Cell;
import org.omnaest.utils.table2.Column;
import org.omnaest.utils.table2.ImmutableRow;
import org.omnaest.utils.table2.ImmutableTable;
import org.omnaest.utils.table2.Row;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TableIndexManager;
import org.omnaest.utils.table2.TableSelect;
import org.omnaest.utils.table2.impl.join.TableSelectImpl;

/**
 * {@link Table} implementation based on an two dimensional array
 * 
 * @author Omnaest
 * @param <E>
 */
public class ArrayTable<E> extends TableAbstract<E>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final TableDataAccessor<E>    tableDataAccessor;
  private final Class<E>                type;
  private TableIndexManager<E, Cell<E>> tableIndexManager;
  
  /* *************************************************** Methods **************************************************** */
  
  @SuppressWarnings("unchecked")
  public ArrayTable( Class<? extends E> type )
  {
    super();
    
    Assert.isNotNull( type, "The table type must not be null" );
    
    this.type = (Class<E>) type;
    
    this.tableDataAccessor = new TableDataAccessor<E>( new TableDataCore<E>( type ), new TableEventDispatcher<E>() );
    this.tableIndexManager = new TableIndexManagerImpl<E>( this.tableDataAccessor, this );
  }
  
  @SuppressWarnings("unchecked")
  public ArrayTable( E[][] elementMatrix )
  {
    this( (Class<? extends E>) ArrayUtils.componentType( ArrayUtils.componentType( elementMatrix.getClass() ) ) );
    this.copyFrom( elementMatrix );
  }
  
  @Override
  public Row<E> getRow( int rowIndex )
  {
    return rowIndex >= 0 ? this.tableDataAccessor.register( new RowImpl<E>( rowIndex, this ) ) : null;
  }
  
  @Override
  public Cell<E> getCell( int rowIndex, int columnIndex )
  {
    return rowIndex >= 0 && columnIndex >= 0 ? this.tableDataAccessor.register( new CellImpl<E>( rowIndex, columnIndex, this ) )
                                            : null;
  }
  
  @Override
  public Column<E> getColumn( int columnIndex )
  {
    return columnIndex >= 0 ? this.tableDataAccessor.register( new ColumnImpl<E>( columnIndex, this ) ) : null;
  }
  
  @Override
  public E getCellElement( int rowIndex, int columnIndex )
  {
    return this.tableDataAccessor.getElement( rowIndex, columnIndex );
  }
  
  @Override
  public Table<E> addRowElements( E[] elements )
  {
    //
    this.tableDataAccessor.addRow( elements );
    return this;
  }
  
  @Override
  public Table<E> addRowElements( int rowIndex, E... elements )
  {
    //
    this.tableDataAccessor.addRow( rowIndex, elements );
    return this;
  }
  
  @Override
  public Table<E> copyFrom( E[][] array )
  {
    //
    this.clear();
    
    //
    if ( array != null )
    {
      for ( E[] elements : array )
      {
        this.addRowElements( elements );
      }
    }
    
    //
    return this;
  }
  
  @Override
  public Table<E> clear()
  {
    this.tableDataAccessor.clear();
    return this;
  }
  
  @Override
  public int rowSize()
  {
    return this.tableDataAccessor.rowSize();
  }
  
  @Override
  public int columnSize()
  {
    return this.tableDataAccessor.columnSize();
  }
  
  @Override
  public Class<E> getElementType()
  {
    return this.type;
  }
  
  @Override
  public TableIndexManager<E, Cell<E>> index()
  {
    return this.tableIndexManager;
  }
  
  @Override
  public Table<E> setCellElement( E element, int rowIndex, int columnIndex )
  {
    this.tableDataAccessor.set( element, rowIndex, columnIndex );
    return this;
  }
  
  @Override
  public TableSelect<E> select()
  {
    return new TableSelectImpl<E>( this );
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public boolean equalsInContent( ImmutableTable<E> table )
  {
    boolean retval = table != null;
    
    if ( table != null )
    {
      int rowSize = table.rowSize();
      int columnSize = table.columnSize();
      
      retval &= this.rowSize() == rowSize;
      retval &= this.columnSize() == columnSize;
      
      if ( retval )
      {
        Iterator<Row<E>> iteratorRowThis = this.rows().iterator();
        Iterator<ImmutableRow<E>> iteratorRowOther = ( (Iterable<ImmutableRow<E>>) table.rows() ).iterator();
        
        while ( iteratorRowThis.hasNext() && iteratorRowOther.hasNext() )
        {
          ImmutableRow<E> rowThis = iteratorRowThis.next();
          ImmutableRow<E> rowOther = iteratorRowOther.next();
          
          if ( !rowThis.equalsInContent( rowOther ) )
          {
            retval = false;
            break;
          }
        }
        
        retval &= !iteratorRowThis.hasNext() && !iteratorRowOther.hasNext();
      }
    }
    
    return retval;
  }
}

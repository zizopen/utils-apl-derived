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
package org.omnaest.utils.structure.table.concrete.components;

import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.internal.TableInternal.CellAndStripeResolver;
import org.omnaest.utils.structure.table.internal.TableInternal.ColumnInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.RowInternal;

/**
 * @see CellAndStripeResolver
 * @author Omnaest
 * @param <E>
 */
public abstract class CellAndStripeResolverAbstract<E> implements CellAndStripeResolver<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -5213948878611712685L;
  
  /* ********************************************** Methods ********************************************** */

  @Override
  public Cell<E> resolveCell( int rowIndexPosition, int columnIndexPosition )
  {
    return this.resolveCell( this.resolveRow( rowIndexPosition ), this.resolveColumn( columnIndexPosition ) );
  }
  
  @Override
  public Cell<E> resolveCell( Row<E> row, int columnIndexPosition )
  {
    return this.resolveCell( row, this.resolveColumn( columnIndexPosition ) );
  }
  
  @Override
  public Cell<E> resolveCell( Row<E> row, Object columnTitleValue )
  {
    return this.resolveCell( row, this.resolveColumn( columnTitleValue ) );
  }
  
  @Override
  public Cell<E> resolveCell( int rowIndexPosition, Column<E> column )
  {
    return this.resolveCell( this.resolveRow( rowIndexPosition ), column );
  }
  
  @Override
  public Cell<E> resolveCell( Object rowTitleValue, Column<E> column )
  {
    return this.resolveCell( this.resolveRow( rowTitleValue ), column );
  }
  
  @Override
  public Cell<E> resolveOrCreateCell( int rowIndexPosition, int columnIndexPosition )
  {
    return this.resolveOrCreateCell( this.resolveRow( rowIndexPosition ), this.resolveColumn( columnIndexPosition ) );
  }
  
  @Override
  public Cell<E> resolveOrCreateCell( int rowIndexPosition, ColumnInternal<E> column )
  {
    return this.resolveOrCreateCell( this.resolveOrCreateRow( rowIndexPosition ), column );
  }
  
  @Override
  public Cell<E> resolveOrCreateCell( RowInternal<E> row, int columnIndexPosition )
  {
    return this.resolveOrCreateCell( row, this.resolveOrCreateColumn( columnIndexPosition ) );
  }
  
  @Override
  public Cell<E> resolveCell( int cellIndexPosition )
  {
    //
    int rowIndexPosition = this.determineRowIndexPositionForCellIndexPosition( cellIndexPosition );
    int columnIndexPosition = this.determineColumnIndexPositionForCellIndexPosition( cellIndexPosition );
    
    //
    return this.resolveCell( rowIndexPosition, columnIndexPosition );
  }
  
  @Override
  public Cell<E> resolveOrCreateCell( int cellIndexPosition )
  {
    //
    int rowIndexPosition = this.determineRowIndexPositionForCellIndexPosition( cellIndexPosition );
    int columnIndexPosition = this.determineColumnIndexPositionForCellIndexPosition( cellIndexPosition );
    
    //
    return this.resolveOrCreateCell( rowIndexPosition, columnIndexPosition );
  }
  
  /**
   * @param cellIndexPosition
   * @return
   */
  protected abstract int determineRowIndexPositionForCellIndexPosition( int cellIndexPosition );
  
  /**
   * @param cellIndexPosition
   * @return
   */
  protected abstract int determineColumnIndexPositionForCellIndexPosition( int cellIndexPosition );
  
}

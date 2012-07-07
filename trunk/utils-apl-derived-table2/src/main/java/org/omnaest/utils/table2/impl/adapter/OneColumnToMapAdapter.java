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
package org.omnaest.utils.table2.impl.adapter;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.omnaest.utils.operation.Operation;
import org.omnaest.utils.structure.map.MapAbstract;
import org.omnaest.utils.table2.Cell;
import org.omnaest.utils.table2.Row;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TableIndex;

/**
 * @author Omnaest
 * @param <E>
 */
class OneColumnToMapAdapter<E> extends MapAbstract<E, Set<Row<E>>>
{
  /* ************************************************** Constants *************************************************** */
  private static final long      serialVersionUID = -195168418120399979L;
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  private final Table<E>         table;
  private TableIndex<E, Cell<E>> index;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see OneColumnToMapAdapter
   * @param table
   * @param columnIndexKey
   */
  OneColumnToMapAdapter( Table<E> table, int columnIndexKey )
  {
    super();
    this.table = table;
    
    this.index = this.table.index().of( columnIndexKey );
  }
  
  @Override
  public Set<Row<E>> get( Object key )
  {
    final Set<Row<E>> retset = new LinkedHashSet<Row<E>>();
    
    Operation<Row<E>, Row<E>> operation = new Operation<Row<E>, Row<E>>()
    {
      @Override
      public Row<E> execute( Row<E> row )
      {
        return row;
      }
    };
    
    this.executeOnMatchingRows( key, retset, operation );
    
    return retset;
  }
  
  private void executeOnMatchingRows( Object key, final Set<Row<E>> retset, Operation<Row<E>, Row<E>> operation )
  {
    final Set<Cell<E>> cellSet = this.index.get( key );
    if ( cellSet != null )
    {
      for ( Cell<E> cell : cellSet )
      {
        if ( cell != null )
        {
          Row<E> row = cell.row();
          if ( row != null )
          {
            retset.add( operation.execute( row ) );
          }
        }
      }
    }
  }
  
  @Override
  public Set<Row<E>> put( final E key, final Set<Row<E>> newRowSet )
  {
    final Set<Row<E>> retset = new LinkedHashSet<Row<E>>();
    
    final Iterator<Row<E>> iterator = newRowSet != null ? newRowSet.iterator() : null;
    Operation<Row<E>, Row<E>> operation = new Operation<Row<E>, Row<E>>()
    {
      @Override
      public Row<E> execute( Row<E> row )
      {
        Row<E> rowPrevious = row;
        
        final Row<E> newRow = iterator != null && iterator.hasNext() ? iterator.next() : null;
        row.setCellElements( newRow.getElements() );
        
        return rowPrevious;
      }
    };
    this.executeOnMatchingRows( key, retset, operation );
    
    return retset;
  }
  
  @Override
  public Set<Row<E>> remove( Object key )
  {
    final Set<Row<E>> retset = new LinkedHashSet<Row<E>>();
    
    Operation<Row<E>, Row<E>> operation = new Operation<Row<E>, Row<E>>()
    {
      @Override
      public Row<E> execute( Row<E> row )
      {
        row.remove();
        return row;
      }
    };
    this.executeOnMatchingRows( key, retset, operation );
    
    return retset;
  }
  
  @Override
  public Set<E> keySet()
  {
    return this.index.keySet();
  }
  
  @Override
  public Collection<Set<Row<E>>> values()
  {
    throw new UnsupportedOperationException();
  }
  
}

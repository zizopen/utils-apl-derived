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
class TwoColumnToMapAdapter<E> extends MapAbstract<E, Set<E>>
{
  /* ************************************************** Constants *************************************************** */
  private static final long      serialVersionUID = -195168418120399979L;
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  private final Table<E>         table;
  private final int              columnIndexValue;
  private TableIndex<E, Cell<E>> index;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see TwoColumnToMapAdapter
   * @param table
   * @param columnIndexKey
   * @param columnIndexValue
   */
  TwoColumnToMapAdapter( Table<E> table, int columnIndexKey, int columnIndexValue )
  {
    super();
    this.table = table;
    this.columnIndexValue = columnIndexValue;
    this.index = this.table.index().of( columnIndexKey );
  }
  
  @Override
  public Set<E> get( Object key )
  {
    final Set<E> retset = new LinkedHashSet<E>();
    
    Operation<E, Row<E>> operation = new Operation<E, Row<E>>()
    {
      @Override
      public E execute( Row<E> row )
      {
        final E element = row.getElement( TwoColumnToMapAdapter.this.columnIndexValue );
        return element;
      }
    };
    
    this.executeOnMatchingRows( key, retset, operation );
    
    return retset;
  }
  
  private void executeOnMatchingRows( Object key, final Set<E> retset, Operation<E, Row<E>> operation )
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
  public Set<E> put( final E key, final Set<E> newElementSet )
  {
    final Set<E> retset = new LinkedHashSet<E>();
    
    final Iterator<E> iterator = newElementSet != null ? newElementSet.iterator() : null;
    Operation<E, Row<E>> operation = new Operation<E, Row<E>>()
    {
      @Override
      public E execute( Row<E> row )
      {
        final E elementPrevious = row.getElement( TwoColumnToMapAdapter.this.columnIndexValue );
        
        final E newElement = iterator != null && iterator.hasNext() ? iterator.next() : null;
        row.setElement( TwoColumnToMapAdapter.this.columnIndexValue, newElement );
        
        return elementPrevious;
      }
    };
    this.executeOnMatchingRows( key, retset, operation );
    
    return retset;
  }
  
  @Override
  public Set<E> remove( Object key )
  {
    final Set<E> retset = new LinkedHashSet<E>();
    
    Operation<E, Row<E>> operation = new Operation<E, Row<E>>()
    {
      @Override
      public E execute( Row<E> row )
      {
        E elementPrevious = null;
        final Cell<E> cell = row.cell( TwoColumnToMapAdapter.this.columnIndexValue );
        if ( cell != null )
        {
          elementPrevious = cell.clear();
        }
        return elementPrevious;
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
  public Collection<Set<E>> values()
  {
    throw new UnsupportedOperationException();
  }
  
}

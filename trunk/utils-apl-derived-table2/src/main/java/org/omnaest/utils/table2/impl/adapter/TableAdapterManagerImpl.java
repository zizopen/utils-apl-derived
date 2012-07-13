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

import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.beans.replicator2.BeanReplicator.Declaration;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterIdentity;
import org.omnaest.utils.structure.map.MapUtils;
import org.omnaest.utils.table2.Row;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TableAdapterManager;

/**
 * @author Omnaest
 * @param <E>
 */
public class TableAdapterManagerImpl<E> implements TableAdapterManager<E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long      serialVersionUID = -4405457601074880665L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Table<E>         table;
  private final ExceptionHandler exceptionHandler;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see TableAdapterManagerImpl
   * @param table
   * @param exceptionHandler
   */
  public TableAdapterManagerImpl( Table<E> table, ExceptionHandler exceptionHandler )
  {
    super();
    this.table = table;
    this.exceptionHandler = exceptionHandler;
  }
  
  @Override
  public Map<E, Set<E>> map( int columnIndexKey, int columnIndexValue )
  {
    return new TwoColumnToMapAdapter<E>( this.table, columnIndexKey, columnIndexValue );
  }
  
  @Override
  public Map<E, Set<Row<E>>> map( int columnIndexKey )
  {
    return new OneColumnToMapAdapter<E>( this.table, columnIndexKey );
  }
  
  @Override
  public Map<E, BitSet> rowIndexMap( int columnIndexKey )
  {
    ElementBidirectionalConverter<Set<Row<E>>, BitSet> elementBidirectionalConverterValue = new ElementBidirectionalConverter<Set<Row<E>>, BitSet>()
    {
      @Override
      public BitSet convert( Set<Row<E>> rowSet )
      {
        final BitSet retval = new BitSet();
        if ( rowSet != null )
        {
          for ( Row<E> row : rowSet )
          {
            retval.set( row.index() );
          }
        }
        return retval;
      }
      
      @Override
      public Set<Row<E>> convertBackwards( BitSet element )
      {
        throw new UnsupportedOperationException();
      }
    };
    return MapUtils.adapter( this.map( columnIndexKey ), new ElementConverterIdentity<E>(), elementBidirectionalConverterValue );
  }
  
  @Override
  public <B> List<B> beanList( Class<? extends B> type )
  {
    return new TableToListAdapter<E, B>( this.table, type, this.exceptionHandler );
  }
  
  @Override
  public <B> List<B> beanList( Class<? extends B> type, Declaration declaration )
  {
    return new TableToListAdapter<E, B>( this.table, type, declaration, this.exceptionHandler );
  }
}

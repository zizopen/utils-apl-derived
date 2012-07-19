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

import java.util.Map;

import org.omnaest.utils.beans.replicator.BeanReplicator;
import org.omnaest.utils.beans.replicator.BeanReplicator.Declaration;
import org.omnaest.utils.beans.replicator.BeanReplicator.DeclarationSupport;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.collection.list.ListAbstract;
import org.omnaest.utils.structure.map.MapUtils;
import org.omnaest.utils.table2.Row;
import org.omnaest.utils.table2.Table;

/**
 * @author Omnaest
 * @param <E>
 * @param <B>
 */
class TableToListUsingDTOsAdapter<E, B> extends ListAbstract<B>
{
  /* ************************************************** Constants *************************************************** */
  private static final long            serialVersionUID = -5899940297760214750L;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  @SuppressWarnings("rawtypes")
  private final BeanReplicator<B, Map> beanReplicatorBeanToRow;
  @SuppressWarnings("rawtypes")
  private final BeanReplicator<Map, B> beanReplicatorRowToBean;
  
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  private final Table<E>               table;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see TableToListUsingDTOsAdapter
   * @param table
   *          {@link Table}
   * @param beanType
   * @param declaration
   *          {@link Declaration}
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  TableToListUsingDTOsAdapter( Table<E> table, Class<? extends B> beanType, Declaration declaration,
                               final ExceptionHandler exceptionHandler )
  {
    this.table = table;
    this.beanReplicatorBeanToRow = new BeanReplicator<B, Map>( (Class<B>) beanType, Map.class ).declare( declaration )
                                                                                               .setExceptionHandler( exceptionHandler );
    this.beanReplicatorRowToBean = new BeanReplicator<Map, B>( Map.class, beanType ).declare( declaration )
                                                                                    .setExceptionHandler( exceptionHandler );
  }
  
  /**
   * @see TableToListUsingDTOsAdapter
   * @param table
   *          {@link Table}
   * @param beanType
   *          {@link Class}
   * @param exceptionHandler
   */
  TableToListUsingDTOsAdapter( Table<E> table, Class<? extends B> beanType, final ExceptionHandler exceptionHandler )
  {
    this( table, beanType, new Declaration()
    {
      private static final long serialVersionUID = -5488806350640682134L;
      
      @Override
      public void declare( DeclarationSupport support )
      {
        support.setPreservedDeepnessLevel( 1 );
      }
    }, exceptionHandler );
  }
  
  @Override
  public int size()
  {
    return this.table.rowSize();
  }
  
  @Override
  public boolean add( B bean )
  {
    // 
    final int index = this.size();
    this.add( index, bean );
    return true;
  }
  
  @Override
  public B get( int index )
  {
    final boolean detached = true;
    final Row<E> row = this.table.row( index, detached );
    final Map<String, E> map = row.to().map();
    B retval = this.beanReplicatorRowToBean.clone( map );
    return retval;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public B set( int index, B bean )
  {
    B retval = this.get( index );
    if ( bean != null )
    {
      Map<String, E> map = this.beanReplicatorBeanToRow.clone( bean );
      E[] elements = MapUtils.filteredValues( map, this.table.elementType(), this.table.getColumnTitles() );
      this.table.setRowElements( index, elements );
    }
    return retval;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void add( int index, B bean )
  {
    if ( bean != null )
    {
      Map<String, E> map = this.beanReplicatorBeanToRow.clone( bean );
      E[] elements = MapUtils.filteredValues( map, this.table.elementType(), this.table.getColumnTitles() );
      this.table.addRowElements( index, elements );
    }
  }
  
  @Override
  public B remove( int index )
  {
    B retval = this.get( index );
    this.table.removeRow( index );
    return retval;
  }
  
  @Override
  public int indexOf( Object o )
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public int lastIndexOf( Object o )
  {
    throw new UnsupportedOperationException();
  }
  
}

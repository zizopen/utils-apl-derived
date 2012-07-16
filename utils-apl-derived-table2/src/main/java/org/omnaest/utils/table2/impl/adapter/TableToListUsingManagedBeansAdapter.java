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

import org.omnaest.utils.beans.replicator.BeanReplicator;
import org.omnaest.utils.beans.replicator.BeanReplicator.Declaration;
import org.omnaest.utils.beans.replicator.BeanReplicator.DeclarationSupport;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.collection.list.ListAbstract;
import org.omnaest.utils.table2.Row;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.impl.rowdata.RowDataBasedBeanFactory;

/**
 * @author Omnaest
 * @param <E>
 * @param <B>
 */
class TableToListUsingManagedBeansAdapter<E, B> extends ListAbstract<B>
{
  /* ************************************************** Constants *************************************************** */
  private static final long                serialVersionUID = -5899940297760214750L;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final RowDataBasedBeanFactory<B> beanFactory;
  private final BeanReplicator<B, B>       beanReplicator;
  
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  private final Table<E>                   table;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see TableToListUsingManagedBeansAdapter
   * @param table
   *          {@link Table}
   * @param beanType
   * @param declaration
   *          {@link Declaration}
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   */
  @SuppressWarnings("unchecked")
  TableToListUsingManagedBeansAdapter( Table<E> table, Class<? extends B> beanType, Declaration declaration,
                                       final ExceptionHandler exceptionHandler )
  {
    this.table = table;
    this.beanFactory = new RowDataBasedBeanFactory<B>( (Class<B>) beanType, exceptionHandler );
    this.beanReplicator = new BeanReplicator<B, B>( (Class<B>) beanType, (Class<B>) beanType ).declare( declaration )
                                                                                              .setExceptionHandler( exceptionHandler );
  }
  
  /**
   * @see TableToListUsingManagedBeansAdapter
   * @param table
   *          {@link Table}
   * @param beanType
   *          {@link Class}
   * @param exceptionHandler
   */
  TableToListUsingManagedBeansAdapter( Table<E> table, Class<? extends B> beanType, final ExceptionHandler exceptionHandler )
  {
    this( table, beanType, new Declaration()
    {
      private static final long serialVersionUID = -8682578614622766973L;
      
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
    final Row<E> row = this.table.row( index );
    final B retval = this.beanFactory.build( row );
    return retval;
  }
  
  @Override
  public B set( int index, B bean )
  {
    B accessBean = this.get( index );
    B retval = this.beanReplicator.clone( accessBean );
    if ( bean != null )
    {
      this.beanReplicator.copy( bean, accessBean );
    }
    return retval;
  }
  
  @Override
  public void add( int index, B bean )
  {
    final Row<E> row = this.table.newRow();
    if ( bean != null )
    {
      B accessBean = this.beanFactory.build( row );
      this.beanReplicator.copy( bean, accessBean );
      row.moveTo( index );
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

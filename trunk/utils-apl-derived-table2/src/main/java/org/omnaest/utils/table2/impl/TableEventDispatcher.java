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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerIgnoring;
import org.omnaest.utils.operation.special.OperationVoid;
import org.omnaest.utils.structure.element.ObjectUtils;

/**
 * @author Omnaest
 * @param <E>
 */
class TableEventDispatcher<E> implements TableEventHandler<E>, Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long                                         serialVersionUID = -8336460926560156773L;
  
  private ExceptionHandler                                          exceptionHandler = new ExceptionHandlerIgnoring();
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final transient List<WeakReference<TableEventHandler<E>>> tableEventHandlerReferenceList;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see TableEventDispatcher
   */
  TableEventDispatcher()
  {
    super();
    
    this.tableEventHandlerReferenceList = new CopyOnWriteArrayList<WeakReference<TableEventHandler<E>>>();
  }
  
  public void add( TableEventHandler<E> tableEventHandler )
  {
    if ( tableEventHandler != null )
    {
      this.tableEventHandlerReferenceList.add( new WeakReference<TableEventHandler<E>>( tableEventHandler ) );
    }
  }
  
  /**
   * Executes a given {@link OperationVoid} on all dispatch instances
   * 
   * @param operation
   */
  private void executeOnAllInstances( OperationVoid<TableEventHandler<E>> operation )
  {
    if ( operation != null )
    {
      final List<WeakReference<TableEventHandler<E>>> removableInstanceList = new ArrayList<WeakReference<TableEventHandler<E>>>();
      for ( WeakReference<TableEventHandler<E>> reference : this.tableEventHandlerReferenceList )
      {
        if ( reference != null )
        {
          final TableEventHandler<E> tableEventHandler = reference.get();
          if ( tableEventHandler == null )
          {
            removableInstanceList.add( reference );
          }
          else
          {
            try
            {
              operation.execute( tableEventHandler );
            }
            catch ( Exception e )
            {
              this.exceptionHandler.handleException( e );
            }
          }
        }
      }
      this.tableEventHandlerReferenceList.removeAll( removableInstanceList );
    }
  }
  
  @Override
  public void handleAddedColumn( final int columnIndex, final E... elements )
  {
    this.executeOnAllInstances( new OperationVoid<TableEventHandler<E>>()
    {
      @Override
      public void execute( TableEventHandler<E> tableEventHandler )
      {
        tableEventHandler.handleAddedColumn( columnIndex, elements );
      }
    } );
  }
  
  @Override
  public void handleAddedRow( final int rowIndex, final E... elements )
  {
    this.executeOnAllInstances( new OperationVoid<TableEventHandler<E>>()
    {
      @Override
      public void execute( TableEventHandler<E> tableEventHandler )
      {
        tableEventHandler.handleAddedRow( rowIndex, elements );
      }
    } );
  }
  
  @Override
  public void handleClearTable()
  {
    this.executeOnAllInstances( new OperationVoid<TableEventHandler<E>>()
    {
      @Override
      public void execute( TableEventHandler<E> tableEventHandler )
      {
        tableEventHandler.handleClearTable();
      }
    } );
  }
  
  @Override
  public void handleRemovedColumn( final int columnIndex, final E[] previousElements, final String columnTitle )
  {
    this.executeOnAllInstances( new OperationVoid<TableEventHandler<E>>()
    {
      @Override
      public void execute( TableEventHandler<E> tableEventHandler )
      {
        tableEventHandler.handleRemovedColumn( columnIndex, previousElements, columnTitle );
      }
    } );
  }
  
  @Override
  public void handleRemovedRow( final int rowIndex, final E[] previousElements, final String rowTitle )
  {
    this.executeOnAllInstances( new OperationVoid<TableEventHandler<E>>()
    {
      @Override
      public void execute( TableEventHandler<E> tableEventHandler )
      {
        tableEventHandler.handleRemovedRow( rowIndex, previousElements, rowTitle );
      }
    } );
  }
  
  @Override
  public void handleUpdatedCell( final int rowIndex, final int columnIndex, final E element, final E previousElement )
  {
    this.executeOnAllInstances( new OperationVoid<TableEventHandler<E>>()
    {
      @Override
      public void execute( TableEventHandler<E> tableEventHandler )
      {
        tableEventHandler.handleUpdatedCell( rowIndex, columnIndex, element, previousElement );
      }
    } );
  }
  
  @Override
  public void handleUpdatedRow( final int rowIndex, final E[] elements, final E[] previousElements, final BitSet modifiedIndices )
  {
    this.executeOnAllInstances( new OperationVoid<TableEventHandler<E>>()
    {
      @Override
      public void execute( TableEventHandler<E> tableEventHandler )
      {
        tableEventHandler.handleUpdatedRow( rowIndex, elements, previousElements, modifiedIndices );
      }
    } );
  }
  
  @SuppressWarnings("static-method")
  private Object readResolve() throws ObjectStreamException
  {
    return new TableEventDispatcher<E>();
  }
  
  /**
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   * @return this
   */
  public TableEventDispatcher<E> setExceptionHandler( ExceptionHandler exceptionHandler )
  {
    this.exceptionHandler = ObjectUtils.defaultIfNull( exceptionHandler, new ExceptionHandlerIgnoring() );
    return this;
  }
  
}

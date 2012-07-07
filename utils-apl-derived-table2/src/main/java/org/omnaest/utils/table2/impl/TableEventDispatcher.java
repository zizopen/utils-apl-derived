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
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerIgnoring;
import org.omnaest.utils.operation.special.OperationVoid;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverterWeakReference;

/**
 * @author Omnaest
 * @param <E>
 */
class TableEventDispatcher<E> implements TableEventHandler<E>, Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long                          serialVersionUID = -8336460926560156773L;
  
  private ExceptionHandler                           exceptionHandler = new ExceptionHandlerIgnoring();
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final transient List<TableEventHandler<E>> instanceList;
  
  /* *************************************************** Methods **************************************************** */
  
  public TableEventDispatcher()
  {
    super();
    
    this.instanceList = ListUtils.adapter( new CopyOnWriteArrayList<WeakReference<TableEventHandler<E>>>(),
                                           new ElementBidirectionalConverterWeakReference<TableEventHandler<E>>() );
  }
  
  public void add( TableEventHandler<E> tableEventHandler )
  {
    if ( tableEventHandler != null )
    {
      this.instanceList.add( tableEventHandler );
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
      for ( TableEventHandler<E> instance : this.instanceList )
      {
        if ( instance != null )
        {
          try
          {
            operation.execute( instance );
          }
          catch ( Exception e )
          {
            this.exceptionHandler.handleException( e );
          }
        }
      }
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
  public void handleRemovedColumn( final int columnIndex, final E[] previousElements )
  {
    this.executeOnAllInstances( new OperationVoid<TableEventHandler<E>>()
    {
      @Override
      public void execute( TableEventHandler<E> tableEventHandler )
      {
        tableEventHandler.handleRemovedColumn( columnIndex, previousElements );
      }
    } );
  }
  
  @Override
  public void handleRemovedRow( final int rowIndex, final E[] previousElements )
  {
    this.executeOnAllInstances( new OperationVoid<TableEventHandler<E>>()
    {
      @Override
      public void execute( TableEventHandler<E> tableEventHandler )
      {
        tableEventHandler.handleRemovedRow( rowIndex, previousElements );
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

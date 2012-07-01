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

import java.lang.ref.WeakReference;
import java.util.BitSet;
import java.util.concurrent.CopyOnWriteArrayList;

import org.omnaest.utils.dispatcher.DispatcherAbstract;
import org.omnaest.utils.operation.special.OperationVoid;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverterWeakReference;

/**
 * @author Omnaest
 * @param <E>
 */
class TableEventDispatcher<E> extends DispatcherAbstract<TableEventHandler<E>> implements TableEventHandler<E>
{
  public TableEventDispatcher()
  {
    super( ListUtils.adapter( new CopyOnWriteArrayList<WeakReference<TableEventHandler<E>>>(),
                              new ElementBidirectionalConverterWeakReference<TableEventHandler<E>>() ) );
  }
  
  public void add( TableEventHandler<E> tableEventHandler )
  {
    if ( tableEventHandler != null )
    {
      this.instanceList.add( tableEventHandler );
    }
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
  
}

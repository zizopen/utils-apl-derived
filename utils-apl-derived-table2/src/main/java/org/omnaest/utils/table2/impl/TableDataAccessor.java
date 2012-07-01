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

import java.util.ConcurrentModificationException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.omnaest.utils.operation.OperationUtils;
import org.omnaest.utils.operation.special.OperationIntrinsic;
import org.omnaest.utils.operation.special.OperationWithResult;

/**
 * Internal data core facade used by the {@link ArrayTable}
 * 
 * @author Omnaest
 * @param <E>
 */
class TableDataAccessor<E>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final AtomicLong              modificationCounter = new AtomicLong();
  private final ReadWriteLock           tableLock           = new ReentrantReadWriteLock( true );
  
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  private final TableDataCore<E>        tableDataCore;
  private final TableEventDispatcher<E> tableEventDispatcher;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * The {@link ModificationValidator} returns true for {@link #hasBeenModified()} if there was any write operation to the table
   * data since the {@link ModificationValidator} was created
   * 
   * @author Omnaest
   */
  public class ModificationValidator
  {
    private long modificationCounterAtCreationTime = TableDataAccessor.this.modificationCounter.get();
    
    public boolean hasBeenModified()
    {
      return this.modificationCounterAtCreationTime != TableDataAccessor.this.modificationCounter.get();
    }
    
    /**
     * This validates and throws a {@link ConcurrentModificationException} if {@link #hasBeenModified()} is true
     * 
     * @throws ConcurrentModificationException
     */
    public void validateForNoModification() throws ConcurrentModificationException
    {
      if ( this.hasBeenModified() )
      {
        throw new ConcurrentModificationException( "Table data has been modified" );
      }
    }
  }
  
  /* *************************************************** Methods **************************************************** */
  
  public TableDataAccessor( TableDataCore<E> tableDataCore, TableEventDispatcher<E> tableEventDispatcher )
  {
    super();
    this.tableDataCore = tableDataCore;
    this.tableEventDispatcher = tableEventDispatcher;
  }
  
  public int columnSize()
  {
    return OperationUtils.executeWithLocks( new OperationWithResult<Integer>()
    {
      @Override
      public Integer execute()
      {
        return TableDataAccessor.this.tableDataCore.columnSize();
      }
    }, this.tableLock.readLock() );
  }
  
  public E getElement( final int rowIndex, final int columnIndex )
  {
    return OperationUtils.executeWithLocks( new OperationWithResult<E>()
    {
      @Override
      public E execute()
      {
        return TableDataAccessor.this.tableDataCore.getElement( rowIndex, columnIndex );
      }
    }, this.tableLock.readLock() );
  }
  
  public void addRow( final E[] elements )
  {
    OperationUtils.executeWithLocks( new OperationIntrinsic()
    {
      @Override
      public void execute()
      {
        final int rowIndex = TableDataAccessor.this.tableDataCore.addRow( elements );
        TableDataAccessor.this.modificationCounter.incrementAndGet();
        TableDataAccessor.this.tableEventDispatcher.handleAddedRow( rowIndex, elements );
      }
    }, this.tableLock.writeLock() );
  }
  
  public void addRow( final int rowIndex, final E... elements )
  {
    OperationUtils.executeWithLocks( new OperationIntrinsic()
    {
      @Override
      public void execute()
      {
        TableDataAccessor.this.tableDataCore.addRow( rowIndex, elements );
        TableDataAccessor.this.modificationCounter.incrementAndGet();
        TableDataAccessor.this.tableEventDispatcher.handleAddedRow( rowIndex, elements );
      }
    }, this.tableLock.writeLock() );
  }
  
  public void set( final E element, final int rowIndex, final int columnIndex )
  {
    OperationUtils.executeWithLocks( new OperationIntrinsic()
    {
      @Override
      public void execute()
      {
        E previousElement = TableDataAccessor.this.tableDataCore.set( element, rowIndex, columnIndex );
        TableDataAccessor.this.modificationCounter.incrementAndGet();
        TableDataAccessor.this.tableEventDispatcher.handleUpdatedCell( rowIndex, columnIndex, element, previousElement );
      }
    }, this.tableLock.writeLock() );
  }
  
  public void clear()
  {
    OperationUtils.executeWithLocks( new OperationIntrinsic()
    {
      @Override
      public void execute()
      {
        TableDataAccessor.this.tableDataCore.clear();
        TableDataAccessor.this.modificationCounter.incrementAndGet();
        TableDataAccessor.this.tableEventDispatcher.handleClearTable();
      }
    }, this.tableLock.writeLock() );
  }
  
  public int rowSize()
  {
    return OperationUtils.executeWithLocks( new OperationWithResult<Integer>()
    {
      @Override
      public Integer execute()
      {
        return TableDataAccessor.this.tableDataCore.rowSize();
      }
    }, this.tableLock.readLock() );
  }
  
  /**
   * Returns a new {@link ModificationValidator} instance
   * 
   * @return
   */
  public ModificationValidator newModificationValidator()
  {
    return new ModificationValidator();
  }
  
  /**
   * Registers a given {@link TableEventHandler} instance. The registration uses weak references, so for any given instance at
   * least one reference must be kept externally
   * 
   * @param tableEventHandler
   * @return the given instance
   */
  @SuppressWarnings("javadoc")
  public <T extends TableEventHandler<E>> T register( T tableEventHandler )
  {
    this.tableEventDispatcher.add( tableEventHandler );
    return tableEventHandler;
  }
  
}

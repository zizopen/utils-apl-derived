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

import java.io.Serializable;
import java.util.BitSet;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.operation.OperationUtils;
import org.omnaest.utils.operation.special.OperationIntrinsic;
import org.omnaest.utils.operation.special.OperationWithResult;
import org.omnaest.utils.structure.array.ArrayUtils;

/**
 * Internal data core facade used by the {@link ArrayTable}
 * 
 * @author Omnaest
 * @param <E>
 */
class TableDataAccessor<E> implements Serializable
{
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
  
  /* ************************************************** Constants *************************************************** */
  private static final long             serialVersionUID    = -9123078800733926152L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final AtomicLong              modificationCounter = new AtomicLong();
  
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  private final TableDataCore<E>        tableDataCore;
  private final TableEventDispatcher<E> tableEventDispatcher;
  private final ReadWriteLock           tableLock           = new ReentrantReadWriteLock( true );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  private final TableMetaData<E>        tableMetaData;
  
  /* *************************************************** Methods **************************************************** */
  
  public TableDataAccessor( TableDataCore<E> tableDataCore, TableEventDispatcher<E> tableEventDispatcher,
                            TableMetaData<E> tableMetaData )
  {
    super();
    this.tableDataCore = tableDataCore;
    this.tableEventDispatcher = tableEventDispatcher;
    this.tableMetaData = tableMetaData;
    
    this.register( tableMetaData );
  }
  
  public void addColumn( final E... elements )
  {
    OperationUtils.executeWithLocks( new OperationIntrinsic()
    {
      @Override
      public void execute()
      {
        int rowIndex = TableDataAccessor.this.tableDataCore.addRow( elements );
        TableDataAccessor.this.modificationCounter.incrementAndGet();
        TableDataAccessor.this.tableEventDispatcher.handleAddedRow( rowIndex, elements );
      }
    }, this.tableLock.writeLock() );
  }
  
  public void addColumn( final int columnIndex, final E... elements )
  {
    OperationUtils.executeWithLocks( new OperationIntrinsic()
    {
      @Override
      public void execute()
      {
        TableDataAccessor.this.tableDataCore.addColumn( columnIndex, elements );
        TableDataAccessor.this.modificationCounter.incrementAndGet();
        TableDataAccessor.this.tableEventDispatcher.handleAddedColumn( columnIndex, elements );
      }
    }, this.tableLock.writeLock() );
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
  
  public int getColumnIndex( final Pattern columnTitlePattern )
  {
    return OperationUtils.executeWithLocks( new OperationWithResult<Integer>()
    {
      @Override
      public Integer execute()
      {
        return TableDataAccessor.this.tableMetaData.getColumnIndex( columnTitlePattern );
      }
    }, this.tableLock.readLock() );
  }
  
  public int getColumnIndex( final String columnTitle )
  {
    return OperationUtils.executeWithLocks( new OperationWithResult<Integer>()
    {
      @Override
      public Integer execute()
      {
        return TableDataAccessor.this.tableMetaData.getColumnIndex( columnTitle );
      }
    }, this.tableLock.readLock() );
  }
  
  public BitSet getColumnIndexFilter( final Pattern columnTitlePattern )
  {
    return OperationUtils.executeWithLocks( new OperationWithResult<BitSet>()
    {
      @Override
      public BitSet execute()
      {
        return TableDataAccessor.this.tableMetaData.getColumnIndexFilter( columnTitlePattern );
      }
    }, this.tableLock.readLock() );
  }
  
  public BitSet getColumnIndexFilter( final Set<String> columnTitleSet )
  {
    return OperationUtils.executeWithLocks( new OperationWithResult<BitSet>()
    {
      @Override
      public BitSet execute()
      {
        return TableDataAccessor.this.tableMetaData.getColumnIndexFilter( columnTitleSet );
      }
    }, this.tableLock.readLock() );
  }
  
  public String getColumnTitle( final int columnIndex )
  {
    return OperationUtils.executeWithLocks( new OperationWithResult<String>()
    {
      @Override
      public String execute()
      {
        return TableDataAccessor.this.tableMetaData.getColumnTitle( columnIndex );
      }
    }, this.tableLock.readLock() );
  }
  
  public List<String> getColumnTitleList()
  {
    return OperationUtils.executeWithLocks( new OperationWithResult<List<String>>()
    {
      @Override
      public List<String> execute()
      {
        return TableDataAccessor.this.tableMetaData.getColumnTitleList();
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
  
  public int getRowIndex( final String rowTitle )
  {
    return OperationUtils.executeWithLocks( new OperationWithResult<Integer>()
    {
      @Override
      public Integer execute()
      {
        return TableDataAccessor.this.tableMetaData.getRowIndex( rowTitle );
      }
    }, this.tableLock.readLock() );
  }
  
  public String getRowTitle( final int rowIndex )
  {
    return OperationUtils.executeWithLocks( new OperationWithResult<String>()
    {
      @Override
      public String execute()
      {
        return TableDataAccessor.this.tableMetaData.getRowTitle( rowIndex );
      }
    }, this.tableLock.readLock() );
  }
  
  public List<String> getRowTitleList()
  {
    return OperationUtils.executeWithLocks( new OperationWithResult<List<String>>()
    {
      @Override
      public List<String> execute()
      {
        return TableDataAccessor.this.tableMetaData.getRowTitleList();
      }
    }, this.tableLock.readLock() );
  }
  
  public ReadWriteLock getTableLock()
  {
    return this.tableLock;
  }
  
  public String getTableName()
  {
    return OperationUtils.executeWithLocks( new OperationWithResult<String>()
    {
      @Override
      public String execute()
      {
        return TableDataAccessor.this.tableMetaData.getTableName();
      }
    }, this.tableLock.readLock() );
  }
  
  public boolean hasColumnTitles()
  {
    return OperationUtils.executeWithLocks( new OperationWithResult<Boolean>()
    {
      @Override
      public Boolean execute()
      {
        return TableDataAccessor.this.tableMetaData.hasColumnTitles();
      }
    }, this.tableLock.readLock() );
  }
  
  public boolean hasRowTitles()
  {
    return OperationUtils.executeWithLocks( new OperationWithResult<Boolean>()
    {
      @Override
      public Boolean execute()
      {
        return TableDataAccessor.this.tableMetaData.hasRowTitles();
      }
    }, this.tableLock.readLock() );
  }
  
  public boolean hasTableName()
  {
    return OperationUtils.executeWithLocks( new OperationWithResult<Boolean>()
    {
      @Override
      public Boolean execute()
      {
        return TableDataAccessor.this.tableMetaData.hasTableName();
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
  
  public void removeColumn( final int columnIndex )
  {
    OperationUtils.executeWithLocks( new OperationIntrinsic()
    {
      @Override
      public void execute()
      {
        final E[] previousElements = TableDataAccessor.this.tableDataCore.removeColumn( columnIndex );
        final String columnTitle = TableDataAccessor.this.getColumnTitle( columnIndex );
        ;
        TableDataAccessor.this.modificationCounter.incrementAndGet();
        TableDataAccessor.this.tableEventDispatcher.handleRemovedColumn( columnIndex, previousElements, columnTitle );
      }
    }, this.tableLock.writeLock() );
  }
  
  public void removeRow( final int rowIndex )
  {
    OperationUtils.executeWithLocks( new OperationIntrinsic()
    {
      @Override
      public void execute()
      {
        final E[] previousElements = TableDataAccessor.this.tableDataCore.removeRow( rowIndex );
        final String rowTitle = TableDataAccessor.this.getRowTitle( rowIndex );
        TableDataAccessor.this.modificationCounter.incrementAndGet();
        TableDataAccessor.this.tableEventDispatcher.handleRemovedRow( rowIndex, previousElements, rowTitle );
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
  
  public void set( final E element, final int rowIndex, final int columnIndex )
  {
    OperationUtils.executeWithLocks( new OperationIntrinsic()
    {
      @Override
      public void execute()
      {
        E previousElement = TableDataAccessor.this.tableDataCore.set( element, rowIndex, columnIndex );
        TableDataAccessor.this.modificationCounter.incrementAndGet();
        
        if ( !ObjectUtils.equals( element, previousElement ) )
        {
          TableDataAccessor.this.tableEventDispatcher.handleUpdatedCell( rowIndex, columnIndex, element, previousElement );
        }
      }
    }, this.tableLock.writeLock() );
  }
  
  public void setColumnTitle( final int columnIndex, final String columnTitle )
  {
    OperationUtils.executeWithLocks( new OperationWithResult<Void>()
    {
      @Override
      public Void execute()
      {
        TableDataAccessor.this.tableMetaData.setColumnTitle( columnIndex, columnTitle );
        return null;
      }
    }, this.tableLock.writeLock() );
  }
  
  public void setColumnTitles( final Iterable<String> columnTitleIterable )
  {
    OperationUtils.executeWithLocks( new OperationWithResult<Void>()
    {
      @Override
      public Void execute()
      {
        TableDataAccessor.this.tableMetaData.setColumnTitles( columnTitleIterable );
        return null;
      }
    }, this.tableLock.writeLock() );
  }
  
  public TableDataAccessor<E> setExceptionHandler( ExceptionHandler exceptionHandler )
  {
    this.tableEventDispatcher.setExceptionHandler( exceptionHandler );
    return this;
  }
  
  public void setRow( final int rowIndex, final E... elements )
  {
    OperationUtils.executeWithLocks( new OperationIntrinsic()
    {
      @Override
      public void execute()
      {
        E[] previousElements = TableDataAccessor.this.tableDataCore.setRow( rowIndex, elements );
        TableDataAccessor.this.modificationCounter.incrementAndGet();
        
        final BitSet modifiedIndices = ArrayUtils.differenceBitSet( elements, previousElements );
        if ( modifiedIndices.cardinality() > 0 )
        {
          TableDataAccessor.this.tableEventDispatcher.handleUpdatedRow( rowIndex, elements, previousElements, modifiedIndices );
        }
      }
    }, this.tableLock.writeLock() );
  }
  
  public void setRowTitle( final int rowIndex, final String rowTitle )
  {
    OperationUtils.executeWithLocks( new OperationWithResult<Void>()
    {
      @Override
      public Void execute()
      {
        TableDataAccessor.this.tableMetaData.setRowTitle( rowIndex, rowTitle );
        return null;
      }
    }, this.tableLock.writeLock() );
  }
  
  public void setRowTitles( final Iterable<String> rowTitleIterable )
  {
    OperationUtils.executeWithLocks( new OperationWithResult<Void>()
    {
      @Override
      public Void execute()
      {
        TableDataAccessor.this.tableMetaData.setRowTitles( rowTitleIterable );
        return null;
      }
    }, this.tableLock.writeLock() );
  }
  
  public void setTableName( final String tableTitle )
  {
    OperationUtils.executeWithLocks( new OperationWithResult<Void>()
    {
      @Override
      public Void execute()
      {
        TableDataAccessor.this.tableMetaData.setTableName( tableTitle );
        return null;
      }
    }, this.tableLock.writeLock() );
  }
  
}

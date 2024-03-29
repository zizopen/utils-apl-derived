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
package org.omnaest.utils.table.impl;

import java.io.File;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.events.exception.ExceptionHandlerSerializable;
import org.omnaest.utils.operation.special.OperationVoid;
import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.table.ImmutableRow;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.TableEventHandler;
import org.omnaest.utils.table.TablePersistence;
import org.omnaest.utils.table.TablePersistenceRegistration;
import org.omnaest.utils.table.impl.persistence.SimpleDirectoryBasedTablePersistenceUsingJAXB;
import org.omnaest.utils.table.impl.persistence.SimpleDirectoryBasedTablePersistenceUsingSerializable;
import org.omnaest.utils.table.impl.persistence.SimpleDirectoryBasedTablePersistenceUsingXStream;
import org.omnaest.utils.table.impl.persistence.SimpleFileBasedTablePersistence;
import org.omnaest.utils.tuple.KeyValue;

/**
 * {@link TablePersistenceRegistration} implementation
 * 
 * @author Omnaest
 * @param <E>
 */
final class TablePersistenceRegistrationImpl<E> implements TablePersistenceRegistration<E>, TableEventHandler<E>
{
  private static final long              serialVersionUID    = -8588863418066581642L;
  
  private final Table<E>                 table;
  private final ReadWriteLock            tableLock;
  private final Set<TablePersistence<E>> tablePersistenceSet = new LinkedHashSet<TablePersistence<E>>();
  private ExceptionHandlerSerializable   exceptionHandler;
  
  /**
   * @see TablePersistenceRegistrationImpl
   * @param table
   * @param tableLock
   * @param exceptionHandler
   */
  TablePersistenceRegistrationImpl( Table<E> table, ReadWriteLock tableLock, ExceptionHandlerSerializable exceptionHandler )
  {
    this.table = table;
    this.tableLock = tableLock;
    this.exceptionHandler = exceptionHandler;
  }
  
  @Override
  public Table<E> attachToFile( File file )
  {
    return this.attach( new SimpleFileBasedTablePersistence<E>( file, this.exceptionHandler ) );
  }
  
  @Override
  public Table<E> attach( TablePersistence<E> tablePersistence )
  {
    if ( tablePersistence != null )
    {
      final Lock writeLock = this.tableLock.writeLock();
      writeLock.lock();
      try
      {
        this.synchronizeTableWithPersistence( tablePersistence );
        this.tablePersistenceSet.add( tablePersistence );
      }
      finally
      {
        writeLock.unlock();
      }
      
    }
    return this.table;
  }
  
  @Override
  public Table<E> detach( TablePersistence<E> tablePersistence )
  {
    if ( tablePersistence != null )
    {
      this.tablePersistenceSet.remove( tablePersistence );
    }
    return this.table;
  }
  
  private void executeOnAllTablePersistenceInstances( OperationVoid<TablePersistence<E>> operation )
  {
    for ( TablePersistence<E> tablePersistence : this.tablePersistenceSet )
    {
      operation.execute( tablePersistence );
    }
  }
  
  @Override
  public void handleAddedColumn( int columnIndex, E... elements )
  {
    this.updateAllRows();
  }
  
  @Override
  public void handleAddedRow( final int rowIndex, final E... elements )
  {
    final Table<E> table = this.table;
    final int rowIndexFrom = rowIndex;
    final int rowIndexTo = table.rowSize();
    this.executeOnAllTablePersistenceInstances( new OperationVoid<TablePersistence<E>>()
    {
      @Override
      public void execute( TablePersistence<E> tablePersistence )
      {
        for ( int ii = rowIndexFrom + 1; ii < rowIndexTo; ii++ )
        {
          tablePersistence.remove( ii - 1 );
        }
        for ( int ii = rowIndexFrom; ii < rowIndexTo; ii++ )
        {
          final boolean detached = true;
          tablePersistence.add( ii, table.row( ii, detached ).getElements() );
        }
      }
    } );
    
  }
  
  @Override
  public void handleClearTable()
  {
    this.executeOnAllTablePersistenceInstances( new OperationVoid<TablePersistence<E>>()
    {
      @Override
      public void execute( TablePersistence<E> tablePersistence )
      {
        tablePersistence.removeAll();
      }
    } );
  }
  
  @Override
  public void handleRemovedColumn( int columnIndex, E[] previousElements, String columnTitle )
  {
    this.updateAllRows();
  }
  
  @Override
  public void handleRemovedRow( final int rowIndex, E[] previousElements, String rowTitle )
  {
    final Table<E> table = this.table;
    final int rowIndexFrom = rowIndex;
    final int rowIndexTo = table.rowSize();
    this.executeOnAllTablePersistenceInstances( new OperationVoid<TablePersistence<E>>()
    {
      @Override
      public void execute( TablePersistence<E> tablePersistence )
      {
        tablePersistence.remove( rowIndex );
        for ( int ii = rowIndexFrom; ii < rowIndexTo; ii++ )
        {
          tablePersistence.remove( ii + 1 );
        }
        for ( int ii = rowIndexFrom; ii < rowIndexTo; ii++ )
        {
          final boolean detached = true;
          tablePersistence.add( ii, table.row( ii, detached ).getElements() );
        }
      }
    } );
  }
  
  @Override
  public void handleUpdatedCell( final int rowIndex, int columnIndex, E element, E previousElement )
  {
    this.updateSingleRow( rowIndex );
  }
  
  @Override
  public void handleUpdatedRow( int rowIndex, E[] elements, E[] previousElements, BitSet modifiedIndices )
  {
    this.updateSingleRow( rowIndex );
  }
  
  private void synchronizeTableWithPersistence( TablePersistence<E> tablePersistence )
  {
    //
    tablePersistence.setTableName( this.table.getTableName() );
    tablePersistence.setColumnTitles( this.table.getColumnTitles() );
    
    //
    final Set<Integer> rowIndexSet = new HashSet<Integer>();
    final Iterable<KeyValue<Integer, E[]>> allElements = tablePersistence.allElements();
    if ( allElements != null )
    {
      for ( KeyValue<Integer, E[]> keyValue : allElements )
      {
        final Integer rowIndex = keyValue.getKey();
        final E[] elements = keyValue.getValue();
        Assert.isNotNull( rowIndex, "row index position must not be null" );
        
        rowIndexSet.add( rowIndex );
        this.table.setRowElements( rowIndex, elements );
      }
    }
    
    for ( ImmutableRow<E> row : this.table )
    {
      final int rowIndex = row.index();
      if ( !rowIndexSet.contains( rowIndex ) )
      {
        final E[] elements = row.getElements();
        tablePersistence.add( rowIndex, elements );
      }
    }
  }
  
  private void updateAllRows()
  {
    final Table<E> table = this.table;
    this.executeOnAllTablePersistenceInstances( new OperationVoid<TablePersistence<E>>()
    {
      @Override
      public void execute( TablePersistence<E> tablePersistence )
      {
        for ( int id = 0; id < table.rowSize(); id++ )
        {
          final E[] elements = table.row( id ).getElements();
          tablePersistence.update( id, elements );
        }
      }
    } );
  }
  
  private void updateSingleRow( final int rowIndex )
  {
    final Table<E> table = this.table;
    this.executeOnAllTablePersistenceInstances( new OperationVoid<TablePersistence<E>>()
    {
      @Override
      public void execute( TablePersistence<E> tablePersistence )
      {
        final E[] elements = table.row( rowIndex ).getElements();
        tablePersistence.update( rowIndex, elements );
      }
    } );
  }
  
  @Override
  public TablePersistenceAttacher<E> attach()
  {
    final ExceptionHandlerSerializable exceptionHandler = this.exceptionHandler;
    final Table<E> table = this.table;
    return new TablePersistenceAttacher<E>()
    {
      private static final long serialVersionUID = 836395788258554075L;
      
      @Override
      public TablePersistenceAttacherTarget<E> asSerialized()
      {
        return new TablePersistenceAttacherTarget<E>()
        {
          private static final long serialVersionUID = -7912388527497053782L;
          
          @Override
          public Table<E> toDirectory( File directory )
          {
            return attach( new SimpleDirectoryBasedTablePersistenceUsingSerializable<E>( directory, exceptionHandler ) );
          }
        };
      }
      
      @Override
      public TablePersistenceAttacherXML<E> asXML()
      {
        return new TablePersistenceAttacherXML<E>()
        {
          private static final long serialVersionUID = 9203977272669395577L;
          
          @Override
          public TablePersistenceAttacherTarget<E> usingXStream()
          {
            return new TablePersistenceAttacherTarget<E>()
            {
              private static final long serialVersionUID = 5729367299715389727L;
              
              @Override
              public Table<E> toDirectory( File directory )
              {
                return attach( new SimpleDirectoryBasedTablePersistenceUsingXStream<E>( directory, exceptionHandler ) );
              }
            };
          }
          
          @Override
          public TablePersistenceAttacherTarget<E> usingJAXB()
          {
            return new TablePersistenceAttacherTarget<E>()
            {
              private static final long serialVersionUID = -7728346094938256974L;
              
              @Override
              public Table<E> toDirectory( File directory )
              {
                return attach( new SimpleDirectoryBasedTablePersistenceUsingJAXB<E>( directory, exceptionHandler,
                                                                                     ArrayUtils.arrayType( table.elementType() ) ) );
              }
            };
          }
        };
      }
    };
  }
  
  @Override
  public void handleModifiedColumnTitle( int columnIndex, String columnTitle, String columnTitlePrevious )
  {
    final Table<E> table = this.table;
    this.executeOnAllTablePersistenceInstances( new OperationVoid<TablePersistence<E>>()
    {
      @Override
      public void execute( TablePersistence<E> tablePersistence )
      {
        tablePersistence.setColumnTitles( table.getColumnTitles() );
      }
    } );
  }
  
  @Override
  public void handleModifiedRowTitle( int rowIndex, String rowTitle, String rowTitlePrevious )
  {
  }
  
  @Override
  public void handleModifiedColumnTitles( final String[] columnTitles, String[] columnTitlesPrevious )
  {
    this.executeOnAllTablePersistenceInstances( new OperationVoid<TablePersistence<E>>()
    {
      @Override
      public void execute( TablePersistence<E> tablePersistence )
      {
        tablePersistence.setColumnTitles( columnTitles );
      }
    } );
  }
  
  @Override
  public void handleModifiedRowTitles( String[] rowTitles, String[] rowTitlesPrevious )
  {
  }
  
  @Override
  public void handleModifiedTableName( final String tableName, String tableNamePrevious )
  {
    this.executeOnAllTablePersistenceInstances( new OperationVoid<TablePersistence<E>>()
    {
      @Override
      public void execute( TablePersistence<E> tablePersistence )
      {
        tablePersistence.setTableName( tableName );
      }
    } );
    
  }
}

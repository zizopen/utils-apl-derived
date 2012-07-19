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

import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.operation.OperationUtils;
import org.omnaest.utils.operation.special.OperationIntrinsic;
import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterSerializable;
import org.omnaest.utils.structure.element.factory.Factory;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.structure.iterator.IteratorUtils;
import org.omnaest.utils.table.Cell;
import org.omnaest.utils.table.Column;
import org.omnaest.utils.table.ImmutableRow;
import org.omnaest.utils.table.ImmutableStripe;
import org.omnaest.utils.table.ImmutableTable;
import org.omnaest.utils.table.Row;
import org.omnaest.utils.table.StripeTransformerPlugin;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.TableAdapterManager;
import org.omnaest.utils.table.TableDataSource;
import org.omnaest.utils.table.TableDataSourceCopier;
import org.omnaest.utils.table.TableEventHandler;
import org.omnaest.utils.table.TableExecution;
import org.omnaest.utils.table.TableIndexManager;
import org.omnaest.utils.table.TablePersistenceRegistration;
import org.omnaest.utils.table.TableSelect;
import org.omnaest.utils.table.TableSorter;
import org.omnaest.utils.table.impl.adapter.TableAdapterManagerImpl;
import org.omnaest.utils.table.impl.join.TableSelectImpl;

/**
 * {@link Table} implementation based on an two dimensional array
 * 
 * @author Omnaest
 * @param <E>
 */
public class ArrayTable<E> extends TableAbstract<E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long                       serialVersionUID = 6360131663629436319L;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Class<E>                          elementType;
  private final TableAdapterManager<E>            tableAdapterManager;
  private final TableDataAccessor<E>              tableDataAccessor;
  private final TableIndexManager<E, Cell<E>>     tableIndexManager;
  private final TablePersistenceRegistration<E>   tablePersistenceRegistration;
  private final StripeTransformerPluginManager<E> stripeTransformerPluginManager;
  
  /* *************************************************** Methods **************************************************** */
  
  @SuppressWarnings("unchecked")
  public ArrayTable( Class<? extends E> elementType )
  {
    super();
    
    Assert.isNotNull( elementType, "The table element type must not be null" );
    
    this.elementType = (Class<E>) elementType;
    
    final TableMetaData<E> tableMetaData = new TableMetaData<E>();
    final TableDataCore<E> tableDataCore = new TableDataCore<E>( elementType );
    final TableEventDispatcher<E> tableEventDispatcher = new TableEventDispatcher<E>();
    this.tableDataAccessor = new TableDataAccessor<E>( tableDataCore, tableEventDispatcher, tableMetaData ).setExceptionHandler( this.exceptionHandler );
    this.tableIndexManager = new TableIndexManagerImpl<E>( this.tableDataAccessor, this, this.exceptionHandler );
    this.tableAdapterManager = new TableAdapterManagerImpl<E>( this, this.exceptionHandler );
    this.tablePersistenceRegistration = this.tableDataAccessor.register( new TablePersistenceRegistrationImpl<E>(
                                                                                                                  this,
                                                                                                                  this.tableDataAccessor.getTableLock(),
                                                                                                                  this.exceptionHandler ) );
    this.stripeTransformerPluginManager = new StripeTransformerPluginManagerImpl<E>();
  }
  
  @SuppressWarnings("unchecked")
  public ArrayTable( E[][] elementMatrix )
  {
    this( (Class<? extends E>) ArrayUtils.componentType( ArrayUtils.componentType( elementMatrix.getClass() ) ) );
    this.copy().from( elementMatrix );
  }
  
  @Override
  public Table<E> addColumnElements( E... elements )
  {
    final int columnIndex = this.columnSize();
    return this.addColumnElements( columnIndex, elements );
  }
  
  @Override
  public Table<E> addColumnElements( int columnIndex, E... elements )
  {
    this.tableDataAccessor.addColumn( columnIndex, elements );
    return this;
  }
  
  @Override
  public Table<E> addRowElements( E... elements )
  {
    this.tableDataAccessor.addRow( elements );
    return this;
  }
  
  @Override
  public Table<E> addRowElements( int rowIndex, E... elements )
  {
    this.tableDataAccessor.addRow( rowIndex, elements );
    return this;
  }
  
  @Override
  public TableAdapterManager<E> as()
  {
    return this.tableAdapterManager;
  }
  
  @Override
  public Cell<E> cell( int rowIndex, int columnIndex )
  {
    return rowIndex >= 0 && columnIndex >= 0 ? this.tableDataAccessor.register( new CellImpl<E>( rowIndex, columnIndex, this ) )
                                            : null;
  }
  
  @Override
  public Iterable<Cell<E>> cells()
  {
    return new Iterable<Cell<E>>()
    {
      @Override
      public Iterator<Cell<E>> iterator()
      {
        final Iterator<Row<E>> rowIterator = rows().iterator();
        return IteratorUtils.factoryBasedIterator( new Factory<Iterator<Cell<E>>>()
        {
          @Override
          public Iterator<Cell<E>> newInstance()
          {
            return rowIterator.hasNext() ? rowIterator.next().cells().iterator() : null;
          }
        } );
      }
    };
  }
  
  @Override
  public Table<E> clear()
  {
    this.tableDataAccessor.clear();
    return this;
  }
  
  @Override
  public Table<E> clone()
  {
    Table<E> table = new ArrayTable<E>( this.to().array() );
    table.setTableName( this.getTableName() );
    table.setRowTitles( this.getRowTitleList() );
    table.setColumnTitles( this.getColumnTitleList() );
    return table;
  }
  
  @Override
  public Column<E> column( int columnIndex )
  {
    return columnIndex >= 0 ? this.tableDataAccessor.register( new ColumnImpl<E>( columnIndex, this, false ) ) : null;
  }
  
  @Override
  public Column<E> column( String columnTitle )
  {
    final int columnIndex = this.tableDataAccessor.getColumnIndex( columnTitle );
    return this.column( columnIndex );
  }
  
  @Override
  public Iterable<Column<E>> columns( Pattern columnTitlePattern )
  {
    final BitSet columnIndexFilter = this.tableDataAccessor.getColumnIndexFilter( columnTitlePattern );
    return IterableUtils.filtered( this.columns(), columnIndexFilter );
  }
  
  @Override
  public Iterable<Column<E>> columns( Set<String> columnTitleSet )
  {
    final BitSet columnIndexFilter = this.tableDataAccessor.getColumnIndexFilter( columnTitleSet );
    return IterableUtils.filtered( this.columns(), columnIndexFilter );
  }
  
  @Override
  public Iterable<Column<E>> columns( String... columnTitles )
  {
    return this.columns( SetUtils.valueOf( columnTitles ) );
  }
  
  @Override
  public int columnSize()
  {
    return this.tableDataAccessor.columnSize();
  }
  
  @Override
  public Class<E> elementType()
  {
    return this.elementType;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public boolean equalsInContent( ImmutableTable<E> table )
  {
    boolean retval = table != null;
    
    if ( table != null )
    {
      int rowSize = table.rowSize();
      int columnSize = table.columnSize();
      
      retval &= this.rowSize() == rowSize;
      retval &= this.columnSize() == columnSize;
      
      if ( retval )
      {
        Iterator<Row<E>> iteratorRowThis = this.rows().iterator();
        Iterator<ImmutableRow<E>> iteratorRowOther = ( (Iterable<ImmutableRow<E>>) table.rows() ).iterator();
        
        while ( iteratorRowThis.hasNext() && iteratorRowOther.hasNext() )
        {
          ImmutableRow<E> rowThis = iteratorRowThis.next();
          ImmutableRow<E> rowOther = iteratorRowOther.next();
          
          if ( !rowThis.equalsInContent( rowOther ) )
          {
            retval = false;
            break;
          }
        }
        
        retval &= !iteratorRowThis.hasNext() && !iteratorRowOther.hasNext();
      }
    }
    
    return retval;
  }
  
  @Override
  public boolean equalsInContentAndMetaData( ImmutableTable<E> table )
  {
    final boolean equalsInContent = this.equalsInContent( table );
    
    final boolean equalsInMetaData = table != null && StringUtils.equals( this.getTableName(), table.getTableName() )
                                     && ObjectUtils.equals( this.getRowTitleList(), table.getRowTitleList() )
                                     && ObjectUtils.equals( this.getColumnTitleList(), table.getColumnTitleList() );
    
    return equalsInContent && equalsInMetaData;
  }
  
  @Override
  public ImmutableTable<E> executeWithReadLock( TableExecution<ImmutableTable<E>, E> tableExecution )
  {
    OperationUtils.executeWithLocks( tableExecution, this, this.tableDataAccessor.getTableLock().readLock() );
    return this;
  }
  
  @Override
  public Table<E> executeWithReadLock( final TableExecution<ImmutableTable<E>, E> tableExecution,
                                       final ImmutableTable<E>... furtherLockedTables )
  {
    final int furtherLockedTablesLength = furtherLockedTables.length;
    if ( furtherLockedTablesLength > 0 )
    {
      OperationUtils.executeWithLocks( new OperationIntrinsic()
      {
        @Override
        public void execute()
        {
          OperationUtils.executeWithLocks( tableExecution, ArrayTable.this, ArrayTable.this.tableDataAccessor.getTableLock()
                                                                                                             .readLock() );
          final ImmutableTable<E> furtherTable = furtherLockedTables[0];
          furtherTable.executeWithReadLock( tableExecution,
                                            Arrays.copyOfRange( furtherLockedTables, 1, furtherLockedTablesLength ) );
        }
      }, this.tableDataAccessor.getTableLock().readLock() );
    }
    else
    {
      OperationUtils.executeWithLocks( tableExecution, this, this.tableDataAccessor.getTableLock().readLock() );
    }
    return this;
  }
  
  @Override
  public Table<E> executeWithWriteLock( TableExecution<Table<E>, E> tableExecution )
  {
    OperationUtils.executeWithLocks( tableExecution, this, this.tableDataAccessor.getTableLock().writeLock() );
    return this;
  }
  
  @Override
  public E getElement( int rowIndex, int columnIndex )
  {
    return this.tableDataAccessor.getElement( rowIndex, columnIndex );
  }
  
  @Override
  public E getElement( String rowTitle, int columnIndex )
  {
    final int rowIndex = this.tableDataAccessor.getRowIndex( rowTitle );
    return this.tableDataAccessor.getElement( rowIndex, columnIndex );
  }
  
  @Override
  public E getElement( int rowIndex, String columnTitle )
  {
    final int columnIndex = this.tableDataAccessor.getColumnIndex( columnTitle );
    return this.tableDataAccessor.getElement( rowIndex, columnIndex );
  }
  
  @Override
  public E getElement( String rowTitle, String columnTitle )
  {
    final int rowIndex = this.tableDataAccessor.getRowIndex( rowTitle );
    final int columnIndex = this.tableDataAccessor.getColumnIndex( columnTitle );
    return this.tableDataAccessor.getElement( rowIndex, columnIndex );
  }
  
  @Override
  public String getColumnTitle( int columnIndex )
  {
    return this.tableDataAccessor.getColumnTitle( columnIndex );
  }
  
  @Override
  public List<String> getColumnTitleList()
  {
    return this.tableDataAccessor.getColumnTitleList();
  }
  
  @Override
  public String getRowTitle( int rowIndex )
  {
    return this.tableDataAccessor.getRowTitle( rowIndex );
  }
  
  @Override
  public List<String> getRowTitleList()
  {
    return this.tableDataAccessor.getRowTitleList();
  }
  
  @Override
  public String getTableName()
  {
    return this.tableDataAccessor.getTableName();
  }
  
  @Override
  public boolean hasColumnTitles()
  {
    return this.tableDataAccessor.hasColumnTitles();
  }
  
  @Override
  public boolean hasRowTitles()
  {
    return this.tableDataAccessor.hasRowTitles();
  }
  
  @Override
  public boolean hasTableName()
  {
    return this.tableDataAccessor.hasTableName();
  }
  
  @Override
  public TableIndexManager<E, Cell<E>> index()
  {
    return this.tableIndexManager;
  }
  
  @Override
  public TablePersistenceRegistration<E> persistence()
  {
    return this.tablePersistenceRegistration;
  }
  
  @Override
  public Table<E> removeColumn( int columnIndex )
  {
    this.tableDataAccessor.removeColumn( columnIndex );
    return this;
  }
  
  @Override
  public Table<E> removeRow( int rowIndex )
  {
    this.tableDataAccessor.removeRow( rowIndex );
    return this;
  }
  
  @Override
  public Row<E> row( int rowIndex )
  {
    return rowIndex >= 0 ? this.tableDataAccessor.register( new RowImpl<E>( rowIndex, this, false ) ) : null;
  }
  
  @Override
  public Row<E> row( String rowTitle )
  {
    final int rowIndex = this.tableDataAccessor.getRowIndex( rowTitle );
    return this.row( rowIndex );
  }
  
  @Override
  public int rowSize()
  {
    return this.tableDataAccessor.rowSize();
  }
  
  @Override
  public TableSelect<E> select()
  {
    return new TableSelectImpl<E>( this );
  }
  
  @Override
  public Table<E> setColumnTitle( int columnIndex, String columnTitle )
  {
    this.tableDataAccessor.setColumnTitle( columnIndex, columnTitle );
    return this;
  }
  
  @Override
  public Table<E> setColumnTitles( Iterable<String> columnTitleIterable )
  {
    this.tableDataAccessor.setColumnTitles( columnTitleIterable );
    return this;
  }
  
  @Override
  public Table<E> setElement( int rowIndex, int columnIndex, E element )
  {
    this.tableDataAccessor.set( element, rowIndex, columnIndex );
    return this;
  }
  
  @Override
  public Table<E> setElement( int rowIndex, String columnTitle, E element )
  {
    final int columnIndex = this.tableDataAccessor.getColumnIndex( columnTitle );
    this.setElement( rowIndex, columnIndex, element );
    return this;
  }
  
  @Override
  public Table<E> setElement( String rowTitle, int columnIndex, E element )
  {
    final int rowIndex = this.tableDataAccessor.getRowIndex( rowTitle );
    this.setElement( rowIndex, columnIndex, element );
    return this;
  }
  
  @Override
  public Table<E> setElement( String rowTitle, String columnTitle, E element )
  {
    final int columnIndex = this.tableDataAccessor.getColumnIndex( columnTitle );
    final int rowIndex = this.tableDataAccessor.getRowIndex( rowTitle );
    this.setElement( rowIndex, columnIndex, element );
    return this;
  }
  
  @Override
  public Table<E> setRowElements( int rowIndex, E... elements )
  {
    this.tableDataAccessor.setRow( rowIndex, elements );
    return this;
  }
  
  @Override
  public Table<E> setRowTitle( int rowIndex, String rowTitle )
  {
    this.tableDataAccessor.setRowTitle( rowIndex, rowTitle );
    return this;
  }
  
  @Override
  public Table<E> setRowTitles( Iterable<String> rowTitleIterable )
  {
    this.tableDataAccessor.setRowTitles( rowTitleIterable );
    return this;
  }
  
  @Override
  public Table<E> setTableName( String tableName )
  {
    this.tableDataAccessor.setTableName( tableName );
    return this;
  }
  
  @Override
  public TableSorter<E> sort()
  {
    return new TableSorterImpl<E>( this );
  }
  
  @Override
  public int getColumnIndex( String columnTitle )
  {
    return this.tableDataAccessor.getColumnIndex( columnTitle );
  }
  
  @Override
  public Table<E> register( StripeTransformerPlugin<E, ?> stripeTransformerPlugin )
  {
    this.stripeTransformerPluginManager.register( stripeTransformerPlugin );
    return this;
  }
  
  @Override
  public <T> T transformStripeInto( Class<T> type, ImmutableStripe<E> stripe )
  {
    T retval = null;
    if ( stripe != null )
    {
      final StripeTransformerPlugin<E, T> stripeTransformerPlugin = this.stripeTransformerPluginManager.resolveStripeTransformerPluginFor( type );
      if ( stripeTransformerPlugin != null )
      {
        try
        {
          retval = stripeTransformerPlugin.transform( stripe );
        }
        catch ( Exception e )
        {
          this.exceptionHandler.handleException( e );
        }
      }
    }
    return retval;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <T> T transformStripeInto( T instance, ImmutableStripe<E> stripe )
  {
    T retval = null;
    if ( instance != null && stripe != null )
    {
      final Class<T> type = (Class<T>) instance.getClass();
      final StripeTransformerPlugin<E, T> stripeTransformerPlugin = this.stripeTransformerPluginManager.resolveStripeTransformerPluginFor( type );
      if ( stripeTransformerPlugin != null )
      {
        try
        {
          retval = stripeTransformerPlugin.transform( stripe, instance );
        }
        catch ( Exception e )
        {
          this.exceptionHandler.handleException( e );
        }
      }
    }
    return retval;
  }
  
  @Override
  public TableDataSourceCopier<E> copy()
  {
    final Table<E> table = this;
    return new TableDataSourceCopier<E>()
    {
      private static final long serialVersionUID = 306474856413841605L;
      
      @Override
      public Table<E> from( E[][] elementMatrix )
      {
        if ( elementMatrix != null )
        {
          for ( E[] elements : elementMatrix )
          {
            table.addRowElements( elements );
          }
        }
        return table;
      }
      
      @Override
      public Table<E> from( TableDataSource<E> tableDataSource )
      {
        if ( tableDataSource != null )
        {
          final Iterable<E[]> rowElements = tableDataSource.rowElements();
          if ( rowElements != null )
          {
            for ( E[] elements : rowElements )
            {
              table.addRowElements( elements );
            }
          }
          
          final String[] columnTitles = tableDataSource.getColumnTitles();
          final String tableName = tableDataSource.getTableName();
          if ( tableName != null )
          {
            table.setTableName( tableName );
          }
          if ( columnTitles != null )
          {
            table.setColumnTitles( columnTitles );
          }
        }
        return table;
      }
    };
  }
  
  @Override
  public String[] getColumnTitles()
  {
    return ArrayUtils.valueOf( this.getColumnTitleList(), String.class );
  }
  
  @Override
  public TableEventHandlerRegistration<E, Table<E>> tableEventHandlerRegistration()
  {
    final Table<E> table = this;
    final TableDataAccessor<E> tableDataAccessor = this.tableDataAccessor;
    return new TableEventHandlerRegistration<E, Table<E>>()
    {
      private static final long serialVersionUID = -4733568643076274493L;
      
      @Override
      public Table<E> attach( TableEventHandler<E> tableEventHandler )
      {
        tableDataAccessor.register( tableEventHandler );
        return table;
      }
      
      @Override
      public Table<E> detach( TableEventHandler<E> tableEventHandler )
      {
        tableDataAccessor.unregister( tableEventHandler );
        return table;
      }
    };
  }
  
  @Override
  public Iterable<E[]> rowElements()
  {
    final ElementConverter<Row<E>, E[]> elementConverter = new ElementConverterSerializable<Row<E>, E[]>()
    {
      private static final long serialVersionUID = -4211554274134868391L;
      
      @Override
      public E[] convert( Row<E> row )
      {
        return row.to().array();
      }
    };
    return IterableUtils.adapter( this.rows(), elementConverter );
  }
  
  @Override
  public Row<E> row( int rowIndex, boolean detached )
  {
    return rowIndex >= 0 ? new RowImpl<E>( rowIndex, this, detached ) : null;
  }
}

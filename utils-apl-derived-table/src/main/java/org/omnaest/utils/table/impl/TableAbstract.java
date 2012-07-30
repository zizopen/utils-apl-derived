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
import java.util.Map;

import org.omnaest.utils.events.exception.ExceptionHandlerSerializable;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerDelegate;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerIgnoring;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.element.factory.Factory;
import org.omnaest.utils.structure.element.factory.FactorySerializable;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.table.Column;
import org.omnaest.utils.table.Columns;
import org.omnaest.utils.table.ImmutableColumn;
import org.omnaest.utils.table.ImmutableRow;
import org.omnaest.utils.table.Row;
import org.omnaest.utils.table.Rows;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.TableSerializer;
import org.omnaest.utils.table.TableTransformer;
import org.omnaest.utils.table.impl.serializer.TableSerializerImpl;
import org.omnaest.utils.table.impl.transformer.TableTransformerImpl;

/**
 * @see Table
 * @author Omnaest
 * @param <E>
 */
abstract class TableAbstract<E> implements Table<E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long          serialVersionUID = 6651647383929942697L;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  protected ExceptionHandlerDelegate exceptionHandler = new ExceptionHandlerDelegate( new ExceptionHandlerIgnoring() );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  public static final class ColumnIterator<E, C extends ImmutableColumn<E>> implements Iterator<C>
  {
    private int            index = -1;
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private final int      indexMax;
    
    /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
    private final Table<E> table;
    
    /* *************************************************** Methods **************************************************** */
    
    public ColumnIterator( Table<E> table )
    {
      this.table = table;
      this.indexMax = table.columnSize() - 1;
    }
    
    @Override
    public boolean hasNext()
    {
      return this.index + 1 <= this.indexMax;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public C next()
    {
      return (C) this.table.column( ++this.index );
    }
    
    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  
  static final class RowIterator<E, R extends ImmutableRow<E>> implements Iterator<R>
  {
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private int            index = -1;
    private final int      indexMax;
    
    /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
    private final Table<E> table;
    
    /* *************************************************** Methods **************************************************** */
    
    /**
     * @see RowIterator
     * @param table
     * @param isDetached
     */
    public RowIterator( Table<E> table, boolean isDetached )
    {
      this.table = table;
      this.indexMax = table.rowSize() - 1;
    }
    
    @Override
    public boolean hasNext()
    {
      return this.index + 1 <= this.indexMax;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public R next()
    {
      return (R) this.table.row( ++this.index );
    }
    
    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  
  /* *************************************************** Methods **************************************************** */
  
  TableAbstract()
  {
    super();
  }
  
  /**
   * @see TableAbstract
   * @param elementMatrix
   */
  TableAbstract( E[][] elementMatrix )
  {
    super();
    
    this.copy().from( elementMatrix );
  }
  
  @Override
  public Table<E> addColumnTitle( String columnTitle )
  {
    final int columnIndex = this.getColumnTitleList().size();
    this.setColumnTitle( columnIndex, columnTitle );
    return this;
  }
  
  @Override
  public abstract Table<E> clone();
  
  @Override
  public Columns<E, Column<E>> columns()
  {
    final Table<E> table = this;
    return new ColumnsImpl<E>( IterableUtils.valueOf( new Factory<Iterator<Column<E>>>()
    {
      @Override
      public Iterator<Column<E>> newInstance()
      {
        return new ColumnIterator<E, Column<E>>( table );
      }
    } ) );
  }
  
  @Override
  public Iterator<ImmutableRow<E>> iterator()
  {
    final boolean detached = false;
    return new RowIterator<E, ImmutableRow<E>>( this, detached );
  }
  
  @Override
  public Rows<E, Row<E>> rows( final boolean detached )
  {
    final Table<E> table = this;
    return new RowsImpl<E>( IterableUtils.valueOf( new FactorySerializable<Iterator<Row<E>>>()
    {
      private static final long serialVersionUID = -8151494883227852607L;
      
      @Override
      public Iterator<Row<E>> newInstance()
      {
        return new RowIterator<E, Row<E>>( table, detached );
      }
    } ) );
  }
  
  @Override
  public Rows<E, Row<E>> rows()
  {
    final Table<E> table = this;
    return new RowsImpl<E>( IterableUtils.valueOf( new FactorySerializable<Iterator<Row<E>>>()
    {
      private static final long serialVersionUID = -8151494883227852607L;
      
      @Override
      public Iterator<Row<E>> newInstance()
      {
        final boolean detached = false;
        return new RowIterator<E, Row<E>>( table, detached );
      }
    } ) );
  }
  
  @Override
  public TableSerializer<E> serializer()
  {
    return new TableSerializerImpl<E>( this, this.exceptionHandler );
  }
  
  @Override
  public Table<E> setExceptionHandler( ExceptionHandlerSerializable exceptionHandler )
  {
    this.exceptionHandler.setExceptionHandler( ObjectUtils.defaultIfNull( exceptionHandler, new ExceptionHandlerIgnoring() ) );
    return this;
  }
  
  @Override
  public TableTransformer<E> to()
  {
    return new TableTransformerImpl<E>( this );
  }
  
  @Override
  public String toString()
  {
    return this.to().string();
  }
  
  @Override
  public Row<E> newRow()
  {
    final int rowIndex = this.rowSize();
    return this.row( rowIndex );
  }
  
  @Override
  public Row<E> lastRow()
  {
    return this.row( this.rowSize() - 1 );
  }
  
  @Override
  public Rows<E, Row<E>> rows( int rowIndexFrom, int rowIndexTo )
  {
    final BitSet filter = new BitSet();
    filter.set( rowIndexFrom, rowIndexTo );
    return this.rows( filter );
  }
  
  @Override
  public Table<E> setColumnTitles( String... columnTitles )
  {
    this.setColumnTitles( Arrays.asList( columnTitles ) );
    return this;
  }
  
  @Override
  public Rows<E, Row<E>> rows( int rowIndexFrom, int rowIndexTo, boolean detached )
  {
    final BitSet filter = new BitSet();
    filter.set( rowIndexFrom, rowIndexTo );
    return this.rows( filter, detached );
  }
  
  @Override
  public Rows<E, Row<E>> rows( BitSet filter, boolean detached )
  {
    return this.rows( detached ).filtered( filter );
  }
  
  @Override
  public Rows<E, Row<E>> rows( BitSet filter )
  {
    return this.rows().filtered( filter );
  }
  
  @Override
  public Table<E> setRowTitles( String... rowTitles )
  {
    return this.setRowTitles( Arrays.asList( rowTitles ) );
  }
  
  @Override
  public String[] getRowTitles()
  {
    return this.getRowTitleList().toArray( new String[0] );
  }
  
  @Override
  public Table<E> addRowElements( Map<String, E> columnToElementMap )
  {
    final boolean createColumnTitleIfDontExists = true;
    return this.addRowElements( columnToElementMap, createColumnTitleIfDontExists );
  }
  
}

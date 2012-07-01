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

import java.util.Iterator;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerIgnoring;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.element.factory.Factory;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.table2.Column;
import org.omnaest.utils.table2.ImmutableColumn;
import org.omnaest.utils.table2.ImmutableRow;
import org.omnaest.utils.table2.Row;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TableSerializer;
import org.omnaest.utils.table2.TableTransformer;
import org.omnaest.utils.table2.impl.serializer.TableSerializerImpl;
import org.omnaest.utils.table2.impl.transformer.TableTransformerImpl;

/**
 * @see Table
 * @author Omnaest
 * @param <E>
 */
abstract class TableAbstract<E> implements Table<E>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  protected ExceptionHandler exceptionHandler = new ExceptionHandlerIgnoring();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  public static final class RowIterator<E, R extends ImmutableRow<E>> implements Iterator<R>
  {
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private final int      indexMax;
    private int            index = -1;
    
    /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
    private final Table<E> table;
    
    /* *************************************************** Methods **************************************************** */
    
    public RowIterator( Table<E> table )
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
      return (R) this.table.getRow( ++this.index );
    }
    
    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  
  public static final class ColumnIterator<E, C extends ImmutableColumn<E>> implements Iterator<C>
  {
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private final int      indexMax;
    private int            index = -1;
    
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
      return (C) this.table.getColumn( ++this.index );
    }
    
    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  
  /* *************************************************** Methods **************************************************** */
  public TableAbstract()
  {
    super();
  }
  
  public TableAbstract( E[][] elementMatrix )
  {
    super();
    
    this.copyFrom( elementMatrix );
  }
  
  @Override
  public TableTransformer<E> to()
  {
    return new TableTransformerImpl<E>( this );
  }
  
  @Override
  public Iterator<ImmutableRow<E>> iterator()
  {
    return new RowIterator<E, ImmutableRow<E>>( this );
  }
  
  @Override
  public Iterable<Row<E>> rows()
  {
    final Table<E> table = this;
    return IterableUtils.valueOf( new Factory<Iterator<Row<E>>>()
    {
      @Override
      public Iterator<Row<E>> newInstance()
      {
        return new RowIterator<E, Row<E>>( table );
      }
    } );
    
  }
  
  @Override
  public Iterable<Column<E>> columns()
  {
    final Table<E> table = this;
    return IterableUtils.valueOf( new Factory<Iterator<Column<E>>>()
    {
      @Override
      public Iterator<Column<E>> newInstance()
      {
        return new ColumnIterator<E, Column<E>>( table );
      }
    } );
  }
  
  @Override
  public TableSerializer<E> serializer()
  {
    return new TableSerializerImpl<E>( this, this.exceptionHandler );
  }
  
  @Override
  public String toString()
  {
    return this.to().string();
  }
  
  @Override
  public Table<E> setExceptionHandler( ExceptionHandler exceptionHandler )
  {
    this.exceptionHandler = ObjectUtils.defaultIfNull( exceptionHandler, new ExceptionHandlerIgnoring() );
    return this;
  }
  
}

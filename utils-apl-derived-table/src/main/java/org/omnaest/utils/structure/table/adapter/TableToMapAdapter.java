/*******************************************************************************
 * Copyright 2011 Danny Kunz
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
package org.omnaest.utils.structure.table.adapter;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverter;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterIdentitiyCast;
import org.omnaest.utils.structure.map.MapAbstract;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.concrete.predicates.internal.filter.ColumnValueEquals;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Predicate;

/**
 * Adapter from two {@link Table} {@link Column}s into a {@link Map}.<br>
 * <br>
 * Be aware of the fact, that this implementation is very slow, since it does a {@link Table#select()} for each
 * {@link #get(Object)} call.
 * 
 * @author Omnaest
 * @param <E>
 * @param <K>
 * @param <V>
 */
public class TableToMapAdapter<E, K, V> extends MapAbstract<K, V> implements TableAdapter<Map<K, V>, E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long            serialVersionUID = 1238618778823037302L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private Table<E>                     table;
  private final Column<E>              columnForKeys;
  private final Column<E>              columnForValues;
  private final ElementConverter<E, V> elementConverterForValues;
  private final ElementConverter<E, K> elementConverterForKeys;
  private ExceptionHandler             exceptionHandler = null;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see TableToMapAdapter
   * @param columnForKeys
   * @param columnForValues
   * @param elementConverterForKeys
   *          {@link ElementConverter}
   * @param elementConverterForValues
   *          {@link ElementConverter}
   */
  public TableToMapAdapter( Column<E> columnForKeys, Column<E> columnForValues, ElementConverter<E, K> elementConverterForKeys,
                            ElementConverter<E, V> elementConverterForValues )
  {
    super();
    this.columnForKeys = columnForKeys;
    this.columnForValues = columnForValues;
    this.elementConverterForKeys = elementConverterForKeys;
    this.elementConverterForValues = elementConverterForValues;
    
    Assert.isNotNull( columnForKeys, "columnForKeys must not be null" );
    Assert.isNotNull( columnForValues, "columnForValues must not be null" );
    Assert.isNotNull( elementConverterForKeys, "elementConverterForKeys must not be null" );
    Assert.isNotNull( elementConverterForValues, "elementConverterForValues must not be null" );
  }
  
  /**
   * Similar to {@link #TableToMapAdapter(Column, Column, ElementConverter, ElementConverter)} using
   * {@link ElementConverterIdentitiyCast} for both key and value instances
   * 
   * @see TableToMapAdapter
   * @param columnForKeys
   * @param columnForValues
   */
  public TableToMapAdapter( Column<E> columnForKeys, Column<E> columnForValues )
  {
    this( columnForKeys, columnForValues, new ElementConverterIdentitiyCast<E, K>(), new ElementConverterIdentitiyCast<E, V>() );
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public V get( Object key )
  {
    //
    V retval = null;
    
    try
    {
      //
      final E element = (E) key;
      final Predicate<E> predicate = new ColumnValueEquals<E>( this.columnForKeys, element );
      Table<E> resultTable = this.table.select().columns( this.columnForValues ).where( predicate ).asTable();
      
      //
      Row<E> row = resultTable.getRow( 0 );
      E resultElement = row.getCell( this.columnForValues ).getElement();
      retval = this.elementConverterForValues.convert( resultElement );
    }
    catch ( Exception e )
    {
      if ( this.exceptionHandler != null )
      {
        this.exceptionHandler.handleException( e );
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Not supported
   * 
   * @throws UnsupportedOperationException
   */
  @Override
  public V put( K key, V value )
  {
    throw new UnsupportedOperationException();
  }
  
  /**
   * Not supported
   * 
   * @throws UnsupportedOperationException
   */
  @Override
  public V remove( Object key )
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Set<K> keySet()
  {
    //
    final Set<E> set = SetUtils.adapter( this.columnForKeys.getCellElementList() );
    final ElementBidirectionalConverter<E, K> elementBidirectionalConverter = new ElementBidirectionalConverter<E, K>()
    {
      @Override
      public K convert( E element )
      {
        return TableToMapAdapter.this.elementConverterForKeys.convert( element );
      }
      
      @Override
      public E convertBackwards( K element )
      {
        throw new UnsupportedOperationException();
      }
    };
    return Collections.unmodifiableSet( SetUtils.adapter( set, elementBidirectionalConverter ) );
  }
  
  @Override
  public Collection<V> values()
  {
    //
    final Set<E> set = SetUtils.adapter( this.columnForValues.getCellElementList() );
    final ElementBidirectionalConverter<E, V> elementBidirectionalConverter = new ElementBidirectionalConverter<E, V>()
    {
      @Override
      public V convert( E element )
      {
        return TableToMapAdapter.this.elementConverterForValues.convert( element );
      }
      
      @Override
      public E convertBackwards( V element )
      {
        throw new UnsupportedOperationException();
      }
    };
    return Collections.unmodifiableSet( SetUtils.adapter( set, elementBidirectionalConverter ) );
  }
  
  @Override
  public Map<K, V> initializeAdapter( Table<E> table )
  {
    this.table = table;
    return this;
  }
  
  /**
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   * @return this
   */
  public TableToMapAdapter<E, K, V> setExceptionHandler( ExceptionHandler exceptionHandler )
  {
    this.exceptionHandler = exceptionHandler;
    return this;
  }
  
}

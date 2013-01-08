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

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.collections.ComparatorUtils;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.KeyExtractor;
import org.omnaest.utils.structure.element.ValueExtractor;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverter;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverterSerializable;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterSerializable;
import org.omnaest.utils.structure.map.MapUtils;
import org.omnaest.utils.structure.map.decorator.MapDecoratorAbstract;
import org.omnaest.utils.table.Cell;
import org.omnaest.utils.table.Column;
import org.omnaest.utils.table.ImmutableColumn;
import org.omnaest.utils.table.Row;
import org.omnaest.utils.table.RowDataReader;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.TableIndex;
import org.omnaest.utils.table.TableIndexManager;
import org.omnaest.utils.table.impl.rowdata.RowDataBasedBeanFactory;
import org.omnaest.utils.tuple.Tuple2;

/**
 * @see TableIndexManager
 * @author Omnaest
 * @param <E>
 */
final class TableIndexManagerImpl<E> implements TableIndexManager<E, Cell<E>>
{
  /* ************************************************** Constants *************************************************** */
  private static final long                             serialVersionUID                          = 902507185120046828L;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final List<TableIndex<E, Cell<E>>>            tableIndexList                            = new CopyOnWriteArrayList<TableIndex<E, Cell<E>>>();
  private final KeyExtractorComparableToSortedMapMap<E> keyExtractorComparableTupleToSortedMapMap = new KeyExtractorComparableToSortedMapMap<E>();
  
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  private final TableDataAccessor<E>                    tableDataAccessor;
  private final Table<E>                                table;
  private final ExceptionHandler                        exceptionHandler;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  private static class KeyExtractorComparableToSortedMapMap<E> extends
      MapDecoratorAbstract<KeyExtractorAndComparator<E>, SortedMapReference<E>>
  {
    private static final long                                                  serialVersionUID = -6086675149149795294L;
    
    private transient Map<KeyExtractorAndComparator<E>, SortedMapReference<E>> map              = new WeakHashMap<KeyExtractorAndComparator<E>, SortedMapReference<E>>();
    
    public Object readResolve()
    {
      return new KeyExtractorComparableToSortedMapMap<E>();
    }
    
    public KeyExtractorComparableToSortedMapMap()
    {
      super();
    }
    
    @Override
    protected Map<KeyExtractorAndComparator<E>, SortedMapReference<E>> getMap()
    {
      return this.map;
    }
    
  }
  
  private static class KeyExtractorAndComparator<E> extends Tuple2<KeyExtractor<?, RowDataReader<E>>, Comparator<?>>
  {
    private static final long serialVersionUID = 3598403450230804232L;
    
    public KeyExtractorAndComparator( KeyExtractor<?, RowDataReader<E>> valueFirst, Comparator<?> valueSecond )
    {
      super( valueFirst, valueSecond );
    }
  }
  
  private static class SortedMapReference<E> extends WeakReference<SortedMap<?, Set<Row<E>>>> implements Serializable
  {
    private static final long serialVersionUID = -6520381411806304950L;
    
    public SortedMapReference( SortedMap<?, Set<Row<E>>> referent )
    {
      super( referent );
    }
  }
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see TableIndexManagerImpl
   * @param tableDataAccessor
   * @param table
   * @param exceptionHandler
   */
  @SuppressWarnings("javadoc")
  public TableIndexManagerImpl( TableDataAccessor<E> tableDataAccessor, Table<E> table, ExceptionHandler exceptionHandler )
  {
    super();
    this.tableDataAccessor = tableDataAccessor;
    this.table = table;
    this.exceptionHandler = exceptionHandler;
  }
  
  @Override
  public TableIndex<E, Cell<E>> of( int columnIndex )
  {
    if ( columnIndex >= 0 && columnIndex < this.table.columnSize() )
    {
      return getOrCreateTableIndexForColumn( columnIndex );
    }
    return null;
  }
  
  private TableIndex<E, Cell<E>> getOrCreateTableIndexForColumn( int columnIndex )
  {
    TableIndex<E, Cell<E>> retval = null;
    
    for ( TableIndex<E, Cell<E>> tableIndex : this.tableIndexList )
    {
      int index = tableIndex.index();
      if ( index == columnIndex )
      {
        retval = tableIndex;
        break;
      }
    }
    
    if ( retval == null )
    {
      Column<E> column = this.table.column( columnIndex );
      retval = this.tableDataAccessor.register( new TableIndexColumnBasedImpl<E>( column ) );
      this.tableIndexList.add( retval );
    }
    
    return retval;
  }
  
  @Override
  public TableIndex<E, Cell<E>> of( ImmutableColumn<E> columnImmutable )
  {
    TableIndex<E, Cell<E>> retval = null;
    if ( columnImmutable != null )
    {
      int columnIndex = columnImmutable.index();
      retval = this.of( columnIndex );
    }
    return retval;
  }
  
  @Override
  public <K> SortedMap<K, Set<Row<E>>> of( KeyExtractor<K, RowDataReader<E>> keyExtractor )
  {
    @SuppressWarnings("unchecked")
    final Comparator<K> comparator = ComparatorUtils.NATURAL_COMPARATOR;
    return this.of( keyExtractor, comparator );
  }
  
  @Override
  public <K> SortedMap<K, Set<Row<E>>> of( KeyExtractor<K, RowDataReader<E>> keyExtractor, Comparator<K> comparator )
  {
    return getOrCreateTableIndexForKeyExtractorAndComparator( keyExtractor, comparator );
  }
  
  @Override
  public <K, V> SortedMap<K, V> of( KeyExtractor<K, RowDataReader<E>> keyExtractor,
                                    final ValueExtractor<V, Set<E[]>> valueExtractor )
  {
    @SuppressWarnings("unchecked")
    final Comparator<K> comparator = ComparatorUtils.NATURAL_COMPARATOR;
    return this.of( keyExtractor, valueExtractor, comparator );
  }
  
  @Override
  public <K, V> SortedMap<K, V> of( KeyExtractor<K, RowDataReader<E>> keyExtractor,
                                    final ValueExtractor<V, Set<E[]>> valueExtractor,
                                    Comparator<K> comparator )
  {
    final ElementBidirectionalConverterSerializable<Set<Row<E>>, V> elementBidirectionalConverterValue = new ElementBidirectionalConverterSerializable<Set<Row<E>>, V>()
    {
      private static final long serialVersionUID = -6417051338827370915L;
      
      @Override
      public Set<Row<E>> convertBackwards( V value )
      {
        throw new UnsupportedOperationException();
      }
      
      @Override
      public V convert( Set<Row<E>> rowSet )
      {
        ElementConverter<Row<E>, E[]> elementConverter = new ElementConverterSerializable<Row<E>, E[]>()
        {
          private static final long serialVersionUID = -3703441560833466927L;
          
          @Override
          public E[] convert( Row<E> row )
          {
            return row.getElements();
          }
        };
        return valueExtractor.extractValue( SetUtils.convert( rowSet, elementConverter ) );
      }
    };
    
    return MapUtils.adapter( this.of( keyExtractor, comparator ), elementBidirectionalConverterValue );
  }
  
  @SuppressWarnings("unchecked")
  private <K> SortedMap<K, Set<Row<E>>> getOrCreateTableIndexForKeyExtractorAndComparator( KeyExtractor<K, RowDataReader<E>> keyExtractor,
                                                                                           Comparator<K> comparator )
  {
    SortedMap<K, Set<Row<E>>> retval = null;
    {
      final KeyExtractorAndComparator<E> keyExtractorComparable = new KeyExtractorAndComparator<E>( keyExtractor, comparator );
      final SortedMapReference<E> sortedMapReference = this.keyExtractorComparableTupleToSortedMapMap.get( keyExtractorComparable );
      if ( sortedMapReference != null )
      {
        retval = (SortedMap<K, Set<Row<E>>>) sortedMapReference.get();
      }
      
      if ( retval == null )
      {
        retval = this.tableDataAccessor.register( new TableIndexArbitraryImpl<K, E>( this.table, keyExtractor, comparator ) );
        this.keyExtractorComparableTupleToSortedMapMap.put( keyExtractorComparable, new SortedMapReference<E>( retval ) );
      }
    }
    return retval;
  }
  
  @Override
  public <K, B> SortedMap<K, Set<B>> of( KeyExtractor<K, B> keyExtractor, Class<B> beanType )
  {
    @SuppressWarnings("unchecked")
    final Comparator<K> comparator = ComparatorUtils.NATURAL_COMPARATOR;
    return this.of( keyExtractor, beanType, comparator );
  }
  
  @Override
  public <K, B> SortedMap<K, Set<B>> of( final KeyExtractor<K, B> keyExtractor, Class<B> beanType, Comparator<K> comparator )
  {
    final RowDataBasedBeanFactory<B> rowDataBasedBeanFactory = new RowDataBasedBeanFactory<B>( beanType, this.exceptionHandler );
    KeyExtractor<K, RowDataReader<E>> keyExtractorOther = new KeyExtractor<K, RowDataReader<E>>()
    {
      private static final long serialVersionUID = 3710840461645547536L;
      
      @Override
      public K extractKey( RowDataReader<E> rowDataReader )
      {
        K retval = null;
        if ( keyExtractor != null )
        {
          B bean = rowDataBasedBeanFactory.build( rowDataReader );
          retval = keyExtractor.extractKey( bean );
        }
        return retval;
      }
    };
    final SortedMap<K, Set<Row<E>>> sortedMap = this.of( keyExtractorOther, comparator );
    final ElementBidirectionalConverter<Set<Row<E>>, Set<B>> elementBidirectionalConverterValue = new ElementBidirectionalConverterSerializable<Set<Row<E>>, Set<B>>()
    {
      private static final long serialVersionUID = -5432567823705034055L;
      
      @Override
      public Set<Row<E>> convertBackwards( Set<B> element )
      {
        throw new UnsupportedOperationException();
      }
      
      @Override
      public Set<B> convert( Set<Row<E>> rowSet )
      {
        ElementConverter<Row<E>, B> elementConverter = new ElementConverterSerializable<Row<E>, B>()
        {
          private static final long serialVersionUID = 3560928844959299734L;
          
          @Override
          public B convert( Row<E> row )
          {
            return rowDataBasedBeanFactory.build( row );
          }
        };
        return SetUtils.convert( rowSet, elementConverter );
      }
    };
    return MapUtils.adapter( sortedMap, elementBidirectionalConverterValue );
  }
  
}

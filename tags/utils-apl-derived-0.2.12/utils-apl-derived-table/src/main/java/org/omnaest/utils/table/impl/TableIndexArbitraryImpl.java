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
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.lang3.ObjectUtils;
import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.KeyExtractor;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverterSerializable;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverterSetToUnmodifiableSet;
import org.omnaest.utils.structure.element.factory.concrete.LinkedHashSetFactory;
import org.omnaest.utils.structure.map.MapUtils;
import org.omnaest.utils.table.Row;
import org.omnaest.utils.table.RowDataReader;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.TableEventHandler;
import org.omnaest.utils.table.TableIndex;
import org.omnaest.utils.table.impl.rowdata.ElementsToRowDataReaderAdapter;
import org.omnaest.utils.table.impl.rowdata.RowToRowDataAccessorAdapter;

/**
 * @see TableIndex
 * @author Omnaest
 * @param <E>
 */
class TableIndexArbitraryImpl<K, E> implements SortedMap<K, Set<Row<E>>>, TableEventHandler<E>, Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long                       serialVersionUID = -8584025610784755115L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final SortedMap<K, Set<Row<E>>>         keyToRowSetMap;
  private final KeyExtractor<K, RowDataReader<E>> keyExtractor;
  private final Table<E>                          table;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see TableIndexArbitraryImpl
   * @param column
   */
  TableIndexArbitraryImpl( Table<E> table, KeyExtractor<K, RowDataReader<E>> keyExtractor, Comparator<K> comparator )
  {
    super();
    this.table = table;
    this.keyExtractor = keyExtractor;
    
    this.keyToRowSetMap = MapUtils.initializedSortedMap( new ConcurrentSkipListMap<K, Set<Row<E>>>(),
                                                         new LinkedHashSetFactory<Row<E>>() );
    
    this.rebuildIndexFully();
    
  }
  
  private void rebuildIndexFully()
  {
    this.keyToRowSetMap.clear();
    for ( Row<E> row : this.table.rows() )
    {
      final K key = extractKey( row );
      this.keyToRowSetMap.get( key ).add( row );
    }
  }
  
  private K extractKey( Row<E> row )
  {
    final RowDataReader<E> rowDataReader = new RowToRowDataAccessorAdapter<E>( row );
    final K key = this.keyExtractor.extractKey( rowDataReader );
    return key;
  }
  
  @Override
  public void clear()
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Comparator<? super K> comparator()
  {
    return this.keyToRowSetMap.comparator();
  }
  
  @Override
  public boolean containsKey( Object key )
  {
    return this.keyToRowSetMap.containsKey( key );
  }
  
  @Override
  public boolean containsValue( Object value )
  {
    return this.keyToRowSetMap.containsValue( value );
  }
  
  @Override
  public Set<java.util.Map.Entry<K, Set<Row<E>>>> entrySet()
  {
    return Collections.unmodifiableSet( SetUtils.adapter( this.keyToRowSetMap.entrySet(),
                                                          new ElementBidirectionalConverterSerializable<Entry<K, Set<Row<E>>>, Entry<K, Set<Row<E>>>>()
                                                          {
                                                            
                                                            private static final long serialVersionUID = 1170906776959603812L;
                                                            
                                                            @Override
                                                            public java.util.Map.Entry<K, Set<Row<E>>> convert( final Map.Entry<K, Set<Row<E>>> entry )
                                                            {
                                                              // 
                                                              return new Entry<K, Set<Row<E>>>()
                                                              {
                                                                @Override
                                                                public K getKey()
                                                                {
                                                                  return entry.getKey();
                                                                }
                                                                
                                                                @Override
                                                                public Set<Row<E>> getValue()
                                                                {
                                                                  return Collections.unmodifiableSet( entry.getValue() );
                                                                }
                                                                
                                                                @Override
                                                                public Set<Row<E>> setValue( Set<Row<E>> value )
                                                                {
                                                                  throw new UnsupportedOperationException();
                                                                }
                                                                
                                                                @Override
                                                                public String toString()
                                                                {
                                                                  return String.valueOf( this.getKey() ) + "="
                                                                         + String.valueOf( this.getValue() );
                                                                }
                                                                
                                                              };
                                                            }
                                                            
                                                            @Override
                                                            public java.util.Map.Entry<K, Set<Row<E>>> convertBackwards( java.util.Map.Entry<K, Set<Row<E>>> element )
                                                            {
                                                              throw new UnsupportedOperationException();
                                                            }
                                                          } ) );
  }
  
  @Override
  public boolean equals( Object o )
  {
    return this.keyToRowSetMap.equals( o );
  }
  
  @Override
  public K firstKey()
  {
    return this.keyToRowSetMap.firstKey();
  }
  
  @Override
  public Set<Row<E>> get( Object key )
  {
    return this.containsKey( key ) ? Collections.unmodifiableSet( this.keyToRowSetMap.get( key ) ) : null;
  }
  
  @Override
  public void handleAddedColumn( int columnIndex, E... elements )
  {
    this.rebuildIndexFully();
  }
  
  @Override
  public void handleAddedRow( int rowIndex, E... elements )
  {
    this.rebuildIndexFully();
  }
  
  @Override
  public void handleClearTable()
  {
    this.keyToRowSetMap.clear();
  }
  
  @Override
  public void handleRemovedColumn( int columnIndex, E[] previousElements, String columnTitle )
  {
    this.rebuildIndexFully();
  }
  
  @Override
  public void handleRemovedRow( int rowIndex, E[] previousElements, String rowTitle )
  {
    final K key = extractKey( previousElements );
    boolean containsKey = this.keyToRowSetMap.containsKey( key );
    if ( containsKey )
    {
      final Set<Row<E>> rowSet = this.keyToRowSetMap.get( key );
      for ( Row<E> row : rowSet )
      {
        if ( row.isDeleted() || row.index() == rowIndex )
        {
          rowSet.remove( row );
          break;
        }
      }
      if ( rowSet.isEmpty() )
      {
        this.keyToRowSetMap.remove( key );
      }
    }
  }
  
  private K extractKey( E[] previousElements )
  {
    final RowDataReader<E> rowDataReader = new ElementsToRowDataReaderAdapter<E>( previousElements, this.table );
    final K key = this.keyExtractor.extractKey( rowDataReader );
    return key;
  }
  
  @Override
  public void handleUpdatedCell( int rowIndex, int columnIndex, E element, E previousElement )
  {
    final Row<E> row = this.table.row( rowIndex );
    final E[] elements = row.getElements();
    final E[] elementsPrevious = Arrays.copyOf( elements, elements.length );
    elementsPrevious[columnIndex] = previousElement;
    
    final K keyPrevious = this.extractKey( elementsPrevious );
    final K keyNew = this.extractKey( elements );
    
    this.updateForChangedRow( rowIndex, row, keyPrevious, keyNew );
  }
  
  @Override
  public void handleUpdatedRow( int rowIndex, E[] elements, E[] previousElements, BitSet modifiedIndices )
  {
    final Row<E> row = this.table.row( rowIndex );
    final K keyPrevious = this.extractKey( previousElements );
    final K keyNew = this.extractKey( elements );
    
    this.updateForChangedRow( rowIndex, row, keyPrevious, keyNew );
  }
  
  private void updateForChangedRow( int rowIndex, final Row<E> row, final K keyPrevious, final K keyNew )
  {
    if ( !ObjectUtils.equals( keyNew, keyPrevious ) )
    {
      //remove old
      if ( keyPrevious != null )
      {
        final Set<Row<E>> rowSet = this.keyToRowSetMap.get( keyPrevious );
        final Set<Row<E>> rowRemovableSet = new HashSet<Row<E>>();
        for ( Row<E> iRow : rowSet )
        {
          if ( iRow.index() == rowIndex )
          {
            rowRemovableSet.add( iRow );
          }
        }
        rowSet.removeAll( rowRemovableSet );
        if ( rowSet.isEmpty() )
        {
          this.keyToRowSetMap.remove( keyPrevious );
        }
      }
      
      //add new
      if ( keyNew != null )
      {
        Set<Row<E>> rowSet = this.keyToRowSetMap.get( keyNew );
        rowSet.add( row );
      }
      
    }
  }
  
  @Override
  public int hashCode()
  {
    return this.keyToRowSetMap.hashCode();
  }
  
  @Override
  public SortedMap<K, Set<Row<E>>> headMap( K toKey )
  {
    return Collections.unmodifiableSortedMap( MapUtils.adapter( this.keyToRowSetMap.headMap( toKey ),
                                                                new ElementBidirectionalConverterSetToUnmodifiableSet<Row<E>>() ) );
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.keyToRowSetMap.isEmpty();
  }
  
  @Override
  public Set<K> keySet()
  {
    return Collections.unmodifiableSet( this.keyToRowSetMap.keySet() );
  }
  
  @Override
  public K lastKey()
  {
    return this.keyToRowSetMap.lastKey();
  }
  
  @Override
  public Set<Row<E>> put( K key, Set<Row<E>> value )
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public void putAll( Map<? extends K, ? extends Set<Row<E>>> m )
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Set<Row<E>> remove( Object key )
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public int size()
  {
    return this.keyToRowSetMap.size();
  }
  
  @Override
  public SortedMap<K, Set<Row<E>>> subMap( K fromKey, K toKey )
  {
    return Collections.unmodifiableSortedMap( MapUtils.adapter( this.keyToRowSetMap.subMap( fromKey, toKey ),
                                                                new ElementBidirectionalConverterSetToUnmodifiableSet<Row<E>>() ) );
  }
  
  @Override
  public SortedMap<K, Set<Row<E>>> tailMap( K fromKey )
  {
    return Collections.unmodifiableSortedMap( MapUtils.adapter( this.keyToRowSetMap.tailMap( fromKey ),
                                                                new ElementBidirectionalConverterSetToUnmodifiableSet<Row<E>>() ) );
  }
  
  @Override
  public Collection<Set<Row<E>>> values()
  {
    return Collections.unmodifiableCollection( CollectionUtils.adapter( this.keyToRowSetMap.values(),
                                                                        new ElementBidirectionalConverterSetToUnmodifiableSet<Row<E>>() ) );
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( MapUtils.toString( this ) );
    return builder.toString();
  }
  
  @Override
  public void handleModifiedColumnTitle( int columnIndex, String columnTitle, String columnTitlePrevious )
  {
  }
  
  @Override
  public void handleModifiedRowTitle( int rowIndex, String rowTitle, String rowTitlePrevious )
  {
  }
  
  @Override
  public void handleModifiedColumnTitles( String[] columnTitles, String[] columnTitlesPrevious )
  {
  }
  
  @Override
  public void handleModifiedRowTitles( String[] rowTitles, String[] rowTitlesPrevious )
  {
  }
  
  @Override
  public void handleModifiedTableName( String tableName, String tableNamePrevious )
  {
  }
  
}

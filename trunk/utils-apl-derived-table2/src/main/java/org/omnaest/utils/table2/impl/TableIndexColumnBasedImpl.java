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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverterSerializable;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverterSetToUnmodifiableSet;
import org.omnaest.utils.structure.element.factory.concrete.LinkedHashSetFactory;
import org.omnaest.utils.structure.map.MapUtils;
import org.omnaest.utils.table2.Cell;
import org.omnaest.utils.table2.Column;
import org.omnaest.utils.table2.ImmutableCell.Position;
import org.omnaest.utils.table2.TableIndex;

/**
 * @see TableIndex
 * @author Omnaest
 * @param <E>
 */
class TableIndexColumnBasedImpl<E> implements TableIndex<E, Cell<E>>, SortedMap<E, Set<Cell<E>>>, TableEventHandler<E>, Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long                serialVersionUID = 3723253328291423721L;
  private final Column<E>                  column;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final SortedMap<E, Set<Cell<E>>> elementToCellSetMap;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see TableIndexColumnBasedImpl
   * @param column
   */
  TableIndexColumnBasedImpl( Column<E> column )
  {
    super();
    this.column = column;
    this.elementToCellSetMap = MapUtils.initializedSortedMap( new ConcurrentSkipListMap<E, Set<Cell<E>>>(),
                                                              new LinkedHashSetFactory<Cell<E>>() );
    
    for ( Cell<E> cell : column.cells() )
    {
      E element = cell.getElement();
      this.elementToCellSetMap.get( element ).add( cell );
    }
  }
  
  @Override
  public void clear()
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Comparator<? super E> comparator()
  {
    return this.elementToCellSetMap.comparator();
  }
  
  @Override
  public boolean containsKey( Object key )
  {
    return this.elementToCellSetMap.containsKey( key );
  }
  
  @Override
  public boolean containsValue( Object value )
  {
    return this.elementToCellSetMap.containsValue( value );
  }
  
  @Override
  public Set<java.util.Map.Entry<E, Set<Cell<E>>>> entrySet()
  {
    return Collections.unmodifiableSet( SetUtils.adapter( this.elementToCellSetMap.entrySet(),
                                                          new ElementBidirectionalConverterSerializable<Entry<E, Set<Cell<E>>>, Entry<E, Set<Cell<E>>>>()
                                                          {
                                                            
                                                            private static final long serialVersionUID = 1170906776959603812L;
                                                            
                                                            @Override
                                                            public java.util.Map.Entry<E, Set<Cell<E>>> convert( final java.util.Map.Entry<E, Set<Cell<E>>> entry )
                                                            {
                                                              // 
                                                              return new Entry<E, Set<Cell<E>>>()
                                                              {
                                                                @Override
                                                                public E getKey()
                                                                {
                                                                  return entry.getKey();
                                                                }
                                                                
                                                                @Override
                                                                public Set<Cell<E>> getValue()
                                                                {
                                                                  return Collections.unmodifiableSet( entry.getValue() );
                                                                }
                                                                
                                                                @Override
                                                                public Set<Cell<E>> setValue( Set<Cell<E>> value )
                                                                {
                                                                  throw new UnsupportedOperationException();
                                                                }
                                                              };
                                                            }
                                                            
                                                            @Override
                                                            public java.util.Map.Entry<E, Set<Cell<E>>> convertBackwards( java.util.Map.Entry<E, Set<Cell<E>>> element )
                                                            {
                                                              throw new UnsupportedOperationException();
                                                            }
                                                          } ) );
  }
  
  @Override
  public boolean equals( Object o )
  {
    return this.elementToCellSetMap.equals( o );
  }
  
  @Override
  public E firstKey()
  {
    return this.elementToCellSetMap.firstKey();
  }
  
  @Override
  public Set<Cell<E>> get( Object key )
  {
    return this.containsKey( key ) ? Collections.unmodifiableSet( this.elementToCellSetMap.get( key ) ) : null;
  }
  
  @Override
  public void handleAddedColumn( int columnIndex, E... elements )
  {
  }
  
  @Override
  public void handleAddedRow( int rowIndex, E... elements )
  {
    Cell<E> cell = this.column.cell( rowIndex );
    E element = cell.getElement();
    this.elementToCellSetMap.get( element ).add( cell );
  }
  
  @Override
  public void handleClearTable()
  {
    this.elementToCellSetMap.clear();
  }
  
  @Override
  public void handleRemovedColumn( int columnIndex, E[] previousElements )
  {
    if ( this.column.index() == columnIndex )
    {
      this.handleClearTable();
    }
  }
  
  @Override
  public void handleRemovedRow( int rowIndex, E[] previousElements )
  {
    final int columnIndex = this.column.index();
    final E element = previousElements.length > columnIndex ? previousElements[columnIndex] : null;
    final Set<Cell<E>> cellSet = this.elementToCellSetMap.get( element );
    if ( cellSet != null )
    {
      for ( Cell<E> cell : cellSet )
      {
        if ( cell.rowIndex() == rowIndex )
        {
          cellSet.remove( cell );
          break;
        }
      }
      if ( cellSet.isEmpty() )
      {
        this.elementToCellSetMap.remove( element );
      }
    }
  }
  
  @Override
  public void handleUpdatedCell( int rowIndex, int columnIndex, E element, E previousElement )
  {
    if ( columnIndex == this.column.index() )
    {
      Cell<E> cell = this.column.cell( rowIndex );
      {
        final Position position = cell.getPosition();
        final Set<Cell<E>> cellSet = this.elementToCellSetMap.get( previousElement );
        final Set<Cell<E>> cellRemovableSet = new HashSet<Cell<E>>();
        for ( Cell<E> iCell : cellSet )
        {
          if ( iCell.isDeleted() || iCell.isModified() || position.equals( iCell.getPosition() ) )
          {
            cellRemovableSet.add( iCell );
          }
        }
        cellSet.removeAll( cellRemovableSet );
        
        if ( cellSet.isEmpty() )
        {
          this.elementToCellSetMap.remove( previousElement );
        }
      }
      {
        Set<Cell<E>> cellSet = this.elementToCellSetMap.get( element );
        cellSet.add( cell );
      }
    }
  }
  
  @Override
  public void handleUpdatedRow( int rowIndex, E[] elements, E[] previousElements, BitSet modifiedIndices )
  {
    for ( int ii = 0; ii >= 0; modifiedIndices.nextSetBit( ii + 1 ) )
    {
      final E element = elements[ii];
      final E previousElement = previousElements[ii];
      
      final int columnIndex = ii;
      this.handleUpdatedCell( rowIndex, columnIndex, element, previousElement );
    }
  }
  
  @Override
  public int hashCode()
  {
    return this.elementToCellSetMap.hashCode();
  }
  
  @Override
  public SortedMap<E, Set<Cell<E>>> headMap( E toKey )
  {
    return Collections.unmodifiableSortedMap( MapUtils.adapter( this.elementToCellSetMap.headMap( toKey ),
                                                                new ElementBidirectionalConverterSetToUnmodifiableSet<Cell<E>>() ) );
  }
  
  @Override
  public int index()
  {
    return this.column.index();
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.elementToCellSetMap.isEmpty();
  }
  
  @Override
  public Set<E> keySet()
  {
    return Collections.unmodifiableSet( this.elementToCellSetMap.keySet() );
  }
  
  @Override
  public E lastKey()
  {
    return this.elementToCellSetMap.lastKey();
  }
  
  @Override
  public Set<Cell<E>> put( E key, Set<Cell<E>> value )
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public void putAll( Map<? extends E, ? extends Set<Cell<E>>> m )
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Set<Cell<E>> remove( Object key )
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public int size()
  {
    return this.elementToCellSetMap.size();
  }
  
  @Override
  public SortedMap<E, Set<Cell<E>>> subMap( E fromKey, E toKey )
  {
    return Collections.unmodifiableSortedMap( MapUtils.adapter( this.elementToCellSetMap.subMap( fromKey, toKey ),
                                                                new ElementBidirectionalConverterSetToUnmodifiableSet<Cell<E>>() ) );
  }
  
  @Override
  public SortedMap<E, Set<Cell<E>>> tailMap( E fromKey )
  {
    return Collections.unmodifiableSortedMap( MapUtils.adapter( this.elementToCellSetMap.tailMap( fromKey ),
                                                                new ElementBidirectionalConverterSetToUnmodifiableSet<Cell<E>>() ) );
  }
  
  @Override
  public Collection<Set<Cell<E>>> values()
  {
    return Collections.unmodifiableCollection( CollectionUtils.adapter( this.elementToCellSetMap.values(),
                                                                        new ElementBidirectionalConverterSetToUnmodifiableSet<Cell<E>>() ) );
  }
  
}

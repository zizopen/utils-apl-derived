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
package org.omnaest.utils.table2.impl.transformer;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TableTransformer;

/**
 * {@link TableTransformer} implementation
 * 
 * @author Omnaest
 * @param <E>
 */
public class TableTransformerImpl<E> implements TableTransformer<E>
{
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  private final Table<E> table;
  
  /* *************************************************** Methods **************************************************** */
  
  public TableTransformerImpl( Table<E> table )
  {
    super();
    this.table = table;
  }
  
  @Override
  public E[][] array()
  {
    return new TableToArrayConverter<E>().convert( this.table );
  }
  
  @Override
  public String string()
  {
    return this.table.serializer().marshal().asPlainText().toString();
  }
  
  @Override
  public Map<E, E[]> map()
  {
    final int columnIndex = 0;
    return this.map( columnIndex );
  }
  
  @Override
  public Map<E, E[]> map( int columnIndex )
  {
    final Map<E, E[]> map = new LinkedHashMap<E, E[]>();
    return this.map( map, columnIndex );
  }
  
  @Override
  public SortedMap<E, E[]> sortedMap()
  {
    final int columnIndex = 0;
    return this.sortedMap( columnIndex );
  }
  
  @Override
  public SortedMap<E, E[]> sortedMap( int columnIndex )
  {
    final SortedMap<E, E[]> map = new TreeMap<E, E[]>();
    return this.map( map, columnIndex );
  }
  
  @Override
  public SortedMap<E, E[]> sortedMap( Comparator<E> comparator )
  {
    final int columnIndex = 0;
    return this.sortedMap( comparator, columnIndex );
  }
  
  @Override
  public SortedMap<E, E[]> sortedMap( Comparator<E> comparator, int columnIndex )
  {
    final SortedMap<E, E[]> map = new TreeMap<E, E[]>( comparator );
    return this.map( map, columnIndex );
  }
  
  @Override
  public <M extends Map<E, E[]>> M map( M map )
  {
    final int columnIndex = 0;
    return this.map( map, columnIndex );
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <M extends Map<E, E[]>> M map( M map, int columnIndex )
  {
    if ( map == null )
    {
      map = (M) new LinkedHashMap<E, E[]>();
    }
    
    final E[][] elementMatrix = this.array();
    for ( E[] elements : elementMatrix )
    {
      final E key = elements[columnIndex];
      final E[] value = elements;
      map.put( key, value );
    }
    return map;
  }
  
  @Override
  public Map<E, E> map( int columnIndexKey, int columnIndexValue )
  {
    final Map<E, E> map = new LinkedHashMap<E, E>();
    return this.map( map, columnIndexKey, columnIndexValue );
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <M extends Map<E, E>> M map( M map, int columnIndexKey, int columnIndexValue )
  {
    if ( map == null )
    {
      map = (M) new LinkedHashMap<E, E>();
    }
    
    final E[][] elementMatrix = this.array();
    for ( E[] elements : elementMatrix )
    {
      final E key = elements[columnIndexKey];
      final E value = elements[columnIndexValue];
      map.put( key, value );
    }
    return map;
  }
  
  @Override
  public SortedMap<E, E> sortedMap( int columnIndexKey, int columnIndexValue )
  {
    final SortedMap<E, E> map = new TreeMap<E, E>();
    return this.map( map, columnIndexKey, columnIndexValue );
  }
  
  @Override
  public SortedMap<E, E> sortedMap( Comparator<E> comparator, int columnIndexKey, int columnIndexValue )
  {
    final SortedMap<E, E> map = new TreeMap<E, E>( comparator );
    return this.map( map, columnIndexKey, columnIndexValue );
  }
}

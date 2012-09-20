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
package org.omnaest.utils.table.impl.transformer;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterSerializable;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.table.Column;
import org.omnaest.utils.table.Row;
import org.omnaest.utils.table.Rows;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.TableDataSource;
import org.omnaest.utils.table.TableTransformer;
import org.omnaest.utils.table.impl.ArrayTable;

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
  
  @Override
  public Table<E> swapped()
  {
    final Table<E> table = this.table;
    final Table<E> retval = new ArrayTable<E>( this.table.elementType() );
    retval.copy().from( new TableDataSource<E>()
    {
      private static final long serialVersionUID = 5929745410356641061L;
      
      @Override
      public String getTableName()
      {
        return table.getTableName();
      }
      
      @Override
      public String[] getColumnTitles()
      {
        return table.getRowTitles();
      }
      
      @Override
      public Iterable<E[]> rowElements()
      {
        final Iterable<Column<E>> columns = table.columns();
        final ElementConverter<Column<E>, E[]> elementConverter = new ElementConverterSerializable<Column<E>, E[]>()
        {
          private static final long serialVersionUID = -3322314134295745486L;
          
          @Override
          public E[] convert( Column<E> column )
          {
            return column.to().array();
          }
        };
        return IterableUtils.convert( columns, elementConverter );
      }
      
      @Override
      public String[] getRowTitles()
      {
        return table.getColumnTitles();
      }
    } );
    return retval;
  }
  
  @Override
  public <N> Table<N> converted( final Class<N> elementType, final ElementConverter<E, N> elementConverter )
  {
    final Table<E> table = this.table;
    final Table<N> retval = new ArrayTable<N>( elementType );
    retval.copy().from( new TableDataSource<N>()
    {
      private static final long serialVersionUID = 5929745410356641061L;
      
      @Override
      public String getTableName()
      {
        return table.getTableName();
      }
      
      @Override
      public String[] getColumnTitles()
      {
        return table.getColumnTitles();
      }
      
      @Override
      public Iterable<N[]> rowElements()
      {
        final boolean detached = true;
        Rows<E, Row<E>> rows = table.rows( detached );
        return IterableUtils.convert( rows, new ElementConverterSerializable<Row<E>, N[]>()
        {
          private static final long serialVersionUID = -5480913656973372565L;
          
          @Override
          public N[] convert( Row<E> row )
          {
            return ArrayUtils.convertArray( row.to().array(), elementType, elementConverter );
          }
        } );
      }
      
      @Override
      public String[] getRowTitles()
      {
        return table.getRowTitles();
      }
    } );
    return retval;
  }
  
  @Override
  public Table<E> subTable( final int rowIndexFrom, final int rowIndexTo, final int columnIndexFrom, final int columnIndexTo )
  {
    final Table<E> table = this.table;
    final Table<E> retval = new ArrayTable<E>( table.elementType() );
    retval.copy().from( new TableDataSource<E>()
    {
      private static final long serialVersionUID = -8443061443474858835L;
      
      @Override
      public String getTableName()
      {
        return table.getTableName();
      }
      
      @Override
      public String[] getColumnTitles()
      {
        return Arrays.copyOfRange( table.getColumnTitles(), columnIndexFrom, columnIndexTo );
      }
      
      @Override
      public String[] getRowTitles()
      {
        return Arrays.copyOfRange( table.getRowTitles(), rowIndexFrom, rowIndexTo );
      }
      
      @Override
      public Iterable<E[]> rowElements()
      {
        final boolean detached = true;
        final BitSet filter = new BitSet();
        filter.set( rowIndexFrom, rowIndexTo );
        Rows<E, Row<E>> rows = table.rows( filter, detached );
        return IterableUtils.convert( rows, new ElementConverterSerializable<Row<E>, E[]>()
        {
          private static final long serialVersionUID = -5480913656973372565L;
          
          @Override
          public E[] convert( Row<E> row )
          {
            return Arrays.copyOfRange( row.to().array(), columnIndexFrom, columnIndexTo );
          }
        } );
      }
      
    } );
    return retval;
  }
}

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
package org.omnaest.utils.structure.table.subspecification;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.TableComponent;
import org.omnaest.utils.structure.table.adapter.TableAdapter;
import org.omnaest.utils.structure.table.adapter.TableToResultSetAdapter;
import org.omnaest.utils.structure.table.adapter.TableToTypeListAdapter;

/**
 * This {@link Table} subspecification adds an {@link #as()} method to access a {@link TableAdapterProvider} as well as an
 * {@link #copyFrom(TableDataSource)} method to copy from {@link TableDataSource}s
 * 
 * @see TableAdapter
 * @see TableDataSource
 * @author Omnaest
 */
public interface TableAdaptable<E>
{
  /**
   * A {@link org.omnaest.utils.structure.table.subspecification.TableAdaptable.TableAdapterProvider} offers methods to support
   * the default {@link TableAdapter}s
   * 
   * @see TableAdapter
   * @see TableAdaptable
   * @see Table
   * @author Omnaest
   * @param <E>
   */
  public static interface TableAdapterProvider<E> extends TableComponent
  {
    /**
     * @see ResultSet
     * @see TableToResultSetAdapter
     * @return
     */
    public ResultSet resultSet();
    
    /**
     * @param beanClass
     * @see List
     * @see TableToTypeListAdapter
     * @return
     */
    public <T> List<T> listOfType( Class<? extends T> beanClass );
    
    /**
     * Returns a given {@link TableAdapter} instance under its adapter interface and initialized with the calling {@link Table}
     * 
     * @param tableAdapter
     * @return the initialized {@link TableAdapter} as its adapted interface
     */
    public <A> A adapter( TableAdapter<A, E> tableAdapter );
    
    /**
     * Returns the content of the {@link Table} as a two dimensional {@link Object} array.
     * 
     * @return
     */
    public Object[][] array();
    
    /**
     * Returns the content of the {@link Table} as a two dimensional array for the given {@link Class} type.
     * 
     * @return
     */
    public E[][] array( Class<? extends E> clazz );
    
    /**
     * Returns a {@link Map} view of the {@link Table} using one column for keys and one for values
     * 
     * @param columnForKey
     *          {@link Column}
     * @param columnForValue
     *          {@link Column}
     * @return {@link Map}
     */
    public Map<E, E> map( Column<E> columnForKey, Column<E> columnForValue );
    
    /**
     * Similar to {@link #map(Column, Column)} allowing to convert the key and value instances using two {@link ElementConverter}
     * 
     * @param columnForKeys
     *          {@link Column}
     * @param columnForValues
     *          {@link Column}
     * @param elementConverterForKeys
     *          {@link ElementConverter}
     * @param elementConverterForValues
     *          {@link ElementConverter}
     * @return
     */
    public <K, V> Map<K, V> map( Column<E> columnForKeys,
                                 Column<E> columnForValues,
                                 ElementConverter<E, K> elementConverterForKeys,
                                 ElementConverter<E, V> elementConverterForValues );
  }
  
  /**
   * @see TableAdapterProvider
   * @return
   */
  public TableAdapterProvider<E> as();
  
  /**
   * Clears the {@link Table} and copies the {@link Cell#getElement()} values from a given {@link TableDataSource} Copyies the
   * cell
   * 
   * @param tableDataSource
   * @return this
   */
  public Table<E> copyFrom( TableDataSource<E> tableDataSource );
}

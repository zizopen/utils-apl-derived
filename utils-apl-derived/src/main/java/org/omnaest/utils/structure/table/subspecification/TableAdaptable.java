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

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.TableComponent;
import org.omnaest.utils.structure.table.adapter.TableAdapter;
import org.omnaest.utils.structure.table.adapter.TableToResultSetAdapter;
import org.omnaest.utils.structure.table.adapter.TableToTypeListAdapter;

/**
 * This {@link Table} subspecification adds an {@link #as()} method to access a {@link TableAdapterProvider}
 * 
 * @author Omnaest
 * @param
 */
public interface TableAdaptable<E>
{
  /**
   * A {@link TableAdapterProvider} offers methods to support the default {@link TableAdapter}
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
     * @param beanClass TODO
     * @see List
     * @see TableToTypeListAdapter
     * @return
     */
    public <T> List<T> listOfType(Class<? extends T> beanClass);
  }
  
  /**
   * @see TableAdapterProvider
   * @return
   */
  public TableAdapterProvider<E> as();
  
}

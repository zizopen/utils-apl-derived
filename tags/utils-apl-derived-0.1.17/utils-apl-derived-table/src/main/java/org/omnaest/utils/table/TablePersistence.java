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
package org.omnaest.utils.table;

import java.io.Serializable;

import org.omnaest.utils.tuple.KeyValue;

/**
 * A {@link TablePersistence} handles the change events from a {@link Table} and persists any modified, added or removed data <br>
 * <br>
 * {@link #setColumnTitles(String[])} and {@link #setTableName(String)} are called before any attempt to access the
 * {@link TablePersistence}, so this methods are useful to synchronize the meta data
 * 
 * @author Omnaest
 * @param <E>
 */
public interface TablePersistence<E> extends Serializable
{
  public void update( int id, E[] elements );
  
  public void add( int id, E[] elements );
  
  public void remove( int id );
  
  public void removeAll();
  
  public Iterable<KeyValue<Integer, E[]>> allElements();
  
  /**
   * This is set before any attempt to write or read from the persistence or the table name changes
   * 
   * @param tableName
   */
  public void setTableName( String tableName );
  
  /**
   * This is set before any attempt to write or read from the persistence or if any of the column titles changes
   * 
   * @param columnTitles
   */
  public void setColumnTitles( String[] columnTitles );
}

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
package org.omnaest.utils.table2;

/**
 * Manager for the index structures of a particular {@link Table} instance
 * 
 * @author Omnaest
 * @param <E>
 * @param <C>
 */
public interface TableIndexManager<E, C extends ImmutableCell<E>>
{
  /**
   * Returns the {@link TableIndex} for the given column index position
   * 
   * @param columnIndex
   * @return
   */
  public TableIndex<E, C> of( int columnIndex );
  
  /**
   * Returns the {@link TableIndex} related to the given {@link ImmutableColumn}
   * 
   * @param column
   *          {@link ImmutableColumn}
   * @return
   */
  public TableIndex<E, C> of( ImmutableColumn<E> column );
}

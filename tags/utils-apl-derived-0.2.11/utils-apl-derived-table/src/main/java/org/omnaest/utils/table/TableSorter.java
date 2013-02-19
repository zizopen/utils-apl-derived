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
import java.util.Comparator;

/**
 * A {@link TableSorter} allows to sorts the {@link Table} based on the elements specified by the given column index position
 * 
 * @author Omnaest
 * @param <E>
 */
public interface TableSorter<E> extends Serializable
{
  /**
   * Enables the use of a table lock, which prevents other {@link Thread}s to read or write to the table during the sort operation
   * 
   * @return this
   */
  public TableSorter<E> withTableLock();
  
  /**
   * Sorts the {@link Table} based on the elements specified by the given column index position
   * 
   * @param columnIndex
   * @return the underlying table instance being sorted
   */
  public Table<E> by( int columnIndex );
  
  /**
   * Sets a {@link Comparator} which should be used for sorting
   * 
   * @param comparator
   *          {@link Comparator}
   * @return this
   */
  public TableSorter<E> using( Comparator<E> comparator );
  
  /**
   * Similar to {@link #by(int)} for a given {@link ImmutableColumn}
   * 
   * @param column
   * @return the underlying {@link Table} instance being sorted
   */
  public Table<E> by( final ImmutableColumn<E> column );
}

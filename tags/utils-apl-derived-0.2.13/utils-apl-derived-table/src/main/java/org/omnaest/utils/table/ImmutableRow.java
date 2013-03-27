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

/**
 * Immutable {@link Row}
 * 
 * @see ImmutableStripe
 * @see ImmutableColumn
 * @author Omnaest
 * @param <E>
 */
public interface ImmutableRow<E> extends ImmutableStripe<E>
{
  
  /**
   * An {@link ImmutableColumn.ColumnIdentity} implements {@link #hashCode()} and {@link #equals(Object)} in the way, that for the
   * same {@link ImmutableTable} and index position two {@link ImmutableColumn.ColumnIdentity}s are considered equal
   * 
   * @author Omnaest
   */
  public static interface RowIdentity<E>
  {
    public ImmutableTable<E> getTable();
    
    public ImmutableRow<E> row();
    
    public int getRowIndex();
  }
  
  /**
   * Returns a new {@link RowIdentity} instance which implements {@link #hashCode()} and {@link #equals(Object)}
   * 
   * @return
   */
  public RowIdentity<E> id();
}

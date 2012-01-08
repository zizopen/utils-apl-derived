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

import org.omnaest.utils.structure.table.Table;

/**
 * Defines the cloning interface for a {@link Table}
 * 
 * @see TableCoreImmutable
 * @author Omnaest
 * @param <E>
 */
public interface TableCloneable<E>
{
  /**
   * A {@link TableCloner} allows to clone a {@link Table} on various ways.
   * 
   * @author Omnaest
   * @param <E>
   */
  public static interface TableCloner<E>
  {
    /**
     * Clones only the {@link Table} structure. The returned {@link Table} and the source {@link Table} will still operate on the
     * same element instances of the content.
     * 
     * @return
     */
    public Table<E> structureOnly();
    
    /**
     * Returns a new {@link Table} instance with all the content of the underlying {@link Table} beeing cloned, too.
     * 
     * @return
     */
    public Table<E> structureAndContent();
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Returns the {@link TableCloner} for the {@link Table} which allows to clone it.
   * 
   * @return
   */
  public TableCloner<E> clone() throws CloneNotSupportedException;
}

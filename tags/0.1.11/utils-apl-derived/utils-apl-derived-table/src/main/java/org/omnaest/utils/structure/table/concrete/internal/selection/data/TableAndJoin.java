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
package org.omnaest.utils.structure.table.concrete.internal.selection.data;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.concrete.internal.selection.join.Join;

/**
 * @author Omnaest
 * @param <E>
 */
public class TableAndJoin<E>
{
  /* ********************************************** Variables ********************************************** */
  protected Table<E> table = null;
  protected Join<E>  join  = null;
  
  /* ********************************************** Methods ********************************************** */
  public TableAndJoin( Table<E> table, Join<E> join )
  {
    super();
    this.table = table;
    this.join = join;
  }
  
  /**
   * @return
   */
  protected Table<E> getTable()
  {
    return this.table;
  }
  
  /**
   * @return
   */
  protected Join<E> getJoin()
  {
    return this.join;
  }
  
}

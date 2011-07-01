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
package org.omnaest.utils.structure.table.view;

import org.omnaest.utils.io.XLSFile.TableRow;
import org.omnaest.utils.structure.table.Table;

/**
 * A {@link BeanTable} treats the horizontal rows of the table as Java Beans.
 * 
 * @author Omnaest
 * @see Table
 * @param <B>
 *          Java Bean type
 */
public interface BeanTable<B>
{
  
  /**
   * Returns the {@link TableRow} for the given row index position as Java Bean.
   * 
   * @param rowIndex
   * @return
   */
  public B getRow( int rowIndex );
  
  /**
   * Returns the underlying {@link Table} data structure.
   * 
   * @return
   */
  public Table<Object> getTable();
}

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

import java.util.List;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.CellImmutable;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;

/**
 * Source for {@link Table} to copy {@link Cell#getElement()} from
 * 
 * @author Omnaest
 * @param <E>
 */
public interface TableDataSource<E>
{
  /**
   * {@link Iterable} of an {@link Iterable} for the {@link Table}s {@link Row}s and the {@link Row}s {@link CellImmutable}s.
   * 
   * @return
   */
  public Iterable<? extends Iterable<? extends CellImmutable<E>>> rows();
  
  /**
   * Returns a new {@link List} with all the {@link Column#getTitleValue()} for the {@link TableDataSource}.
   * 
   * @return
   */
  public List<Object> getColumnTitleValueList();
  
  /**
   * Returns a new {@link List} with all the {@link Row#getTitleValue()} for the {@link TableDataSource}.
   * 
   * @return
   */
  public List<Object> getRowTitleValueList();
  
  /**
   * Returns the name for the whole {@link TableDataSource}.
   * 
   * @return
   */
  public Object getTableName();
}

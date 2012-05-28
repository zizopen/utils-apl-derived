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

import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Selection;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Selection.Order;

/**
 * @see Selection
 * @see Selection#orderBy(Column, Order)
 * @author Omnaest
 */
public class ColumnOrder<E>
{
  /* ********************************************** Variables ********************************************** */
  protected Column<E> column = null;
  protected Order     order  = null;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @see Selection#orderBy(ColumnOrder<E>...)
   * @see ColumnOrder
   * @param column
   * @param order
   */
  public ColumnOrder( Column<E> column, Order order )
  {
    super();
    this.column = column;
    this.order = order;
  }
  
  /**
   * @return
   */
  protected Column<E> getColumn()
  {
    return this.column;
  }
  
  /**
   * @return
   */
  protected Order getOrder()
  {
    return this.order;
  }
  
}

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
package org.omnaest.utils.structure.table.concrete.components.body;

import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.internal.TableInternal.CellInternal;

/**
 * @see Cell
 * @see CellInternal
 * @author Omnaest
 * @param <E>
 */
public class CellImpl<E> implements CellInternal<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 4937853192103513084L;
  protected Row<E>          row              = null;
  protected Column<E>       column           = null;
  protected E               element          = null;
  
  /* ********************************************** Methods ********************************************** */

  @Override
  public E getValue()
  {
    return this.element;
  }
  
  @Override
  public void setValue( E element )
  {
    this.element = element;
  }
  
}

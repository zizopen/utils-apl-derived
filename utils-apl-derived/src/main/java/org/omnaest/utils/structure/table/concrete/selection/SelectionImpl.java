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
package org.omnaest.utils.structure.table.concrete.selection;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.TableSelectable.Join;
import org.omnaest.utils.structure.table.TableSelectable.Order;
import org.omnaest.utils.structure.table.TableSelectable.Selection;
import org.omnaest.utils.structure.table.TableSelectable.Result;
import org.omnaest.utils.structure.table.TableSelectable.Where;
import org.omnaest.utils.structure.table.internal.TableInternal;

/**
 * @see Selection
 * @author Omnaest
 * @param <E>
 */
public class SelectionImpl<E> implements Selection<E>
{
  /* ********************************************** Variables ********************************************** */
  protected TableInternal<E> tableInternal = null;
  
  /* ********************************************** Methods ********************************************** */

  public SelectionImpl( TableInternal<E> tableInternal )
  {
    super();
    this.tableInternal = tableInternal;
  }
  
  @Override
  public Selection<E> columns( Column<E>... columns )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Selection<E> from( Table<E> table )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Selection<E> join( Join join )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Result<E> result()
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Selection<E> distinct()
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Selection<E> where( Where<E> where )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Selection<E> orderBy( Order<E> order )
  {
    // TODO Auto-generated method stub
    return null;
  }
  
}

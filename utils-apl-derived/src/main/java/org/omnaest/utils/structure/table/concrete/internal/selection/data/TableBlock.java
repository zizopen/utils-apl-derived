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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.concrete.internal.selection.SelectionExecutor;
import org.omnaest.utils.structure.table.concrete.internal.selection.join.Join;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Predicate;

/**
 * Temporary data element for filtering with {@link Predicate}s and joining
 * 
 * @see Predicate
 * @see SelectionExecutor
 * @see Join
 * @author Omnaest
 * @param <E>
 */
public class TableBlock<E>
{
  /* ********************************************** Variables ********************************************** */
  protected TableInternal<E>         tableInternal    = null;
  protected List<Column<E>>          columnList       = new ArrayList<Column<E>>();
  protected SortedSet<StripeData<E>> rowStripeDataSet = new TreeSet<StripeData<E>>();
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @return the tableInternal
   */
  public TableInternal<E> getTableInternal()
  {
    return this.tableInternal;
  }
  
  /**
   * @param tableInternal
   *          the tableInternal to set
   */
  public void setTableInternal( TableInternal<E> tableInternal )
  {
    this.tableInternal = tableInternal;
  }
  
  /**
   * @return the columnList
   */
  public List<Column<E>> getColumnList()
  {
    return this.columnList;
  }
  
  /**
   * @return the rowStripeDataSet
   */
  public SortedSet<StripeData<E>> getRowStripeDataSet()
  {
    return this.rowStripeDataSet;
  }
  
}

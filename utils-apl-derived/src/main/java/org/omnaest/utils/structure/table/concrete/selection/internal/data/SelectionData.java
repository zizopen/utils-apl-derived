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
package org.omnaest.utils.structure.table.concrete.selection.internal.data;

import java.util.ArrayList;
import java.util.List;

import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.concrete.predicates.internal.PredicateInternal;
import org.omnaest.utils.structure.table.concrete.selection.SelectionImpl;
import org.omnaest.utils.structure.table.internal.TableInternal;

/**
 * @see SelectionImpl
 * @author Omnaest
 * @param <E>
 */
public class SelectionData<E>
{
  /* ********************************************** Variables ********************************************** */
  private List<TableInternal<E>>     tableInternalList = new ArrayList<TableInternal<E>>();
  private List<Column<E>>            columnList        = new ArrayList<Column<E>>();
  private List<TableAndJoin<E>>      tableAndJoinList  = new ArrayList<TableAndJoin<E>>();
  private List<PredicateInternal<E>> predicateList     = new ArrayList<PredicateInternal<E>>();
  private List<ColumnOrder<E>>       columnOrderList   = new ArrayList<ColumnOrder<E>>();
  private boolean                    selectAllColumns  = true;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @return
   */
  public List<TableInternal<E>> getTableInternalList()
  {
    return this.tableInternalList;
  }
  
  /**
   * @param tableInternalList
   */
  public void setTableInternalList( List<TableInternal<E>> tableInternalList )
  {
    this.tableInternalList = tableInternalList;
  }
  
  /**
   * @return
   */
  public List<Column<E>> getColumnList()
  {
    return this.columnList;
  }
  
  /**
   * @param columnList
   */
  public void setColumnList( List<Column<E>> columnList )
  {
    this.columnList = columnList;
  }
  
  /**
   * @return
   */
  public List<TableAndJoin<E>> getTableAndJoinList()
  {
    return this.tableAndJoinList;
  }
  
  /**
   * @param tableAndJoinList
   */
  public void setTableAndJoinList( List<TableAndJoin<E>> tableAndJoinList )
  {
    this.tableAndJoinList = tableAndJoinList;
  }
  
  /**
   * @return
   */
  public List<PredicateInternal<E>> getPredicateList()
  {
    return this.predicateList;
  }
  
  /**
   * @param predicateList
   */
  public void setPredicateList( List<PredicateInternal<E>> predicateList )
  {
    this.predicateList = predicateList;
  }
  
  /**
   * @return
   */
  public List<ColumnOrder<E>> getColumnOrderList()
  {
    return this.columnOrderList;
  }
  
  /**
   * @param columnOrderList
   */
  public void setColumnOrderList( List<ColumnOrder<E>> columnOrderList )
  {
    this.columnOrderList = columnOrderList;
  }
  
  /**
   * @return
   */
  public boolean isSelectAllColumns()
  {
    return this.selectAllColumns;
  }
  
  /**
   * @param selectAllColumns
   */
  public void setSelectAllColumns( boolean selectAllColumns )
  {
    this.selectAllColumns = selectAllColumns;
  }
}

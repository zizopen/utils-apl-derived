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
import java.util.Map;

import org.omnaest.utils.structure.map.IdentityLinkedHashMap;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.concrete.internal.selection.SelectionImpl;
import org.omnaest.utils.structure.table.concrete.internal.selection.join.Join;
import org.omnaest.utils.structure.table.concrete.predicates.internal.filter.PredicateFilter;
import org.omnaest.utils.structure.table.concrete.predicates.internal.joiner.PredicateJoiner;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Selection.Order;

/**
 * @see SelectionImpl
 * @author Omnaest
 * @param <E>
 */
public class SelectionData<E>
{
  /* ********************************************** Variables ********************************************** */
  private List<Column<E>>          columnList          = new ArrayList<Column<E>>();
  private Map<Table<E>, Join<E>>   tableToJoinMap      = new IdentityLinkedHashMap<Table<E>, Join<E>>();
  private List<PredicateFilter<E>> predicateFilterList = new ArrayList<PredicateFilter<E>>();
  private List<PredicateJoiner<E>> predicateJoinerList = new ArrayList<PredicateJoiner<E>>();
  private Map<Column<E>, Order>    columnToOrderMap    = new IdentityLinkedHashMap<Column<E>, Order>();
  private boolean                  selectAllColumns    = true;
  private boolean                  distinct            = false;
  private int                      topNumberOfRows     = -1;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @return
   */
  public List<Column<E>> getColumnList()
  {
    return this.columnList;
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
  
  /**
   * @return the tableToJoinMap
   */
  public Map<Table<E>, Join<E>> getTableToJoinMap()
  {
    return this.tableToJoinMap;
  }
  
  /**
   * @return the columnToOrderMap
   */
  public Map<Column<E>, Order> getColumnToOrderMap()
  {
    return this.columnToOrderMap;
  }
  
  /**
   * @return the distinct
   */
  public boolean isDistinct()
  {
    return this.distinct;
  }
  
  /**
   * @param distinct
   *          the distinct to set
   */
  public void setDistinct( boolean distinct )
  {
    this.distinct = distinct;
  }
  
  /**
   * @return the predicateJoinerList
   */
  public List<PredicateJoiner<E>> getPredicateJoinerList()
  {
    return this.predicateJoinerList;
  }
  
  /**
   * @return the predicateFilterList
   */
  public List<PredicateFilter<E>> getPredicateFilterList()
  {
    return this.predicateFilterList;
  }
  
  /**
   * @return the topNumberOfRows
   */
  public int getTopNumberOfRows()
  {
    return this.topNumberOfRows;
  }
  
  /**
   * @param topNumberOfRows
   *          the topNumberOfRows to set
   */
  public void setTopNumberOfRows( int topNumberOfRows )
  {
    this.topNumberOfRows = topNumberOfRows;
  }
  
}

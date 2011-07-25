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

import java.util.ArrayList;
import java.util.List;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.TableSelectable.Join;
import org.omnaest.utils.structure.table.TableSelectable.Order;
import org.omnaest.utils.structure.table.TableSelectable.Result;
import org.omnaest.utils.structure.table.TableSelectable.Selection;
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
  protected List<TableInternal<E>> tableInternalList = new ArrayList<TableInternal<E>>();
  protected List<Column<E>>        columnList        = new ArrayList<Column<E>>();
  protected List<Join>             joinList          = new ArrayList<Join>();
  protected List<Where<E>>         whereList         = new ArrayList<Where<E>>();
  protected List<Order<E>>         orderList         = new ArrayList<Order<E>>();
  protected boolean                selectAllColumns  = true;
  protected SelectionExecutor<E>   selectionExecutor = new SelectionExecutor<E>( this );
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see SelectionImpl
   * @param tableInternal
   */
  public SelectionImpl( TableInternal<E> tableInternal )
  {
    super();
    
    //
    if ( tableInternal != null )
    {
      this.tableInternalList.add( tableInternal );
    }
  }
  
  @Override
  public Selection<E> columns( Column<E>... columns )
  {
    //
    if ( columns != null )
    {
      //
      for ( Column<E> column : columns )
      {
        this.columnList.add( column );
      }
      
      //
      this.selectAllColumns = false;
    }
    
    // 
    return this;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Selection<E> from( Table<E>... tables )
  {
    //
    if ( tables != null )
    {
      for ( Table<E> table : tables )
      {
        if ( table instanceof TableInternal )
        {
          this.tableInternalList.add( (TableInternal<E>) table );
        }
      }
    }
    
    // 
    return this;
  }
  
  @Override
  public Selection<E> join( Join join )
  {
    //
    if ( join != null )
    {
      this.joinList.add( join );
    }
    
    //
    return this;
  }
  
  @Override
  public Selection<E> distinct()
  {
    // TODO Auto-generated method stub
    return this;
  }
  
  @Override
  public Selection<E> where( Where<E>... wheres )
  {
    //
    if ( wheres != null )
    {
      for ( Where<E> where : wheres )
      {
        if ( where != null )
        {
          this.whereList.add( where );
        }
      }
    }
    
    // 
    return this;
  }
  
  @Override
  public Selection<E> orderBy( Order<E> order )
  {
    //
    if ( order != null )
    {
      this.orderList.add( order );
    }
    
    // 
    return this;
  }
  
  @Override
  public Selection<E> allColumns()
  {
    //
    this.selectAllColumns = true;
    
    // 
    return this;
  }
  
  @Override
  public Result<E> asView()
  {
    //
    Result<E> result = null;
    
    //
    if ( this.selectAllColumns )
    {
      //FIXME go on here
    }
    else
    {
      
    }
    
    //
    
    // 
    return result;
  }
  
  @Override
  public Table<E> asTable()
  {
    return this.selectionExecutor.execute();
  }
  
  /**
   * @return
   */
  protected List<TableInternal<E>> getTableInternalList()
  {
    return this.tableInternalList;
  }
  
  /**
   * @param tableInternalList
   */
  protected void setTableInternalList( List<TableInternal<E>> tableInternalList )
  {
    this.tableInternalList = tableInternalList;
  }
  
  /**
   * @return
   */
  protected List<Column<E>> getColumnList()
  {
    return this.columnList;
  }
  
  /**
   * @param columnList
   */
  protected void setColumnList( List<Column<E>> columnList )
  {
    this.columnList = columnList;
  }
  
  /**
   * @return
   */
  protected List<Join> getJoinList()
  {
    return this.joinList;
  }
  
  /**
   * @param joinList
   */
  protected void setJoinList( List<Join> joinList )
  {
    this.joinList = joinList;
  }
  
  /**
   * @return
   */
  protected List<Where<E>> getWhereList()
  {
    return this.whereList;
  }
  
  /**
   * @param whereList
   */
  protected void setWhereList( List<Where<E>> whereList )
  {
    this.whereList = whereList;
  }
  
  /**
   * @return
   */
  protected List<Order<E>> getOrderList()
  {
    return this.orderList;
  }
  
  /**
   * @param orderList
   */
  protected void setOrderList( List<Order<E>> orderList )
  {
    this.orderList = orderList;
  }
  
  /**
   * @return
   */
  protected boolean isSelectAllColumns()
  {
    return this.selectAllColumns;
  }
  
  /**
   * @param selectAllColumns
   */
  protected void setSelectAllColumns( boolean selectAllColumns )
  {
    this.selectAllColumns = selectAllColumns;
  }
  
}

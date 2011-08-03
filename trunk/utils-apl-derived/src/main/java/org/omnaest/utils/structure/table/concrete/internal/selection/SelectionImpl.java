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
package org.omnaest.utils.structure.table.concrete.internal.selection;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.concrete.internal.selection.data.SelectionData;
import org.omnaest.utils.structure.table.concrete.internal.selection.join.Join;
import org.omnaest.utils.structure.table.concrete.internal.selection.join.JoinInner;
import org.omnaest.utils.structure.table.concrete.predicates.internal.filter.PredicateFilter;
import org.omnaest.utils.structure.table.concrete.predicates.internal.joiner.PredicateJoiner;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Predicate;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Selection;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.SelectionJoin;
import org.omnaest.utils.structure.table.view.TableView;
import org.omnaest.utils.structure.table.view.concrete.TableViewImpl;

/**
 * @see Selection
 * @author Omnaest
 * @param <E>
 */
public class SelectionImpl<E> implements SelectionJoin<E>
{
  protected SelectionData<E>     selectionData     = new SelectionData<E>();
  protected SelectionExecutor<E> selectionExecutor = new SelectionExecutor<E>( this.selectionData );
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see SelectionImpl
   * @param tableInternal
   */
  @SuppressWarnings("unchecked")
  public SelectionImpl( TableInternal<E> tableInternal )
  {
    //
    super();
    
    //
    if ( tableInternal != null )
    {
      this.from( tableInternal.getUnderlyingTable() );
    }
  }
  
  @Override
  public SelectionJoin<E> innerJoin( Table<E> table )
  {
    //    
    if ( table != null )
    {
      //
      Join<E> join = new JoinInner<E>();
      this.selectionData.getTableToJoinMap().put( table, join );
    }
    
    // 
    return this;
  }
  
  @Override
  public SelectionJoin<E> on( Predicate<E>... predicates )
  {
    //
    this.where( predicates );
    
    //
    return this;
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
        this.selectionData.getColumnList().add( column );
      }
      
      //
      this.selectionData.setSelectAllColumns( false );
    }
    
    // 
    return this;
  }
  
  @Override
  public Selection<E> from( Table<E>... tables )
  {
    //
    if ( tables != null )
    {
      for ( Table<E> table : tables )
      {
        if ( table != null )
        {
          //
          this.innerJoin( table );
        }
      }
    }
    
    // 
    return this;
  }
  
  @Override
  public Selection<E> distinct()
  {
    //
    this.selectionData.setDistinct( true );
    
    //
    return this;
  }
  
  @Override
  public Selection<E> where( Predicate<E>... predicates )
  {
    //
    if ( predicates != null )
    {
      for ( Predicate<E> predicate : predicates )
      {
        if ( predicate instanceof PredicateFilter )
        {
          this.selectionData.getPredicateFilterList().add( (PredicateFilter<E>) predicate );
        }
        if ( predicate instanceof PredicateJoiner )
        {
          this.selectionData.getPredicateJoinerList().add( (PredicateJoiner<E>) predicate );
        }
      }
    }
    
    // 
    return this;
  }
  
  @Override
  public Selection<E> orderBy( Column<E> column, Order order )
  {
    //
    if ( column != null )
    {
      //
      order = order == null ? Order.ASCENDING : order;
      this.selectionData.getColumnToOrderMap().put( column, order );
    }
    
    // 
    return this;
  }
  
  @Override
  public Selection<E> orderBy( Column<E> column )
  {
    //    
    Order order = null;
    this.orderBy( column, order );
    
    // 
    return this;
  }
  
  @Override
  public Selection<E> allColumns()
  {
    //
    this.selectionData.setSelectAllColumns( true );
    
    // 
    return this;
  }
  
  @Override
  public TableView<E> asView()
  {
    //
    TableView<E> result = null;
    
    //
    result = new TableViewImpl<E>( this );
    result.refresh();
    
    // 
    return result;
  }
  
  @Override
  public Table<E> asTable()
  {
    return this.selectionExecutor.execute();
  }
  
}

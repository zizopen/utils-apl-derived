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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.structure.collection.ListUtils;
import org.omnaest.utils.structure.collection.ListUtils.ElementTransformer;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.concrete.internal.helper.StripeDataHelper;
import org.omnaest.utils.structure.table.concrete.predicates.internal.PredicateInternal;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.CellData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeDataList;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeInternal;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Selection;

import com.sun.rowset.internal.Row;

/**
 * @see SelectionImpl
 * @author Omnaest
 * @param <E>
 */
public class SelectionExecutor<E>
{
  /* ********************************************** Variables ********************************************** */
  protected SelectionImpl<E> selectionImpl = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see StripeInternal
   * @see StripeData
   * @see TableInternal
   * @author Omnaest
   * @param <E>
   */
  protected static class StripeInternalData<E>
  {
    /* ********************************************** Variables ********************************************** */
    protected StripeData<E>    stripeData    = null;
    protected TableInternal<E> tableInternal = null;
    
    /* ********************************************** Variables ********************************************** */
    
    /**
     * @param stripeData
     * @param tableInternal
     */
    public StripeInternalData( StripeData<E> stripeData, TableInternal<E> tableInternal )
    {
      super();
      this.stripeData = stripeData;
      this.tableInternal = tableInternal;
    }
    
    /**
     * @return
     */
    public StripeData<E> getStripeData()
    {
      return this.stripeData;
    }
    
    /**
     * @return
     */
    public TableInternal<E> getTableInternal()
    {
      return this.tableInternal;
    }
    
    /**
     * Returns true if the {@link #getStripeData()} and {@link #getTableInternal()} are not null
     * 
     * @return
     */
    public boolean isValid()
    {
      return this.stripeData != null && this.tableInternal != null;
    }
  }
  
  /**
   * Makes the {@link ArrayTable.ArrayTableInternal} accessible for the {@link SelectionExecutor}
   * 
   * @see ArrayTable
   * @see TableInternal
   * @see ArrayTableInternal
   * @author Omnaest
   * @param <E>
   */
  protected static class ArrayTableWithAccessibleTableInternal<E> extends ArrayTable<E>
  {
    /* ********************************************** Constants ********************************************** */
    private static final long serialVersionUID = 883639402376632483L;
    
    /* ********************************************** Variables ********************************************** */
    
    @Override
    protected TableInternal<E> getTableInternal()
    {
      return super.getTableInternal();
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param selectionImpl
   */
  public SelectionExecutor( SelectionImpl<E> selectionImpl )
  {
    super();
    this.selectionImpl = selectionImpl;
  }
  
  /**
   * @param selection
   * @return
   */
  public Table<E> execute()
  {
    //
    TableInternal<E> tableInternal = null;
    
    //
    if ( this.selectionImpl.isSelectAllColumns() )
    {
      this.resolveAllColumnsFromTablesAndAttachThemToTheSelectionColumnList();
    }
    
    //
    tableInternal = this.createTableWithDeclaredColumns();
    this.mergeTableNames( tableInternal );
    
    //fill and filter rows
    this.determineAndMergeRowStripeDataList( tableInternal );
    this.executeWhereClauses( tableInternal );
    
    //
    return tableInternal.getUnderlyingTable();
  }
  
  /**
   * Executes the {@link PredicateInternal}s declared for the
   * {@link Selection#where(org.omnaest.utils.structure.table.subspecification.TableSelectable.Predicate...)} clause
   * 
   * @param tableInternal
   */
  private void executeWhereClauses( TableInternal<E> tableInternal )
  {
    //
    for ( PredicateInternal<E> wherePredicateInternal : this.selectionImpl.getWherePredicateList() )
    {
      wherePredicateInternal.filterStripeDataSet( tableInternal );
    }
  }
  
  /**
   * Merges the {@link Table#getTableName()}s
   * 
   * @param tableInternal
   */
  private void mergeTableNames( TableInternal<E> tableInternal )
  {
    //
    List<TableInternal<E>> tableInternalReferencedByColumnsList = this.determineTableInternalReferencedByColumnsList();
    
    //
    if ( tableInternalReferencedByColumnsList.size() == 0 )
    {
    }
    else if ( tableInternalReferencedByColumnsList.size() == 1 )
    {
      //
      tableInternal.getUnderlyingTable().setTableName( tableInternalReferencedByColumnsList.get( 0 )
                                                                                           .getUnderlyingTable()
                                                                                           .getTableName() );
    }
    else
    {
      //
      ElementTransformer<TableInternal<E>, Object> elementTransformer = new ElementTransformer<TableInternal<E>, Object>()
      {
        @Override
        public Object transformElement( TableInternal<E> tableInternal )
        {
          // 
          return tableInternal.getUnderlyingTable().getTableName();
        }
      };
      List<Object> tableNameList = ListUtils.transform( tableInternalReferencedByColumnsList, elementTransformer );
      
      //
      tableInternal.getUnderlyingTable().setTableName( tableNameList );
    }
  }
  
  /**
   * Merges all the {@link Row}s of the specified {@link Column}s into the given {@link Table} using a Cartesian product
   */
  private void determineAndMergeRowStripeDataList( TableInternal<E> tableInternal )
  {
    //
    List<StripeData<E>> stripeDataForCartesianProductList = new ArrayList<StripeData<E>>();
    
    //
    Map<TableInternal<E>, Set<CellData<E>>> tableInternalToColumnCellDataSetMap = this.determineTableInternalToColumnCellDataSetMap();
    Set<CellData<E>> cellDataSetFromColumns = new HashSet<CellData<E>>();
    for ( TableInternal<E> tableInternalForColumns : tableInternalToColumnCellDataSetMap.keySet() )
    {
      //
      cellDataSetFromColumns.addAll( tableInternalToColumnCellDataSetMap.get( tableInternalForColumns ) );
      
      //      
      StripeDataList<E> stripeDataList = tableInternal.getTableContent().getStripeDataList( StripeType.ROW );
      
      //
      List<StripeData<E>> stripeDataForCartesianProductListNew = new ArrayList<StripeData<E>>();
      for ( StripeData<E> stripeDataNew : tableInternalForColumns.getTableContent().getRowStripeDataList() )
      {
        //
        if ( stripeDataForCartesianProductList.isEmpty() )
        {
          //
          @SuppressWarnings("unchecked")
          StripeData<E> stripeDataMerged = StripeDataHelper.createNewStripeDataFromExisting( stripeDataList,
                                                                                             cellDataSetFromColumns,
                                                                                             stripeDataNew );
          
          //
          stripeDataForCartesianProductListNew.add( stripeDataMerged );
        }
        else
        {
          //
          for ( StripeData<E> stripeDataExisting : stripeDataForCartesianProductList )
          {
            //
            @SuppressWarnings("unchecked")
            StripeData<E> stripeDataMerged = StripeDataHelper.createNewStripeDataFromExisting( stripeDataList,
                                                                                               cellDataSetFromColumns,
                                                                                               stripeDataExisting, stripeDataNew );
            
            //
            stripeDataForCartesianProductListNew.add( stripeDataMerged );
          }
        }
      }
      
      //
      stripeDataForCartesianProductList = stripeDataForCartesianProductListNew;
    }
    
    //
    tableInternal.getTableContent().getRowStripeDataList().addAllStripeData( stripeDataForCartesianProductList );
  }
  
  /**
   * Resolves a {@link Map} of all {@link TableInternal} instances which belongs to the declared {@link Column}s and maps the
   * {@link Column}s {@link StripeData} to them.
   * 
   * @return
   */
  private Map<TableInternal<E>, Set<CellData<E>>> determineTableInternalToColumnCellDataSetMap()
  {
    //    
    Map<TableInternal<E>, Set<CellData<E>>> retmap = new LinkedHashMap<TableInternal<E>, Set<CellData<E>>>();
    
    //
    for ( Column<E> column : this.selectionImpl.getColumnList() )
    {
      //
      SelectionExecutor.ensureColumnHasCellDataInstancesForAllRows( column );
      
      //
      StripeInternalData<E> stripeInternalData = SelectionExecutor.<E> determineStripeDataFromStripe( column );
      if ( stripeInternalData != null && stripeInternalData.isValid() )
      {
        //
        TableInternal<E> tableInternal = stripeInternalData.getTableInternal();
        
        //
        if ( !retmap.containsKey( tableInternal ) )
        {
          retmap.put( tableInternal, new HashSet<CellData<E>>() );
        }
        
        //
        Set<CellData<E>> cellDataSet = retmap.get( tableInternal );
        cellDataSet.addAll( stripeInternalData.getStripeData().getCellDataSet() );
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Determines all {@link TableInternal} references used by the declared {@link Column}s
   * 
   * @return
   */
  private List<TableInternal<E>> determineTableInternalReferencedByColumnsList()
  {
    //    
    List<TableInternal<E>> retlist = new ArrayList<TableInternal<E>>();
    
    //
    for ( Column<E> column : this.selectionImpl.getColumnList() )
    {
      //
      StripeInternalData<E> stripeInternalData = SelectionExecutor.<E> determineStripeDataFromStripe( column );
      if ( stripeInternalData != null && stripeInternalData.isValid() )
      {
        //
        TableInternal<E> tableInternal = stripeInternalData.getTableInternal();
        
        //
        if ( !retlist.contains( tableInternal ) )
        {
          retlist.add( tableInternal );
        }
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * @param column
   */
  private static <E> void ensureColumnHasCellDataInstancesForAllRows( Column<E> column )
  {
    //
    for ( @SuppressWarnings("unused")
    Cell<E> cell : column.cells() )
    {
    }
  }
  
  /**
   * 
   */
  private void resolveAllColumnsFromTablesAndAttachThemToTheSelectionColumnList()
  {
    for ( TableInternal<E> tableInternal : this.selectionImpl.getTableInternalList() )
    {
      //
      if ( tableInternal != null )
      {
        //
        Table<E> table = tableInternal.getUnderlyingTable();
        if ( table != null )
        {
          //
          List<Column<E>> columnList = this.selectionImpl.getColumnList();
          for ( Column<E> column : table.getColumnList() )
          {
            if ( !columnList.contains( column ) )
            {
              columnList.add( column );
            }
          }
        }
      }
    }
  }
  
  /**
   * @return
   */
  private TableInternal<E> createTableWithDeclaredColumns()
  {
    //
    TableInternal<E> retval = null;
    
    //
    ArrayTableWithAccessibleTableInternal<E> table = new ArrayTableWithAccessibleTableInternal<E>();
    retval = table.getTableInternal();
    
    //
    for ( Column<E> column : this.selectionImpl.getColumnList() )
    {
      //
      StripeInternalData<E> stripeInternalData = SelectionExecutor.<E> determineStripeDataFromStripe( column );
      if ( stripeInternalData != null )
      {
        //
        StripeDataList<E> columnStripeDataList = retval.getTableContent().getColumnStripeDataList();
        
        //        
        StripeData<E> stripeDataOld = stripeInternalData.getStripeData();
        if ( stripeDataOld != null )
        {
          //
          @SuppressWarnings("unchecked")
          StripeData<E> stripeDataNew = StripeDataHelper.createNewStripeDataFromExisting( columnStripeDataList, stripeDataOld );
          
          //
          columnStripeDataList.addStripeData( stripeDataNew );
        }
      }
    }
    
    //
    return retval;
  }
  
  /**
   * @see StripeInternalData
   * @param stripe
   * @return
   */
  private static <E> StripeInternalData<E> determineStripeDataFromStripe( Stripe<E> stripe )
  {
    //
    StripeInternalData<E> retval = null;
    
    //
    if ( stripe instanceof StripeInternal )
    {
      //
      StripeInternal<E> stripeInternal = (StripeInternal<E>) stripe;
      
      //
      StripeData<E> stripeData = stripeInternal.getStripeData();
      TableInternal<E> tableInternal = stripeInternal.getTableInternal();
      
      //
      retval = new StripeInternalData<E>( stripeData, tableInternal );
    }
    
    //
    return retval;
  }
  
}

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.ComparatorUtils;
import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.IdentityArrayListBasedSet;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.map.IdentityLinkedHashMap;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.concrete.internal.StripeFactory;
import org.omnaest.utils.structure.table.concrete.internal.helper.StripeDataHelper;
import org.omnaest.utils.structure.table.concrete.internal.helper.TableInternalHelper;
import org.omnaest.utils.structure.table.concrete.internal.selection.data.SelectionData;
import org.omnaest.utils.structure.table.concrete.internal.selection.data.TableBlock;
import org.omnaest.utils.structure.table.concrete.internal.selection.join.Join;
import org.omnaest.utils.structure.table.concrete.internal.selection.join.JoinInner;
import org.omnaest.utils.structure.table.concrete.internal.selection.scannable.ScannableStripeDataContainer;
import org.omnaest.utils.structure.table.concrete.internal.selection.scannable.ScannableStripeDataContainerFullScan;
import org.omnaest.utils.structure.table.concrete.internal.selection.scannable.ScannableStripeDataContainerIndex;
import org.omnaest.utils.structure.table.concrete.predicates.internal.filter.ColumnHaveDistinctRows;
import org.omnaest.utils.structure.table.concrete.predicates.internal.filter.PredicateFilter;
import org.omnaest.utils.structure.table.concrete.predicates.internal.joiner.PredicateJoiner;
import org.omnaest.utils.structure.table.concrete.predicates.internal.joiner.PredicateJoinerCollector;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeDataList;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeInternal;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Selection;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Selection.Order;

/**
 * @see SelectionImpl
 * @author Omnaest
 * @param <E>
 */
public class SelectionExecutor<E>
{
  /* ********************************************** Variables ********************************************** */
  protected SelectionData<E> selectionData = null;
  
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
  public SelectionExecutor( SelectionData<E> selectionData )
  {
    super();
    this.selectionData = selectionData;
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
    this.normalizeSelectionData();
    
    //
    tableInternal = this.createTableWithDeclaredColumns();
    this.mergeTableNames( tableInternal );
    
    //
    Map<Column<E>, TableBlock<E>> columnToTableBlockMap = this.createColumnToTableBlockMap();
    this.createStripeDataIndexes( columnToTableBlockMap.values() );
    this.processPredicates( columnToTableBlockMap );
    TableBlock<E> joinedTableBlock = this.joinTableBlocks( columnToTableBlockMap, tableInternal.getTableContent()
                                                                                               .getRowStripeDataList() );
    this.processDistinctPredicate( joinedTableBlock );
    this.processOrders( joinedTableBlock );
    
    //
    SelectionExecutor.<E> mergeJoinedTableBlockIntoTableInternal( tableInternal, joinedTableBlock );
    this.truncateToTopNumberOfRows( tableInternal );
    
    //
    return tableInternal.getUnderlyingTable();
  }
  
  /**
   * @see Table#truncateRows(int)
   * @param tableInternal
   */
  private void truncateToTopNumberOfRows( TableInternal<E> tableInternal )
  {
    //
    int topNumberOfRows = this.selectionData.getTopNumberOfRows();
    if ( topNumberOfRows >= 0 )
    {
      tableInternal.getUnderlyingTable().truncateRows( topNumberOfRows );
    }
  }
  
  /**
   * @param joinedTableBlock
   */
  private void processOrders( final TableBlock<E> joinedTableBlock )
  {
    //
    final Map<Column<E>, Order> columnToOrderMap = this.selectionData.getColumnToOrderMap();
    
    //
    if ( !columnToOrderMap.isEmpty() )
    {
      
      //
      final Set<StripeData<E>> rowStripeDataSet = joinedTableBlock.getRowStripeDataSet();
      final StripeFactory<E> stripeFactory = joinedTableBlock.getTableInternal().getStripeFactory();
      
      //    
      ElementConverter<StripeData<E>, StripeInternal<E>> elementTransformer = new ElementConverter<StripeData<E>, StripeInternal<E>>()
      {
        @Override
        public StripeInternal<E> convert( StripeData<E> stripeData )
        {
          return stripeFactory.newInstanceOfStripeInternal( stripeData );
        }
      };
      List<StripeInternal<E>> rowStripeInternalListToBeSorted = ListUtils.convert( rowStripeDataSet, elementTransformer );
      Comparator<StripeInternal<E>> comparator = new Comparator<StripeInternal<E>>()
      {
        @SuppressWarnings("unchecked")
        @Override
        public int compare( StripeInternal<E> stripeInternal1, StripeInternal<E> stripeInternal2 )
        {
          //
          int retval = 0;
          for ( Column<E> column : columnToOrderMap.keySet() )
          {
            //
            Order order = columnToOrderMap.get( column );
            final int orderMultiplicator = Order.DESCENDING.equals( order ) ? -1 : 1;
            
            //
            E cellElement1 = stripeInternal1.getCellElement( column );
            E cellElement2 = stripeInternal2.getCellElement( column );
            
            //
            if ( cellElement1 != null && cellElement2 != null )
            {
              retval = ComparatorUtils.naturalComparator().compare( cellElement1, cellElement2 );
              retval *= orderMultiplicator;
            }
            
            //
            if ( retval != 0 )
            {
              break;
            }
          }
          
          // 
          return retval;
        }
      };
      Collections.sort( rowStripeInternalListToBeSorted, comparator );
      
      //
      List<StripeData<E>> rowStripeDataListSorted = ListUtils.convert( rowStripeInternalListToBeSorted,
                                                                         new ElementConverter<StripeInternal<E>, StripeData<E>>()
                                                                         {
                                                                           @Override
                                                                           public StripeData<E> convert( StripeInternal<E> stripeInternal )
                                                                           {
                                                                             return stripeInternal.getStripeData();
                                                                           }
                                                                         } );
      
      rowStripeDataSet.clear();
      rowStripeDataSet.addAll( rowStripeDataListSorted );
    }
  }
  
  /**
   * Creates the {@link TableBlock#getColumnToStripeDataIndexMap()}
   * 
   * @param tableBlockCollection
   */
  private void createStripeDataIndexes( Collection<TableBlock<E>> tableBlockCollection )
  {
    //
    if ( tableBlockCollection != null )
    {
      //
      Set<Column<E>> columnSetUsedByPredicates = this.determineColumnSetUsedByPredicates();
      
      //
      for ( TableBlock<E> tableBlock : tableBlockCollection )
      {
        if ( tableBlock != null )
        {
          //
          Map<Column<E>, ScannableStripeDataContainer<E>> columnToScannableStripeDataContainerMap = tableBlock.getColumnToScannableStripeDataContainerMap();
          TableInternal<E> tableInternal = tableBlock.getTableInternal();
          for ( Column<E> column : tableBlock.getColumnList() )
          {
            //
            if ( columnSetUsedByPredicates.contains( column ) )
            {
              //
              ScannableStripeDataContainer<E> scannableStripeDataContainer = new ScannableStripeDataContainerIndex<E>(
                                                                                                                       tableInternal,
                                                                                                                       column );
              if ( !scannableStripeDataContainer.isValid() )
              {
                scannableStripeDataContainer = new ScannableStripeDataContainerFullScan<E>( tableInternal, column );
              }
              
              //
              columnToScannableStripeDataContainerMap.put( column, scannableStripeDataContainer );
            }
          }
        }
      }
    }
  }
  
  private Set<Column<E>> determineColumnSetUsedByPredicates()
  {
    //
    Set<Column<E>> retset = new HashSet<Column<E>>();
    
    //
    for ( PredicateFilter<E> predicateFilter : this.selectionData.getPredicateFilterList() )
    {
      Column<E>[] requiredColumns = predicateFilter.getRequiredColumns();
      retset.addAll( Arrays.asList( requiredColumns ) );
    }
    
    //
    for ( PredicateJoiner<E> predicateJoiner : this.selectionData.getPredicateJoinerList() )
    {
      Column<E>[] requiredColumns = predicateJoiner.getRequiredColumns();
      retset.addAll( Arrays.asList( requiredColumns ) );
    }
    
    //
    return retset;
  }
  
  /**
   * @param tableInternal
   * @param tableBlock
   */
  private static <E> void mergeJoinedTableBlockIntoTableInternal( TableInternal<E> tableInternal, TableBlock<E> tableBlock )
  {
    //
    if ( tableBlock != null && tableInternal != null )
    {
      //
      Set<StripeData<E>> rowStripeDataSet = tableBlock.getRowStripeDataSet();
      tableInternal.getTableContent().getRowStripeDataList().addAllStripeData( rowStripeDataSet );
    }
  }
  
  /**
   * @param stripeDataList
   * @param columnToTableBlockMap
   */
  private TableBlock<E> joinTableBlocks( Map<Column<E>, TableBlock<E>> columnToTableBlockMap, StripeDataList<E> stripeDataList )
  {
    //
    TableBlock<E> retval = null;
    
    //
    Map<Table<E>, TableBlock<E>> tableToTableBlockMap = SelectionExecutor.<E> determineTableToTableBlockMap( columnToTableBlockMap.values() );
    Map<Table<E>, Join<E>> tableToJoinMap = this.selectionData.getTableToJoinMap();
    for ( Table<E> table : tableToJoinMap.keySet() )
    {
      //
      TableBlock<E> tableBlock = tableToTableBlockMap.get( table );
      Join<E> join = tableToJoinMap.get( table );
      if ( tableBlock != null && join != null )
      {
        //
        if ( retval == null )
        {
          //
          retval = tableBlock;
        }
        else
        {
          //
          TableBlock<E> tableBlockLeft = retval;
          TableBlock<E> tableBlockRight = tableBlock;
          PredicateJoinerCollector<E> predicateJoinerCollector = this.createPredicateJoinerCollector();
          TableBlock<E> joinedTableBlock = join.joinTableBlocks( tableBlockLeft, tableBlockRight, stripeDataList,
                                                                 predicateJoinerCollector );
          
          //
          if ( joinedTableBlock != null )
          {
            retval = joinedTableBlock;
          }
        }
      }
    }
    
    //
    return retval;
  }
  
  /**
   * @see PredicateJoinerCollector
   * @return
   */
  private PredicateJoinerCollector<E> createPredicateJoinerCollector()
  {
    //
    Set<PredicateJoiner<E>> predicateJoinerSet = new LinkedHashSet<PredicateJoiner<E>>(
                                                                                        this.selectionData.getPredicateJoinerList() );
    
    //
    return new PredicateJoinerCollector<E>( predicateJoinerSet );
  }
  
  /**
   * @param tableBlockCollection
   * @return
   */
  private static <E> Map<Table<E>, TableBlock<E>> determineTableToTableBlockMap( Collection<TableBlock<E>> tableBlockCollection )
  {
    //
    Map<Table<E>, TableBlock<E>> retmap = new IdentityLinkedHashMap<Table<E>, TableBlock<E>>();
    
    //
    if ( tableBlockCollection != null )
    {
      for ( TableBlock<E> tableBlock : tableBlockCollection )
      {
        if ( tableBlock != null )
        {
          TableInternal<E> tableInternal = tableBlock.getTableInternal();
          if ( tableInternal != null )
          {
            Table<E> table = tableInternal.getUnderlyingTable();
            if ( table != null )
            {
              retmap.put( table, tableBlock );
            }
          }
        }
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * @return
   */
  private Map<Column<E>, TableBlock<E>> createColumnToTableBlockMap()
  {
    //   
    Map<Column<E>, TableBlock<E>> columnToTableBlockMap = new LinkedHashMap<Column<E>, TableBlock<E>>();
    Map<Table<E>, TableBlock<E>> tableToTableBlockMap = new IdentityLinkedHashMap<Table<E>, TableBlock<E>>();
    
    //
    for ( Table<E> table : this.selectionData.getTableToJoinMap().keySet() )
    {
      //
      if ( !tableToTableBlockMap.containsKey( table ) )
      {
        //
        TableInternal<E> tableInternal = TableInternalHelper.extractTableInternalFromTable( table );
        if ( tableInternal != null )
        {
          //
          TableBlock<E> tableBlock = new TableBlock<E>();
          tableBlock.setTableInternal( tableInternal );
          for ( StripeData<E> stripeData : tableInternal.getTableContent().getRowStripeDataList() )
          {
            tableBlock.getRowStripeDataSet().add( stripeData );
          }
          
          //
          tableToTableBlockMap.put( table, tableBlock );
        }
        
        //
        TableBlock<E> tableBlock = tableToTableBlockMap.get( table );
        
        //
        List<Column<E>> columnList = table.getColumnList();
        for ( Column<E> column : columnList )
        {
          //
          tableBlock.getColumnList().add( column );
          columnToTableBlockMap.put( column, tableBlock );
          
          //
          ensureColumnHasCellDataInstancesForAllRows( column );
        }
      }
    }
    
    //
    return columnToTableBlockMap;
  }
  
  /**
   * 
   */
  private void normalizeSelectionData()
  {
    //
    if ( this.selectionData.isSelectAllColumns() )
    {
      this.determineAllColumnsFromTablesAndAttachThemToTheSelectionColumnList();
    }
    
    //
    Set<Table<E>> tableListReferencedByColumn = this.determineTableSetReferencedByColumn();
    Map<Table<E>, Join<E>> tableToJoinMap = this.selectionData.getTableToJoinMap();
    for ( Table<E> joinTable : tableListReferencedByColumn )
    {
      //
      boolean containsObjectIdentity = CollectionUtils.containsObjectIdentity( tableToJoinMap.keySet(), joinTable );
      if ( !containsObjectIdentity )
      {
        tableToJoinMap.put( joinTable, new JoinInner<E>() );
      }
    }
  }
  
  /**
   * Executes the {@link PredicateFilter}s declared for the
   * {@link Selection#where(org.omnaest.utils.structure.table.subspecification.TableSelectable.Predicate...)} clause
   * 
   * @param columnToTableBlockMap
   */
  private void processPredicates( Map<Column<E>, TableBlock<E>> columnToTableBlockMap )
  {
    //
    List<PredicateFilter<E>> predicateFilterList = this.selectionData.getPredicateFilterList();
    for ( PredicateFilter<E> predicateInternal : predicateFilterList )
    {
      //
      Column<E>[] requiredColumns = predicateInternal.getRequiredColumns();
      
      //
      Set<TableBlock<E>> tableBlockSet = new IdentityArrayListBasedSet<TableBlock<E>>();
      for ( Column<E> column : requiredColumns )
      {
        //
        TableBlock<E> tableBlock = columnToTableBlockMap.get( column );
        if ( tableBlock != null )
        {
          tableBlockSet.add( tableBlock );
        }
      }
      
      //
      predicateInternal.filterStripeDataSet( tableBlockSet );
    }
  }
  
  /**
   * Executes the {@link PredicateFilter}s declared for the
   * {@link Selection#where(org.omnaest.utils.structure.table.subspecification.TableSelectable.Predicate...)} clause
   * 
   * @param joinedTableBlock
   */
  @SuppressWarnings("unchecked")
  private void processDistinctPredicate( TableBlock<E> joinedTableBlock )
  {
    //
    if ( this.selectionData.isDistinct() )
    {
      PredicateFilter<E> columnHaveDistinctRows = new ColumnHaveDistinctRows<E>();
      columnHaveDistinctRows.filterStripeDataSet( Arrays.asList( joinedTableBlock ) );
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
    Set<Table<E>> tableReferencedByColumnsList = this.determineTableSetReferencedByColumn();
    
    //
    if ( tableReferencedByColumnsList.size() == 0 )
    {
    }
    else if ( tableReferencedByColumnsList.size() == 1 )
    {
      //
      tableInternal.getUnderlyingTable().setTableName( tableReferencedByColumnsList.iterator().next().getTableName() );
    }
    else
    {
      //
      ElementConverter<Table<E>, Object> elementTransformer = new ElementConverter<Table<E>, Object>()
      {
        @Override
        public Object convert( Table<E> table )
        {
          // 
          return table.getTableName();
        }
      };
      List<Object> tableNameList = ListUtils.convert( tableReferencedByColumnsList, elementTransformer );
      
      //
      tableInternal.getUnderlyingTable().setTableName( tableNameList );
    }
  }
  
  /**
   * Determines all {@link Table} references used by the declared {@link Column}s
   * 
   * @return
   */
  private Set<Table<E>> determineTableSetReferencedByColumn()
  {
    //    
    Set<Table<E>> retset = new IdentityArrayListBasedSet<Table<E>>();
    
    //
    for ( Column<E> column : this.selectionData.getColumnList() )
    {
      //
      StripeInternalData<E> stripeInternalData = SelectionExecutor.<E> determineStripeDataInternalFromStripe( column );
      if ( stripeInternalData != null && stripeInternalData.isValid() )
      {
        //
        TableInternal<E> tableInternal = stripeInternalData.getTableInternal();
        if ( tableInternal != null && !retset.contains( tableInternal ) )
        {
          retset.add( tableInternal.getUnderlyingTable() );
        }
      }
    }
    
    //
    return retset;
  }
  
  /**
   * 
   */
  private void determineAllColumnsFromTablesAndAttachThemToTheSelectionColumnList()
  {
    //
    List<Column<E>> columnList = this.selectionData.getColumnList();
    for ( Column<E> column : this.determineAllColumnsFromTables() )
    {
      if ( !columnList.contains( column ) )
      {
        columnList.add( column );
      }
    }
  }
  
  /**
   * Determines all {@link Column}s referenced by the {@link SelectionData#getTableToJoinMap()}
   * 
   * @return
   */
  private List<Column<E>> determineAllColumnsFromTables()
  {
    //
    List<Column<E>> retlist = new ArrayList<Column<E>>();
    
    //
    Set<Table<E>> tableSet = this.selectionData.getTableToJoinMap().keySet();
    
    //
    ElementConverter<Table<E>, TableInternal<E>> elementConverter = new ElementConverter<Table<E>, TableInternal<E>>()
    {
      @Override
      public TableInternal<E> convert( Table<E> table )
      {
        return TableInternalHelper.extractTableInternalFromTable( table );
      }
    };
    Collection<TableInternal<E>> tableInternalCollection = CollectionUtils.convertCollectionExcludingNullElements( tableSet,
                                                                                                                     elementConverter );
    for ( TableInternal<E> tableInternal : tableInternalCollection )
    {
      //
      Table<E> table = tableInternal.getUnderlyingTable();
      if ( table != null )
      {
        //
        for ( Column<E> column : table.getColumnList() )
        {
          if ( !retlist.contains( column ) )
          {
            retlist.add( column );
          }
        }
      }
    }
    
    //
    return retlist;
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
    for ( Column<E> column : this.selectionData.getColumnList() )
    {
      //
      StripeInternalData<E> stripeInternalData = SelectionExecutor.<E> determineStripeDataInternalFromStripe( column );
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
  protected static <E> StripeInternalData<E> determineStripeDataInternalFromStripe( Stripe<E> stripe )
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
  
}

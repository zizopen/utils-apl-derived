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
package org.omnaest.utils.structure.table.concrete.predicates.internal.joiner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.concrete.internal.selection.data.TableBlock;
import org.omnaest.utils.structure.table.concrete.internal.selection.scannable.ScannableStripeDataContainer;
import org.omnaest.utils.structure.table.concrete.predicates.internal.filter.PredicateFilter;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeInternal;

/**
 * @see PredicateJoiner
 * @see PredicateFilter
 * @author Omnaest
 */
public class EqualColumns<E> implements PredicateJoiner<E>, PredicateFilter<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long                        serialVersionUID             = -5393721215272944848L;
  
  /* ********************************************** Variables ********************************************** */
  protected Map<TableInternal<E>, List<Column<E>>> tableInternalToColumnListMap = new HashMap<TableInternal<E>, List<Column<E>>>();
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param columnList
   */
  public EqualColumns( List<Column<E>> columnList )
  {
    //
    super();
    
    //
    this.initTableInternalToColumnListMap( columnList );
  }
  
  /**
   * @param columnList
   */
  private void initTableInternalToColumnListMap( List<Column<E>> columnList )
  {
    if ( columnList != null )
    {
      for ( Column<E> column : columnList )
      {
        if ( column instanceof StripeInternal )
        {
          //
          StripeInternal<E> stripeInternal = (StripeInternal<E>) column;
          
          //
          TableInternal<E> tableInternal = stripeInternal.getTableInternal();
          if ( tableInternal != null )
          {
            //
            if ( !this.tableInternalToColumnListMap.containsKey( tableInternal ) )
            {
              this.tableInternalToColumnListMap.put( tableInternal, new ArrayList<Column<E>>() );
            }
            
            //
            List<Column<E>> columnListSeparated = this.tableInternalToColumnListMap.get( tableInternal );
            columnListSeparated.add( column );
          }
        }
      }
    }
  }
  
  @Override
  public Set<StripeData<E>> determineJoinableStripeDataSet( StripeData<E> stripeData,
                                                            TableBlock<E> tableBlockLeft,
                                                            TableBlock<E> tableBlockRight )
  {
    //
    Set<StripeData<E>> retset = new LinkedHashSet<StripeData<E>>();
    
    //
    if ( stripeData != null && tableBlockLeft != null && tableBlockRight != null
         && this.affectsBothTableBlocks( tableBlockLeft, tableBlockRight ) )
    {
      //
      List<E> cellElementList = this.determineCellElementListFor( stripeData, tableBlockLeft );
      if ( !cellElementList.isEmpty() )
      {
        //
        E elementReference = cellElementList.get( 0 );
        
        //
        Map<Column<E>, ScannableStripeDataContainer<E>> columnToStripeDataIndexMap = tableBlockRight.getColumnToScannableStripeDataContainerMap();
        List<Column<E>> columnListAffected = this.tableInternalToColumnListMap.get( tableBlockRight.getTableInternal() );
        if ( columnListAffected != null )
        {
          for ( Column<E> column : columnListAffected )
          {
            //
            ScannableStripeDataContainer<E> scannableStripeDataContainer = columnToStripeDataIndexMap.get( column );
            if ( scannableStripeDataContainer != null )
            {
              List<StripeData<E>> stripeDataList = scannableStripeDataContainer.determineStripeDataListContainingElement( elementReference );
              if ( stripeDataList != null )
              {
                retset.addAll( stripeDataList );
              }
            }
          }
        }
      }
    }
    
    //
    return retset;
  }
  
  /**
   * @param stripeData
   * @param tableBlock
   * @return
   */
  private List<E> determineCellElementListFor( StripeData<E> stripeData, TableBlock<E> tableBlock )
  {
    //
    List<E> retlist = new ArrayList<E>();
    
    //
    TableInternal<E> tableInternal = tableBlock.getTableInternal();
    List<Column<E>> columnList = this.tableInternalToColumnListMap.get( tableInternal );
    if ( columnList != null )
    {
      for ( Column<E> column : columnList )
      {
        if ( column instanceof StripeInternal )
        {
          //
          StripeInternal<E> stripeInternal = (StripeInternal<E>) column;
          
          //
          StripeData<E> stripeDataColumn = stripeInternal.getStripeData();
          
          //
          Cell<E> cell = tableBlock.getTableInternal().getCellAndStripeResolver().resolveCell( stripeData, stripeDataColumn );
          retlist.add( cell != null ? cell.getElement() : null );
        }
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * @param elementList
   * @return
   */
  private boolean determineHasCellElementListOnlyEqualElements( List<E> elementList )
  {
    //
    boolean retval = false;
    
    //
    if ( elementList != null && !elementList.isEmpty() )
    {
      //
      elementList = new ArrayList<E>( elementList );
      
      //
      E element = elementList.remove( 0 );
      if ( element != null )
      {
        //
        retval = true;
        for ( E elementCompare : elementList )
        {
          //
          retval = element.equals( elementCompare );
          if ( !retval )
          {
            break;
          }
        }
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public boolean affectsBothTableBlocks( TableBlock<E> tableBlockLeft, TableBlock<E> tableBlockRight )
  {
    //
    boolean containsAnyLeft = this.tableInternalToColumnListMap.containsKey( tableBlockLeft.getTableInternal() );
    boolean containsAnyRight = this.tableInternalToColumnListMap.containsKey( tableBlockRight.getTableInternal() );
    return containsAnyLeft && containsAnyRight;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Column<E>[] getRequiredColumns()
  {
    //
    Set<Column<E>> retset = new LinkedHashSet<Column<E>>();
    
    //
    retset.addAll( ListUtils.mergeAll( this.tableInternalToColumnListMap.values() ) );
    
    //
    return retset.toArray( new Column[0] );
  }
  
  @Override
  public void filterStripeDataSet( Collection<TableBlock<E>> tableBlockCollection )
  {
    //
    if ( tableBlockCollection != null && tableBlockCollection.size() == 1 )
    {
      //
      TableBlock<E> tableBlock = tableBlockCollection.iterator().next();
      
      //
      Set<StripeData<E>> remainingStripeDataSet = new HashSet<StripeData<E>>();
      
      //
      for ( StripeData<E> stripeData : tableBlock.getRowStripeDataSet() )
      {
        //
        List<E> cellElementList = this.determineCellElementListFor( stripeData, tableBlock );
        boolean hasCellElementListOnlyEqualElements = this.determineHasCellElementListOnlyEqualElements( cellElementList );
        if ( hasCellElementListOnlyEqualElements )
        {
          remainingStripeDataSet.add( stripeData );
        }
      }
      
      //
      tableBlock.getRowStripeDataSet().retainAll( remainingStripeDataSet );
    }
  }
  
}

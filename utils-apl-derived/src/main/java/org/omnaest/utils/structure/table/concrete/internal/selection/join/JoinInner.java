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
package org.omnaest.utils.structure.table.concrete.internal.selection.join;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.concrete.internal.helper.StripeDataHelper;
import org.omnaest.utils.structure.table.concrete.internal.selection.data.TableBlock;
import org.omnaest.utils.structure.table.internal.TableInternal.CellData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeDataList;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeInternal;

/**
 * @see Join
 * @author Omnaest
 * @param <E>
 */
public class JoinInner<E> implements Join<E>
{
  
  @Override
  public TableBlock<E> joinTableBlocks( TableBlock<E> tableBlockLeft,
                                        TableBlock<E> tableBlockRight,
                                        StripeDataList<E> stripeDataList )
  {
    //    
    TableBlock<E> retval = new TableBlock<E>();
    Set<StripeData<E>> rowStripeDataSetNew = retval.getRowStripeDataSet();
    
    //
    Set<CellData<E>> columnsCellDataSet = JoinInner.<E> determineColumnsCellDataSet( tableBlockLeft, tableBlockRight );
    
    //
    Set<StripeData<E>> rowStripeDataSetLeft = tableBlockLeft.getRowStripeDataSet();
    Set<StripeData<E>> rowStripeDataSetRight = tableBlockRight.getRowStripeDataSet();
    for ( StripeData<E> stripeDataLeft : rowStripeDataSetLeft )
    {
      for ( StripeData<E> stripeDataRight : rowStripeDataSetRight )
      {
        @SuppressWarnings("unchecked")
        StripeData<E> stripeDataMerged = StripeDataHelper.createNewStripeDataFromExisting( stripeDataList, columnsCellDataSet,
                                                                                           stripeDataLeft, stripeDataRight );
        
        //
        rowStripeDataSetNew.add( stripeDataMerged );
      }
    }
    
    // 
    return retval;
  }
  
  private static <E> Set<CellData<E>> determineColumnsCellDataSet( TableBlock<E> tableBlockLeft, TableBlock<E> tableBlockRight )
  {
    //
    Set<CellData<E>> retset = new HashSet<CellData<E>>();
    
    //
    List<Column<E>> columnListLeft = tableBlockLeft.getColumnList();
    Set<CellData<E>> columnsCellDataSetLeft = JoinInner.<E> determineColumnsCellDataSet( columnListLeft );
    
    //
    List<Column<E>> columnListRight = tableBlockRight.getColumnList();
    Set<CellData<E>> columnsCellDataSetRight = JoinInner.<E> determineColumnsCellDataSet( columnListRight );
    
    //
    retset.addAll( columnsCellDataSetLeft );
    retset.addAll( columnsCellDataSetRight );
    
    //
    return retset;
  }
  
  /**
   * Resolves a {@link Set} of all {@link CellData} instances which belongs to the declared {@link Column}s
   * 
   * @return
   */
  private static <E> Set<CellData<E>> determineColumnsCellDataSet( Collection<Column<E>> columnCollection )
  {
    //    
    Set<CellData<E>> retset = new HashSet<CellData<E>>();
    
    //
    for ( Column<E> column : columnCollection )
    {
      //
      if ( column != null )
      {
        //
        
        //
        StripeData<E> stripeData = determineStripeDataFromStripe( column );
        if ( stripeData != null )
        {
          //
          retset.addAll( stripeData.getCellDataSet() );
        }
      }
    }
    
    //
    return retset;
  }
  
  /**
   * @see StripeData
   * @param stripe
   * @return
   */
  protected static <E> StripeData<E> determineStripeDataFromStripe( Stripe<E> stripe )
  {
    //
    StripeData<E> retval = null;
    
    //
    if ( stripe instanceof StripeInternal )
    {
      //
      StripeInternal<E> stripeInternal = (StripeInternal<E>) stripe;
      
      //
      StripeData<E> stripeData = stripeInternal.getStripeData();
      
      //
      retval = stripeData;
    }
    
    //
    return retval;
  }
  
}

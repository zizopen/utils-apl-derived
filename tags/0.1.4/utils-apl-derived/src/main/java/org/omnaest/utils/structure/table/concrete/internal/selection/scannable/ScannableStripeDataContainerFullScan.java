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
package org.omnaest.utils.structure.table.concrete.internal.selection.scannable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.structure.collection.ListUtils;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.concrete.internal.helper.StripeDataHelper;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.CellData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;

/**
 * @see ScannableStripeDataContainer
 * @author Omnaest
 * @param <E>
 */
public class ScannableStripeDataContainerFullScan<E> implements ScannableStripeDataContainer<E>
{
  /* ********************************************** Variables ********************************************** */
  protected Map<E, List<StripeData<E>>> elementToStripeDataListMap = new HashMap<E, List<StripeData<E>>>();
  protected boolean                     isValid                    = false;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param elementToStripeDataListMap
   */
  public ScannableStripeDataContainerFullScan( TableInternal<E> tableInternal, Column<E> column )
  {
    //
    super();
    
    //
    this.isValid = this.createIndex( tableInternal, column );
  }
  
  /**
   * @param tableInternal
   * @param column
   * @return true: the index could be successfully created
   */
  protected boolean createIndex( TableInternal<E> tableInternal, Column<E> column )
  {
    //       
    boolean retval = false;
    
    //
    if ( tableInternal != null && column != null )
    {
      //
      StripeData<E> stripeData = StripeDataHelper.extractStripeDataFromStripe( column );
      if ( stripeData != null )
      {
        //
        try
        {
          //
          Set<CellData<E>> cellDataSet = stripeData.getCellDataSet();
          for ( CellData<E> cellData : cellDataSet )
          {
            if ( cellData != null )
            {
              //
              E element = cellData.getElement();
              
              //
              List<StripeData<E>> stripeDataList = tableInternal.getTableContent()
                                                                .getRowStripeDataList()
                                                                .findStripeDataListContaining( cellData );
              
              //
              this.elementToStripeDataListMap.put( element, stripeDataList );
            }
          }
          
          //
          retval = true;
        }
        catch ( Exception e )
        {
        }
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public List<StripeData<E>> determineStripeDataListForRange( final E fromKey, final E toKey )
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public boolean containsKey( E key )
  {
    return this.elementToStripeDataListMap.containsKey( key );
  }
  
  @Override
  public Set<E> keySet()
  {
    return this.elementToStripeDataListMap.keySet();
  }
  
  @Override
  public void clear()
  {
    this.elementToStripeDataListMap.clear();
  }
  
  @Override
  public int size()
  {
    return this.elementToStripeDataListMap.size();
  }
  
  @Override
  public List<StripeData<E>> values()
  {
    return ListUtils.mergeAll( this.elementToStripeDataListMap.values() );
  }
  
  @Override
  public boolean isValid()
  {
    return this.isValid;
  }
  
  @Override
  public List<StripeData<E>> determineStripeDataListContainingElement( E key )
  {
    return this.elementToStripeDataListMap.get( key );
  }
  
}

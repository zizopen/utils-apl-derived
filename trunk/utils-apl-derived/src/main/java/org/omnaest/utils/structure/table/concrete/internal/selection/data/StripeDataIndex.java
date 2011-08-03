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

import java.util.Collection;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import org.omnaest.utils.structure.collection.ListUtils;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.concrete.internal.helper.StripeDataHelper;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.CellData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;

/**
 * @author Omnaest
 * @param <E>
 */
public class StripeDataIndex<E>
{
  /* ********************************************** Variables ********************************************** */
  protected NavigableMap<E, List<StripeData<E>>> elementToStripeDataListMap = new TreeMap<E, List<StripeData<E>>>();
  protected boolean                              isValid                    = false;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param elementToStripeDataListMap
   */
  public StripeDataIndex( TableInternal<E> tableInternal, Column<E> column )
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
  
  /**
   * Returns a new {@link List} containing all {@link StripeData} instances containing elements which are between the fromKey and
   * the toKey. The upper and lower bound of {@link StripeData}s will be included in the result.
   * 
   * @param fromKey
   * @param toKey
   * @return
   */
  public List<StripeData<E>> determineStripeDataSetForRange( final E fromKey, final E toKey )
  {
    //
    final boolean fromInclusive = true;
    final boolean toInclusive = true;
    Collection<List<StripeData<E>>> stripeDataListCollection = this.elementToStripeDataListMap.subMap( fromKey, fromInclusive,
                                                                                                       toKey, toInclusive )
                                                                                              .values();
    return ListUtils.mergeAll( stripeDataListCollection );
  }
  
  /**
   * @param key
   * @return
   * @see java.util.Map#containsKey(java.lang.Object)
   */
  public boolean containsKey( E key )
  {
    return this.elementToStripeDataListMap.containsKey( key );
  }
  
  /**
   * @return
   * @see java.util.SortedMap#keySet()
   */
  public Set<E> keySet()
  {
    return this.elementToStripeDataListMap.keySet();
  }
  
  /**
   * @see java.util.Map#clear()
   */
  public void clear()
  {
    this.elementToStripeDataListMap.clear();
  }
  
  /**
   * @return
   * @see java.util.Map#size()
   */
  public int size()
  {
    return this.elementToStripeDataListMap.size();
  }
  
  /**
   * Returns a new {@link List} containg all {@link StripeData} instances within the {@link StripeDataIndex}
   * 
   * @return
   */
  public List<StripeData<E>> values()
  {
    return ListUtils.mergeAll( this.elementToStripeDataListMap.values() );
  }
  
  /**
   * @return the isValid
   */
  protected boolean isValid()
  {
    return this.isValid;
  }
  
  /**
   * @param key
   * @return
   * @see java.util.Map#get(java.lang.Object)
   */
  public List<StripeData<E>> get( E key )
  {
    return this.elementToStripeDataListMap.get( key );
  }
  
}

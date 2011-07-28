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
package org.omnaest.utils.structure.table.concrete.internal.helper;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.omnaest.utils.structure.collection.ListUtils;
import org.omnaest.utils.structure.collection.ListUtils.ElementTransformer;
import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.concrete.internal.StripeDataImpl;
import org.omnaest.utils.structure.table.internal.TableInternal.CellData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeDataList;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeInternal;

/**
 * Helper class for {@link StripeData}
 * 
 * @author Omnaest
 */
public class StripeDataHelper
{
  
  /**
   * Extracts the underlying {@link StripeData} from a given {@link Stripe}
   * 
   * @param stripe
   * @return
   */
  public static <E> StripeData<E> extractStripeDataFromStripe( Stripe<E> stripe )
  {
    //
    StripeData<E> retval = null;
    
    //
    if ( stripe instanceof StripeInternal )
    {
      //
      StripeInternal<E> stripeInternal = (StripeInternal<E>) stripe;
      
      //
      retval = stripeInternal.getStripeData();
    }
    
    //
    return retval;
  }
  
  /**
   * Creates a new {@link StripeData} instance which belongs to the given {@link StripeDataList} instance and contains the
   * {@link CellData} elements from the given old {@link StripeData}s
   * 
   * @param stripeDataList
   * @param stripeDataOld
   * @return
   */
  public static <E> StripeData<E> createNewStripeDataFromExisting( StripeDataList<E> stripeDataList,
                                                                   StripeData<E>... stripeDatasOld )
  {
    Set<CellData<E>> cellDataSet = null;
    return createNewStripeDataFromExisting( stripeDataList, cellDataSet, stripeDatasOld );
  }
  
  /**
   * Creates a new {@link StripeData} instance which belongs to the given {@link StripeDataList} instance and contains the
   * {@link CellData} elements from the given old {@link StripeData}s. The {@link CellData}s will be additionally filtered by the
   * given {@link Set} of {@link CellData}s, which means that only {@link CellData} instances will remain in the new created
   * {@link StripeData} if they are contained within the given {@link Set}.
   * 
   * @param stripeDataList
   * @param cellDataSetFilter
   * @param stripeDataOld
   * @return
   */
  public static <E> StripeData<E> createNewStripeDataFromExisting( StripeDataList<E> stripeDataList,
                                                                   Set<CellData<E>> cellDataSetFilter,
                                                                   StripeData<E>... stripeDatasOld )
  {
    //    
    StripeData<E> retval = null;
    
    //
    if ( stripeDataList != null )
    {
      //
      retval = new StripeDataImpl<E>( stripeDataList );
      
      //
      if ( stripeDatasOld != null && stripeDatasOld.length > 0 )
      {
        //
        for ( StripeData<E> stripeDataOld : stripeDatasOld )
        {
          //
          Set<CellData<E>> cellDataSet = stripeDataOld.getCellDataSet();
          
          //
          if ( cellDataSetFilter != null )
          {
            cellDataSet.retainAll( cellDataSetFilter );
          }
          
          //
          retval.registerCells( cellDataSet );
        }
        
        //
        if ( stripeDatasOld.length == 1 )
        {
          //
          retval.getTitleInternal().setValue( stripeDatasOld[0].getTitleInternal().getValue() );
        }
        else
        {
          //
          ElementTransformer<StripeData<E>, Object> elementTransformer = new ElementTransformer<StripeData<E>, Object>()
          {
            @Override
            public Object transformElement( StripeData<E> stripeData )
            {
              // 
              return stripeData.getTitleInternal().getValue();
            }
          };
          List<Object> titleValueList = ListUtils.transform( Arrays.asList( stripeDatasOld ), elementTransformer );
          retval.getTitleInternal().setValue( titleValueList );
        }
      }
    }
    
    //
    return retval;
  }
  
}

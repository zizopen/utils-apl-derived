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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mockito;
import org.omnaest.utils.structure.table.concrete.internal.StripeDataImpl;
import org.omnaest.utils.structure.table.concrete.internal.helper.StripeDataHelper;
import org.omnaest.utils.structure.table.internal.TableInternal.CellData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeDataList;

/**
 * @see StripeDataHelper
 * @author Omnaest
 */
public class StripeDataHelperTest
{
  
  @SuppressWarnings("unchecked")
  @Test
  public void testCreateNewStripeDataFromExistingStripeDataListOfESetOfCellDataOfEStripeDataOfEArray()
  {
    //
    Set<CellData<Object>> cellDataSet1 = new HashSet<CellData<Object>>();
    cellDataSet1.add( Mockito.mock( CellData.class ) );
    cellDataSet1.add( Mockito.mock( CellData.class ) );
    
    Set<CellData<Object>> cellDataSet2 = new HashSet<CellData<Object>>();
    cellDataSet2.add( Mockito.mock( CellData.class ) );
    cellDataSet2.add( Mockito.mock( CellData.class ) );
    
    //
    StripeDataList<Object> stripeDataList = Mockito.mock( StripeDataList.class );
    
    //
    Set<CellData<Object>> cellDataSetFilter = new HashSet<CellData<Object>>();
    cellDataSetFilter.add( cellDataSet1.iterator().next() );
    cellDataSetFilter.add( cellDataSet2.iterator().next() );
    
    //
    StripeData<Object> stripeData1 = new StripeDataImpl<Object>( stripeDataList );
    stripeData1.registerCells( cellDataSet1 );
    StripeData<Object> stripeData2 = new StripeDataImpl<Object>( stripeDataList );
    stripeData2.registerCells( cellDataSet2 );
    StripeData<Object>[] stripeDatasOld = new StripeData[] { stripeData1, stripeData2 };
    
    StripeData<Object> newStripeDataFromExisting = StripeDataHelper.createNewStripeDataFromExisting( stripeDataList,
                                                                                                     cellDataSetFilter,
                                                                                                     stripeDatasOld );
    assertNotNull( newStripeDataFromExisting );
    
    //
    Set<CellData<Object>> cellDataSetFromNewStripeData = newStripeDataFromExisting.getCellDataSet();
    assertEquals( cellDataSetFilter, cellDataSetFromNewStripeData );
  }
}

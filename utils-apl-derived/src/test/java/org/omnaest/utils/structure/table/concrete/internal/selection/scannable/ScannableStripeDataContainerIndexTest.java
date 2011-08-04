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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.TableFiller;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.concrete.internal.helper.TableInternalHelper;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;

/**
 * @see ScannableStripeDataContainerIndex
 * @author Omnaest
 */
public class ScannableStripeDataContainerIndexTest
{
  /* ********************************************** Variables ********************************************** */
  protected Table<String>                        table                             = null;
  protected ScannableStripeDataContainer<String> scannableStripeDataContainerIndex = null;
  protected TableInternal<String>                tableInternal                     = null;
  
  /* ********************************************** Methods ********************************************** */
  @Before
  public void setUp()
  {
    //
    this.table = new ArrayTable<String>();
    
    //
    int rows = 10;
    int columns = 3;
    String tableName = "Table1";
    TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, this.table );
    
    //
    this.tableInternal = TableInternalHelper.extractTableInternalFromTable( this.table );
  }
  
  @Test
    public void testDetermineStripeDataListForRange()
    {
      //
      this.scannableStripeDataContainerIndex = new ScannableStripeDataContainerIndex<String>( this.tableInternal,
                                                                                              this.table.getColumn( 1 ) );
      
      //
      assertEquals( this.table.getTableSize().getRowSize(), this.scannableStripeDataContainerIndex.size() );
      assertEquals( this.table.getColumn( 1 ).getCellElementList(),
                    new ArrayList<String>( this.scannableStripeDataContainerIndex.keySet() ) );
      
      //
      List<StripeData<String>> stripeDataList = this.scannableStripeDataContainerIndex.determineStripeDataListForRange( "1:1", "3:1" );
      assertEquals( 3, stripeDataList.size() );
    }
  
  @Test
  public void testContainsKey()
  {
    //
    this.scannableStripeDataContainerIndex = new ScannableStripeDataContainerIndex<String>( this.tableInternal,
                                                                                            this.table.getColumn( 1 ) );
    
    //
    assertTrue( this.scannableStripeDataContainerIndex.isValid() );
    assertTrue( this.scannableStripeDataContainerIndex.containsKey( "5:1" ) );
    assertFalse( this.scannableStripeDataContainerIndex.containsKey( "5:0" ) );
  }
  
}

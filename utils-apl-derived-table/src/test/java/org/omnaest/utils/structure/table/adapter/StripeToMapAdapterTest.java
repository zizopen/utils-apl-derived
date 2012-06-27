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
package org.omnaest.utils.structure.table.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.TableFiller;
import org.omnaest.utils.structure.table.concrete.ArrayTable;

/**
 * @see StripeToMapAdapter
 * @author Omnaest
 */
public class StripeToMapAdapterTest
{
  /* ********************************************** Variables ********************************************** */
  protected Table<String>       table     = new ArrayTable<String>();
  protected Map<Object, String> rowMap    = null;
  protected Map<Object, String> columnMap = null;
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    //
    int rows = 2;
    int columns = 4;
    String tableName = "Table1";
    TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, this.table );
    
    //
    Row<String> row = this.table.getRow( 0 );
    this.rowMap = new StripeToMapAdapter<String>( row );
    
    //
    Column<String> column = this.table.getColumn( 0 );
    this.columnMap = new StripeToMapAdapter<String>( column );
  }
  
  @Test
  public void testGet()
  {
    //
    assertEquals( "0:0", this.rowMap.get( "c0" ) );
    assertEquals( "0:1", this.rowMap.get( "c1" ) );
    
    //
    assertNull( this.rowMap.get( "c" ) );
  }
  
  @Test
  public void testPut()
  {
    //
    this.rowMap.put( "c1", "lala" );
    
    //
    assertEquals( "0:0", this.rowMap.get( "c0" ) );
    assertEquals( "lala", this.rowMap.get( "c1" ) );
  }
  
  @Test
  public void testRemove()
  {
    //
    this.rowMap.remove( "c2" );
    
    //
    assertEquals( "0:0", this.rowMap.get( "c0" ) );
    assertEquals( "0:1", this.rowMap.get( "c1" ) );
    assertEquals( null, this.rowMap.get( "c2" ) );
    assertEquals( "0:3", this.rowMap.get( "c3" ) );
    
  }
  
  @Test
  public void testKeySet()
  {
    assertEquals( Arrays.asList( "c0", "c1", "c2", "c3" ), new ArrayList<Object>( this.rowMap.keySet() ) );
    assertEquals( Arrays.asList( "r0", "r1" ), new ArrayList<Object>( this.columnMap.keySet() ) );
  }
  
  @Test
  public void testValues()
  {
    assertEquals( Arrays.asList( "0:0", "0:1", "0:2", "0:3" ), new ArrayList<Object>( this.rowMap.values() ) );
    assertEquals( Arrays.asList( "0:0", "1:0" ), new ArrayList<Object>( this.columnMap.values() ) );
  }
  
}

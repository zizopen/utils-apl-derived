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
package org.omnaest.utils.structure.table.view.concrete;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.TableFiller;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.view.TableView;

/**
 * @see TableViewImpl
 * @author Omnaest
 */
public class TableViewImplTest
{
  /* ********************************************** Variables ********************************************** */
  protected Table<String> table = new ArrayTable<String>();
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    //      
    int rows = 10;
    int columns = 5;
    String tableName = "Table1";
    TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, this.table );
  }
  
  @Test
  public void testEqualsObject()
  {
    assertEquals( this.table, this.table.select().asTableView() );
  }
  
  @Test
  public void testRefresh()
  {
    //
    TableView<String> tableView = this.table.select().asTableView();
    tableView.refresh();
    assertEquals( this.table, tableView );
    
    //
    tableView.refresh();
    assertEquals( this.table, tableView );
    
    //
    this.table.truncateRows( 2 );
    assertFalse( this.table.equals( tableView ) );
    
    //
    tableView.refresh();
    assertEquals( this.table, tableView );
    
  }
  
}

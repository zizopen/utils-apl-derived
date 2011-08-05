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
package org.omnaest.utils.structure.table.concrete.predicates.internal.filter;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.TableFiller;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.concrete.predicates.PredicateFactory;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Predicate;

/**
 * @see ColumnValueEquals
 * @author Omnaest
 */
public class ColumnValueEqualsTest
{
  /* ********************************************** Variables ********************************************** */
  protected Table<String> table1 = new ArrayTable<String>();
  protected Table<String> table2 = new ArrayTable<String>();
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    //
    {
      //
      int rows = 10;
      int columns = 2;
      String tableName = "Table1";
      TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, this.table1 );
    }
    
    //
    {
      //
      int rows = 5;
      int columns = 3;
      String tableName = "Table2";
      TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, this.table2 );
    }
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testFilterStripeDataSet()
  {
    //
    Predicate<String> predicate = PredicateFactory.columnValueEquals( this.table1.getColumn( 0 ), "5:0" );
    Table<String> tableResult = this.table1.select().allColumns().where( predicate ).asTable();
    
    //System.out.println( tableResult );
    
    //
    assertEquals( 1, tableResult.getTableSize().getRowSize() );
    assertEquals( Arrays.asList( "5:0", "5:1" ), tableResult.getRow( 0 ).getCellElementList() );
    
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testFilterStripeDataSetMultipleColumns()
  {
    //
    Predicate<String> predicate = PredicateFactory.columnValueEquals( "3:0", this.table1.getColumn( 0 ),
                                                                      this.table2.getColumn( 0 ) );
    Table<String> tableResult = this.table1.select().allColumns().from( this.table2 ).where( predicate ).asTable();
    
    //
    //System.out.println( tableResult );
    
    //
    assertEquals( 1, tableResult.getTableSize().getRowSize() );
    assertEquals( Arrays.asList( "3:0", "3:1", "3:0", "3:1", "3:2" ), tableResult.getRow( 0 ).getCellElementList() );
    
  }
  
}

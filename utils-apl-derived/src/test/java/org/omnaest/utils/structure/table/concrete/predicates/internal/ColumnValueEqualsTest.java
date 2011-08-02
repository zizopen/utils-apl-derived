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
package org.omnaest.utils.structure.table.concrete.predicates.internal;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.TableFiller;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Predicate;

/**
 * @see ColumnValueEquals
 * @author Omnaest
 */
public class ColumnValueEqualsTest
{
  /* ********************************************** Variables ********************************************** */
  protected Table<String> table = new ArrayTable<String>();
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    //
    int rows = 10;
    int columns = 2;
    String tableName = "Table1";
    TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, this.table );
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testFilterStripeDataSet()
  {
    Predicate<String> predicate = new ColumnValueEquals<String>( this.table.getColumn( 0 ), "1:0" );
    Table<String> tableResult = this.table.select().allColumns().where( predicate ).asTable();
    
    System.out.println( tableResult );
    
    //FIXME go on here
  }
  
}

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
package org.omnaest.utils.structure.table.concrete.internal.selection;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.TableFiller;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Selection.Order;

import com.sun.rowset.internal.Row;

/**
 * @see SelectionExecutor
 * @author Omnaest
 */
public class SelectionExecutorTest
{
  /* ********************************************** Variables ********************************************** */
  protected Table<String> table  = new ArrayTable<String>();
  protected Table<String> table2 = new ArrayTable<String>();
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testAsTableSelectAllColumnsFromOneTable()
  {
    //
    int numberOfRows = 4;
    int numberOfColumns = 3;
    this.fillTable( numberOfRows, numberOfColumns, this.table );
    
    //
    Table<String> tableResult = this.table.select().allColumns().asTable();
    
    //
    //System.out.println( tableResult );
    assertEquals( this.table, tableResult );
  }
  
  @Test
  public void testAsTableSelectAllColumnsFromTwoTables()
  {
    //
    {
      //
      int numberOfRows = 2;
      int numberOfColumns = 2;
      this.fillTable( numberOfRows, numberOfColumns, this.table, "t1" );
    }
    
    //
    {
      //
      int numberOfRows = 2;
      int numberOfColumns = 2;
      this.fillTable( numberOfRows, numberOfColumns, this.table2, "t2" );
    }
    
    //
    @SuppressWarnings("unchecked")
    Table<String> tableResult = this.table.select().allColumns().from( this.table2 ).asTable();
    
    //
    //System.out.println( tableResult );
    assertEquals( Arrays.asList( "0:0", "0:1", "0:0", "0:1" ), tableResult.getRow( 0 ).getCellElementList() );
    assertEquals( Arrays.asList( "0:0", "0:1", "1:0", "1:1" ), tableResult.getRow( 1 ).getCellElementList() );
    assertEquals( Arrays.asList( "1:0", "1:1", "0:0", "0:1" ), tableResult.getRow( 2 ).getCellElementList() );
    assertEquals( Arrays.asList( "1:0", "1:1", "1:0", "1:1" ), tableResult.getRow( 3 ).getCellElementList() );
  }
  
  /**
   * @see #fillTable(int, int, Table, String)
   * @param numberOfRows
   * @param numberOfColumns
   * @param table
   */
  protected void fillTable( int numberOfRows, int numberOfColumns, Table<String> table )
  {
    String title = "";
    this.fillTable( numberOfRows, numberOfColumns, table, title );
  }
  
  /**
   * Fills the {@link Table} with the given number of {@link Row}s and {@link Column}s
   * 
   * @param numberOfRows
   * @param numberOfColumns
   * @param table
   */
  protected void fillTable( int numberOfRows, int numberOfColumns, Table<String> table, String title )
  {
    //
    for ( int rowIndexPosition = 0; rowIndexPosition < numberOfRows; rowIndexPosition++ )
    {
      for ( int columnIndexPosition = 0; columnIndexPosition < numberOfColumns; columnIndexPosition++ )
      {
        //
        String element = rowIndexPosition + ":" + columnIndexPosition;
        table.setCellElement( rowIndexPosition, columnIndexPosition, element );
        
        //
        String titleValue = title + "c" + columnIndexPosition;
        table.setColumnTitleValue( titleValue, columnIndexPosition );
      }
      
      //
      String titleValue = title + "r" + rowIndexPosition;
      table.setRowTitleValue( titleValue, rowIndexPosition );
    }
    
    //
    table.setTableName( title );
  }
  
  @Test
  public void testProcessOrders1()
  {
    //
    int rows = 10;
    int columns = 3;
    String tableName = "Table1";
    TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, this.table );
    
    //
    Table<String> tableResult = this.table.select().orderBy( this.table.getColumn( 0 ), Order.DESCENDING ).asTable();
    
    //
    //System.out.println( tableResult );
    
    //
    assertEquals( 10, tableResult.getTableSize().getRowSize() );
    assertEquals( Arrays.asList( "9:0", "9:1", "9:2" ), tableResult.getRow( 0 ).getCellElementList() );
    assertEquals( Arrays.asList( "8:0", "8:1", "8:2" ), tableResult.getRow( 1 ).getCellElementList() );
    assertEquals( Arrays.asList( "1:0", "1:1", "1:2" ), tableResult.getRow( 8 ).getCellElementList() );
    assertEquals( Arrays.asList( "0:0", "0:1", "0:2" ), tableResult.getRow( 9 ).getCellElementList() );
    
  }
  
  @Test
  public void testTruncateToTopNumberOfRows()
  {
    //
    int rows = 10;
    int columns = 3;
    String tableName = "Table1";
    TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, this.table );
    
    //
    Table<String> tableResult = this.table.select().top( 3 ).asTable();
    
    //
    //System.out.println( tableResult );
    
    //
    assertEquals( 3, tableResult.getTableSize().getRowSize() );
  }
  
}
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
package org.omnaest.utils.structure.table.concrete;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.TableFiller;

/**
 * @see TableAbstract
 * @author Omnaest
 */
public class TableAbstractTest
{
  /* ********************************************** Variables ********************************************** */
  protected Table<Object> table = new ArrayTable<Object>();
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testPutTable()
  {
    //
    Table<Object> insertTable = new ArrayTable<Object>();
    
    //
    final int rowsInsert = 4;
    final int columnsInsert = 15;
    TableFiller.fillTableWithMatrixNumbers( rowsInsert, columnsInsert, insertTable );
    
    //
    final int rows = 20;
    final int columns = 10;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    final int rowIndexPosition = 3;
    final int columnIndexPosition = 2;
    this.table.putTable( insertTable, rowIndexPosition, columnIndexPosition );
    
    //
    assertEquals( rows, this.table.getTableSize().getRowSize() );
    assertEquals( columnsInsert + columnIndexPosition, this.table.getTableSize().getColumnSize() );
    
    assertEquals( "0:0", this.table.getCellElement( 0, 0 ) );
    assertEquals( "0:0", this.table.getCellElement( rowIndexPosition, columnIndexPosition ) );
    assertEquals( ( rowsInsert - 1 ) + ":" + ( columnsInsert - 1 ),
                  this.table.getCellElement( rowIndexPosition + rowsInsert - 1, columnIndexPosition + columnsInsert - 1 ) );
    
  }
  
  @Test
  public void testSetRowTitleValues()
  {
    this.table.setRowTitleValues( "row1", "row2" );
    assertEquals( Arrays.asList( "row1", "row2" ), this.table.getRowTitleValueList() );
  }
  
  @Test
  public void testSetColumnTitleValues()
  {
    this.table.setColumnTitleValues( "column1", "column2" );
    assertEquals( Arrays.asList( "column1", "column2" ), this.table.getColumnTitleValueList() );
  }
  
}

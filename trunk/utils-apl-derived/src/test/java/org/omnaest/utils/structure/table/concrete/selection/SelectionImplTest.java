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
package org.omnaest.utils.structure.table.concrete.selection;

import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.TableSelectable.Result;
import org.omnaest.utils.structure.table.concrete.ArrayTable;

import com.sun.rowset.internal.Row;

/**
 * @see SelectionImpl
 * @author Omnaest
 */
public class SelectionImplTest
{
  /* ********************************************** Variables ********************************************** */
  protected Table<String> table = new ArrayTable<String>();
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testAsView()
  {
    Result<String> result = this.table.select().allColumns().asView();
  }
  
  @Test
  public void testAsTable()
  {
    //
    int numberOfRows = 4;
    int numberOfColumns = 3;
    this.fillTable( numberOfRows, numberOfColumns );
    
    //
    Table<String> tableResult = this.table.select().allColumns().asTable();
    
    //FIXME go on here
    
    //
    System.out.println( tableResult.toString() );
    
  }
  
  /**
   * Fills the {@link Table} with the given number of {@link Row}s and {@link Column}s
   * 
   * @param numberOfRows
   * @param numberOfColumns
   */
  protected void fillTable( int numberOfRows, int numberOfColumns )
  {
    //
    for ( int rowIndexPosition = 0; rowIndexPosition < numberOfRows; rowIndexPosition++ )
    {
      for ( int columnIndexPosition = 0; columnIndexPosition < numberOfColumns; columnIndexPosition++ )
      {
        //
        String element = rowIndexPosition + ":" + columnIndexPosition;
        this.table.setCellElement( rowIndexPosition, columnIndexPosition, element );
        
        //
        String titleValue = "c" + columnIndexPosition;
        this.table.setColumnTitleValue( titleValue, columnIndexPosition );
      }
      
      //
      String titleValue = "r" + rowIndexPosition;
      this.table.setRowTitleValue( titleValue, rowIndexPosition );
    }
    
    //
    
  }
  
}

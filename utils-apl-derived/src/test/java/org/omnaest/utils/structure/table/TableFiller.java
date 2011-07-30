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
package org.omnaest.utils.structure.table;

import org.omnaest.utils.structure.table.Table.Column;

import com.sun.rowset.internal.Row;

/**
 * Helper class which offers methods to fill {@link Table} instances with {@link Row}s and {@link Column}s
 * 
 * @author Omnaest
 */
public class TableFiller
{
  /**
   * @see #fillTableWithMatrixNumbers(int, int, String, Table)
   * @param rows
   * @param columns
   * @param table
   */
  @SuppressWarnings("rawtypes")
  public static void fillTableWithMatrixNumbers( int rows, int columns, Table table )
  {
    String tableName = null;
    fillTableWithMatrixNumbers( rows, columns, tableName, table );
  }
  
  /**
   * Fills the given {@link Table} with <br>
   * ["0:0","0:1",...,"0:n"]<br>
   * ["1:0","1:1",...,"1:n"]<br>
   * ...<br>
   * ["m:0","m:1",...,"m:n"]<br>
   * <br>
   * and adds titles to {@link Row}s and {@link Column}s like "r0",...,"rm" and "c0",...,"cn"
   * 
   * @see #fillTableWithMatrixNumbers(int, int, Table)
   * @param rows
   * @param columns
   * @param tableName
   * @param table
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void fillTableWithMatrixNumbers( int rows, int columns, String tableName, Table table )
  {
    //
    if ( table != null )
    {
      //
      for ( int rowIndexPosition = 0; rowIndexPosition < rows; rowIndexPosition++ )
      {
        //
        table.setRowTitleValue( "r" + rowIndexPosition, rowIndexPosition );
        
        //
        for ( int columnIndexPosition = 0; columnIndexPosition < columns; columnIndexPosition++ )
        {
          //
          table.setColumnTitleValue( "c" + columnIndexPosition, columnIndexPosition );
          
          //
          table.setCellElement( rowIndexPosition, columnIndexPosition, ( "" + rowIndexPosition + ":" + columnIndexPosition ) );
        }
      }
      
      //
      if ( tableName != null )
      {
        //
        table.setTableName( tableName );
      }
    }
  }
  
}

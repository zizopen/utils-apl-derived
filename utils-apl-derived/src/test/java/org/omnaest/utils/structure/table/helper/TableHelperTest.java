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
package org.omnaest.utils.structure.table.helper;

import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.TableCellVisitor;
import org.omnaest.utils.structure.table.concrete.ArrayTable;

/**
 * @see Table
 * @see TableHelper
 * @author Omnaest
 */
public class TableHelperTest
{
  /* ********************************************** Variables ********************************************** */
  protected Table<Object> table = new ArrayTable<Object>();
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testRenderToString()
  {
    //
    TableCellVisitor<Object> tableCellVisitor = new TableCellVisitor<Object>()
    {
      @Override
      public void process( int rowIndexPosition, int columnIndexPosition, Cell<Object> cell )
      {
        cell.setElement( rowIndexPosition + ":" + columnIndexPosition );
      }
    };
    this.table.ensureNumberOfColumns( 5 );
    this.table.ensureNumberOfRows( 5 );
    this.table.processTableCells( tableCellVisitor );
    this.table.setTableName( "Table name" );
    
    //
    String renderedTableString = TableHelper.renderToString( this.table );
    
    //
    System.out.println( renderedTableString );
    
  }
  
}

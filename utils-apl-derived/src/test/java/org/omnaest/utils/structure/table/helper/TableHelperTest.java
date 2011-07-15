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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
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
  protected Table<Object>            table            = new ArrayTable<Object>();
  protected TableCellVisitor<Object> tableCellVisitor = new TableCellVisitor<Object>()
                                                      {
                                                        @Override
                                                        public void process( int rowIndexPosition,
                                                                             int columnIndexPosition,
                                                                             Cell<Object> cell )
                                                        {
                                                          cell.setElement( rowIndexPosition + ":" + columnIndexPosition );
                                                        }
                                                      };
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testRenderToStringWithTableNameAndRowAndColumnTitle()
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
    
    //
    this.table.ensureNumberOfColumns( 5 );
    this.table.ensureNumberOfRows( 5 );
    for ( Row<Object> row : this.table.rows() )
    {
      row.getTitle().setValue( "r" + row.determineIndexPosition() );
    }
    for ( Column<Object> column : this.table.columns() )
    {
      column.getTitle().setValue( "c" + column.determineIndexPosition() );
    }
    
    this.table.processTableCells( tableCellVisitor );
    this.table.setTableName( "Table name" );
    
    //
    String renderedTableString = TableHelper.renderToString( this.table );
    
    //
    assertEquals( "=======Table name=======\n" + "!  !c0 !c1 !c2 !c3 !c4 !\n" + "!r0!0:0|0:1|0:2|0:3|0:4|\n"
                  + "!r1!1:0|1:1|1:2|1:3|1:4|\n" + "!r2!2:0|2:1|2:2|2:3|2:4|\n" + "!r3!3:0|3:1|3:2|3:3|3:4|\n"
                  + "!r4!4:0|4:1|4:2|4:3|4:4|\n" + "------------------------\n", renderedTableString );
    
  }
  
  @Test
  public void testRenderToStringWithRowAndColumnTitle()
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
    
    //
    this.table.ensureNumberOfColumns( 5 );
    this.table.ensureNumberOfRows( 5 );
    for ( Row<Object> row : this.table.rows() )
    {
      row.getTitle().setValue( "r" + row.determineIndexPosition() );
    }
    for ( Column<Object> column : this.table.columns() )
    {
      column.getTitle().setValue( "c" + column.determineIndexPosition() );
    }
    
    this.table.processTableCells( tableCellVisitor );
    
    //
    String renderedTableString = TableHelper.renderToString( this.table );
    
    //
    assertEquals( "------------------------\n" + "!  !c0 !c1 !c2 !c3 !c4 !\n" + "!r0!0:0|0:1|0:2|0:3|0:4|\n"
                  + "!r1!1:0|1:1|1:2|1:3|1:4|\n" + "!r2!2:0|2:1|2:2|2:3|2:4|\n" + "!r3!3:0|3:1|3:2|3:3|3:4|\n"
                  + "!r4!4:0|4:1|4:2|4:3|4:4|\n" + "------------------------\n", renderedTableString );
    
  }
  
  @Test
  public void testRenderToStringWithColumnTitle()
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
    
    //
    this.table.ensureNumberOfColumns( 5 );
    this.table.ensureNumberOfRows( 5 );
    
    for ( Column<Object> column : this.table.columns() )
    {
      column.getTitle().setValue( "c" + column.determineIndexPosition() );
    }
    
    this.table.processTableCells( tableCellVisitor );
    
    //
    String renderedTableString = TableHelper.renderToString( this.table );
    
    //
    assertEquals( "---------------------\n" + "!c0 !c1 !c2 !c3 !c4 !\n" + "|0:0|0:1|0:2|0:3|0:4|\n" + "|1:0|1:1|1:2|1:3|1:4|\n"
                      + "|2:0|2:1|2:2|2:3|2:4|\n" + "|3:0|3:1|3:2|3:3|3:4|\n" + "|4:0|4:1|4:2|4:3|4:4|\n"
                      + "---------------------\n", renderedTableString );
    
  }
  
  @Test
  public void testRenderToStringWithRowTitles()
  {
    //
    this.table.ensureNumberOfColumns( 5 );
    this.table.ensureNumberOfRows( 5 );
    for ( Row<Object> row : this.table.rows() )
    {
      row.getTitle().setValue( "r" + row.determineIndexPosition() );
    }
    
    this.table.processTableCells( this.tableCellVisitor );
    
    //
    String renderedTableString = TableHelper.renderToString( this.table );
    
    //
    assertEquals( "------------------------\n" + "!r0!0:0|0:1|0:2|0:3|0:4|\n" + "!r1!1:0|1:1|1:2|1:3|1:4|\n"
                  + "!r2!2:0|2:1|2:2|2:3|2:4|\n" + "!r3!3:0|3:1|3:2|3:3|3:4|\n" + "!r4!4:0|4:1|4:2|4:3|4:4|\n"
                  + "------------------------\n", renderedTableString );
    
  }
  
  @Test
  public void testRenderToStringWithoutTitles()
  {
    //
    this.table.ensureNumberOfColumns( 5 );
    this.table.ensureNumberOfRows( 5 );
    
    this.table.processTableCells( this.tableCellVisitor );
    
    //
    String renderedTableString = TableHelper.renderToString( this.table );
    
    //
    assertEquals( "---------------------\n" + "|0:0|0:1|0:2|0:3|0:4|\n" + "|1:0|1:1|1:2|1:3|1:4|\n" + "|2:0|2:1|2:2|2:3|2:4|\n"
                  + "|3:0|3:1|3:2|3:3|3:4|\n" + "|4:0|4:1|4:2|4:3|4:4|\n" + "---------------------\n", renderedTableString );
    
  }
  
  @Test
  public void testRenderToStringWithTableName()
  {
    //
    this.table.ensureNumberOfColumns( 5 );
    this.table.ensureNumberOfRows( 5 );
    
    this.table.processTableCells( this.tableCellVisitor );
    this.table.setTableName( "Table name" );
    
    //
    String renderedTableString = TableHelper.renderToString( this.table );
    
    //
    assertEquals( "=====Table name======\n" + "|0:0|0:1|0:2|0:3|0:4|\n" + "|1:0|1:1|1:2|1:3|1:4|\n" + "|2:0|2:1|2:2|2:3|2:4|\n"
                  + "|3:0|3:1|3:2|3:3|3:4|\n" + "|4:0|4:1|4:2|4:3|4:4|\n" + "---------------------\n", renderedTableString );
    
  }
  
}

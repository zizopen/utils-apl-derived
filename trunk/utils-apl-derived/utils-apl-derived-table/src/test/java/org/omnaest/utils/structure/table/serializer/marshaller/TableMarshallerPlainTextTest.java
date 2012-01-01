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
package org.omnaest.utils.structure.table.serializer.marshaller;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.TableFiller;
import org.omnaest.utils.structure.table.concrete.ArrayTable;

/**
 * @see TableMarshallerPlainText
 * @author Omnaest
 */
public class TableMarshallerPlainTextTest
{
  /* ********************************************** Variables ********************************************** */
  protected TableMarshallerPlainText<String> tableMarshallerPlainText = new TableMarshallerPlainText<String>();
  protected Table<String>                    table1                   = new ArrayTable<String>();
  protected Table<String>                    table2                   = new ArrayTable<String>();
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void prepareTable()
  {
    //
    {
      //
      int rows = 4;
      int columns = 2;
      TableFiller.fillTableWithMatrixNumbers( rows, columns, "Table1", this.table1 );
    }
    
    //
    {
      //
      int rows = 2;
      int columns = 4;
      TableFiller.fillTableWithMatrixNumbers( rows, columns, "Table2", this.table2 );
    }
  }
  
  @Test
  public void testMarshalTableOfEOutputStream()
  {
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    this.tableMarshallerPlainText.marshal( this.table1, byteArrayContainer.getOutputStream() );
    
    //
    //System.out.println( stringBuffer );
    
    //
    TableMarshallerPlainTextTest.assertTable1Content( new StringBuffer( byteArrayContainer.toString() ) );
  }
  
  @Test
  public void testMarshalTableOfEAppendable()
  {
    //
    StringBuffer stringBuffer = new StringBuffer();
    this.tableMarshallerPlainText.marshal( this.table2, stringBuffer );
    
    //
    //System.out.println( stringBuffer );
    
    //
    TableMarshallerPlainTextTest.assertTable2Content( stringBuffer );
  }
  
  /**
   * @param stringBuffer
   */
  private static void assertTable1Content( StringBuffer stringBuffer )
  {
    //
    final String estimatedTableContent = "===Table1===\n" + "!  !c0 !c1 !\n" + "!r0!0:0|0:1|\n" + "!r1!1:0|1:1|\n"
                                         + "!r2!2:0|2:1|\n" + "!r3!3:0|3:1|\n" + "------------\n";
    assertEquals( estimatedTableContent, stringBuffer.toString() );
  }
  
  /**
   * @param stringBuffer
   */
  private static void assertTable2Content( StringBuffer stringBuffer )
  {
    //   
    final String estimatedTableContent = "=======Table2=======\n" + "!  !c0 !c1 !c2 !c3 !\n" + "!r0!0:0|0:1|0:2|0:3|\n"
                                         + "!r1!1:0|1:1|1:2|1:3|\n" + "--------------------\n";
    assertEquals( estimatedTableContent, stringBuffer.toString() );
  }
  
}

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
  protected Table<String>                    table                    = new ArrayTable<String>();
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void prepareTable()
  {
    //
    int rows = 3;
    int columns = 2;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, "Table1", this.table );
  }
  
  @Test
  public void testMarshalTableOfEOutputStream()
  {
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    this.tableMarshallerPlainText.marshal( this.table, byteArrayContainer.getOutputStream() );
    
    //
    //System.out.println( stringBuffer );
    
    //
    TableMarshallerPlainTextTest.assertTableContent( new StringBuffer( byteArrayContainer.toString() ) );
  }
  
  @Test
  public void testMarshalTableOfEAppendable()
  {
    //
    StringBuffer stringBuffer = new StringBuffer();
    this.tableMarshallerPlainText.marshal( this.table, stringBuffer );
    
    //
    //System.out.println( stringBuffer );
    
    //
    TableMarshallerPlainTextTest.assertTableContent( stringBuffer );
  }
  
  /**
   * @param stringBuffer
   */
  private static void assertTableContent( StringBuffer stringBuffer )
  {
    //
    final String estimatedTableContent = "===Table1===\n" + "!  !c0 !c1 !\n" + "!r0!0:0|0:1|\n" + "!r1!1:0|1:1|\n"
                                         + "!r2!2:0|2:1|\n" + "------------\n";
    assertEquals( estimatedTableContent, stringBuffer.toString() );
  }
  
}

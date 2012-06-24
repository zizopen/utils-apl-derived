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
import org.omnaest.utils.structure.table.serializer.TableMarshaller;
import org.omnaest.utils.structure.table.serializer.common.CSVMarshallingConfiguration;

/**
 * @see TableMarshallerCSV
 * @author Omnaest
 */
public class TableMarshallerCSVTest
{
  /* ********************************************** Variables ********************************************** */
  protected Table<String>           table           = new ArrayTable<String>();
  protected TableMarshaller<String> tableMarshaller = new TableMarshallerCSV<String>().setConfiguration( new CSVMarshallingConfiguration().setHasEnabledTableName( true )
                                                                                                                                       .setHasEnabledColumnTitles( true )
                                                                                                                                       .setHasEnabledRowTitles( true ) );
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    //
    int rows = 3;
    int columns = 10;
    String tableName = "Table1";
    TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, this.table );
  }
  
  @Test
  public void testMarshalTableOfEOutputStream()
  {
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    this.tableMarshaller.marshal( this.table, byteArrayContainer.getOutputStream() );
    
    //
    //System.out.println( stringBuffer );
    
    //
    String estimatedTableContent = "Table1\n;c0;c1;c2;c3;c4;c5;c6;c7;c8;c9\nr0;0:0;0:1;0:2;0:3;0:4;0:5;0:6;0:7;0:8;0:9\nr1;1:0;1:1;1:2;1:3;1:4;1:5;1:6;1:7;1:8;1:9\nr2;2:0;2:1;2:2;2:3;2:4;2:5;2:6;2:7;2:8;2:9\n";
    assertEquals( estimatedTableContent, byteArrayContainer.toString() );
  }
  
  @Test
  public void testMarshalTableOfEAppendable()
  {
    //
    StringBuffer stringBuffer = new StringBuffer();
    this.tableMarshaller.marshal( this.table, stringBuffer );
    
    //
    //System.out.println( stringBuffer );
    
    //
    String estimatedTableContent = "Table1\n;c0;c1;c2;c3;c4;c5;c6;c7;c8;c9\nr0;0:0;0:1;0:2;0:3;0:4;0:5;0:6;0:7;0:8;0:9\nr1;1:0;1:1;1:2;1:3;1:4;1:5;1:6;1:7;1:8;1:9\nr2;2:0;2:1;2:2;2:3;2:4;2:5;2:6;2:7;2:8;2:9\n";
    assertEquals( estimatedTableContent, stringBuffer.toString() );
  }
}

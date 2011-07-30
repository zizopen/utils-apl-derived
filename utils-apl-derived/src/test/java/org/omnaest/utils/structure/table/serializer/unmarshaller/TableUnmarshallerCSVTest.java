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
package org.omnaest.utils.structure.table.serializer.unmarshaller;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.TableFiller;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.serializer.TableUnmarshaller;
import org.omnaest.utils.structure.table.serializer.marshaller.TableMarshallerCSV;

/**
 * @see TableUnmarshallerCSV
 * @author Omnaest
 */
public class TableUnmarshallerCSVTest
{
  /* ********************************************** Variables ********************************************** */
  protected Table<Object>             tableAfter        = new ArrayTable<Object>();
  protected Table<Object>             tableBefore       = new ArrayTable<Object>();
  protected TableUnmarshaller<Object> tableUnmarshaller = new TableUnmarshallerCSV<Object>();
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testUnmarshalTableOfEInputStream()
  {
    //
    StringBuffer csvContent = this.generateCSVContent();
    
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer( csvContent );
    
    //
    this.tableUnmarshaller.unmarshal( this.tableAfter, byteArrayContainer.getInputStream() );
    
    //
    assertEquals( this.tableBefore, this.tableAfter );
  }
  
  @Test
  public void testUnmarshalTableOfECharSequence()
  {
    //
    StringBuffer csvContent = this.generateCSVContent();
    
    //
    this.tableUnmarshaller.unmarshal( this.tableAfter, csvContent );
    
    //
    assertEquals( this.tableBefore, this.tableAfter );
  }
  
  /**
   * @return
   */
  protected StringBuffer generateCSVContent()
  {
    //
    StringBuffer retval = new StringBuffer();
    
    //
    Table<Object> table = this.tableBefore;
    
    //
    int rows = 3;
    int columns = 10;
    String tableName = "Table1";
    TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, table );
    
    //
    new TableMarshallerCSV<Object>().marshal( table, retval );
    
    //
    return retval;
  }
}

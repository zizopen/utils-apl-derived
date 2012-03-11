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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.TableFiller;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.serializer.TableMarshaller;
import org.omnaest.utils.structure.table.serializer.TableUnmarshaller;
import org.omnaest.utils.structure.table.serializer.marshaller.TableMarshallerCSV;

/**
 * @see TableUnmarshallerCSV
 * @author Omnaest
 */
@RunWith(value = Parameterized.class)
public class TableUnmarshallerCSVTest
{
  @Parameters
  public static Collection<Object[]> configurationDataCollection()
  {
    //
    List<Object[]> retlist = new ArrayList<Object[]>();
    retlist.add( new Object[] { true, true, true } );
    retlist.add( new Object[] { true, true, false } );
    retlist.add( new Object[] { true, false, true } );
    retlist.add( new Object[] { true, false, false } );
    retlist.add( new Object[] { false, true, true } );
    retlist.add( new Object[] { false, true, false } );
    retlist.add( new Object[] { false, false, true } );
    retlist.add( new Object[] { false, false, false } );
    
    //
    return retlist;
  }
  
  /**
   * @param hasTableName
   * @param hasColumnTitles
   * @param hasRowTitles
   * @param tableUnmarshaller
   */
  public TableUnmarshallerCSVTest( boolean hasTableName, boolean hasColumnTitles, boolean hasRowTitles )
  {
    //
    super();
    
    //
    this.tableUnmarshaller = new TableUnmarshallerCSV<Object>( hasTableName, hasColumnTitles, hasRowTitles );
    this.tableMarshaller = new TableMarshallerCSV<Object>( hasTableName, hasColumnTitles, hasRowTitles );
    
    //
    this.csvContent = this.generateCSVContent( hasTableName, hasColumnTitles, hasRowTitles );
  }
  
  /* ********************************************** Variables ********************************************** */
  protected Table<Object>             tableAfter        = new ArrayTable<Object>();
  protected Table<Object>             tableBefore       = new ArrayTable<Object>();
  
  protected TableUnmarshaller<Object> tableUnmarshaller = null;
  protected TableMarshaller<Object>   tableMarshaller   = null;
  
  protected StringBuffer              csvContent        = null;
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testUnmarshalTableOfEInputStream()
  {
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer( this.csvContent );
    
    //
    this.tableUnmarshaller.unmarshal( this.tableAfter, byteArrayContainer.getInputStream() );
    
    //
    assertEquals( this.tableBefore, this.tableAfter );
  }
  
  @Test
  public void testUnmarshalTableOfECharSequence()
  {
    //
    this.tableUnmarshaller.unmarshal( this.tableAfter, this.csvContent );
    
    //
    assertEquals( this.tableBefore, this.tableAfter );
  }
  
  /**
   * @param hasTableName
   * @param hasColumnTitles
   * @param hasRowTitles
   * @return
   */
  protected StringBuffer generateCSVContent( boolean hasTableName, boolean hasColumnTitles, boolean hasRowTitles )
  {
    //
    StringBuffer retval = new StringBuffer();
    
    //
    Table<Object> table = this.tableBefore;
    
    //
    int rows = 3;
    int columns = 10;
    String tableName = "Table1";
    TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, hasTableName, hasColumnTitles, hasRowTitles, table );
    
    //
    this.tableMarshaller.marshal( table, retval );
    
    //
    return retval;
  }
}

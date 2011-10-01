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

import java.util.Arrays;

import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.serializer.TableUnmarshaller;

/**
 * @see TableUnmarshallerHTML
 * @author Omnaest
 */
public class TableUnmarshallerHTMLTest
{
  /* ********************************************** Variables ********************************************** */
  private Table<String>             table             = new ArrayTable<String>();
  private TableUnmarshaller<String> tableUnmarshaller = new TableUnmarshallerHTML<String>();
  
  /* ********************************************** Methods ********************************************** */
  @Test
  public void testUnmarshal()
  {
    //<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\">
    CharSequence charSequence = "<?xml version=\"1.0\" ?>"
                                + "<table> <thead> <tr>  <th>Berlin</th>    <th>Hamburg</th>    <th>München</th>  </tr> </thead><tbody> <tr>    <td>Miloh</td>    <td>Kiez</td>    <td>Bierdampf</td>  </tr>  <tr>    <td>Buletten</td>    <td>Frikadellen</td>    <td>Fleischpflanzerl</td>  </tr></tbody></table>";
    this.tableUnmarshaller.unmarshal( this.table, charSequence );
    
    //
    System.out.println( this.table );
    assertEquals( Arrays.asList( "Berlin", "Hamburg", "München" ), this.table.getColumnTitleValueList() );
    assertEquals( Arrays.asList( "Miloh", "Kiez", "Bierdampf" ), this.table.getRow( 0 ).getCellElementList() );
    assertEquals( Arrays.asList( "Buletten", "Frikadellen", "Fleischpflanzerl" ), this.table.getRow( 1 ).getCellElementList() );
    
  }
}

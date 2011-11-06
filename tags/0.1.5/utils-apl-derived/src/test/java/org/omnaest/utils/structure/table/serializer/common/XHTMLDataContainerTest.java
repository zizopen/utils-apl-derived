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
package org.omnaest.utils.structure.table.serializer.common;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.omnaest.utils.structure.table.serializer.common.XHTMLDataContainer.Anker;
import org.omnaest.utils.structure.table.serializer.common.XHTMLDataContainer.Cell;
import org.omnaest.utils.structure.table.serializer.common.XHTMLDataContainer.Row;
import org.omnaest.utils.xml.XMLHelper;

public class XHTMLDataContainerTest
{
  
  @Test
  public void testJAXB()
  {
    XHTMLDataContainer<String> xhtmlDataContainer = new XHTMLDataContainer<String>();
    List<Row> rowList = xhtmlDataContainer.getBody().getRowList();
    {
      //
      {
        //
        Row row = new Row();
        {
          List<Cell> cellList = row.getCellList();
          {
            //
            Cell cell = new Cell();
            Anker anker = new Anker();
            anker.getTextList().add( "cell1" );
            cell.getAnkerList().add( anker );
            cellList.add( cell );
          }
          {
            //
            Cell cell = new Cell();
            cell.getTextList().add( "cell2" );
            cellList.add( cell );
          }
        }
        rowList.add( row );
      }
      {
        //
        Row row = new Row();
        {
          List<Cell> cellList = row.getCellList();
          {
            //
            Cell cell = new Cell();
            cell.getTextList().add( "cell1" );
            cellList.add( cell );
          }
          {
            //
            Cell cell = new Cell();
            cell.getTextList().add( "cell2" );
            cellList.add( cell );
          }
        }
        rowList.add( row );
      }
      {
        //
        Row row = new Row();
        {
          List<String> titleList = row.getTitleList();
          titleList.add( "column1" );
          titleList.add( "column2" );
        }
        xhtmlDataContainer.getHeader().getRowList().add( row );
      }
      
    }
    
    //
    String asXML = XMLHelper.storeObjectAsXML( xhtmlDataContainer );
    
    //System.out.println( asXML );
    asXML = asXML.replaceAll( "\\s+", " " );
    //System.out.println( asXML );
    
    //
    assertEquals( "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?> <table> <thead> <tr> <th>column1</th> <th>column2</th> </tr> </thead> <tbody> <tr> <td> <a>cell1</a> </td> <td>cell2</td> </tr> <tr> <td>cell1</td> <td>cell2</td> </tr> </tbody> </table> ",
                  asXML );
  }
}

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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.serializer.TableUnmarshaller;
import org.omnaest.utils.structure.table.serializer.common.XHTMLDataContainer;
import org.omnaest.utils.structure.table.serializer.common.XHTMLDataContainer.Cell;
import org.omnaest.utils.structure.table.serializer.common.XHTMLDataContainer.Row;
import org.omnaest.utils.structure.table.serializer.common.XMLDataContainer;
import org.omnaest.utils.structure.table.serializer.marshaller.TableMarshallerXML;
import org.omnaest.utils.structure.table.subspecification.TableSerializable.TableSerializer;
import org.omnaest.utils.xml.JAXBXMLHelper;

/**
 * @see TableMarshallerXML
 * @see TableUnmarshaller
 * @author Omnaest
 * @param <E>
 */
public class TableUnmarshallerHTML implements TableUnmarshaller<String>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -1183646781295216284L;
  
  /* ********************************************** Variables ********************************************** */
  protected String          encoding         = TableSerializer.DEFAULT_ENCODING_UTF8;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * 
   */
  public TableUnmarshallerHTML()
  {
    super();
  }
  
  /**
   * @param encoding
   */
  public TableUnmarshallerHTML( String encoding )
  {
    super();
    this.encoding = encoding;
  }
  
  @Override
  public void unmarshal( Table<String> table, InputStream inputStream )
  {
    //
    if ( table != null && inputStream != null )
    {
      //
      try
      {
        //
        @SuppressWarnings("unchecked")
        XHTMLDataContainer<String> xhtmlDataContainer = JAXBXMLHelper.loadObjectFromXML( inputStream, XHTMLDataContainer.class );
        
        //
        this.writeXMLDataContainerToTableContent( table, xhtmlDataContainer );
      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }
    }
  }
  
  @Override
  public void unmarshal( Table<String> table, CharSequence charSequence )
  {
    //
    if ( charSequence != null && table != null )
    {
      //
      ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      byteArrayContainer.copyFrom( charSequence, this.encoding );
      
      //
      InputStream inputStream = byteArrayContainer.getInputStream();
      this.unmarshal( table, inputStream );
    }
  }
  
  /**
   * Clears the {@link Table} and writes the data of a {@link XMLDataContainer} to it
   * 
   * @param xmlDataContainer
   */
  protected void writeXMLDataContainerToTableContent( Table<String> table, XHTMLDataContainer<String> xhtmlDataContainer )
  {
    //
    if ( table != null && xhtmlDataContainer != null )
    {
      //
      table.clear();
      
      //
      List<Row> rowList = new ArrayList<Row>();
      rowList.addAll( xhtmlDataContainer.getHeader().getRowList() );
      rowList.addAll( xhtmlDataContainer.getBody().getRowList() );
      rowList.addAll( xhtmlDataContainer.getRowList() );
      
      //
      {
        //
        for ( Row row : rowList )
        {
          if ( row != null )
          {
            //
            List<String> titleList = row.getTitleList();
            if ( titleList != null && !titleList.isEmpty() )
            {
              table.setColumnTitleValues( titleList );
            }
            
            //              
            List<Cell> cellList = row.getCellList();
            List<List<String>> collectionOfValueList = ListUtils.convert( cellList, new ElementConverter<Cell, List<String>>()
            {
              @Override
              public List<String> convert( Cell cell )
              {
                return cell.getValueList();
              }
            } );
            List<String> mergedValueList = ListUtils.mergeAll( collectionOfValueList );
            if ( mergedValueList != null && !mergedValueList.isEmpty() )
            {
              table.addRowCellElements( mergedValueList );
            }
          }
        }
      }
    }
  }
  
}

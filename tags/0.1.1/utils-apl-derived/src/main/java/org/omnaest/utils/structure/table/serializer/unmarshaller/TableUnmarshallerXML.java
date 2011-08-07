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

import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.serializer.TableUnmarshaller;
import org.omnaest.utils.structure.table.serializer.common.XMLDataContainer;
import org.omnaest.utils.structure.table.serializer.marshaller.TableMarshallerXML;
import org.omnaest.utils.structure.table.subspecification.TableSerializable.TableSerializer;
import org.omnaest.utils.xml.XMLHelper;

/**
 * @see TableMarshallerXML
 * @see TableUnmarshaller
 * @author Omnaest
 * @param <E>
 */
public class TableUnmarshallerXML<E> implements TableUnmarshaller<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -1183646781295216284L;
  
  /* ********************************************** Variables ********************************************** */
  protected String          encoding         = TableSerializer.DEFAULT_ENCODING_UTF8;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * 
   */
  public TableUnmarshallerXML()
  {
    super();
  }
  
  /**
   * @param encoding
   */
  public TableUnmarshallerXML( String encoding )
  {
    super();
    this.encoding = encoding;
  }
  
  @Override
  public void unmarshal( Table<E> table, InputStream inputStream )
  {
    //
    if ( table != null && inputStream != null )
    {
      //
      try
      {
        //
        @SuppressWarnings("unchecked")
        XMLDataContainer<E> xmlDataContainer = XMLHelper.loadObjectFromXML( inputStream, XMLDataContainer.class );
        
        //
        this.writeXMLDataContainerToTableContent( table, xmlDataContainer );
      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }
    }
  }
  
  @Override
  public void unmarshal( Table<E> table, CharSequence charSequence )
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
  protected void writeXMLDataContainerToTableContent( Table<E> table, XMLDataContainer<E> xmlDataContainer )
  {
    //
    if ( table != null && xmlDataContainer != null )
    {
      //
      table.clear();
      
      //
      {
        int rowIndexPosition = xmlDataContainer.getRowSize() - 1;
        int columnIndexPosition = xmlDataContainer.getColumnSize() - 1;
        E element = null;
        table.setCellElement( rowIndexPosition, columnIndexPosition, element );
      }
      
      //
      int cellIndexPosition = 0;
      for ( E element : xmlDataContainer.getCellElementList() )
      {
        table.setCellElement( cellIndexPosition++, element );
      }
      
      //
      table.setColumnTitleValues( xmlDataContainer.getColumnTitleValueList() );
      table.setRowTitleValues( xmlDataContainer.getRowTitleValueList() );
      table.setTableName( xmlDataContainer.getTableName() );
    }
  }
  
}

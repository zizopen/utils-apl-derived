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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.serializer.TableMarshaller;
import org.omnaest.utils.structure.table.serializer.common.XMLDataContainer;
import org.omnaest.utils.structure.table.subspecification.TableSerializable.TableSerializer;
import org.omnaest.utils.xml.XMLHelper;

/**
 * @see TableMarshaller
 * @author Omnaest
 * @param <E>
 */
public class TableMarshallerXML<E> implements TableMarshaller<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 729579410301748875L;
  
  /* ********************************************** Variables ********************************************** */
  protected String          encoding         = TableSerializer.DEFAULT_ENCODING_UTF8;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * 
   */
  public TableMarshallerXML()
  {
    super();
  }
  
  /**
   * @param encoding
   */
  public TableMarshallerXML( String encoding )
  {
    super();
    this.encoding = encoding;
  }
  
  @Override
  public void marshal( Table<E> table, OutputStream outputStream )
  {
    //
    if ( table != null && outputStream != null )
    {
      //
      XMLHelper.storeObjectAsXML( this.createXMLDataContainerFromTableContent( table ), outputStream, this.encoding );
    }
  }
  
  @Override
  public void marshal( Table<E> table, Appendable appendable )
  {
    //
    if ( table != null && appendable != null )
    {
      //
      ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      
      //
      OutputStream outputStream = byteArrayContainer.getOutputStream();
      this.marshal( table, outputStream );
      
      //
      byteArrayContainer.writeTo( appendable, this.encoding );
    }
  }
  
  /**
   * Creates a {@link XMLDataContainer} from the {@link Table} content
   * 
   * @return
   */
  protected XMLDataContainer<E> createXMLDataContainerFromTableContent( Table<E> table )
  {
    //
    XMLDataContainer<E> retval = null;
    
    //
    if ( table != null )
    {
      //      
      ArrayList<Object> rowTitleValueList = new ArrayList<Object>( table.getRowTitleValueList() );
      ArrayList<Object> columnTitleValueList = new ArrayList<Object>( table.getColumnTitleValueList() );
      ArrayList<E> cellElementList = new ArrayList<E>( table.getCellElementList() );
      Integer columnSize = table.getTableSize().getColumnSize();
      Integer rowSize = table.getTableSize().getRowSize();
      Object tableName = table.getTableName();
      
      retval = new XMLDataContainer<E>( rowTitleValueList, columnTitleValueList, cellElementList, columnSize, rowSize, tableName );
    }
    
    //
    return retval;
  }
  
  @Override
  public void marshal( Table<E> table, InputStream inputStream, OutputStream outputStream )
  {
    throw new UnsupportedOperationException();
  }
  
}

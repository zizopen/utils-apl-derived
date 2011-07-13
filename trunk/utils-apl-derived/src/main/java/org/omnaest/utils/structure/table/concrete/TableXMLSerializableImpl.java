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
package org.omnaest.utils.structure.table.concrete;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.TableXMLSerializable;
import org.omnaest.utils.xml.XMLHelper;

/**
 * @see Table
 * @see TableXMLSerializable
 * @author Omnaest
 * @param <E>
 */
public class TableXMLSerializableImpl<E> implements TableXMLSerializable<E>
{
  /* ********************************************** Variables ********************************************** */
  protected Table<E> table = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Transfer data class for XML serialization
   * 
   * @author Omnaest
   * @param <E>
   */
  @XmlRootElement
  protected static class XMLDataContainer<E>
  {
    /* ********************************************** Variables ********************************************** */
    
    @XmlElement
    protected Object            tableName            = null;
    
    @XmlAttribute
    protected Integer           rowSize              = null;
    
    @XmlAttribute
    protected Integer           columnSize           = null;
    
    @XmlElementWrapper
    protected ArrayList<Object> rowTitleValueList    = new ArrayList<Object>();
    
    @XmlElementWrapper
    protected ArrayList<Object> columnTitleValueList = new ArrayList<Object>();
    
    @XmlElementWrapper
    protected ArrayList<E>      cellElementList      = new ArrayList<E>();
    
    protected XMLDataContainer()
    {
      super();
    }
    
    /**
     * @param rowTitleValueList
     * @param columnTitleValueList
     * @param cellElementList
     * @param columnSize
     * @param rowSize
     * @param tableName
     */
    public XMLDataContainer( ArrayList<Object> rowTitleValueList, ArrayList<Object> columnTitleValueList,
                             ArrayList<E> cellElementList, Integer columnSize, Integer rowSize, Object tableName )
    {
      super();
      this.rowTitleValueList = rowTitleValueList;
      this.columnTitleValueList = columnTitleValueList;
      this.cellElementList = cellElementList;
      this.columnSize = columnSize;
      this.rowSize = rowSize;
      this.tableName = tableName;
    }
    
    public Integer getRowSize()
    {
      return this.rowSize;
    }
    
    public Integer getColumnSize()
    {
      return this.columnSize;
    }
    
    public ArrayList<Object> getRowTitleValueList()
    {
      return this.rowTitleValueList;
    }
    
    public ArrayList<Object> getColumnTitleValueList()
    {
      return this.columnTitleValueList;
    }
    
    public ArrayList<E> getCellElementList()
    {
      return this.cellElementList;
    }
    
    public Object getTableName()
    {
      return this.tableName;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param table
   */
  public TableXMLSerializableImpl( Table<E> table )
  {
    super();
    this.table = table;
  }
  
  /**
   * Creates a {@link XMLDataContainer} from the {@link Table} content
   * 
   * @return
   */
  protected XMLDataContainer<E> createXMLDataContainerFromTableContent()
  {
    //
    ArrayList<Object> rowTitleValueList = new ArrayList<Object>( this.table.getRowTitleValueList() );
    ArrayList<Object> columnTitleValueList = new ArrayList<Object>( this.table.getColumnTitleValueList() );
    ArrayList<E> cellElementList = new ArrayList<E>( this.table.getCellElementList() );
    Integer columnSize = this.table.getTableSize().getColumnSize();
    Integer rowSize = this.table.getTableSize().getRowSize();
    Object tableName = this.table.getTableName();
    XMLDataContainer<E> dataContainer = new XMLDataContainer<E>( rowTitleValueList, columnTitleValueList, cellElementList,
                                                                 columnSize, rowSize, tableName );
    
    //
    return dataContainer;
  }
  
  @Override
  public Table<E> writeAsXMLTo( Appendable appendable )
  {
    //
    XMLHelper.storeObjectAsXML( this.createXMLDataContainerFromTableContent(), appendable );
    
    // 
    return this.table;
  }
  
  @Override
  public Table<E> writeAsXMLTo( OutputStream outputStream )
  {
    //
    XMLHelper.storeObjectAsXML( this.createXMLDataContainerFromTableContent(), outputStream );
    
    //
    return this.table;
  }
  
  @SuppressWarnings({ "unchecked" })
  @Override
  public Table<E> parseXMLFrom( String xmlContent )
  {
    //
    try
    {
      //
      XMLDataContainer<E> xmlDataContainer = XMLHelper.loadObjectFromXML( xmlContent, XMLDataContainer.class );
      
      //
      this.writeXMLDataContainerToTableContent( xmlDataContainer );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    
    // 
    return this.table;
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public Table<E> parseXMLFrom( InputStream inputStream )
  {
    //
    try
    {
      //
      XMLDataContainer xmlDataContainer = XMLHelper.loadObjectFromXML( inputStream, XMLDataContainer.class );
      
      //
      this.writeXMLDataContainerToTableContent( xmlDataContainer );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    
    // 
    return this.table;
  }
  
  @Override
  public Table<E> parseXMLFrom( CharSequence charSequence )
  {
    //
    try
    {
      //      
      @SuppressWarnings("unchecked")
      XMLDataContainer<E> xmlDataContainer = XMLHelper.loadObjectFromXML( charSequence, XMLDataContainer.class );
      
      //
      this.writeXMLDataContainerToTableContent( xmlDataContainer );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    
    // 
    return this.table;
  }
  
  /**
   * Clears the {@link Table} and writes the data of a {@link XMLDataContainer} to it
   * 
   * @param xmlDataContainer
   */
  protected void writeXMLDataContainerToTableContent( XMLDataContainer<E> xmlDataContainer )
  {
    //
    this.table.clear();
    
    //
    {
      int rowIndexPosition = xmlDataContainer.getRowSize() - 1;
      int columnIndexPosition = xmlDataContainer.getColumnSize() - 1;
      E element = null;
      this.table.setCellElement( rowIndexPosition, columnIndexPosition, element );
    }
    
    //
    int cellIndexPosition = 0;
    for ( E element : xmlDataContainer.getCellElementList() )
    {
      this.table.setCellElement( cellIndexPosition++, element );
    }
    
    //
    this.table.setColumnTitleValues( xmlDataContainer.getColumnTitleValueList() );
    this.table.setRowTitleValues( xmlDataContainer.getRowTitleValueList() );
    this.table.setTableName( xmlDataContainer.getTableName() );
  }
  
}

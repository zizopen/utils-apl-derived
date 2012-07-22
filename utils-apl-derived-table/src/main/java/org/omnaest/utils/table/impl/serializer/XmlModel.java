/*******************************************************************************
 * Copyright 2012 Danny Kunz
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
package org.omnaest.utils.table.impl.serializer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Model for {@link XmlMarshallerImpl} and {@link XmlUnmarshallerImpl}
 * 
 * @author Omnaest
 */
@SuppressWarnings("javadoc")
@XmlRootElement(name = "table")
@XmlAccessorType(XmlAccessType.FIELD)
class XmlModel<E>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  
  @XmlElement
  private MetaData metaData = null;
  
  @XmlElementWrapper(name = "rows")
  @XmlElement(name = "row")
  private Row<E>[] rows;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  @XmlAccessorType(XmlAccessType.FIELD)
  static class Row<E>
  {
    @XmlElements({ @XmlElement(name = "string", type = String.class), @XmlElement(name = "byte", type = Byte.class),
        @XmlElement(name = "short", type = Short.class), @XmlElement(name = "int", type = Integer.class),
        @XmlElement(name = "long", type = Long.class), @XmlElement(name = "char", type = Character.class),
        @XmlElement(name = "float", type = Float.class), @XmlElement(name = "double", type = Double.class),
        @XmlElement(name = "boolean", type = Boolean.class), @XmlElement(name = "date", type = Date.class),
        @XmlElement(name = "bigint", type = BigInteger.class), @XmlElement(name = "bigdecimal", type = BigDecimal.class),
        @XmlElement(name = "object") })
    private E[] elements;
    
    Row( E[] elements )
    {
      super();
      this.elements = elements;
    }
    
    Row()
    {
      super();
    }
    
    public E[] getElements()
    {
      return this.elements;
    }
    
    public void setElements( E[] elements )
    {
      this.elements = elements;
    }
    
  }
  
  @XmlType
  @XmlAccessorType(XmlAccessType.FIELD)
  static class MetaData
  {
    private String       tableName       = null;
    
    @XmlElementWrapper(name = "rowTitles")
    @XmlElement(name = "rowTitle")
    private List<String> rowTitleList    = null;
    
    @XmlElementWrapper(name = "columnTitles")
    @XmlElement(name = "columnTitle")
    private List<String> columnTitleList = null;
    
    public String getTableName()
    {
      return this.tableName;
    }
    
    public void setTableName( String tableName )
    {
      this.tableName = tableName;
    }
    
    public List<String> getRowTitleList()
    {
      return this.rowTitleList;
    }
    
    public void setRowTitleList( List<String> rowTitleList )
    {
      this.rowTitleList = rowTitleList;
    }
    
    public List<String> getColumnTitleList()
    {
      return this.columnTitleList;
    }
    
    public void setColumnTitleList( List<String> columnTitleList )
    {
      this.columnTitleList = columnTitleList;
    }
    
  }
  
  /* *************************************************** Methods **************************************************** */
  
  public MetaData getMetaData()
  {
    return this.metaData;
  }
  
  public void setMetaData( MetaData metaData )
  {
    this.metaData = metaData;
  }
  
  public Row<E>[] getRows()
  {
    return this.rows;
  }
  
  public void setRows( Row<E>[] rows )
  {
    this.rows = rows;
  }
  
}

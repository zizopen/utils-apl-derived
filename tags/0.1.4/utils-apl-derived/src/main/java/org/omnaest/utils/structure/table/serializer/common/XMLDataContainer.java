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

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Transfer data class for XML serialization
 * 
 * @author Omnaest
 * @param <E>
 */
@XmlRootElement
public class XMLDataContainer<E>
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

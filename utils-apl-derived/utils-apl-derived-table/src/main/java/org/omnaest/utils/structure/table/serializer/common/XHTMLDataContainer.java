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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.xml.JAXBXMLHelper;

/**
 * Transfer data class for HTML table serialization
 * 
 * @author Omnaest
 * @param <E>
 */
@XmlRootElement(name = "table")
@XmlAccessorType(XmlAccessType.FIELD)
public class XHTMLDataContainer<E>
{
  /* ********************************************** Variables ********************************************** */
  
  @XmlElement(name = "thead")
  private Header    header  = new Header();
  
  @XmlElement(name = "tbody")
  private Body      body    = new Body();
  
  @XmlElement(name = "tr")
  private List<Row> rowList = new ArrayList<Row>();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class RowContainer
  {
    @XmlElement(name = "tr")
    private List<Row> rowList = new ArrayList<Row>();
    
    public List<Row> getRowList()
    {
      return this.rowList;
    }
    
  }
  
  public static class Header extends RowContainer
  {
    
  }
  
  public static class Body extends RowContainer
  {
    
  }
  
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class Anker
  {
    @XmlMixed
    private List<String> textList = new ArrayList<String>();
    
    public List<String> getTextList()
    {
      return this.textList;
    }
    
  }
  
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class Cell
  {
    @XmlElement(name = "a")
    private List<Anker>  ankerList = new ArrayList<Anker>();
    
    @XmlMixed
    private List<String> textList  = new ArrayList<String>();
    
    public List<Anker> getAnkerList()
    {
      return this.ankerList;
    }
    
    public List<String> getTextList()
    {
      return this.textList;
    }
    
    public List<String> getValueList()
    {
      //
      List<String> retlist = new ArrayList<String>();
      
      //
      retlist.addAll( this.getTextList() );
      ElementConverter<Anker, List<String>> elementTransformer = new ElementConverter<Anker, List<String>>()
      {
        @Override
        public List<String> convert( Anker anker )
        {
          return anker.getTextList();
        }
      };
      List<List<String>> collectionOfTextList = ListUtils.convert( this.getAnkerList(), elementTransformer );
      retlist.addAll( ListUtils.mergeAll( collectionOfTextList ) );
      
      //
      return retlist;
    }
    
  }
  
  @XmlType(name = "tr")
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class Row
  {
    @XmlElement(name = "td")
    private List<Cell>   cellList  = new ArrayList<Cell>();
    
    @XmlElement(name = "th")
    private List<String> titleList = new ArrayList<String>();
    
    public List<String> getTitleList()
    {
      return this.titleList;
    }
    
    public void setTitleList( List<String> titleList )
    {
      this.titleList = titleList;
    }
    
    public List<Cell> getCellList()
    {
      return this.cellList;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  protected XHTMLDataContainer()
  {
    super();
  }
  
  @Override
  public String toString()
  {
    return JAXBXMLHelper.storeObjectAsXML( this );
  }
  
  public Header getHeader()
  {
    return this.header;
  }
  
  public Body getBody()
  {
    return this.body;
  }
  
  public List<Row> getRowList()
  {
    return this.rowList;
  }
  
}

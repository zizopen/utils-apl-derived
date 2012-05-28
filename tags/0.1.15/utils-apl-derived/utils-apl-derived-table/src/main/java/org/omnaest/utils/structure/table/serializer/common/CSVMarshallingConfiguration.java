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
package org.omnaest.utils.structure.table.serializer.common;

/* ********************************************** Classes/Interfaces ********************************************** */
/**
 * @author Omnaest
 */
public class CSVMarshallingConfiguration
{
  /* ************************************************** Constants *************************************************** */
  public static final String  DEFAULT_DELIMITER                 = ";";
  public static final String  DEFAULT_QUOTATION_CHARACTER       = "\"";
  public static final boolean DEFAULT_HAS_ENABLED_TABLE_NAME    = false;
  public static final boolean DEFAULT_HAS_ENABLED_COLUMN_TITLES = true;
  public static final boolean DEFAULT_HAS_ENABLED_ROW_TITLES    = false;
  public static final String  DEFAULT_ENCODING                  = "utf-8";
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private String              encoding                          = DEFAULT_ENCODING;
  private String              delimiter                         = DEFAULT_DELIMITER;
  private String              quotationCharacter                = DEFAULT_QUOTATION_CHARACTER;
  private boolean             hasEnabledTableName               = DEFAULT_HAS_ENABLED_TABLE_NAME;
  private boolean             hasEnabledColumnTitles            = DEFAULT_HAS_ENABLED_COLUMN_TITLES;
  private boolean             hasEnabledRowTitles               = DEFAULT_HAS_ENABLED_ROW_TITLES;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @return
   */
  public String getEncoding()
  {
    return this.encoding;
  }
  
  /**
   * Sets the encoding. Default is {@value #DEFAULT_ENCODING}
   * 
   * @param encoding
   * @return this
   */
  public CSVMarshallingConfiguration setEncoding( String encoding )
  {
    this.encoding = encoding;
    return this;
  }
  
  /**
   * @return
   */
  public String getDelimiter()
  {
    return this.delimiter;
  }
  
  /**
   * Sets the delimiter. Default is {@value #DEFAULT_DELIMITER}
   * 
   * @param delimiter
   * @return this
   */
  public CSVMarshallingConfiguration setDelimiter( String delimiter )
  {
    this.delimiter = delimiter;
    return this;
  }
  
  /**
   * @return
   */
  public String getQuotationCharacter()
  {
    return this.quotationCharacter;
  }
  
  /**
   * Sets the quotation character. Default is {@value #DEFAULT_QUOTATION_CHARACTER}
   * 
   * @param quotationCharacter
   * @return this
   */
  public CSVMarshallingConfiguration setQuotationCharacter( String quotationCharacter )
  {
    this.quotationCharacter = quotationCharacter;
    return this;
  }
  
  /**
   * @return
   */
  public boolean hasEnabledTableName()
  {
    return this.hasEnabledTableName;
  }
  
  /**
   * Set if table name marshaling should be enabled. Default is {@value #DEFAULT_HAS_ENABLED_TABLE_NAME}
   * 
   * @param hasEnabledTableName
   * @return this
   */
  public CSVMarshallingConfiguration setHasEnabledTableName( boolean hasEnabledTableName )
  {
    this.hasEnabledTableName = hasEnabledTableName;
    return this;
  }
  
  /**
   * @return
   */
  public boolean hasEnabledColumnTitles()
  {
    return this.hasEnabledColumnTitles;
  }
  
  /**
   * Set if table columns marshaling should be enabled. Default is {@value #DEFAULT_HAS_ENABLED_COLUMN_TITLES}
   * 
   * @param hasEnabledColumnTitles
   * @return this
   */
  public CSVMarshallingConfiguration setHasEnabledColumnTitles( boolean hasEnabledColumnTitles )
  {
    this.hasEnabledColumnTitles = hasEnabledColumnTitles;
    return this;
  }
  
  /**
   * @return
   */
  public boolean hasEnabledRowTitles()
  {
    return this.hasEnabledRowTitles;
  }
  
  /**
   * Set if table rows marshaling should be enabled. Default is {@value #DEFAULT_HAS_ENABLED_ROW_TITLES}
   * 
   * @param hasEnabledRowTitles
   * @return this
   */
  public CSVMarshallingConfiguration setHasEnabledRowTitles( boolean hasEnabledRowTitles )
  {
    this.hasEnabledRowTitles = hasEnabledRowTitles;
    return this;
  }
}

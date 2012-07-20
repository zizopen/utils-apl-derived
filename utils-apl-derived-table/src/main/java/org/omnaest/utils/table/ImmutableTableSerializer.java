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
package org.omnaest.utils.table;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Immutable {@link TableSerializer}
 * 
 * @author Omnaest
 * @param <E>
 */
public interface ImmutableTableSerializer<E>
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see ImmutableTableSerializer
   * @see ImmutableTableSerializer.Marshaller
   * @author Omnaest
   * @param <E>
   */
  public static interface MarshallerDeclarer<E>
  {
    /**
     * @return {@link ImmutableTableSerializer.MarshallerCsv}
     */
    public MarshallerCsv<E> asCsv();
    
    /**
     * @return {@link ImmutableTableSerializer.MarshallerXml}
     */
    public MarshallerXml<E> asXml();
    
    /**
     * @return {@link ImmutableTableSerializer.MarshallerJson}
     */
    public MarshallerJson<E> asJson();
    
    /**
     * @return {@link ImmutableTableSerializer.MarshallerPlainText}
     */
    public MarshallerPlainText<E> asPlainText();
  }
  
  /**
   * @see ImmutableTableSerializer
   * @see ImmutableTableSerializer.MarshallerDeclarer
   * @author Omnaest
   * @param <E>
   */
  public static interface Marshaller<E>
  {
    
    /* ********************************************** Classes/Interfaces ********************************************** */
    /**
     * {@link MarshallingConfiguration} for an {@link ImmutableTableSerializer.Marshaller}
     * 
     * @author Omnaest
     */
    public static class MarshallingConfiguration
    {
      /* ************************************************** Constants *************************************************** */
      public static final boolean DEFAULT_HAS_ENABLED_TABLE_NAME    = false;
      public static final boolean DEFAULT_HAS_ENABLED_COLUMN_TITLES = true;
      public static final boolean DEFAULT_HAS_ENABLED_ROW_TITLES    = false;
      public static final String  DEFAULT_ENCODING                  = "utf-8";
      
      /* ************************************** Variables / State (internal/hiding) ************************************* */
      private String              encoding                          = DEFAULT_ENCODING;
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
      public MarshallingConfiguration setEncoding( String encoding )
      {
        this.encoding = encoding;
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
      public MarshallingConfiguration setHasEnabledTableName( boolean hasEnabledTableName )
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
      public MarshallingConfiguration setHasEnabledColumnTitles( boolean hasEnabledColumnTitles )
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
      public MarshallingConfiguration setHasEnabledRowTitles( boolean hasEnabledRowTitles )
      {
        this.hasEnabledRowTitles = hasEnabledRowTitles;
        return this;
      }
    }
    
    /* *************************************************** Methods **************************************************** */
    
    /**
     * @param writer
     *          {@link Writer}
     * @return the executing {@link Table} instance
     */
    public Table<E> to( Writer writer );
    
    /**
     * @param outputStream
     *          {@link OutputStream}
     * @return the executing {@link Table} instance
     */
    public Table<E> to( OutputStream outputStream );
    
    /**
     * @param appendable
     *          {@link Appendable}
     * @return the executing {@link Table} instance
     */
    public Table<E> to( Appendable appendable );
    
    /**
     * Returns a {@link String} representation
     * 
     * @return
     */
    public String toString();
    
    /**
     * Writes to a {@link File}
     * 
     * @param file
     *          {@link File}
     * @return the executing {@link Table} instance
     */
    public Table<E> to( File file );
    
  }
  
  /**
   * {@link ImmutableTableSerializer.Marshaller} for plain text
   * 
   * @author Omnaest
   * @param <E>
   */
  public static interface MarshallerPlainText<E> extends Marshaller<E>
  {
    /**
     * Makes the {@link ImmutableTableSerializer.Marshaller} using the given
     * {@link ImmutableTableSerializer.Marshaller.MarshallingConfiguration}
     * 
     * @param configuration
     *          {@link ImmutableTableSerializer.Marshaller.MarshallingConfiguration}
     * @return this
     */
    public MarshallerPlainText<E> using( MarshallingConfiguration configuration );
  }
  
  /**
   * {@link ImmutableTableSerializer.Marshaller} for xml
   * 
   * @author Omnaest
   * @param <E>
   */
  public static interface MarshallerXml<E> extends Marshaller<E>
  {
    /**
     * Makes the {@link ImmutableTableSerializer.Marshaller} using the given
     * {@link ImmutableTableSerializer.Marshaller.MarshallingConfiguration}
     * 
     * @param configuration
     *          {@link ImmutableTableSerializer.Marshaller.MarshallingConfiguration}
     * @return this
     */
    public MarshallerXml<E> using( MarshallingConfiguration configuration );
  }
  
  /**
   * {@link ImmutableTableSerializer.Marshaller} for xml
   * 
   * @author Omnaest
   * @param <E>
   */
  public static interface MarshallerJson<E> extends Marshaller<E>
  {
    /**
     * Makes the {@link ImmutableTableSerializer.Marshaller} using the given
     * {@link ImmutableTableSerializer.Marshaller.MarshallingConfiguration}
     * 
     * @param configuration
     *          {@link ImmutableTableSerializer.Marshaller.MarshallingConfiguration}
     * @return this
     */
    public MarshallerJson<E> using( MarshallingConfiguration configuration );
  }
  
  public static interface MarshallerCsv<E> extends Marshaller<E>
  {
    /* ********************************************** Classes/Interfaces ********************************************** */
    /**
     * {@link CSVMarshallingConfiguration} for an {@link ImmutableTableSerializer.MarshallerCsv}
     * 
     * @author Omnaest
     */
    public static class CSVMarshallingConfiguration extends MarshallingConfiguration
    {
      /* ************************************************** Constants *************************************************** */
      public static final String DEFAULT_DELIMITER           = ";";
      public static final String DEFAULT_QUOTATION_CHARACTER = "\"";
      
      /* ************************************** Variables / State (internal/hiding) ************************************* */
      
      private String             delimiter                   = DEFAULT_DELIMITER;
      private String             quotationCharacter          = DEFAULT_QUOTATION_CHARACTER;
      
      /* *************************************************** Methods **************************************************** */
      
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
      
      @Override
      public CSVMarshallingConfiguration setEncoding( String encoding )
      {
        super.setEncoding( encoding );
        return this;
      }
      
      @Override
      public CSVMarshallingConfiguration setHasEnabledTableName( boolean hasEnabledTableName )
      {
        super.setHasEnabledTableName( hasEnabledTableName );
        return this;
      }
      
      @Override
      public CSVMarshallingConfiguration setHasEnabledColumnTitles( boolean hasEnabledColumnTitles )
      {
        super.setHasEnabledColumnTitles( hasEnabledColumnTitles );
        return this;
      }
      
      @Override
      public CSVMarshallingConfiguration setHasEnabledRowTitles( boolean hasEnabledRowTitles )
      {
        super.setHasEnabledRowTitles( hasEnabledRowTitles );
        return this;
      }
      
    }
    
    /* *************************************************** Methods **************************************************** */
    /**
     * Makes the {@link ImmutableTableSerializer.Marshaller} using the given {@link CSVMarshallingConfiguration}
     * 
     * @param configuration
     *          {@link CSVMarshallingConfiguration}
     * @return this
     */
    public MarshallerCsv<E> using( CSVMarshallingConfiguration configuration );
  }
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * Allows to marshal the executing {@link Table} into another format like Xml, Json or Csv
   * 
   * @return {@link ImmutableTableSerializer.MarshallerDeclarer}
   */
  public MarshallerDeclarer<E> marshal();
}

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.serializer.TableMarshaller;
import org.omnaest.utils.structure.table.serializer.common.CSVConstants;
import org.omnaest.utils.structure.table.serializer.unmarshaller.TableUnmarshallerCSV;
import org.omnaest.utils.structure.table.subspecification.TableSerializable.TableSerializer;

/**
 * {@link TableMarshaller} which returns CSV text.<br>
 * <br>
 * This implementation supports any delimiter and simple quotation rules. See {@link TableUnmarshallerCSV} for more information
 * about the rules.
 * 
 * @see CSVConstants
 * @see TableMarshaller
 * @see TableUnmarshallerCSV
 * @author Omnaest
 * @param <E>
 */
public class TableMarshallerCSV<E> implements TableMarshaller<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID   = 729579410301748875L;
  
  /* ********************************************** Variables ********************************************** */
  private String            encoding           = TableSerializer.DEFAULT_ENCODING_UTF8;
  private String            delimiter          = CSVConstants.DEFAULT_DELIMITER;
  private String            quotationCharacter = CSVConstants.DEFAULT_QUOTATION_CHARACTER;
  
  private boolean           writeTableName     = CSVConstants.DEFAULT_HAS_TABLE_NAME;
  private boolean           writeColumnTitles  = CSVConstants.DEFAULT_HAS_COLUMN_TITLES;
  private boolean           writeRowTitles     = CSVConstants.DEFAULT_HAS_ROW_TITLES;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see TableMarshallerCSV
   */
  public TableMarshallerCSV()
  {
    super();
  }
  
  @Override
  public void marshal( Table<E> table, OutputStream outputStream )
  {
    //
    if ( table != null && outputStream != null )
    {
      //
      StringBuffer stringBuffer = new StringBuffer();
      this.marshal( table, stringBuffer );
      
      //
      ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      byteArrayContainer.copyFrom( stringBuffer, this.encoding );
      byteArrayContainer.writeTo( outputStream );
    }
  }
  
  @Override
  public void marshal( Table<E> table, Appendable appendable )
  {
    //
    try
    {
      //
      if ( table != null && appendable != null )
      {
        //
        boolean hasColumnTitles = table.hasColumnTitles();
        boolean hasRowTitles = table.hasRowTitles();
        boolean hasTableName = table.hasTableName();
        
        //
        boolean processColumnTitles = this.writeColumnTitles && hasColumnTitles;
        boolean processRowTitles = this.writeRowTitles && hasRowTitles;
        boolean processTableName = this.writeTableName && hasTableName;
        
        //
        if ( processTableName )
        {
          //
          appendable.append( String.valueOf( table.getTableName() ) + "\n" );
        }
        
        //
        if ( processColumnTitles )
        {
          //
          if ( processRowTitles )
          {
            //
            appendable.append( this.delimiter );
          }
          
          //
          boolean first = true;
          for ( Object columnTitleValue : table.getColumnTitleValueList() )
          {
            //
            appendable.append( !first ? this.delimiter : "" );
            appendable.append( String.valueOf( columnTitleValue ) );
            first = false;
          }
          
          //
          appendable.append( "\n" );
        }
        
        //
        for ( Row<E> row : table.rows() )
        {
          //
          if ( processRowTitles )
          {
            //
            appendable.append( row.title().getValueAsString() );
            appendable.append( this.delimiter );
          }
          
          //
          boolean first = true;
          for ( Cell<E> cell : row.cells() )
          {
            //
            appendable.append( !first ? this.delimiter : "" );
            
            //
            final E element = cell.getElement();
            appendable.append( this.encodeIntoCellString( element ) );
            first = false;
          }
          
          //
          appendable.append( "\n" );
        }
      }
    }
    catch ( Exception e )
    {
    }
  }
  
  /**
   * Encodes a single cell element into its csv string form
   * 
   * @param element
   * @return
   */
  private String encodeIntoCellString( final E element )
  {
    //
    String retval = null;
    
    //
    if ( element != null )
    {
      //
      retval = String.valueOf( element );
      
      //
      final boolean containsDelimiter = retval.contains( this.delimiter );
      final boolean containsQuotationCharacter = StringUtils.isNotEmpty( this.quotationCharacter )
                                                 && retval.contains( this.quotationCharacter );
      if ( containsQuotationCharacter )
      {
        retval = retval.replaceAll( Pattern.quote( this.quotationCharacter ),
                                    Matcher.quoteReplacement( this.quotationCharacter + this.quotationCharacter ) );
      }
      if ( containsDelimiter )
      {
        retval = this.quotationCharacter + retval + this.quotationCharacter;
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public void marshal( Table<E> table, InputStream inputStream, OutputStream outputStream )
  {
    throw new UnsupportedOperationException();
  }
  
  /**
   * Sets the quotation character which is {@link CSVConstants#DEFAULT_QUOTATION_CHARACTER} by default
   * 
   * @param quotationCharacter
   * @return
   */
  public TableMarshallerCSV<E> setQuotationCharacter( String quotationCharacter )
  {
    this.quotationCharacter = quotationCharacter;
    return this;
  }
  
  /**
   * @param encoding
   * @return this
   */
  public TableMarshallerCSV<E> setEncoding( String encoding )
  {
    this.encoding = encoding;
    return this;
  }
  
  /**
   * @param delimiter
   * @return this
   */
  public TableMarshallerCSV<E> setDelimiter( String delimiter )
  {
    this.delimiter = delimiter;
    return this;
  }
  
  /**
   * @param writeTableName
   * @return this
   */
  public TableMarshallerCSV<E> setWriteTableName( boolean writeTableName )
  {
    this.writeTableName = writeTableName;
    return this;
  }
  
  /**
   * @param writeColumnTitles
   * @return this
   */
  public TableMarshallerCSV<E> setWriteColumnTitles( boolean writeColumnTitles )
  {
    this.writeColumnTitles = writeColumnTitles;
    return this;
  }
  
  /**
   * @param writeRowTitles
   * @return this
   */
  public TableMarshallerCSV<E> setWriteRowTitles( boolean writeRowTitles )
  {
    this.writeRowTitles = writeRowTitles;
    return this;
  }
  
}

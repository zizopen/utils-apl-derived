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
import org.omnaest.utils.structure.table.serializer.common.CSVMarshallingConfiguration;
import org.omnaest.utils.structure.table.serializer.unmarshaller.TableUnmarshallerCSV;

/**
 * {@link TableMarshaller} which returns CSV text.<br>
 * <br>
 * This implementation supports any delimiter and simple quotation rules. See {@link TableUnmarshallerCSV} for more information
 * about the rules.
 * 
 * @see CSVMarshallingConfiguration
 * @see TableMarshaller
 * @see TableUnmarshallerCSV
 * @author Omnaest
 * @param <E>
 */
public class TableMarshallerCSV<E> implements TableMarshaller<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long          serialVersionUID = 729579410301748875L;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private CSVMarshallingConfiguration configuration    = new CSVMarshallingConfiguration();
  
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
      byteArrayContainer.copyFrom( stringBuffer, this.configuration.getEncoding() );
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
        boolean processColumnTitles = this.configuration.hasEnabledColumnTitles() && hasColumnTitles;
        boolean processRowTitles = this.configuration.hasEnabledRowTitles() && hasRowTitles;
        boolean processTableName = this.configuration.hasEnabledTableName() && hasTableName;
        
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
            appendable.append( this.configuration.getDelimiter() );
          }
          
          //
          boolean first = true;
          for ( Object columnTitleValue : table.getColumnTitleValueList() )
          {
            //
            appendable.append( !first ? this.configuration.getDelimiter() : "" );
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
            appendable.append( this.configuration.getDelimiter() );
          }
          
          //
          boolean first = true;
          for ( Cell<E> cell : row.cells() )
          {
            //
            appendable.append( !first ? this.configuration.getDelimiter() : "" );
            
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
      final boolean containsDelimiter = retval.contains( this.configuration.getDelimiter() );
      final boolean containsQuotationCharacter = StringUtils.isNotEmpty( this.configuration.getQuotationCharacter() )
                                                 && retval.contains( this.configuration.getQuotationCharacter() );
      if ( containsQuotationCharacter )
      {
        retval = retval.replaceAll( Pattern.quote( this.configuration.getQuotationCharacter() ),
                                    Matcher.quoteReplacement( this.configuration.getQuotationCharacter()
                                                              + this.configuration.getQuotationCharacter() ) );
      }
      if ( containsDelimiter )
      {
        retval = this.configuration.getQuotationCharacter() + retval + this.configuration.getQuotationCharacter();
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
   * @return {@link CSVMarshallingConfiguration}
   */
  public CSVMarshallingConfiguration getConfiguration()
  {
    return this.configuration;
  }
  
  /**
   * @param configuration
   *          {@link CSVMarshallingConfiguration}
   * @return this
   */
  public TableMarshallerCSV<E> setConfiguration( CSVMarshallingConfiguration configuration )
  {
    this.configuration = configuration;
    return this;
  }
  
}

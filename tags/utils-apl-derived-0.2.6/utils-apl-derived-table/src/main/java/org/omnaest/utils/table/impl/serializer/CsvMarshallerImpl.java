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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.table.Cell;
import org.omnaest.utils.table.Row;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.ImmutableTableSerializer.Marshaller;
import org.omnaest.utils.table.ImmutableTableSerializer.MarshallerCsv;

/**
 * {@link Marshaller} for csv
 * 
 * @author Omnaest
 * @param <E>
 */
class CsvMarshallerImpl<E> extends MarshallerAbstract<E> implements MarshallerCsv<E>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private CSVMarshallingConfiguration configuration = new CSVMarshallingConfiguration();
  
  /* *************************************************** Methods **************************************************** */
  
  public CsvMarshallerImpl( Table<E> table, ExceptionHandler exceptionHandler )
  {
    super( table, exceptionHandler );
  }
  
  @Override
  public Table<E> to( Appendable appendable )
  {
    //
    try
    {
      //
      if ( appendable != null )
      {
        //
        boolean hasColumnTitles = this.table.hasColumnTitles();
        boolean hasRowTitles = this.table.hasRowTitles();
        boolean hasTableName = this.table.hasTableName();
        
        //
        boolean processColumnTitles = this.configuration.hasEnabledColumnTitles() && hasColumnTitles;
        boolean processRowTitles = this.configuration.hasEnabledRowTitles() && hasRowTitles;
        boolean processTableName = this.configuration.hasEnabledTableName() && hasTableName;
        
        //
        if ( processTableName )
        {
          //
          appendable.append( String.valueOf( this.table.getTableName() ) + "\n" );
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
          for ( Object columnTitleValue : this.table.getColumnTitleList() )
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
        for ( Row<E> row : this.table.rows() )
        {
          //
          if ( processRowTitles )
          {
            //
            appendable.append( row.getTitle() );
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
      this.exceptionHandler.handleException( e );
    }
    
    // 
    return this.table;
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
  public MarshallerCsv<E> using( CSVMarshallingConfiguration configuration )
  {
    this.configuration = ObjectUtils.defaultIfNull( configuration, new CSVMarshallingConfiguration() );
    return this;
  }
  
  @Override
  protected String getEncoding()
  {
    return this.configuration.getEncoding();
  }
  
}

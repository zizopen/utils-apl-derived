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
package org.omnaest.utils.table2.impl.serializer;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.table2.ImmutableTableSerializer.MarshallerCsv.CSVMarshallingConfiguration;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TableSerializer.Unmarshaller;
import org.omnaest.utils.table2.TableSerializer.UnmarshallerCsv;

/**
 * @see Unmarshaller
 * @author Omnaest
 * @param <E>
 */
class CsvUnmarshallerImpl<E> extends UnmarshallerAbstract<E> implements UnmarshallerCsv<E>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private CSVMarshallingConfiguration configuration = new CSVMarshallingConfiguration();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * Special parser for text containing delimiters and quotations
   * 
   * @author Omnaest
   */
  public static class QuotationTextParser
  {
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private final char[] delimiter;
    private final char[] quotationCharacter;
    private int          offset = 0;
    private final char[] characters;
    
    /* *************************************************** Methods **************************************************** */
    public QuotationTextParser( String text, String delimiter, String quotationCharacter )
    {
      super();
      this.delimiter = delimiter.toCharArray();
      this.quotationCharacter = quotationCharacter.toCharArray();
      this.characters = text.toCharArray();
    }
    
    public String next()
    {
      // 
      StringBuilder retval = null;
      
      //
      boolean hasUnreadCharacters = determineHasUnreadCharacters();
      if ( hasUnreadCharacters )
      {
        //
        retval = new StringBuilder();
        
        //        
        final int quotationCharacterLength = this.quotationCharacter.length;
        final int delimiterCharacterLength = this.delimiter.length;
        
        //   
        boolean isWithinQuote = false;
        while ( determineHasUnreadCharacters() )
        {
          //
          final char[] nextCharsForQuotationMatching = this.determineNextChars( quotationCharacterLength );
          if ( nextCharsForQuotationMatching != null && Arrays.equals( this.quotationCharacter, nextCharsForQuotationMatching ) )
          {
            //
            final char[] nextCharsForDoubleQuotationMatching = this.determineNextChars( quotationCharacterLength,
                                                                                        quotationCharacterLength );
            if ( Arrays.equals( this.quotationCharacter, nextCharsForDoubleQuotationMatching ) )
            {
              //
              retval.append( this.quotationCharacter );
              this.forwardOffset( quotationCharacterLength * 2 );
            }
            else
            {
              //
              isWithinQuote = !isWithinQuote;
              this.forwardOffset( quotationCharacterLength );
            }
          }
          else
          {
            //
            final char[] nextCharsForDelimiterMatching = this.determineNextChars( delimiterCharacterLength );
            if ( !isWithinQuote && Arrays.equals( this.delimiter, nextCharsForDelimiterMatching ) )
            {
              //
              this.forwardOffset( delimiterCharacterLength );
              break;
            }
            
            //
            final char[] nextChar = this.determineNextChars( 1 );
            this.forwardOffset( 1 );
            retval.append( nextChar );
          }
        }
      }
      
      //
      return retval != null ? retval.toString() : null;
    }
    
    private boolean determineHasUnreadCharacters()
    {
      return this.offset < this.characters.length;
    }
    
    private void forwardOffset( int length )
    {
      this.offset += length;
    }
    
    private char[] determineNextChars( int length )
    {
      final int additionalOffsetDelta = 0;
      return this.determineNextChars( additionalOffsetDelta, length );
    }
    
    private char[] determineNextChars( int additionalOffsetDelta, int length )
    {
      //
      char[] retval = null;
      
      //
      final int startOffset = this.offset + additionalOffsetDelta;
      final int endOffset = startOffset + length;
      if ( endOffset <= this.characters.length )
      {
        retval = Arrays.copyOfRange( this.characters, startOffset, endOffset );
      }
      
      //
      return retval;
    }
  }
  
  /* *************************************************** Methods **************************************************** */
  
  public CsvUnmarshallerImpl( Table<E> table, ExceptionHandler exceptionHandler )
  {
    super( table, exceptionHandler );
  }
  
  @Override
  public Table<E> from( Reader reader )
  {
    //
    if ( reader != null )
    {
      //
      this.table.clear();
      
      //
      final Scanner scanner = new Scanner( reader );
      
      //
      if ( this.configuration.hasEnabledTableName() )
      {
        //
        String tableName = scanner.hasNextLine() ? scanner.nextLine() : null;
        if ( tableName != null )
        {
          this.table.setTableName( tableName );
        }
      }
      
      //
      if ( this.configuration.hasEnabledColumnTitles() )
      {
        //
        String columnLine = scanner.hasNextLine() ? scanner.nextLine() : null;
        if ( columnLine != null )
        {
          //
          String[] columnTokens = extractCellTokensFromLine( columnLine );
          if ( this.configuration.hasEnabledRowTitles() )
          {
            //
            columnTokens = ArrayUtils.remove( columnTokens, 0 );
          }
          
          //
          this.table.setColumnTitles( Arrays.asList( columnTokens ) );
        }
      }
      
      //
      int rowIndexPosition = 0;
      String line = scanner.hasNextLine() ? scanner.nextLine() : null;
      while ( line != null )
      {
        //
        String[] cellTokens = this.extractCellTokensFromLine( line );
        if ( this.configuration.hasEnabledRowTitles() && cellTokens.length > 0 )
        {
          //
          String rowTitleValue = cellTokens[0];
          this.table.setRowTitle( rowIndexPosition, rowTitleValue );
          
          //
          cellTokens = ArrayUtils.remove( cellTokens, 0 );
        }
        
        //
        for ( int columnIndexPosition = 0; columnIndexPosition < cellTokens.length; columnIndexPosition++ )
        {
          //
          try
          {
            //
            final String elementString = cellTokens[columnIndexPosition];
            final Class<E> elementType = this.table.elementType();
            final E element = ObjectUtils.castTo( elementType, elementString );
            this.table.setElement( rowIndexPosition, columnIndexPosition, element );
          }
          catch ( Exception e )
          {
            this.exceptionHandler.handleException( e );
          }
        }
        
        //
        line = scanner.hasNextLine() ? scanner.nextLine() : null;
        rowIndexPosition++;
      }
    }
    
    // 
    return this.table;
  }
  
  private String[] extractCellTokensFromLine( String line )
  {
    //
    String[] retvals = null;
    
    if ( line != null )
    {
      //
      if ( StringUtils.isNotEmpty( this.configuration.getQuotationCharacter() ) )
      {
        //
        final List<String> retlist = new ArrayList<String>();
        QuotationTextParser quotationTextParser = new QuotationTextParser( line, this.configuration.getDelimiter(),
                                                                           this.configuration.getQuotationCharacter() );
        for ( String next = null; ( next = quotationTextParser.next() ) != null; )
        {
          retlist.add( next );
        }
        retvals = retlist.toArray( new String[retlist.size()] );
      }
      else
      {
        retvals = StringUtils.splitPreserveAllTokens( line, this.configuration.getDelimiter() );
      }
    }
    
    //
    return retvals;
  }
  
  @Override
  public UnmarshallerCsv<E> using( CSVMarshallingConfiguration configuration )
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

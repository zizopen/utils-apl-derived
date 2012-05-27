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
package org.omnaest.utils.structure.table.serializer.unmarshaller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.serializer.TableUnmarshaller;
import org.omnaest.utils.structure.table.serializer.common.CSVConstants;
import org.omnaest.utils.structure.table.serializer.marshaller.TableMarshallerCSV;
import org.omnaest.utils.structure.table.subspecification.TableSerializable.TableSerializer;

/**
 * {@link TableUnmarshaller} for reading in CSV files. <br>
 * <br>
 * This implementation supports any delimiter and simple quotation rules. The quotation rules are that any cell containing at
 * least one quote character or delimiter will be enclosed in quotes and every containing quote character being duplicated.<br>
 * <br>
 * The performance is about 500ms per 1000 lines containing about 10 columns per line with text length < 10.
 * 
 * @see CSVConstants
 * @see TableMarshallerCSV
 * @see TableUnmarshaller
 * @author Omnaest
 * @param <E>
 */
public class TableUnmarshallerCSV<E> implements TableUnmarshaller<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID   = -1183646781295216284L;
  
  /* ********************************************** Variables ********************************************** */
  private String            encoding           = TableSerializer.DEFAULT_ENCODING_UTF8;
  private String            delimiter          = CSVConstants.DEFAULT_DELIMITER;
  private String            quotationCharacter = CSVConstants.DEFAULT_QUOTATION_CHARACTER;
  
  private boolean           hasTableName       = CSVConstants.DEFAULT_HAS_TABLE_NAME;
  private boolean           hasColumnTitles    = CSVConstants.DEFAULT_HAS_COLUMN_TITLES;
  private boolean           hasRowTitles       = CSVConstants.DEFAULT_HAS_ROW_TITLES;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * 
   */
  public TableUnmarshallerCSV()
  {
    super();
  }
  
  @Override
  public void unmarshal( Table<E> table, InputStream inputStream )
  {
    //
    if ( table != null && inputStream != null )
    {
      //
      ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      byteArrayContainer.copyFrom( inputStream );
      
      //
      this.unmarshal( table, byteArrayContainer.toString( this.encoding ) );
    }
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void unmarshal( Table<E> table, CharSequence charSequence )
  {
    //
    if ( charSequence != null && table != null )
    {
      //
      table.clear();
      
      //
      final Scanner scanner = new Scanner( String.valueOf( charSequence ) );
      
      //
      if ( this.hasTableName )
      {
        //
        String tableName = scanner.hasNextLine() ? scanner.nextLine() : null;
        if ( tableName != null )
        {
          //
          table.setTableName( tableName );
        }
      }
      
      //
      if ( this.hasColumnTitles )
      {
        //
        String columnLine = scanner.hasNextLine() ? scanner.nextLine() : null;
        if ( columnLine != null )
        {
          //
          String[] columnTokens = extractCellTokensFromLine( columnLine );
          if ( this.hasRowTitles )
          {
            //
            columnTokens = ArrayUtils.remove( columnTokens, 0 );
          }
          
          //
          table.setColumnTitleValues( Arrays.asList( columnTokens ) );
        }
      }
      
      //
      int rowIndexPosition = 0;
      String line = scanner.hasNextLine() ? scanner.nextLine() : null;
      while ( line != null )
      {
        //
        String[] cellTokens = this.extractCellTokensFromLine( line );
        if ( this.hasRowTitles && cellTokens.length > 0 )
        {
          //
          String rowTitleValue = cellTokens[0];
          table.setRowTitleValue( rowTitleValue, rowIndexPosition );
          
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
            String element = cellTokens[columnIndexPosition];
            
            //
            table.setCellElement( rowIndexPosition, columnIndexPosition, (E) element );
          }
          catch ( Exception e )
          {
          }
        }
        
        //
        line = scanner.hasNextLine() ? scanner.nextLine() : null;
        rowIndexPosition++;
      }
      
    }
  }
  
  /**
   * Special parser for text containing delimiters and quotations
   * 
   * @author Omnaest
   */
  protected class QuotationTextParser
  {
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    @SuppressWarnings("hiding")
    private final char[] delimiter;
    @SuppressWarnings("hiding")
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
  
  private String[] extractCellTokensFromLine( String line )
  {
    //
    String[] retvals = null;
    
    if ( line != null )
    {
      //
      if ( StringUtils.isNotEmpty( this.quotationCharacter ) )
      {
        //
        final List<String> retlist = new ArrayList<String>();
        QuotationTextParser quotationTextParser = new QuotationTextParser( line, this.delimiter, this.quotationCharacter );
        for ( String next = null; ( next = quotationTextParser.next() ) != null; )
        {
          retlist.add( next );
        }
        retvals = retlist.toArray( new String[retlist.size()] );
      }
      else
      {
        retvals = StringUtils.splitPreserveAllTokens( line, this.delimiter );
      }
    }
    
    //
    return retvals;
  }
  
  /**
   * @param encoding
   * @return this
   */
  public TableUnmarshallerCSV<E> setEncoding( String encoding )
  {
    this.encoding = encoding;
    return this;
  }
  
  /**
   * @see CSVConstants#DEFAULT_DELIMITER
   * @param delimiter
   * @return this
   */
  public TableUnmarshallerCSV<E> setDelimiter( String delimiter )
  {
    this.delimiter = delimiter;
    return this;
  }
  
  /**
   * @see CSVConstants#DEFAULT_QUOTATION_CHARACTER
   * @param quotationCharacter
   * @return this
   */
  public TableUnmarshallerCSV<E> setQuotationCharacter( String quotationCharacter )
  {
    this.quotationCharacter = quotationCharacter;
    return this;
  }
  
  /**
   * @param hasTableName
   * @return this
   */
  public TableUnmarshallerCSV<E> setHasTableName( boolean hasTableName )
  {
    this.hasTableName = hasTableName;
    return this;
  }
  
  /**
   * @param hasColumnTitles
   * @return this
   */
  public TableUnmarshallerCSV<E> setHasColumnTitles( boolean hasColumnTitles )
  {
    this.hasColumnTitles = hasColumnTitles;
    return this;
  }
  
  /**
   * @param hasRowTitles
   * @return this
   */
  public TableUnmarshallerCSV<E> setHasRowTitles( boolean hasRowTitles )
  {
    this.hasRowTitles = hasRowTitles;
    return this;
  }
  
}

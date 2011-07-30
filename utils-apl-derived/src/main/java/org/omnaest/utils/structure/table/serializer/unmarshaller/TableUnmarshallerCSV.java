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
import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.serializer.TableUnmarshaller;
import org.omnaest.utils.structure.table.serializer.marshaller.TableMarshallerCSV;
import org.omnaest.utils.structure.table.subspecification.TableSerializable.TableSerializer;

/**
 * @see TableMarshallerCSV
 * @see TableUnmarshaller
 * @author Omnaest
 * @param <E>
 */
public class TableUnmarshallerCSV<E> implements TableUnmarshaller<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long  serialVersionUID            = -1183646781295216284L;
  public final static String DEFAULT_DELIMITER_SEMICOLON = TableMarshallerCSV.DEFAULT_DELIMITER_SEMICOLON;
  
  /* ********************************************** Variables ********************************************** */
  protected String           encoding                    = TableSerializer.DEFAULT_ENCODING_UTF8;
  protected String           delimiter                   = DEFAULT_DELIMITER_SEMICOLON;
  
  protected boolean          hasTableName                = true;
  protected boolean          hasColumnTitles             = true;
  protected boolean          hasRowTitles                = true;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * 
   */
  public TableUnmarshallerCSV()
  {
    super();
  }
  
  /**
   * @param encoding
   */
  public TableUnmarshallerCSV( String encoding )
  {
    super();
    this.encoding = encoding;
  }
  
  /**
   * @param encoding
   * @param delimiter
   */
  public TableUnmarshallerCSV( String encoding, String delimiter )
  {
    super();
    this.encoding = encoding;
    this.delimiter = delimiter;
  }
  
  /**
   * @param encoding
   * @param delimiter
   * @param hasTableName
   * @param hasColumnTitles
   * @param hasRowTitles
   */
  public TableUnmarshallerCSV( String encoding, String delimiter, boolean hasTableName, boolean hasColumnTitles,
                               boolean hasRowTitles )
  {
    super();
    this.encoding = encoding;
    this.delimiter = delimiter;
    this.hasTableName = hasTableName;
    this.hasColumnTitles = hasColumnTitles;
    this.hasRowTitles = hasRowTitles;
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
      this.unmarshal( table, byteArrayContainer.toString() );
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
      StringBuilder stringBuilder = new StringBuilder( charSequence );
      Scanner scanner = new Scanner( stringBuilder.toString() );
      
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
          String[] columnTokens = StringUtils.splitPreserveAllTokens( columnLine, this.delimiter );
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
        String[] cellTokens = StringUtils.splitPreserveAllTokens( line, this.delimiter );
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
  
}

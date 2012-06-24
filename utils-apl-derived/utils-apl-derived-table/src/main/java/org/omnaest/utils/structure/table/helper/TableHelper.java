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
package org.omnaest.utils.structure.table.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.serializer.marshaller.TableMarshallerPlainText;

/**
 * Helper class offering several methods for {@link Table}
 * 
 * @author Omnaest
 */
public class TableHelper
{
  /* ********************************************** Constants ********************************************** */
  private static final String defaultCSVColumnSeparator = ";";
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Converts a array of given index positions into a sorted list.
   * 
   * @param indexPositions
   * @return
   */
  protected static List<Integer> determineIndexPositionList( int[] indexPositions )
  {
    //
    List<Integer> indexPositionList = new ArrayList<Integer>( indexPositions.length );
    
    //
    for ( int iRowIndexPosition : indexPositions )
    {
      indexPositionList.add( iRowIndexPosition );
    }
    
    //
    Collections.sort( indexPositionList );
    
    //
    return indexPositionList;
  }
  
  /**
   * Returns a table for a given csv text content.
   * 
   * @see #parseCSVContent(String, String)
   * @param content
   * @return
   */
  public static Table<String> parseCSVContent( String content )
  {
    return TableHelper.parseCSVContent( content, TableHelper.defaultCSVColumnSeparator );
  }
  
  /**
   * Returns a table for a given csv text content using the given column separator.
   * 
   * @see #parseCSVContent(String, String)
   * @param content
   * @param columnSeparator
   * @return
   */
  public static Table<String> parseCSVContent( String content, String columnSeparator )
  {
    //
    Table<String> table = new ArrayTable<String>();
    
    //
    if ( StringUtils.isNotBlank( content ) )
    {
      //
      int rowIndexPosition = 0;
      int columnIndexPosition = 0;
      
      //
      String[] lines = content.split( "[\r\n]+" );
      for ( String iLine : lines )
      {
        //
        String[] tokens = iLine.split( columnSeparator );
        for ( String iToken : tokens )
        {
          //
          table.setCellElement( rowIndexPosition, columnIndexPosition, iToken );
          
          //
          columnIndexPosition++;
        }
        
        //
        rowIndexPosition++;
        columnIndexPosition = 0;
      }
    }
    
    //
    return table;
  }
  
  /**
   * @see #renderAsCSVContent(Table, String)
   * @param <E>
   * @param table
   * @return
   */
  public static <E> String renderAsCSVContent( Table<E> table )
  {
    return TableHelper.renderAsCSVContent( table, TableHelper.defaultCSVColumnSeparator );
  }
  
  /**
   * Renders the content of a table as csv text using the given column separator.
   * 
   * @see #renderAsCSVContent(Table)
   * @param columnSeparator
   * @param table
   * @return
   */
  public static <E> String renderAsCSVContent( Table<E> table, String columnSeparator )
  {
    //
    StringBuilder content = new StringBuilder();
    
    //
    if ( table != null )
    {
      for ( int rowIndexPosition = 0; rowIndexPosition < table.getTableSize().getRowSize(); rowIndexPosition++ )
      {
        //
        if ( rowIndexPosition > 0 )
        {
          content.append( "\n" );
        }
        
        //
        for ( int columnIndexPosition = 0; columnIndexPosition < table.getTableSize().getColumnSize(); columnIndexPosition++ )
        {
          //
          if ( columnIndexPosition > 0 )
          {
            content.append( columnSeparator );
          }
          
          //
          String cellContent = String.valueOf( table.getCell( rowIndexPosition, columnIndexPosition ) );
          content.append( cellContent );
        }
      }
    }
    
    //
    return content.toString();
  }
  
  /**
   * Converts a given {@link Table} to a readable {@link String}
   * 
   * @param table
   * @return
   */
  public static <E> String renderToString( Table<E> table )
  {
    //
    StringBuffer stringBuffer = new StringBuffer();
    new TableMarshallerPlainText<E>().marshal( table, stringBuffer );
    return stringBuffer.toString();
  }
  
}

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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.omnaest.utils.strings.StringUtil;
import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.concrete.ArrayTable;

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
          table.setCell( rowIndexPosition, columnIndexPosition, iToken );
          
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
  
  public static <E> void printTable( Table<E> table )
  {
    //resolve the needed maximum width for every column separately
    List<Integer> columnWidthList = new ArrayList<Integer>( table.getTableSize().getColumnSize() );
    
    //if there are row titles, calculate their width values first
    if ( table.getRowTitleList().size() > 0 )
    {
      int maxWidth = 0;
      for ( String iTitle : table.getRowTitleList() )
      {
        //determine maxwidth
        int width = iTitle.length();
        if ( maxWidth < width )
        {
          maxWidth = width;
        }
      }
      columnWidthList.add( maxWidth );
    }
    //calculate data column width values
    for ( int columnIndexPosition = 0; columnIndexPosition < table.getTableSize().getColumnSize(); columnIndexPosition++ )
    {
      //
      List<E> iColumn = table.getColumn( columnIndexPosition );
      
      //title
      int columnTitleWidth = -1;
      String columnTitle = table.getColumnTitle( columnIndexPosition );
      if ( columnTitle != null )
      {
        columnTitleWidth = columnTitle.length();
      }
      
      //data
      int maxWidth = 0;
      for ( E iElement : iColumn )
      {
        int width = String.valueOf( iElement ).length();
        if ( maxWidth < width )
        {
          maxWidth = width;
        }
      }
      
      //
      if ( columnTitleWidth > maxWidth )
      {
        maxWidth = columnTitleWidth;
      }
      columnWidthList.add( maxWidth );
    }
    
    //
    String lineDelimiter = ".";
    String rowDelimiter = ":";
    String titleRowDelimiter = "!";
    
    //
    int printOutTableWidth = columnWidthList.size() + CollectionUtils.sumOfIntegerCollection( columnWidthList ) + 1;
    String lineRepeatedDelimiter = StringUtil.repeatString( printOutTableWidth, lineDelimiter );
    System.out.println( lineRepeatedDelimiter );
    
    //table name
    {
      String tableName = table.getTableName();
      if ( tableName != null )
      {
        String title = rowDelimiter + StringUtil.repeatString( printOutTableWidth - 2, " " ) + rowDelimiter;
        title = StringUtil.insertString( title, tableName, ( printOutTableWidth - tableName.length() ) / 2, true );
        System.out.println( title );
        System.out.println( lineRepeatedDelimiter );
      }
    }
    
    //columns and rows with titles
    for ( int ii = -1; ii < table.getTableSize().getRowSize(); ii++ )
    {
      //
      StringBuffer sb = new StringBuffer();
      int columnWidthListIndex = 0;
      
      //
      if ( ii == -1 && table.getColumnTitleList().size() > 0 )//add column title if available first
      {
        sb.append( titleRowDelimiter );
        
        if ( table.getRowTitleList().size() > 0 )
        {
          int columnWidth = columnWidthList.get( columnWidthListIndex++ );
          sb.append( StringUtil.setFixedWitdth( " ", columnWidth ) + titleRowDelimiter );
        }
        
        for ( String iColumnTitle : table.getColumnTitleList() )
        {
          int columnWidth = columnWidthList.get( columnWidthListIndex++ );
          sb.append( StringUtil.setFixedWitdth( iColumnTitle, columnWidth ) + titleRowDelimiter );
        }
        
      }
      else if ( ii >= 0 ) //data rows
      {
        sb.append( rowDelimiter );
        List<E> row = table.getRow( ii );
        
        //add column for the row
        for ( int jj = -1; jj < row.size(); jj++ )
        {
          if ( jj == -1 )
          {
            if ( table.getRowTitleList().size() > 0 )
            {
              //
              int columnWidth = columnWidthList.get( columnWidthListIndex++ );
              String rowTitle = table.getRowTitleList().size() > ii ? table.getRowTitleList().get( ii ) : " ";
              sb.append( StringUtil.setFixedWitdth( rowTitle, columnWidth ) + titleRowDelimiter );
            }
          }
          else if ( jj >= 0 ) //row cells
          {
            E element = row.get( jj );
            int columnWidth = columnWidthList.get( columnWidthListIndex++ );
            
            String elementValue = String.valueOf( element );
            sb.append( StringUtil.setFixedWitdth( elementValue, columnWidth ) + rowDelimiter );
          }
        }
        
      }
      
      if ( sb.length() > 0 )
      {
        System.out.println( sb.toString() );
      }
    }
    System.out.println( lineRepeatedDelimiter );
  }
}

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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
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
    /**
     * Converter for a table
     * 
     * @author Omnaest
     */
    class TableToStringConverter
    {
      /* ********************************************** Variables ********************************************** */
      @SuppressWarnings("hiding")
      protected Table<E> table = null;
      
      /* ********************************************** Methods ********************************************** */
      
      /**
       * @param table
       */
      public TableToStringConverter( Table<E> table )
      {
        super();
        this.table = table;
      }
      
      /**
       * Resolves the width of the row titles of the given table
       * 
       * @param table
       * @return
       */
      private Integer determineRowTitleWidth( Table<E> table )
      {
        //
        Integer retval = null;
        
        //
        for ( Row<E> row : table.rows() )
        {
          //
          int lengthMax = 0;
          
          //
          {
            String content = this.convertObjectContentToString( row.getTitle().getValue() );
            if ( content != null )
            {
              lengthMax = content.length();
            }
          }
          
          //
          retval = lengthMax;
        }
        
        //
        return retval;
      }
      
      /**
       * Resolves the meta data for a given table. This includes the width for each column
       * 
       * @param table
       * @return
       */
      private List<Integer> determineColumnWidthList( Table<E> table )
      {
        //
        List<Integer> retlist = new ArrayList<Integer>();
        
        //
        for ( Column<E> column : table.columns() )
        {
          //
          if ( column != null )
          {
            //
            int lengthMax = 0;
            
            //
            if ( column.getTitle() != null )
            {
              String content = this.convertObjectContentToString( column.getTitle().getValue() );
              if ( content != null )
              {
                lengthMax = content.length();
              }
            }
            
            //
            for ( Cell<E> cell : column )
            {
              //
              if ( cell != null )
              {
                //
                String content = this.convertObjectContentToString( cell.getElement() );
                if ( content != null )
                {
                  lengthMax = Math.max( lengthMax, content.length() );
                }
              }
            }
            
            //
            retlist.add( lengthMax );
          }
        }
        
        //
        return retlist;
      }
      
      /**
       * @param value
       * @return
       */
      private String convertObjectContentToString( Object value )
      {
        return value != null ? String.valueOf( value ) : "";
      }
      
      /**
       * Converts the table to a string representation
       * 
       * @return
       */
      public String convertTableToString()
      {
        //
        StringBuilder retval = new StringBuilder();
        
        //
        final String delimiterRow = "-";
        final String delimiterColumn = "|";
        final String delimiterTitleColumn = "!";
        final String delimiterTableTitle = "=";
        
        //
        List<Integer> columnWidthList = this.determineColumnWidthList( this.table );
        
        //
        boolean hasColumnTitles = this.table.hasColumnTitles();
        boolean hasRowTitles = this.table.hasRowTitles();
        boolean hasTableName = this.table.hasTableName();
        
        //
        int tableCharacterWidth = CollectionUtils.sumOfCollectionInteger( columnWidthList ) + columnWidthList.size() + 1;
        int rowTitlesCharacterWidth = 0;
        
        //
        if ( hasRowTitles )
        {
          tableCharacterWidth += ( rowTitlesCharacterWidth = this.determineRowTitleWidth( this.table ) ) + 1;
        }
        
        //
        if ( hasTableName )
        {
          //
          retval.append( StringUtils.center( this.convertObjectContentToString( this.table.getTableName() ), tableCharacterWidth,
                                             delimiterTableTitle ) + "\n" );
        }
        else
        {
          //
          retval.append( StringUtils.repeat( delimiterRow, tableCharacterWidth ) + "\n" );
        }
        
        //
        if ( hasColumnTitles )
        {
          //
          if ( hasRowTitles )
          {
            //
            retval.append( delimiterTitleColumn );
            retval.append( StringUtils.repeat( " ", rowTitlesCharacterWidth ) );
          }
          
          //
          Iterator<Integer> iteratorColumnWidthList = columnWidthList.iterator();
          for ( Column<E> column : this.table.columns() )
          {
            //
            retval.append( delimiterTitleColumn );
            
            //
            Object titleValue = column.getTitle().getValue();
            retval.append( StringUtils.center( this.convertObjectContentToString( titleValue ), iteratorColumnWidthList.next() ) );
          }
          
          //
          retval.append( delimiterTitleColumn + "\n" );
        }
        
        //
        for ( Row<E> row : this.table.rows() )
        {
          //
          if ( hasRowTitles )
          {
            //
            retval.append( delimiterTitleColumn );
            retval.append( StringUtils.center( this.convertObjectContentToString( row.getTitle().getValue() ),
                                               rowTitlesCharacterWidth ) );
            retval.append( delimiterTitleColumn );
          }
          else
          {
            //
            retval.append( delimiterColumn );
          }
          
          //
          Iterator<Integer> iteratorColumnWidthList = columnWidthList.iterator();
          for ( Cell<E> cell : row.cells() )
          {
            //
            if ( iteratorColumnWidthList.hasNext() )
            {
              //            
              retval.append( StringUtils.center( this.convertObjectContentToString( cell != null ? cell.getElement() : null ),
                                                 iteratorColumnWidthList.next() ) );
              //
              retval.append( delimiterColumn );
            }
          }
          
          //
          retval.append( "\n" );
        }
        
        //
        retval.append( StringUtils.repeat( delimiterRow, tableCharacterWidth ) + "\n" );
        
        //        
        return retval.toString();
      }
    }
    
    //
    return new TableToStringConverter( table ).convertTableToString();
  }
  
}

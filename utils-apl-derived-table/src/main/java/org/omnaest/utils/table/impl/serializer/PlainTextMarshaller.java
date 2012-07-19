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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.table.Column;
import org.omnaest.utils.table.Row;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.ImmutableTableSerializer.Marshaller;
import org.omnaest.utils.table.ImmutableTableSerializer.MarshallerPlainText;

/**
 * {@link Marshaller} for plain text
 * 
 * @author Omnaest
 * @param <E>
 */
class PlainTextMarshaller<E> extends MarshallerAbstract<E> implements MarshallerPlainText<E>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private MarshallingConfiguration configuration = new MarshallingConfiguration();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Converter for a table
   * 
   * @author Omnaest
   */
  private class TableToStringConverter
  {
    /* ********************************************** Variables ********************************************** */
    @SuppressWarnings("hiding")
    private final Table<E> table;
    
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
          String content = this.convertObjectContentToString( row.getTitle() );
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
          final String columnTitle = column.getTitle();
          if ( columnTitle != null )
          {
            String content = this.convertObjectContentToString( columnTitle );
            if ( content != null )
            {
              lengthMax = content.length();
            }
          }
          
          //
          for ( E element : column )
          {
            //
            if ( element != null )
            {
              //
              String content = this.convertObjectContentToString( element );
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
     */
    public void appendTableTo( Appendable appendable )
    {
      //
      try
      {
        
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
          appendable.append( StringUtils.center( this.convertObjectContentToString( this.table.getTableName() ),
                                                 tableCharacterWidth, delimiterTableTitle ) + "\n" );
        }
        else
        {
          //
          appendable.append( StringUtils.repeat( delimiterRow, tableCharacterWidth ) + "\n" );
        }
        
        //
        if ( hasColumnTitles )
        {
          //
          if ( hasRowTitles )
          {
            //
            appendable.append( delimiterTitleColumn );
            appendable.append( StringUtils.repeat( " ", rowTitlesCharacterWidth ) );
          }
          
          //
          Iterator<Integer> iteratorColumnWidthList = columnWidthList.iterator();
          for ( Column<E> column : this.table.columns() )
          {
            //
            appendable.append( delimiterTitleColumn );
            
            //
            String titleValue = column.getTitle();
            appendable.append( StringUtils.center( this.convertObjectContentToString( titleValue ),
                                                   iteratorColumnWidthList.next() ) );
          }
          
          //
          appendable.append( delimiterTitleColumn + "\n" );
        }
        
        //
        for ( Row<E> row : this.table.rows() )
        {
          //
          if ( hasRowTitles )
          {
            //
            appendable.append( delimiterTitleColumn );
            appendable.append( StringUtils.center( this.convertObjectContentToString( row.getTitle() ), rowTitlesCharacterWidth ) );
            appendable.append( delimiterTitleColumn );
          }
          else
          {
            //
            appendable.append( delimiterColumn );
          }
          
          //
          Iterator<Integer> iteratorColumnWidthList = columnWidthList.iterator();
          for ( E element : row )
          {
            //
            if ( iteratorColumnWidthList.hasNext() )
            {
              //            
              appendable.append( StringUtils.center( this.convertObjectContentToString( element ), iteratorColumnWidthList.next() ) );
              //
              appendable.append( delimiterColumn );
            }
          }
          
          //
          appendable.append( "\n" );
        }
        
        //
        appendable.append( StringUtils.repeat( delimiterRow, tableCharacterWidth ) + "\n" );
        
      }
      catch ( Exception e )
      {
        PlainTextMarshaller.this.exceptionHandler.handleException( e );
      }
    }
  }
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see PlainTextMarshaller
   * @param table
   * @param exceptionHandler
   */
  @SuppressWarnings("javadoc")
  public PlainTextMarshaller( Table<E> table, ExceptionHandler exceptionHandler )
  {
    super( table, exceptionHandler );
  }
  
  @Override
  public Table<E> to( Appendable appendable )
  {
    //
    if ( appendable != null )
    {
      new TableToStringConverter( this.table ).appendTableTo( appendable );
    }
    
    return this.table;
  }
  
  @Override
  public MarshallerPlainText<E> using( MarshallingConfiguration configuration )
  {
    this.configuration = ObjectUtils.defaultIfNull( configuration, new MarshallingConfiguration() );
    return this;
  }
  
  @Override
  protected String getEncoding()
  {
    return this.configuration.getEncoding();
  }
  
}

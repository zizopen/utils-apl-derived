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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.serializer.TableMarshaller;
import org.omnaest.utils.structure.table.subspecification.TableSerializable.TableSerializer;

/**
 * @see TableMarshaller
 * @author Omnaest
 * @param <E>
 */
public class TableMarshallerPlainText<E> implements TableMarshaller<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 729579410301748875L;
  
  /* ********************************************** Variables ********************************************** */
  protected String          encoding         = TableSerializer.DEFAULT_ENCODING_UTF8;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * Converter for a table
   * 
   * @author Omnaest
   */
  protected class TableToStringConverter
  {
    /* ********************************************** Variables ********************************************** */
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
            Object titleValue = column.getTitle().getValue();
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
            appendable.append( StringUtils.center( this.convertObjectContentToString( row.getTitle().getValue() ),
                                                   rowTitlesCharacterWidth ) );
            appendable.append( delimiterTitleColumn );
          }
          else
          {
            //
            appendable.append( delimiterColumn );
          }
          
          //
          Iterator<Integer> iteratorColumnWidthList = columnWidthList.iterator();
          for ( Cell<E> cell : row.cells() )
          {
            //
            if ( iteratorColumnWidthList.hasNext() )
            {
              //            
              appendable.append( StringUtils.center( this.convertObjectContentToString( cell != null ? cell.getElement() : null ),
                                                     iteratorColumnWidthList.next() ) );
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
      }
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * 
   */
  public TableMarshallerPlainText()
  {
    super();
  }
  
  /**
   * @param encoding
   */
  public TableMarshallerPlainText( String encoding )
  {
    super();
    this.encoding = encoding;
  }
  
  @Override
  public void marshal( Table<E> table, OutputStream outputStream )
  {
    //
    if ( table != null && outputStream != null )
    {
      //
      StringBuffer stringBuffer = new StringBuffer();
      
      //
      this.marshal( table, stringBuffer );
      
      //
      ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      byteArrayContainer.copyFrom( stringBuffer );
      
      //
      byteArrayContainer.writeTo( outputStream );
    }
  }
  
  @Override
  public void marshal( Table<E> table, Appendable appendable )
  {
    //
    if ( table != null && appendable != null )
    {
      //      
      new TableToStringConverter( table ).appendTableTo( appendable );
    }
    
  }
  
}

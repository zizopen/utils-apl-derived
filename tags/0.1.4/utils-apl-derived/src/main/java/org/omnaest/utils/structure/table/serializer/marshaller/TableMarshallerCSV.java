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

import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.serializer.TableMarshaller;
import org.omnaest.utils.structure.table.serializer.unmarshaller.TableUnmarshallerCSV;
import org.omnaest.utils.structure.table.subspecification.TableSerializable.TableSerializer;

/**
 * @see TableMarshaller
 * @see TableUnmarshallerCSV
 * @author Omnaest
 * @param <E>
 */
public class TableMarshallerCSV<E> implements TableMarshaller<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long  serialVersionUID            = 729579410301748875L;
  public final static String DEFAULT_DELIMITER_SEMICOLON = ";";
  
  /* ********************************************** Variables ********************************************** */
  protected String           encoding                    = TableSerializer.DEFAULT_ENCODING_UTF8;
  protected String           delimiter                   = DEFAULT_DELIMITER_SEMICOLON;
  
  protected boolean          writeTableName              = true;
  protected boolean          writeColumnTitles           = true;
  protected boolean          writeRowTiles               = true;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * 
   */
  public TableMarshallerCSV()
  {
    super();
  }
  
  /**
   * @param encoding
   */
  public TableMarshallerCSV( String encoding )
  {
    super();
    this.encoding = encoding;
  }
  
  /**
   * @param encoding
   * @param delimiter
   */
  public TableMarshallerCSV( String encoding, String delimiter )
  {
    super();
    this.encoding = encoding;
    this.delimiter = delimiter;
  }
  
  /**
   * @param writeTableName
   * @param writeColumnTitles
   * @param writeRowTiles
   */
  public TableMarshallerCSV( boolean writeTableName, boolean writeColumnTitles, boolean writeRowTiles )
  {
    super();
    this.writeTableName = writeTableName;
    this.writeColumnTitles = writeColumnTitles;
    this.writeRowTiles = writeRowTiles;
  }
  
  /**
   * @param encoding
   * @param delimiter
   * @param writeTableName
   * @param writeColumnTitles
   * @param writeRowTiles
   */
  public TableMarshallerCSV( String encoding, String delimiter, boolean writeTableName, boolean writeColumnTitles,
                             boolean writeRowTiles )
  {
    super();
    this.encoding = encoding;
    this.delimiter = delimiter;
    this.writeTableName = writeTableName;
    this.writeColumnTitles = writeColumnTitles;
    this.writeRowTiles = writeRowTiles;
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
      byteArrayContainer.copyFrom( stringBuffer, this.encoding );
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
        boolean processColumnTitles = this.writeColumnTitles && hasColumnTitles;
        boolean processRowTitles = this.writeRowTiles && hasRowTitles;
        boolean processTableName = this.writeTableName && hasTableName;
        
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
            appendable.append( this.delimiter );
          }
          
          //
          boolean first = true;
          for ( Object columnTitleValue : table.getColumnTitleValueList() )
          {
            //
            appendable.append( !first ? this.delimiter : "" );
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
            appendable.append( this.delimiter );
          }
          
          //
          boolean first = true;
          for ( Cell<E> cell : row.cells() )
          {
            //
            appendable.append( !first ? this.delimiter : "" );
            appendable.append( String.valueOf( cell.getElement() ) );
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
  
  @Override
  public void marshal( Table<E> table, InputStream inputStream, OutputStream outputStream )
  {
    throw new UnsupportedOperationException();
  }
  
}

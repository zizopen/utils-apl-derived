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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.serializer.TableMarshaller;
import org.omnaest.utils.structure.table.serializer.unmarshaller.TableUnmarshallerXLS;

/**
 * @see TableMarshaller
 * @see TableUnmarshallerXLS
 * @author Omnaest
 * @param <E>
 */
public class TableMarshallerXLS<E> implements TableMarshaller<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID  = 729579410301748875L;
  
  /* ********************************************** Variables ********************************************** */
  protected String          workSheetName     = TableUnmarshallerXLS.WORKSHEET_NAME_DEFAULT;
  protected boolean         writeTableName    = true;
  protected boolean         writeColumnTitles = true;
  protected boolean         writeRowTitles    = true;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * 
   */
  public TableMarshallerXLS()
  {
    super();
  }
  
  /**
   * @param workSheetName
   */
  public TableMarshallerXLS( String workSheetName )
  {
    super();
    this.workSheetName = workSheetName;
  }
  
  /**
   * @param writeTableName
   * @param writeColumnTitles
   * @param writeRowTiles
   */
  public TableMarshallerXLS( boolean writeTableName, boolean writeColumnTitles, boolean writeRowTiles )
  {
    super();
    this.writeTableName = writeTableName;
    this.writeColumnTitles = writeColumnTitles;
    this.writeRowTitles = writeRowTiles;
  }
  
  /**
   * @param workSheetName
   * @param writeTableName
   * @param writeColumnTitles
   * @param writeRowTitles
   */
  public TableMarshallerXLS( String workSheetName, boolean writeTableName, boolean writeColumnTitles, boolean writeRowTitles )
  {
    super();
    this.workSheetName = workSheetName;
    this.writeTableName = writeTableName;
    this.writeColumnTitles = writeColumnTitles;
    this.writeRowTitles = writeRowTitles;
  }
  
  @Override
  public void marshal( Table<E> table, InputStream inputStream, OutputStream outputStream )
  {
    //
    if ( table != null && outputStream != null )
    {
      //
      Workbook workbook = null;
      if ( inputStream != null )
      {
        try
        {
          //
          workbook = new HSSFWorkbook( new POIFSFileSystem( inputStream ) );
        }
        catch ( IOException e )
        {
          e.printStackTrace();
        }
      }
      if ( workbook == null )
      {
        workbook = new HSSFWorkbook();
      }
      
      // 
      Sheet sheet = workbook.createSheet( this.workSheetName );
      
      //
      int rowIndexPosition = 0;
      
      //
      if ( this.writeTableName )
      {
        //
        try
        {
          //
          Object tableName = table.getTableName();
          
          //
          Row row = sheet.createRow( rowIndexPosition++ );
          Cell cell = row.createCell( 0 );
          cell.setCellValue( String.valueOf( tableName ) );
        }
        catch ( Exception e )
        {
        }
      }
      
      //
      if ( this.writeColumnTitles )
      {
        //
        Row row = sheet.createRow( rowIndexPosition++ );
        int columnIndexPosition = 0;
        
        //
        if ( this.writeRowTitles )
        {
          row.createCell( columnIndexPosition++ );
        }
        
        //
        List<Object> columnTitleValueList = table.getColumnTitleValueList();
        for ( Object titleValue : columnTitleValueList )
        {
          try
          {
            //
            Cell cell = row.createCell( columnIndexPosition++ );
            TableMarshallerXLS.setCellValue( cell, titleValue );
          }
          catch ( Exception e )
          {
          }
        }
      }
      
      //
      for ( Table.Row<E> tableRow : table )
      {
        //
        Row row = sheet.createRow( rowIndexPosition++ );
        int columnIndexPosition = 0;
        
        //
        if ( this.writeRowTitles )
        {
          try
          {
            //
            Object titleValue = tableRow.getTitleValue();
            Cell cell = row.createCell( columnIndexPosition++ );
            cell.setCellValue( String.valueOf( titleValue ) );
          }
          catch ( Exception e )
          {
          }
        }
        
        //
        for ( Table.Cell<E> tableCell : tableRow )
        {
          //
          Cell cell = row.createCell( columnIndexPosition++ );
          
          //
          E element = tableCell.getElement();
          TableMarshallerXLS.setCellValue( cell, element );
        }
        
      }
      
      try
      {
        workbook.write( outputStream );
        outputStream.close();
      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }
    }
  }
  
  @Override
  public void marshal( Table<E> table, OutputStream outputStream )
  {
    InputStream inputStream = null;
    this.marshal( table, inputStream, outputStream );
  }
  
  /**
   * @param cell
   * @param element
   */
  private static void setCellValue( Cell cell, Object element )
  {
    try
    {
      if ( cell != null )
      {
        if ( element instanceof Number )
        {
          cell.setCellType( Cell.CELL_TYPE_NUMERIC );
          cell.setCellValue( ( (Number) element ).doubleValue() );
        }
        else if ( element instanceof Boolean )
        {
          cell.setCellType( Cell.CELL_TYPE_BOOLEAN );
          cell.setCellValue( (Boolean) element );
        }
        else
        {
          cell.setCellType( Cell.CELL_TYPE_STRING );
          cell.setCellValue( String.valueOf( element ) );
        }
      }
    }
    catch ( Exception e )
    {
    }
  }
  
  @Override
  public void marshal( Table<E> table, Appendable appendable )
  {
    throw new UnsupportedOperationException();
  }
  
}

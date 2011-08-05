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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.serializer.TableUnmarshaller;
import org.omnaest.utils.structure.table.serializer.marshaller.TableMarshallerXLS;

/**
 * @see TableMarshallerXLS
 * @see TableUnmarshaller
 * @author Omnaest
 * @param <E>
 */
public class TableUnmarshallerXLS<E> implements TableUnmarshaller<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -1183646781295216284L;
  
  /* ********************************************** Variables ********************************************** */
  
  protected boolean         hasTableName     = true;
  protected boolean         hasColumnTitles  = true;
  protected boolean         hasRowTitles     = true;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * 
   */
  public TableUnmarshallerXLS()
  {
    super();
  }
  
  /**
   * @param hasTableName
   * @param hasColumnTitles
   * @param hasRowTitles
   */
  public TableUnmarshallerXLS( boolean hasTableName, boolean hasColumnTitles, boolean hasRowTitles )
  {
    super();
    this.hasTableName = hasTableName;
    this.hasColumnTitles = hasColumnTitles;
    this.hasRowTitles = hasRowTitles;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void unmarshal( Table<E> table, InputStream inputStream )
  {
    //
    if ( table != null && inputStream != null )
    {
      //
      try
      {
        //
        table.clear();
        
        //
        HSSFWorkbook wb = new HSSFWorkbook( new POIFSFileSystem( inputStream ) );
        Sheet sheet = wb.getSheetAt( 0 );
        
        //
        if ( this.hasTableName )
        {
          table.setTableName( sheet.getSheetName() );
        }
        
        //
        int rowIndexPosition = 0;
        for ( Row iRow : sheet )
        {
          //
          int columnIndexPosition = 0;
          for ( Cell iCell : iRow )
          {
            //
            E element = null;
            
            //
            int cellType = iCell.getCellType();
            if ( Cell.CELL_TYPE_BOOLEAN == cellType )
            {
              element = (E) Boolean.valueOf( iCell.getBooleanCellValue() );
            }
            else if ( Cell.CELL_TYPE_NUMERIC == cellType )
            {
              element = (E) Double.valueOf( iCell.getNumericCellValue() );
            }
            else if ( Cell.CELL_TYPE_STRING == cellType )
            {
              element = (E) iCell.getStringCellValue();
            }
            
            //
            int rowIndexPositionCorrected = rowIndexPosition + ( this.hasColumnTitles ? -1 : 0 );
            int columnIndexPositionCorrected = columnIndexPosition + ( this.hasRowTitles ? -1 : 0 );
            
            //
            if ( this.hasColumnTitles && rowIndexPosition == 0 )
            {
              if ( !this.hasRowTitles || columnIndexPosition > 0 )
              {
                table.setColumnTitleValue( element, columnIndexPositionCorrected );
              }
            }
            else
            {
              //
              if ( this.hasRowTitles && columnIndexPosition == 0 )
              {
                table.setRowTitleValue( element, rowIndexPositionCorrected );
              }
              else
              {
                table.setCellElement( rowIndexPositionCorrected, columnIndexPositionCorrected, element );
              }
            }
            
            //
            columnIndexPosition++;
          }
          
          //
          rowIndexPosition++;
        }
      }
      catch ( FileNotFoundException e )
      {
        e.printStackTrace();
      }
      catch ( IOException e )
      {
        e.printStackTrace();
      }
    }
  }
  
  @Override
  public void unmarshal( Table<E> table, CharSequence charSequence )
  {
    throw new UnsupportedOperationException();
  }
  
}

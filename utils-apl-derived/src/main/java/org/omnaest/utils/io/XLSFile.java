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
package org.omnaest.utils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Simple representation of an XLS file of Microsoft Excel.
 * 
 * @author Omnaest
 */
public class XLSFile
{
  /* ********************************************** Constants ********************************************** */
  private static final long   serialVersionUID  = 4924867114503312907L;
  private static final String MAINSHEETPAGENAME = "all";
  private static final String FILESUFFIX        = ".xls";
  
  /* ********************************************** Variables ********************************************** */
  private List<TableRow>      tableRowList      = new ArrayList<TableRow>();
  
  protected File              file              = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */

  /**
   * Representation of a row.
   */
  public static class TableRow extends ArrayList<String>
  {
    private static final long serialVersionUID = 4599939864378182879L;
    
    public TableRow()
    {
      super();
    }
    
    public TableRow( Collection<? extends String> valueCollection )
    {
      super( valueCollection );
    }
    
    public TableRow( String... values )
    {
      this( Arrays.asList( values ) );
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */

  /**
   * Creates a unlinked instance. The underlying file has to be set before invoking {@link XLSFile#load()} or
   * {@link XLSFile#store()} methods.
   * 
   * @see XLSFile#setFile(File)
   */
  public XLSFile()
  {
  }
  
  /**
   * @param file
   */
  public XLSFile( File file )
  {
    this.file = file;
  }
  
  /**
   * Loads the data from the disk into this object.
   */
  public void load()
  {
    try
    {
      //
      InputStream inp = new FileInputStream( this.file );
      HSSFWorkbook wb = new HSSFWorkbook( new POIFSFileSystem( inp ) );
      Sheet sheet = wb.getSheet( MAINSHEETPAGENAME );
      
      //
      this.clear();
      for ( Row iRow : sheet )
      {
        //
        TableRow newTableRow = new TableRow();
        
        //
        for ( Cell iCell : iRow )
        {
          newTableRow.add( iCell.getStringCellValue() );
        }
        
        //
        this.tableRowList.add( newTableRow );
      }
      
      //
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
  
  /**
   * Stores the data from the object onto disk.
   */
  public void store()
  {
    Workbook wb = new HSSFWorkbook();
    CreationHelper createHelper = wb.getCreationHelper();
    Sheet sheet = wb.createSheet( "all" );
    
    int lineNumber = 0;
    for ( TableRow iLine : this.tableRowList )
    {
      //
      Row row = sheet.createRow( lineNumber++ );
      
      //
      int cellIndex = 0;
      for ( String iCellText : iLine )
      {
        Cell cell = row.createCell( cellIndex++ );
        cell.setCellValue( createHelper.createRichTextString( iCellText ) );
      }
    }
    
    try
    {
      FileOutputStream fileOut = new FileOutputStream( this.file );
      wb.write( fileOut );
      fileOut.close();
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
  
  public static boolean isXLSFile( File file )
  {
    //
    boolean retval = false;
    
    //
    retval = ( file != null ) && file.exists() && file.isFile() && file.getAbsolutePath().toLowerCase().endsWith( FILESUFFIX );
    
    //
    return retval;
  }
  
  public List<TableRow> getTableRowList()
  {
    return tableRowList;
  }
  
  public File getFile()
  {
    return this.file;
  }
  
  public void setFile( File file )
  {
    this.file = file;
  }
  
  public void clear()
  {
    this.tableRowList.clear();
  }
  
}

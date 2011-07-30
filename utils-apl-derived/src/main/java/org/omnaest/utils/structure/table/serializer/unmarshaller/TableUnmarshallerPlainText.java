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
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.serializer.TableUnmarshaller;

/**
 * @see TableUnmarshaller
 * @author Omnaest
 * @param <E>
 */
public class TableUnmarshallerPlainText<E> implements TableUnmarshaller<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -6885465933583394486L;
  
  /* ********************************************** Methods ********************************************** */
  @Override
  public void unmarshal( Table<E> table, InputStream inputStream )
  {
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    byteArrayContainer.copyFrom( inputStream );
    
    //
    this.unmarshal( table, byteArrayContainer.toString() );
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void unmarshal( Table<E> table, CharSequence charSequence )
  {
    /*
    ===Table1===
    !  !c0 !c1 !
    !r0!0:0|0:1|
    !r1!1:0|1:1|
    !r2!2:0|2:1|
    ------------
    */
    
    //
    final String delimiterRow = "-";
    final String delimiterColumn = "|";
    final String delimiterTitleColumn = "!";
    final String delimiterTableTitle = "=";
    
    //
    if ( table != null )
    {
      //
      table.clear();
      
      //
      StringBuilder stringBuilder = new StringBuilder( charSequence );
      Scanner scanner = new Scanner( stringBuilder.toString() );
      
      //
      {
        //
        String firstLine = scanner.hasNextLine() ? scanner.nextLine() : null;
        if ( firstLine != null && firstLine.startsWith( delimiterTableTitle ) )
        {
          //
          String tableName = firstLine.replaceAll( delimiterTableTitle, "" );
          table.setTableName( tableName );
        }
      }
      
      //
      boolean hasRowTitles = false;
      String line = scanner.hasNextLine() ? scanner.nextLine() : null;
      if ( line != null && line.startsWith( delimiterTitleColumn ) && line.endsWith( delimiterTitleColumn ) )
      {
        //
        String[] columnTokens = StringUtils.splitPreserveAllTokens( line, delimiterTitleColumn );
        if ( columnTokens.length > 1 )
        {
          //
          columnTokens = ArrayUtils.remove( columnTokens, columnTokens.length - 1 );
          columnTokens = ArrayUtils.remove( columnTokens, 0 );
        }
        
        //
        line = scanner.hasNextLine() ? scanner.nextLine() : null;
        
        //
        if ( line != null && line.startsWith( delimiterTitleColumn ) )
        {
          //
          if ( columnTokens.length > 0 )
          {
            //
            columnTokens = ArrayUtils.remove( columnTokens, 0 );
          }
        }
        
        //
        columnTokens = org.omnaest.utils.structure.array.ArrayUtils.trimStringArrayTokens( columnTokens );
        
        //
        table.setColumnTitleValues( Arrays.asList( columnTokens ) );
      }
      
      //
      hasRowTitles = line != null && line.startsWith( delimiterTitleColumn );
      
      //
      int rowIndexPosition = 0;
      while ( line != null )
      {
        //
        if ( !line.startsWith( delimiterRow ) )
        {
          //
          String[] cellTokens = StringUtils.splitPreserveAllTokens( line, delimiterColumn );
          if ( cellTokens.length > 0 )
          {
            //
            cellTokens = ArrayUtils.remove( cellTokens, cellTokens.length - 1 );
          }
          
          //
          if ( cellTokens.length > 0 && hasRowTitles )
          {
            //
            String[] firstCellTokens = StringUtils.splitPreserveAllTokens( cellTokens[0], delimiterTitleColumn );
            
            //
            firstCellTokens = org.omnaest.utils.structure.array.ArrayUtils.trimStringArrayTokens( firstCellTokens );
            
            //
            if ( firstCellTokens.length >= 2 )
            {
              //
              String titleValue = firstCellTokens[1];
              
              //
              table.setRowTitleValue( titleValue, rowIndexPosition );
            }
            
            //
            cellTokens[0] = "";
            if ( firstCellTokens.length >= 3 )
            {
              cellTokens[0] = firstCellTokens[2];
            }
          }
          else if ( cellTokens.length > 0 )
          {
            //
            cellTokens = ArrayUtils.remove( cellTokens, 0 );
          }
          
          //
          cellTokens = org.omnaest.utils.structure.array.ArrayUtils.trimStringArrayTokens( cellTokens );
          
          //
          table.setRowCellElements( rowIndexPosition++, (List<? extends E>) Arrays.asList( cellTokens ) );
        }
        
        //
        line = scanner.hasNextLine() ? scanner.nextLine() : null;
      }
      
    }
    
  }
  
}

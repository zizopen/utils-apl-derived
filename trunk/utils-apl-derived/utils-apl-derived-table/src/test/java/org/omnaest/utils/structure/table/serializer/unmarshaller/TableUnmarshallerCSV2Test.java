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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.concrete.ArrayTable;

/**
 * @see TableUnmarshallerCSV
 * @author Omnaest
 */
public class TableUnmarshallerCSV2Test
{
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testExtractCellTokensFromLine() throws Exception
  {
    //
    String text = "\"a;\";\"b\";\nc;\"d\"\"\"e";
    
    //
    Table<String> table = new ArrayTable<String>();
    new TableUnmarshallerCSV<String>().setHasTableName( false )
                                      .setHasColumnTitles( false )
                                      .setHasRowTitles( false )
                                      .unmarshal( table, text );
    
    //
    String[][] array = table.as().array( String.class );
    assertArrayEquals( new String[][] { new String[] { "a;", "b" }, new String[] { "c", "d\"e" } }, array );
    
  }
  
  @Test
  @Ignore("Performance test")
  public void testLoadPerformance()
  {
    //
    final int rows = 1000;
    final int columns = 10;
    String csvText = generateCSVText( rows, columns );
    
    //
    Table<String> table = new ArrayTable<String>();
    new TableUnmarshallerCSV<String>().setHasTableName( false )
                                      .setHasColumnTitles( false )
                                      .setHasRowTitles( false )
                                      .unmarshal( table, csvText );
    
    //
    //System.out.println( table );
    
    //
    assertEquals( rows, table.getRowList().size() );
    assertEquals( columns, table.getColumnList().size() );
  }
  
  private static String generateCSVText( int rows, int columns )
  {
    //
    final StringBuilder retval = new StringBuilder();
    
    //
    for ( int ii = 0; ii < rows; ii++ )
    {
      retval.append( ii > 0 ? "\n" : "" );
      for ( int jj = 0; jj < columns; jj++ )
      {
        //
        retval.append( jj > 0 ? ";" : "" );
        retval.append( "" + Math.round( Math.random() * 1000 ) );
      }
    }
    
    //
    return retval.toString();
  }
}

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
package org.omnaest.utils.table2;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import org.junit.Test;
import org.omnaest.utils.structure.array.ArrayUtils;

/**
 * @see Table
 * @author Omnaest
 */
public abstract class TableTest
{
  public abstract <E> Table<E> newTable( E[][] elementMatrix, Class<E> type );
  
  @Test
  public void testIterator()
  {
    //
    final String[][] elementMatrix = new String[][] { { "a", "b", "c" }, { "d", "e", "f" } };
    Table<String> tableAbstract = this.newTable( elementMatrix, String.class );
    
    //
    Iterator<ImmutableRow<String>> iterator = tableAbstract.iterator();
    assertNotNull( iterator );
    assertTrue( iterator.hasNext() );
    assertTrue( iterator.hasNext() );
    assertArrayEquals( elementMatrix[0], ArrayUtils.valueOf( iterator.next(), String.class ) );
    assertTrue( iterator.hasNext() );
    assertArrayEquals( elementMatrix[1], ArrayUtils.valueOf( iterator.next(), String.class ) );
    assertFalse( iterator.hasNext() );
    
  }
  
  @Test
  public void testTo() throws Exception
  {
    final String[][] elementMatrix = new String[][] { { "a", "b", "c" }, { "d", "e", "f" } };
    Table<String> table = this.newTable( elementMatrix, String.class );
    
    final String[][] array = table.to().array();
    assertArrayEquals( elementMatrix, array );
  }
  
  protected Table<String> filledTableWithTitles( int rowSize, int columnSize )
  {
    final Table<String> table = this.filledTable( rowSize, columnSize );
    
    table.setTableName( "table name" );
    
    List<String> columnTitleList = new ArrayList<String>();
    for ( int ii = 0; ii < columnSize; ii++ )
    {
      columnTitleList.add( "c" + ii );
    }
    table.setColumnTitles( columnTitleList );
    
    List<String> rowTitleList = new ArrayList<String>();
    for ( int ii = 0; ii < rowSize; ii++ )
    {
      rowTitleList.add( "r" + ii );
    }
    table.setRowTitles( rowTitleList );
    
    return table;
  }
  
  protected Table<String> filledTable( int rowSize, int columnSize )
  {
    String[][] elementMatrix = new String[rowSize][columnSize];
    for ( int ii = 0; ii < rowSize; ii++ )
    {
      for ( int jj = 0; jj < columnSize; jj++ )
      {
        elementMatrix[ii][jj] = ii + ":" + jj;
      }
    }
    return this.newTable( elementMatrix, String.class );
  }
  
  @Test
  public void testSelect() throws Exception
  {
    Table<String> table = this.filledTableWithTitles( 20, 4 );
    Table<String> table2 = this.filledTableWithTitles( 10, 8 );
    Table<String> table3 = this.filledTableWithTitles( 3, 3 );
    
    table.setTableName( "table1" );
    table2.setTableName( "table2" );
    table3.setTableName( "table3" );
    
    {
      Table<String> result = table.select().allColumns( table ).as().table();
      assertTrue( result.equalsInContent( table ) );
    }
    {
      Table<String> result = table.select().allColumns().as().table();
      assertTrue( result.equalsInContent( table ) );
    }
    {
      Table<String> result = table.select().column( 0 ).allColumns( table ).as().table();
      assertTrue( result.equalsInContent( table ) );
    }
    {
      Table<String> result = table.select().columns( 0, 1, 2, 3 ).as().table();
      assertTrue( result.equalsInContent( table ) );
    }
    {
      Table<String> result = table.select()
                                  .column( 1 )
                                  .join( table2 )
                                  .allColumns()
                                  .onEqual( table.column( 0 ), table2.column( 0 ) )
                                  .as()
                                  .table();
      assertNotNull( result );
      assertEquals( 10, result.rowSize() );
      assertEquals( "0:1", result.getCellElement( 0, 0 ) );
      assertEquals( "0:0", result.getCellElement( 0, 1 ) );
      assertEquals( "0:1", result.getCellElement( 0, 2 ) );
      assertEquals( "0:6", result.getCellElement( 0, 7 ) );
      assertEquals( "0:7", result.getCellElement( 0, 8 ) );
      assertNull( result.getCellElement( 0, 9 ) );
      
      assertEquals( "9:1", result.getCellElement( 9, 0 ) );
      assertEquals( "9:0", result.getCellElement( 9, 1 ) );
      assertEquals( "9:1", result.getCellElement( 9, 2 ) );
      assertEquals( "9:6", result.getCellElement( 9, 7 ) );
      assertEquals( "9:7", result.getCellElement( 9, 8 ) );
    }
    {
      Table<String> result = table.select()
                                  .columns( 1, 2 )
                                  .join( table2 )
                                  .columns( 6, 7 )
                                  .onEqual( table.column( 0 ), table2.column( 0 ) )
                                  .as()
                                  .table();
      assertNotNull( result );
      assertEquals( 10, result.rowSize() );
      assertEquals( "0:1", result.getCellElement( 0, 0 ) );
      assertEquals( "0:2", result.getCellElement( 0, 1 ) );
      assertEquals( "0:6", result.getCellElement( 0, 2 ) );
      assertEquals( "0:7", result.getCellElement( 0, 3 ) );
      
      assertEquals( "9:1", result.getCellElement( 9, 0 ) );
      assertEquals( "9:2", result.getCellElement( 9, 1 ) );
      assertEquals( "9:6", result.getCellElement( 9, 2 ) );
      assertEquals( "9:7", result.getCellElement( 9, 3 ) );
      
    }
    
    {
      /*
       * Cartesian product with table1, table2 and table3
       */
      Table<String> result = table.select()
                                  .columns( 1, 2 )
                                  .join( table2 )
                                  .columns( 6, 7 )
                                  .onEqual( table.column( 0 ), table2.column( 0 ) )
                                  .join( table3 )
                                  .column( 0 )
                                  .as()
                                  .table();
      
      //System.out.println( result );
      
      /*
          0:1,0:2,0:6,0:7,0:0
          0:1,0:2,0:6,0:7,1:0
          0:1,0:2,0:6,0:7,2:0
          1:1,1:2,1:6,1:7,0:0
          ...
          8:1,8:2,8:6,8:7,2:0
          9:1,9:2,9:6,9:7,0:0
          9:1,9:2,9:6,9:7,1:0
          9:1,9:2,9:6,9:7,2:0
       */
      
      assertNotNull( result );
      assertEquals( 30, result.rowSize() );
      assertEquals( "0:1", result.getCellElement( 0, 0 ) );
      assertEquals( "0:2", result.getCellElement( 0, 1 ) );
      assertEquals( "0:6", result.getCellElement( 0, 2 ) );
      assertEquals( "0:7", result.getCellElement( 0, 3 ) );
      assertEquals( "0:0", result.getCellElement( 0, 4 ) );
      
      assertEquals( "0:1", result.getCellElement( 2, 0 ) );
      assertEquals( "0:2", result.getCellElement( 2, 1 ) );
      assertEquals( "0:6", result.getCellElement( 2, 2 ) );
      assertEquals( "0:7", result.getCellElement( 2, 3 ) );
      assertEquals( "2:0", result.getCellElement( 2, 4 ) );
      
      assertEquals( "9:1", result.getCellElement( 27, 0 ) );
      assertEquals( "9:2", result.getCellElement( 27, 1 ) );
      assertEquals( "9:6", result.getCellElement( 27, 2 ) );
      assertEquals( "9:7", result.getCellElement( 27, 3 ) );
      assertEquals( "0:0", result.getCellElement( 27, 4 ) );
      
      assertEquals( "9:1", result.getCellElement( 29, 0 ) );
      assertEquals( "9:2", result.getCellElement( 29, 1 ) );
      assertEquals( "9:6", result.getCellElement( 29, 2 ) );
      assertEquals( "9:7", result.getCellElement( 29, 3 ) );
      assertEquals( "2:0", result.getCellElement( 29, 4 ) );
      
    }
    {
      Table<String> result = table.select()
                                  .columns( 1, 2 )
                                  .join( table2 )
                                  .columns( 6, 7 )
                                  .onEqual( table.column( 0 ), table2.column( 0 ) )
                                  .join( table3 )
                                  .onEqual( table.column( 0 ), table3.column( 0 ) )
                                  .column( 0 )
                                  .as()
                                  .table();
      
      //System.out.println( result );
      
      /*
          0:1,0:2,0:6,0:7,0:0
          1:1,1:2,1:6,1:7,1:0
          2:1,2:2,2:6,2:7,2:0
       */
      
      assertNotNull( result );
      assertEquals( 3, result.rowSize() );
      assertEquals( "0:1", result.getCellElement( 0, 0 ) );
      assertEquals( "0:2", result.getCellElement( 0, 1 ) );
      assertEquals( "0:6", result.getCellElement( 0, 2 ) );
      assertEquals( "0:7", result.getCellElement( 0, 3 ) );
      assertEquals( "0:0", result.getCellElement( 0, 4 ) );
      
      assertEquals( "2:1", result.getCellElement( 2, 0 ) );
      assertEquals( "2:2", result.getCellElement( 2, 1 ) );
      assertEquals( "2:6", result.getCellElement( 2, 2 ) );
      assertEquals( "2:7", result.getCellElement( 2, 3 ) );
      assertEquals( "2:0", result.getCellElement( 2, 4 ) );
      
    }
    {
      Table<String> result = table.select()
                                  .columns( 1, 2 )
                                  .join( table2 )
                                  .columns( 6, 7 )
                                  .onEqual( table.column( 0 ), table2.column( 0 ) )
                                  .join( table3 )
                                  .onEqual( table.column( 1 ), table3.column( 1 ) )
                                  .column( 0 )
                                  .as()
                                  .table();
      
      //      System.out.println( table );
      //      System.out.println( table2 );
      //      System.out.println( table3 );
      //      System.out.println( result );
      
      /*
          0:1,0:2,0:6,0:7,0:0
          1:1,1:2,1:6,1:7,1:0
          2:1,2:2,2:6,2:7,2:0
       */
      
      assertNotNull( result );
      assertEquals( 3, result.rowSize() );
      assertEquals( "0:1", result.getCellElement( 0, 0 ) );
      assertEquals( "0:2", result.getCellElement( 0, 1 ) );
      assertEquals( "0:6", result.getCellElement( 0, 2 ) );
      assertEquals( "0:7", result.getCellElement( 0, 3 ) );
      assertEquals( "0:0", result.getCellElement( 0, 4 ) );
      
      assertEquals( "2:1", result.getCellElement( 2, 0 ) );
      assertEquals( "2:2", result.getCellElement( 2, 1 ) );
      assertEquals( "2:6", result.getCellElement( 2, 2 ) );
      assertEquals( "2:7", result.getCellElement( 2, 3 ) );
      assertEquals( "2:0", result.getCellElement( 2, 4 ) );
      
    }
    
    {
      SortedMap<String, Set<Row<String>>> result = table.select()
                                                        .columns( 2, 1 )
                                                        .join( table2 )
                                                        .columns( 6, 7 )
                                                        .onEqual( table.column( 0 ), table2.column( 0 ) )
                                                        .join( table3 )
                                                        .onEqual( table.column( 1 ), table3.column( 1 ) )
                                                        .column( 0 )
                                                        .as()
                                                        .sortedMap();
      
      // System.out.println( result );
      
      /*
               index
                / 
          0:1,0:2,0:6,0:7,0:0
          1:1,1:2,1:6,1:7,1:0
          2:1,2:2,2:6,2:7,2:0
       */
      
      assertNotNull( result );
      assertEquals( 3, result.size() );
      assertArrayEquals( new String[] { "0:2", "0:1", "0:6", "0:7", "0:0" }, result.get( "0:2" ).iterator().next().to().array() );
      assertArrayEquals( new String[] { "2:2", "2:1", "2:6", "2:7", "2:0" }, result.get( "2:2" ).iterator().next().to().array() );
    }
  }
}

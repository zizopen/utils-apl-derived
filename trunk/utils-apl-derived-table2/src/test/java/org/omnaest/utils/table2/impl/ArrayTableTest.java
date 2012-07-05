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
package org.omnaest.utils.table2.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.table2.Column;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TableTest;

/**
 * @see ArrayTable
 * @author Omnaest
 */
public class ArrayTableTest extends TableTest
{
  @Override
  public <E> Table<E> newTable( E[][] elementMatrix, Class<E> type )
  {
    return new ArrayTable<E>( type ).copyFrom( elementMatrix );
  }
  
  @Test
  public void testColumns()
  {
    Table<String> table = this.filledTableWithTitles( 10, 8 );
    
    {
      Iterable<Column<String>> columns = table.columns( "c1", "c3" );
      assertEquals( 2, IterableUtils.size( columns ) );
      assertEquals( table.column( 1 ).id(), IterableUtils.elementAt( columns, 0 ).id() );
      assertEquals( table.column( 3 ).id(), IterableUtils.elementAt( columns, 1 ).id() );
    }
    {
      Iterable<Column<String>> columns = table.columns( Pattern.compile( "c1|c3" ) );
      assertEquals( 2, IterableUtils.size( columns ) );
      assertEquals( table.column( 1 ).id(), IterableUtils.elementAt( columns, 0 ).id() );
      assertEquals( table.column( 3 ).id(), IterableUtils.elementAt( columns, 1 ).id() );
    }
  }
  
  @Test
  @Ignore("Performance test")
  public void testPerformanceAddRows()
  {
    Table<String> table = this.newTable( new String[][] { { "a", "b", "c" }, { "d", "e", "f" } }, String.class );
    
    for ( int ii = 0; ii < 200000; ii++ )
    {
      String[] values = new String[] { "" + ii, "b", "c" };
      table.addRowElements( values );
    }
  }
  
  @Test
  @Ignore("Performance test")
  public void testPerformanceSelect() throws Exception
  {
    Table<String> table = this.filledTableWithTitles( 10000, 4 );
    Table<String> table2 = this.filledTableWithTitles( 200, 8 );
    Table<String> table3 = this.filledTableWithTitles( 50, 3 );
    
    table.setTableName( "table1" );
    table2.setTableName( "table2" );
    table3.setTableName( "table3" );
    
    {
      Table<String> result = table.select()
                                  .withTableLock( true )
                                  .columns( 1, 2 )
                                  .join( table2 )
                                  .withTableLock( true )
                                  .columns( 6, 7 )
                                  .onEqual( table.column( 0 ), table2.column( 0 ) )
                                  .join( table3 )
                                  .withTableLock( true )
                                  .onEqual( table.column( 1 ), table3.column( 1 ) )
                                  .column( 0 )
                                  .as()
                                  .table();
      
      //      System.out.println( table );
      //      System.out.println( table2 );
      //      System.out.println( table3 );
      //      System.out.println( result );
      
      assertNotNull( result );
      assertEquals( 50, result.rowSize() );
      assertEquals( 5, result.columnSize() );
    }
    
  }
}
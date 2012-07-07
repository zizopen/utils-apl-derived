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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.collections.ComparatorUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerEPrintStackTrace;
import org.omnaest.utils.structure.element.KeyExtractor;
import org.omnaest.utils.structure.element.ValueExtractor;
import org.omnaest.utils.structure.iterator.IterableUtils;
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
  
  @SuppressWarnings({ "unchecked", "cast" })
  @Test
  public void testIndexOfArbitraryKeyExtractor()
  {
    Table<String> table = this.filledTable( 100, 5 );
    
    KeyExtractor<Integer, String[]> keyExtractor = new KeyExtractor<Integer, String[]>()
    {
      @Override
      public Integer extractKey( String[] elements )
      {
        String[] tokens = elements[1].split( ":" );
        return Integer.valueOf( tokens[0] );
      }
    };
    ValueExtractor<Integer, Set<String[]>> valueExtractor = new ValueExtractor<Integer, Set<String[]>>()
    {
      @Override
      public Integer extractValue( Set<String[]> elementsSet )
      {
        final String[] elements = IterableUtils.firstElement( elementsSet );
        final String[] tokens = elements[1].split( ":" );
        return Integer.valueOf( tokens[1] );
      }
    };
    
    SortedMap<Integer, Integer> sortedMap = table.index()
                                                 .of( keyExtractor,
                                                      valueExtractor,
                                                      (Comparator<Integer>) ComparatorUtils.reversedComparator( ComparatorUtils.NATURAL_COMPARATOR ) );
    {
      assertNotNull( sortedMap );
      assertEquals( table.rowSize(), sortedMap.size() );
      assertTrue( sortedMap.containsKey( 0 ) );
    }
    
    table.removeRow( 0 );
    {
      assertFalse( sortedMap.containsKey( 0 ) );
      assertTrue( sortedMap.containsKey( 1 ) );
      assertFalse( sortedMap.containsKey( 101 ) );
      
      table.setCellElement( 0, 1, "101:88" );
      assertTrue( sortedMap.containsKey( 101 ) );
      
      Integer columnIndex = sortedMap.get( 101 );
      assertEquals( 88, columnIndex.intValue() );
    }
    
  }
  
  @Test
  @Ignore("Persistence test")
  public void testPersistenceWithDirectory()
  {
    final File directory = new File( "target/persistenceStoreTest" );
    final ExceptionHandlerEPrintStackTrace exceptionHandler = new ExceptionHandlerEPrintStackTrace();
    
    Table<String> table = new ArrayTable<String>( String.class ).setExceptionHandler( exceptionHandler )
                                                                .persistence()
                                                                .attachToDirectoryUsingXStream( directory );
    table.clear();
    
    final int rowSize = 500;
    if ( table.rowSize() == 0 )
    {
      table = this.filledTable( rowSize, 5 )
                  .setExceptionHandler( exceptionHandler )
                  .persistence()
                  .attachToDirectoryUsingXStream( directory );
    }
    
    assertEquals( rowSize, table.rowSize() );
    
    {
      Table<String> tableOther = new ArrayTable<String>( String.class ).persistence().attachToDirectoryUsingXStream( directory );
      //System.out.println( tableOther );
      assertEquals( table.rowSize(), tableOther.rowSize() );
      assertTrue( table.equalsInContent( tableOther ) );
    }
    
    table.row( 16 ).switchWith( 4 );
    table.row( 5 ).switchWith( 15 );
    table.row( 14 ).switchWith( 6 );
    table.row( 7 ).switchWith( 14 );
    //System.out.println( table );
    
    {
      Table<String> tableOther = new ArrayTable<String>( String.class ).persistence().attachToDirectoryUsingXStream( directory );
      //System.out.println( tableOther );
      assertTrue( table.equalsInContent( tableOther ) );
    }
    
    table.clear();
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

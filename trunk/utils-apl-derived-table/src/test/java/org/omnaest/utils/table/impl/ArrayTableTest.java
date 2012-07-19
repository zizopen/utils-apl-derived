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
package org.omnaest.utils.table.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Ignore;
import org.junit.Test;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerEPrintStackTrace;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.TableTest;
import org.omnaest.utils.table.impl.datasource.TableDataSourceResultSet;

/**
 * @see ArrayTable
 * @author Omnaest
 */
public class ArrayTableTest extends TableTest
{
  
  @Override
  public <E> Table<E> newTable( E[][] elementMatrix, Class<E> type )
  {
    return new ArrayTable<E>( type ).copy().from( elementMatrix );
  }
  
  @Test
  public void testResultSet() throws SQLException
  {
    Table<String> table = this.filledTableWithTitles( 10, 5 );
    {
      ResultSet resultSet = table.as().resultSet();
      for ( int ii = 0; ii < 10; ii++ )
      {
        assertTrue( resultSet.next() );
        assertEquals( ii + ":0", resultSet.getString( 1 ) );
        assertEquals( ii + ":1", resultSet.getString( "c1" ) );
      }
      assertFalse( resultSet.next() );
    }
    {
      ResultSet resultSet = table.as().resultSet();
      Table<String> tableOther = new ArrayTable<String>( String.class );
      tableOther.copy().from( new TableDataSourceResultSet<String>( resultSet, String.class ) );
      assertTrue( table.equalsInContent( tableOther ) );
      //System.out.println( tableOther );
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
                                                                .attach()
                                                                .asXML()
                                                                .usingXStream()
                                                                .toDirectory( directory );
    table.clear();
    
    final int rowSize = 500;
    if ( table.rowSize() == 0 )
    {
      table = this.filledTable( rowSize, 5 )
                  .setExceptionHandler( exceptionHandler )
                  .persistence()
                  .attach()
                  .asXML()
                  .usingXStream()
                  .toDirectory( directory );
    }
    
    assertEquals( rowSize, table.rowSize() );
    
    {
      Table<String> tableOther = new ArrayTable<String>( String.class ).persistence()
                                                                       .attach()
                                                                       .asXML()
                                                                       .usingXStream()
                                                                       .toDirectory( directory );
      
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
      Table<String> tableOther = new ArrayTable<String>( String.class ).persistence()
                                                                       .attach()
                                                                       .asXML()
                                                                       .usingXStream()
                                                                       .toDirectory( directory );
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

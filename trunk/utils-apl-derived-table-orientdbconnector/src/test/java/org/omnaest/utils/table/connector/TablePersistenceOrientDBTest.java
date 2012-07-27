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
package org.omnaest.utils.table.connector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.omnaest.utils.events.exception.ExceptionHandlerSerializable;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerEPrintStackTrace;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.impl.ArrayTable;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

/**
 * @see TablePersistenceOrientDB
 * @author Omnaest
 */
public class TablePersistenceOrientDBTest
{
  @Test
  public void testPersistence()
  {
    ODatabaseDocumentTx db = new ODatabaseDocumentTx( "memory:testdb" );
    db.create();
    {
      final ExceptionHandlerSerializable exceptionHandler = new ExceptionHandlerEPrintStackTrace();
      final TablePersistenceOrientDB<String> tablePersistence = new TablePersistenceOrientDB<String>( db, String.class );
      final String[] columnTitles = new String[] { "column1", "column2", "column3" };
      final String tableName = "Test";
      {
        Table<String> table = new ArrayTable<String>( String.class ).setExceptionHandler( exceptionHandler )
                                                                    .persistence()
                                                                    .attach( tablePersistence )
                                                                    .setColumnTitles( columnTitles )
                                                                    .setTableName( tableName );
        
        table.addRowElements( "a0", "b", "c" );
        table.addRowElements( "a1", "b", "c" );
        table.addRowElements( "a2", "b", "c" );
        table.addRowElements( "a3", "b", "c" );
        
        table.row( 1 ).switchWith( 3 );
        table.row( 0 ).switchWith( 2 );
        table.row( 1 ).switchWith( 3 );
        table.row( 0 ).switchWith( 2 );
        
        table.row( 1 ).moveTo( 3 );
        
        //System.out.println( table );
      }
      {
        Table<String> table = new ArrayTable<String>( String.class ).setExceptionHandler( exceptionHandler )
                                                                    .setTableName( tableName )
                                                                    .setColumnTitles( columnTitles )
                                                                    .persistence()
                                                                    .attach( tablePersistence );
        
        /*
        ==========Test===========
        !column1!column2!column3!
        |  a0   |   b   |   c   |
        |  a2   |   b   |   c   |
        |  a1   |   b   |   c   |
        |  a3   |   b   |   c   |
        -------------------------
         */
        
        //System.out.println( table );
        assertEquals( 4, table.rowSize() );
        
        assertEquals( tableName, table.getTableName() );
        assertArrayEquals( columnTitles, table.getColumnTitles() );
        
        assertArrayEquals( new String[] { "a0", "b", "c" }, table.row( 0 ).getElements() );
        assertArrayEquals( new String[] { "a2", "b", "c" }, table.row( 1 ).getElements() );
        assertArrayEquals( new String[] { "a1", "b", "c" }, table.row( 2 ).getElements() );
        assertArrayEquals( new String[] { "a3", "b", "c" }, table.row( 3 ).getElements() );
        
      }
      {
        final String[] reducedColumns = new String[] { "column1", "column3" };
        Table<String> table = new ArrayTable<String>( String.class ).setExceptionHandler( exceptionHandler )
                                                                    .setTableName( tableName )
                                                                    .setColumnTitles( reducedColumns )
                                                                    .persistence()
                                                                    .attach( tablePersistence );
        
        /*
        ======Test=======
        !column1!column3!
        |  a0   |   c   |
        |  a2   |   c   |
        |  a1   |   c   |
        |  a3   |   c   |
        -----------------
         */
        
        //System.out.println( table );
        assertEquals( 4, table.rowSize() );
        
        assertEquals( tableName, table.getTableName() );
        assertArrayEquals( reducedColumns, table.getColumnTitles() );
        
        assertArrayEquals( new String[] { "a0", "c" }, table.row( 0 ).getElements() );
        assertArrayEquals( new String[] { "a2", "c" }, table.row( 1 ).getElements() );
        assertArrayEquals( new String[] { "a1", "c" }, table.row( 2 ).getElements() );
        assertArrayEquals( new String[] { "a3", "c" }, table.row( 3 ).getElements() );
        
      }
    }
    db.close();
  }
}

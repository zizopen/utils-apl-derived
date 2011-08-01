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
package org.omnaest.utils.structure.table.adapter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.TableFiller;
import org.omnaest.utils.structure.table.concrete.ArrayTable;

public class TableListToCallableStatementAdapterTest
{
  /* ********************************************** Variables ********************************************** */
  protected Table<String>     table1            = new ArrayTable<String>();
  protected Table<String>     table2            = new ArrayTable<String>();
  
  protected CallableStatement callableStatement = new TableListToCallableStatementAdapter( this.table1, this.table2 );
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    //
    {
      //
      int rows = 3;
      int columns = 4;
      String tableName = "Table1";
      @SuppressWarnings("rawtypes")
      Table table = this.table1;
      TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, table );
    }
    
    //
    {
      //
      int rows = 4;
      int columns = 2;
      String tableName = "Table2";
      @SuppressWarnings("rawtypes")
      Table table = this.table2;
      TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, table );
    }
  }
  
  @Test
  public void testExecuteQuery()
  {
    //
    try
    {
      //
      assertTrue( this.callableStatement.execute() );
      assertNotNull( this.callableStatement.executeQuery() );
      assertNotNull( this.callableStatement.executeQuery( "Dummy SQL statement" ) );
    }
    catch ( SQLException e )
    {
      Assert.fail( e.getMessage() );
    }
  }
  
  @Test
  public void testGetMoreResults()
  {
    //
    try
    {
      //
      {
        //
        ResultSet resultSet1 = this.callableStatement.executeQuery();
        int rows = this.table1.tableSize().getRowSize();
        int columns = this.table1.tableSize().getColumnSize();
        
        this.assertResultSet( resultSet1, rows, columns );
      }
      
      //
      {
        //      
        assertTrue( this.callableStatement.getMoreResults() );
        ResultSet resultSet2 = this.callableStatement.getResultSet();
        int rows = this.table2.tableSize().getRowSize();
        int columns = this.table2.tableSize().getColumnSize();
        
        this.assertResultSet( resultSet2, rows, columns );
      }
      
      //
      {
        //
        assertFalse( this.callableStatement.getMoreResults() );
        ResultSet resultSet3 = this.callableStatement.getResultSet();
        assertNull( resultSet3 );
      }
      
    }
    catch ( SQLException e )
    {
      Assert.fail( e.getMessage() );
    }
  }
  
  /**
   * @param resultSet
   * @throws SQLException
   */
  protected void assertResultSet( ResultSet resultSet, int rows, int columns ) throws SQLException
  {
    //
    assertTrue( resultSet.isBeforeFirst() );
    assertFalse( resultSet.isFirst() );
    assertFalse( resultSet.isAfterLast() );
    assertFalse( resultSet.isLast() );
    for ( int ii = 0; ii < rows; ii++ )
    {
      //
      assertTrue( resultSet.next() );
      
      //
      if ( ii == 0 )
      {
        //
        assertFalse( resultSet.isBeforeFirst() );
        assertFalse( resultSet.isAfterLast() );
        assertFalse( resultSet.isLast() );
        assertTrue( resultSet.isFirst() );
      }
      else if ( ii == rows - 1 )
      {
        //
        assertFalse( resultSet.isFirst() );
        assertFalse( resultSet.isBeforeFirst() );
        assertFalse( resultSet.isAfterLast() );
        assertTrue( resultSet.isLast() );
      }
      else
      {
        //
        assertFalse( resultSet.isFirst() );
        assertFalse( resultSet.isBeforeFirst() );
        assertFalse( resultSet.isAfterLast() );
        assertFalse( resultSet.isLast() );
      }
    }
    
    //
    assertFalse( resultSet.next() );
    
    assertFalse( resultSet.isFirst() );
    assertFalse( resultSet.isBeforeFirst() );
    assertFalse( resultSet.isLast() );
    assertTrue( resultSet.isAfterLast() );
  }
  
}

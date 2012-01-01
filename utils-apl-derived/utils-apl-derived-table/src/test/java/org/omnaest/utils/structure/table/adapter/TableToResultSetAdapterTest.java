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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.TableFiller;
import org.omnaest.utils.structure.table.concrete.ArrayTable;

/**
 * @see TableToResultSetAdapter
 * @author Omnaest
 */
@RunWith(value = Parameterized.class)
public class TableToResultSetAdapterTest
{
  @Parameters
  public static Collection<Object[]> configurationDataCollection()
  {
    //
    List<Object[]> retlist = new ArrayList<Object[]>();
    
    //    
    retlist.add( new Object[] { new ResultSetAdapterFactory()
    {
      @Override
      public ResultSet newResultSetAdapter( Table<Object> table )
      {
        return table.as().adapter( new TableToResultSetAdapter() );
      }
    } } );
    retlist.add( new Object[] { new ResultSetAdapterFactory()
    {
      @Override
      public ResultSet newResultSetAdapter( Table<Object> table )
      {
        return new TableToResultSetAdapter( table );
      }
    } } );
    
    //
    return retlist;
  }
  
  /**
   * @param resultSet
   * @param table
   */
  public TableToResultSetAdapterTest( ResultSetAdapterFactory resultSetAdapterFactory )
  {
    super();
    
    //
    this.resultSet = resultSetAdapterFactory.newResultSetAdapter( this.table );
    
  }
  
  /* ********************************************** Variables ********************************************** */
  protected Table<Object> table            = new ArrayTable<Object>();
  protected ResultSet     resultSet        = null;
  
  List<Object>            elementValueList = new ArrayList<Object>();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @author Omnaest
   */
  protected abstract static class ResultSetAdapterFactory
  {
    /**
     * @param table
     * @return
     */
    public abstract ResultSet newResultSetAdapter( Table<Object> table );
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    //
    int rows = 5;
    int columns = 10;
    String tableName = "Test1";
    TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, this.table );
    
    //    
    this.elementValueList.add( Byte.valueOf( (byte) 11 ) );
    this.elementValueList.add( Short.valueOf( (short) 12 ) );
    this.elementValueList.add( Integer.valueOf( 13 ) );
    
    this.elementValueList.add( Long.valueOf( 14l ) );
    this.elementValueList.add( Float.valueOf( (float) 15.123 ) );
    this.elementValueList.add( Double.valueOf( 16.123 ) );
    
    this.elementValueList.add( BigDecimal.valueOf( 18.123 ) );
    this.elementValueList.add( String.valueOf( "text" ) );
    this.elementValueList.add( new Date().getTime() );
    this.elementValueList.add( new Time( new Date().getTime() ).getTime() );
    
    this.table.getRow( 0 ).setCellElements( this.elementValueList );
  }
  
  @Test
  public void testNext() throws SQLException
  {
    //
    final int rows = this.table.getTableSize().getRowSize();
    
    //
    assertTrue( this.resultSet.isBeforeFirst() );
    assertFalse( this.resultSet.isFirst() );
    assertFalse( this.resultSet.isAfterLast() );
    assertFalse( this.resultSet.isLast() );
    assertEquals( -1, this.resultSet.getRow() );
    for ( int ii = 0; ii < rows; ii++ )
    {
      //
      assertTrue( this.resultSet.next() );
      assertEquals( ii, this.resultSet.getRow() );
      assertEquals( "r" + ii, this.resultSet.getCursorName() );
      
      //
      if ( ii == 0 )
      {
        //
        assertFalse( this.resultSet.isBeforeFirst() );
        assertFalse( this.resultSet.isAfterLast() );
        assertFalse( this.resultSet.isLast() );
        assertTrue( this.resultSet.isFirst() );
      }
      else if ( ii == rows - 1 )
      {
        //
        assertFalse( this.resultSet.isFirst() );
        assertFalse( this.resultSet.isBeforeFirst() );
        assertFalse( this.resultSet.isAfterLast() );
        assertTrue( this.resultSet.isLast() );
      }
      else
      {
        //
        assertFalse( this.resultSet.isFirst() );
        assertFalse( this.resultSet.isBeforeFirst() );
        assertFalse( this.resultSet.isAfterLast() );
        assertFalse( this.resultSet.isLast() );
      }
    }
    
    //
    assertFalse( this.resultSet.next() );
    
    assertFalse( this.resultSet.isFirst() );
    assertFalse( this.resultSet.isBeforeFirst() );
    assertFalse( this.resultSet.isLast() );
    assertTrue( this.resultSet.isAfterLast() );
    assertEquals( rows, this.resultSet.getRow() );
    
  }
  
  @Test
  public void testClose() throws SQLException
  {
    assertFalse( this.resultSet.isClosed() );
    assertTrue( this.resultSet.next() );
    this.resultSet.close();
    assertTrue( this.resultSet.isClosed() );
    assertFalse( this.resultSet.next() );
  }
  
  @Test
  public void testGetValue() throws SQLException
  {
    //
    assertTrue( this.resultSet.next() );
    
    //
    {
      //
      int ii = 0;
      assertEquals( this.elementValueList.get( ii++ ), this.resultSet.getByte( ii ) );
      assertEquals( this.elementValueList.get( ii++ ), this.resultSet.getShort( ii ) );
      assertEquals( this.elementValueList.get( ii++ ), this.resultSet.getInt( ii ) );
      
      assertEquals( this.elementValueList.get( ii++ ), this.resultSet.getLong( ii ) );
      assertEquals( this.elementValueList.get( ii++ ), this.resultSet.getFloat( ii ) );
      assertEquals( this.elementValueList.get( ii++ ), this.resultSet.getDouble( ii ) );
      
      assertEquals( this.elementValueList.get( ii++ ), this.resultSet.getBigDecimal( ii ) );
      assertEquals( this.elementValueList.get( ii++ ), this.resultSet.getString( ii ) );
      assertEquals( new Date( (Long) this.elementValueList.get( ii++ ) ), this.resultSet.getDate( ii ) );
      assertEquals( new Time( (Long) this.elementValueList.get( ii++ ) ), this.resultSet.getTime( ii ) );
      
      assertEquals( this.elementValueList.get( 0 ), this.resultSet.getObject( 1 ) );
    }
    
    //
    {
      //
      int ii = 0;
      assertEquals( this.elementValueList.get( ii ), this.resultSet.getByte( "c" + ii++ ) );
      assertEquals( this.elementValueList.get( ii ), this.resultSet.getShort( "c" + ii++ ) );
      assertEquals( this.elementValueList.get( ii ), this.resultSet.getInt( "c" + ii++ ) );
      
      assertEquals( this.elementValueList.get( ii ), this.resultSet.getLong( "c" + ii++ ) );
      assertEquals( this.elementValueList.get( ii ), this.resultSet.getFloat( "c" + ii++ ) );
      assertEquals( this.elementValueList.get( ii ), this.resultSet.getDouble( "c" + ii++ ) );
      
      assertEquals( this.elementValueList.get( ii ), this.resultSet.getBigDecimal( "c" + ii++ ) );
      assertEquals( this.elementValueList.get( ii ), this.resultSet.getString( "c" + ii++ ) );
      assertEquals( new Date( (Long) this.elementValueList.get( ii ) ), this.resultSet.getDate( "c" + ii++ ) );
      assertEquals( new Time( (Long) this.elementValueList.get( ii ) ), this.resultSet.getTime( "c" + ii++ ) );
    }
    
    //
    for ( int ii = 1; ii <= this.elementValueList.size(); ii++ )
    {
      //
      assertEquals( this.elementValueList.get( ii - 1 ), this.resultSet.getObject( ii ) );
      assertEquals( this.elementValueList.get( ii - 1 ), this.resultSet.getObject( "c" + ( ii - 1 ) ) );
    }
  }
  
  @Test
  public void testGetMetaData() throws SQLException
  {
    //
    ResultSetMetaData metaData = this.resultSet.getMetaData();
    assertNotNull( metaData );
    
    //
    int columnCount = metaData.getColumnCount();
    assertEquals( this.elementValueList.size(), columnCount );
    for ( int ii = 1; ii <= columnCount; ii++ )
    {
      assertNotNull( metaData.getColumnName( ii ) );
      assertEquals( "c" + ( ii - 1 ), metaData.getColumnName( ii ) );
      assertEquals( "c" + ( ii - 1 ), metaData.getColumnLabel( ii ) );
    }
    
  }
  
  @Test
  public void testFindColumn() throws SQLException
  {
    //    
    for ( int ii = 1; ii <= this.elementValueList.size(); ii++ )
    {
      assertEquals( ii, this.resultSet.findColumn( "c" + ( ii - 1 ) ) );
    }
  }
  
  @Test
  public void testBeforeFirst() throws SQLException
  {
    //
    this.resultSet.beforeFirst();
    
    //        
    assertTrue( this.resultSet.isBeforeFirst() );
    assertFalse( this.resultSet.isAfterLast() );
    assertFalse( this.resultSet.isLast() );
    assertFalse( this.resultSet.isFirst() );
  }
  
  @Test
  public void testAfterLast() throws SQLException
  {
    //
    this.resultSet.afterLast();
    
    //        
    assertFalse( this.resultSet.isBeforeFirst() );
    assertTrue( this.resultSet.isAfterLast() );
    assertFalse( this.resultSet.isLast() );
    assertFalse( this.resultSet.isFirst() );
  }
  
  @Test
  public void testFirst() throws SQLException
  {
    //
    this.resultSet.first();
    
    //        
    assertFalse( this.resultSet.isBeforeFirst() );
    assertFalse( this.resultSet.isAfterLast() );
    assertFalse( this.resultSet.isLast() );
    assertTrue( this.resultSet.isFirst() );
  }
  
  @Test
  public void testLast() throws SQLException
  {
    //
    this.resultSet.last();
    
    //        
    assertFalse( this.resultSet.isBeforeFirst() );
    assertFalse( this.resultSet.isAfterLast() );
    assertTrue( this.resultSet.isLast() );
    assertFalse( this.resultSet.isFirst() );
  }
  
  @Test
  public void testPrevious() throws SQLException
  {
    //
    final int rows = this.table.getTableSize().getRowSize();
    
    //
    while ( this.resultSet.next() )
    {
    }
    assertFalse( this.resultSet.previous() );
    
    //
    assertFalse( this.resultSet.isBeforeFirst() );
    assertFalse( this.resultSet.isFirst() );
    assertTrue( this.resultSet.isAfterLast() );
    assertFalse( this.resultSet.isLast() );
    assertEquals( rows, this.resultSet.getRow() );
    for ( int ii = 0; ii < rows; ii++ )
    {
      //
      assertTrue( this.resultSet.previous() );
      assertEquals( rows - ii - 1, this.resultSet.getRow() );
      
      //
      if ( ii == 0 )
      {
        //
        assertFalse( this.resultSet.isBeforeFirst() );
        assertFalse( this.resultSet.isAfterLast() );
        assertTrue( this.resultSet.isLast() );
        assertFalse( this.resultSet.isFirst() );
      }
      else if ( ii == rows - 1 )
      {
        //
        assertTrue( this.resultSet.isFirst() );
        assertFalse( this.resultSet.isBeforeFirst() );
        assertFalse( this.resultSet.isAfterLast() );
        assertFalse( this.resultSet.isLast() );
      }
      else
      {
        //
        assertFalse( this.resultSet.isFirst() );
        assertFalse( this.resultSet.isBeforeFirst() );
        assertFalse( this.resultSet.isAfterLast() );
        assertFalse( this.resultSet.isLast() );
      }
    }
    
    //
    assertFalse( this.resultSet.previous() );
    
    assertFalse( this.resultSet.isFirst() );
    assertTrue( this.resultSet.isBeforeFirst() );
    assertFalse( this.resultSet.isLast() );
    assertFalse( this.resultSet.isAfterLast() );
    assertEquals( -1, this.resultSet.getRow() );
  }
  
  @Test
  public void testAbsolute() throws SQLException
  {
    for ( int ii = 0; ii < this.table.getTableSize().getRowSize(); ii++ )
    {
      //
      this.resultSet.absolute( ii );
      assertEquals( ii, this.resultSet.getRow() );
    }
  }
  
}

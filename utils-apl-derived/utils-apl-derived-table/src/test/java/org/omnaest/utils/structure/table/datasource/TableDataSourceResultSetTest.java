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
package org.omnaest.utils.structure.table.datasource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ListIterator;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.adapter.TableToResultSetAdapter;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.subspecification.TableDataSource;

/**
 * @see TableDataSourceResultSet
 * @author Omnaest
 */
public class TableDataSourceResultSetTest
{
  /* ********************************************** Variables ********************************************** */
  protected Object[][]              elementArray    = { { 0.0, 0.1, 0.2 }, { 1.0, 1.1, 1.2 } };
  protected Table<Object>           table           = new ArrayTable<Object>( this.elementArray );
  protected ResultSet               resultSet       = new TableToResultSetAdapter( this.table );
  protected TableDataSource<Object> tableDataSource = new TableDataSourceResultSet<Object>( this.resultSet );
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    this.table.setTableName( "Table1" );
    this.table.setColumnTitleValues( "Column1", "Column2", "Column3" );
    this.table.setRowTitleValues( "Row1", "Row2" );
  }
  
  @Test
  public void testRows()
  {
    //
    Table<Object> tableResult = new ArrayTable<Object>();
    
    //
    tableResult.copyFrom( this.tableDataSource );
    
    //
    assertEquals( this.table, tableResult );
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testWithH2InMemoryDatabase()
  {
    try
    {
      //
      JdbcDataSource jdbcDataSource = new JdbcDataSource();
      jdbcDataSource.setURL( "jdbc:h2:mem:test" );
      jdbcDataSource.setUser( "sa" );
      jdbcDataSource.setPassword( "sa" );
      
      //
      Connection connection = jdbcDataSource.getConnection();
      
      //
      {
        //
        PreparedStatement preparedStatement = connection.prepareStatement( "CREATE TABLE TestTable(Column0 int, Column1 nvarchar2, Column2 Number(20,2), Column3 date);" );
        preparedStatement.execute();
      }
      
      //
      {
        //
        for ( int ii = 0; ii < 5; ii++ )
        {
          PreparedStatement preparedStatement = connection.prepareStatement( "INSERT into TestTable (Column0,Column1,Column2,Column3) VALUES (?,?,?,?);" );
          preparedStatement.setInt( 1, ii );
          preparedStatement.setString( 2, "value" + ii );
          preparedStatement.setDouble( 3, ii * 1.1 );
          
          preparedStatement.setDate( 4, new Date( new SimpleDateFormat( "yyyy-MM-dd" ).parse( "2011-08-06" ).getTime() ) );
          preparedStatement.execute();
        }
      }
      
      //
      {
        //
        PreparedStatement preparedStatement = connection.prepareStatement( "SELECT * FROM TestTable;" );
        ResultSet resultSet = preparedStatement.executeQuery();
        
        //
        this.table.copyFrom( new TableDataSourceResultSet<Object>( resultSet ) );
        
        //
        //System.out.println( this.table );
        
        /*
            =============TESTTABLE==============
            !COLUMN0!COLUMN1!COLUMN2! COLUMN3  !
            |   0   |value0 | 0.00  |2011-08-06|
            |   1   |value1 | 1.10  |2011-08-06|
            |   2   |value2 | 2.20  |2011-08-06|
            |   3   |value3 | 3.30  |2011-08-06|
            |   4   |value4 | 4.40  |2011-08-06|
            ------------------------------------
         */
        
        //
        final ListIterator<Row<Object>> rowIterator = this.table.rows().iterator();
        final Date date = new Date( new SimpleDateFormat( "yyyy-MM-dd" ).parse( "2011-08-06" ).getTime() );
        
        //
        assertEquals( "TESTTABLE", this.table.getTableName() );
        
        assertEquals( Arrays.asList( "COLUMN0", "COLUMN1", "COLUMN2", "COLUMN3" ), this.table.getColumnTitleValueList() );
        assertEquals( 0, this.table.getCellElement( 0, 0 ) );
        assertEquals( "value0", this.table.getCellElement( 0, 1 ) );
        assertEquals( new BigDecimal( "0.00" ), this.table.getCellElement( 0, 2 ) );
        assertEquals( new Date( new SimpleDateFormat( "yyyy-MM-dd" ).parse( "2011-08-06" ).getTime() ),
                      this.table.getCellElement( 0, 3 ) );
        
        assertEquals( Arrays.asList( 0, "value0", new BigDecimal( "0.00" ), date ), rowIterator.next().getCellElementList() );
        assertEquals( Arrays.asList( 1, "value1", new BigDecimal( "1.10" ), date ), rowIterator.next().getCellElementList() );
        assertEquals( Arrays.asList( 2, "value2", new BigDecimal( "2.20" ), date ), rowIterator.next().getCellElementList() );
        assertEquals( Arrays.asList( 3, "value3", new BigDecimal( "3.30" ), date ), rowIterator.next().getCellElementList() );
        assertEquals( Arrays.asList( 4, "value4", new BigDecimal( "4.40" ), date ), rowIterator.next().getCellElementList() );
      }
      
    }
    catch ( Exception e )
    {
      fail( e.getMessage() );
    }
  }
  
}

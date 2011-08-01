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

import java.sql.ResultSet;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
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
  
}

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
package org.omnaest.utils.structure.table.concrete.internal.adapterprovider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.TableFiller;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.datasource.TableDataSourceResultSet;
import org.omnaest.utils.structure.table.subspecification.TableAdaptable.TableAdapterProvider;
import org.omnaest.utils.structure.table.subspecification.TableDataSource;

/**
 * @see TableAdapterProviderImpl
 * @see TableAdapterProvider
 * @author Omnaest
 */
public class TableAdapterProviderImplTest
{
  /* ********************************************** Variables ********************************************** */
  protected Table<String> table = new ArrayTable<String>();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  protected static class BeanType
  {
    /* ********************************************** Variables ********************************************** */
    protected String c0 = null;
    protected String c1 = null;
    protected String c2 = null;
    
    /* ********************************************** Methods ********************************************** */
    /**
     * @return the c0
     */
    public String getC0()
    {
      return this.c0;
    }
    
    /**
     * @return the c1
     */
    public String getC1()
    {
      return this.c1;
    }
    
    /**
     * @return the c2
     */
    public String getC2()
    {
      return this.c2;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    //
    int rows = 10;
    int columns = 5;
    String tableName = "Table1";
    TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, this.table );
  }
  
  @Test
  public void testResultSet()
  {
    //
    ResultSet resultSet = this.table.as().resultSet();
    
    //
    Table<String> tableCopy = new ArrayTable<String>();
    TableDataSource<String> tableDataSource = new TableDataSourceResultSet<String>( resultSet );
    tableCopy.copyFrom( tableDataSource );
    
    assertEquals( this.table, tableCopy );
  }
  
  @Test
  public void testListOfType()
  {
    List<BeanType> listOfType = this.table.as().listOfType( BeanType.class );
    assertNotNull( listOfType );
    assertEquals( this.table.getTableSize().getRowSize(), listOfType.size() );
    assertNotNull( listOfType.get( 0 ) );
    assertEquals( Arrays.asList( "1:0", "1:1", "1:2" ), BeanUtils.propertyValueList( listOfType.get( 1 ), "c0", "c1", "c2" ) );
  }
  
  @Test
  public void testArrayObject()
  {
    //
    Object[][] array = this.table.as().array();
    assertEquals( this.table.getTableSize().getRowSize(), array.length );
    assertEquals( Arrays.asList( "0:0", "0:1", "0:2", "0:3", "0:4" ), Arrays.asList( array[0] ) );
    assertEquals( Arrays.asList( "9:0", "9:1", "9:2", "9:3", "9:4" ), Arrays.asList( array[9] ) );
  }
  
  @Test
  public void testArray()
  {
    //
    String[][] array = this.table.as().array( String.class );
    assertEquals( this.table.getTableSize().getRowSize(), array.length );
    assertEquals( Arrays.asList( "0:0", "0:1", "0:2", "0:3", "0:4" ), Arrays.asList( array[0] ) );
    assertEquals( Arrays.asList( "9:0", "9:1", "9:2", "9:3", "9:4" ), Arrays.asList( array[9] ) );
  }
}

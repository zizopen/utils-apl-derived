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

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.TableFiller;
import org.omnaest.utils.structure.table.adapter.TableToTypeListAdapter.Column;
import org.omnaest.utils.structure.table.concrete.ArrayTable;

/**
 * @see TableToTypeListAdapter
 * @author Omnaest
 */
public class TableToTypeListAdapterTest
{
  /* ********************************************** Variables ********************************************** */
  protected Table<String>                    table                  = new ArrayTable<String>();
  protected TableToTypeListAdapter<BeanType> tableToTypeListAdapter = TableToTypeListAdapter.newInstance( this.table,
                                                                                                          BeanType.class );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see TableToTypeListAdapterTest
   * @author Omnaest
   */
  protected static class BeanType
  {
    /* ********************************************** Variables ********************************************** */
    protected String c0      = null;
    protected String c1      = null;
    
    @Column(title = "c2")
    protected String column2 = null;
    
    /* ********************************************** Methods ********************************************** */
    /**
     * @return the c0
     */
    public String getC0()
    {
      return this.c0;
    }
    
    /**
     * @param c0
     *          the c0 to set
     */
    public void setC0( String c0 )
    {
      this.c0 = c0;
    }
    
    /**
     * @return the c1
     */
    public String getC1()
    {
      return this.c1;
    }
    
    /**
     * @param c1
     *          the c1 to set
     */
    public void setC1( String c1 )
    {
      this.c1 = c1;
    }
    
    /**
     * @return
     */
    public String getColumn2()
    {
      return this.column2;
    }
    
    /**
     * @param column2
     */
    public void setColumn2( String column2 )
    {
      this.column2 = column2;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    int rows = 5;
    int columns = 3;
    String tableName = "Table1";
    TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, this.table );
  }
  
  @Test
  public void testGetTable()
  {
    assertEquals( this.table, this.tableToTypeListAdapter.getTable() );
  }
  
  @Test
  public void testSize()
  {
    assertEquals( this.table.getTableSize().getRowSize(), this.tableToTypeListAdapter.size() );
  }
  
  @Test
  public void testAddB()
  {
    //
    BeanType bean = new BeanType();
    bean.setC0( "value0" );
    bean.setC1( "value1" );
    bean.setColumn2( "value2 " );
    this.tableToTypeListAdapter.add( bean );
    
    //
    assertEquals( Arrays.asList( bean.getC0(), bean.getC1(), bean.getColumn2() ), this.table.getLastRow().getCellElementList() );
  }
  
  @Test
  public void testAddRow()
  {
    //
    BeanType beanType = new BeanType();
    beanType.setC0( "value0" );
    beanType.setC1( "value1" );
    beanType.setColumn2( "value2 " );
    BeanType beanTypeManaged = this.tableToTypeListAdapter.addRow( 1, beanType );
    
    //
    assertEquals( Arrays.asList( beanType.getC0(), beanType.getC1(), beanType.getColumn2() ), this.table.getRow( 1 )
                                                                                                        .getCellElementList() );
    
    //
    beanTypeManaged.setC1( "other value" );
    
    //
    assertEquals( Arrays.asList( beanTypeManaged.getC0(), beanTypeManaged.getC1(), beanTypeManaged.getColumn2() ),
                  this.table.getRow( 1 ).getCellElementList() );
  }
  
  @Test
  public void testClear()
  {
    this.tableToTypeListAdapter.clear();
    assertEquals( 0, this.table.getTableSize().getRowSize() );
  }
  
  @Test
  public void testGet()
  {
    BeanType beanType = this.tableToTypeListAdapter.get( 1 );
    assertEquals( Arrays.asList( beanType.getC0(), beanType.getC1(), beanType.getColumn2() ), this.table.getRow( 1 )
                                                                                                        .getCellElementList() );
  }
  
  @Test
  public void testSet()
  {
    //
    BeanType beanType = new BeanType();
    beanType.setC0( "value0" );
    beanType.setC1( "value1" );
    beanType.setColumn2( "value2 " );
    BeanType beanTypeManaged = this.tableToTypeListAdapter.set( 1, beanType );
    
    //
    assertEquals( Arrays.asList( beanType.getC0(), beanType.getC1(), beanType.getColumn2() ), this.table.getRow( 1 )
                                                                                                        .getCellElementList() );
    
    //
    assertEquals( Arrays.asList( beanTypeManaged.getC0(), beanTypeManaged.getC1(), beanTypeManaged.getColumn2() ),
                  this.table.getRow( 1 ).getCellElementList() );
    
  }
  
  @Test
  public void testAddIntB()
  {
    //
    BeanType bean = new BeanType();
    bean.setC0( "value0" );
    bean.setC1( "value1" );
    bean.setColumn2( "value2 " );
    this.tableToTypeListAdapter.add( 2, bean );
    
    //
    assertEquals( Arrays.asList( bean.getC0(), bean.getC1(), bean.getColumn2() ), this.table.getRow( 2 ).getCellElementList() );
  }
  
  @Test
  public void testRemoveInt()
  {
    //
    BeanType beanType = this.tableToTypeListAdapter.remove( 1 );
    
    //
    assertEquals( "1:0", beanType.getC0() );
    assertEquals( "1:1", beanType.getC1() );
    assertEquals( "1:2", beanType.getColumn2() );
    
    //
    assertEquals( 4, this.table.getTableSize().getRowSize() );
    assertEquals( Arrays.asList( "0:0", "0:1", "0:2" ), this.table.getRow( 0 ).getCellElementList() );
    assertEquals( Arrays.asList( "2:0", "2:1", "2:2" ), this.table.getRow( 1 ).getCellElementList() );
    
  }
  
  @Test(expected = UnsupportedOperationException.class)
  public void testIndexOf()
  {
    this.tableToTypeListAdapter.indexOf( new Object() );
  }
  
  @Test(expected = UnsupportedOperationException.class)
  public void testLastIndexOf()
  {
    this.tableToTypeListAdapter.lastIndexOf( new Object() );
  }
  
}

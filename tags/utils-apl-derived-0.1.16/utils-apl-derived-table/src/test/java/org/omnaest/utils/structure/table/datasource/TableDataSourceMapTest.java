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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.subspecification.TableDataSource;

/**
 * @see TableDataSource
 * @author Omnaest
 */
public class TableDataSourceMapTest
{
  /* ********************************************** Variables ********************************************** */
  private final Map<String, String> map              = new LinkedHashMap<String, String>();
  private final String              columnTitleKey   = "key";
  private final String              columnTitleValue = "value";
  private TableDataSource<String>   tableDataSource  = new TableDataSourceMap<String>( this.map, this.columnTitleKey,
                                                                                       this.columnTitleValue );
  private Table<String>             table            = new ArrayTable<String>();
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    for ( int ii = 0; ii < 100; ii++ )
    {
      this.map.put( this.columnTitleKey + ii, this.columnTitleValue + ii );
    }
  }
  
  @Test
  public void testRows()
  {
    //
    this.table.copyFrom( this.tableDataSource );
    //System.out.println( this.table );
    
    //
    assertEquals( 100, this.table.getTableSize().getRowSize() );
    assertEquals( Arrays.asList( this.columnTitleKey, this.columnTitleValue ), this.table.getColumnTitleValueList() );
    assertEquals( Arrays.asList( this.columnTitleKey + "0", this.columnTitleValue + "0" ), this.table.getRow( 0 )
                                                                                                     .getCellElementList() );
  }
}

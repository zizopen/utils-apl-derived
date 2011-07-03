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
package org.omnaest.utils.structure.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.Table.RowList;
import org.omnaest.utils.structure.table.Table.TableCellConverter;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.concrete.IndexArrayTable;
import org.omnaest.utils.time.DurationCapture;

public class IndexArrayTableTest
{
  @Test
  public void testTranspose()
  {
    Table<Integer> indexedTable = new IndexArrayTable<Integer>();
    indexedTable.putArray( new Integer[][] { { 11, 12, 13 }, { 21, 22, 23 }, { 31, 32, 33 } }, 0, 0 );
    
    indexedTable.transpose();
    
    Table<Integer> indexedTableTransposed = new IndexArrayTable<Integer>();
    indexedTableTransposed.putArray( new Integer[][] { { 11, 21, 31 }, { 12, 22, 32 }, { 13, 23, 33 } }, 0, 0 );
    
    assertEquals( true, indexedTableTransposed.equals( indexedTable ) );
    
  }
  
  /**
   * @see IndexArrayTableTest#testAddRowWithClass()
   * @author Omnaest
   */
  public class JavaBeanMock1
  {
    private String property1 = null;
    private String property2 = null;
    
    public String getProperty1()
    {
      return property1;
    }
    
    public void setProperty1( String property1 )
    {
      this.property1 = property1;
    }
    
    public String getProperty2()
    {
      return property2;
    }
    
    public void setProperty2( String property2 )
    {
      this.property2 = property2;
    }
  }
  
  /**
   * @see IndexArrayTableTest#testAddRowWithClass()
   * @author Omnaest
   */
  public class JavaBeanMock2
  {
    private String property3 = null;
    private String property2 = null;
    
    public String getProperty3()
    {
      return property3;
    }
    
    public void setProperty3( String property3 )
    {
      this.property3 = property3;
    }
    
    public String getProperty2()
    {
      return property2;
    }
    
    public void setProperty2( String property2 )
    {
      this.property2 = property2;
    }
  }
  
  @Test
  public void testAddPutGetRowByClass()
  {
    /*
     * Inserting data from a class object into the table
     */
    JavaBeanMock1 beanMock1 = new JavaBeanMock1();
    beanMock1.setProperty1( "test1" );
    beanMock1.setProperty2( "test2" );
    
    JavaBeanMock2 beanMock2 = new JavaBeanMock2();
    beanMock2.setProperty3( "test3" );
    beanMock2.setProperty2( "test2" );
    
    @SuppressWarnings("rawtypes")
    Table<?> indexedTable = new IndexArrayTable();
    
    indexedTable.addRow( beanMock1 );
    indexedTable.addRow( beanMock2 );
    indexedTable.setRow( 2, beanMock1 );
    
    /*
        .....................
        !property2!property1!
        :    test2:    test1:
        :    test2:     null:
        :    test2:    test1:
        .....................
     */
    assertEquals( 2, indexedTable.getTableSize().getColumnSize() );
    assertEquals( 3, indexedTable.getTableSize().getRowSize() );
    assertEquals( beanMock1.getProperty1(), indexedTable.getCell( 0, "property1" ) );
    assertEquals( beanMock1.getProperty2(), indexedTable.getCell( 0, "property2" ) );
    assertEquals( null, indexedTable.getCell( 1, "property1" ) );
    assertEquals( beanMock2.getProperty2(), indexedTable.getCell( 1, "property2" ) );
    assertEquals( beanMock1.getProperty1(), indexedTable.getCell( 2, "property1" ) );
    assertEquals( beanMock1.getProperty2(), indexedTable.getCell( 2, "property2" ) );
    
    /*
     * Getting an class object from the table data
     */
    //
    JavaBeanMock1 beanMockResolve = indexedTable.getRow( 2, new JavaBeanMock1() );
    
    //
    assertNotNull( beanMockResolve );
    assertEquals( beanMock1.getProperty1(), beanMockResolve.getProperty1() );
    assertEquals( beanMock1.getProperty2(), beanMockResolve.getProperty2() );
    
  }
  
  @Test
  public void testRemoveRows()
  {
    //
    Table<String> indexedTable = new IndexArrayTable<String>();
    indexedTable.insertArray( new String[][] { { "a", "0", "0", "0" }, { "b", "1", "1", "1" }, { "c", "2", "1", "2" },
        { "d", "3", "2", null } }, 0, 0 );
    indexedTable.setColumnTitles( LeftColumnTitleEnum.values() );
    indexedTable.setTableName( "remove rows table" );
    
    //
    indexedTable.removeRows( new int[] { 1, 3 } );
    
    //
    //IndexedArrayTable.printTable(indexedTable);
    
    //
    assertEquals( 2, indexedTable.getTableSize().getRowSize() );
    assertEquals( "0", indexedTable.getCell( 0, 2 ) );
    assertEquals( "1", indexedTable.getCell( 1, 2 ) );
    
  }
  
  private enum WhereColumnTitleEnum
  {
    column1,
    column2,
    column3,
    column4
  }
  
  @Test
  public void testWhereElementEquals()
  {
    //
    IndexTable<String> indexedTable = new IndexArrayTable<String>();
    indexedTable.insertArray( new String[][] { { "a", "0", "0", "0" }, { "b", "1", "1", "1" }, { "c", "2", "1", "2" },
        { "d", "3", "2", null } }, 0, 0 );
    indexedTable.setColumnTitles( LeftColumnTitleEnum.values() );
    indexedTable.setTableName( "where table" );
    
    //
    Table<String> result = indexedTable.whereElementEquals( 2, "1" );
    
    //IndexedArrayTable.printTable(indexedTable);
    
    //
    assertEquals( 2, result.getTableSize().getRowSize() );
    assertEquals( "1", result.getCell( 0, 2 ) );
    assertEquals( "1", result.getCell( 1, 2 ) );
    
    /*
     * column enums
     */

    //
    indexedTable.setColumnTitles( WhereColumnTitleEnum.values() );
    
    Map<Enum<?>, String> columnEnumToElementMap = new LinkedHashMap<Enum<?>, String>();
    columnEnumToElementMap.put( WhereColumnTitleEnum.column2, "2" );
    columnEnumToElementMap.put( WhereColumnTitleEnum.column3, "1" );
    
    //
    result = indexedTable.whereElementEqualsColumnEnumMap( columnEnumToElementMap );
    
    //
    assertEquals( 1, result.getTableSize().getRowSize() );
    assertEquals( "c", result.getCell( 0 ) );
    
  }
  
  @Test
  public void testWhereElementsBetween()
  {
    IndexTable<String> indexedTable = new IndexArrayTable<String>();
    indexedTable.insertArray( new String[][] { { "a", "0", "0", "0" }, { "b", "1", "1", "1" }, { "c", "2", "1", "2" },
        { "d", "3", "2", null } }, 0, 0 );
    indexedTable.setColumnTitles( LeftColumnTitleEnum.values() );
    indexedTable.setTableName( "where table" );
    
    //
    indexedTable = (IndexTable<String>) indexedTable.whereElementIsBetween( 1, "2", "3" );
    
    //IndexedArrayTable.printTable(indexedTable);
    
    //
    assertEquals( 2, indexedTable.getTableSize().getRowSize() );
    assertEquals( "2", indexedTable.getCell( 0, 1 ) );
    assertEquals( "3", indexedTable.getCell( 1, 1 ) );
    
  }
  
  @Test
  public void testWhereElementsGreaterThan()
  {
    IndexTable<String> indexedTable = new IndexArrayTable<String>();
    indexedTable.insertArray( new String[][] { { "a", "0", "0", "0" }, { "b", "1", "1", "1" }, { "c", "2", "1", "2" },
        { "d", "3", "2", null } }, 0, 0 );
    indexedTable.setColumnTitles( LeftColumnTitleEnum.values() );
    indexedTable.setTableName( "where > table" );
    
    //
    indexedTable = (IndexTable<String>) indexedTable.whereElementIsGreaterThan( 1, "1" );
    
    //IndexedArrayTable.printTable(indexedTable);
    
    //
    assertEquals( 2, indexedTable.getTableSize().getRowSize() );
    assertEquals( "2", indexedTable.getCell( 0, 1 ) );
    assertEquals( "3", indexedTable.getCell( 1, 1 ) );
    
  }
  
  @Test
  public void testWhereElementsLesserThan()
  {
    IndexTable<String> indexedTable = new IndexArrayTable<String>();
    indexedTable.insertArray( new String[][] { { "a", "0", "0", "0" }, { "b", "1", "1", "1" }, { "c", "2", "1", "2" },
        { "d", "3", "2", null } }, 0, 0 );
    indexedTable.setColumnTitles( LeftColumnTitleEnum.values() );
    indexedTable.setTableName( "where < table" );
    
    //
    indexedTable = (IndexTable<String>) indexedTable.whereElementIsLesserThan( 1, "2" );
    
    //IndexedArrayTable.printTable(indexedTable);
    
    //
    assertEquals( 2, indexedTable.getTableSize().getRowSize() );
    assertEquals( "0", indexedTable.getCell( 0, 1 ) );
    assertEquals( "1", indexedTable.getCell( 1, 1 ) );
    
  }
  
  @Test
  public void testOrderRowsBy()
  {
    //
    IndexTable<Integer> indexedTable = new IndexArrayTable<Integer>();
    for ( int ii = 0; ii < 100; ii++ )
    {
      for ( int jj = 0; jj < 4; jj++ )
      {
        Integer randomValue;
        if ( jj > 0 )
        {
          randomValue = (int) Math.round( Math.random() * ( ii + 1 ) * ( jj + 1 ) );
        }
        else
        {
          randomValue = ii;
        }
        indexedTable.setCell( ii, jj, randomValue );
      }
    }
    
    indexedTable.setColumnTitles( RightColumnTitleEnum.values() );
    indexedTable.setTableName( "order table" );
    indexedTable.setIndexColumn( 1, true );
    
    //
    List<Integer> compareColumn = indexedTable.getColumn( 3 );
    Collections.sort( compareColumn );
    
    List<Integer> testRow = indexedTable.getRow( 20 );
    
    //
    indexedTable.orderRowsBy( 3, true );
    
    //
    //IndexedArrayTable.printTable(indexedTable);
    
    //
    for ( int ii = 0; ii < compareColumn.size(); ii++ )
    {
      assertEquals( compareColumn.get( ii ), indexedTable.getCell( ii, 3 ) );
    }
    
    //
    List<Integer> rowAfter = indexedTable.getRow( indexedTable.indexOfFirstRowWithElementEquals( 0, testRow.get( 0 ) ) );
    assertEquals( testRow, rowAfter );
  }
  
  @Test
  public void testPutTable()
  {
    Table<String> insertIndexedTable = new IndexArrayTable<String>();
    
    List<String> column = new ArrayList<String>( 0 );
    column.add( "a" );
    column.add( "b" );
    column.add( "c" );
    
    List<String> row = new ArrayList<String>( 0 );
    row.add( "d" );
    row.add( "e" );
    row.add( "f" );
    
    insertIndexedTable.addColumn( column );
    insertIndexedTable.addRow( row );
    insertIndexedTable.setColumnTitles( ColumnTitleEnum.values() );
    
    IndexArrayTable<String> indexedTable = new IndexArrayTable<String>();
    column = new ArrayList<String>( 0 );
    column.add( "x" );
    column.add( "y" );
    column.add( "z" );
    indexedTable.addColumn( column );
    indexedTable.putTable( insertIndexedTable, 1, 1 );
    
    //IndexedArrayTable.printTable(indexedTable);
    
    assertEquals( "a", indexedTable.getCell( 1, 1 ) );
    assertEquals( "b", indexedTable.getCell( 2, 1 ) );
    assertEquals( "c", indexedTable.getCell( 3, 1 ) );
    assertEquals( "d", indexedTable.getCell( 4, 1 ) );
    assertEquals( "e", indexedTable.getCell( 4, 2 ) );
    assertEquals( "f", indexedTable.getCell( 4, 3 ) );
    
    assertEquals( ColumnTitleEnum.c1.name(), indexedTable.getColumnTitle( 1 ) );
    assertEquals( ColumnTitleEnum.c2.name(), indexedTable.getColumnTitle( 2 ) );
    assertEquals( ColumnTitleEnum.c3.name(), indexedTable.getColumnTitle( 3 ) );
  }
  
  @Test
  public void testInsertTable()
  {
    Table<String> insertIndexedTable = new IndexArrayTable<String>();
    
    List<String> column = new ArrayList<String>( 0 );
    column.add( "a" );
    column.add( "b" );
    column.add( "c" );
    
    List<String> row = new ArrayList<String>( 0 );
    row.add( "d" );
    row.add( "e" );
    row.add( "f" );
    
    insertIndexedTable.addColumn( column );
    insertIndexedTable.addRow( row );
    insertIndexedTable.setColumnTitles( ColumnTitleEnum.values() );
    
    IndexArrayTable<String> indexedTable = new IndexArrayTable<String>();
    column = new ArrayList<String>( 0 );
    column.add( "a" );
    column.add( "b" );
    column.add( "c" );
    indexedTable.addColumn( column );
    
    indexedTable.insertTable( insertIndexedTable, 1, 1 );
    
    //IndexedArrayTable.printTable(indexedTable);
    
    assertEquals( "a", indexedTable.getCell( 1, 1 ) );
    assertEquals( "b", indexedTable.getCell( 2, 1 ) );
    assertEquals( "c", indexedTable.getCell( 3, 1 ) );
    assertEquals( "d", indexedTable.getCell( 4, 1 ) );
    assertEquals( "e", indexedTable.getCell( 4, 2 ) );
    assertEquals( "f", indexedTable.getCell( 4, 3 ) );
    
    assertEquals( ColumnTitleEnum.c1.name(), indexedTable.getColumnTitle( 1 ) );
    assertEquals( ColumnTitleEnum.c2.name(), indexedTable.getColumnTitle( 2 ) );
    assertEquals( ColumnTitleEnum.c3.name(), indexedTable.getColumnTitle( 3 ) );
  }
  
  @Test
  public void testInsertArray()
  {
    Table<String> indexedTable = new IndexArrayTable<String>();
    
    indexedTable.insertArray( new String[][] { { "a" }, { "b" }, { "c" }, { "d", "e", "f" } }, 1, 1 );
    
    //indexedTable.printTable();
    
    assertEquals( "a", indexedTable.getCell( 1, 1 ) );
    assertEquals( "b", indexedTable.getCell( 2, 1 ) );
    assertEquals( "c", indexedTable.getCell( 3, 1 ) );
    assertEquals( "d", indexedTable.getCell( 4, 1 ) );
    assertEquals( "e", indexedTable.getCell( 4, 2 ) );
    assertEquals( "f", indexedTable.getCell( 4, 3 ) );
  }
  
  private enum LeftColumnTitleEnum
  {
    lc1,
    lc2,
    lc3,
    lc4
  }
  
  private enum RightColumnTitleEnum
  {
    rc1,
    rc2,
    rc3,
    rc4
  }
  
  @Test
  public void testInnerJoin()
  {
    //
    Table<String> leftIndexedTable = new IndexArrayTable<String>();
    leftIndexedTable.insertArray( new String[][] { { "a", "0", "0", "0" }, { "b", "1", "1", "1" }, { "c", "2", "1", "2" },
        { "d", "3", "2", null } }, 0, 0 );
    leftIndexedTable.setColumnTitles( LeftColumnTitleEnum.values() );
    leftIndexedTable.setTableName( "left table" );
    
    //
    IndexTable<String> rightIndexedTable = new IndexArrayTable<String>();
    rightIndexedTable.insertArray( new String[][] { { "e", "0", "0", "0" }, { "f", "1", "1", "1" }, { "g", "2", "1", null },
        { "h", "3", "2", "3" } }, 0, 0 );
    rightIndexedTable.setColumnTitles( RightColumnTitleEnum.values() );
    rightIndexedTable.setTableName( "right table" );
    rightIndexedTable.setIndexColumn( 1, true );
    
    //
    Table<String> joinedTable = leftIndexedTable.innerJoinByEqualColumn( rightIndexedTable, new int[][] { { 1, 1 } } );
    joinedTable.setTableName( "lc2-rc2" );
    
    //
    //IndexedArrayTable.printTable(leftIndexedTable);
    //IndexedArrayTable.printTable(rightIndexedTable);
    
    //IndexedArrayTable.printTable(joinedTable);
    
    //
    assertEquals( 16 + 16, joinedTable.getTableSize().getCellSize() );
    assertEquals( "a", joinedTable.getCell( 0, 0 ) );
    assertEquals( "b", joinedTable.getCell( 1, 0 ) );
    assertEquals( "c", joinedTable.getCell( 2, 0 ) );
    assertEquals( "d", joinedTable.getCell( 3, 0 ) );
    assertEquals( "0", joinedTable.getCell( 0, 1 ) );
    assertEquals( "1", joinedTable.getCell( 1, 1 ) );
    assertEquals( "2", joinedTable.getCell( 2, 1 ) );
    assertEquals( "3", joinedTable.getCell( 3, 1 ) );
    assertEquals( "0", joinedTable.getCell( 0, 2 ) );
    assertEquals( "1", joinedTable.getCell( 1, 2 ) );
    assertEquals( "1", joinedTable.getCell( 2, 2 ) );
    assertEquals( "2", joinedTable.getCell( 3, 2 ) );
    assertEquals( "0", joinedTable.getCell( 0, 3 ) );
    assertEquals( "1", joinedTable.getCell( 1, 3 ) );
    assertEquals( "2", joinedTable.getCell( 2, 3 ) );
    assertEquals( null, joinedTable.getCell( 3, 3 ) );
    
    assertEquals( "e", joinedTable.getCell( 0, 4 ) );
    assertEquals( "f", joinedTable.getCell( 1, 4 ) );
    assertEquals( "g", joinedTable.getCell( 2, 4 ) );
    assertEquals( "h", joinedTable.getCell( 3, 4 ) );
    assertEquals( "0", joinedTable.getCell( 0, 5 ) );
    assertEquals( "1", joinedTable.getCell( 1, 5 ) );
    assertEquals( "2", joinedTable.getCell( 2, 5 ) );
    assertEquals( "3", joinedTable.getCell( 3, 5 ) );
    assertEquals( "0", joinedTable.getCell( 0, 6 ) );
    assertEquals( "1", joinedTable.getCell( 1, 6 ) );
    assertEquals( "1", joinedTable.getCell( 2, 6 ) );
    assertEquals( "2", joinedTable.getCell( 3, 6 ) );
    assertEquals( "0", joinedTable.getCell( 0, 7 ) );
    assertEquals( "1", joinedTable.getCell( 1, 7 ) );
    assertEquals( null, joinedTable.getCell( 2, 7 ) );
    assertEquals( "3", joinedTable.getCell( 3, 7 ) );
    
    //
    joinedTable = leftIndexedTable.innerJoinByEqualColumn( rightIndexedTable, new int[][] { { 1, 2 } } );
    joinedTable.setTableName( "lc2-rc3" );
    
    //IndexedArrayTable.printTable(joinedTable);
    
    //0113 with 0112
    joinedTable = leftIndexedTable.innerJoinByEqualColumn( rightIndexedTable, new int[][] { { 2, 2 } } );
    joinedTable.setTableName( "lc3-rc3" );
    //IndexedArrayTable.printTable(joinedTable);
    
    //0123 with 01null3
    joinedTable = leftIndexedTable.innerJoinByEqualColumn( rightIndexedTable, new int[][] { { 3, 1 } } );
    joinedTable.setTableName( "lc4-rc2" );
    
    //IndexedArrayTable.printTable(joinedTable);
    
    //012null with 01null3
    joinedTable = leftIndexedTable.innerJoinByEqualColumn( rightIndexedTable, new int[][] { { 3, 3 } } );
    joinedTable.setTableName( "lc4-rc4" );
    
    //IndexedArrayTable.printTable(joinedTable);
    
    /*
    //Performance of the inner join
    TimeDuration duration = new TimeDuration();
    IndexedTable<Integer> indexedTable = new IndexedArrayTable<Integer>();
    for (int ii = 0; ii < 20000; ii++)
    {
      List<Integer> row = new ArrayList<Integer>(5);
      for (int jj = 0; jj < 5; jj++)
      {
        row.add(Integer.valueOf(ii));
      }
      indexedTable.addRow(row);
    }
    IndexedTable<Integer> resultTable;
    duration.startTimeMeasurement();
    resultTable = indexedTable.innerJoinByEqualColumn(indexedTable, new int[][] { { 1, 1 } });
    duration.stopTimeMeasurement();

    System.out.println(duration.getDuration() + ":" + resultTable.getTableSize().getRowSize());
    */
  }
  
  @Test
  public void testPutAndGet()
  {
    Table<String> indexedTable = new IndexArrayTable<String>();
    
    String writtenString = "Hallo";
    indexedTable.setCell( 0, 0, writtenString );
    String resolvedString = indexedTable.getCell( 0, 0 );
    
    assertNotNull( resolvedString );
    assertEquals( writtenString, resolvedString );
    
    indexedTable.setCell( 10, 20, writtenString );
    resolvedString = indexedTable.getCell( 10, 20 );
    
    assertNotNull( resolvedString );
    assertEquals( writtenString, resolvedString );
  }
  
  @Test
  public void testAddColumnAndRow()
  {
    Table<String> indexedTable = new IndexArrayTable<String>();
    
    List<String> column = new ArrayList<String>( 0 );
    column.add( "a" );
    column.add( "b" );
    column.add( "c" );
    
    List<String> row = new ArrayList<String>( 0 );
    row.add( "d" );
    row.add( "e" );
    row.add( "f" );
    
    indexedTable.addColumn( column );
    indexedTable.addRow( row );
    
    //IndexedArrayTable.printTable(indexedTable.printTable);
    
    assertEquals( "a", indexedTable.getCell( 0, 0 ) );
    assertEquals( "b", indexedTable.getCell( 1, 0 ) );
    assertEquals( "c", indexedTable.getCell( 2, 0 ) );
    assertEquals( "d", indexedTable.getCell( 3, 0 ) );
    assertEquals( "e", indexedTable.getCell( 3, 1 ) );
    assertEquals( "f", indexedTable.getCell( 3, 2 ) );
  }
  
  @SuppressWarnings("unused")
  @Test
  public void testSetIndexColumnAndRow()
  {
    IndexTable<String> indexedTable = new IndexArrayTable<String>();
    
    List<String> column = new ArrayList<String>( 0 );
    column.add( "a" );
    column.add( "b" );
    column.add( "c" );
    
    List<String> row = new ArrayList<String>( 0 );
    row.add( "d" );
    row.add( "e" );
    row.add( "f" );
    
    indexedTable.addColumn( column );
    indexedTable.addRow( row );
    
    indexedTable.setIndexColumn( 0, true );
    indexedTable.setIndexRow( 3, true );
    
    //indexedTable.printTable();
    
    assertEquals( "a", indexedTable.getCell( 0, 0 ) );
    assertEquals( "b", indexedTable.getCell( 1, 0 ) );
    assertEquals( "c", indexedTable.getCell( 2, 0 ) );
    assertEquals( "d", indexedTable.getCell( 3, 0 ) );
    assertEquals( "e", indexedTable.getCell( 3, 1 ) );
    assertEquals( "f", indexedTable.getCell( 3, 2 ) );
    assertEquals( true, indexedTable.isColumnIndexed( 0 ) );
    assertEquals( true, indexedTable.isRowIndexed( 3 ) );
    
    assertNotNull( indexedTable.indexesOfRowsWithElementsEquals( 0, "b" ) );
    assertEquals( 1, indexedTable.indexesOfRowsWithElementsEquals( 0, "b" )[0] );
    //
    indexedTable.setRowTitles( RowTitleEnum.values() );
    indexedTable.setColumnTitles( ColumnTitleEnum.values() );
    
    //
    indexedTable.setIndexColumn( 0, false );
    indexedTable.setIndexRow( 3, false );
    
    //
    indexedTable.setIndexColumn( ColumnTitleEnum.c1, true );
    assertEquals( true, indexedTable.isColumnIndexed( ColumnTitleEnum.c1 ) );
    
    indexedTable.setIndexColumn( ColumnTitleEnum.c1.name(), false );
    assertEquals( false, indexedTable.isColumnIndexed( ColumnTitleEnum.c1.name() ) );
    
    indexedTable.setIndexRow( RowTitleEnum.r4, true );
    assertEquals( true, indexedTable.isRowIndexed( RowTitleEnum.r4 ) );
    
    indexedTable.setIndexRow( RowTitleEnum.r4.name(), false );
    assertEquals( false, indexedTable.isRowIndexed( RowTitleEnum.r4.name() ) );
    
    //
    indexedTable.clear();
    
    /*
     * Performance
     */
    int rowNumber = 2000;
    int columnNumber = 5;
    
    //load data
    for ( int ii = 0; ii < rowNumber; ii++ )
    {
      for ( int jj = 0; jj < columnNumber; jj++ )
      {
        indexedTable.setCell( ii, jj, String.valueOf( Math.abs( Math.random() * rowNumber ) ) );
      }
    }
    
    //
    long durationWithoutIndexes = 0;
    long durationWithIndexes = 0;
    
    int searchNumber = 0;
    
    //perform search
    DurationCapture duration = DurationCapture.newInstance().startTimeMeasurement();
    for ( int ii = 0; duration.getInterimTimeInMilliseconds() < 200; ii++ )
    {
      indexedTable.indexOf( String.valueOf( Math.abs( Math.random() * rowNumber ) ) );
      searchNumber = ii;
    }
    duration.stopTimeMeasurement();
    durationWithoutIndexes = duration.getDurationInMilliseconds();
    
    //activate indexes of table
    for ( int ii = 0; ii < columnNumber; ii++ )
    {
      indexedTable.setIndexColumn( ii, true );
    }
    
    //perform search
    duration.startTimeMeasurement();
    for ( int ii = 0; ii < searchNumber; ii++ )
    {
      indexedTable.indexOf( String.valueOf( Math.abs( Math.random() * rowNumber ) ) );
    }
    duration.stopTimeMeasurement();
    durationWithIndexes = duration.getDurationInMilliseconds();
    
    //System.out.println( searchNumber + "->" + durationWithoutIndexes + ":" + durationWithIndexes );
    //assertEquals( true, durationWithoutIndexes > durationWithIndexes );
  }
  
  private enum RowTitleEnum
  {
    r1,
    r2,
    r3,
    r4
  }
  
  private enum ColumnTitleEnum
  {
    c1,
    c2,
    c3
  }
  
  @Test
  public void testSetRowAndColumnTitles()
  {
    Table<String> indexedTable = new IndexArrayTable<String>();
    
    //titles
    indexedTable.setRowTitles( RowTitleEnum.values() );
    indexedTable.setColumnTitles( ColumnTitleEnum.values() );
    
    //data
    List<String> column = new ArrayList<String>( 0 );
    column.add( "a" );
    column.add( "b" );
    column.add( "c" );
    
    List<String> row = new ArrayList<String>( 0 );
    row.add( "d" );
    row.add( "e" );
    row.add( "f" );
    
    indexedTable.addColumn( column );
    indexedTable.addRow( row );
    
    //indexedTable.printTable();
    
    assertEquals( "a", indexedTable.getCell( 0, 0 ) );
    assertEquals( "b", indexedTable.getCell( 1, 0 ) );
    assertEquals( "c", indexedTable.getCell( 2, 0 ) );
    assertEquals( "d", indexedTable.getCell( 3, 0 ) );
    assertEquals( "e", indexedTable.getCell( 3, 1 ) );
    assertEquals( "f", indexedTable.getCell( 3, 2 ) );
    
    assertEquals( "e", indexedTable.getCell( RowTitleEnum.r4, 1 ) );
    assertEquals( "e", indexedTable.getCell( 3, ColumnTitleEnum.c2 ) );
    assertEquals( "e", indexedTable.getCell( RowTitleEnum.r4, ColumnTitleEnum.c2 ) );
    
    assertEquals( "e", indexedTable.getCell( RowTitleEnum.r4.name(), 1 ) );
    assertEquals( "e", indexedTable.getCell( 3, ColumnTitleEnum.c2.name() ) );
    assertEquals( "e", indexedTable.getCell( RowTitleEnum.r4.name(), ColumnTitleEnum.c2.name() ) );
    
    //
    List<String> columnList;
    
    columnList = indexedTable.getColumn( 0 );
    assertEquals( 4, columnList.size() );
    assertEquals( "a", columnList.get( 0 ) );
    assertEquals( "b", columnList.get( 1 ) );
    assertEquals( "c", columnList.get( 2 ) );
    assertEquals( "d", columnList.get( 3 ) );
    
    columnList = indexedTable.getColumn( ColumnTitleEnum.c1 );
    assertEquals( 4, columnList.size() );
    assertEquals( "a", columnList.get( 0 ) );
    assertEquals( "b", columnList.get( 1 ) );
    assertEquals( "c", columnList.get( 2 ) );
    assertEquals( "d", columnList.get( 3 ) );
    
    columnList = indexedTable.getColumn( ColumnTitleEnum.c1.name() );
    assertEquals( 4, columnList.size() );
    assertEquals( "a", columnList.get( 0 ) );
    assertEquals( "b", columnList.get( 1 ) );
    assertEquals( "c", columnList.get( 2 ) );
    assertEquals( "d", columnList.get( 3 ) );
    
    //
    List<String> rowList;
    rowList = indexedTable.getRow( 3 );
    assertEquals( 3, rowList.size() );
    assertEquals( "d", rowList.get( 0 ) );
    assertEquals( "e", rowList.get( 1 ) );
    assertEquals( "f", rowList.get( 2 ) );
    
    rowList = indexedTable.getRow( RowTitleEnum.r4 );
    assertEquals( 3, rowList.size() );
    assertEquals( "d", rowList.get( 0 ) );
    assertEquals( "e", rowList.get( 1 ) );
    assertEquals( "f", rowList.get( 2 ) );
    
    rowList = indexedTable.getRow( RowTitleEnum.r4.name() );
    assertEquals( 3, rowList.size() );
    assertEquals( "d", rowList.get( 0 ) );
    assertEquals( "e", rowList.get( 1 ) );
    assertEquals( "f", rowList.get( 2 ) );
    
    //
  }
  
  @Test
  public void testGetSubTable()
  {
    IndexTable<String> indexedTable = new IndexArrayTable<String>();
    
    //titles
    indexedTable.setRowTitles( RowTitleEnum.values() );
    indexedTable.setColumnTitles( ColumnTitleEnum.values() );
    
    //data
    List<String> column = new ArrayList<String>( 0 );
    column.add( "a" );
    column.add( "b" );
    column.add( "c" );
    
    List<String> row = new ArrayList<String>( 0 );
    row.add( "d" );
    row.add( "e" );
    row.add( "f" );
    
    indexedTable.addColumn( column );
    indexedTable.addRow( row );
    
    //
    Table<String> subTable;
    subTable = indexedTable.getSubTableByRows( 1, 2 );
    
    //IndexedArrayTable.printTable(subTable);
    
    assertEquals( 2, subTable.getTableSize().getRowSize() );
    assertEquals( "b", subTable.getCell( 0, 0 ) );
    assertEquals( "c", subTable.getCell( 1, 0 ) );
    
    //
    subTable = indexedTable.getSubTableByColumns( 0, 1 );
    
    // IndexedArrayTable.printTable(subTable);
    
    assertEquals( 4, subTable.getTableSize().getRowSize() );
    assertEquals( 2, subTable.getTableSize().getColumnSize() );
    
    assertEquals( "a", subTable.getCell( 0, 0 ) );
    assertEquals( "b", subTable.getCell( 1, 0 ) );
    assertEquals( "c", subTable.getCell( 2, 0 ) );
    assertEquals( "d", subTable.getCell( 3, 0 ) );
    
    //
    indexedTable.setIndexColumn( 0, true );
    
    subTable = indexedTable.getSubTable( 1, 3, 0, 1 );
    
    //IndexedArrayTable.printTable(subTable);
    
    assertEquals( 3, subTable.getTableSize().getRowSize() );
    assertEquals( 2, subTable.getTableSize().getColumnSize() );
    
    assertEquals( "b", subTable.getCell( 0, 0 ) );
    assertEquals( "c", subTable.getCell( 1, 0 ) );
    assertEquals( "d", subTable.getCell( 2, 0 ) );
    assertEquals( "e", subTable.getCell( 2, 1 ) );
    
  }
  
  @Test
  public void getRowList()
  {
    //
    Table<Integer> naturalIndexedTable = new IndexArrayTable<Integer>();
    naturalIndexedTable.putArray( new Integer[][] { { 11, 12, 13 }, { 21, 22, 23 }, { 31, 32, 33 } }, 0, 0 );
    
    Table<Integer> listbackedIndexedTable = naturalIndexedTable.clone();
    assertEquals( true, naturalIndexedTable.equals( listbackedIndexedTable ) );
    
    /*
     * get
     */
    RowList<Integer> backedRowList = listbackedIndexedTable.getRowList();
    
    for ( int rowIndexPosition = 0; rowIndexPosition < naturalIndexedTable.getTableSize().getRowSize(); rowIndexPosition++ )
    {
      List<Integer> iTableRow = naturalIndexedTable.getRow( rowIndexPosition );
      List<Integer> row = backedRowList.get( rowIndexPosition++ );
      assertEquals( iTableRow, row );
    }
    
    //TableHelper.printTable( naturalIndexedTable );
    //TableHelper.printTable( listbackedIndexedTable );
    
    /*
     * add
     * add(index)
     * addAll
     * addAll(index)
     * containsAll
     * indexOf
     * lastIndexOf
     */
    List<List<Integer>> rowList = new ArrayList<List<Integer>>();
    rowList.add( Arrays.asList( new Integer[] { 1, 2, 3 } ) );
    rowList.add( Arrays.asList( new Integer[] { 4, 5, 6 } ) );
    rowList.add( Arrays.asList( new Integer[] { 7, 8, 9 } ) );
    backedRowList.addAll( rowList );
    for ( List<Integer> iRow : rowList )
    {
      naturalIndexedTable.addRow( iRow );
    }
    
    assertEquals( true, naturalIndexedTable.equals( listbackedIndexedTable ) );
    
    rowList = new ArrayList<List<Integer>>();
    rowList.add( Arrays.asList( new Integer[] { 7, 8, 9 } ) );
    rowList.add( Arrays.asList( new Integer[] { 10, 11, 12 } ) );
    backedRowList.addAll( 0, rowList );
    {
      int rowIndexPosition = 0;
      for ( List<Integer> iRow : rowList )
      {
        naturalIndexedTable.addRow( rowIndexPosition++, iRow );
      }
    }
    
    assertEquals( true, naturalIndexedTable.equals( listbackedIndexedTable ) );
    assertEquals( true, backedRowList.containsAll( rowList ) );
    assertEquals( 0, backedRowList.indexOf( Arrays.asList( new Integer[] { 7, 8, 9 } ) ) );
    assertEquals( backedRowList.size() - 1, backedRowList.lastIndexOf( Arrays.asList( new Integer[] { 7, 8, 9 } ) ) );
    assertEquals( 3, backedRowList.get( 0 ).size() );
    assertEquals( 3, backedRowList.get( backedRowList.size() - 1 ).size() );
    
    //TableHelper.printTable( naturalIndexedTable );
    //TableHelper.printTable( listbackedIndexedTable );
    
    /*
     * remove
     * remove(index)
     * removeAll
     * retainAll
     */
    rowList = new ArrayList<List<Integer>>();
    rowList.add( Arrays.asList( new Integer[] { 1, 2, 3 } ) );
    rowList.add( Arrays.asList( new Integer[] { 4, 5, 6 } ) );
    rowList.add( Arrays.asList( new Integer[] { 7, 8, 9 } ) );
    
    backedRowList.retainAll( rowList );
    backedRowList.remove( 1 );
    {
      //
      List<Integer> rowIndexPositionList = new ArrayList<Integer>();
      
      //
      for ( int rowIndexPosition = 0; rowIndexPosition < naturalIndexedTable.getTableSize().getRowSize(); rowIndexPosition++ )
      {
        List<Integer> iRow = naturalIndexedTable.getRow( rowIndexPosition );
        if ( !rowList.contains( iRow ) )
        {
          rowIndexPositionList.add( rowIndexPosition );
        }
      }
      
      //remove the index positions, from the bigest to the lowest index, to avoid wrong index positions cause of removed earlier elements.
      Collections.sort( rowIndexPositionList, new Comparator<Integer>()
      {
        @Override
        public int compare( Integer o1, Integer o2 )
        {
          return -1 * o1.compareTo( o2 );
        }
      } );
      for ( Integer rowIndexPosition : rowIndexPositionList )
      {
        naturalIndexedTable.removeRow( rowIndexPosition );
      }
      
      //
      naturalIndexedTable.removeRow( 1 );
    }
    
    //TableHelper.printTable( naturalIndexedTable );
    //TableHelper.printTable( listbackedIndexedTable );
    
    assertEquals( true, naturalIndexedTable.equals( listbackedIndexedTable ) );
    
    /*
     * set
     */
    backedRowList.set( 1, Arrays.asList( new Integer[] { 1, 2, 3 } ) );
    naturalIndexedTable.setRow( 1, Arrays.asList( new Integer[] { 1, 2, 3 } ) );
    assertEquals( true, naturalIndexedTable.equals( listbackedIndexedTable ) );
    
    /*
     * clear
     * isEmpty
     */
    //
    assertEquals( false, backedRowList.isEmpty() );
    
    //
    backedRowList.clear();
    naturalIndexedTable.clear();
    
    assertEquals( true, backedRowList.isEmpty() );
    assertEquals( true, naturalIndexedTable.equals( listbackedIndexedTable ) );
    
  }
  
  @Test
  public void testGetCellIterator()
  {
    //
    Table<Integer> naturalIndexedTable = new IndexArrayTable<Integer>();
    naturalIndexedTable.putArray( new Integer[][] { { 11, 12, 13 }, { 21, 22, 23 }, { 31, 32, 33 } }, 0, 0 );
    List<Integer> valueList = new ArrayList<Integer>( Arrays.asList( new Integer[] { 11, 12, 13, 21, 22, 23, 31, 32, 33 } ) );
    
    //
    Iterator<Integer> iterator = naturalIndexedTable.cellIterator();
    int index = 0;
    while ( iterator.hasNext() )
    {
      Integer listValue = valueList.get( index++ );
      Integer iteratorValue = iterator.next();
      assertEquals( listValue, iteratorValue );
      
      if ( index == 3 && listValue != null )
      {
        iterator.remove();
        valueList.set( --index, null );
      }
    }
    
  }
  
  @Test
  public void testGetCellList()
  {
    //
    Table<Integer> table = new IndexArrayTable<Integer>();
    table.putArray( new Integer[][] { { 11, 12, 13 }, { 21, 22, 23 }, { 31, 32, 33 }, { 11, 12, 13 } }, 0, 0 );
    List<Integer> valueList = Arrays.asList( new Integer[] { 11, 12, 13, 21, 22, 23, 31, 32, 33, 11, 12, 13 } );
    List<Integer> cellList = table.getCellList();
    
    //
    assertEquals( valueList.indexOf( 11 ), cellList.indexOf( 11 ) );
    assertEquals( valueList.indexOf( 13 ), cellList.indexOf( 13 ) );
    assertEquals( valueList.lastIndexOf( 11 ), cellList.lastIndexOf( 11 ) );
    assertEquals( valueList.lastIndexOf( 13 ), cellList.lastIndexOf( 13 ) );
  }
  
  /**
   * @see IndexArrayTableTest#testGetRowListGet()
   * @author Omnaest
   */
  private enum TableTitle
  {
    column1,
    column2,
    column3
  }
  
  @Test
  public void testGetRowListGet()
  {
    Table<Integer> table = new IndexArrayTable<Integer>();
    table.putArray( new Integer[][] { { 11, 12, 13 }, { 21, 22, 23 }, { 31, 32, 33 }, { 11, 12, 13 } }, 0, 0 );
    table.setColumnTitles( TableTitle.values() );
    
    Row<Integer> row = table.getRow( 0 );
    assertEquals( Integer.valueOf( 11 ), row.get( TableTitle.column1 ) );
    assertEquals( Integer.valueOf( 12 ), row.get( TableTitle.column2 ) );
    assertEquals( Integer.valueOf( 13 ), row.get( TableTitle.column3 ) );
    
    assertEquals( Integer.valueOf( 11 ), row.get( "column1" ) );
    assertEquals( Integer.valueOf( 12 ), row.get( "column2" ) );
    assertEquals( Integer.valueOf( 13 ), row.get( "column3" ) );
    
  }
  
  /**
   * @see IndexArrayTableTest#testGetRowAsBean()
   * @author Omnaest
   */
  public class BeanMock
  {
    private Integer property1 = null;
    private Integer property2 = null;
    
    public Integer getProperty1()
    {
      return this.property1;
    }
    
    public void setProperty1( Integer property1 )
    {
      this.property1 = property1;
    }
    
    public Integer getProperty2()
    {
      return this.property2;
    }
    
    public void setProperty2( Integer property2 )
    {
      this.property2 = property2;
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result + ( ( this.property1 == null ) ? 0 : this.property1.hashCode() );
      result = prime * result + ( ( this.property2 == null ) ? 0 : this.property2.hashCode() );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof BeanMock ) )
      {
        return false;
      }
      BeanMock other = (BeanMock) obj;
      if ( !getOuterType().equals( other.getOuterType() ) )
      {
        return false;
      }
      if ( this.property1 == null )
      {
        if ( other.property1 != null )
        {
          return false;
        }
      }
      else if ( !this.property1.equals( other.property1 ) )
      {
        return false;
      }
      if ( this.property2 == null )
      {
        if ( other.property2 != null )
        {
          return false;
        }
      }
      else if ( !this.property2.equals( other.property2 ) )
      {
        return false;
      }
      return true;
    }
    
    private IndexArrayTableTest getOuterType()
    {
      return IndexArrayTableTest.this;
    }
  }
  
  @Test
  public void testGetRowAsBean()
  {
    //
    IndexTable<Integer> table = new IndexArrayTable<Integer>();
    
    //
    BeanMock[] beanMock = new BeanMock[10];
    for ( int ii = 0; ii < beanMock.length; ii++ )
    {
      beanMock[ii] = new BeanMock();
      beanMock[ii].setProperty1( ii );
      beanMock[ii].setProperty2( 100 + ii );
      table.addRow( beanMock[ii] );
    }
    
    //
    assertEquals( beanMock.length, table.getTableSize().getRowSize() );
    
    //
    for ( int ii = 0; ii < beanMock.length; ii++ )
    {
      assertEquals( beanMock[ii], table.getRowAsBean( new BeanMock(), ii ) );
    }
    
    /*
     * with iterator
     */
    {
      //
      int ii = 0;
      for ( Row<Integer> row : table )
      {
        assertEquals( beanMock[ii++], row.asBeanAdapter( new BeanMock() ) );
      }
      
      //
      assertEquals( beanMock.length, ii );
    }
    
    /*
     * whereElementsEquals
     */

    //
    Table<Integer> result;
    
    //
    {
      result = table.whereElementEqualsBeanObject( beanMock[5] );
      
      assertEquals( 1, result.getTableSize().getRowSize() );
      assertEquals( beanMock[5], result.getRowAsBean( new BeanMock(), 0 ) );
    }
    
    //prepare the table to have two same property2 values
    {
      //
      beanMock[5].setProperty1( 55 );
      table.addRow( beanMock[5] );
      
      //
      beanMock[5].setProperty1( null );
      
      //
      result = table.whereElementEqualsBeanObjectIgnoringNullValues( beanMock[5] );
      
      //
      assertEquals( 2, result.getTableSize().getRowSize() );
      beanMock[5].setProperty1( 5 );
      assertEquals( beanMock[5], result.getRowAsBean( new BeanMock(), 0 ) );
      beanMock[5].setProperty1( 55 );
      assertEquals( beanMock[5], result.getRowAsBean( new BeanMock(), 1 ) );
    }
    
    /*
     * where is greater than
     */
    beanMock[5].setProperty1( 5 );
    result = table.whereElementIsGreaterThanBeanObjectIgnoringNullValues( beanMock[5] );
    
    /* 
            >5        >105
        .....................
        !property1!property2!
        :        0:      100:
        :        1:      101:
        :        2:      102:
        :        3:      103:
        :        4:      104:
        :        5:      105:
        :        6:      106:x
        :        7:      107:x
        :        8:      108:x
        :        9:      109:x
        :       55:      105:
        .....................
    */

    assertEquals( 4, result.getTableSize().getRowSize() );
    
  }
  
  @Test
  public void testConvert()
  {
    //
    Table<Integer> integerTable = new ArrayTable<Integer>();
    integerTable.putArray( new Integer[][] { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } }, 0, 0 );
    
    //
    Table<String> stringTable = integerTable.convert( new TableCellConverter<Integer, String>()
    {
      @Override
      public String convert( Integer cell )
      {
        return String.valueOf( cell );
      }
    } );
    
    //
    Iterator<Integer> integerCellIterator = integerTable.cellIterator();
    Iterator<String> stringCellIterator = stringTable.cellIterator();
    boolean atLeastOneLoop = false;
    while ( integerCellIterator.hasNext() && stringCellIterator.hasNext() )
    {
      //
      Integer integerValue = integerCellIterator.next();
      String stringValue = stringCellIterator.next();
      
      //
      assertEquals( String.valueOf( integerValue ), stringValue );
      atLeastOneLoop = true;
    }
    
    //
    assertTrue( atLeastOneLoop );
    assertEquals( integerCellIterator.hasNext(), stringCellIterator.hasNext() );
    
  }
  
  @Test
  public void testOrderRowsAscendingByBeanObjectPropertyNotNull()
  {
    //
    IndexTable<Integer> table = new IndexArrayTable<Integer>();
    
    //
    BeanMock[] beanMocks = new BeanMock[10];
    for ( int ii = 0; ii < beanMocks.length; ii++ )
    {
      beanMocks[ii] = new BeanMock();
      beanMocks[ii].setProperty1( ii );
      beanMocks[ii].setProperty2( 100 - ii );
      table.addRow( beanMocks[ii] );
    }
    
    //
    assertEquals( beanMocks.length, table.getTableSize().getRowSize() );
    
    /*
     * order by bean object property not null
     */

    //
    BeanMock beanMock;
    IndexTable<Integer> result;
    
    //
    beanMock = new BeanMock();
    beanMock.setProperty2( 2 );
    result = (IndexTable<Integer>) table.orderRowsAscendingByBeanObjectPropertyNotNull( beanMock );
    
    //
    assertEquals( beanMocks.length, result.getTableSize().getRowSize() );
    assertEquals( beanMocks[9], result.getRowAsBean( new BeanMock(), 0 ) );
    
    //
    beanMock = new BeanMock();
    beanMock.setProperty1( 2 );
    result = (IndexTable<Integer>) table.orderRowsAscendingByBeanObjectPropertyNotNull( beanMock );
    
    //
    assertEquals( beanMocks.length, result.getTableSize().getRowSize() );
    assertEquals( beanMocks[0], result.getRowAsBean( new BeanMock(), 0 ) );
    
  }
  
  @Test
  public void testDistinct()
  {
    //
    Table<Integer> table = new ArrayTable<Integer>();
    table.putArray( new Integer[][] { { 11, 12, 13 }, { 11, 12, 13 }, { 11, 12, 33 }, { 11, 22, 13 } }, 0, 0 );
    
    Table<Integer> compareTable = new ArrayTable<Integer>();
    compareTable.putArray( new Integer[][] { { 11, 12, 13 }, { 11, 12, 33 }, { 11, 22, 13 } }, 0, 0 );
    
    //
    Table<Integer> result = table.distinct();
    assertEquals( true, compareTable.equals( result ) );
    assertEquals( compareTable.toString(), result.toString() );
    
  }
  
}

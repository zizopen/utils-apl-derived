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
package org.omnaest.utils.table2.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.table2.Cell;
import org.omnaest.utils.table2.Column;
import org.omnaest.utils.table2.ImmutableTableSerializer.Marshaller.MarshallingConfiguration;
import org.omnaest.utils.table2.ImmutableTableSerializer.MarshallerCsv.CSVMarshallingConfiguration;
import org.omnaest.utils.table2.Row;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TableIndex;
import org.omnaest.utils.table2.TableTest;

/**
 * @see ArrayTable
 * @author Omnaest
 */
public class ArrayTableTest extends TableTest
{
  @Test
  public void testSerialization()
  {
    Table<String> table = this.filledTableWithTitles( 4, 5 );
    Table<String> clone = SerializationUtils.clone( table );
    assertTrue( table.equalsInContent( clone ) );
    assertTrue( table.equalsInContentAndMetaData( clone ) );
    
    //System.out.println( clone );
  }
  
  @SuppressWarnings("cast")
  @Test
  public void testRow()
  {
    Table<String> table = this.newTable( new String[][] { { "a", "b", "c" }, { "d", "e", "f" } }, String.class );
    
    String[] values = new String[] { "a", "b", "c" };
    table.addRowElements( values );
    
    {
      Row<String> row = table.row( 0 );
      assertEquals( Arrays.asList( values ), ListUtils.valueOf( (Iterable<String>) row ) );
    }
    {
      Row<String> row = table.row( 1 );
      assertEquals( Arrays.asList( "d", "e", "f" ), ListUtils.valueOf( (Iterable<String>) row ) );
    }
    {
      Row<String> row = table.row( 2 );
      assertEquals( Arrays.asList( "a", "b", "c" ), ListUtils.valueOf( (Iterable<String>) row ) );
    }
    {
      Row<String> row = table.row( 0 );
      row.setCellElement( 1, "b2" );
      assertEquals( "b2", row.getCellElement( 1 ) );
    }
    {
      assertNull( table.row( -1 ) );
    }
  }
  
  @Test
  @Ignore("Performance test")
  public void testPerformance()
  {
    Table<String> table = this.newTable( new String[][] { { "a", "b", "c" }, { "d", "e", "f" } }, String.class );
    
    for ( int ii = 0; ii < 200000; ii++ )
    {
      String[] values = new String[] { "" + ii, "b", "c" };
      table.addRowElements( values );
    }
  }
  
  @Override
  public <E> Table<E> newTable( E[][] elementMatrix, Class<E> type )
  {
    return new ArrayTable<E>( type ).copyFrom( elementMatrix );
  }
  
  @Test
  public void testGetAndSetCellElement() throws Exception
  {
    Table<String> table = this.newTable( new String[][] { { "a", "b", "c" }, { "d", "e", "f" } }, String.class );
    
    {
      Cell<String> cell = table.cell( 0, 0 );
      assertNotNull( cell );
      assertEquals( "a", cell.getElement() );
      assertEquals( 0, cell.columnIndex() );
      assertEquals( 0, cell.rowIndex() );
      
      table.addRowElements( 0, new String[] { "g", "h", "i" } );
      assertEquals( "a", cell.getElement() );
      assertEquals( 0, cell.columnIndex() );
      assertEquals( 1, cell.rowIndex() );
    }
    
    {
      Cell<String> cell = table.cell( 0, 2 );
      assertNotNull( cell );
      assertEquals( "i", cell.getElement() );
      assertEquals( 2, cell.columnIndex() );
      assertEquals( 0, cell.rowIndex() );
    }
    
    {
      Cell<String> cell = table.cell( 0, 3 );
      assertNotNull( cell );
      assertEquals( null, cell.getElement() );
      assertEquals( 3, cell.columnIndex() );
      assertEquals( 0, cell.rowIndex() );
    }
    
    {
      assertNull( table.cell( -1, 0 ) );
      assertNull( table.cell( 0, -1 ) );
    }
  }
  
  @Test
  public void testColumn() throws Exception
  {
    Table<String> table = this.newTable( new String[][] { { "a", "b", "c" }, { "d", "e", "f" } }, String.class );
    
    {
      Column<String> column = table.column( 0 );
      assertNotNull( column );
      assertEquals( "a", column.getCellElement( 0 ) );
      assertEquals( "d", column.getCellElement( 1 ) );
      assertEquals( null, column.getCellElement( 2 ) );
      assertEquals( null, column.getCellElement( -1 ) );
      
      column.setCellElement( 0, "a2" );
      assertEquals( "a2", column.getCellElement( 0 ) );
    }
    {
      Column<String> column = table.column( 2 );
      assertNotNull( column );
      assertEquals( "c", column.getCellElement( 0 ) );
      assertEquals( "f", column.getCellElement( 1 ) );
      assertEquals( null, column.getCellElement( 2 ) );
    }
    {
      Column<String> column = table.column( 3 );
      assertNotNull( column );
      assertEquals( null, column.getCellElement( 0 ) );
    }
    {
      assertNull( table.column( -1 ) );
    }
    
  }
  
  @Test
  public void testIndex() throws Exception
  {
    Table<String> table = this.filledTable( 100, 5 );
    
    TableIndex<String, Cell<String>> tableIndex = table.index().of( 1 );
    assertNotNull( tableIndex );
    
    {
      assertFalse( tableIndex.containsKey( "0:0" ) );
      assertTrue( tableIndex.containsKey( "0:1" ) );
      
      table.setCellElement( 0, 1, "xxx" );
      assertFalse( tableIndex.containsKey( "0:1" ) );
      assertTrue( tableIndex.containsKey( "xxx" ) );
      
      Set<Cell<String>> cellSet = tableIndex.get( "10:1" );
      assertEquals( 1, cellSet.size() );
    }
    {
      assertSame( tableIndex, table.index().of( 1 ) );
      assertSame( tableIndex, table.index().of( table.column( 1 ) ) );
    }
    {
      SortedMap<String, Set<Cell<String>>> sortedMap = tableIndex.asMap();
      Set<Cell<String>> set = sortedMap.get( "10:1" );
      assertNotNull( set );
      assertEquals( 1, set.size() );
      Cell<String> cell = set.iterator().next();
      assertEquals( "10:1", cell.getElement() );
    }
    {
      table.clear();
      assertTrue( tableIndex.isEmpty() );
    }
  }
  
  @Test
  public void testToString() throws Exception
  {
    Table<String> table = this.filledTable( 3, 4 );
    String content = table.to().string();
    
    //System.out.println( string );
    assertNotNull( content );
    assertEquals( table.toString(), content );
  }
  
  @Test
  public void testToMap() throws Exception
  {
    Table<String> table = this.filledTable( 3, 4 );
    {
      final SortedMap<String, String[]> sortedMap = table.to().sortedMap( 1 );
      assertNotNull( sortedMap );
      assertEquals( 3, sortedMap.size() );
      assertEquals( Arrays.asList( "0:1", "1:1", "2:1" ), ListUtils.valueOf( sortedMap.keySet() ) );
      assertArrayEquals( new String[] { "1:0", "1:1", "1:2", "1:3" }, sortedMap.get( "1:1" ) );
    }
    {
      final SortedMap<String, String> sortedMap = table.to().sortedMap( 1, 3 );
      assertNotNull( sortedMap );
      assertEquals( 3, sortedMap.size() );
      assertEquals( Arrays.asList( "0:1", "1:1", "2:1" ), ListUtils.valueOf( sortedMap.keySet() ) );
      assertEquals( "1:3", sortedMap.get( "1:1" ) );
    }
  }
  
  @Test
  public void testSerializingCSV()
  {
    Table<String> table = this.filledTableWithTitles( 20, 3 );
    
    final CSVMarshallingConfiguration configuration = new CSVMarshallingConfiguration().setHasEnabledRowTitles( true )
                                                                                       .setHasEnabledTableName( true );
    String content = table.serializer().marshal().asCsv().using( configuration ).toString();
    
    //System.out.println( content );
    
    Table<String> result = new ArrayTable<String>( String.class ).serializer()
                                                                 .unmarshal()
                                                                 .asCsv()
                                                                 .using( configuration )
                                                                 .from( content );
    assertTrue( table.equalsInContentAndMetaData( result ) );
    
  }
  
  @Test
  public void testSerializingPlainText()
  {
    Table<String> table = this.filledTableWithTitles( 20, 3 );
    
    final MarshallingConfiguration configuration = new MarshallingConfiguration().setHasEnabledRowTitles( true )
                                                                                 .setHasEnabledTableName( true );
    String content = table.serializer().marshal().asPlainText().using( configuration ).toString();
    
    //System.out.println( table );
    
    Table<String> result = new ArrayTable<String>( String.class ).serializer()
                                                                 .unmarshal()
                                                                 .asPlainText()
                                                                 .using( configuration )
                                                                 .from( content );
    assertTrue( table.equalsInContentAndMetaData( result ) );
    
  }
  
  @Test
  @Ignore("Performance test")
  public void testSelectPerformance() throws Exception
  {
    Table<String> table = this.filledTableWithTitles( 100000, 4 );
    Table<String> table2 = this.filledTableWithTitles( 2, 8 );
    Table<String> table3 = this.filledTableWithTitles( 2, 3 );
    
    table.setTableName( "table1" );
    table2.setTableName( "table2" );
    table3.setTableName( "table3" );
    
    {
      Table<String> result = table.select()
                                  .columns( 1, 2 )
                                  .join( table2 )
                                  .columns( 6, 7 )
                                  .onEqual( table.column( 0 ), table2.column( 0 ) )
                                  .join( table3 )
                                  .onEqual( table.column( 1 ), table3.column( 1 ) )
                                  .column( 0 )
                                  .as()
                                  .table();
      
      //      System.out.println( table );
      //      System.out.println( table2 );
      //      System.out.println( table3 );
      //      System.out.println( result );
      
      assertNotNull( result );
      
    }
    
  }
  
  @Test
  public void testAdapterTwoColumnMap()
  {
    Table<String> table = this.filledTableWithTitles( 10, 4 );
    
    final int columnIndexKey = 1;
    final int columnIndexValue = 3;
    Map<String, Set<String>> map = table.as().map( columnIndexKey, columnIndexValue );
    assertNotNull( map );
    
    {
      assertEquals( 10, map.size() );
      assertEquals( SetUtils.valueOf( "0:3" ), map.get( "0:1" ) );
      assertEquals( SetUtils.valueOf( "9:3" ), map.get( "9:1" ) );
      assertEquals( SetUtils.emptySet(), map.get( "10:1" ) );
      assertEquals( SetUtils.emptySet(), map.get( "0:2" ) );
      assertTrue( map.containsKey( "0:1" ) );
      assertEquals( table.column( 1 ).to().set(), map.keySet() );
    }
    {
      Set<String> previous = map.put( "0:1", SetUtils.valueOf( "xxx" ) );
      assertEquals( SetUtils.valueOf( "0:3" ), previous );
      assertEquals( SetUtils.valueOf( "xxx" ), map.get( "0:1" ) );
      assertEquals( "xxx", table.cell( 0, 3 ).getElement() );
    }
    {
      Cell<String> cell = table.cell( 0, 3 );
      Set<String> remove = map.remove( "0:1" );
      assertEquals( SetUtils.valueOf( "xxx" ), remove );
      assertEquals( SetUtils.valueOf( (String) null ), map.get( "0:1" ) );
      assertTrue( cell.isModified() );
    }
  }
  
  @SuppressWarnings({ "unchecked", "cast" })
  @Test
  public void testAdapterOneColumnMap()
  {
    Table<String> table = this.filledTableWithTitles( 10, 4 );
    
    final int columnIndexKey = 1;
    Map<String, Set<Row<String>>> map = table.as().map( columnIndexKey );
    assertNotNull( map );
    assertEquals( 10, map.size() );
    
    {
      final Set<Row<String>> rowSet = map.get( "0:1" );
      assertEquals( 1, rowSet.size() );
      assertArrayEquals( table.row( 0 ).getCellElements(), rowSet.iterator().next().getCellElements() );
    }
    {
      Set<Row<String>> previous = map.put( "0:1", SetUtils.<Row<String>> valueOf( (Row<String>) table.row( 9 ) ) );
      assertArrayEquals( table.row( 0 ).getCellElements(), previous.iterator().next().getCellElements() );
    }
    {
      Set<Row<String>> remove = map.remove( "9:1" );
      final Row<String> row = IterableUtils.firstElement( remove );
      assertTrue( row.isDeleted() );
      assertEquals( 9, table.rowSize() );
    }
  }
}

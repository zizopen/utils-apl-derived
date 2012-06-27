package org.omnaest.utils.table;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

public class TableDataCoreTest
{
  
  @Test
  public void testAddRow() throws Exception
  {
    TableDataCore<String> tableDataCore = new TableDataCore<String>( String.class );
    
    {
      final String[] data = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };
      tableDataCore.addRow( data );
      
      assertArrayEquals( data, tableDataCore.getRow( 0 ) );
      assertEquals( "a", tableDataCore.getData( 0, 0 ) );
      assertEquals( "m", tableDataCore.getData( 0, 12 ) );
      assertNull( tableDataCore.getData( 0, 13 ) );
      assertEquals( 1, tableDataCore.rowSize() );
    }
    
    //
    for ( int ii = 1; ii < 255; ii++ )
    {
      final String[] data = new String[] { "a" + ii, "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };
      tableDataCore.addRow( data );
      assertArrayEquals( data, tableDataCore.getRow( ii ) );
    }
    
    //System.out.println( tableDataCore );
  }
  
  @Test
  public void testAddColumn() throws Exception
  {
    TableDataCore<String> tableDataCore = new TableDataCore<String>( String.class );
    
    {
      final String[] data = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };
      tableDataCore.addColumn( data );
      
      assertArrayEquals( data, tableDataCore.getColumn( 0 ) );
      assertEquals( "a", tableDataCore.getData( 0, 0 ) );
      assertEquals( "m", tableDataCore.getData( 12, 0 ) );
      assertNull( tableDataCore.getData( 13, 0 ) );
      assertEquals( 1, tableDataCore.columnSize() );
    }
    
    //
    for ( int ii = 1; ii < 255; ii++ )
    {
      final String[] data = new String[] { "a" + ii, "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };
      tableDataCore.addColumn( data );
      assertArrayEquals( data, tableDataCore.getColumn( ii ) );
    }
    
    //System.out.println( tableDataCore );
  }
  
  @Test
  public void testSet() throws Exception
  {
    TableDataCore<String> tableDataCore = new TableDataCore<String>( String.class );
    
    assertNull( tableDataCore.getData( 0, 0 ) );
    tableDataCore.set( "00", 0, 0 );
    assertEquals( "00", tableDataCore.getData( 0, 0 ) );
    
    tableDataCore.set( "10", 1, 0 );
    assertEquals( "10", tableDataCore.getData( 1, 0 ) );
    
    tableDataCore.set( "11", 1, 1 );
    assertEquals( "11", tableDataCore.getData( 1, 1 ) );
    
    tableDataCore.set( "01", 0, 1 );
    assertEquals( "01", tableDataCore.getData( 0, 1 ) );
    
    tableDataCore.set( "99", 9, 9 );
    assertEquals( "99", tableDataCore.getData( 9, 9 ) );
    
    //System.out.println( tableDataCore );
  }
  
  @Test
  public void testRemoveRow() throws Exception
  {
    //
    TableDataCore<String> tableDataCore = new TableDataCore<String>( String.class );
    
    //
    for ( int ii = 0; ii < 12; ii++ )
    {
      final String[] data = new String[] { "a" + ii, "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };
      tableDataCore.addRow( data );
      assertArrayEquals( data, tableDataCore.getRow( ii ) );
    }
    
    //
    assertEquals( 12, tableDataCore.rowSize() );
    tableDataCore.removeRow( 5 );
    assertEquals( 11, tableDataCore.rowSize() );
    assertEquals( "a6", tableDataCore.getData( 5, 0 ) );
    
    //
    for ( int ii = 0; ii < 10; ii++ )
    {
      tableDataCore.removeRow( 0 );
    }
    assertEquals( 1, tableDataCore.rowSize() );
    
    {
      final String[] data = new String[] { "a11", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };
      assertArrayEquals( data, tableDataCore.getRow( 0 ) );
    }
  }
  
  @Test
  @Ignore("Performance test")
  public void testPerformance() throws Exception
  {
    //
    TableDataCore<String> tableDataCore = new TableDataCore<String>( String.class );
    
    //
    for ( int ii = 0; ii < 100000; ii++ )
    {
      final String[] data = new String[] { "" + ii, "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };
      tableDataCore.addRow( data );
    }
    
    //    for ( int ii = 0; ii < 100000; ii++ )
    //    {
    //      if ( ii % 2 == 0 )
    //      {
    //        tableDataCore.removeRow( ii );
    //      }
    //    }
  }
  
}

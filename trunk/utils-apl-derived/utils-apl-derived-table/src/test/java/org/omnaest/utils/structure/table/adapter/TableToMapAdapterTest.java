package org.omnaest.utils.structure.table.adapter;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.TableFiller;
import org.omnaest.utils.structure.table.concrete.ArrayTable;

/**
 * @see TableToMapAdapter
 */
public class TableToMapAdapterTest
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private Table<String> table = new ArrayTable<String>();
  
  /* *************************************************** Methods **************************************************** */
  
  @Before
  public void setUp()
  {
    int rows = 10;
    int columns = 4;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
  }
  
  @Test
  public void testGet()
  {
    //
    final Column<String> columnForKeys = this.table.getColumn( 3 );
    final Column<String> columnForValues = this.table.getColumn( 1 );
    Map<String, String> map = this.table.as().adapter( new TableToMapAdapter<String, String, String>( columnForKeys,
                                                                                                      columnForValues ) );
    
    //
    //System.out.println( map );
    //    System.out.print( map.keySet() );
    //    System.out.print( map.values() );
    
    //
    assertEquals( 10, map.size() );
    assertEquals( "1:1", map.get( "1:3" ) );
    assertEquals( "[0:3, 1:3, 2:3, 3:3, 4:3, 5:3, 6:3, 7:3, 8:3, 9:3]", String.valueOf( map.keySet() ) );
    assertEquals( "[0:1, 1:1, 2:1, 3:1, 4:1, 5:1, 6:1, 7:1, 8:1, 9:1]", String.valueOf( map.values() ) );
  }
  
}

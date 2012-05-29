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
package org.omnaest.utils.structure.table.concrete;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.Table.TableCellConverter;
import org.omnaest.utils.structure.table.Table.TableCellVisitor;
import org.omnaest.utils.structure.table.Table.TableSize;
import org.omnaest.utils.structure.table.TableFiller;
import org.omnaest.utils.structure.table.serializer.marshaller.TableMarshallerXML;
import org.omnaest.utils.structure.table.serializer.unmarshaller.TableUnmarshallerXML;

/**
 * @see ArrayTable
 * @author Omnaest
 */
public class ArrayTableTest
{
  /* ********************************************** Variables ********************************************** */
  protected Table<Object> table = new ArrayTable<Object>();
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    assertNotNull( this.table );
  }
  
  @Test
  public void testTranspose()
  {
    //
    final int rows = 5;
    final int columns = 2;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    this.table.transpose();
    
    //
    TableSize tableSize = this.table.getTableSize();
    assertNotNull( tableSize );
    assertEquals( columns, tableSize.getRowSize() );
    assertEquals( rows, tableSize.getColumnSize() );
    assertEquals( "0:0", this.table.getCellElement( 0, 0 ) );
    assertEquals( "1:0", this.table.getCellElement( 0, 1 ) );
    assertEquals( "2:0", this.table.getCellElement( 0, 2 ) );
    assertEquals( "0:1", this.table.getCellElement( 1, 0 ) );
  }
  
  @Test
  public void testGetTableSize()
  {
    //
    final int rows = 2;
    final int columns = 5;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    TableSize tableSize = this.table.getTableSize();
    assertNotNull( tableSize );
    assertEquals( rows, tableSize.getRowSize() );
    assertEquals( columns, tableSize.getColumnSize() );
    assertEquals( rows * columns, tableSize.getCellSize() );
  }
  
  @Test
  public void testGetCellIntInt()
  {
    //
    final int rows = 2;
    final int columns = 2;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    assertEquals( "0:0", this.table.getCellElement( 0, 0 ) );
    assertEquals( "1:0", this.table.getCellElement( 1, 0 ) );
    assertEquals( "0:1", this.table.getCellElement( 0, 1 ) );
    assertEquals( "1:1", this.table.getCellElement( 1, 1 ) );
  }
  
  @Test
  public void testGetRowInt()
  {
    //
    final int rows = 3;
    final int columns = 2;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    {
      //
      Row<Object> row = this.table.getRow( 0 );
      assertNotNull( row );
      
      //
      {
        //
        Cell<Object> cell = row.getCell( 0 );
        assertNotNull( cell );
        
        //
        Object element = cell.getElement();
        assertNotNull( element );
        assertEquals( "0:0", element );
      }
    }
    
    //
    assertEquals( "0:0", this.table.getRow( 0 ).getCellElement( 0 ) );
    assertEquals( "0:1", this.table.getRow( 0 ).getCellElement( 1 ) );
    assertEquals( "1:0", this.table.getRow( 1 ).getCellElement( 0 ) );
    assertEquals( "1:1", this.table.getRow( 1 ).getCellElement( 1 ) );
    assertEquals( "2:0", this.table.getRow( 2 ).getCellElement( 0 ) );
    assertEquals( "2:1", this.table.getRow( 2 ).getCellElement( 1 ) );
  }
  
  @Test
  public void testGetColumnInt()
  {
    //
    final int rows = 2;
    final int columns = 3;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    {
      //
      Column<Object> column = this.table.getColumn( 0 );
      assertNotNull( column );
      
      //
      {
        //
        Cell<Object> cell = column.getCell( 0 );
        assertNotNull( cell );
        
        //
        Object element = cell.getElement();
        assertNotNull( element );
        assertEquals( "0:0", element );
      }
    }
    
    //
    assertEquals( "0:0", this.table.getColumn( 0 ).getCellElement( 0 ) );
    assertEquals( "1:0", this.table.getColumn( 0 ).getCellElement( 1 ) );
    assertEquals( "0:1", this.table.getColumn( 1 ).getCellElement( 0 ) );
    assertEquals( "1:1", this.table.getColumn( 1 ).getCellElement( 1 ) );
    assertEquals( "0:2", this.table.getColumn( 2 ).getCellElement( 0 ) );
    assertEquals( "1:2", this.table.getColumn( 2 ).getCellElement( 1 ) );
  }
  
  @Test
  public void testAddColumn()
  {
    //
    final int rows = 2;
    final int columns = 3;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    {
      //
      Column<Object> column = this.table.addColumn( "c4" );
      assertNotNull( column );
      
      //
      {
        //
        Cell<Object> cell = column.getCell( 0 );
        assertNotNull( cell );
        
        //
        cell.setElement( "0:3" );
        Object element = cell.getElement();
        assertNotNull( element );
        assertEquals( "0:3", element );
      }
    }
    
    //
    assertEquals( "0:0", this.table.getColumn( 0 ).getCellElement( 0 ) );
    assertEquals( "1:0", this.table.getColumn( 0 ).getCellElement( 1 ) );
    assertEquals( "0:1", this.table.getColumn( 1 ).getCellElement( 0 ) );
    assertEquals( "1:1", this.table.getColumn( 1 ).getCellElement( 1 ) );
    assertEquals( "0:2", this.table.getColumn( 2 ).getCellElement( 0 ) );
    assertEquals( "1:2", this.table.getColumn( 2 ).getCellElement( 1 ) );
    assertEquals( "0:3", this.table.getColumn( 3 ).getCellElement( 0 ) );
    assertEquals( null, this.table.getColumn( 3 ).getCellElement( 1 ) );
  }
  
  @Test
  public void testGetCellInt()
  {
    //
    final int rows = 2;
    final int columns = 3;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    assertEquals( "0:0", this.table.getCellElement( 0 ) );
    assertEquals( "0:1", this.table.getCellElement( 1 ) );
    assertEquals( "0:2", this.table.getCellElement( 2 ) );
    assertEquals( "1:0", this.table.getCellElement( 3 ) );
    assertEquals( "1:1", this.table.getCellElement( 4 ) );
    assertEquals( "1:2", this.table.getCellElement( 5 ) );
  }
  
  @Test
  public void testContains()
  {
    //
    final int rows = 2;
    final int columns = 3;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    assertTrue( this.table.contains( "0:0" ) );
    assertTrue( this.table.contains( "1:2" ) );
    assertFalse( this.table.contains( "2:2" ) );
  }
  
  @Test
  public void testGetColumnTitleValue()
  {
    //
    final int rows = 2;
    final int columns = 3;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    assertNotNull( this.table.getColumnTitleValue( 0 ) );
    assertEquals( "c0", this.table.getColumnTitleValue( 0 ) );
    assertEquals( "c1", this.table.getColumnTitleValue( 1 ) );
    assertEquals( "c2", this.table.getColumnTitleValue( 2 ) );
  }
  
  @Test
  public void testGetColumnTitleValueValues()
  {
    //
    final int rows = 2;
    final int columns = 3;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    assertEquals( Arrays.asList( "c0", "c1", "c2" ), this.table.getColumnTitleValueList() );
  }
  
  @Test
  public void testGetRowTitleValue()
  {
    //
    final int rows = 2;
    final int columns = 3;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    assertNotNull( this.table.getRowTitleValue( 0 ) );
    assertEquals( "r0", this.table.getRowTitleValue( 0 ) );
    assertEquals( "r1", this.table.getRowTitleValue( 1 ) );
  }
  
  @Test
  public void testEquals()
  {
    //
    Table<Object> tableFirst = new ArrayTable<Object>();
    Table<Object> tableEqual = new ArrayTable<Object>();
    Table<Object> tableUnequalBySize = new ArrayTable<Object>();
    Table<Object> tableUnequalByContent = new ArrayTable<Object>();
    
    //
    final int rows = 2;
    final int columns = 3;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, tableFirst );
    TableFiller.fillTableWithMatrixNumbers( rows, columns, tableEqual );
    TableFiller.fillTableWithMatrixNumbers( columns, rows, tableUnequalBySize );
    TableFiller.fillTableWithMatrixNumbers( rows, columns, tableUnequalByContent );
    tableUnequalByContent.setCellElement( 0, "changed" );
    
    //
    assertTrue( tableFirst.equals( tableEqual ) );
    assertTrue( tableEqual.equals( tableFirst ) );
    assertFalse( tableFirst.equals( tableUnequalBySize ) );
    assertFalse( tableFirst.equals( tableUnequalByContent ) );
    assertEquals( tableFirst, tableEqual );
  }
  
  @Test
  public void testIteratorCell()
  {
    //
    final int rows = 2;
    final int columns = 3;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    Iterator<Cell<Object>> iteratorCell = this.table.iteratorCell();
    assertNotNull( iteratorCell );
    
    //
    for ( int rowIndexPosition = 0; rowIndexPosition < rows; rowIndexPosition++ )
    {
      for ( int columnIndexPosition = 0; columnIndexPosition < columns; columnIndexPosition++ )
      {
        //
        assertTrue( iteratorCell.hasNext() );
        
        //
        Cell<Object> cell = iteratorCell.next();
        assertNotNull( cell );
        assertEquals( "" + rowIndexPosition + ":" + columnIndexPosition, cell.getElement() );
      }
    }
    assertFalse( iteratorCell.hasNext() );
  }
  
  @Test
  public void testSetRowCellElements()
  {
    //
    final int rows = 2;
    final int columns = 3;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    this.table.setRowCellElements( 0, Arrays.asList( "a", "b", "c" ) );
    
    //
    assertEquals( 3, this.table.getTableSize().getColumnSize() );
    assertEquals( "a", this.table.getCellElement( 0 ) );
    assertEquals( "b", this.table.getCellElement( 1 ) );
    assertEquals( "c", this.table.getCellElement( 2 ) );
    assertEquals( "1:0", this.table.getCellElement( 3 ) );
    assertEquals( "1:1", this.table.getCellElement( 4 ) );
    assertEquals( "1:2", this.table.getCellElement( 5 ) );
    
    //
    this.table.setRowCellElements( 0, Arrays.asList( "a", "b", "c", "d" ) );
    assertEquals( 4, this.table.getTableSize().getColumnSize() );
    assertEquals( "a", this.table.getCellElement( 0 ) );
    assertEquals( "b", this.table.getCellElement( 1 ) );
    assertEquals( "c", this.table.getCellElement( 2 ) );
    assertEquals( "d", this.table.getCellElement( 3 ) );
    assertEquals( null, this.table.getCellElement( 1, 3 ) );
  }
  
  @Test
  public void testAddRowCellElementsUsingAMap()
  {
    //
    final int rows = 2;
    final int columns = 3;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    Map<Object, Object> map = new HashMap<Object, Object>();
    map.put( "c0", "a" );
    map.put( "c1", "b" );
    map.put( "c2", "c" );
    map.put( "c3", "c" );
    this.table.addRowCellElements( map );
    
    //
    //System.out.println( this.table );
    
    //
    assertEquals( 3, this.table.getTableSize().getColumnSize() );
    assertEquals( "0:0", this.table.getCellElement( 0 ) );
    assertEquals( "0:1", this.table.getCellElement( 1 ) );
    assertEquals( "0:2", this.table.getCellElement( 2 ) );
    assertEquals( "1:0", this.table.getCellElement( 3 ) );
    assertEquals( "1:1", this.table.getCellElement( 4 ) );
    assertEquals( "1:2", this.table.getCellElement( 5 ) );
    assertEquals( "a", this.table.getCellElement( 6 ) );
    assertEquals( "b", this.table.getCellElement( 7 ) );
    assertEquals( "c", this.table.getCellElement( 8 ) );
  }
  
  @Test
  public void testSetColumnCellElements()
  {
    //
    final int rows = 3;
    final int columns = 2;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    this.table.setColumnCellElements( 0, Arrays.asList( "a", "b", "c" ) );
    
    //
    assertEquals( 3, this.table.getTableSize().getRowSize() );
    assertEquals( "a", this.table.getCellElement( 0, 0 ) );
    assertEquals( "b", this.table.getCellElement( 1, 0 ) );
    assertEquals( "c", this.table.getCellElement( 2, 0 ) );
    assertEquals( "0:1", this.table.getCellElement( 0, 1 ) );
    assertEquals( "1:1", this.table.getCellElement( 1, 1 ) );
    assertEquals( "2:1", this.table.getCellElement( 2, 1 ) );
    
    //
    this.table.setColumnCellElements( 0, Arrays.asList( "a", "b", "c", "d" ) );
    assertEquals( 4, this.table.getTableSize().getRowSize() );
    assertEquals( "a", this.table.getCellElement( 0, 0 ) );
    assertEquals( "b", this.table.getCellElement( 1, 0 ) );
    assertEquals( "c", this.table.getCellElement( 2, 0 ) );
    assertEquals( "d", this.table.getCellElement( 3, 0 ) );
    assertEquals( "2:1", this.table.getCellElement( 2, 1 ) );
    assertEquals( null, this.table.getCellElement( 3, 1 ) );
  }
  
  @Test
  public void testAddColumnCellElements()
  {
    //
    final int rows = 3;
    final int columns = 2;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    this.table.addColumnCellElements( 1, Arrays.asList( "a", "b", "c" ) );
    
    //
    assertEquals( 3, this.table.getTableSize().getRowSize() );
    assertEquals( 3, this.table.getTableSize().getColumnSize() );
    assertEquals( "0:0", this.table.getCellElement( 0, 0 ) );
    assertEquals( "1:0", this.table.getCellElement( 1, 0 ) );
    assertEquals( "2:0", this.table.getCellElement( 2, 0 ) );
    assertEquals( "a", this.table.getCellElement( 0, 1 ) );
    assertEquals( "b", this.table.getCellElement( 1, 1 ) );
    assertEquals( "c", this.table.getCellElement( 2, 1 ) );
    assertEquals( "0:1", this.table.getCellElement( 0, 2 ) );
    assertEquals( "1:1", this.table.getCellElement( 1, 2 ) );
    assertEquals( "2:1", this.table.getCellElement( 2, 2 ) );
    
    //
    this.table.addColumnCellElements( 6, Arrays.asList( "a", "b", "c" ) );
    assertEquals( 7, this.table.getTableSize().getColumnSize() );
    assertEquals( "a", this.table.getCellElement( 0, 6 ) );
    assertEquals( "b", this.table.getCellElement( 1, 6 ) );
    assertEquals( "c", this.table.getCellElement( 2, 6 ) );
    assertEquals( null, this.table.getCellElement( 0, 5 ) );
    assertEquals( null, this.table.getCellElement( 1, 5 ) );
    assertEquals( null, this.table.getCellElement( 2, 5 ) );
    assertEquals( null, this.table.getCellElement( 0, 4 ) );
    assertEquals( null, this.table.getCellElement( 1, 4 ) );
    assertEquals( null, this.table.getCellElement( 2, 4 ) );
    
    //
    this.table.addColumnCellElements( Arrays.asList( "a", "b", "c" ) );
    assertEquals( 8, this.table.getTableSize().getColumnSize() );
  }
  
  @Test
  public void testAddRowCellElements()
  {
    //
    final int rows = 2;
    final int columns = 3;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    this.table.addRowCellElements( 1, Arrays.asList( "a", "b", "c" ) );
    
    //
    assertEquals( 3, this.table.getTableSize().getRowSize() );
    assertEquals( 3, this.table.getTableSize().getColumnSize() );
    assertEquals( "0:0", this.table.getCellElement( 0, 0 ) );
    assertEquals( "0:1", this.table.getCellElement( 0, 1 ) );
    assertEquals( "0:2", this.table.getCellElement( 0, 2 ) );
    assertEquals( "a", this.table.getCellElement( 1, 0 ) );
    assertEquals( "b", this.table.getCellElement( 1, 1 ) );
    assertEquals( "c", this.table.getCellElement( 1, 2 ) );
    assertEquals( "1:0", this.table.getCellElement( 2, 0 ) );
    assertEquals( "1:1", this.table.getCellElement( 2, 1 ) );
    assertEquals( "1:2", this.table.getCellElement( 2, 2 ) );
    
    //
    this.table.addRowCellElements( 6, Arrays.asList( "a", "b", "c" ) );
    assertEquals( 7, this.table.getTableSize().getRowSize() );
    assertEquals( "a", this.table.getCellElement( 6, 0 ) );
    assertEquals( "b", this.table.getCellElement( 6, 1 ) );
    assertEquals( "c", this.table.getCellElement( 6, 2 ) );
    assertEquals( null, this.table.getCellElement( 5, 0 ) );
    assertEquals( null, this.table.getCellElement( 5, 1 ) );
    assertEquals( null, this.table.getCellElement( 5, 2 ) );
    assertEquals( null, this.table.getCellElement( 4, 0 ) );
    assertEquals( null, this.table.getCellElement( 4, 1 ) );
    assertEquals( null, this.table.getCellElement( 4, 2 ) );
    
    //
    this.table.addRowCellElements( Arrays.asList( "a", "b", "c" ) );
    assertEquals( 8, this.table.getTableSize().getRowSize() );
  }
  
  @Test
  public void testClear()
  {
    //
    final int rows = 2;
    final int columns = 3;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    this.table.clear();
    
    //
    assertEquals( 0, this.table.getTableSize().getRowSize() );
    assertEquals( 0, this.table.getTableSize().getColumnSize() );
  }
  
  @Test
  public void testGetRowTitleValueList()
  {
    //
    List<String> titleValueList = Arrays.asList( "title1", "title2", "title3" );
    this.table.setRowTitleValues( titleValueList );
    
    //
    assertEquals( titleValueList, this.table.getRowTitleValueList() );
  }
  
  @Test
  public void testGetColumnTitleValueList()
  {
    //
    List<String> titleValueList = Arrays.asList( "title1", "title2", "title3" );
    this.table.setColumnTitleValues( titleValueList );
    
    //
    assertEquals( titleValueList, this.table.getColumnTitleValueList() );
  }
  
  @Test
  public void testRemoveRow()
  {
    //
    final int rows = 5;
    final int columns = 3;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    this.table.removeRow( 1 );
    
    //
    assertEquals( rows - 1, this.table.getTableSize().getRowSize() );
    assertEquals( columns, this.table.getTableSize().getColumnSize() );
    assertEquals( "0:0", this.table.getCellElement( 0, 0 ) );
    assertEquals( "2:0", this.table.getCellElement( 1, 0 ) );
    assertEquals( "3:0", this.table.getCellElement( 2, 0 ) );
    assertEquals( "4:0", this.table.getCellElement( 3, 0 ) );
  }
  
  @Test
  public void testRemoveColumn()
  {
    //
    final int rows = 3;
    final int columns = 5;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    this.table.removeColumn( 1 );
    
    //
    assertEquals( rows, this.table.getTableSize().getRowSize() );
    assertEquals( columns - 1, this.table.getTableSize().getColumnSize() );
    assertEquals( "0:0", this.table.getCellElement( 0, 0 ) );
    assertEquals( "0:2", this.table.getCellElement( 0, 1 ) );
    assertEquals( "0:3", this.table.getCellElement( 0, 2 ) );
  }
  
  @Test
  public void testGetRow()
  {
    //
    this.table.setRowTitleValues( Arrays.asList( "title1", "title2", "title3" ) );
    
    //
    Row<Object> row = this.table.getRow( "title2" );
    assertNotNull( row );
  }
  
  @Test
  public void testGetColumn()
  {
    //
    this.table.setColumnTitleValues( Arrays.asList( "title1", "title2", "title3" ) );
    
    //
    assertNotNull( this.table.getColumn( "title2" ) );
    
    //
    assertNull( this.table.getColumn( -1 ) );
    assertNull( this.table.getColumn( this.table.getTableSize().getColumnSize() ) );
    assertNotNull( this.table.getColumn( 0 ) );
    
  }
  
  @Test
  public void testGetTableName()
  {
    //
    final String tableName = "table name";
    
    //
    this.table.setTableName( tableName );
    
    //
    assertEquals( tableName, this.table.getTableName() );
  }
  
  @Test
  public void testProcessTableCells()
  {
    //
    final int rows = 3;
    final int columns = 5;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //    
    TableCellVisitor<Object> tableCellVisitor = new TableCellVisitor<Object>()
    {
      @Override
      public void process( int rowIndexPosition, int columnIndexPosition, Cell<Object> cell )
      {
        cell.setElement( cell.getElement() + "+" );
      }
    };
    
    //
    this.table.processTableCells( tableCellVisitor );
    
    //
    for ( Cell<Object> cell : this.table.cells() )
    {
      String value = (String) cell.getElement();
      assertTrue( value.endsWith( "+" ) );
    }
    
  }
  
  @Test
  public void testConvert()
  {
    //
    final int rows = 3;
    final int columns = 5;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    TableCellConverter<Object, String> tableCellConverter = new TableCellConverter<Object, String>()
    {
      @Override
      public String convert( Object cellElement )
      {
        return (String) cellElement;
      }
    };
    
    //
    Table<String> tableConverted = this.table.convert( tableCellConverter );
    
    //
    assertEquals( this.table, tableConverted );
  }
  
  @Test
  public void testRowsIterator()
  {
    //
    final int rows = 3;
    final int columns = 5;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    Iterator<Row<Object>> iteratorRow = this.table.rows().iterator();
    
    //
    assertNotNull( iteratorRow );
    
    //
    int counterRows = 0;
    while ( iteratorRow.hasNext() )
    {
      //
      Row<Object> row = iteratorRow.next();
      assertNotNull( row );
      
      //
      assertEquals( counterRows + ":0", row.getCellElement( 0 ) );
      
      //
      counterRows++;
    }
    assertEquals( rows, counterRows );
  }
  
  @Test
  public void testParseXMLFrom()
  {
    //
    final int rows = 3;
    final int columns = 5;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    StringBuilder stringBuilder = new StringBuilder();
    
    //
    this.table.serializer().marshal( new TableMarshallerXML<Object>() ).appendTo( stringBuilder );
    this.table.serializer().unmarshal( new TableUnmarshallerXML<Object>() ).from( stringBuilder );
    
    //
    //System.out.println( stringBuilder );
    
    //
    assertEquals( rows, this.table.getTableSize().getRowSize() );
    
  }
  
  @Test
  public void testGetCell()
  {
    //
    final int rows = 5;
    final int columns = 3;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    this.table.setColumnTitleValues( Arrays.asList( "c0", "c1", "c2" ) );
    this.table.setRowTitleValues( Arrays.asList( "r0", "r1", "r2", "r3", "r4" ) );
    
    //
    assertNotNull( this.table.getCell( "r0", "c0" ) );
    assertNotNull( this.table.getCell( "r4", "c2" ) );
    assertEquals( "0:0", this.table.getCell( "r0", "c0" ).getElement() );
    assertEquals( "4:2", this.table.getCell( "r4", "c2" ).getElement() );
    
    assertEquals( null, this.table.getCell( "a", "b" ) );
    
  }
  
  @Test
  @Ignore("Long running performance test")
  public void testGetCellPerformance()
  {
    //
    final int rows = 200000;
    final int columns = 3;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    this.table.setColumnTitleValues( Arrays.asList( "c0", "c1", "c2" ) );
    
    //
    for ( Cell<Object> cell : this.table.cells() )
    {
      assertNotNull( cell );
      assertNotNull( cell.getElement() );
    }
    
  }
  
  @Test
  public void testGetStripeTitleValue()
  {
    //
    final int rows = 5;
    final int columns = 3;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, this.table );
    
    //
    assertNull( this.table.getColumnTitleValue( -1 ) );
    assertNull( this.table.getColumnTitleValue( 3 ) );
    assertEquals( "c0", this.table.getColumnTitleValue( 0 ) );
    assertEquals( "c2", this.table.getColumnTitleValue( 2 ) );
  }
}

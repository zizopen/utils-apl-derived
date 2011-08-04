package org.omnaest.utils.structure.table.concrete.predicates.internal.filter;

import static org.junit.Assert.assertEquals;
import static org.omnaest.utils.structure.table.concrete.predicates.PredicateFactory.columnValueIsBetween;
import static org.omnaest.utils.structure.table.concrete.predicates.PredicateFactory.equalColumns;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.TableFiller;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.concrete.predicates.PredicateFactory;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Predicate;

/**
 * @see ColumnValueIsBetween
 * @author Omnaest
 */
public class ColumnValueIsBetweenTest
{
  /* ********************************************** Variables ********************************************** */
  protected Table<String> table1 = new ArrayTable<String>();
  protected Table<String> table2 = new ArrayTable<String>();
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    //
    {
      //
      int rows = 10;
      int columns = 2;
      String tableName = "Table1";
      TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, this.table1 );
    }
    
    //
    {
      //
      int rows = 5;
      int columns = 3;
      String tableName = "Table2";
      TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, this.table2 );
    }
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testFilterStripeDataSet()
  {
    //
    Predicate<String> predicate = PredicateFactory.columnValueIsBetween( this.table1.getColumn( 0 ), "2:0", "3:0" );
    Table<String> tableResult = this.table1.select().allColumns().where( predicate ).asTable();
    
    //
    //System.out.println( tableResult );
    
    //
    assertEquals( 2, tableResult.getTableSize().getRowSize() );
    assertEquals( Arrays.asList( "2:0", "2:1" ), tableResult.getRow( 0 ).asNewListOfCellElements() );
    assertEquals( Arrays.asList( "3:0", "3:1" ), tableResult.getRow( 1 ).asNewListOfCellElements() );
    
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testFilterStripeDataSetMultipleColumns()
  {
    //
    Predicate<String> predicate = PredicateFactory.columnValueIsBetween( "3:1", "4:1", this.table1.getColumn( 1 ),
                                                                       this.table2.getColumn( 1 ) );
    Predicate<String> predicateJoin = PredicateFactory.equalColumns( this.table1.getColumn( 0 ), this.table2.getColumn( 0 ) );
    Table<String> tableResult = this.table1.select()
                                           .allColumns()
                                           .innerJoin( this.table2 )
                                           .on( predicateJoin )
                                           .where( predicate )
                                           .asTable();
    
    this.table1.select()
               .allColumns()
               .innerJoin( this.table2 )
               .on( equalColumns( this.table1.getColumn( 0 ), this.table2.getColumn( 0 ) ) )
               .where( columnValueIsBetween( "3:1", "4:1", this.table1.getColumn( 1 ), this.table2.getColumn( 1 ) ) )
               .asTable();
    
    //
    //System.out.println( tableResult );
    
    //
    assertEquals( 2, tableResult.getTableSize().getRowSize() );
    assertEquals( Arrays.asList( "3:0", "3:1", "3:0", "3:1", "3:2" ), tableResult.getRow( 0 ).asNewListOfCellElements() );
    assertEquals( Arrays.asList( "4:0", "4:1", "4:0", "4:1", "4:2" ), tableResult.getRow( 1 ).asNewListOfCellElements() );
    
  }
  
}

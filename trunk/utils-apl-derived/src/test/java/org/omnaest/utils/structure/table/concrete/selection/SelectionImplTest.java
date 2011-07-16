package org.omnaest.utils.structure.table.concrete.selection;

import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.TableSelectable.Result;
import org.omnaest.utils.structure.table.concrete.ArrayTable;

/**
 * @see SelectionImpl
 * @author Omnaest
 */
public class SelectionImplTest
{
  /* ********************************************** Variables ********************************************** */
  protected Table<String> table = new ArrayTable<String>();
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testResult()
  {
    Result<String> result = this.table.select().allColumns().result();
  }
  
  
}

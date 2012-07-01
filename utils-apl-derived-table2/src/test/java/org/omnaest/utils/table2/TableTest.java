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
package org.omnaest.utils.table2;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;
import org.omnaest.utils.structure.array.ArrayUtils;

/**
 * @see Table
 * @author Omnaest
 */
public abstract class TableTest
{
  public abstract <E> Table<E> newTable( E[][] elementMatrix, Class<E> type );
  
  @Test
  public void testIterator()
  {
    //
    final String[][] elementMatrix = new String[][] { { "a", "b", "c" }, { "d", "e", "f" } };
    Table<String> tableAbstract = this.newTable( elementMatrix, String.class );
    
    //
    Iterator<ImmutableRow<String>> iterator = tableAbstract.iterator();
    assertNotNull( iterator );
    assertTrue( iterator.hasNext() );
    assertTrue( iterator.hasNext() );
    assertArrayEquals( elementMatrix[0], ArrayUtils.valueOf( iterator.next(), String.class ) );
    assertTrue( iterator.hasNext() );
    assertArrayEquals( elementMatrix[1], ArrayUtils.valueOf( iterator.next(), String.class ) );
    assertFalse( iterator.hasNext() );
    
  }
  
  @Test
  public void testTo() throws Exception
  {
    final String[][] elementMatrix = new String[][] { { "a", "b", "c" }, { "d", "e", "f" } };
    Table<String> table = this.newTable( elementMatrix, String.class );
    
    final String[][] array = table.to().array();
    assertArrayEquals( elementMatrix, array );
  }
  
  protected Table<String> filledTable( int rowSize, int columnSize )
  {
    String[][] elementMatrix = new String[rowSize][columnSize];
    for ( int ii = 0; ii < rowSize; ii++ )
    {
      for ( int jj = 0; jj < columnSize; jj++ )
      {
        elementMatrix[ii][jj] = ii + ":" + jj;
      }
    }
    return this.newTable( elementMatrix, String.class );
  }
}

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

import java.util.List;

import org.omnaest.utils.structure.table.IndexTable.IndexPositionPair;
import org.omnaest.utils.structure.table.concrete.IndexArrayTable;

public interface TableJoinable<T extends Table<?>>
{
  /**
   * See {@link IndexArrayTable#innerJoinByEqualColumn(IndexArrayTable, List)} but with another columnIndexJoinFormat.<br>
   * The index positions of each pair of columns for each table, are coded within an array of integer pairs:<br>
   * {{1,2},{2,3},{3,1}} means joining the current table column 1 with the join table column 2, and 2 with 3 and 3 with 1.
   * 
   * @see IndexTable#innerJoinByEqualColumn(Table, List)
   * @param joinTable
   * @param columnIndexPositionPairs
   * @return
   */
  public T innerJoinByEqualColumn( T joinTable, int[][] columnIndexPositionPairs );
  
  /**
   * Returns a new table object, which has the original table on the left, the joinTable on the right, and only the rows of both,
   * where the given index positions corresponds to column values that are equal.<br>
   * Compared are the index positions for the current table and the join table.
   * 
   * @see #innerJoinByEqualColumn(IndexArrayTable, int[][])
   * @param joinTable
   * @param columnIndexPositionPairList
   * @return
   */
  public T innerJoinByEqualColumn( T joinTable, List<IndexPositionPair> columnIndexPositionPairList );
}

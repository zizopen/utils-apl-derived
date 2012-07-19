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
package org.omnaest.utils.table2.impl.transformer;

import java.lang.reflect.Array;

import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.table2.Table;

class TableToArrayConverter<E> implements ElementConverter<Table<E>, E[][]>
{
  @SuppressWarnings("unchecked")
  @Override
  public E[][] convert( Table<E> table )
  {
    final int rowSize = table.rowSize();
    final int columnSize = table.columnSize();
    E[][] retvals = (E[][]) Array.newInstance( table.elementType(), rowSize, columnSize );
    
    for ( int rowIndex = 0; rowIndex < rowSize; rowIndex++ )
    {
      for ( int columnIndex = 0; columnIndex < columnSize; columnIndex++ )
      {
        retvals[rowIndex][columnIndex] = table.getElement( rowIndex, columnIndex );
      }
    }
    return retvals;
  }
}

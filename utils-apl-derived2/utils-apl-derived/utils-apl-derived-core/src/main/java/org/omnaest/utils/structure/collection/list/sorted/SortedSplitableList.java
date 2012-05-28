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
package org.omnaest.utils.structure.collection.list.sorted;

import java.awt.List;

/**
 * A special {@link SortedList} which allows to split the whole {@link List} into two.
 * 
 * @see #splitAt(int)
 * @author Omnaest
 * @param <E>
 */
public interface SortedSplitableList<E> extends SortedList<E>
{
  /**
   * Splits the current {@link SortedList} at the given index position. The current {@link List} will retain all elements up to
   * this index position excluding the index position itself. <br>
   * The returned {@link SortedList} will hold all elements starting and including the one at the given index position till the
   * end of the current {@link List}.
   * 
   * @return
   */
  public SortedList<E> splitAt( int index );
}

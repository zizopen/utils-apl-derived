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
package org.omnaest.utils.structure.collection.list;

import java.util.List;

/**
 * Defines an interface for lists that are based on an index structure. The performance of queries are much better, but
 * performance of inserting is reduced.
 * 
 * @see IndexTable
 * @author Omnaest
 */
public interface IndexList<E> extends List<E>
{
  /**
   * Returns all index positions, where the data values at are equal to the given element.
   * 
   * @see #indexOf(Object)
   * @see #lastIndexOf(Object)
   * @param e
   * @return
   */
  public int[] indexesOf( E e );
  
  /**
   * Returns all index positions of elements that are between both as paramter given elements. Equal elements are included in the
   * resulting array of index positions.
   * 
   * @param smallestElement
   * @param largestElement
   * @return
   */
  public int[] indexesOfElementsEqualOrBetween( E smallestElement, E largestElement );
  
  /**
   * Returns all index positions of elements that have a lesser value than the given element. Elements that equals are NOT
   * included.
   * 
   * @param element
   * @return
   */
  public int[] indexesOfElementsLessThan( E element );
  
  /**
   * Returns all index positions of elements that are greater than the given element. Equla elements are NOT inlcuded.
   * 
   * @param element
   * @return
   */
  public int[] indexesOfElementsGreaterThan( E element );
}

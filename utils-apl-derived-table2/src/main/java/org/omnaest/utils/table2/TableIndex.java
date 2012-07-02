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

import java.io.Serializable;
import java.util.Set;
import java.util.SortedMap;

/**
 * Index representation of a {@link Column} of a {@link Table}
 * 
 * @author Omnaest
 * @param <E>
 * @param <C>
 */
public interface TableIndex<E, C extends ImmutableCell<E>> extends Serializable
{
  
  public Set<E> keySet();
  
  public E lastKey();
  
  public E firstKey();
  
  public Set<Cell<E>> get( Object key );
  
  public boolean containsKey( Object key );
  
  public boolean isEmpty();
  
  public int size();
  
  /**
   * Returns the actual index position
   * 
   * @return
   */
  public int index();
  
  /**
   * Returns a {@link SortedMap} instance based on the {@link TableIndex} which can not be modified
   * 
   * @return
   */
  public SortedMap<E, Set<C>> asMap();
  
}

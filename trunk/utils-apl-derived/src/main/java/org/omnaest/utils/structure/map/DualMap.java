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
package org.omnaest.utils.structure.map;

import java.util.List;
import java.util.Map;

/**
 * A dual map is the dual way counterpart of the {@link Map} interface. All {@link DualMap} implementations should ensure that
 * both elements are treated as keys, so the search within large sized {@link DualMap}s should be fast using either of the
 * elements.<br>
 * <br>
 * Be aware of the fact that both indexes can have different sizes, since there can be 1:n relationships being put into the map,
 * but stored are only the last inserted 1:1 relationship. This means adding (a,c) and (b,c) will result in "b" being found for a
 * search after the second element "c". "a" will be lost in the first index in this case.
 * 
 * @author Omnaest
 * @param <FIRSTELEMENT>
 * @param <SECONDELEMENT>
 */
public interface DualMap<FIRSTELEMENT, SECONDELEMENT>
{
  
  /**
   * Clears the indexes.
   */
  public void clear();
  
  /**
   * Returns true, if the element is contained in one of the two indexes.
   * 
   * @param element
   * @return
   */
  public boolean contains( Object element );
  
  /**
   * Returns true, if the element is contained in one of the two indexes.
   * 
   * @param firstElement
   * @return
   */
  public boolean containsFirstElement( FIRSTELEMENT firstElement );
  
  /**
   * Returns true, if the element is contained in one of the two indexes.
   * 
   * @param secondElement
   * @return
   */
  public boolean containsSecondElement( SECONDELEMENT secondElement );
  
  /**
   * Returns true, if both indexes are empty.
   * 
   * @return
   */
  public boolean isEmpty();
  
  /**
   * Returns the first element resolved by the second element.
   * 
   * @see #getSecondElementBy(Object)
   * @param secondElement
   * @return
   */
  public FIRSTELEMENT getFirstElementBy( SECONDELEMENT secondElement );
  
  /**
   * Returns the second element resolved by the first element.
   * 
   * @see #getFirstElementBy(Object)
   * @param firstElement
   * @return
   */
  public SECONDELEMENT getSecondElementBy( FIRSTELEMENT firstElement );
  
  /**
   * @see #getFirstElementToSecondElementMap()
   * @see #getSecondElementList()
   * @return
   */
  public List<FIRSTELEMENT> getFirstElementList();
  
  /**
   * @see #getSecondElementToFirstElementMap()
   * @see #getFirstElementList()
   * @return
   */
  public List<SECONDELEMENT> getSecondElementList();
  
  /**
   * Puts a two elements into the {@link DualMap}
   * 
   * @param firstElement
   * @param secondElement
   * @return this
   */
  public DualMap<FIRSTELEMENT, SECONDELEMENT> put( FIRSTELEMENT firstElement, SECONDELEMENT secondElement );
  
  /**
   * @see #put(Object, Object)
   * @see #putAllFirstElementToSecondElement(Map)
   * @see #putAllSecondElementToFirstElement(Map)
   * @param firstElementAndSecondElementDualMap
   * @return this
   */
  public DualMap<FIRSTELEMENT, SECONDELEMENT> putAll( DualMap<? extends FIRSTELEMENT, ? extends SECONDELEMENT> firstElementAndSecondElementDualMap );
  
  /**
   * @see #putAll(DualMap)
   * @see #putAllSecondElementToFirstElement(Map)
   * @param firstElementToSecondElementMap
   * @return this
   */
  public DualMap<FIRSTELEMENT, SECONDELEMENT> putAllFirstElementToSecondElement( Map<? extends FIRSTELEMENT, ? extends SECONDELEMENT> firstElementToSecondElementMap );
  
  /**
   * @see #putAll(DualMap)
   * @see #putAllFirstElementToSecondElement(Map)
   * @param secondElementToFirstElementMap
   * @return this
   */
  public DualMap<FIRSTELEMENT, SECONDELEMENT> putAllSecondElementToFirstElement( Map<? extends SECONDELEMENT, ? extends FIRSTELEMENT> secondElementToFirstElementMap );
  
  /**
   * Returns a new {@link DualMap} instance with inverted first and second key.
   * 
   * @return {@link DualMap}
   */
  public DualMap<SECONDELEMENT, FIRSTELEMENT> invert();
  
  /**
   * Removes the given first element from the first index and the resolved second element from the second index.
   * 
   * @param firstElement
   */
  public void removeFirstElement( FIRSTELEMENT firstElement );
  
  /**
   * Removes the given second element from the second index and the resolved first element from the first index.
   * 
   * @param secondElement
   */
  public void removeSecondElement( SECONDELEMENT secondElement );
  
  /**
   * Returns the size of the larger index
   * 
   * @return
   */
  public int size();
  
  /**
   * Returns a new {@link Map} instance.
   * 
   * @see #getFirstElementToSecondElementMap()
   * @see #getFirstElementList()
   * @return
   */
  public Map<SECONDELEMENT, FIRSTELEMENT> getSecondElementToFirstElementMap();
  
  /**
   * Returns a new {@link Map} instance.
   * 
   * @see #getSecondElementToFirstElementMap()
   * @see #getSecondElementList()
   * @return
   */
  public Map<FIRSTELEMENT, SECONDELEMENT> getFirstElementToSecondElementMap();
  
}

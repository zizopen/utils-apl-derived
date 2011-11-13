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

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.BiMap;

/**
 * A {@link DualMap} is the dual way counterpart of the {@link Map} interface.<br>
 * <br>
 * All {@link DualMap} implementations should ensure that both elements are treated as keys, so the search within large sized
 * {@link DualMap}s should be fast using either of the elements.<br>
 * <br>
 * Be aware of the fact that the key and value indexes can have different sizes, since there can be 1:n relationships being put
 * into the map, but stored are only the last inserted 1:1 relationship. This means adding (a,c) and (b,c) will result in "b"
 * being found for a search after the value element "c". "a" will be lost in the key index in this case.<br>
 * This does differ from the {@link BiMap} definition.
 * 
 * @see Map
 * @author Omnaest
 * @param <K>
 * @param <V>
 */
public interface DualMap<K, V> extends Map<K, V>,Serializable
{
  
  /**
   * Returns true, if the element is contained as key or value
   * 
   * @param element
   * @return
   */
  public boolean contains( Object element );
  
  /**
   * @see #put(Object, Object)
   * @see #putAll(Map)
   * @see #putAllSecondElementToFirstElement(Map)
   * @param dualMap
   * @return this
   */
  public DualMap<K, V> putAll( DualMap<? extends K, ? extends V> dualMap );
  
  /**
   * Returns the same {@link DualMap} instance but with inverted key and value
   * 
   * @return {@link DualMap}
   */
  public DualMap<V, K> invert();
  
}

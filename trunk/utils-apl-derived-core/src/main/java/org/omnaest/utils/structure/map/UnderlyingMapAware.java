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

import java.util.Map;

/**
 * This interface makes a derivative type aware of an underlying {@link Map} implementation.
 * 
 * @see #getUnderlyingMap()
 * @see #setUnderlyingMap(Map)
 * @see UnderlyingPropertyMapAware
 */
public interface UnderlyingMapAware<K, V>
{
  /**
   * Returns the {@link Map} which underlies this class type facade.
   * 
   * @return
   */
  public Map<K, V> getUnderlyingMap();
  
  /**
   * Sets the {@link Map} which should underly this class type facade.
   * 
   * @param underlyingMap
   */
  public <M extends Map<K, V>> void setUnderlyingMap( M underlyingMap );
}

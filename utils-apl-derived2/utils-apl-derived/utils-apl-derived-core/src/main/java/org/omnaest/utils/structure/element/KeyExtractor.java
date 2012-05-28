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
package org.omnaest.utils.structure.element;

import org.omnaest.utils.structure.map.MapUtils;

/**
 * @see MapUtils#valueOf(KeyExtractor, Iterable)
 * @author Omnaest
 * @param <K>
 * @param <E>
 */
public interface KeyExtractor<K, E>
{
  /**
   * Extracts a key from the given element
   * 
   * @param element
   * @return
   */
  public K extractKey( E element );
}

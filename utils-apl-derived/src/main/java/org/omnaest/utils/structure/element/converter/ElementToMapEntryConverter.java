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
package org.omnaest.utils.structure.element.converter;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.omnaest.utils.structure.collection.ListUtils;

/**
 * A transformer from a {@link List} element type to a {@link Entry} type
 * 
 * @see ListUtils#asMap(Collection)
 * @author Omnaest
 * @param <K>
 * @param <V>
 * @param <E>
 */
public interface ElementToMapEntryConverter<E, K, V>
{
  
  /**
   * Transforms a given {@link List} element instance into an {@link Entry}. Null values must be handled by this method, too.
   * 
   * @param element
   * @return {@link Entry}
   */
  public Entry<K, V> convert( E element );
}

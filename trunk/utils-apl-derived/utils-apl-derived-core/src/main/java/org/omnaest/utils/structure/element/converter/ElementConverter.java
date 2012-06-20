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

import java.io.Serializable;
import java.util.Collection;

import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.iterator.IterableUtils;

/**
 * The provides a {@link #convert(Object)} method to transform one element instance into another.
 * 
 * @see ElementBidirectionalConverter
 * @see ElementConverterOneToMany
 * @see ElementConverterTypeAware
 * @see ListUtils#convert(Collection, ElementConverter)
 * @see SetUtils#convert(Iterable, ElementConverter)
 * @see IterableUtils#convert(Iterable, ElementConverter)
 * @see ElementConverterChain
 * @see Serializable
 */
public interface ElementConverter<FROM, TO> extends Serializable
{
  /**
   * Transforms a single element from one type into another.
   * 
   * @param element
   * @return converted element
   */
  public TO convert( FROM element );
}

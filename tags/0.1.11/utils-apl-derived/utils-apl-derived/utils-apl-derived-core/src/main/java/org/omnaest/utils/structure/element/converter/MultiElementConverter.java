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

import org.omnaest.utils.structure.collection.list.ListUtils;

/**
 * The provides the transformation method to transform one generic {@link Collection} instance into one or multiple elements of
 * other type. The resulting list will be merged to a ordered list by a transformation process, so the order will be kept.
 * 
 * @see ElementConverter
 * @see ListUtils#convert(Collection, MultiElementConverter, boolean)
 */
public interface MultiElementConverter<FROM, TO>
{
  /**
   * Transforms a single element from one type into an (ordered) list of the other types.
   * 
   * @param element
   * @return converted element
   */
  public Collection<TO> convert( FROM element );
}

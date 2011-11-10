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

import org.omnaest.utils.structure.collection.ListUtils;


/* ********************************************** Classes/Interfaces ********************************************** */
/**
 * The provides the transformation method to transform one generic element instance into another.
 * 
 * @see MultiElementConverter
 * @see ListUtils#convert(Collection, ElementConverter)
 */
public interface ElementConverter<FROM, TO>
{
  /**
   * Transforms a single element from one type into another.
   * 
   * @param element
   * @return converted element
   */
  public TO convert( FROM element );
}

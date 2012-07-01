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
package org.omnaest.utils.structure.element.converter;

/**
 * {@link ElementConverter} which additonally specify a {@link #convertBackwards(Object)} method, so that a two way conversion is
 * possible
 * 
 * @see ElementConverter
 * @see ElementConverterToBidirectionalConverterAdapter
 * @author Omnaest
 * @param <FROM>
 * @param <TO>
 */
public interface ElementBidirectionalConverter<FROM, TO> extends ElementConverter<FROM, TO>
{
  /**
   * Converts an element in backwards way
   * 
   * @param element
   * @return
   */
  public FROM convertBackwards( TO element );
}

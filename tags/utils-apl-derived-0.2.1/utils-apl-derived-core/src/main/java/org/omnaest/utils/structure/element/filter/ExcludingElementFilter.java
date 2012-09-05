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
package org.omnaest.utils.structure.element.filter;

/**
 * Abstract implementation of the {@link ElementFilter} which returns {@link ElementFilter.FilterMode#EXCLUDING} for the
 * {@link #getFilterMode()} method
 * 
 * @author Omnaest
 * @param <E>
 */
public abstract class ExcludingElementFilter<E> implements ElementFilter<E>
{
  /**
   * The filter method should return true if the given element should be excluded
   * 
   * @see ElementFilter#filter(Object)
   * @param element
   * @return
   */
  public abstract boolean filter( E element );
  
  @Override
  public FilterMode getFilterMode()
  {
    return FilterMode.EXCLUDING;
  }
}

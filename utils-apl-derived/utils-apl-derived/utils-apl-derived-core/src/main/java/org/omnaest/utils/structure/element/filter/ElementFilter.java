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
 * An {@link ElementFilter} provides a {@link #filter(Object)} and a {@link #getFilterMode()} method. Both allow to identify
 * elements to be excluded or included. <br>
 * <br>
 * The {@link ExcludingElementFilter} provides an abstract implementation which return the {@link FilterMode#EXCLUDING} by default
 * and only needs to implement the {@link #filter(Object)} method
 * 
 * @see ExcludingElementFilter
 * @author Omnaest
 * @param <E>
 */
public interface ElementFilter<E>
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * Declares the behavior mode which can be {@link #EXCLUDING} or {@link #INCLUDING}
   * 
   * @author Omnaest
   */
  public static enum FilterMode
  {
    EXCLUDING,
    INCLUDING
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Returns the {@link FilterMode} in which the filter acts. This can be {@link FilterMode#EXCLUDING} or
   * {@link FilterMode#INCLUDING}
   * 
   * @return {@link FilterMode}
   */
  public ElementFilter.FilterMode getFilterMode();
  
  /**
   * The {@link #filter(Object)} method should return true if the given element matches the internal {@link FilterMode}
   * 
   * @param element
   * @return
   */
  public boolean filter( E element );
}

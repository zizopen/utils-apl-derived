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

import java.util.Collection;

import org.omnaest.utils.structure.element.filter.ElementFilter.FilterMode;

/**
 * {@link ExcludingElementFilterIndexPositionBased} which filters all elements which do not have any of the given index numbers
 * 
 * @see FilterMode
 * @author Omnaest
 */
public class ElementFilterIndexPositionBasedForGivenIndexes implements ExcludingElementFilterIndexPositionBased
{
  
  /* ********************************************** Variables ********************************************** */
  private Collection<Integer> indexCollection = null;
  private FilterMode          mode            = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see FilterMode
   * @param indexCollection
   * @param mode
   */
  public ElementFilterIndexPositionBasedForGivenIndexes( Collection<Integer> indexCollection, FilterMode mode )
  {
    super();
    this.indexCollection = indexCollection;
    this.mode = mode;
  }
  
  @Override
  public boolean filter( int indexPosition )
  {
    boolean contained = this.indexCollection.contains( indexPosition );
    return this.indexCollection != null
           && ( ( FilterMode.INCLUDING.equals( this.mode ) && !contained ) | ( ( FilterMode.EXCLUDING.equals( this.mode ) && contained ) ) );
  }
  
}

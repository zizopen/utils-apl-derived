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
 * Filter which is based on the index position of an element within the related structure
 * 
 * @author Omnaest
 */
public interface ExcludingElementFilterIndexPositionBased
{
  /**
   * Returns true for all to be excluded elements with the given index position
   * 
   * @param indexPosition
   * @return
   */
  public boolean filter( int indexPosition );
}

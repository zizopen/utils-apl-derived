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
package org.omnaest.utils.structure.element.accessor;

/**
 * An {@link AccessorReadable} allows to redirect read access to an underlying element
 * 
 * @see Accessor
 * @see AccessorWritable
 * @see #getElement()
 * @author Omnaest
 * @param <E>
 */
public interface AccessorReadable<E>
{
  
  /**
   * Returns the element the {@link AccessorReadable} does point to
   * 
   * @return
   */
  public E getElement();
  
}

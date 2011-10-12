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
package org.omnaest.utils.structure.element;

/**
 * Simple predefined {@link ElementWrapper} around an arbitrary element
 * 
 * @author Omnaest
 * @param <E>
 */
public abstract class ElementWrapper<E>
{
  /* ********************************************** Variables ********************************************** */
  protected E element = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param element
   */
  public ElementWrapper( E element )
  {
    super();
    this.element = element;
  }
  
  /**
   * @return the element
   */
  public E getElement()
  {
    return this.element;
  }
  
}

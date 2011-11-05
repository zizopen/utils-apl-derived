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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Simple predefined {@link ElementHolderUnmodifiable} around an arbitrary element
 * 
 * @see ElementHolder
 * @author Omnaest
 * @param <E>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ElementHolderUnmodifiable<E>
{
  /* ********************************************** Variables ********************************************** */
  @XmlElement
  protected E element = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param element
   */
  public ElementHolderUnmodifiable( E element )
  {
    super();
    this.element = element;
  }
  
  protected ElementHolderUnmodifiable()
  {
    super();
  }
  
  /**
   * @return the element
   */
  public E getElement()
  {
    return this.element;
  }
  
  /**
   * Sets the given element to the {@link ElementHolderUnmodifiable}
   * 
   * @param element
   */
  protected void setElement( E element )
  {
    this.element = element;
  }
  
}

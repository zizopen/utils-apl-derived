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

import org.omnaest.utils.structure.element.accessor.AccessorReadable;

/**
 * Simple predefined {@link ElementHolderUnmodifiable} around an arbitrary element.
 * 
 * @see ElementHolder
 * @author Omnaest
 * @param <E>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ElementHolderUnmodifiable<E> implements AccessorReadable<E>
{
  /* ********************************************** Variables ********************************************** */
  @XmlElement
  protected volatile E element = null;
  
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
  @Override
  public E getElement()
  {
    return this.element;
  }
  
  /**
   * Returns true if the current element is not null
   * 
   * @return
   */
  public boolean hasElement()
  {
    // 
    return this.element != null;
  }
  
  /**
   * Sets the given element to the {@link ElementHolderUnmodifiable}
   * 
   * @param element
   * @return this
   */
  protected ElementHolderUnmodifiable<E> setElement( E element )
  {
    this.element = element;
    return this;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "ElementHolderUnmodifiable [element=" );
    builder.append( this.element );
    builder.append( "]" );
    return builder.toString();
  }
  
}

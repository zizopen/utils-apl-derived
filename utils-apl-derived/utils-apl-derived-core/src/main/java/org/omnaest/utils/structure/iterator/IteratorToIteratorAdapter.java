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
package org.omnaest.utils.structure.iterator;

import java.util.Iterator;

import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverter;

/**
 * Adapter from an {@link Iterator} with one specific type to another {@link Iterator} with another type using a given
 * {@link ElementBidirectionalConverter}
 * 
 * @author Omnaest
 * @param <TO>
 * @param <FROM>
 */
public final class IteratorToIteratorAdapter<TO, FROM> implements Iterator<TO>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Iterator<FROM>                          iterator;
  private final ElementBidirectionalConverter<FROM, TO> elementBidirectionalConverter;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see IteratorToIteratorAdapter
   * @param iterator
   * @param elementBidirectionalConverter
   */
  public IteratorToIteratorAdapter( Iterator<FROM> iterator, ElementBidirectionalConverter<FROM, TO> elementBidirectionalConverter )
  {
    this.iterator = iterator;
    this.elementBidirectionalConverter = elementBidirectionalConverter;
  }
  
  @Override
  public boolean hasNext()
  {
    // 
    return this.iterator.hasNext();
  }
  
  @Override
  public TO next()
  {
    // 
    return this.elementBidirectionalConverter.convert( this.iterator.next() );
  }
  
  @Override
  public void remove()
  {
    this.iterator.remove();
  }
}

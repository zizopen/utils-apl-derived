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
package org.omnaest.utils.structure.iterator.decorator;

import java.util.Iterator;

import org.omnaest.utils.structure.element.converter.ElementConverter;

/**
 * Decorator of an {@link Iterator} which uses an {@link ElementConverter} to convert the output of the {@link #next()} method of
 * the underlying {@link Iterator} to another type .
 * 
 * @author Omnaest
 */
public class IteratorToIteratorAdapter<FROM, TO> implements Iterator<TO>
{
  /* ********************************************** Variables ********************************************** */
  private final ElementConverter<FROM, TO> elementConverter;
  private final Iterator<FROM>             iterator;
  private final boolean                    hasIteratorAndElementConverter;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @see IteratorToIteratorAdapter
   * @param iterator
   * @param elementConverter
   */
  public IteratorToIteratorAdapter( Iterator<FROM> iterator, ElementConverter<FROM, TO> elementConverter )
  {
    this.iterator = iterator;
    this.elementConverter = elementConverter;
    this.hasIteratorAndElementConverter = this.iterator != null && this.elementConverter != null;
  }
  
  @Override
  public boolean hasNext()
  {
    return this.hasIteratorAndElementConverter && this.iterator.hasNext();
  }
  
  @Override
  public void remove()
  {
    this.iterator.remove();
  }
  
  @Override
  public TO next()
  {
    //
    TO retval = null;
    if ( this.hasIteratorAndElementConverter )
    {
      final FROM nextElement = this.iterator.next();
      retval = this.elementConverter.convert( nextElement );
    }
    return retval;
  }
}

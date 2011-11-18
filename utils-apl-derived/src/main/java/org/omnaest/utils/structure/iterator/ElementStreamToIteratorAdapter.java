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
package org.omnaest.utils.structure.iterator;

import java.util.Iterator;

import org.omnaest.utils.structure.element.cached.CachedElement;
import org.omnaest.utils.structure.element.cached.CachedElement.ValueResolver;
import org.omnaest.utils.structure.element.ElementStream;

/**
 * Allows to wrap a given {@link ElementStream} as {@link Iterator}.<br>
 * <br>
 * Notice:<br>
 * The {@link #remove()} method is not supported and therefore throws an {@link UnsupportedOperationException}
 * 
 * @author Omnaest
 */
public class ElementStreamToIteratorAdapter<E> implements Iterator<E>
{
  /* ********************************************** Variables ********************************************** */
  protected ElementStream<E> elementStream = null;
  protected CachedElement<E> cachedElement = new CachedElement<E>( new ValueResolver<E>()
                                           {
                                             @Override
                                             public E resolveValue()
                                             {
                                               return ElementStreamToIteratorAdapter.this.elementStream.next();
                                             }
                                           } );
  
  /* ********************************************** Methods ********************************************** */
  public ElementStreamToIteratorAdapter( ElementStream<E> elementStream )
  {
    super();
    this.elementStream = elementStream;
  }
  
  @Override
  public boolean hasNext()
  {
    return this.cachedElement.getValue() != null;
  }
  
  @Override
  public E next()
  {
    E retval = this.cachedElement.getValue();
    this.cachedElement.clearCache();
    return retval;
  }
  
  @Override
  public void remove()
  {
    throw new UnsupportedOperationException( "ElementStreamToIteratorAdapter cannot remove elements from an ElementStream" );
  }
  
}

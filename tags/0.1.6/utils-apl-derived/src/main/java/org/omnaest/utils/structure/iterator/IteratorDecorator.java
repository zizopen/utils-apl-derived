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

/**
 * An {@link IteratorDecorator} decorates an existing {@link Iterator} instance. If it is subclassed method invocations can be
 * intercepted by overriding the respective methods.
 * 
 * @author Omnaest
 * @param <E>
 */
public class IteratorDecorator<E> implements Iterator<E>
{
  /* ********************************************** Variables ********************************************** */
  protected Iterator<E> iterator = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see IteratorDecorator
   * @param iterator
   */
  public IteratorDecorator( Iterator<E> iterator )
  {
    super();
    this.iterator = iterator;
  }
  
  /**
   * @return the iterator
   */
  protected Iterator<E> getIterator()
  {
    return this.iterator;
  }
  
  /**
   * @param iterator
   *          the iterator to set
   */
  protected void setIterator( Iterator<E> iterator )
  {
    this.iterator = iterator;
  }
  
  /**
   * @return
   * @see java.util.Iterator#hasNext()
   */
  public boolean hasNext()
  {
    return this.iterator.hasNext();
  }
  
  /**
   * @return
   * @see java.util.Iterator#next()
   */
  public E next()
  {
    return this.iterator.next();
  }
  
  /**
   * @see java.util.Iterator#remove()
   */
  public void remove()
  {
    this.iterator.remove();
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( this.iterator );
    return builder.toString();
  }
  
}

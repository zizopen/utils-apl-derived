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

import org.omnaest.utils.structure.element.ElementHolder;
import org.omnaest.utils.structure.element.Range;

/**
 * A {@link RangedIterable} will only iterate over the subset of elements within the given {@link Range} of index positions.
 * 
 * @author Omnaest
 * @param <T>
 */
public class RangedIterable<T> implements Iterable<T>
{
  /* ********************************************** Variables ********************************************** */
  protected Range       range    = null;
  protected Iterable<T> iterable = null;
  
  /* ********************************************** Methods ********************************************** */
  
  public RangedIterable( Range range, Iterable<T> iterable )
  {
    super();
    this.range = range;
    this.iterable = iterable;
  }
  
  @Override
  public Iterator<T> iterator()
  {
    //
    final Iterator<T> iterator = this.iterable.iterator();
    
    //
    long nextIndexPosition = 0;
    while ( nextIndexPosition < this.range.getNumberFrom() )
    {
      if ( iterator.hasNext() )
      {
        iterator.next();
        nextIndexPosition++;
      }
      else
      {
        break;
      }
    }
    
    //
    final ElementHolder<Long> elementHolderIndexPosition = new ElementHolder<Long>( nextIndexPosition );
    return new Iterator<T>()
    {
      /* ********************************************** Variables ********************************************** */
      @SuppressWarnings("hiding")
      private long nextIndexPosition = elementHolderIndexPosition.getElement();
      
      /* ********************************************** Methods ********************************************** */
      @Override
      public boolean hasNext()
      {
        return RangedIterable.this.range.isWithinRange( this.nextIndexPosition ) && iterator.hasNext();
      }
      
      @Override
      public T next()
      {
        //
        T retval = null;
        
        //
        if ( this.hasNext() )
        {
          //
          retval = iterator.next();
          this.nextIndexPosition++;
        }
        
        //
        return retval;
      }
      
      @Override
      public void remove()
      {
        iterator.remove();
      }
    };
    
  }
}

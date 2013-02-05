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
 * A {@link ChainedIterable} allows to chain a given array of {@link Iterable} instances and merge them together into one single
 * {@link Iterable}. The given order of the array is kept during iteration.
 * 
 * @see Iterable
 * @author Omnaest
 * @param <T>
 */
public class ChainedIterable<T> implements Iterable<T>
{
  /* ********************************************** Variables ********************************************** */
  protected Iterable<T>[] iterables = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see ChainedIterable
   * @param iterables
   */
  public ChainedIterable( Iterable<T>... iterables )
  {
    super();
    this.iterables = iterables;
  }
  
  @Override
  public Iterator<T> iterator()
  {
    return new Iterator<T>()
    {
      /* ********************************************** Variables ********************************************** */
      private int         iterableIndexPosition = -1;
      private Iterator<T> currentIterator       = null;
      
      /* ********************************************** Methods ********************************************** */
      
      /**
       * Switches the current iterable to the next available. Returns false if no next one is present.
       * 
       * @return
       */
      public boolean switchToNextIterable()
      {
        //
        boolean retval = false;
        
        //
        this.currentIterator = null;
        
        //
        if ( ++this.iterableIndexPosition < ChainedIterable.this.iterables.length )
        {
          this.currentIterator = ChainedIterable.this.iterables[this.iterableIndexPosition].iterator();
          retval = true;
        }
        
        //
        return retval;
      }
      
      @Override
      public boolean hasNext()
      {
        //
        boolean retval = false;
        
        //
        if ( ( this.currentIterator != null || ( this.switchToNextIterable() && this.currentIterator != null ) ) )
        {
          //
          while ( !this.currentIterator.hasNext() && ( this.switchToNextIterable() && this.currentIterator != null ) )
          {
          }
          
          //
          retval = this.currentIterator != null && this.currentIterator.hasNext();
        }
        
        // 
        return retval;
      }
      
      @Override
      public T next()
      {
        //
        T retval = null;
        
        //
        if ( this.hasNext() )
        {
          retval = this.currentIterator.next();
        }
        
        // 
        return retval;
      }
      
      @Override
      public void remove()
      {
        if ( this.currentIterator != null )
        {
          this.currentIterator.remove();
        }
      }
    };
  }
  
}

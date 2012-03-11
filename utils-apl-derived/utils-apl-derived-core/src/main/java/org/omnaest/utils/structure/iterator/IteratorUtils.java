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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.element.ElementStream;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.factory.Factory;
import org.omnaest.utils.structure.iterator.decorator.ConverterIteratorDecorator;
import org.omnaest.utils.structure.iterator.decorator.LockingIteratorDecorator;

/**
 * Helper related to {@link Iterator}
 * 
 * @see IterableUtils
 * @author Omnaest
 */
public class IteratorUtils
{
  
  /**
   * Resolves the size of a given {@link Iterator} by iterating over it.
   * 
   * @param iterator
   * @return
   */
  public static int size( Iterator<?> iterator )
  {
    //
    int retval = 0;
    
    //
    if ( iterator != null )
    {
      for ( ; iterator.hasNext(); iterator.next() )
      {
        retval++;
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns a view on the given {@link Iterator} which uses a {@link Lock} to synchronize all its methods.
   * 
   * @param iterator
   * @param lock
   * @return
   */
  public static <E> Iterator<E> lockedIterator( Iterator<E> iterator, Lock lock )
  {
    return new LockingIteratorDecorator<E>( iterator, lock );
  }
  
  /**
   * Returns a view on the given {@link Iterator} which uses a {@link ReentrantLock} to synchronize all its methods.
   * 
   * @param iterator
   * @return
   */
  public static <E> Iterator<E> lockedByReentrantLockIterator( Iterator<E> iterator )
  {
    Lock lock = new ReentrantLock();
    return lockedIterator( iterator, lock );
  }
  
  /**
   * Returns a new {@link Iterator} instance which will iterate over all {@link Iterator} instances created by the given
   * {@link Factory}. If the {@link Factory#newInstance()} returns null, the {@link Iterator} ends.
   * 
   * @param iteratorFactory
   * @return
   */
  public static <E> Iterator<E> factoryBasedIterator( final Factory<Iterator<E>> iteratorFactory )
  {
    //
    Assert.isNotNull( iteratorFactory );
    
    //
    return new Iterator<E>()
    {
      /* ********************************************** Variables ********************************************** */
      private Iterator<E> iterator = null;
      
      /* ********************************************** Methods ********************************************** */
      
      /**
       * @return
       */
      private Iterator<E> getOrSwitchIterator()
      {
        //
        if ( this.iterator == null )
        {
          this.iterator = iteratorFactory.newInstance();
        }
        
        //
        return this.iterator;
      }
      
      @Override
      public boolean hasNext()
      {
        //
        boolean retval = false;
        
        //
        Iterator<E> iterator = this.getOrSwitchIterator();
        while ( iterator != null && !iterator.hasNext() )
        {
          this.iterator = null;
          iterator = this.getOrSwitchIterator();
        }
        
        //
        retval = iterator != null;
        
        //
        return retval;
      }
      
      @Override
      public E next()
      {
        //
        Iterator<E> iterator = this.getOrSwitchIterator();
        
        //
        return iterator != null ? iterator.next() : null;
      }
      
      @Override
      public void remove()
      {
        //
        Iterator<E> iterator = this.getOrSwitchIterator();
        
        //
        if ( iterator != null )
        {
          iterator.remove();
        }
      }
    };
  }
  
  /**
   * Returns a new decorator instance of the given {@link Iterator} which uses the given {@link ElementConverter} to convert the
   * result of the {@link Iterator#next()} method. <br>
   * <br>
   * If the given {@link ElementConverter} or {@link Iterator} is null, this method return null.
   * 
   * @param iterator
   * @param elementConverter
   * @return
   */
  public static <TO, FROM> Iterator<TO> convertingIteratorDecorator( Iterator<FROM> iterator,
                                                                     ElementConverter<FROM, TO> elementConverter )
  {
    return iterator != null && elementConverter != null ? new ConverterIteratorDecorator<FROM, TO>( iterator, elementConverter )
                                                       : null;
  }
  
  /**
   * Returns an {@link Iterator} adapter on a given {@link ElementStream}
   * 
   * @param elementStream
   * @return
   */
  public static <E> Iterator<E> adapter( final ElementStream<E> elementStream )
  {
    return new ElementStreamToIteratorAdapter<E>( elementStream );
  }
  
  /**
   * Returns the {@link Iterator} instances of the given {@link Iterable}s. If an {@link Iterator} instance is null it will not be
   * added to the returned array. This circumstance can lead to different array sizes.
   * 
   * @param iterables
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <E> Iterator<E>[] valueOf( Iterable<E>... iterables )
  {
    //    
    final List<Iterator<E>> retlist = new ArrayList<Iterator<E>>();
    
    //
    for ( Iterable<E> iterable : iterables )
    {
      //
      final Iterator<E> iterator = iterable.iterator();
      if ( iterator != null )
      {
        retlist.add( iterator );
      }
    }
    
    //
    return retlist.toArray( new Iterator[retlist.size()] );
  }
  
  /**
   * Returns the {@link ListIterator} instances of the given {@link List}s. If an {@link ListIterator} instance is null it will
   * not be added to the returned array. This circumstance can lead to different array sizes.
   * 
   * @param lists
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <E> ListIterator<E>[] valueOf( List<E>... lists )
  {
    //    
    final List<Iterator<E>> retlist = new ArrayList<Iterator<E>>();
    
    //
    for ( List<E> list : lists )
    {
      //
      final ListIterator<E> listIterator = list.listIterator();
      if ( listIterator != null )
      {
        retlist.add( listIterator );
      }
    }
    
    //
    return retlist.toArray( new ListIterator[retlist.size()] );
  }
}

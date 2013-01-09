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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.ElementStream;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterChain;
import org.omnaest.utils.structure.element.factory.Factory;
import org.omnaest.utils.structure.iterator.decorator.IteratorDecorator;
import org.omnaest.utils.structure.iterator.decorator.IteratorToIteratorAdapter;
import org.omnaest.utils.structure.iterator.decorator.LockingIteratorDecorator;

import com.google.common.collect.AbstractIterator;

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
   * Returns a view on the given {@link Iterator} which uses a {@link ThreadLocal} instance to cache resolved elements per
   * {@link Thread}. The underlying {@link Iterator} will by locked to be usable by multiple threads.
   * 
   * @see #lockedIterator(Iterator, Lock)
   * @see ThreadLocalCachedIterator
   * @param iterator
   * @return
   */
  public static <E> Iterator<E> threadLocalCachedIterator( Iterator<E> iterator )
  {
    Assert.isNotNull( iterator, "Iterator must not be null" );
    return new ThreadLocalCachedIterator<E>( iterator );
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
        while ( this.iterator != null && !this.iterator.hasNext() )
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
        final Iterator<E> iterator = this.getOrSwitchIterator();
        return iterator != null;
      }
      
      @Override
      public E next()
      {
        //
        final Iterator<E> iterator = this.getOrSwitchIterator();
        return iterator != null ? iterator.next() : null;
      }
      
      @Override
      public void remove()
      {
        //
        final Iterator<E> iterator = this.getOrSwitchIterator();
        if ( iterator != null )
        {
          iterator.remove();
        }
      }
    };
  }
  
  /**
   * Merges all elements immediately into a single and new {@link Iterator} instance.<br>
   * All given {@link Iterator} instances will be traversed after calling this method.
   * 
   * @see #chained(Iterator...)
   * @param iterators
   * @return
   */
  public static <E> Iterator<E> merge( Iterator<E>... iterators )
  {
    //
    final List<E> retlist = new ArrayList<E>();
    
    //
    for ( Iterator<E> iterator : iterators )
    {
      retlist.addAll( ListUtils.valueOf( iterator ) );
    }
    
    //
    return retlist.iterator();
  }
  
  /**
   * Returns a new {@link Iterator} instance which contains all elements from the given {@link Iterator} and the additional given
   * elements. <br>
   * All given {@link Iterator} instances will be traversed after calling this method.
   * 
   * @see #chained(Iterator...)
   * @param iterator
   * @param elements
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <E> Iterator<E> addToNewIterator( Iterator<E> iterator, E... elements )
  {
    return merge( iterator, Arrays.asList( elements ).iterator() );
  }
  
  /**
   * Returns an {@link Iterator} wrapper which chains the given {@link Iterator} instances. The single {@link Iterator} instances
   * will only executed when the returned wrapper iterator points to them.
   * 
   * @see #merge(Iterator...)
   * @param iterators
   * @return
   */
  public static <E> Iterator<E> chained( Iterator<E>... iterators )
  {
    return new ChainedIterator<E>( iterators );
  }
  
  /**
   * Returns an {@link Iterator} which calls {@link Iterator#next()} of all given {@link Iterator}s in a round robin way
   * 
   * @param iterators
   * @return
   */
  public static <E> Iterator<E> roundRobin( Iterator<E>... iterators )
  {
    final Set<Iterator<E>> unemptyIteratorSet = SetUtils.valueOf( iterators );
    {
      List<Iterator<E>> removableIteratorList = new ArrayList<Iterator<E>>();
      for ( Iterator<E> iterator : unemptyIteratorSet )
      {
        if ( !iterator.hasNext() )
        {
          removableIteratorList.add( iterator );
        }
      }
      unemptyIteratorSet.removeAll( removableIteratorList );
    }
    return new AbstractIterator<E>()
    {
      private Queue<Iterator<E>> remainingIteratorQueue = new ArrayDeque<Iterator<E>>();
      
      @Override
      protected E computeNext()
      {
        //
        if ( this.remainingIteratorQueue.isEmpty() )
        {
          this.remainingIteratorQueue.addAll( unemptyIteratorSet );
        }
        
        //
        if ( this.remainingIteratorQueue.isEmpty() )
        {
          return this.endOfData();
        }
        
        //
        E retval = null;
        
        Iterator<E> iterator = this.remainingIteratorQueue.poll();
        retval = iterator.next();
        
        if ( !iterator.hasNext() )
        {
          unemptyIteratorSet.remove( iterator );
        }
        
        //
        return retval;
      }
      
    };
    
  }
  
  /**
   * Returns a {@link List} of the {@link Iterator}s related to the given {@link Iterable}s
   * 
   * @param iterables
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <E> Iterator<E>[] valueOfMultiple( Iterable<E>... iterables )
  {
    List<Iterator<E>> retlist = new ArrayList<Iterator<E>>();
    
    if ( iterables != null )
    {
      for ( Iterable<E> iterable : iterables )
      {
        if ( iterable != null )
        {
          Iterator<E> iterator = iterable.iterator();
          if ( iterator != null )
          {
            retlist.add( iterator );
          }
        }
      }
    }
    
    return retlist.toArray( new Iterator[0] );
  }
  
  /**
   * Returns a new decorator instance of the given {@link Iterator} which uses the given {@link ElementConverter} to convert the
   * result of the {@link Iterator#next()} method. <br>
   * <br>
   * If the given {@link ElementConverter} or {@link Iterator} is null, this method return null.<br>
   * <br>
   * The elements will be converted at traversal time not in advance.
   * 
   * @param iterator
   * @param elementConverter
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <TO, FROM> Iterator<TO> adapter( Iterator<? extends FROM> iterator, ElementConverter<FROM, TO> elementConverter )
  {
    //
    final boolean referencesAreNotNull = iterator != null && elementConverter != null;
    return referencesAreNotNull ? new IteratorToIteratorAdapter<FROM, TO>( (Iterator<FROM>) iterator, elementConverter ) : null;
  }
  
  /**
   * Similar to {@link #adapter(Iterator, ElementConverter)}
   * 
   * @param iterator
   * @param elementConverterFirst
   * @param elementConverterSecond
   * @return
   */
  public static <TO, FROM, T> Iterator<TO> adapter( Iterator<FROM> iterator,
                                                    ElementConverter<FROM, ? extends T> elementConverterFirst,
                                                    ElementConverter<T, ? extends TO> elementConverterSecond )
  {
    //
    final boolean referencesAreNotNull = iterator != null && elementConverterFirst != null && elementConverterSecond != null;
    return referencesAreNotNull ? new IteratorToIteratorAdapter<FROM, TO>(
                                                                           iterator,
                                                                           new ElementConverterChain<FROM, TO>(
                                                                                                                elementConverterFirst,
                                                                                                                elementConverterSecond ) )
                               : null;
  }
  
  /**
   * Similar to {@link #adapter(Iterator, ElementConverter)}
   * 
   * @param iterator
   * @param elementConverterFirst
   * @param elementConverterSecond
   * @param elementConverterThird
   * @return
   */
  public static <TO, FROM, T1, T2> Iterator<TO> adapter( Iterator<FROM> iterator,
                                                         ElementConverter<FROM, ? extends T1> elementConverterFirst,
                                                         ElementConverter<T1, ? extends T2> elementConverterSecond,
                                                         ElementConverter<T2, ? extends TO> elementConverterThird )
  {
    // 
    final boolean referencesAreNotNull = iterator != null && elementConverterFirst != null && elementConverterSecond != null
                                         && elementConverterThird != null;
    return referencesAreNotNull ? new IteratorToIteratorAdapter<FROM, TO>(
                                                                           iterator,
                                                                           new ElementConverterChain<FROM, TO>(
                                                                                                                elementConverterFirst,
                                                                                                                elementConverterSecond,
                                                                                                                elementConverterThird ) )
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
  
  /**
   * Returns an {@link Iterator} instance for the given elements
   * 
   * @param elements
   * @return new instance of an {@link Iterator}
   */
  public static <E> Iterator<E> valueOf( E... elements )
  {
    return Arrays.asList( elements ).iterator();
  }
  
  /**
   * Drains the given {@link Iterator} to the given {@link Collection} <br>
   * This invokes the {@link Iterator#remove()} method for every element drained.
   * 
   * @param iterator
   * @param collection
   */
  public static <E> void drainTo( Iterator<E> iterator, Collection<E> collection )
  {
    //
    if ( iterator != null && collection != null )
    {
      while ( iterator.hasNext() )
      {
        final E nextElement = iterator.next();
        collection.add( nextElement );
        iterator.remove();
      }
    }
  }
  
  /**
   * Drains the given {@link Iterator} by the given maximum number of elements to the given {@link Collection}. <br>
   * This invokes the {@link Iterator#remove()} method for every element drained.
   * 
   * @param iterator
   * @param collection
   * @param maxNumberOfElements
   */
  public static <E> void drainTo( Iterator<E> iterator, Collection<E> collection, int maxNumberOfElements )
  {
    //
    if ( iterator != null && collection != null )
    {
      for ( int ii = 0; ii < maxNumberOfElements && iterator.hasNext(); ii++ )
      {
        final E nextElement = iterator.next();
        collection.add( nextElement );
        iterator.remove();
      }
    }
  }
  
  /**
   * Returns a new decorator instance wrapping the given {@link Iterator} which will limit the elements which can be retrieved to
   * the given number
   * 
   * @param iterator
   *          {@link Iterator}
   * @param limit
   * @return
   */
  public static <E> Iterator<E> limitingIteratorDecorator( Iterator<E> iterator, final int limit )
  {
    return new IteratorDecorator<E>( iterator )
    {
      /* ********************************************** Variables ********************************************** */
      private int counter = 0;
      
      /* ********************************************** Methods ********************************************** */
      
      @Override
      public boolean hasNext()
      {
        return isWithinLimit() && super.hasNext();
      }
      
      /**
       * @param limit
       * @return
       */
      private boolean isWithinLimit()
      {
        return this.counter < limit;
      }
      
      @Override
      public E next()
      {
        //
        if ( !this.isWithinLimit() )
        {
          throw new NoSuchElementException();
        }
        
        //
        this.counter++;
        return super.next();
      }
    };
  }
  
  /**
   * Returns an {@link Iterator} which returns a filtered subset of the given {@link Iterator} based on the given filter
   * {@link BitSet}
   * 
   * @param iterator
   * @param filter
   * @return new {@link Iterator} instance
   */
  public static <E> Iterator<E> filtered( final Iterator<E> iterator, final BitSet filter )
  {
    if ( iterator == null )
    {
      return empty();
    }
    
    return new Iterator<E>()
    {
      private int     index                     = 0;
      private boolean hasNoMoreFilteredElements = false;
      
      @Override
      public boolean hasNext()
      {
        forwardToNextFilterBit( iterator, filter );
        return !this.hasNoMoreFilteredElements && iterator.hasNext();
      }
      
      private void forwardToNextFilterBit( final Iterator<E> iterator, final BitSet filter )
      {
        if ( !this.hasNoMoreFilteredElements )
        {
          int nextSetBit = -1;
          while ( this.index >= 0 && this.index < ( nextSetBit = filter.nextSetBit( this.index ) ) && iterator.hasNext() )
          {
            this.fetchElement();
          }
          this.hasNoMoreFilteredElements = nextSetBit < 0;
        }
      }
      
      @Override
      public E next()
      {
        if ( this.hasNext() )
        {
          return fetchElement();
        }
        throw new NoSuchElementException();
      }
      
      private E fetchElement()
      {
        this.index++;
        return iterator.next();
      }
      
      @Override
      public void remove()
      {
        iterator.remove();
      }
    };
  }
  
  /**
   * Returns an empty {@link Iterator}
   * 
   * @return
   */
  public static <E> Iterator<E> empty()
  {
    return new Iterator<E>()
    {
      @Override
      public boolean hasNext()
      {
        return false;
      }
      
      @Override
      public E next()
      {
        throw new NoSuchElementException();
      }
      
      @Override
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
  
}

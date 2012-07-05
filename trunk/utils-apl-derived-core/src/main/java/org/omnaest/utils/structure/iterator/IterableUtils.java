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

import java.util.BitSet;
import java.util.Iterator;

import org.apache.commons.lang3.ObjectUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.ElementStream;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.factory.Factory;

/**
 * Helper class related to {@link Iterable} instances
 * 
 * @see IteratorUtils
 * @author Omnaest
 */
public class IterableUtils
{
  
  /**
   * Returns a new {@link Iterable} instance which uses an {@link Iterator} adapter based on the resolved
   * {@link Iterable#iterator()} instance. <br>
   * <br>
   * The elements will be converted at traversal time not in advance.
   * 
   * @param iterable
   *          {@link Iterable}
   * @param elementConverter
   *          {@link ElementConverter}
   * @return new instance
   */
  public static <FROM, TO> Iterable<TO> adapter( final Iterable<FROM> iterable, final ElementConverter<FROM, TO> elementConverter )
  {
    return new Iterable<TO>()
    {
      @Override
      public Iterator<TO> iterator()
      {
        return IteratorUtils.adapter( iterable.iterator(), elementConverter );
      }
    };
  }
  
  /**
   * Returns a new {@link Iterable} instance for the given one which will return a circular {@link Iterator} which circulates
   * endlessly.<br>
   * <br>
   * Be aware of the fact, that the given {@link Iterable} has to return new {@link Iterator} instances otherwise this will cause
   * an infinite loop.
   * 
   * @see #circular(Iterable)
   * @param iterable
   * @return
   */
  public static <E> Iterable<E> circular( final Iterable<E> iterable )
  {
    long limit = -1;
    return circular( iterable, limit );
  }
  
  /**
   * Returns a new {@link Iterable} instance for the given one which will return a circular {@link Iterator}. The {@link Iterator}
   * will stop additionally if the given limit of cycles is reached. If no limit should be used set the parameter to -1
   * 
   * @see #circular(Iterable)
   * @param iterable
   * @param limit
   * @return
   */
  public static <E> Iterable<E> circular( final Iterable<E> iterable, final long limit )
  {
    //
    Assert.isNotNull( iterable );
    
    //
    return new Iterable<E>()
    {
      
      @Override
      public Iterator<E> iterator()
      {
        //
        Factory<Iterator<E>> iteratorFactory = new Factory<Iterator<E>>()
        {
          /* ********************************************** Variables ********************************************** */
          private long counter = 0;
          
          /* ********************************************** Methods ********************************************** */
          
          @Override
          public Iterator<E> newInstance()
          {
            return limit < 0 || this.counter++ < limit ? iterable.iterator() : null;
          }
        };
        return IteratorUtils.factoryBasedIterator( iteratorFactory );
      }
    };
  }
  
  /**
   * Converts a given {@link Iterable} to a new {@link Iterable} type using the given {@link ElementConverter}
   * 
   * @param iterable
   * @param elementConverter
   * @return
   */
  public static <FROM, TO> Iterable<TO> convert( Iterable<FROM> iterable, ElementConverter<FROM, TO> elementConverter )
  {
    return ListUtils.convert( ListUtils.valueOf( iterable ), elementConverter );
  }
  
  /**
   * Counts all elements within an {@link Iterable} which are equal to the given element.
   * 
   * @param iterable
   * @param element
   * @return
   */
  public static <E> int countEquals( Iterable<E> iterable, E element )
  {
    //
    int retval = 0;
    
    //
    if ( iterable != null )
    {
      for ( E iElement : iterable )
      {
        if ( ObjectUtils.equals( element, iElement ) )
        {
          retval++;
        }
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the element of the given {@link Iterable} at the given index position. Does never throw an {@link Exception} instead
   * returns null if no element could be resolved.
   * 
   * @param iterable
   * @param indexPosition
   * @return
   */
  public static <E> E elementAt( final Iterable<E> iterable, final int indexPosition )
  {
    //
    E retval = null;
    
    //
    if ( iterable != null )
    {
      //
      final Iterator<E> iterator = iterable.iterator();
      if ( iterator != null )
      {
        for ( int ii = 0; ii <= indexPosition && iterator.hasNext(); ii++ )
        {
          //
          final E nextElement = iterator.next();
          if ( ii == indexPosition )
          {
            retval = nextElement;
          }
        }
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns an {@link Iterable} which returns always a new empty {@link Iterator}
   * 
   * @see IteratorUtils#empty()
   * @return
   */
  public static <E> Iterable<E> empty()
  {
    return new Iterable<E>()
    {
      @Override
      public Iterator<E> iterator()
      {
        return IteratorUtils.empty();
      }
    };
  }
  
  /**
   * Returns true if...<br>
   * <ul>
   * <li>iterable1 == iterable2</li>
   * <li>same number of elements of both iterables and each element pair is equal</li>
   * </ul>
   * 
   * @param iterable1
   * @param iterable2
   * @return
   */
  public static boolean equals( Iterable<?> iterable1, Iterable<?> iterable2 )
  {
    //
    boolean retval = false;
    
    //
    retval |= iterable1 == iterable2;
    if ( !retval && iterable1 != null && iterable2 != null )
    {
      //
      Iterator<?> iterator1 = iterable1.iterator();
      Iterator<?> iterator2 = iterable2.iterator();
      
      //
      if ( iterator1 != null && iterator2 != null )
      {
        //
        retval = true;
        while ( iterator1.hasNext() && iterator2.hasNext() )
        {
          retval &= ObjectUtils.equals( iterator1.next(), iterator2.next() );
          if ( !retval )
          {
            break;
          }
        }
        
        //
        retval &= !iterator1.hasNext() && !iterator2.hasNext();
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns an {@link Iterable} where the {@link Iterable#iterator()} instance returns only those elements where the respective
   * bit within the filter {@link BitSet} is set to true
   * 
   * @param iterable
   * @param filter
   * @return new {@link Iterable}
   */
  public static <E> Iterable<E> filtered( final Iterable<E> iterable, final BitSet filter )
  {
    return new Iterable<E>()
    {
      @Override
      public Iterator<E> iterator()
      {
        if ( iterable != null )
        {
          return IteratorUtils.filtered( iterable.iterator(), filter );
        }
        return IteratorUtils.empty();
      }
    };
  }
  
  /**
   * Returns the first element of the given {@link Iterable}
   * 
   * @param iterable
   * @return
   */
  public static <E> E firstElement( Iterable<E> iterable )
  {
    //
    final int indexPosition = 0;
    return elementAt( iterable, indexPosition );
  }
  
  /**
   * Generates a {@link Object#hashCode()} for a given {@link Iterable} and its elements. The hash code respects the hash codes of
   * the element instances and their order.
   * 
   * @param iterable
   * @return
   */
  public static int hashCode( Iterable<?> iterable )
  {
    final int prime = 31;
    int result = 1;
    if ( iterable != null )
    {
      for ( Object object : iterable )
      {
        result = prime * result + ( ( object == null ) ? 0 : object.hashCode() );
      }
    }
    return result;
  }
  
  /**
   * <pre>
   * isEmpty( null ) = true
   * isEmpty( Arrays.asList()) = true
   * isEmpty( Arrays.asList("") ) = false
   * </pre>
   * 
   * @param iterable
   * @return
   */
  public static boolean isEmpty( Iterable<?> iterable )
  {
    //
    boolean retval = true;
    
    //
    if ( iterable != null )
    {
      //
      Iterator<?> iterator = iterable.iterator();
      retval = iterator == null || !iterator.hasNext();
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the last element of the given {@link Iterable}
   * 
   * @param iterable
   * @return
   */
  public static <E> E lastElement( Iterable<E> iterable )
  {
    //
    E retval = null;
    
    //
    if ( iterable != null )
    {
      //
      final Iterator<E> iterator = iterable.iterator();
      if ( iterator != null )
      {
        while ( iterator.hasNext() )
        {
          retval = iterator.next();
        }
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Resolves the size of an {@link Iterable} by iterating over it and counting the elements.
   * 
   * @param iterable
   * @return
   */
  public static int size( Iterable<?> iterable )
  {
    //
    int retval = 0;
    
    //
    if ( iterable != null )
    {
      //
      final Iterator<?> iterator = iterable.iterator();
      retval = IteratorUtils.size( iterator );
    }
    
    //
    return retval;
  }
  
  /**
   * Returns an {@link Iterable} on an {@link ElementStream}
   * 
   * @param elementStream
   * @return
   */
  public static <E> Iterable<E> valueOf( final ElementStream<E> elementStream )
  {
    //
    final Iterator<E> iterator = IteratorUtils.adapter( elementStream );
    return valueOf( iterator );
  }
  
  /**
   * Returns a new {@link Iterable} based on a given {@link Factory} for {@link Iterator}s
   * 
   * @param iteratorFactory
   * @return
   */
  public static <E> Iterable<E> valueOf( final Factory<Iterator<E>> iteratorFactory )
  {
    return new Iterable<E>()
    {
      @Override
      public Iterator<E> iterator()
      {
        return iteratorFactory != null ? iteratorFactory.newInstance() : null;
      }
    };
  }
  
  /**
   * Returns a new instance of an {@link Iterable} which returns the given {@link Iterator} instance
   * 
   * @param iterator
   * @return
   */
  public static <E> Iterable<E> valueOf( final Iterator<E> iterator )
  {
    return new Iterable<E>()
    {
      @Override
      public Iterator<E> iterator()
      {
        return iterator;
      }
    };
  }
}

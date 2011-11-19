package org.omnaest.utils.structure.iterator;

import java.util.Iterator;

import org.apache.commons.lang3.ObjectUtils;

/**
 * Helper class related to {@link Iterable} instances
 * 
 * @author Omnaest
 */
public class IterableUtils
{
  
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
      Iterator<?> iterator = iterable.iterator();
      if ( iterator != null )
      {
        for ( ; iterator.hasNext(); iterator.next() )
        {
          retval++;
        }
      }
    }
    
    //
    return retval;
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
}

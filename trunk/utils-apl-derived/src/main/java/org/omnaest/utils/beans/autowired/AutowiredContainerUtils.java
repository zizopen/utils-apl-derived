package org.omnaest.utils.beans.autowired;

import java.util.Iterator;

import com.google.common.collect.Iterators;

/**
 * Helper related to {@link AutowiredContainer}
 * 
 * @author Omnaest
 */
public class AutowiredContainerUtils
{
  
  /**
   * Returns a {@link AutowiredContainerDecorator} which prohibits modifications to the underlying {@link AutowiredContainer}. If
   * a modification access is tried a {@link UnsupportedOperationException} is thrown therefore.<br>
   * The {@link Iterator} will allow traversing but no {@link Iterator#remove()}.<br>
   * <br>
   * If the give parameter is null, null is returned, too.
   * 
   * @param autowiredContainer
   * @return
   */
  public static <E> AutowiredContainer<E> unmodifiable( AutowiredContainer<E> autowiredContainer )
  {
    //    
    AutowiredContainer<E> retval = null;
    
    //
    if ( autowiredContainer != null )
    {
      retval = new AutowiredContainerDecorator<E>( autowiredContainer )
      {
        /* ********************************************** Constants ********************************************** */
        private static final long serialVersionUID = -6875582761205350886L;
        
        /* ********************************************** Methods ********************************************** */
        
        @Override
        public Iterator<E> iterator()
        {
          return Iterators.unmodifiableIterator( super.iterator() );
        }
        
        @Override
        public AutowiredContainer<E> put( E object )
        {
          throw new UnsupportedOperationException();
        }
        
        @Override
        public AutowiredContainer<E> putAll( Iterable<E> iterable )
        {
          throw new UnsupportedOperationException();
        }
        
        @Override
        public <O extends E> AutowiredContainer<E> put( O object, Class<? extends O>... types )
        {
          throw new UnsupportedOperationException();
        }
        
        @Override
        public <O extends E> AutowiredContainer<E> remove( O object )
        {
          throw new UnsupportedOperationException();
        }
        
        @Override
        public AutowiredContainer<E> remove( Class<? extends E> type )
        {
          throw new UnsupportedOperationException();
        }
        
      };
    }
    
    //
    return retval;
  }
  
  /**
   * Creates a new {@link AutowiredContainer} instance based on a given {@link Iterable}
   * 
   * @param iterable
   * @return
   */
  public static <E> AutowiredContainer<E> valueOf( Iterable<E> iterable )
  {
    return ClassMapToAutowiredContainerAdapter.<E> newInstanceUsingLinkedHashMap().putAll( iterable );
  }
}

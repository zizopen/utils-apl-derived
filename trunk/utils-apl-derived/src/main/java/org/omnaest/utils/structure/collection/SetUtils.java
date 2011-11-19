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
package org.omnaest.utils.structure.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class SetUtils
{
  /**
   * Merges all elements of the given {@link Collection} instances into one single {@link Set} instance.
   * 
   * @see #mergeAll(Collection)
   * @param <E>
   * @param collections
   * @return
   */
  public static <E> Set<E> mergeAll( Collection<E>... collections )
  {
    return SetUtils.mergeAll( Arrays.asList( collections ) );
  }
  
  /**
   * Merges all elements of the given {@link Collection} instances into one single {@link Set} instance .
   * 
   * @see #mergeAll(Collection...)
   * @param <E>
   * @param collectionOfCollections
   * @return
   */
  public static <E> Set<E> mergeAll( Collection<? extends Collection<E>> collectionOfCollections )
  {
    return new LinkedHashSet<E>( ListUtils.mergeAll( collectionOfCollections ) );
  }
  
  /**
   * Returns the intersection of the {@link Collection}s of the given container {@link Collection}
   * 
   * @param collectionOfCollections
   * @return
   */
  public static <E> Set<E> intersection( Collection<? extends Collection<E>> collectionOfCollections )
  {
    return new LinkedHashSet<E>( ListUtils.intersection( collectionOfCollections ) );
  }
  
  /**
   * Returns a new ordered {@link Set} instance with the element of the given {@link Iterable}
   * 
   * @param iterable
   * @return
   */
  public static <E> Set<E> valueOf( Iterable<E> iterable )
  {
    //    
    Set<E> retset = new LinkedHashSet<E>();
    
    //
    if ( iterable != null )
    {
      for ( E element : iterable )
      {
        retset.add( element );
      }
    }
    
    //
    return retset;
  }
}

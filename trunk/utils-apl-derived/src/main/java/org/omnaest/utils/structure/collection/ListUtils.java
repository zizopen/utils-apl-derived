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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Helper class for modifying {@link List} instances.
 * 
 * @author Omnaest
 */
public class ListUtils
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * The provides the transformation method to transform one generic element instance into another.
   * 
   * @see MultiElementTransformer
   * @see ListUtils#transform(Collection, ElementTransformer)
   */
  public static interface ElementTransformer<FROM, TO>
  {
    /**
     * Transforms a single element from one type into another.
     * 
     * @param element
     * @return converted element
     */
    public TO transformElement( FROM element );
  }
  
  /**
   * The provides the transformation method to transform one generic {@link Collection} instance into one or multiple elements of
   * other type. The resulting list will be merged to a ordered list by a transformation process, so the order will be kept.
   * 
   * @see ElementTransformer
   * @see ListUtils#transform(Collection, MultiElementTransformer, boolean)
   */
  public static interface MultiElementTransformer<FROM, TO>
  {
    /**
     * Transforms a single element from one type into an (ordered) list of the other types.
     * 
     * @param element
     * @return converted element
     */
    public Collection<TO> transformElement( FROM element );
  }
  
  /**
   * Simple {@link ElementTransformer} implementation which casts the given object and returns it.
   * 
   * @author Omnaest
   * @param <FROM>
   * @param <TO>
   */
  public static class ElementTransformerIdentitiyCast<FROM, TO> implements ElementTransformer<FROM, TO>
  {
    @SuppressWarnings("unchecked")
    @Override
    public TO transformElement( FROM element )
    {
      return (TO) element;
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Creates a {@link List} from a given {@link Iterator}
   * 
   * @param iterator
   * @return
   */
  public static <E> List<E> createListFrom( Iterator<E> iterator )
  {
    //    
    List<E> retlist = new ArrayList<E>();
    
    //
    if ( iterator != null )
    {
      while ( iterator.hasNext() )
      {
        retlist.add( iterator.next() );
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Transforms a given {@link Collection} instance from one generic type into the other using a given {@link ElementTransformer}.
   * 
   * @see #transform(Collection, ElementTransformer, boolean)
   * @param collection
   * @param elementTransformer
   */
  public static <FROM, TO> List<TO> transform( Collection<FROM> collection, ElementTransformer<FROM, TO> elementTransformer )
  {
    return ListUtils.transform( collection, elementTransformer, false );
  }
  
  /**
   * Transforms a given {@link Collection} instance from one generic type into the other using a given
   * {@link MultiElementTransformer}.
   * 
   * @see #transform(Collection, ElementTransformer, boolean)
   * @param collection
   * @param multiElementTransformer
   */
  public static <FROM, TO> List<TO> transform( Collection<FROM> collection,
                                               MultiElementTransformer<FROM, TO> multiElementTransformer )
  {
    return ListUtils.transform( collection, multiElementTransformer, false );
  }
  
  /**
   * Transforms a given {@link Collection} instance from one generic type into the other using a given {@link ElementTransformer}.
   * Every null value returned by the {@link ElementTransformer} will be discarded and not put into the result list.
   * 
   * @see #transform(Collection, ElementTransformer, boolean)
   * @param collection
   * @param elementTransformer
   */
  public static <FROM, TO> List<TO> transformListEliminatingNullValues( Collection<FROM> collection,
                                                                        ElementTransformer<FROM, TO> elementTransformer )
  {
    return ListUtils.transform( collection, elementTransformer, true );
  }
  
  /**
   * Transforms a given {@link Collection} instance from one generic type into the other using a given
   * {@link MultiElementTransformer}. Every null value returned by the {@link ElementTransformer} will be discarded and not put
   * into the result list.
   * 
   * @see #transform(Collection, ElementTransformer, boolean)
   * @param collection
   * @param multiElementTransformer
   */
  public static <FROM, TO> List<TO> transformListEliminatingNullValues( Collection<FROM> collection,
                                                                        MultiElementTransformer<FROM, TO> multiElementTransformer )
  {
    return ListUtils.transform( collection, multiElementTransformer, true );
  }
  
  /**
   * Transforms a given {@link Collection} instance from one generic type into the other using a given {@link ElementTransformer}.
   * 
   * @see #transform(Collection, ElementTransformer)
   * @param collection
   * @param elementTransformer
   * @param eliminateNullValues
   *          : true->all null results from the element transformer will be discarded and not inserted into the result list.
   */
  public static <FROM, TO> List<TO> transform( Collection<FROM> collection,
                                               final ElementTransformer<FROM, TO> elementTransformer,
                                               boolean eliminateNullValues )
  {
    //
    List<TO> retlist = new ArrayList<TO>();
    if ( elementTransformer != null )
    {
      MultiElementTransformer<FROM, TO> multiElementTransformer = new MultiElementTransformer<FROM, TO>()
      {
        @SuppressWarnings("unchecked")
        @Override
        public Collection<TO> transformElement( FROM element )
        {
          return Arrays.asList( elementTransformer.transformElement( element ) );
        }
      };
      retlist = ListUtils.transform( collection, multiElementTransformer, eliminateNullValues );
    }
    
    //
    return retlist;
  }
  
  /**
   * Transforms a given {@link Collection} instance from one generic type into the other using a given {@link ElementTransformer}.
   * 
   * @see #transform(Collection, ElementTransformer)
   * @param collection
   * @param multiElementTransformer
   * @param eliminateNullValues
   *          : true->all null results from the element transformer will be discarded and not inserted into the result list.
   * @return always new (ordered) list instance containing transformed elements
   */
  public static <FROM, TO> List<TO> transform( Collection<FROM> collection,
                                               MultiElementTransformer<FROM, TO> multiElementTransformer,
                                               boolean eliminateNullValues )
  {
    //
    List<TO> retlist = new ArrayList<TO>();
    
    //
    if ( collection != null && multiElementTransformer != null )
    {
      for ( FROM element : collection )
      {
        //
        try
        {
          //
          Collection<TO> transformedElementCollection = multiElementTransformer.transformElement( element );
          
          //
          if ( transformedElementCollection != null )
          {
            for ( TO transformedElement : transformedElementCollection )
            {
              if ( !eliminateNullValues || transformedElement != null )
              {
                retlist.add( transformedElement );
              }
            }
          }
        }
        catch ( Exception e )
        {
          e.printStackTrace();
        }
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Merges all elements of the given {@link Collection} instances into one single {@link List} instance which keeps the order of
   * the elements.
   * 
   * @see #mergeAll(Collection)
   * @param <E>
   * @param collections
   * @return
   */
  public static <E> List<E> mergeAll( Collection<E>... collections )
  {
    return ListUtils.mergeAll( Arrays.asList( collections ) );
  }
  
  /**
   * Merges all elements of the given {@link Collection} instances into one single {@link List} instance which keeps the order of
   * the elements.
   * 
   * @see #mergeAll(Collection...)
   * @param <E>
   * @param collections
   * @return
   */
  public static <E> List<E> mergeAll( Collection<? extends Collection<E>> collections )
  {
    //
    List<E> retlist = new ArrayList<E>();
    
    //
    if ( collections != null )
    {
      for ( Collection<E> list : collections )
      {
        retlist.addAll( list );
      }
    }
    
    //
    return retlist;
  }
  
}

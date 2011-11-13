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
package org.omnaest.utils.operation.foreach;

import java.util.ArrayList;
import java.util.List;

import org.omnaest.utils.operation.Operation;
import org.omnaest.utils.operation.foreach.ForEach.Result;
import org.omnaest.utils.structure.collection.CollectionUtils.CollectionConverter;
import org.omnaest.utils.structure.collection.ListUtils;
import org.omnaest.utils.structure.collection.list.ListDecorator;

/**
 * A {@link ForEach} will iterate over a given {@link Iterable} instance and executes a given {@link List} of {@link Operation}s.
 * 
 * @see Operation
 * @see Iterable
 * @author Omnaest
 * @param <E>
 *          elements
 * @param <V>
 *          result values
 */
public class ForEach<E, V> implements Operation<Result<V>, Operation<V, E>>
{
  /* ********************************************** Variables ********************************************** */
  protected final Iterable<E>[] iterables;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * {@link Result} of a {@link ForEach} {@link Operation} which is basically a {@link List} of all returned instances from the
   * {@link Operation}s.<br>
   * <br>
   * Additionally there a some special methods.<br>
   * E.g. the {@link #areAllValuesEqualTo(Object)} allows to test for equality of the whole result objects:<br>
   * 
   * <pre>
   * boolean result = new ForEach&lt;String, Boolean&gt;( iterables ).execute( operation ).areAllValuesEqualTo( true );
   * </pre>
   * 
   * @see #convert(CollectionConverter)
   * @see #areAllValuesEqualTo(Object)
   * @author Omnaest
   * @param <V>
   */
  public static class Result<V> extends ListDecorator<V>
  {
    /* ********************************************** Constants ********************************************** */
    private static final long serialVersionUID = -3838376068713161966L;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see Result
     * @param list
     */
    protected Result( List<V> list )
    {
      super( list );
    }
    
    /**
     * Converts the {@link Result} to a single value using a {@link CollectionConverter}
     * 
     * @param collectionConverter
     * @return
     */
    public <TO> TO convert( CollectionConverter<V, TO> collectionConverter )
    {
      return ListUtils.convert( this.list, collectionConverter );
    }
    
    /**
     * Returns true if all values of the {@link Result} are equal to the given value
     * 
     * @param value
     * @return
     */
    public boolean areAllValuesEqualTo( V value )
    {
      //
      boolean retval = value != null;
      
      //
      if ( retval )
      {
        for ( V iValue : this )
        {
          retval &= value.equals( iValue );
          if ( !retval )
          {
            break;
          }
        }
      }
      
      //
      return retval;
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see ForEach
   * @param iterables
   */
  public ForEach( Iterable<E>... iterables )
  {
    super();
    this.iterables = iterables;
  }
  
  /**
   * Executes the given {@link Operation} and uses the given {@link CollectionConverter} to produce a single result value
   * 
   * @param collectionConverter
   * @param operations
   * @return
   */
  public <O> V execute( CollectionConverter<O, V> collectionConverter, Operation<O, E>... operations )
  {
    return new ForEach<E, O>( this.iterables ).execute( operations ).convert( collectionConverter );
  }
  
  /**
   * Executes the {@link ForEach} {@link Operation}
   * 
   * @param operation
   * @return
   */
  @SuppressWarnings("unchecked")
  @Override
  public Result<V> execute( Operation<V, E> operation )
  {
    return this.execute( new Operation[] { operation } );
  }
  
  /**
   * Executes the {@link ForEach} {@link Operation}
   * 
   * @param operations
   * @return
   */
  public Result<V> execute( Operation<V, E>... operations )
  {
    //
    List<V> retlist = new ArrayList<V>();
    
    //
    for ( Iterable<E> iterable : this.iterables )
    {
      if ( iterable != null )
      {
        for ( E element : iterable )
        {
          for ( Operation<V, E> operation : operations )
          {
            if ( operation != null )
            {
              retlist.add( operation.execute( element ) );
            }
          }
        }
      }
    }
    
    //
    return new Result<V>( retlist );
  }
  
}
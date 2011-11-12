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
package org.omnaest.utils.operation;

import java.util.ArrayList;
import java.util.List;

import org.omnaest.utils.operation.special.OperationBooleanResult;
import org.omnaest.utils.structure.collection.CollectionUtils.CollectionConverter;
import org.omnaest.utils.structure.collection.ListUtils;
import org.omnaest.utils.structure.collection.list.ListDecorator;

/**
 * A {@link ForEach} will iterate over a given {@link Iterable} instance and executes a given {@link List} of {@link Operation}s.
 * 
 * @see Operation
 * @see Iterable
 * @author Omnaest
 */
public class ForEach<E>
{
  /* ********************************************** Variables ********************************************** */
  private final Iterable<E>[] iterables;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * {@link Result} of a {@link ForEach} {@link Operation}
   * 
   * @see #convert(CollectionConverter)
   * @author Omnaest
   * @param <O>
   */
  public class Result<O> extends ListDecorator<O>
  {
    /* ********************************************** Constants ********************************************** */
    private static final long serialVersionUID = -3838376068713161966L;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see Result
     * @param list
     */
    protected Result( List<O> list )
    {
      super( list );
    }
    
    /**
     * Converts the {@link Result} to a single value using a {@link CollectionConverter}
     * 
     * @param collectionConverter
     * @return
     */
    public <TO> TO convert( CollectionConverter<O, TO> collectionConverter )
    {
      return ListUtils.convert( this.list, collectionConverter );
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
  public <R, O> R execute( CollectionConverter<O, R> collectionConverter, Operation<O, E>... operations )
  {
    return this.execute( operations ).convert( collectionConverter );
  }
  
  /**
   * Executes the given {@link OperationBooleanResult} instance on the {@link Iterable}s and returns a {@link Boolean} result if
   * all {@link Operation}s will return true and nothing else.
   * 
   * @param operation
   * @return
   */
  @SuppressWarnings("unchecked")
  public boolean execute( OperationBooleanResult<E> operation )
  {
    return this.execute( new OperationBooleanResult[] { operation } );
  }
  
  /**
   * Executes the given {@link OperationBooleanResult} instances on the {@link Iterable}s and returns a {@link Boolean} result if
   * all {@link Operation}s will return true and nothing else.
   * 
   * @param operation
   * @return
   */
  public boolean execute( OperationBooleanResult<E>... operations )
  {
    CollectionConverter<Boolean, Boolean> collectionConverter = new CollectionConverter<Boolean, Boolean>()
    {
      private boolean retval = true;
      
      @Override
      public Boolean result()
      {
        return this.retval;
      }
      
      @Override
      public void process( Boolean element )
      {
        this.retval &= element != null && element;
      }
    };
    return this.execute( collectionConverter, operations );
  }
  
  /**
   * Executes the {@link ForEach} {@link Operation}
   * 
   * @param operation
   * @return
   */
  @SuppressWarnings("unchecked")
  public <O> Result<O> execute( Operation<O, E> operation )
  {
    return this.execute( new Operation[] { operation } );
  }
  
  /**
   * Executes the {@link ForEach} {@link Operation}
   * 
   * @param operations
   * @return
   */
  public <O> Result<O> execute( Operation<O, E>... operations )
  {
    //
    List<O> retlist = new ArrayList<O>();
    
    //
    for ( Iterable<E> iterable : this.iterables )
    {
      if ( iterable != null )
      {
        for ( E element : iterable )
        {
          for ( Operation<O, E> operation : operations )
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
    return new Result<O>( retlist );
  }
  
}

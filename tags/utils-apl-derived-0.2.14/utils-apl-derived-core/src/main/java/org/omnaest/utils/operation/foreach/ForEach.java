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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.collections.IteratorUtils;
import org.omnaest.utils.operation.Operation;

/**
 * Wrapper of any {@link Iterable} which allows parallel processing using the well known {@link #map(Operation)} and
 * {@link IterationResult#reduce(Operation)} paradigm.
 * 
 * @author Omnaest
 * @param <E>
 */
public class ForEach<E>
{
  protected Iterable<E> iterable;
  
  /**
   * Result of an iteration which provides an {@link #iterator()} over the result element, as well as to
   * {@link #reduce(Operation)} them to a single reduction result
   * 
   * @author Omnaest
   * @param <R>
   */
  public static class IterationResult<R> implements Iterable<R>
  {
    private List<R> elementList;
    
    public IterationResult( List<R> elementList )
    {
      super();
      this.elementList = elementList;
    }
    
    /**
     * Reduces multiple result elements into a single result
     * 
     * @param operation
     * @return
     */
    public <RR> RR reduce( Operation<RR, Collection<R>> operation )
    {
      RR retval = operation != null ? operation.execute( this.elementList ) : null;
      return retval;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Iterator<R> iterator()
    {
      return IteratorUtils.unmodifiableIterator( this.elementList.iterator() );
    }
  }
  
  /**
   * @see ForEach
   * @param elementIterable
   */
  public ForEach( Iterable<E> elementIterable )
  {
    this.iterable = elementIterable;
  }
  
  /**
   * @see ForEach
   * @param elements
   */
  public ForEach( E... elements )
  {
    this.iterable = Arrays.asList( elements );
  }
  
  /**
   * Maps any element to a result. This is done using an fixed threadpool with as many threads as
   * {@link Runtime#availableProcessors()} is set.
   * 
   * @param operation
   *          {@link Operation}
   * @return {@link IterationResult}
   * @throws ExecutionException
   */
  public <R> IterationResult<R> map( Operation<R, E> operation ) throws ExecutionException
  {
    final ExecutorService executorService = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );
    return this.map( operation, executorService );
  }
  
  /**
   * @see #map(Operation)
   * @param operation
   * @param executorService
   * @return
   * @throws ExecutionException
   */
  public <R> IterationResult<R> map( final Operation<R, E> operation, ExecutorService executorService ) throws ExecutionException
  {
    IterationResult<R> retval = null;
    
    List<Callable<R>> callableList = new ArrayList<Callable<R>>();
    for ( final E element : this.iterable )
    {
      callableList.add( new Callable<R>()
      {
        @Override
        public R call() throws Exception
        {
          return operation.execute( element );
        }
      } );
    }
    
    List<R> resultList = new ArrayList<R>();
    {
      try
      {
        List<Future<R>> futureList = executorService.invokeAll( callableList );
        for ( Future<R> future : futureList )
        {
          do
          {
            try
            {
              resultList.add( future.get() );
            }
            catch ( InterruptedException e )
            {
            }
          } while ( !future.isDone() );
        }
      }
      catch ( InterruptedException e )
      {
      }
    }
    retval = new IterationResult<R>( resultList );
    
    return retval;
  }
  
}

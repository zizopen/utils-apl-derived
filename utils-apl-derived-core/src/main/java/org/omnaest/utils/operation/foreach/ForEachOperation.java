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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

import org.omnaest.utils.operation.Operation;
import org.omnaest.utils.operation.foreach.ForEachOperation.Result;
import org.omnaest.utils.strings.StringReplacer.ReplacementResult;
import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.collection.CollectionUtils.CollectionConverter;
import org.omnaest.utils.structure.collection.list.decorator.ListDecorator;
import org.omnaest.utils.threads.FutureTaskManager;

/**
 * A {@link ForEachOperation} will iterate over a given {@link Iterable} instance and executes a given {@link List} of {@link Operation}s.
 * 
 * @see Operation
 * @see Iterable
 * @author Omnaest
 * @param <E>
 *          elements
 * @param <V>
 *          result values
 */
public class ForEachOperation<E, V> implements Operation<Result<V>, Operation<V, E>>
{
  /* ********************************************** Variables ********************************************** */
  private final Iterable<E>[] iterables;
  
  /* ********************************************** Beans / Services / References ********************************************** */
  private ExecutorService     executorServiceForParallelExecution = null;
  private ExecutorService     executorServiceForParallelIteration = null;
  private int                 numberOfThreadsForParallelIteration = 1;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * {@link ReplacementResult} of a {@link ForEachOperation} {@link Operation} which is basically an unmodifiable {@link List} of all
   * returned instances from the {@link Operation}s.<br>
   * <br>
   * Additionally there a some special methods.<br>
   * E.g. the {@link #areAllValuesEqualTo(Object)} allows to test for equality of the whole result objects:<br>
   * 
   * <pre>
   * boolean result = new ForEachOperation&lt;String, Boolean&gt;( iterables ).execute( operation ).areAllValuesEqualTo( true );
   * </pre>
   * 
   * @see #convert(CollectionConverter)
   * @see #areAllValuesEqualTo(Object)
   * @see #areNumberOfValuesEqualTo(Object, int)
   * @see #isAnyValueEqualTo(Object)
   * @author Omnaest
   * @param <V>
   */
  public static class Result<V> extends ListDecorator<V>
  {
    /* ********************************************** Constants ********************************************** */
    private static final long serialVersionUID = -3838376068713161966L;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see ReplacementResult
     * @param list
     */
    protected Result( List<V> list )
    {
      super( list );
    }
    
    /**
     * Converts the {@link ReplacementResult} to a single value using a {@link CollectionConverter}
     * 
     * @param collectionConverter
     * @return
     */
    public <TO> TO convert( CollectionConverter<V, TO> collectionConverter )
    {
      return CollectionUtils.convert( this.list, collectionConverter );
    }
    
    /**
     * Returns true if all values of the {@link ReplacementResult} are equal to the given value
     * 
     * @param value
     * @return
     */
    public boolean areAllValuesEqualTo( V value )
    {
      return this.areNumberOfValuesEqualTo( value, this.size() );
    }
    
    /**
     * Returns true if any value of the result is equal to the given value
     * 
     * @param value
     * @return
     */
    public boolean isAnyValueEqualTo( V value )
    {
      return this.areNumberOfValuesEqualTo( value, 1 );
    }
    
    /**
     * Returns true if a given number of values of the result are equal to the given value
     * 
     * @param value
     * @param number
     * @return
     */
    public boolean areNumberOfValuesEqualTo( V value, int number )
    {
      //
      boolean retval = false;
      
      //
      int counter = 0;
      if ( value != null )
      {
        for ( V iValue : this )
        {
          counter += value.equals( iValue ) ? 1 : 0;
          if ( counter >= number )
          {
            retval = true;
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
   * @see ForEachOperation
   * @param iterables
   */
  public ForEachOperation( Iterable<E>... iterables )
  {
    super();
    this.iterables = iterables;
  }
  
  /**
   * @see ForEachOperation
   * @param iterable
   */
  @SuppressWarnings("unchecked")
  public ForEachOperation( Iterable<E> iterable )
  {
    this( new Iterable[] { iterable } );
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
    return new ForEachOperation<E, O>( this.iterables ).execute( operations ).convert( collectionConverter );
  }
  
  /**
   * Executes the {@link ForEachOperation} {@link Operation}
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
   * Executes the {@link ForEachOperation} {@link Operation}
   * 
   * @param operations
   * @return
   */
  public Result<V> execute( Operation<V, E>... operations )
  {
    //
    Result<V> retval = null;
    
    //
    if ( this.executorServiceForParallelExecution != null || this.executorServiceForParallelIteration != null )
    {
      retval = this.executeMultiThreaded( operations );
    }
    else
    {
      retval = this.executeSingleThreaded( operations );
    }
    
    //
    return retval;
  }
  
  /**
   * @see #execute(Operation...)
   * @param operations
   * @return
   */
  @SuppressWarnings("unchecked")
  private Result<V> executeSingleThreaded( Operation<V, E>... operations )
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
    return new Result<V>( org.apache.commons.collections.ListUtils.unmodifiableList( retlist ) );
  }
  
  /**
   * @see #execute(Operation...)
   * @param operations
   * @return
   */
  @SuppressWarnings("unchecked")
  private Result<V> executeMultiThreaded( final Operation<V, E>... operations )
  {
    //
    final List<V> retlist = new CopyOnWriteArrayList<V>();
    
    //
    final FutureTaskManager futureTaskManagerExecution = new FutureTaskManager( this.executorServiceForParallelExecution );
    final FutureTaskManager futureTaskManagerIteration = new FutureTaskManager( this.executorServiceForParallelIteration );
    
    //    
    for ( Iterable<E> iterable : this.iterables )
    {
      if ( iterable != null )
      {
        //
        final Iterator<E> iterator = iterable.iterator();
        final Runnable runnableForIteration = new Runnable()
        {
          @Override
          public void run()
          {
            //
            try
            {
              while ( true )
              {
                //
                final E element = iterator.next();
                
                //
                for ( final Operation<V, E> operation : operations )
                {
                  if ( operation != null )
                  {
                    if ( futureTaskManagerExecution.hasExecutorService() )
                    {
                      futureTaskManagerExecution.submitAndManage( new Callable<V>()
                      {
                        @Override
                        public V call() throws Exception
                        {
                          return operation.execute( element );
                        }
                      } );
                    }
                    else
                    {
                      retlist.add( operation.execute( element ) );
                    }
                  }
                }
              }
              
            }
            catch ( NoSuchElementException e )
            {
            }
            
            //
            if ( futureTaskManagerExecution.hasExecutorService() )
            {
              retlist.addAll( (List<V>) futureTaskManagerExecution.waitForAllTasksToFinish().getResult() );
            }
          }
        };
        
        //
        if ( futureTaskManagerIteration.hasExecutorService() )
        {
          //
          for ( int ii = 1; ii <= this.numberOfThreadsForParallelIteration; ii++ )
          {
            futureTaskManagerIteration.submitAndManage( runnableForIteration );
          }
          
          //
          futureTaskManagerIteration.waitForAllTasksToFinish();
        }
        else
        {
          runnableForIteration.run();
        }
      }
    }
    
    //
    return new Result<V>( org.apache.commons.collections.ListUtils.unmodifiableList( retlist ) );
  }
  
  /**
   * @param executorService
   *          the executorService to set
   * @return this
   */
  public ForEachOperation<E, V> doExecuteInParallelUsing( ExecutorService executorService )
  {
    this.executorServiceForParallelExecution = executorService;
    return this;
  }
  
  /**
   * If this option is used, the given {@link Iterable}s have to have threadsafe {@link Iterable#iterator()} instances, since even
   * the {@link Iterator}s will be called from parallel running threads.<br>
   * <br>
   * Be aware that several default implementations like {@link ArrayList} do not provide thread safe iterators, so do only use
   * this option, if you have special {@link Iterator}s in place.
   * 
   * @param executorService
   *          the executorService to set
   * @param numberOfThreads
   * @return this
   */
  public ForEachOperation<E, V> doIterateInParallelUsing( ExecutorService executorService, int numberOfThreads )
  {
    this.executorServiceForParallelIteration = executorService;
    this.numberOfThreadsForParallelIteration = numberOfThreads;
    return this;
  }
  
}

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
package org.omnaest.utils.threads.submit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.element.ObjectUtils;

/**
 * @see SubmitGroup
 * @author Omnaest
 * @param <T>
 */
class SubmitGroupImpl<T> implements SubmitGroup<T>
{
  /* ************************************************** Constants *************************************************** */
  private static final long      serialVersionUID = 1369165651238494278L;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private List<Future<T>>        futureList       = new ArrayList<Future<T>>();
  
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  private final ExecutorService  executorService;
  private final ExceptionHandler exceptionHandler;
  private final Collection<T>    resultCollection;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see SubmitGroupImpl
   * @param executorService
   * @param exceptionHandler
   */
  SubmitGroupImpl( ExecutorService executorService, ExceptionHandler exceptionHandler )
  {
    this.executorService = executorService;
    this.exceptionHandler = exceptionHandler;
    this.resultCollection = new ArrayList<T>();
  }
  
  SubmitGroupImpl( ExecutorService executorService, ExceptionHandler exceptionHandler, Collection<T> resultCollection )
  {
    this.executorService = executorService;
    this.exceptionHandler = exceptionHandler;
    this.resultCollection = ObjectUtils.defaultIfNull( resultCollection, new ArrayList<T>() );
  }
  
  @Override
  public SubmitGroup<T> submit( Callable<T> callable )
  {
    final Future<T> future = this.executorService.submit( callable );
    this.futureList.add( future );
    return this;
  }
  
  @Override
  public SubmitGroup<T> submit( Callable<T> callable, int numberOfTimes )
  {
    for ( int ii = 0; ii < numberOfTimes; ii++ )
    {
      this.submit( callable );
    }
    return this;
  }
  
  @Override
  public Waiter<T> doWait()
  {
    final ExceptionHandler exceptionHandler = this.exceptionHandler;
    final List<Future<T>> futureList = this.futureList;
    final Collection<T> resultCollection = this.resultCollection;
    return new Waiter<T>()
    {
      private static final long serialVersionUID = -7431148600723570701L;
      
      private final Reducer<T>  reducer          = new ReducerImpl<T>( resultCollection );
      
      @Override
      public Reducer<T> untilAllTasksAreDone()
      {
        resultCollection.clear();
        for ( Future<T> future : futureList )
        {
          this.tryResolveValue( exceptionHandler, future, resultCollection );
        }
        return this.reducer;
      }
      
      @Override
      public Reducer<T> anAmountOfTime( int amount, TimeUnit timeUnit )
      {
        try
        {
          Thread.sleep( TimeUnit.MILLISECONDS.convert( amount, timeUnit ) );
        }
        catch ( InterruptedException e )
        {
          exceptionHandler.handleException( e );
        }
        return this.reducer;
      }
      
      @Override
      public Reducer<T> untilThePercentageOfTasksAreDone( double ratio )
      {
        final int resultSizeMax = futureList.size();
        resultCollection.clear();
        for ( Future<T> future : futureList )
        {
          //
          this.tryResolveValue( exceptionHandler, future, resultCollection );
          
          //
          final int resultSize = resultCollection.size();
          double currentRatio = resultSize * 1.0 / resultSizeMax;
          if ( currentRatio >= ratio )
          {
            break;
          }
        }
        return this.reducer;
      }
      
      @Override
      public Reducer<T> untilTheNumberOfTasksAreDone( int numberOfTasks )
      {
        resultCollection.clear();
        for ( Future<T> future : futureList )
        {
          //
          this.tryResolveValue( exceptionHandler, future, resultCollection );
          
          //
          final int resultSize = resultCollection.size();
          if ( resultSize >= numberOfTasks )
          {
            break;
          }
        }
        return this.reducer;
      }
      
      private void tryResolveValue( ExceptionHandler exceptionHandler, Future<T> future, Collection<T> resultCollection )
      {
        try
        {
          for ( boolean done = false; !done; )
          {
            try
            {
              final T result = future.get();
              resultCollection.add( result );
              done = true;
            }
            catch ( InterruptedException e )
            {
              exceptionHandler.handleException( e );
            }
          }
        }
        catch ( Exception e )
        {
          exceptionHandler.handleException( e );
        }
      }
      
    };
  }
}

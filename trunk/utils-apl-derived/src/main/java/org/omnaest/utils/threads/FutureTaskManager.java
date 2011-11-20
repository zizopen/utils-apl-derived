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
package org.omnaest.utils.threads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.ExceptionHandledResult;
import org.omnaest.utils.structure.element.converter.ElementConverter;

/**
 * A {@link FutureTaskManager} will manage the {@link Future}s created e.g. by a
 * {@link ExecutorService#submit(java.util.concurrent.Callable)} call and allows to wait on all managed {@link Future}s.<br>
 * <br>
 * If an {@link ExecutorService} instance is declared using {@link #FutureTaskManager(ExecutorService)}, it is possible to let the
 * {@link FutureTaskManager} submit tasks by invoking {@link #submitAndManage(Callable)} or {@link #submitAndManage(Runnable)}.
 * 
 * @author Omnaest
 */
public class FutureTaskManager
{
  /* ********************************************** Variables ********************************************** */
  protected final List<Future<?>> futureList = new ArrayList<Future<?>>();
  protected final ExecutorService executorService;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see FutureTaskManager
   * @param executorService
   */
  public FutureTaskManager( ExecutorService executorService )
  {
    super();
    this.executorService = executorService;
  }
  
  /**
   * Does not support {@link #submitAndManage(Callable)} and {@link #submitAndManage(Runnable)}. If these methods should be used
   * provide an {@link ExecutorService} instance using {@link #FutureTaskManager(ExecutorService)}
   * 
   * @see FutureTaskManager
   */
  public FutureTaskManager()
  {
    super();
    this.executorService = null;
  }
  
  /**
   * Manages the given {@link Future}
   * 
   * @param future
   */
  public void manageFutureTask( Future<?> future )
  {
    if ( future != null )
    {
      this.futureList.add( future );
    }
  }
  
  /**
   * Manages the given {@link Future} instances
   * 
   * @param futureIterable
   */
  public void manageFutureTask( Iterable<Future<?>> futureIterable )
  {
    if ( futureIterable != null )
    {
      for ( Future<?> future : futureIterable )
      {
        this.manageFutureTask( future );
      }
    }
  }
  
  /**
   * Submits the given {@link Callable} to the given {@link ExecutorService} instance and calls {@link #manageFutureTask(Future)}
   * for the resulting {@link FutureTask}
   * 
   * @param executorService
   * @param callable
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void submitAndManage( ExecutorService executorService, Callable<?> callable )
  {
    this.submitAndManageCallables( executorService, (Collection) Arrays.asList( callable ), 1 );
  }
  
  /**
   * Submits the given {@link Runnable} to the given {@link ExecutorService} instance and calls {@link #manageFutureTask(Future)}
   * for the resulting {@link FutureTask}
   * 
   * @param executorService
   * @param callable
   */
  @SuppressWarnings({ "unchecked", "rawtypes", "cast" })
  public void submitAndManage( ExecutorService executorService, Runnable runnable )
  {
    this.submitAndManageRunnables( executorService, (Collection) Arrays.asList( runnable ), 1 );
  }
  
  /**
   * Submits the given {@link Callable} to the internal {@link ExecutorService}. Throws an {@link UnsupportedOperationException}
   * if not {@link ExecutorService} instance is available.
   * 
   * @see Callable
   * @param callable
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void submitAndManage( Callable<?> callable )
  {
    if ( this.executorService == null )
    {
      throw new UnsupportedOperationException(
                                               "The "
                                                   + FutureTaskManager.class
                                                   + " must be initialied with an instance of an ExecutorService to support this operation" );
    }
    this.submitAndManageCallables( this.executorService, (Collection) Arrays.asList( callable ), 1 );
  }
  
  /**
   * @see Runnable
   * @see #submitAndManage(Callable)
   * @param runnable
   */
  public void submitAndManage( Runnable runnable )
  {
    //
    if ( runnable != null )
    {
      this.submitAndManage( new RunnableToCallableAdapter( runnable ) );
    }
  }
  
  /**
   * @see #submitAndManage(Callable)
   * @see #submitAndManageRunnables(ExecutorService, Collection, int)
   * @param executorService
   * @param runnable
   * @param submitCount
   */
  public void submitAndManage( ExecutorService executorService, Runnable runnable, int submitCount )
  {
    this.submitAndManage( executorService, new RunnableToCallableAdapter( runnable ), submitCount );
  }
  
  /**
   * @see #submitAndManage(ExecutorService, Callable)
   * @see #submitAndManageCallables(ExecutorService, Collection, int)
   * @param executorService
   * @param callable
   * @param submitCount
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void submitAndManage( ExecutorService executorService, Callable<?> callable, int submitCount )
  {
    //
    if ( callable != null )
    {
      this.submitAndManageCallables( executorService, (Collection) Arrays.asList( callable ), submitCount );
    }
  }
  
  /**
   * @see #submitAndManageCallables(ExecutorService, Collection, int)
   * @param executorService
   * @param runnableCollection
   * @param submitCount
   */
  public void submitAndManageRunnables( ExecutorService executorService, Collection<Runnable> runnableCollection, int submitCount )
  {
    //
    final ElementConverter<Runnable, Callable<?>> elementConverter = new ElementConverter<Runnable, Callable<?>>()
    {
      @Override
      public Callable<?> convert( Runnable runnable )
      {
        return new RunnableToCallableAdapter( runnable );
      }
    };
    final Collection<Callable<?>> callableCollection = ListUtils.convert( runnableCollection, elementConverter );
    this.submitAndManageCallables( executorService, callableCollection, submitCount );
  }
  
  /**
   * @see #submitAndManage(ExecutorService, Callable)
   * @see #submitAndManage(Callable)
   * @param callableTaskSubmitter
   * @param callableCollection
   * @param submitCount
   */
  public void submitAndManageCallables( ExecutorService executorService,
                                        Collection<Callable<?>> callableCollection,
                                        int submitCount )
  {
    //
    if ( executorService != null && callableCollection != null )
    {
      for ( Callable<?> callable : callableCollection )
      {
        for ( int ii = 1; ii <= submitCount; ii++ )
        {
          //
          Future<?> future = executorService.submit( callable );
          
          //
          if ( future != null )
          {
            this.manageFutureTask( future );
          }
        }
      }
    }
  }
  
  /**
   * Uses the {@link Future#get()} to wait on all managed {@link Future}s until they are finished
   * 
   * @return {@link ExceptionHandledResult}
   */
  public ExceptionHandledResult<List<Object>> waitForAllTasksToFinish()
  {
    //
    Collection<Exception> exceptionCollection = new ArrayList<Exception>();
    List<Object> result = new ArrayList<Object>();
    
    //
    for ( Future<?> future : this.futureList )
    {
      //
      ExceptionHandledResult<?> exceptionHandledResult = waitForTaskToFinish( future );
      
      //
      result.add( exceptionHandledResult.getResult() );
      exceptionCollection.addAll( exceptionHandledResult.getExceptionList() );
    }
    
    //
    return new ExceptionHandledResult<List<Object>>( result, exceptionCollection );
  }
  
  /**
   * Waits for a given {@link Future} to finish. Returns an {@link ExceptionHandledResult} which does exclude any
   * {@link InterruptedException}
   * 
   * @param future
   * @return {@link ExceptionHandledResult}
   */
  public static <V> ExceptionHandledResult<V> waitForTaskToFinish( Future<V> future )
  {
    //
    Collection<Exception> exceptionCollection = new ArrayList<Exception>();
    V result = null;
    
    //
    if ( future != null )
    {
      try
      {
        //
        boolean waitForTask = true;
        while ( waitForTask )
        {
          //
          waitForTask = false;
          
          //
          try
          {
            result = future.get();
          }
          catch ( InterruptedException e )
          {
            waitForTask = true;
          }
        }
      }
      catch ( Exception e )
      {
        exceptionCollection.add( e );
      }
    }
    
    //
    return new ExceptionHandledResult<V>( result, exceptionCollection );
  }
  
  /**
   * Clears all {@link Future}s which are {@link Future#isDone()}
   */
  public void clearFinishedTasks()
  {
    //
    Set<Future<?>> removeFutureSet = new HashSet<Future<?>>();
    for ( Future<?> future : this.futureList )
    {
      if ( future == null || future.isDone() )
      {
        removeFutureSet.add( future );
      }
    }
    
    //
    this.futureList.removeAll( removeFutureSet );
  }
  
  /**
   * Returns the {@link List} of manage {@link Future}s
   * 
   * @return
   */
  public List<Future<?>> getFutureList()
  {
    return this.futureList;
  }
  
}

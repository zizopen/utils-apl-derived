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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.omnaest.utils.structure.element.ExceptionHandledResult;

/**
 * A {@link FutureTaskManager} will manage the {@link Future}s created e.g. by a
 * {@link ExecutorService#submit(java.util.concurrent.Callable)} call and allows to wait on all {@link Future}s.
 * 
 * @author Omnaest
 */
public class FutureTaskManager
{
  /* ********************************************** Variables ********************************************** */
  private List<Future<?>> futureList = new ArrayList<Future<?>>();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  public static interface CallableTaskSubmitter
  {
    /**
     * Submits a given {@link Callable} and returns the {@link Future} which provides information about the state of the
     * {@link Thread}s task.
     * 
     * @param callable
     * @return
     */
    public Future<?> submitTask( Callable<?> callable );
    
  }
  
  public static interface RunnableTaskSubmitter
  {
    /**
     * Submits a given {@link Runnable} and returns the {@link Future} which provides information about the state of the
     * {@link Thread}s task.
     * 
     * @param runnable
     * @return
     */
    public Future<?> submitTask( Runnable runnable );
  }
  
  /* ********************************************** Methods ********************************************** */
  
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
   * Manages the given {@link Future}
   * 
   * @param future
   */
  public void manageFutureTask( List<Future<?>> futureList )
  {
    if ( futureList != null )
    {
      for ( Future<?> future : futureList )
      {
        this.manageFutureTask( future );
      }
    }
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void submitAndManage( CallableTaskSubmitter callableTaskSubmitter, Callable<?> callable )
  {
    this.submitAndManage( callableTaskSubmitter, (Collection) Arrays.asList( callable ), 1 );
  }
  
  /**
   * @param callableTaskSubmitter
   * @param callableCollection
   * @param submitCount
   */
  public void submitAndManage( CallableTaskSubmitter callableTaskSubmitter,
                               Collection<Callable<?>> callableCollection,
                               int submitCount )
  {
    //
    if ( callableTaskSubmitter != null && callableCollection != null )
    {
      for ( Callable<?> callable : callableCollection )
      {
        for ( int ii = 1; ii <= submitCount; ii++ )
        {
          //
          Future<?> future = callableTaskSubmitter.submitTask( callable );
          
          //
          if ( future != null )
          {
            this.manageFutureTask( future );
          }
        }
      }
    }
  }
  
  @SuppressWarnings({ "cast", "unchecked", "rawtypes" })
  public void submitAndManage( RunnableTaskSubmitter runnableTaskSubmitter, Runnable runnable )
  {
    this.submitAndManage( runnableTaskSubmitter, (Collection) Arrays.asList( runnable ), 1 );
  }
  
  @SuppressWarnings({ "cast", "unchecked", "rawtypes" })
  public void submitAndManage( RunnableTaskSubmitter runnableTaskSubmitter, Runnable runnable, int submitCount )
  {
    this.submitAndManage( runnableTaskSubmitter, (Collection) Arrays.asList( runnable ), submitCount );
  }
  
  /**
   * @param runnableTaskSubmitter
   * @param runnableCollection
   * @param submitCount
   */
  public void submitAndManage( RunnableTaskSubmitter runnableTaskSubmitter,
                               Collection<Runnable> runnableCollection,
                               int submitCount )
  {
    //
    if ( runnableTaskSubmitter != null && runnableCollection != null )
    {
      for ( Runnable runnable : runnableCollection )
      {
        for ( int ii = 1; ii <= submitCount; ii++ )
        {
          //
          Future<?> future = runnableTaskSubmitter.submitTask( runnable );
          
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
   * @return true, if no exception has occurred
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
  
  public List<Future<?>> getFutureList()
  {
    return this.futureList;
  }
  
}

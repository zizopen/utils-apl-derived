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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.ExceptionHandledResult;
import org.omnaest.utils.structure.element.converter.ElementConverterIdentitiyCast;

/**
 * @see FutureTaskManager
 * @author Omnaest
 */
public class FutureTaskManagerTest
{
  /* ********************************************** Variables ********************************************** */
  private final int                     corePoolSize      = 10;
  private final int                     maximumPoolSize   = 10;
  private final long                    keepAliveTime     = 1;
  private final TimeUnit                unit              = TimeUnit.SECONDS;
  private final BlockingQueue<Runnable> workQueue         = new ArrayBlockingQueue<Runnable>( 10 );
  private final ExecutorService         executorService   = new ThreadPoolExecutor( this.corePoolSize, this.maximumPoolSize,
                                                                                    this.keepAliveTime, this.unit, this.workQueue );
  private final FutureTaskManager       futureTaskManager = new FutureTaskManager( this.executorService );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testWaitForTaskToFinish() throws InterruptedException
  {
    //
    Runnable runnable = new Runnable()
    {
      
      @Override
      public void run()
      {
        throw new RuntimeException();
      }
    };
    
    //
    this.futureTaskManager.submitAndManage( runnable );
    
    //
    ExceptionHandledResult<?> exceptionHandledResult = this.futureTaskManager.waitForAllTasksToFinish();
    assertTrue( exceptionHandledResult.hasExceptions() );
    assertTrue( exceptionHandledResult.containsAssignableException( RuntimeException.class ) );
  }
  
  @Test
  public void testWaitForAllTasksToFinish() throws Exception
  {
    //
    final Callable<Boolean> callable = new Callable<Boolean>()
    {
      @Override
      public Boolean call() throws Exception
      {
        //
        Thread.sleep( 10 );
        return true;
      }
    };
    
    final int submitCount = 500;
    FutureTaskManager futureTaskManager = new FutureTaskManager( Executors.newFixedThreadPool( submitCount / 10 ) );
    futureTaskManager.submitAndManage( callable, submitCount );
    
    //
    List<Object> resultList = futureTaskManager.waitForAllTasksToFinish().getResult();
    assertTrue( futureTaskManager.areAllTasksFinished() );
    assertEquals( submitCount, resultList.size() );
    for ( Boolean success : ListUtils.convert( resultList, new ElementConverterIdentitiyCast<Object, Boolean>() ) )
    {
      if ( success == null )
      {
        System.out.println( resultList );
      }
      assertNotNull( success );
      assertTrue( success );
    }
  }
  
}

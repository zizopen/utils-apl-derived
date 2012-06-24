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
package org.omnaest.utils.structure.map;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

/**
 * @see ThreadLocalMap
 * @author Omnaest
 */
public class ThreadLocalMapTest
{
  /* ********************************************** Constants ********************************************** */
  protected final static int               NUMBER_OF_THREADS = 50;
  
  /* ********************************************** Variables ********************************************** */
  protected ThreadLocalMap<String, String> threadLocalMap    = new ThreadLocalMap<String, String>();
  protected ExecutorService                executorService   = null;
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    //
    int corePoolSize = NUMBER_OF_THREADS;
    int maximumPoolSize = corePoolSize;
    long keepAliveTime = 10;
    TimeUnit unit = TimeUnit.SECONDS;
    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>( corePoolSize );
    this.executorService = new ThreadPoolExecutor( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue );
  }
  
  @Test
  public void testPut() throws InterruptedException,
                       ExecutionException
  {
    //
    final ThreadLocalMap<String, String> threadLocalMap = this.threadLocalMap;
    
    //
    class Tester implements Callable<Boolean>
    {
      /* ********************************************** Variables ********************************************** */
      private int threadCounter = 0;
      
      /* ********************************************** Methods ********************************************** */
      
      /**
       * @param threadCounter
       */
      public Tester( int threadCounter )
      {
        super();
        this.threadCounter = threadCounter;
      }
      
      @Override
      public Boolean call() throws Exception
      {
        //
        final String value = "" + this.threadCounter;
        final String key = "key";
        threadLocalMap.put( key, value );
        
        //
        Thread.sleep( 100 );
        
        // 
        return threadLocalMap.containsKey( key ) && threadLocalMap.get( key ).equals( value );
      }
      
    }
    
    //
    List<Future<Boolean>> taskList = new ArrayList<Future<Boolean>>();
    for ( int ii = 0; ii < NUMBER_OF_THREADS; ii++ )
    {
      //
      Future<Boolean> task = this.executorService.submit( new Tester( ii ) );
      taskList.add( task );
    }
    
    //
    for ( Future<Boolean> task : taskList )
    {
      assertTrue( task.get() );
    }
    
  }
}

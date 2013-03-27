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
package org.omnaest.utils.operation;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;

import org.omnaest.utils.operation.special.OperationIntrinsic;
import org.omnaest.utils.operation.special.OperationVoid;
import org.omnaest.utils.operation.special.OperationWithResult;

/**
 * Helper for {@link Operation}
 * 
 * @author Omnaest
 */
public class OperationUtils
{
  
  /**
   * Executes the given {@link Operation} using one {@link Callable} tasks submitted to the given {@link ExecutorService}
   * 
   * @param operation
   *          {@link OperationIntrinsic}
   * @param executorService
   *          {@link ExecutorService}
   * @param timeout
   * @param timeUnit
   *          {@link TimeUnit}
   * @return true if no timeout occurs
   */
  public static boolean executeWithTimeout( final OperationIntrinsic operation,
                                            ExecutorService executorService,
                                            long timeout,
                                            TimeUnit timeUnit )
  {
    boolean retval = false;
    
    if ( executorService != null )
    {
      Future<Boolean> submit = executorService.submit( new Callable<Boolean>()
      {
        @Override
        public Boolean call() throws Exception
        {
          operation.execute();
          return true;
        }
      } );
      try
      {
        retval = submit.get( timeout, timeUnit );
      }
      catch ( InterruptedException e )
      {
      }
      catch ( ExecutionException e )
      {
      }
      catch ( TimeoutException e )
      {
      }
    }
    
    return retval;
  }
  
  /**
   * @param operation
   *          {@link OperationWithResult}
   * @param paramter
   * @param locks
   *          {@link Lock}
   * @return result
   */
  public static <R, P> R executeWithLocks( Operation<R, P> operation, P paramter, Lock... locks )
  {
    R retval = null;
    
    for ( Lock lock : locks )
    {
      lock.lock();
    }
    try
    {
      retval = operation.execute( paramter );
    }
    finally
    {
      for ( Lock lock : locks )
      {
        lock.unlock();
      }
    }
    
    return retval;
  }
  
  /**
   * @param operation
   *          {@link OperationWithResult}
   * @param locks
   *          {@link Lock}
   * @return result
   */
  public static <R> R executeWithLocks( OperationWithResult<R> operation, Lock... locks )
  {
    R retval = null;
    
    for ( Lock lock : locks )
    {
      lock.lock();
    }
    try
    {
      retval = operation.execute();
    }
    finally
    {
      for ( Lock lock : locks )
      {
        lock.unlock();
      }
    }
    
    return retval;
  }
  
  /**
   * Similar to {@link #executeWithLocks(OperationIntrinsic, Lock...)}
   * 
   * @param operation
   *          {@link OperationVoid}
   * @param parameter
   * @param locks
   *          {@link Lock}
   */
  public static <P> void executeWithLocks( OperationVoid<P> operation, P parameter, Lock... locks )
  {
    for ( Lock lock : locks )
    {
      lock.lock();
    }
    try
    {
      operation.execute( parameter );
    }
    finally
    {
      for ( Lock lock : locks )
      {
        lock.unlock();
      }
    }
  }
  
  /**
   * @param operation
   *          {@link OperationIntrinsic}
   * @param locks
   *          {@link Lock}
   */
  public static void executeWithLocks( OperationIntrinsic operation, Lock... locks )
  {
    for ( Lock lock : locks )
    {
      lock.lock();
    }
    try
    {
      operation.execute();
    }
    finally
    {
      for ( Lock lock : locks )
      {
        lock.unlock();
      }
    }
  }
}

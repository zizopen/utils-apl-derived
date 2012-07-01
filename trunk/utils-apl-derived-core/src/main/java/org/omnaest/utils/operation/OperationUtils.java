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

import java.util.concurrent.locks.Lock;

import org.omnaest.utils.operation.special.OperationIntrinsic;
import org.omnaest.utils.operation.special.OperationWithResult;

/**
 * Helper for {@link Operation}
 * 
 * @author Omnaest
 */
public class OperationUtils
{
  
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
  public static <P> P executeWithLocks( OperationWithResult<P> operation, Lock... locks )
  {
    P retval = null;
    
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

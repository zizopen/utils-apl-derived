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
package org.omnaest.utils.operation.decorator;

import java.util.concurrent.locks.ReentrantLock;

import org.omnaest.utils.operation.Operation;
import org.springframework.util.Assert;

/**
 * An {@link OperationDecorator} which decorates the {@link Operation#execute(Object)} invocation with the use of
 * {@link ReentrantLock#lock()}.
 * 
 * @author Omnaest
 * @param <RESULT>
 * @param <PARAMETER>
 */
public class OperationDecoratorReentrantLock<RESULT, PARAMETER> extends OperationDecorator<RESULT, PARAMETER>
{
  /* ********************************************** Variables ********************************************** */
  protected ReentrantLock reentrantLock = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param operation
   * @param reentrantLock
   */
  public OperationDecoratorReentrantLock( Operation<RESULT, PARAMETER> operation, ReentrantLock reentrantLock )
  {
    super( operation );
    this.reentrantLock = reentrantLock;
  }
  
  /**
   * @param operation
   */
  public OperationDecoratorReentrantLock( Operation<RESULT, PARAMETER> operation )
  {
    super( operation );
    this.reentrantLock = new ReentrantLock();
  }
  
  @Override
  public RESULT execute( PARAMETER parameter )
  {
    //
    RESULT retval = null;
    
    //
    Assert.notNull( this.operation,
                    "OperationDecoratorReentrantLock cannot decorate an non existing operation. Provide an instance reference which is not null." );
    if ( this.reentrantLock != null )
    {
      //
      this.reentrantLock.lock();
      retval = this.operation.execute( parameter );
      this.reentrantLock.unlock();
    }
    else
    {
      //
      retval = this.operation.execute( parameter );
    }
    
    // 
    return retval;
  }
}

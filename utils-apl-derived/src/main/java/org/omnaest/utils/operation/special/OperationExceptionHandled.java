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
package org.omnaest.utils.operation.special;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.events.exception.ExceptionHandlerManager;
import org.omnaest.utils.events.exception.ExceptionHandlerManager.ExceptionHandlerRegistration;
import org.omnaest.utils.operation.Operation;

/**
 * {@link Operation} which uses an internal {@link ExceptionHandlerManager} to handle occurring {@link Exception}s. With
 * {@link #getExceptionHandlerRegistration()} any {@link ExceptionHandler} instances can register themselves to be notified for
 * any occuring {@link Exception}
 * 
 * @author Omnaest
 * @param <RESULT>
 * @param <PARAMETER>
 */
public class OperationExceptionHandled<RESULT, PARAMETER> implements Operation<RESULT, PARAMETER>
{
  /* ********************************************** Variables ********************************************** */
  protected final Operation<RESULT, PARAMETER> operation;
  protected final ExceptionHandlerManager      exceptionHandlerManager = new ExceptionHandlerManager();
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see OperationExceptionHandled
   * @param operation
   */
  public OperationExceptionHandled( Operation<RESULT, PARAMETER> operation )
  {
    super();
    this.operation = operation;
  }
  
  @Override
  public RESULT execute( PARAMETER parameter )
  {
    //
    RESULT retval = null;
    
    //
    if ( this.operation != null )
    {
      retval = this.exceptionHandlerManager.executeOperationAndHandleAnyException( this.operation, parameter );
    }
    
    // 
    return retval;
  }
  
  /**
   * @return
   * @see org.omnaest.utils.events.exception.ExceptionHandlerManager#getExceptionHandlerRegistration()
   */
  public ExceptionHandlerRegistration getExceptionHandlerRegistration()
  {
    return this.exceptionHandlerManager.getExceptionHandlerRegistration();
  }
  
}

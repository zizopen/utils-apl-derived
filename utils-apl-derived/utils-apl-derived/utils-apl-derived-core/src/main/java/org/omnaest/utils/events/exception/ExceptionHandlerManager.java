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
package org.omnaest.utils.events.exception;

import java.util.LinkedHashSet;
import java.util.Set;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.operation.Operation;
import org.omnaest.utils.operation.special.OperationIntrinsic;

/**
 * An {@link ExceptionHandlerManager} will manage instances of {@link ExceptionHandler}s
 * 
 * @author Omnaest
 */
public class ExceptionHandlerManager
{
  /* ********************************************** Variables ********************************************** */
  protected final ExceptionHandler             exceptionHandler             = new DispatchingExceptionHandler();
  
  protected final Set<ExceptionHandler>        exceptionHandlerSet          = new LinkedHashSet<ExceptionHandler>();
  protected final ExceptionHandlerRegistration exceptionHandlerRegistration = new ExceptionHandlerRegistration();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  protected class DispatchingExceptionHandler implements ExceptionHandler
  {
    @Override
    public void handleExcpetion( Exception e )
    {
      //
      for ( ExceptionHandler exceptionHandler : ExceptionHandlerManager.this.exceptionHandlerSet )
      {
        try
        {
          //
          exceptionHandler.handleExcpetion( e );
        }
        catch ( Exception e2 )
        {
        }
      }
    }
  }
  
  /**
   * Registration for {@link ExceptionHandler}
   * 
   * @author Omnaest
   */
  public class ExceptionHandlerRegistration
  {
    /**
     * Registers an {@link ExceptionHandler}
     * 
     * @param exceptionHandler
     * @return this
     */
    public ExceptionHandlerRegistration registerExceptionHandler( ExceptionHandler exceptionHandler )
    {
      //
      Assert.isNotNull( exceptionHandler, "ExceptionHandler must not be null" );
      ExceptionHandlerManager.this.exceptionHandlerSet.add( exceptionHandler );
      
      //
      return this;
    }
    
    /**
     * Unregisters a given {@link ExceptionHandler}
     * 
     * @param exceptionHandler
     * @return this
     */
    public ExceptionHandlerRegistration unregisterExceptionHandler( ExceptionHandler exceptionHandler )
    {
      //
      Assert.isNotNull( exceptionHandler, "ExceptionHandler must not be null" );
      ExceptionHandlerManager.this.exceptionHandlerSet.remove( exceptionHandler );
      
      //
      return this;
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @return the exceptionHandlerRegistration
   */
  public ExceptionHandlerRegistration getExceptionHandlerRegistration()
  {
    return this.exceptionHandlerRegistration;
  }
  
  /**
   * @return the exceptionHandler
   */
  public ExceptionHandler getExceptionHandler()
  {
    return this.exceptionHandler;
  }
  
  /**
   * Unregisters all registered {@link ExceptionHandler}s
   * 
   * @return this
   */
  public ExceptionHandlerManager clearExceptionHandlers()
  {
    this.exceptionHandlerSet.clear();
    return this;
  }
  
  /**
   * Invokes {@link OperationIntrinsic#execute(Object)} with the given parameter and handles any occurring {@link Exception}
   * 
   * @param operationIntrinsic
   * @return this
   */
  public <R, P> ExceptionHandlerManager executeOperationAndHandleAnyException( OperationIntrinsic operationIntrinsic )
  {
    //
    if ( operationIntrinsic != null )
    {
      try
      {
        operationIntrinsic.execute();
      }
      catch ( Exception e )
      {
        this.exceptionHandler.handleExcpetion( e );
      }
    }
    
    //
    return this;
  }
  
  /**
   * Invokes {@link Operation#execute(Object)} with the given parameter and handles any occurring {@link Exception}
   * 
   * @param operation
   * @param parameter
   * @return
   */
  public <R, P> R executeOperationAndHandleAnyException( Operation<R, P> operation, P parameter )
  {
    //
    R retval = null;
    
    //
    if ( operation != null )
    {
      try
      {
        retval = operation.execute( parameter );
      }
      catch ( Exception e )
      {
        this.exceptionHandler.handleExcpetion( e );
      }
    }
    
    //
    return retval;
  }
}

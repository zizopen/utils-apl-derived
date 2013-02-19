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

import org.omnaest.utils.operation.Operation;
import org.omnaest.utils.time.DurationCapture;

/**
 * {@link Operation} which will allow to execute the {@link #execute(Object)} method only once within a given period of time. All
 * faster reinvoked {@link #execute(Object)} calls will result in an {@link ToFastInvocationException}.
 * 
 * @author Omnaest
 * @param <RESULT>
 * @param <PARAMETER>
 */
public class OperationBlockingToFastRepeatingExecutions<RESULT, PARAMETER> implements Operation<RESULT, PARAMETER>
{
  /* ********************************************** Variables ********************************************** */
  protected Operation<RESULT, PARAMETER> operation                                                      = null;
  protected int                          maximumNumberOfToleratedTooFastInvocations                     = 0;
  protected long                         minimalDurationBetweenExecutionInMilliseconds                  = 1;
  protected long                         forcedDurationAfterToFastInvocationInMilliseconds              = 100;
  protected boolean                      lastInvocationExceededMinimalDurationBetweenTwoExcecutionCalls = false;
  protected DurationCapture              durationCapture                                                = DurationCapture.newInstance();
  protected long                         alreadyToleratedToFastInvocations                              = 0;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see OperationBlockingToFastRepeatingExecutions
   * @see #getDurationToWaitInMilliseconds()
   * @author Omnaest
   */
  public static class ToFastInvocationException extends RuntimeException
  {
    /* ********************************************** Constants ********************************************** */
    private static final long serialVersionUID             = 610230248396653113L;
    
    /* ********************************************** Variables ********************************************** */
    private long              durationToWaitInMilliseconds = 0;
    
    /* ********************************************** Methods ********************************************** */
    public ToFastInvocationException( long durationToWaitInMilliseconds )
    {
      super( "The Operation#execute(...) method was invoked to often within a given period of time. Please wait for "
             + durationToWaitInMilliseconds + " ms" );
      this.durationToWaitInMilliseconds = durationToWaitInMilliseconds;
    }
    
    /**
     * @return
     */
    public long getDurationToWaitInMilliseconds()
    {
      return this.durationToWaitInMilliseconds;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param operation
   * @param minimalDurationBetweenExecutionInMilliseconds
   * @param forcedDurationAfterToFastInvocationInMilliseconds
   */
  public OperationBlockingToFastRepeatingExecutions( Operation<RESULT, PARAMETER> operation,
                                                     long minimalDurationBetweenExecutionInMilliseconds,
                                                     long forcedDurationAfterToFastInvocationInMilliseconds )
  {
    super();
    this.operation = operation;
    this.minimalDurationBetweenExecutionInMilliseconds = minimalDurationBetweenExecutionInMilliseconds;
    this.forcedDurationAfterToFastInvocationInMilliseconds = forcedDurationAfterToFastInvocationInMilliseconds;
  }
  
  /**
   * @see OperationBlockingToFastRepeatingExecutions
   * @param operation
   * @param maximumNumberOfToleratedTooFastInvocations
   * @param minimalDurationBetweenExecutionInMilliseconds
   * @param forcedDurationAfterToFastInvocationInMilliseconds
   */
  public OperationBlockingToFastRepeatingExecutions( Operation<RESULT, PARAMETER> operation,
                                                     int maximumNumberOfToleratedTooFastInvocations,
                                                     long minimalDurationBetweenExecutionInMilliseconds,
                                                     long forcedDurationAfterToFastInvocationInMilliseconds )
  {
    super();
    this.operation = operation;
    this.maximumNumberOfToleratedTooFastInvocations = maximumNumberOfToleratedTooFastInvocations;
    this.minimalDurationBetweenExecutionInMilliseconds = minimalDurationBetweenExecutionInMilliseconds;
    this.forcedDurationAfterToFastInvocationInMilliseconds = forcedDurationAfterToFastInvocationInMilliseconds;
  }
  
  @Override
  public RESULT execute( PARAMETER parameter )
  {
    //
    this.throwToFastInvocationExceptionIfInvocationIsTooFast();
    
    //
    return this.operation != null ? this.operation.execute( parameter ) : null;
  }
  
  private void throwToFastInvocationExceptionIfInvocationIsTooFast() throws ToFastInvocationException
  {
    //
    long interimTimeInMilliseconds = this.durationCapture.getInterimTimeInMilliseconds();
    if ( !this.lastInvocationExceededMinimalDurationBetweenTwoExcecutionCalls
         && interimTimeInMilliseconds <= this.minimalDurationBetweenExecutionInMilliseconds )
    {
      //
      if ( this.alreadyToleratedToFastInvocations++ >= this.maximumNumberOfToleratedTooFastInvocations )
      {
        //
        this.lastInvocationExceededMinimalDurationBetweenTwoExcecutionCalls = true;
        long durationToWaitInMilliseconds = this.minimalDurationBetweenExecutionInMilliseconds - interimTimeInMilliseconds;
        throw new ToFastInvocationException( durationToWaitInMilliseconds );
      }
    }
    else if ( this.lastInvocationExceededMinimalDurationBetweenTwoExcecutionCalls
              && interimTimeInMilliseconds <= this.forcedDurationAfterToFastInvocationInMilliseconds )
    {
      //
      long durationToWaitInMilliseconds = this.forcedDurationAfterToFastInvocationInMilliseconds - interimTimeInMilliseconds;
      throw new ToFastInvocationException( durationToWaitInMilliseconds );
    }
    else
    {
      this.lastInvocationExceededMinimalDurationBetweenTwoExcecutionCalls = false;
      this.alreadyToleratedToFastInvocations = 0;
      this.durationCapture.startTimeMeasurement();
    }
  }
  
  /**
   * Returns true if the next {@link #execute(Object)} will be forced to wait for
   * {@link #forcedDurationAfterToFastInvocationInMilliseconds}
   * 
   * @return
   */
  public boolean hasLastInvocationExceededMinimalDurationBetweenTwoExcecutionCalls()
  {
    return this.lastInvocationExceededMinimalDurationBetweenTwoExcecutionCalls;
  }
  
}

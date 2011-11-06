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
package org.omnaest.utils.operation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.omnaest.utils.operation.OperationBlockingToFastRepeatingExecutions.ToFastInvocationException;
import org.omnaest.utils.structure.element.ExceptionHandledResult;

/**
 * @see OperationBlockingToFastRepeatingExecutions
 * @author Omnaest
 */
public class OperationBlockingToFastRepeatingExecutionsTest
{
  
  @SuppressWarnings("rawtypes")
  @Test
  public void testExecuteToFast()
  {
    //
    long forcedDurationAfterToFastInvocationInMilliseconds = 1000;
    long minimalDurationBetweenExecutionInMilliseconds = 50;
    @SuppressWarnings("unchecked")
    Operation<String, String> operationOriginal = Mockito.mock( Operation.class );
    Mockito.when( operationOriginal.execute( Matchers.anyString() ) ).thenReturn( "tata" );
    
    //
    Operation<ExceptionHandledResult<String>, String> operation = new OperationBlockingToFastRepeatingExecutions<String, String>(
                                                                                                                                  operationOriginal,
                                                                                                                                  minimalDurationBetweenExecutionInMilliseconds,
                                                                                                                                  forcedDurationAfterToFastInvocationInMilliseconds );
    
    //
    {
      //
      ExceptionHandledResult<String> exceptionHandledResult = operation.execute( "lala" );
      assertEquals( "tata", exceptionHandledResult.getResult() );
      assertTrue( exceptionHandledResult.hasNoExceptions() );
    }
    {
      //
      ExceptionHandledResult<String> exceptionHandledResult = operation.execute( "lala" );
      assertEquals( null, exceptionHandledResult.getResult() );
      assertTrue( exceptionHandledResult.hasExceptions() );
      assertTrue( ( (OperationBlockingToFastRepeatingExecutions) operation ).hasLastInvocationExceededMinimalDurationBetweenTwoExcecutionCalls() );
      assertTrue( exceptionHandledResult.getFirstException() instanceof ToFastInvocationException );
      assertTrue( ( (ToFastInvocationException) exceptionHandledResult.getFirstException() ).getDurationToWaitInMilliseconds() > 0 );
    }
    {
      //
      ExceptionHandledResult<String> exceptionHandledResult = operation.execute( "lala" );
      assertEquals( null, exceptionHandledResult.getResult() );
      assertTrue( exceptionHandledResult.hasExceptions() );
      assertTrue( ( (OperationBlockingToFastRepeatingExecutions) operation ).hasLastInvocationExceededMinimalDurationBetweenTwoExcecutionCalls() );
      assertTrue( exceptionHandledResult.getFirstException() instanceof ToFastInvocationException );
      assertTrue( ( (ToFastInvocationException) exceptionHandledResult.getFirstException() ).getDurationToWaitInMilliseconds() > 100 );
    }
    
  }
  
}

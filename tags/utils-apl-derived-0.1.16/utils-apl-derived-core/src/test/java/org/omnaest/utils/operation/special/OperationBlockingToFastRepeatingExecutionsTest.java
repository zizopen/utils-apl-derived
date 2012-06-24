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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.omnaest.utils.operation.Operation;
import org.omnaest.utils.operation.special.OperationBlockingToFastRepeatingExecutions.ToFastInvocationException;
import org.omnaest.utils.structure.element.ExceptionHandledResult;

/**
 * @see OperationBlockingToFastRepeatingExecutions
 * @author Omnaest
 */
public class OperationBlockingToFastRepeatingExecutionsTest
{
  
  @Test
  public void testExecuteToFast() throws InterruptedException
  {
    //
    long forcedDurationAfterToFastInvocationInMilliseconds = 200;
    long minimalDurationBetweenExecutionInMilliseconds = 50;
    int maximumNumberOfToleratedTooFastInvocations = 2;
    @SuppressWarnings("unchecked")
    Operation<String, String> operationOriginal = Mockito.mock( Operation.class );
    Mockito.when( operationOriginal.execute( Matchers.anyString() ) ).thenReturn( "tata" );
    
    //
    final OperationBlockingToFastRepeatingExecutions<String, String> operationBlockingToFastRepeatingExecutions = new OperationBlockingToFastRepeatingExecutions<String, String>(
                                                                                                                                                                                  operationOriginal,
                                                                                                                                                                                  maximumNumberOfToleratedTooFastInvocations,
                                                                                                                                                                                  minimalDurationBetweenExecutionInMilliseconds,
                                                                                                                                                                                  forcedDurationAfterToFastInvocationInMilliseconds );
    final Operation<ExceptionHandledResult<String>, String> operation = new OperationExceptionHandledResult<String, String>(
                                                                                                                             operationBlockingToFastRepeatingExecutions );
    
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
      assertEquals( "tata", exceptionHandledResult.getResult() );
      assertTrue( exceptionHandledResult.hasNoExceptions() );
    }
    {
      //
      ExceptionHandledResult<String> exceptionHandledResult = operation.execute( "lala" );
      assertEquals( "tata", exceptionHandledResult.getResult() );
      assertTrue( exceptionHandledResult.hasNoExceptions() );
    }
    
    //
    Thread.sleep( minimalDurationBetweenExecutionInMilliseconds * 2 );
    
    {
      //
      ExceptionHandledResult<String> exceptionHandledResult = operation.execute( "lala" );
      assertEquals( "tata", exceptionHandledResult.getResult() );
      assertTrue( exceptionHandledResult.hasNoExceptions() );
    }
    {
      //
      ExceptionHandledResult<String> exceptionHandledResult = operation.execute( "lala" );
      assertEquals( "tata", exceptionHandledResult.getResult() );
      assertTrue( exceptionHandledResult.hasNoExceptions() );
    }
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
      assertTrue( operationBlockingToFastRepeatingExecutions.hasLastInvocationExceededMinimalDurationBetweenTwoExcecutionCalls() );
      assertTrue( exceptionHandledResult.getFirstException() instanceof ToFastInvocationException );
      assertTrue( ( (ToFastInvocationException) exceptionHandledResult.getFirstException() ).getDurationToWaitInMilliseconds() > 0 );
    }
    {
      //
      ExceptionHandledResult<String> exceptionHandledResult = operation.execute( "lala" );
      assertEquals( null, exceptionHandledResult.getResult() );
      assertTrue( exceptionHandledResult.hasExceptions() );
      assertTrue( operationBlockingToFastRepeatingExecutions.hasLastInvocationExceededMinimalDurationBetweenTwoExcecutionCalls() );
      assertTrue( exceptionHandledResult.getFirstException() instanceof ToFastInvocationException );
      assertTrue( ( (ToFastInvocationException) exceptionHandledResult.getFirstException() ).getDurationToWaitInMilliseconds() > 100 );
    }
    
    //
    Thread.sleep( forcedDurationAfterToFastInvocationInMilliseconds * 2 );
    
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
      assertEquals( "tata", exceptionHandledResult.getResult() );
      assertTrue( exceptionHandledResult.hasNoExceptions() );
    }
    {
      //
      ExceptionHandledResult<String> exceptionHandledResult = operation.execute( "lala" );
      assertEquals( "tata", exceptionHandledResult.getResult() );
      assertTrue( exceptionHandledResult.hasNoExceptions() );
    }
  }
  
}

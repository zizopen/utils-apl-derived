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

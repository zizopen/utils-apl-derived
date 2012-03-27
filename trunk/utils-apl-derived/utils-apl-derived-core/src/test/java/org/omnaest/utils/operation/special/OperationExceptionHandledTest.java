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

import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.operation.Operation;

public class OperationExceptionHandledTest
{
  /* ********************************************** Variables ********************************************** */
  private final Operation<String, String>                 operation                 = new Operation<String, String>()
                                                                                    {
                                                                                      @Override
                                                                                      public String execute( String parameter )
                                                                                      {
                                                                                        throw new RuntimeException(
                                                                                                                    "Test Exception" );
                                                                                      }
                                                                                    };
  private final OperationExceptionHandled<String, String> operationExceptionHandled = new OperationExceptionHandled<String, String>(
                                                                                                                                     this.operation );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testExecute()
  {
    //
    final AtomicBoolean atomicBoolean = new AtomicBoolean( false );
    final ExceptionHandler exceptionHandler = new ExceptionHandler()
    {
      @Override
      public void handleException( Exception e )
      {
        atomicBoolean.set( true );
      }
    };
    this.operationExceptionHandled.getExceptionHandlerRegistration().registerExceptionHandler( exceptionHandler );
    
    //
    String parameter = "test";
    this.operationExceptionHandled.execute( parameter );
    
    //
    assertTrue( atomicBoolean.get() );
  }
  
}

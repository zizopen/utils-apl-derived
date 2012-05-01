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
package org.omnaest.utils.structure.element;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

/**
 * @see ExceptionHandledResult
 * @author Omnaest
 */
public class ExceptionHandledResultTest
{
  
  /* ********************************************** Variables ********************************************** */
  private Collection<Exception>          exceptionCollection    = Arrays.asList( new Exception(
                                                                                                "a normal exception",
                                                                                                new Exception(
                                                                                                               new UnsupportedOperationException() ) ),
                                                                                 new RuntimeException() );
  private String                         result                 = "result";
  private ExceptionHandledResult<String> exceptionHandledResult = new ExceptionHandledResult<String>( this.result,
                                                                                                      this.exceptionCollection );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testContainsAssignableException()
  {
    assertTrue( this.exceptionHandledResult.containsAssignableException( RuntimeException.class ) );
    assertTrue( this.exceptionHandledResult.containsAssignableException( UnsupportedOperationException.class ) );
  }
  
}

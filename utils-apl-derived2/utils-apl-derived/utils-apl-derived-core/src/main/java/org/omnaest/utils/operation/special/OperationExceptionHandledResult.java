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

import java.util.ArrayList;
import java.util.Collection;

import org.omnaest.utils.operation.Operation;
import org.omnaest.utils.structure.element.ExceptionHandledResult;

/**
 * {@link Operation} which will catch any occurring {@link Exception} and return an {@link ExceptionHandledResult}
 * 
 * @see OperationExceptionHandled
 * @author Omnaest
 * @param <RESULT>
 * @param <PARAMETER>
 */
public class OperationExceptionHandledResult<RESULT, PARAMETER> implements Operation<ExceptionHandledResult<RESULT>, PARAMETER>
{
  /* ********************************************** Variables ********************************************** */
  protected final Operation<RESULT, PARAMETER> operation;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see OperationExceptionHandledResult
   * @param operation
   */
  public OperationExceptionHandledResult( Operation<RESULT, PARAMETER> operation )
  {
    super();
    this.operation = operation;
  }
  
  @Override
  public ExceptionHandledResult<RESULT> execute( PARAMETER parameter )
  {
    //
    RESULT retval = null;
    final Collection<Exception> exceptionCollection = new ArrayList<Exception>();
    
    //
    if ( this.operation != null )
    {
      try
      {
        //
        retval = this.operation.execute( parameter );
      }
      catch ( Exception e )
      {
        exceptionCollection.add( e );
      }
    }
    
    // 
    return new ExceptionHandledResult<RESULT>( retval, exceptionCollection );
  }
  
}

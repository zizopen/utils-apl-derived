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

import java.util.ArrayList;
import java.util.Collection;

import org.omnaest.utils.structure.element.ExceptionHandledResult;

/**
 * {@link Operation} which is wrapped with a try catch block catching all {@link Exception} derivative types. As result a
 * {@link ExceptionHandledResult} is returned.
 * 
 * @author Omnaest
 * @param <RESULT>
 * @param <PARAMETER>
 */
public class OperationExceptionHandled<RESULT, PARAMETER> implements Operation<ExceptionHandledResult<RESULT>, PARAMETER>
{
  /* ********************************************** Variables ********************************************** */
  protected Operation<RESULT, PARAMETER> operation = null;
  
  /* ********************************************** Methods ********************************************** */
  
  @Override
  public ExceptionHandledResult<RESULT> execute( PARAMETER parameter )
  {
    //
    RESULT result = null;
    Collection<Exception> exceptionCollection = new ArrayList<Exception>();
    
    //
    if ( this.operation != null )
    {
      try
      {
        //
        result = this.operation.execute( parameter );
      }
      catch ( Exception e )
      {
        exceptionCollection.add( e );
      }
    }
    
    // 
    return new ExceptionHandledResult<RESULT>( result, exceptionCollection );
  }
  
}

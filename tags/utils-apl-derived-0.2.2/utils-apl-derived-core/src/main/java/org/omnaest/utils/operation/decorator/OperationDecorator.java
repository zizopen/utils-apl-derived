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
package org.omnaest.utils.operation.decorator;

import org.omnaest.utils.operation.Operation;

/**
 * Abstract decorator for an {@link Operation}
 * 
 * @see Operation
 * @author Omnaest
 * @param <RESULT>
 * @param <PARAMETER>
 */
public abstract class OperationDecorator<RESULT, PARAMETER> implements Operation<RESULT, PARAMETER>
{
  /* ********************************************** Variables ********************************************** */
  protected Operation<RESULT, PARAMETER> operation = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param operation
   */
  public OperationDecorator( Operation<RESULT, PARAMETER> operation )
  {
    super();
    this.operation = operation;
  }
  
  /**
   * @param operation
   */
  public void setOperation( Operation<RESULT, PARAMETER> operation )
  {
    this.operation = operation;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "OperationDecorator [operation=" );
    builder.append( this.operation );
    builder.append( "]" );
    return builder.toString();
  }
  
}

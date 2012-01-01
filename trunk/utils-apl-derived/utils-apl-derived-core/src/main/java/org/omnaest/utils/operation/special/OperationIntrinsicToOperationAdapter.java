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

/**
 * Adapter to use an {@link OperationIntrinsic} instance as {@link Operation} instance.
 * 
 * @see Operation
 * @see OperationIntrinsic
 * @author Omnaest
 */
public class OperationIntrinsicToOperationAdapter implements Operation<Void, Void>
{
  /* ********************************************** Variables ********************************************** */
  protected final OperationIntrinsic operationIntrinsic;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @see OperationIntrinsicToOperationAdapter
   * @param operationIntrinsic
   */
  public OperationIntrinsicToOperationAdapter( OperationIntrinsic operationIntrinsic )
  {
    super();
    this.operationIntrinsic = operationIntrinsic;
  }
  
  @Override
  public Void execute( Void parameter )
  {
    //
    if ( this.operationIntrinsic != null )
    {
      this.operationIntrinsic.execute();
    }
    
    //
    return null;
  }
  
}

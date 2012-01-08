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
package org.omnaest.utils.operation.composite;

import java.util.ArrayList;
import java.util.List;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.operation.Operation;

/**
 * Composite which takes a {@link List} of {@link Operation} instances. All {@link Operation#execute(Object)} will be invoked in
 * the given order of the {@link List}
 * 
 * @author Omnaest
 * @param <RESULT>
 * @param <PARAMETER>
 */
public class OperationComposite<RESULT, PARAMETER> implements Operation<List<RESULT>, PARAMETER>
{
  /* ********************************************** Variables ********************************************** */
  protected final List<Operation<RESULT, PARAMETER>> operationList;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see OperationComposite
   * @param operationList
   */
  public OperationComposite( List<Operation<RESULT, PARAMETER>> operationList )
  {
    super();
    this.operationList = operationList;
    Assert.isNotNull( operationList );
  }
  
  @Override
  public List<RESULT> execute( PARAMETER parameter )
  {
    //
    List<RESULT> retlist = new ArrayList<RESULT>();
    
    //
    for ( Operation<RESULT, PARAMETER> operation : this.operationList )
    {
      if ( operation != null )
      {
        //
        RESULT result = operation.execute( parameter );
        retlist.add( result );
      }
    }
    
    // 
    return retlist;
  }
  
}

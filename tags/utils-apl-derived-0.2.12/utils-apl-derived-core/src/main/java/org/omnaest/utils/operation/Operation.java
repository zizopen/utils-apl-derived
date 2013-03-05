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

import org.omnaest.utils.operation.special.OperationIntrinsic;
import org.omnaest.utils.operation.special.OperationVoid;

/**
 * An {@link Operation} offers an {@link #execute(Object)} method. This supports simple strategy pattern.
 * 
 * @see OperationVoid
 * @see OperationIntrinsic
 * @see OperationFactory
 * @param <RESULT>
 * @param <PARAMETER>
 * @author Omnaest
 */
public interface Operation<RESULT, PARAMETER>
{
  public RESULT execute( PARAMETER parameter );
}

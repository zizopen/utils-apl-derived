/*******************************************************************************
 * Copyright 2012 Danny Kunz
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
package org.omnaest.utils.events.exception.basic;

import org.omnaest.utils.events.exception.ExceptionHandlerSerializable;

/**
 * {@link ExceptionHandlerSerializable} holding an internal state flag. If any {@link Exception} has been handled
 * {@link #hasNoErrors()} will return false until {@link #resetState()} is called
 * 
 * @author Omnaest
 */
public class ExceptionHandlerBooleanState implements ExceptionHandlerSerializable
{
  private static final long serialVersionUID = -2868609329599755244L;
  
  private boolean           noErrors         = true;
  
  @Override
  public void handleException( Exception e )
  {
    this.noErrors = false;
  }
  
  public boolean hasNoErrors()
  {
    return this.noErrors;
  }
  
  public void resetState()
  {
    this.noErrors = true;
  }
}

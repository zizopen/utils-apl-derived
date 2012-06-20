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
package org.omnaest.utils.spring.extension;

import java.io.Serializable;
import java.util.List;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.operation.special.OperationVoid;

/**
 * Generic template implementation for an {@link ExtensionPoint} which allows to {@link #setExceptionHandler(ExceptionHandler)}
 * 
 * @see ExtensionPoint
 * @param <S>
 *          type of service
 * @author Omnaest
 */
@ExtensionPoint
public abstract class ExtensionPointTemplate<S> implements Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = 6825126831651331418L;
  /* ********************************************** Beans / Services / References ********************************************** */
  private final List<S>     beanList;
  private ExceptionHandler  exceptionHandler = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see ExtensionPointTemplate
   * @param serviceList
   */
  protected ExtensionPointTemplate( List<S> serviceList )
  {
    super();
    this.beanList = serviceList;
  }
  
  /**
   * Executes the given {@link OperationVoid} on all internal bean instances of this {@link ExtensionPointTemplate}
   * 
   * @param operation
   */
  protected void executeOnAll( OperationVoid<S> operation )
  {
    //
    if ( operation != null && this.beanList != null )
    {
      for ( S bean : this.beanList )
      {
        if ( bean != null )
        {
          try
          {
            //
            operation.execute( bean );
          }
          catch ( Exception e )
          {
            if ( this.exceptionHandler != null )
            {
              this.exceptionHandler.handleException( e );
            }
          }
        }
      }
    }
  }
  
  /**
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   * @return this
   */
  public ExtensionPointTemplate<S> setExceptionHandler( ExceptionHandler exceptionHandler )
  {
    this.exceptionHandler = exceptionHandler;
    return this;
  }
}

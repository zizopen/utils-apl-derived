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
package org.omnaest.utils.dispatcher;

import java.io.Serializable;
import java.util.List;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerIgnoring;
import org.omnaest.utils.operation.special.OperationVoid;
import org.omnaest.utils.structure.element.ObjectUtils;

/**
 * A {@link DispatcherAbstract} allows to implements dispatcher instances
 * 
 * @see #executeOnAllInstances(OperationVoid)
 * @author Omnaest
 */
public abstract class DispatcherAbstract<T> implements Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long  serialVersionUID = 392437202904888843L;
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  protected final List<T>    instanceList;
  protected ExceptionHandler exceptionHandler = new ExceptionHandlerIgnoring();
  
  /* *************************************************** Methods **************************************************** */
  
  public DispatcherAbstract( List<T> instanceList )
  {
    super();
    this.instanceList = instanceList;
  }
  
  /**
   * Executes a given {@link OperationVoid} on all dispatch instances
   * 
   * @param operation
   */
  public void executeOnAllInstances( OperationVoid<T> operation )
  {
    if ( operation != null )
    {
      for ( T instance : this.instanceList )
      {
        try
        {
          operation.execute( instance );
        }
        catch ( Exception e )
        {
          this.exceptionHandler.handleException( e );
        }
      }
    }
  }
  
  /**
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   * @return this
   */
  public DispatcherAbstract<T> setExceptionHandler( ExceptionHandler exceptionHandler )
  {
    this.exceptionHandler = ObjectUtils.defaultIfNull( exceptionHandler, new ExceptionHandlerIgnoring() );
    return this;
  }
  
}

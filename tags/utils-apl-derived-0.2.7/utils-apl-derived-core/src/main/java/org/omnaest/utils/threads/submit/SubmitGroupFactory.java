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
package org.omnaest.utils.threads.submit;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerDelegate;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerIgnoring;

/**
 * Enclosure around an {@link ExecutorService} which allows to manage groups of submitted tasks with the same result type.
 * 
 * @author Omnaest
 */
public class SubmitGroupFactory implements Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long        serialVersionUID = -5393142042749817737L;
  
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  private final ExecutorService    executorService;
  private ExceptionHandlerDelegate exceptionHandler = new ExceptionHandlerDelegate( new ExceptionHandlerIgnoring() );
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see SubmitGroupFactory
   * @param executorService
   *          {@link ExecutorService}
   */
  public SubmitGroupFactory( ExecutorService executorService )
  {
    super();
    this.executorService = executorService;
  }
  
  /**
   * @param exceptionHandler
   * @return
   */
  public SubmitGroupFactory setExceptionHandler( ExceptionHandler exceptionHandler )
  {
    this.exceptionHandler.setExceptionHandler( exceptionHandler );
    return this;
  }
  
  public <T> SubmitGroup<T> newSubmitGroup( Class<T> type )
  {
    return new SubmitGroupImpl<T>( this.executorService, this.exceptionHandler );
  }
  
  public ExecutorService getExecutorService()
  {
    return this.executorService;
  }
  
  public <T> SubmitGroup<T> newSubmitGroup( Collection<T> resultCollection )
  {
    return new SubmitGroupImpl<T>( this.executorService, this.exceptionHandler, resultCollection );
  }
  
}

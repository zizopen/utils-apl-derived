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
import java.util.concurrent.Callable;

/**
 * A group of {@link Callable} submits which share the same result type
 * 
 * @author Omnaest
 * @param <T>
 */
public interface SubmitGroup<T> extends Serializable
{
  /**
   * @param callable
   *          {@link Callable}
   * @return this
   */
  public SubmitGroup<T> submit( Callable<T> callable );
  
  /**
   * @param callable
   *          {@link Callable}
   * @param numberOfTimes
   * @return this
   */
  public SubmitGroup<T> submit( Callable<T> callable, int numberOfTimes );
  
  /**
   * @return {@link Waiter}
   */
  public Waiter<T> doWait();
  
}

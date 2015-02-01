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
import java.util.concurrent.TimeUnit;

/**
 * A {@link Waiter} allows to wait for the results of a {@link SubmitGroup} and allows to reduce the result into further forms.
 * 
 * @author Omnaest
 * @param <T>
 */
public interface Waiter<T> extends Serializable
{
  /**
   * @param amount
   * @param timeUnit
   *          {@link TimeUnit}
   * @return this
   */
  public Reducer<T> anAmountOfTime( int amount, TimeUnit timeUnit );
  
  /**
   * @return this
   */
  public Reducer<T> untilAllTasksAreDone();
  
  /**
   * @param numberOfTasks
   * @return this
   */
  public Reducer<T> untilTheNumberOfTasksAreDone( int numberOfTasks );
  
  /**
   * @param ratio
   * @return this
   */
  public Reducer<T> untilThePercentageOfTasksAreDone( double ratio );
  
}

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
package org.omnaest.utils.listener.asynchronous;

import java.util.concurrent.Future;

import org.omnaest.utils.listener.ListenerRegistration;

/**
 * @see ListenerRegistration
 * @see ListenerAsynchronous
 * @see ListenerManagerAsynchronous
 * @author Omnaest
 * @param <EVENT>
 * @param <RESULT>
 */
public interface ListenerRegistrationAsynchronous<EVENT, RESULT> extends ListenerRegistration<Future<EVENT>, Future<RESULT>>
{
  /**
   * @param listener
   * @return
   */
  public ListenerRegistrationAsynchronous<EVENT, RESULT> addListener( ListenerAsynchronous<EVENT, RESULT> listener );
  
  /**
   * @param listener
   * @return
   */
  public ListenerRegistrationAsynchronous<EVENT, RESULT> removeListener( ListenerAsynchronous<EVENT, RESULT> listener );
  
}
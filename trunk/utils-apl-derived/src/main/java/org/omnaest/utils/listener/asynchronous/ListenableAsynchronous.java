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

import org.omnaest.utils.listener.Listenable;
import org.omnaest.utils.listener.Listener;
import org.omnaest.utils.listener.ListenerRegistration;

/**
 * Interface for classes which allows to register {@link ListenerAsynchronous}s at a {@link ListenerRegistrationAsynchronous}.
 * 
 * @author Omnaest
 */
public interface ListenableAsynchronous<EVENT, RESULT> extends Listenable<Future<EVENT>, Future<RESULT>>
{
  /**
   * Offers the {@link ListenerRegistrationAsynchronous} to register {@link ListenerAsynchronous} to this instance. All events
   * will be signaled to the {@link Listener}s from the time on they get registered.
   * 
   * @see ListenerRegistration
   * @see ListenableAsynchronous
   * @return
   */
  public ListenerRegistrationAsynchronous<EVENT, RESULT> getListenerRegistration();
}

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
package org.omnaest.utils.listener;

import org.omnaest.utils.listener.concrete.ListenerRegistrationImpl;

/**
 * Interface for classes which allows to register {@link Listener}s at a {@link ListenerRegistrationImpl}.
 * 
 * @author Omnaest
 */
public interface Listenable<EVENT, RESULT>
{
  /**
   * Offers the {@link ListenerRegistrationImpl} to register {@link Listener} to this instance. All events will be signaled to the
   * {@link Listener}s from the time on they get registered.
   * 
   * @see ListenerRegistrationImpl
   * @see Listenable
   * @return
   */
  public ListenerRegistration<EVENT, RESULT> getListenerRegistration();
}

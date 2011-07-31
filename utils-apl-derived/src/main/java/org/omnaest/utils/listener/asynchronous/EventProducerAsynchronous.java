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

import org.omnaest.utils.listener.EventListener;
import org.omnaest.utils.listener.EventListenerRegistration;
import org.omnaest.utils.listener.EventProducer;

/**
 * Interface for classes which allows to register {@link EventListenerAsynchronous}s at a {@link EventListenerRegistrationAsynchronous}.
 * 
 * @author Omnaest
 */
public interface EventProducerAsynchronous<EVENT, RESULT> extends EventProducer<Future<EVENT>, Future<RESULT>>
{
  /**
   * Offers the {@link EventListenerRegistrationAsynchronous} to register {@link EventListenerAsynchronous} to this instance. All events
   * will be signaled to the {@link EventListener}s from the time on they get registered.
   * 
   * @see EventListenerRegistration
   * @see EventProducerAsynchronous
   * @return
   */
  public EventListenerRegistrationAsynchronous<EVENT, RESULT> getEventListenerRegistration();
}

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
package org.omnaest.utils.events;

import java.io.Serializable;

/**
 * Interface for classes which allows to register {@link EventListener}s at a {@link EventListenerRegistration}.
 * 
 * @author Omnaest
 */
public interface EventProducer<EVENT, RESULT> extends Serializable
{
  /**
   * Offers the {@link EventListenerRegistration} to register {@link EventListener} to this instance. All events will be signaled
   * to the {@link EventListener}s from the time on they get registered.
   * 
   * @see EventListenerRegistration
   * @see EventProducer
   * @return
   */
  public EventListenerRegistration<EVENT, RESULT> getEventListenerRegistration();
}

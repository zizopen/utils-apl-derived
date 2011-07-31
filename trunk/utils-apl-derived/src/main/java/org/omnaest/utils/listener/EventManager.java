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

import org.omnaest.utils.listener.event.EventResults;

/**
 * The {@link EventManager} is the central control unit within a client class which want to fire events. It holds references to an
 * {@link EventListenerRegistration} as well as to a {@link EventManagerConnector}.<br>
 * <br>
 * The {@link EventManager#fireEvent(Object)} method will pick up given events and will take care of the event handling. This
 * includes the management of connected {@link EventListener} as well as a chaining path via the {@link EventManagerConnector}.<br>
 * <br>
 * Additionally with the {@link EventProducer} interface clients can make themselves usable for {@link EventHandler} instances
 * which delegates the control to a central {@link EventHandler}.
 * 
 * @author Omnaest
 * @param <EVENT>
 * @param <RESULT>
 */
public interface EventManager<EVENT, RESULT> extends EventProducer<EVENT, RESULT>
{
  
  /**
   * Removes all listeners from the {@link EventManager} instance.
   * 
   * @return this
   */
  public EventManager<EVENT, RESULT> clearListeners();
  
  /**
   * Fires a given event. This will move the current control flow to the registered {@link EventListener} instances. This implies
   * that performance critical logic should fire events carefully, since it can not be guaranteed that connected
   * {@link EventListener} will handle the event with acceptable performance.
   * 
   * @param event
   * @return {@link EventResults}
   */
  public EventResults<RESULT> fireEvent( EVENT event );
  
  /**
   * @see EventManagerConnector
   * @return
   */
  public EventManagerConnector<EVENT, RESULT> getEventManagerConnector();
}

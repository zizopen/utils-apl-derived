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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

import org.omnaest.utils.listener.EventHandler;
import org.omnaest.utils.listener.EventManager;
import org.omnaest.utils.listener.event.EventResults;

/**
 * @see Future
 * @see EventListenerAsynchronous
 * @see EventManager
 * @author Omnaest
 * @param <EVENT>
 * @param <RESULT>
 */
public interface EventManagerAsynchronous<EVENT, RESULT> extends EventManager<EVENT, Future<RESULT>>,
                                                         EventProducerAsynchronous<EVENT, RESULT>
{
  
  /**
   * Fires an event but puts it into an internal {@link BlockingQueue} which has to be pulled from {@link EventHandler}s actively. <br>
   * If the internal {@link BlockingQueue} is filled this method call block until the {@link BlockingQueue} gets free event slots
   * again. For a non blocking behavior see {@link #tryToFireQueuedEvent(Object)} instead, or use the regular push behaviored
   * {@link #fireEvent(Object)}.
   * 
   * @see #fireEvent(Object)
   * @see #tryToFireQueuedEvent(Object)
   * @param event
   * @return {@link EventResults}
   */
  public EventResults<RESULT> fireQueuedEvent( EVENT event );
  
  /**
   * Tries to fire the given event if the internal {@link BlockingQueue} has {@link BlockingQueue#remainingCapacity()} left.
   * Otherwise the event is discarded.<br>
   * Because of this behavior only non critical event should be fired by using this method. For critical events use
   * {@link #fireQueuedEvent(Object)} or {@link #fireEvent(Object)} instead.
   * 
   * @see #fireEvent(Object)
   * @see #fireQueuedEvent(Object)
   * @param event
   * @return {@link EventResults}
   */
  public EventResults<RESULT> tryToFireQueuedEvent( EVENT event );
}

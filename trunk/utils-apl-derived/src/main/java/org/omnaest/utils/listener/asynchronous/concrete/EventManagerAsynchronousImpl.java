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
package org.omnaest.utils.listener.asynchronous.concrete;

import java.util.concurrent.Future;

import org.omnaest.utils.listener.EventManager;
import org.omnaest.utils.listener.asynchronous.EventManagerAsynchronous;
import org.omnaest.utils.listener.asynchronous.EventListenerRegistrationAsynchronous;
import org.omnaest.utils.listener.concrete.EventManagerImpl;

/**
 * @see EventManagerAsynchronous
 * @see EventManager
 * @see EventManagerImpl
 * @see EventListenerRegistrationAsynchronousImpl
 * @author Omnaest
 * @param <EVENT>
 * @param <RESULT>
 */
public class EventManagerAsynchronousImpl<EVENT, RESULT> extends EventManagerImpl<Future<EVENT>, Future<RESULT>>
                                                                                                                      implements
                                                                                                                      EventManagerAsynchronous<EVENT, RESULT>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -1779193308279798570L;
  
  /* ********************************************** Methods ********************************************** */
  @Override
  public EventListenerRegistrationAsynchronous<EVENT, RESULT> getEventListenerRegistration()
  {
    return new EventListenerRegistrationAsynchronousImpl<EVENT, RESULT>( super.getEventListenerRegistration() );
  }
  
}

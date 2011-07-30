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

import org.omnaest.utils.listener.ListenerManager;
import org.omnaest.utils.listener.concrete.ListenerManagerImpl;

/**
 * @see ListenerManagerAsynchronous
 * @see ListenerManager
 * @see ListenerManagerImpl
 * @see ListenerRegistrationAsynchronous
 * @author Omnaest
 * @param <EVENT>
 * @param <RESULT>
 */
public class ListenerManagerAsynchronousImpl<EVENT, RESULT> extends ListenerManagerImpl<Future<EVENT>, Future<RESULT>>
                                                                                                                      implements
                                                                                                                      ListenerManagerAsynchronous<EVENT, RESULT>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -1779193308279798570L;
  
  /* ********************************************** Methods ********************************************** */
  @Override
  public ListenerRegistrationAsynchronous<EVENT, RESULT> getListenerRegistration()
  {
    return new ListenerRegistrationAsynchronous<EVENT, RESULT>( super.getListenerRegistration() );
  }
  
}

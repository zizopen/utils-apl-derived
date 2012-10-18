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

public interface EventListenerRegistration<EVENT, RESULT>
{
  
  /**
   * Adds a new {@link EventListener} to the handler.
   * 
   * @param listener
   * @return this
   */
  public EventListenerRegistration<EVENT, RESULT> addEventListener( EventListener<EVENT, RESULT> listener );
  
  /**
   * Removes a given {@link EventListener} instance from the handler.
   * 
   * @param listener
   * @return this
   */
  public EventListenerRegistration<EVENT, RESULT> removeEventListener( EventListener<EVENT, RESULT> listener );
  
}

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

import java.util.List;

/**
 * Extended version of the {@link ListenerRegistration} class which allows more control for clients about registered listeners
 * within the respective {@link ListenerManager} instance.
 * 
 * @see ListenerRegistration
 * @author Omnaest
 * @param <PARAMETER>
 * @param <RETURN_INFO>
 */
public class ListenerRegistrationControl<PARAMETER, RETURN_INFO> extends ListenerRegistration<PARAMETER, RETURN_INFO>
{
  /* ********************************************** Methods ********************************************** */
  protected ListenerRegistrationControl( List<Listener<PARAMETER, RETURN_INFO>> listenerList )
  {
    super( listenerList );
  }
  
  /**
   * Removes all listeners from the {@link ListenerRegistration} instance.
   * 
   * @return this
   */
  public ListenerRegistrationControl<PARAMETER, RETURN_INFO> clear()
  {
    //
    this.listenerList.clear();
    
    //
    return this;
  }
  
}

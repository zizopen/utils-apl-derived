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

import java.util.ArrayList;
import java.util.List;

/**
 * Simple abstract implementation of a {@link EventListener} which offers several overwritable methods to handle the event.
 * 
 * @author Omnaest
 */
public abstract class EventListenerBasic<EVENT, RESULT> implements EventListener<EVENT, RESULT>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 1250853029881040746L;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param parameter
   */
  public void handleEventSilently( EVENT parameter )
  {
  }
  
  /**
   * @param parameter
   * @param listenerRegistration
   */
  public void handleEventSilently( EVENT parameter, EventListenerRegistration<EVENT, RESULT> listenerRegistration )
  {
  }
  
  @Override
  public List<RESULT> handleEvent( EVENT parameter )
  {
    //
    List<RESULT> retlist = new ArrayList<RESULT>();
    
    //
    retlist.addAll( this.handleEvent( parameter ) );
    
    //
    this.handleEventSilently( parameter );
    
    // 
    return retlist;
  }
  
}

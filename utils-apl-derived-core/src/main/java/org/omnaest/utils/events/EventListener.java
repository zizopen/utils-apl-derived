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
import java.util.List;

/**
 * Simple listener which allows to handle events.
 * 
 * @param <EVENT>
 * @param <RESULT>
 * @author Omnaest
 */
public interface EventListener<EVENT, RESULT> extends Serializable
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /* ********************************************** Methods ********************************************** */
  /**
   * This method will be called from an {@link EventManager} when events are being fired.
   * 
   * @param event
   * @return map<client,result>
   */
  public List<RESULT> handleEvent( EVENT event );
}

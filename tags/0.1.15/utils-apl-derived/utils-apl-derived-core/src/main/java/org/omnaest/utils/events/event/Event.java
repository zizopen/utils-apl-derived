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
package org.omnaest.utils.events.event;

import org.omnaest.utils.events.EventListener;

/**
 * Generic extended {@link EventListener} event implementation.
 * 
 * @author Omnaest
 * @param <SOURCE>
 * @param <EVENT>
 * @param <DATA>
 */
public class Event<SOURCE, EVENT, DATA>
{
  /* ********************************************** Variables ********************************************** */
  protected SOURCE source = null;
  protected EVENT  event  = null;
  protected DATA   data   = null;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * 
   */
  public Event()
  {
  }
  
  /**
   * @param source
   * @param event
   * @param data
   */
  public Event( SOURCE source, EVENT event, DATA data )
  {
    super();
    this.source = source;
    this.event = event;
    this.data = data;
  }
  
  /**
   * @return
   */
  public SOURCE getSource()
  {
    return this.source;
  }
  
  /**
   * @return
   */
  public EVENT getEvent()
  {
    return this.event;
  }
  
  /**
   * @return
   */
  public DATA getData()
  {
    return this.data;
  }
  
}

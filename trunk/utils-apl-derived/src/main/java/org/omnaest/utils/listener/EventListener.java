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

import java.io.Serializable;
import java.util.List;

import org.omnaest.utils.listener.concrete.EventListenerRegistrationImpl;

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
  
  /**
   * Generic default {@link EventListener} event result implementation.
   */
  public static class ListenerExtendedResult<CLIENT, RESULT> implements Serializable
  {
    /* ********************************************** Constants ********************************************** */
    private static final long serialVersionUID = -8069531864203403670L;
    
    /* ********************************************** Variables ********************************************** */
    protected CLIENT          client           = null;
    protected RESULT          result           = null;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * 
     */
    public ListenerExtendedResult()
    {
    }
    
    /**
     * @param client
     * @param result
     */
    public ListenerExtendedResult( CLIENT client, RESULT result )
    {
      super();
      this.client = client;
      this.result = result;
    }
    
    /**
     * @return
     */
    public CLIENT getClient()
    {
      return this.client;
    }
    
    /**
     * @return
     */
    public RESULT getResult()
    {
      return this.result;
    }
    
  }
  
  /**
   * Generic extended {@link EventListener} event implementation.
   * 
   * @author Omnaest
   * @param <SOURCE>
   * @param <EVENT>
   * @param <DATA>
   */
  public static class ListenerExtendedEvent<SOURCE, EVENT, DATA>
  {
    /* ********************************************** Variables ********************************************** */
    protected SOURCE source = null;
    protected EVENT  event  = null;
    protected DATA   data   = null;
    
    /* ********************************************** Methods ********************************************** */
    /**
     * 
     */
    public ListenerExtendedEvent()
    {
    }
    
    /**
     * @param source
     * @param event
     * @param data
     */
    public ListenerExtendedEvent( SOURCE source, EVENT event, DATA data )
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
  
  /* ********************************************** Methods ********************************************** */
  /**
   * Is triggered if a event comes in from a given source. The listenerRegistration parameter can be used to modify the underlying
   * {@link EventListenerRegistrationImpl} instance.
   * 
   * @param source
   * @param event
   * @param data
   * @param listenerRegistration
   * @return map<client,result>
   */
  public List<RESULT> handleEvent( EVENT parameter, EventListenerRegistration<EVENT, RESULT> listenerRegistration );
}

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
 * Simple listener which allows to handle events.
 * 
 * @param <PARAMETER>
 * @param <RETURN_INFO>
 * @author Omnaest
 */
public interface Listener<PARAMETER, RETURN_INFO>
{
  /* ********************************************** Classes/Interfaces ********************************************** */

  /**
   * Generic default {@link Listener} return info implementation.
   */
  public static class ListenerReturnInfo<CLIENT, RESULT>
  {
    /* ********************************************** Variables ********************************************** */
    protected CLIENT client = null;
    protected RESULT result = null;
    
    /* ********************************************** Methods ********************************************** */

    public ListenerReturnInfo()
    {
    }
    
    public ListenerReturnInfo( CLIENT client, RESULT result )
    {
      super();
      this.client = client;
      this.result = result;
    }
    
    public CLIENT getClient()
    {
      return this.client;
    }
    
    public void setClient( CLIENT client )
    {
      this.client = client;
    }
    
    public RESULT getResult()
    {
      return this.result;
    }
    
    public void setResult( RESULT result )
    {
      this.result = result;
    }
    
  }
  
  /**
   * Generic default {@link Listener} parameter implementation.
   * 
   * @author Omnaest
   * @param <SOURCE>
   * @param <EVENT>
   * @param <DATA>
   */
  public static class ListenerParameter<SOURCE, EVENT, DATA>
  {
    /* ********************************************** Variables ********************************************** */
    protected SOURCE source = null;
    protected EVENT  event  = null;
    protected DATA   data   = null;
    
    /* ********************************************** Methods ********************************************** */
    public ListenerParameter()
    {
    }
    
    public ListenerParameter( SOURCE source, EVENT event, DATA data )
    {
      super();
      this.source = source;
      this.event = event;
      this.data = data;
    }
    
    public SOURCE getSource()
    {
      return this.source;
    }
    
    public void setSource( SOURCE source )
    {
      this.source = source;
    }
    
    public EVENT getEvent()
    {
      return this.event;
    }
    
    public void setEvent( EVENT event )
    {
      this.event = event;
    }
    
    public DATA getData()
    {
      return this.data;
    }
    
    public void setData( DATA data )
    {
      this.data = data;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  /**
   * Is triggered if a event comes in from a given source. The listenerRegistration parameter can be used to modify the underlying
   * {@link ListenerRegistration} instance.
   * 
   * @param source
   * @param event
   * @param data
   * @param listenerRegistration
   * @return map<client,result>
   */
  public List<RETURN_INFO> handleEvent( PARAMETER parameter, ListenerRegistration<PARAMETER, RETURN_INFO> listenerRegistration );
}

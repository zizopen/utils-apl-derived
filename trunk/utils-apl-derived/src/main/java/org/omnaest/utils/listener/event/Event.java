package org.omnaest.utils.listener.event;

import org.omnaest.utils.listener.EventListener;

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
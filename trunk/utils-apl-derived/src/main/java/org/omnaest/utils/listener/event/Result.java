package org.omnaest.utils.listener.event;

import java.io.Serializable;

import org.omnaest.utils.listener.EventListener;

/**
 * Generic default {@link EventListener} event result implementation.
 */
public class Result<CLIENT, RESULT> implements Serializable
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
  public Result()
  {
  }
  
  /**
   * @param client
   * @param result
   */
  public Result( CLIENT client, RESULT result )
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
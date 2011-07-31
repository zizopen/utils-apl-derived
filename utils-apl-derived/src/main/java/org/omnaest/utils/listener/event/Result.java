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

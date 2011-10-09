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
package org.omnaest.utils.web;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.omnaest.utils.beans.PropertynameMapToTypeAdapter;

/**
 * A {@link HttpSessionFacadeFactory} creates proxy instances for given types which allows to access the {@link HttpSession}. To
 * operate the {@link HttpSessionFacadeFactory} needs an {@link HttpSessionResolver}. The {@link HttpSession} will be resolved for
 * each {@link #newSessionFacade(Class)} method call.
 * 
 * @see HttpSession
 * @see HttpSessionResolver
 * @author Omnaest
 */
public class HttpSessionFacadeFactory
{
  /* ********************************************** Variables ********************************************** */
  private HttpSessionResolver httpSessionResolver = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param httpSessionResolver
   *          {@link HttpSessionResolver} : must be not null
   */
  public HttpSessionFacadeFactory( HttpSessionResolver httpSessionResolver )
  {
    super();
    this.httpSessionResolver = httpSessionResolver;
  }
  
  /**
   * Creates a new proxy instance for the given {@link Class} type which relies on the {@link HttpSession} attributes
   * 
   * @param type
   * @return
   */
  public <T> T newSessionFacade( Class<T> type )
  {
    //    
    T retval = null;
    
    //
    if ( this.httpSessionResolver != null )
    {
      HttpSession httpSession = this.httpSessionResolver.resolveHttpSession();
      if ( httpSession != null )
      {
        //
        Map<String, Object> httpSessionMap = HttpSessionToMapAdapter.newInstance( httpSession );
        
        //
        retval = PropertynameMapToTypeAdapter.newInstance( httpSessionMap, type );
      }
    }
    
    //
    return retval;
  }
  
}

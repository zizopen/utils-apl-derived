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
package org.omnaest.utils.spring;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.omnaest.utils.web.HttpSessionFacade;
import org.omnaest.utils.web.HttpSessionFacadeFactory;
import org.omnaest.utils.web.HttpSessionToMapAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * The {@link HttpSessionService} allows to create {@link HttpSessionFacade} instances, as well as to resolve the
 * {@link HttpSession} directly or to manipulate it a regular {@link Map}.<br>
 * <br>
 * This Spring service bean should be instantiated as session bean with a given reference to a
 * {@link HttpSessionAndServletRequestResolverService} bean. Using annotation configuration will work since such a service is
 * declared as autowired.
 * 
 * @author Omnaest
 */
@Service
@Scope("session")
public class HttpSessionService
{
  /* ********************************************** Beans / Services / References ********************************************** */
  @Autowired
  protected HttpSessionAndServletRequestResolverService httpSessionAndServletRequestResolverService = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Creates a new {@link HttpSessionFacade} instance for the {@link HttpSession} of the current {@link Thread}
   * 
   * @see HttpSessionFacade
   * @see HttpSessionFacadeFactory
   * @param httpSessionFacadeType
   * @return
   */
  public <F extends HttpSessionFacade> F newHttpSessionFacade( Class<? extends F> httpSessionFacadeType )
  {
    return new HttpSessionFacadeFactory( this.httpSessionAndServletRequestResolverService ).newHttpSessionFacade( httpSessionFacadeType );
  }
  
  /**
   * Resolves the {@link HttpSession}
   * 
   * @return
   */
  public HttpSession resolveHttpSession()
  {
    return this.httpSessionAndServletRequestResolverService.resolveHttpSession();
  }
  
  /**
   * Returns a {@link Map} based view on the current {@link HttpSession}. Changes to the {@link Map} will be reflected by a change
   * of the {@link HttpSession} and vice versa.
   * 
   * @return
   */
  public Map<String, Object> resolveHttpSessionAndReturnAsMapView()
  {
    HttpSession httpSession = this.resolveHttpSession();
    return httpSession != null ? HttpSessionToMapAdapter.newInstance( httpSession ) : null;
  }
  
  /**
   * Resolves an attribute value from the {@link HttpSession} for the given attribute name
   * 
   * @param attributeName
   * @return
   */
  public Object getHttpSessionAttribute( String attributeName )
  {
    return this.resolveHttpSession().getAttribute( attributeName );
  }
  
  /**
   * Sets an attribute value for the given attribute name within the {@link HttpSession}
   * 
   * @param attributeName
   * @param value
   */
  public void setHttpSessionAttribute( String attributeName, Object value )
  {
    this.resolveHttpSession().setAttribute( attributeName, value );
  }
  
}

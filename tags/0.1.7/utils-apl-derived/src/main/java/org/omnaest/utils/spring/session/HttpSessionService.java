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
package org.omnaest.utils.spring.session;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.omnaest.utils.web.HttpSessionFacade;
import org.omnaest.utils.web.HttpSessionFacadeFactory;

/**
 * The {@link HttpSessionService} allows to create {@link HttpSessionFacade} instances, as well as to resolve the
 * {@link HttpSession} directly or to manipulate it a regular {@link Map}.<br>
 * <br>
 * This Spring service bean should be instantiated as session bean with a given reference to a
 * {@link HttpSessionAndServletRequestResolverService} bean. Using annotation configuration will work since such a service is
 * declared as autowired.<br>
 * <br>
 * Spring configuration:
 * 
 * <pre>
 * &lt;context:annotation-config /&gt;
 * &lt;bean class=&quot;org.omnaest.utils.spring.session.implementation.HttpSessionAndServletRequestResolverServiceBean&quot; /&gt;
 * &lt;bean class=&quot;org.omnaest.utils.spring.session.implementation.HttpSessionServiceBean&quot; /&gt;
 * </pre>
 * 
 * @see HttpSession
 * @see HttpSessionFacade
 * @see HttpSessionAndServletRequestResolverService
 * @author Omnaest
 */
public interface HttpSessionService
{
  
  /**
   * Sets an attribute value for the given attribute name within the {@link HttpSession}
   * 
   * @param attributeName
   * @param value
   */
  public void setHttpSessionAttribute( String attributeName, Object value );
  
  /**
   * Resolves an attribute value from the {@link HttpSession} for the given attribute name
   * 
   * @param attributeName
   * @return
   */
  public Object getHttpSessionAttribute( String attributeName );
  
  /**
   * Returns a {@link Map} based view on the current {@link HttpSession}. Changes to the {@link Map} will be reflected by a change
   * of the {@link HttpSession} and vice versa.
   * 
   * @see #resolveHttpSessionAndReturnAsCaseinsensitiveMapView()
   * @return
   */
  public Map<String, Object> resolveHttpSessionAndReturnAsMapView();
  
  /**
   * Resolves the {@link HttpSession}
   * 
   * @see #resolveHttpSessionAndReturnAsMapView()
   * @see #newHttpSessionFacade(Class)
   * @return
   */
  public HttpSession resolveHttpSession();
  
  /**
   * Creates a new {@link HttpSessionFacade} instance for the {@link HttpSession} of the current {@link Thread}
   * 
   * @see HttpSessionFacade
   * @see HttpSessionFacadeFactory
   * @param httpSessionFacadeType
   * @return
   */
  public <F extends HttpSessionFacade> F newHttpSessionFacade( Class<? extends F> httpSessionFacadeType );
  
  /**
   * Resolves the {@link HttpSession} and provides a caseinsensitive {@link Map} view on its attributes
   * 
   * @see #resolveHttpSessionAndReturnAsMapView()
   * @return
   */
  public Map<String, Object> resolveHttpSessionAndReturnAsCaseinsensitiveMapView();
  
}

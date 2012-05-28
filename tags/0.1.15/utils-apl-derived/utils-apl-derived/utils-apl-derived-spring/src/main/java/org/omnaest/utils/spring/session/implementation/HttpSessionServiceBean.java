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
package org.omnaest.utils.spring.session.implementation;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.omnaest.utils.spring.session.HttpSessionAndServletRequestResolverService;
import org.omnaest.utils.spring.session.HttpSessionService;
import org.omnaest.utils.structure.map.decorator.CaseinsensitiveMapDecorator;
import org.omnaest.utils.web.HttpSessionFacade;
import org.omnaest.utils.web.HttpSessionFacadeFactory;
import org.omnaest.utils.web.HttpSessionFacadeFactory.Configuration;
import org.omnaest.utils.web.HttpSessionResolver;
import org.omnaest.utils.web.HttpSessionToMapAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @see HttpSessionService
 * @author Omnaest
 */
@Service
@Scope("session")
public class HttpSessionServiceBean implements HttpSessionService
{
  /* ********************************************** Variables ********************************************** */
  protected Configuration                               httpSessionFacadeConfiguration              = null;
  
  /* ********************************************** Beans / Services / References ********************************************** */
  @Autowired
  protected HttpSessionAndServletRequestResolverService httpSessionAndServletRequestResolverService = null;
  
  /* ********************************************** Methods ********************************************** */
  
  @Override
  public <F extends HttpSessionFacade> F newHttpSessionFacade( Class<? extends F> httpSessionFacadeType )
  {
    return this.newHttpSessionFacade( httpSessionFacadeType, this.httpSessionAndServletRequestResolverService );
    
  }
  
  @Override
  public <F extends HttpSessionFacade> F newHttpSessionFacade( Class<? extends F> httpSessionFacadeType,
                                                               HttpSessionResolver httpSessionResolver )
  {
    return new HttpSessionFacadeFactory( httpSessionResolver ).newHttpSessionFacade( httpSessionFacadeType,
                                                                                     this.httpSessionFacadeConfiguration );
  }
  
  @Override
  public HttpSession resolveHttpSession()
  {
    return this.httpSessionAndServletRequestResolverService.resolveHttpSession();
  }
  
  @Override
  public Map<String, Object> resolveHttpSessionAndReturnAsMapView()
  {
    //
    HttpSession httpSession = this.resolveHttpSession();
    return httpSession != null ? HttpSessionToMapAdapter.newInstance( httpSession ) : null;
  }
  
  @Override
  public Map<String, Object> resolveHttpSessionAndReturnAsCaseinsensitiveMapView()
  {
    //
    Map<String, Object> map = this.resolveHttpSessionAndReturnAsMapView();
    boolean useCacheForKeys = true;
    return new CaseinsensitiveMapDecorator<Object>( map, useCacheForKeys );
  }
  
  @Override
  public Object getHttpSessionAttribute( String attributeName )
  {
    return this.resolveHttpSession().getAttribute( attributeName );
  }
  
  @Override
  public void setHttpSessionAttribute( String attributeName, Object value )
  {
    this.resolveHttpSession().setAttribute( attributeName, value );
  }
  
  @Override
  public void removeHttpSessionAttribute( String attributeName )
  {
    this.resolveHttpSession().removeAttribute( attributeName );
  }
  
  public void setHttpSessionFacadeConfiguration( Configuration httpSessionFacadeConfiguration )
  {
    this.httpSessionFacadeConfiguration = httpSessionFacadeConfiguration;
  }
  
}

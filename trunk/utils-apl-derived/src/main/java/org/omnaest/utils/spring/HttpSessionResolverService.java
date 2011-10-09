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

import javax.servlet.http.HttpSession;

import org.omnaest.utils.web.HttpSessionResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.RequestContextFilter;

/**
 * The {@link HttpSessionResolverService} provides methods to resolve the {@link HttpSession} for a thread. It also can be
 * injected as {@link Service}. It is based on the {@link RequestContextHolder} of Spring.
 * 
 * @see #resolveHttpSession()
 * @author Omnaest
 */
public class HttpSessionResolverService implements HttpSessionResolver
{
  /**
   * Resolves the {@link HttpSession} using the spring {@link RequestContextHolder} from the {@link Thread}s request context. This
   * needs the spring {@link RequestContextListener} or {@link RequestContextFilter} to be enabled within the web.xml. Returns
   * null if no {@link HttpSession} exists.
   * 
   * @return
   */
  public HttpSession resolveHttpSession()
  {
    //
    HttpSession session = null;
    
    //
    RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
    if ( requestAttributes instanceof ServletRequestAttributes )
    {
      ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
      session = servletRequestAttributes.getRequest().getSession();
    }
    
    //
    return session;
  }
  
}

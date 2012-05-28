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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.omnaest.utils.spring.session.HttpSessionAndServletRequestResolverService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @see HttpSessionAndServletRequestResolverService
 * @author Omnaest
 */
@Service
public class HttpSessionAndServletRequestResolverServiceBean implements HttpSessionAndServletRequestResolverService
{
  
  @Override
  public HttpSession resolveHttpSession()
  {
    //
    HttpSession session = null;
    
    //
    HttpServletRequest httpServletRequest = this.resolveHttpServletRequest();
    if ( httpServletRequest != null )
    {
      session = httpServletRequest.getSession();
    }
    
    //
    return session;
  }
  
  @Override
  public HttpServletRequest resolveHttpServletRequest()
  {
    //
    HttpServletRequest request = null;
    
    //
    RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
    if ( requestAttributes instanceof ServletRequestAttributes )
    {
      ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
      request = servletRequestAttributes.getRequest();
    }
    
    // 
    return request;
  }
  
}

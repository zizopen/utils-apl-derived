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
package org.omnaest.utils.spring.session.threadcontext.internal;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.omnaest.utils.spring.session.implementation.HttpSessionAndServletRequestResolverServiceBean;
import org.omnaest.utils.spring.session.threadcontext.RequestContextAwareCallableDecorator;
import org.omnaest.utils.spring.session.threadcontext.RequestContextAwareRunnableDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @see RequestContextAwareRunnableDecorator
 * @see RequestContextAwareCallableDecorator
 * @author Omnaest
 */
public class RequestContextManagerForCallableAndRunnableDecorator implements Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long                                     serialVersionUID                                = 2050852221752364667L;
  /* ********************************************** Variables ********************************************** */
  private final HttpServletRequest                              httpServletRequest;
  private final boolean                                         inheritable;
  
  /* ********************************************** Beans / Services / References ********************************************** */
  private final HttpSessionAndServletRequestResolverServiceBean httpSessionAndServletRequestResolverServiceBean = new HttpSessionAndServletRequestResolverServiceBean();
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see RequestContextManagerForCallableAndRunnableDecorator
   * @param inheritable
   */
  public RequestContextManagerForCallableAndRunnableDecorator( boolean inheritable )
  {
    super();
    this.inheritable = inheritable;
    this.httpServletRequest = this.httpSessionAndServletRequestResolverServiceBean.resolveHttpServletRequest();
  }
  
  /**
   * @see RequestContextManagerForCallableAndRunnableDecorator
   */
  public RequestContextManagerForCallableAndRunnableDecorator()
  {
    this( false );
  }
  
  /**
   * Sets the {@link RequestAttributes} which have been resolved at constructor creation time and returns already available
   * {@link RequestAttributes}.
   * 
   * @return
   */
  public RequestAttributes setRequestAttributesResolvedAtCreationTime()
  {
    return this.setRequestAttributes( new ServletRequestAttributes( this.httpServletRequest ) );
  }
  
  /**
   * Sets the given {@link RequestAttributes} and returns any previously set instance of {@link RequestAttributes}
   * 
   * @return
   */
  public RequestAttributes setRequestAttributes( final RequestAttributes requestAttributes )
  {
    //
    final RequestAttributes retval = RequestContextHolder.getRequestAttributes();
    RequestContextHolder.setRequestAttributes( requestAttributes, this.inheritable );
    
    // 
    return retval;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "RequestContextManagerForCallableAndRunnableDecorator [httpServletRequest=" );
    builder.append( this.httpServletRequest );
    builder.append( ", inheritable=" );
    builder.append( this.inheritable );
    builder.append( "]" );
    return builder.toString();
  }
  
}

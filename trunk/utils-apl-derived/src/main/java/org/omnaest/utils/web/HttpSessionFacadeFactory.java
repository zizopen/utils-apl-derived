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

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.omnaest.utils.beans.adapter.PropertyAccessOption;
import org.omnaest.utils.beans.adapter.PropertynameMapToTypeAdapter;
import org.omnaest.utils.beans.adapter.source.PropertyNameTemplate;
import org.omnaest.utils.beans.adapter.source.SourcePropertyAccessorDecorator;
import org.omnaest.utils.proxy.handler.MethodInvocationHandlerDecorator;
import org.omnaest.utils.structure.element.converter.Converter;

/**
 * A {@link HttpSessionFacadeFactory} creates proxy instances for given types which allows to access the {@link HttpSession}. To
 * operate the {@link HttpSessionFacadeFactory} needs an {@link HttpSessionResolver}. The {@link HttpSession} will be resolved for
 * each {@link #newHttpSessionFacade(Class)} method call. <br>
 * <br>
 * <br>
 * For examples and supported {@link Annotation}s see {@link HttpSessionFacade}s.<br>
 * 
 * @see HttpSessionFacade
 * @see HttpSession
 * @see HttpSessionResolver
 * @see Converter
 * @see PropertyNameTemplate
 * @author Omnaest
 */
public class HttpSessionFacadeFactory
{
  /* ********************************************** Variables ********************************************** */
  protected HttpSessionResolver httpSessionResolver = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see PropertynameMapToTypeAdapter.Configuration
   * @author Omnaest
   */
  public static class Configuration extends PropertynameMapToTypeAdapter.Configuration
  {
    
    /**
     * @see Configuration
     */
    public Configuration()
    {
      super();
    }
    
    /**
     * @see Configuration
     * @param underlyingMapAware
     * @param simulatingToString
     */
    public Configuration( boolean underlyingMapAware, boolean simulatingToString )
    {
      super( underlyingMapAware, simulatingToString );
    }
    
    /**
     * @see Configuration
     * @param interfaces
     */
    public Configuration( Class<?>[] interfaces )
    {
      super( interfaces );
    }
    
    /**
     * @see Configuration
     * @param methodInvocationHandlerDecorators
     * @param sourcePropertyAccessorDecorators
     */
    public Configuration( MethodInvocationHandlerDecorator[] methodInvocationHandlerDecorators,
                          SourcePropertyAccessorDecorator[] sourcePropertyAccessorDecorators )
    {
      super( methodInvocationHandlerDecorators, sourcePropertyAccessorDecorators );
    }
    
    /**
     * @see Configuration
     * @param propertyAccessOption
     * @param isRegardingAdapterAnnotation
     * @param isRegardingPropertyNameTemplateAnnotation
     * @param isRegardingDefaultValueAnnotation
     * @param underlyingMapAware
     * @param simulatingToString
     */
    public Configuration( PropertyAccessOption propertyAccessOption, boolean isRegardingAdapterAnnotation,
                          boolean isRegardingPropertyNameTemplateAnnotation, boolean isRegardingDefaultValueAnnotation,
                          boolean underlyingMapAware, boolean simulatingToString )
    {
      super( propertyAccessOption, isRegardingAdapterAnnotation, isRegardingPropertyNameTemplateAnnotation,
             isRegardingDefaultValueAnnotation, underlyingMapAware, simulatingToString );
    }
    
  }
  
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
  public <T extends HttpSessionFacade> T newHttpSessionFacade( Class<T> type )
  {
    Configuration configuration = null;
    return this.newHttpSessionFacade( type, configuration );
  }
  
  /**
   * Creates a new proxy instance for the given {@link Class} type which relies on the {@link HttpSession} attributes.<br>
   * <br>
   * 
   * @see Configuration
   * @param type
   * @param configuration
   *          : if null a default configuration is used
   * @return
   */
  public <T extends HttpSessionFacade> T newHttpSessionFacade( Class<T> type, Configuration configuration )
  {
    //    
    T retval = null;
    
    //
    if ( configuration == null )
    {
      //
      PropertyAccessOption propertyAccessOption = PropertyAccessOption.PROPERTY;
      boolean isRegardingAdapterAnnotation = true;
      boolean underlyingMapAware = true;
      boolean isRegardingPropertyNameTemplateAnnotation = true;
      boolean simulatingToString = true;
      boolean isRegardingDefaultValueAnnotation = true;
      configuration = new Configuration( propertyAccessOption, isRegardingAdapterAnnotation,
                                         isRegardingPropertyNameTemplateAnnotation, isRegardingDefaultValueAnnotation,
                                         underlyingMapAware, simulatingToString );
    }
    
    //
    if ( this.httpSessionResolver != null )
    {
      //
      HttpSession httpSession = this.httpSessionResolver.resolveHttpSession();
      if ( httpSession != null )
      {
        //
        Map<String, Object> httpSessionMap = HttpSessionToMapAdapter.newInstance( httpSession );
        
        retval = PropertynameMapToTypeAdapter.newInstance( httpSessionMap, type, configuration );
      }
    }
    
    //
    return retval;
  }
}

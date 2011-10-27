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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.beans.PropertynameMapToTypeAdapter;
import org.omnaest.utils.structure.map.DualMap;
import org.omnaest.utils.structure.map.LinkedHashDualMap;
import org.omnaest.utils.structure.map.MapWithKeyMappingAdapter;

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
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Allows to declare the name of the accessed attribute of the {@link HttpSession}. It is only necessary to annotate at least
   * one setter or getter of the same property, but it is not necessary to annotate both of them.
   * 
   * @author Omnaest
   */
  @Documented
  @Retention(value = RetentionPolicy.RUNTIME)
  @Target({ ElementType.METHOD })
  public @interface AttributeName
  {
    public String value();
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
  
  protected <T> DualMap<String, String> determinePropertyNameToSessionAttributeNameMap( Class<T> type )
  {
    //
    DualMap<String, String> propertyNameToSessionAttributeNameMap = new LinkedHashDualMap<String, String>();
    
    //
    Map<String, AttributeName> propertyNameToBeanPropertyAnnotationMap = BeanUtils.propertyNameToBeanPropertyAnnotationMap( type,
                                                                                                                            AttributeName.class );
    for ( String propertyName : propertyNameToBeanPropertyAnnotationMap.keySet() )
    {
      //
      AttributeName attributeName = propertyNameToBeanPropertyAnnotationMap.get( propertyName );
      
      //
      String sessionAttributeName = propertyName;
      
      //
      String value = null;
      if ( attributeName != null && ( value = attributeName.value() ) != null )
      {
        sessionAttributeName = value;
      }
      
      //
      propertyNameToSessionAttributeNameMap.put( propertyName, sessionAttributeName );
    }
    
    //
    return propertyNameToSessionAttributeNameMap;
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
      //
      HttpSession httpSession = this.httpSessionResolver.resolveHttpSession();
      if ( httpSession != null )
      {
        //
        Map<String, Object> httpSessionMap = HttpSessionToMapAdapter.newInstance( httpSession );
        
        //
        DualMap<String, String> propertyNameToSessionAttributeNameMap = this.determinePropertyNameToSessionAttributeNameMap( type );
        
        MapWithKeyMappingAdapter<String, String, Object> httpSessionMapWithKeyMapping = new MapWithKeyMappingAdapter<String, String, Object>(
                                                                                                                                              httpSessionMap,
                                                                                                                                              propertyNameToSessionAttributeNameMap.invert() );
        
        //
        retval = PropertynameMapToTypeAdapter.newInstance( httpSessionMapWithKeyMapping, type, true, true );
      }
    }
    
    //
    return retval;
  }
  
}

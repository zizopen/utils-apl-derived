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
import org.omnaest.utils.structure.element.converter.Converter;

/**
 * A {@link HttpSessionFacadeFactory} creates proxy instances for given types which allows to access the {@link HttpSession}. To
 * operate the {@link HttpSessionFacadeFactory} needs an {@link HttpSessionResolver}. The {@link HttpSession} will be resolved for
 * each {@link #newSessionFacade(Class)} method call. <br>
 * <br>
 * <br>
 * The {@link HttpSessionFacadeFactory} supports following {@link Annotation}s:<br>
 * <ul>
 * <li>{@link Converter}</li>
 * <li>{@link PropertyNameTemplate}</li>
 * </ul>
 * <br>
 * <br>
 * An example of an interface put on top of a {@link HttpSession} can look like:
 * 
 * <pre>
 * public static interface HttpSessionFacadeExample
 * {
 *   public void setFieldString( String field );
 *   
 *   public String getFieldString();
 *   
 *   public void setFieldDouble( Double fieldDouble );
 *   
 *   public Double getFieldDouble();
 *   
 *   &#064;PropertyNameTemplate(&quot;OTHERFIELD&quot;)
 *   public String getOtherField();
 *   
 *   &#064;Adapter(type = ElementConverterIdentity.class)
 *   public void setOtherField( String value );
 * }
 * </pre>
 * 
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
      //
      HttpSession httpSession = this.httpSessionResolver.resolveHttpSession();
      if ( httpSession != null )
      {
        //
        Map<String, Object> httpSessionMap = HttpSessionToMapAdapter.newInstance( httpSession );
        
        //
        PropertyAccessOption propertyAccessOption = null;
        boolean isRegardingAdapterAnnotation = true;
        boolean underlyingMapAware = true;
        boolean isRegardingPropertyNameTemplate = true;
        boolean simulatingToString = true;
        retval = PropertynameMapToTypeAdapter.newInstance( httpSessionMap,
                                                           type,
                                                           new PropertynameMapToTypeAdapter.Configuration(
                                                                                                           propertyAccessOption,
                                                                                                           isRegardingAdapterAnnotation,
                                                                                                           isRegardingPropertyNameTemplate,
                                                                                                           underlyingMapAware,
                                                                                                           simulatingToString ) );
      }
    }
    
    //
    return retval;
  }
  
}

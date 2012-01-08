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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.collections.EnumerationUtils;
import org.omnaest.utils.structure.map.MapAbstract;

/**
 * Adapter for an {@link HttpSession} which allows direct access using a regular {@link Map} interface.
 * 
 * @author Omnaest
 */
public class HttpSessionToMapAdapter extends MapAbstract<String, Object>
{
  /* ********************************************** Variables ********************************************** */
  private HttpSession httpSession = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param httpSession
   *          {@link HttpSession}
   */
  protected HttpSessionToMapAdapter( HttpSession httpSession )
  {
    super();
    this.httpSession = httpSession;
  }
  
  /**
   * Creates a new {@link Map} instance based on the given {@link HttpSession}. If no {@link HttpSession} is given, null is
   * returned.
   * 
   * @param httpSession
   * @return
   */
  public static Map<String, Object> newInstance( HttpSession httpSession )
  {
    return httpSession == null ? null : new HttpSessionToMapAdapter( httpSession );
  }
  
  @Override
  public Object get( Object key )
  {
    return key instanceof String ? this.httpSession.getAttribute( (String) key ) : null;
  }
  
  @Override
  public Object put( String key, Object value )
  {
    //
    Object retval = this.get( key );
    
    //
    this.httpSession.setAttribute( key, value );
    
    //
    return retval;
  }
  
  @Override
  public Object remove( Object key )
  {
    //
    Object retval = this.get( key );
    
    //
    if ( key instanceof String )
    {
      this.httpSession.removeAttribute( (String) key );
    }
    
    // 
    return retval;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Set<String> keySet()
  {
    return new LinkedHashSet<String>( EnumerationUtils.toList( this.httpSession.getAttributeNames() ) );
  }
  
  @Override
  public Collection<Object> values()
  {
    //
    List<Object> retlist = new ArrayList<Object>();
    
    //
    for ( String key : this.keySet() )
    {
      retlist.add( this.get( key ) );
    }
    
    //
    return retlist;
  }
  
}

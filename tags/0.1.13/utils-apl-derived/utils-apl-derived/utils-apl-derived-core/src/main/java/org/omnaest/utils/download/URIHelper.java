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
package org.omnaest.utils.download;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

public class URIHelper
{
  /**
   * Creates a new uri instance with the given parameters.
   * 
   * @see URI#URI(String, String, String, String, String))
   * @param scheme
   *          : e.g. "http"
   * @param host
   *          : e.g. "www.google.de"
   * @param path
   *          : e.g. "search/subsearch"
   * @param queryParameters
   *          : e.g. "key1=value1", "key2=value2"
   * @return {@link URI}
   */
  public static URI createURI( String scheme, String host, String path, String... queryParameters )
  {
    //
    URI uri = null;
    
    //
    try
    {
      //
      uri = new URI( scheme, host, path.startsWith( "/" ) ? path : "/" + path, StringUtils.join( queryParameters, "&" ), null );
    }
    catch ( URISyntaxException e )
    {
      e.printStackTrace();
    }
    
    //
    return uri;
  }
  
  /**
   * Creates a new {@link URI} based on the given location
   * 
   * @param location
   * @return
   */
  public static URI createUri( String location )
  {
    //    
    URI retval = null;
    
    //
    if ( location != null )
    {
      try
      {
        retval = new URI( location );
      }
      catch ( URISyntaxException e )
      {
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Creates a new {@link Uri} which is based on the given base address and a relative path
   * 
   * @param baseAddress
   * @param relativePath
   * @return
   */
  public static URI createUri( URI baseAddress, String relativePath )
  {
    //
    URI retval = null;
    
    //
    if ( baseAddress != null && relativePath != null )
    {
      try
      {
        //
        String baseAddressAsString = baseAddress.toString();
        if ( !baseAddressAsString.endsWith( "/" ) )
        {
          baseAddress = new URI( baseAddress.toString() + "/" );
        }
        
        //
        retval = baseAddress.normalize().resolve( relativePath ).normalize();
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns an url for a given uri if possible. Otherwise null is returned.
   * 
   * @param uri
   * @return
   */
  public static URL getURLfromURI( URI uri )
  {
    //
    URL url = null;
    
    //
    try
    {
      url = uri.toURL();
    }
    catch ( MalformedURLException e )
    {
    }
    
    //
    return url;
  }
}

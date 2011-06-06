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

public class URIHelper
{
  /**
   * Creates a new uri instance with the given parameters.
   * 
   * @see URI#URI(String, String, String, String, String))
   * @param scheme
   * @param host
   * @param path
   * @param query
   * @return
   */
  public static URI createURI( String scheme, String host, String path, String query )
  {
    URI uri = null;
    try
    {
      uri = new URI( scheme, host, path, query, null );
    }
    catch ( URISyntaxException e )
    {
    }
    return uri;
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

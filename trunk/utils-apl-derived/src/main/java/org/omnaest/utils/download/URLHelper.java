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
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.omnaest.utils.structure.map.MapUtils;
import org.omnaest.utils.structure.map.MapUtils.MapEntryToElementTransformer;

/**
 * Helper for {@link URL} related actions
 * 
 * @author Omnaest
 */
public class URLHelper
{
  /**
   * Creates a new url. Returns null if any exception occurs.
   * 
   * @param urlStr
   * @return
   */
  public static URL createURL( String urlStr )
  {
    URL url = null;
    try
    {
      url = new URL( urlStr );
    }
    catch ( MalformedURLException e )
    {
    }
    
    return url;
  }
  
  /**
   * @param scheme
   * @param host
   * @param path
   * @param queryMap
   * @return
   */
  public static URL createUrl( String scheme, String host, String path, Map<String, String> queryMap )
  {
    //
    URL retval = null;
    
    //
    try
    {
      //    
      List<String> queryList = MapUtils.toList( queryMap, new MapEntryToElementTransformer<String, String, String>()
      {
        @Override
        public String transform( Entry<String, String> entry )
        {
          return entry.getKey() + "=" + entry.getValue();
        }
      } );
      
      //
      URI uri = URIHelper.createURI( scheme, host, path, queryList.toArray( new String[0] ) );
      
      //
      retval = uri.toURL();
    }
    catch ( Exception e )
    {
    }
    
    //
    return retval;
  }
}

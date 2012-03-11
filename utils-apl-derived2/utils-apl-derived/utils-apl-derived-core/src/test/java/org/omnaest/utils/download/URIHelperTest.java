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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;

import org.junit.Test;

/**
 * @see URIHelper
 * @author Omnaest
 */
public class URIHelperTest
{
  
  @Test
  public void testCreateURI()
  {
    //
    String scheme = "http";
    String host = "www.yahoo.de";
    String path = "finance";
    String queryParameter1 = "q=1";
    String queryParameter2 = "s=2";
    
    //
    URI uri = URIHelper.createURI( scheme, host, path, queryParameter1, queryParameter2 );
    assertNotNull( uri );
    assertEquals( "http://www.yahoo.de/finance?q=1&s=2", uri.toString() );
  }
  
  @Test
  public void testCreateUri()
  {
    //
    {
      //
      String relativePath = "someRelative/path";
      String location = "http://localhost:8080/webapp";
      URI baseAddress = URIHelper.createUri( location );
      
      URI uri = URIHelper.createUri( baseAddress, relativePath );
      assertNotNull( uri );
      assertEquals( location + "/" + relativePath, uri.toString() );
    }
    {
      //
      String relativePath = "someRelative/path";
      String location = "http://localhost:8080/webapp/";
      URI baseAddress = URIHelper.createUri( location );
      
      URI uri = URIHelper.createUri( baseAddress, relativePath );
      assertNotNull( uri );
      assertEquals( location + relativePath, uri.toString() );
    }
    
  }
}

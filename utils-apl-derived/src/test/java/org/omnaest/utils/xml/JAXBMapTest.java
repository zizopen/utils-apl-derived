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
package org.omnaest.utils.xml;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class JAXBMapTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  @Test
  public void testNewInstance()
  {
    //
    Map<String, String> map = new HashMap<String, String>();
    map.put( "key1", "value1" );
    map.put( "key2", "value2" );
    map.put( "key3", "value3" );
    
    //
    JAXBMap<String, String> jaxbMap = JAXBMap.newInstance( map );
    assertEquals( map, jaxbMap );
    
    //
    String xmlContent = XMLHelper.storeObjectAsXML( jaxbMap );
    //System.out.println( xmlContent );
    
    //
    @SuppressWarnings("unchecked")
    Map<String, String> jaxbMapLoaded = XMLHelper.loadObjectFromXML( xmlContent, JAXBMap.class );
    assertEquals( map, jaxbMapLoaded );
  }
  
}

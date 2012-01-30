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
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class JAXBCollectionTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  @Test
  public void testNewInstance()
  {
    //
    List<String> list = new ArrayList<String>();
    list.add( "value1" );
    list.add( "value2" );
    list.add( "value3" );
    
    //
    JAXBCollection<String> jaxbCollection = JAXBCollection.newInstance( list );
    
    //
    String xmlContent = JAXBXMLHelper.storeObjectAsXML( jaxbCollection );
    assertNotNull( xmlContent );
    //System.out.println( xmlContent );
    
    //
    assertEquals( list, new ArrayList<String>( JAXBXMLHelper.cloneObject( jaxbCollection ) ) );
  }
  
}

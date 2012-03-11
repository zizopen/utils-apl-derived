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

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class JAXBSetTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  @Test
  public void testNewInstance()
  {
    //
    Set<String> set = new HashSet<String>();
    set.add( "value1" );
    set.add( "value2" );
    set.add( "value3" );
    
    //
    JAXBSet<String> jaxbSet = JAXBSet.newInstance( set );
    
    //
    String xmlContent = JAXBXMLHelper.storeObjectAsXML( jaxbSet );
    assertNotNull( xmlContent );
    
    //
    assertEquals( set, new HashSet<String>( JAXBXMLHelper.cloneObject( jaxbSet ) ) );
  }
}

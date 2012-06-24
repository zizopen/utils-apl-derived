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
package org.omnaest.utils.structure.map.decorator;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.structure.map.AssertContract;
import org.omnaest.utils.xml.JAXBXMLHelper;

/**
 * @see MapDecorator
 * @author Omnaest
 */
public class MapDecoratorTest
{
  /* ********************************************** Variables ********************************************** */
  protected Map<String, Double>          map          = new LinkedHashMap<String, Double>();
  protected MapDecorator<String, Double> mapDecorator = new MapDecorator<String, Double>( this.map );
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    this.map.put( "key1", 1.34 );
  }
  
  @Test
  public void testMapContract()
  {
    //
    Map<String, Double> testDataMap = new LinkedHashMap<String, Double>();
    testDataMap.put( "abc", 1.234 );
    testDataMap.put( "def", 3.456 );
    testDataMap.put( "ghi", 3.456 );
    
    AssertContract.assertMapContract( this.map, testDataMap );
  }
  
  @Test
  public void testJAXBCompliance()
  {
    //
    String objectAsXML = JAXBXMLHelper.storeObjectAsXML( this.mapDecorator );
    //System.out.println( objectAsXML );
    
    //
    @SuppressWarnings("unchecked")
    Map<String, Double> restoredMap = JAXBXMLHelper.loadObjectFromXML( objectAsXML, MapDecorator.class );
    
    //
    assertEquals( this.map, this.mapDecorator );
    assertEquals( this.map, restoredMap );
    
  }
}

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
package org.omnaest.utils.structure.map;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.omnaest.utils.xml.JAXBXMLHelper;

/**
 * @see MapDecorator
 * @author Omnaest
 */
public class MapDecoratorTest
{
  
  @Test
  public void testJAXBCompliance()
  {
    //
    Map<String, Double> map = new LinkedHashMap<String, Double>();
    map.put( "key1", 1.34 );
    
    //
    MapDecorator<String, Double> mapDecorator = new MapDecorator<String, Double>( map );
    
    //
    String objectAsXML = JAXBXMLHelper.storeObjectAsXML( mapDecorator );
    //System.out.println( objectAsXML );
    
    //
    @SuppressWarnings("unchecked")
    Map<String, Double> restoredMap = JAXBXMLHelper.loadObjectFromXML( objectAsXML, MapDecorator.class );
    
    //
    assertEquals( map, mapDecorator );
    assertEquals( map, restoredMap );
    
  }
  
}

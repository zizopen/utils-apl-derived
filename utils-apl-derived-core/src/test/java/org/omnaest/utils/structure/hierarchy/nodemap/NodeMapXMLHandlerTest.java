/*******************************************************************************
 * Copyright 2013 Danny Kunz
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
package org.omnaest.utils.structure.hierarchy.nodemap;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class NodeMapXMLHandlerTest
{
  
  @Test
  public void testConvertToXML()
  {
    NodeMapXMLHandler nodeMapXMLHandler = new NodeMapXMLHandler();
    NodeMap<String, Map<String, String>> nodeMap = new NodeMapImpl<String, Map<String, String>>();
    {
      NodeMap<String, Map<String, String>> subNodeMap = new NodeMapImpl<String, Map<String, String>>();
      {
        Map<String, String> model = new LinkedHashMap<String, String>();
        model.put( "key1", "value1" );
        model.put( "key2", "value2" );
        subNodeMap.setModel( model );
      }
      nodeMap.put( "subtag", subNodeMap );
    }
    String rootTagName = "root";
    String xml = nodeMapXMLHandler.convertToXML( rootTagName, nodeMap );
    System.out.println( xml );
  }
  
}

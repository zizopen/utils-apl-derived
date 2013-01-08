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

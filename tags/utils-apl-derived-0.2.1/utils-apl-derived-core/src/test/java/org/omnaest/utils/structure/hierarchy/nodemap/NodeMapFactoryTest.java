/*******************************************************************************
 * Copyright 2012 Danny Kunz
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.omnaest.utils.structure.map.MapUtils;

/**
 * @see NodeMapFactory
 * @author Omnaest
 */
public class NodeMapFactoryTest
{
  
  protected static class TestBeanRoot
  {
    private String   fieldString;
    private TestBean testBean;
    
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
    public TestBean getTestBean()
    {
      return this.testBean;
    }
    
    public void setTestBean( TestBean testBean )
    {
      this.testBean = testBean;
    }
  }
  
  protected static class TestBean
  {
    private String fieldString;
    private double fieldDouble;
    
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
    public double getFieldDouble()
    {
      return this.fieldDouble;
    }
    
    public void setFieldDouble( double fieldDouble )
    {
      this.fieldDouble = fieldDouble;
    }
  }
  
  @Test
  public void testNewNodeMapFromObject() throws Exception
  {
    TestBeanRoot testBeanRoot = new TestBeanRoot();
    testBeanRoot.setFieldString( "valueRoot" );
    TestBean testBean = new TestBean();
    {
      testBean.setFieldDouble( 1.45 );
      testBean.setFieldString( "value" );
    }
    testBeanRoot.setTestBean( testBean );
    
    NodeMap<String, Map<String, Object>> nodeMap = NodeMapFactory.newNodeMap( testBeanRoot );
    assertEquals( "valueRoot", nodeMap.getModel().get( "fieldString" ) );
    assertTrue( nodeMap.containsKey( "testBean" ) );
    
    Map<String, Object> testBeanModel = nodeMap.get( "testBean" ).getModel();
    assertEquals( "value", testBeanModel.get( "fieldString" ) );
    assertEquals( 1.45, (Double) testBeanModel.get( "fieldDouble" ), 0.01 );
    
    //System.out.println( nodeMap );
  }
  
  @Test
  public void testNewNodeMapForNestedMaps() throws Exception
  {
    Map<String, Object> rootMap = new LinkedHashMap<String, Object>();
    rootMap.put( "field1", "value1" );
    rootMap.put( "subMap", MapUtils.builder().put( "field1", "value2" ).buildAs().linkedHashMap() );
    
    NodeMap<String, Map<String, Object>> nodeMap = NodeMapFactory.newNodeMap( rootMap );
    assertEquals( "value1", nodeMap.getModel().get( "field1" ) );
    assertTrue( nodeMap.containsKey( "subMap" ) );
    
    Map<String, Object> testBeanModel = nodeMap.get( "subMap" ).getModel();
    assertEquals( "value2", testBeanModel.get( "field1" ) );
    
    //System.out.println( nodeMap );
  }
}

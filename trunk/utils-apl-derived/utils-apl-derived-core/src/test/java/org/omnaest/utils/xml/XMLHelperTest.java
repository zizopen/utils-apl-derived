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
package org.omnaest.utils.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Test;

/**
 * @see XMLHelper
 * @author Omnaest
 */
public class XMLHelperTest
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  protected static class TestEntity
  {
    @XmlAttribute
    private String     attribute1;
    
    @XmlAttribute
    private String     attribute2;
    
    @XmlElement
    private String     field1;
    
    @XmlElement
    private String     field2;
    
    @XmlElement
    private TestEntity testEntity;
    
    /**
     * @return the attribute1
     */
    public String getAttribute1()
    {
      return this.attribute1;
    }
    
    /**
     * @param attribute1
     *          the attribute1 to set
     */
    public void setAttribute1( String attribute1 )
    {
      this.attribute1 = attribute1;
    }
    
    /**
     * @return the attribute2
     */
    public String getAttribute2()
    {
      return this.attribute2;
    }
    
    /**
     * @param attribute2
     *          the attribute2 to set
     */
    public void setAttribute2( String attribute2 )
    {
      this.attribute2 = attribute2;
    }
    
    /**
     * @return the field1
     */
    public String getField1()
    {
      return this.field1;
    }
    
    /**
     * @param field1
     *          the field1 to set
     */
    public void setField1( String field1 )
    {
      this.field1 = field1;
    }
    
    /**
     * @return the field2
     */
    public String getField2()
    {
      return this.field2;
    }
    
    /**
     * @param field2
     *          the field2 to set
     */
    public void setField2( String field2 )
    {
      this.field2 = field2;
    }
    
    /**
     * @return the testEntity
     */
    public TestEntity getTestEntity()
    {
      return this.testEntity;
    }
    
    /**
     * @param testEntity
     *          the testEntity to set
     */
    public void setTestEntity( TestEntity testEntity )
    {
      this.testEntity = testEntity;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "TestEntity [attribute1=" );
      builder.append( this.attribute1 );
      builder.append( ", attribute2=" );
      builder.append( this.attribute2 );
      builder.append( ", field1=" );
      builder.append( this.field1 );
      builder.append( ", field2=" );
      builder.append( this.field2 );
      builder.append( ", testEntity=" );
      builder.append( this.testEntity );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  @SuppressWarnings("unchecked")
  @Test
  public void testNewMapFromXML()
  {
    //
    final TestEntity testEntity = newTestEntity( 1 );
    final String objectAsXML = JAXBXMLHelper.storeObjectAsXML( testEntity );
    
    //
    //System.out.println( objectAsXML );
    
    //    
    Map<String, Object> map = XMLHelper.newMapFromXML( objectAsXML, null );
    assertNotNull( map );
    assertEquals( 1, map.size() );
    assertTrue( map.containsKey( "testEntity" ) );
    
    //
    Object object = map.get( "testEntity" );
    assertTrue( object instanceof Map );
    
    //
    Map<String, Object> testEntityMap = (Map<String, Object>) object;
    assertEquals( 5, testEntityMap.size() );
    assertEquals( "attribute1_1", testEntityMap.get( "attribute1" ) );
    assertEquals( "attribute2_1", testEntityMap.get( "attribute2" ) );
    assertEquals( "field1_1 with \n                    secondline", testEntityMap.get( "field1" ) );
    assertEquals( "field2_1", testEntityMap.get( "field2" ) );
    
    //
    assertTrue( testEntityMap.containsKey( "testEntity" ) );
    Object object2 = testEntityMap.get( "testEntity" );
    assertTrue( object2 instanceof Map );
    
    //
    Map<String, Object> subTestEntityMap = (Map<String, Object>) object2;
    assertEquals( 4, subTestEntityMap.size() );
    assertEquals( "attribute1_0", subTestEntityMap.get( "attribute1" ) );
    assertEquals( "attribute2_0", subTestEntityMap.get( "attribute2" ) );
    assertEquals( "field1_0 with \n                    secondline", subTestEntityMap.get( "field1" ) );
    assertEquals( "field2_0", subTestEntityMap.get( "field2" ) );
    
    //
    //MapUtils.printMapHierarchical( System.out, map );
  }
  
  /**
   * @param level
   * @return
   */
  private static TestEntity newTestEntity( int level )
  {
    //    
    final TestEntity retval = new TestEntity();
    
    //
    final String id = "_" + level;
    retval.setAttribute1( "attribute1" + id );
    retval.setAttribute2( "attribute2" + id );
    retval.setField1( "field1" + id + " with \n                    secondline" );
    retval.setField2( "field2" + id );
    
    final TestEntity testEntity = level > 0 ? newTestEntity( level - 1 ) : null;
    retval.setTestEntity( testEntity );
    
    //
    return retval;
  }
  
}

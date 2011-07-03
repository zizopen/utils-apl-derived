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
package org.omnaest.utils.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * @see TypeToAutowiredPropertyContainerAdapter
 * @author Omnaest
 */
public class TypeToAutowiredPropertyContainerAdapterTest
{
  
  /* ********************************************** Classes/Interfaces ********************************************** */

  /**
   * @see TypeToAutowiredPropertyContainerAdapterTest
   */
  protected static class MockClass
  {
    /* ********************************************** Variables ********************************************** */
    protected String       fieldString  = "default value";
    protected Double       fieldDouble  = 2.5;
    protected Double       fieldDouble2 = 3.5;
    protected MockSubClass mockSubClass = new MockSubClass();
    
    /* ********************************************** Methods ********************************************** */
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
    public Double getFieldDouble()
    {
      return this.fieldDouble;
    }
    
    public void setFieldDouble( Double fieldDouble )
    {
      this.fieldDouble = fieldDouble;
    }
    
    public MockSubClass getMockSubClass()
    {
      return this.mockSubClass;
    }
    
    public void setMockSubClass( MockSubClass mockSubClass )
    {
      this.mockSubClass = mockSubClass;
    }
    
    public Double getFieldDouble2()
    {
      return this.fieldDouble2;
    }
    
    public void setFieldDouble2( Double fieldDouble2 )
    {
      this.fieldDouble2 = fieldDouble2;
    }
    
  }
  
  /**
   * @see MockClass
   * @author Omnaest
   */
  protected static class MockSubClass
  {
    /* ********************************************** Variables ********************************************** */
    protected String fieldString = null;
    protected Double fieldDouble = null;
    
    /* ********************************************** Methods ********************************************** */
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
    public Double getFieldDouble()
    {
      return this.fieldDouble;
    }
    
    public void setFieldDouble( Double fieldDouble )
    {
      this.fieldDouble = fieldDouble;
    }
  }
  
  /* ********************************************** Methods ********************************************** */

  @Test
  public void testNewInstance()
  {
    //
    MockClass mockClass = new MockClass();
    
    //
    AutowiredPropertyContainer autowiredPropertyContainer = TypeToAutowiredPropertyContainerAdapter.newInstance( mockClass );
    assertNotNull( autowiredPropertyContainer );
    
  }
  
  @Test
  public void testGetValueSet()
  {
    //
    MockClass mockClass = new MockClass();
    
    //
    AutowiredPropertyContainer autowiredPropertyContainer = TypeToAutowiredPropertyContainerAdapter.newInstance( mockClass );
    
    //
    Set<Double> valueSet = autowiredPropertyContainer.getValueSet( Double.class );
    assertNotNull( valueSet );
    assertEquals( 2, valueSet.size() );
    assertTrue( valueSet.contains( mockClass.getFieldDouble() ) );
    assertTrue( valueSet.contains( mockClass.getFieldDouble2() ) );
  }
  
  @Test
  public void testGetPropertynameToValueMap()
  {
    //
    MockClass mockClass = new MockClass();
    
    //
    AutowiredPropertyContainer autowiredPropertyContainer = TypeToAutowiredPropertyContainerAdapter.newInstance( mockClass );
    
    //
    Map<String, String> propertynameToValueMap = autowiredPropertyContainer.getPropertynameToValueMap( String.class );
    assertNotNull( propertynameToValueMap );
    assertEquals( 1, propertynameToValueMap.size() );
    assertEquals( mockClass.getFieldString(), propertynameToValueMap.get( "fieldString" ) );
  }
  
  @Test
  public void testPut()
  {
    //
    MockClass mockClass = new MockClass();
    
    //
    AutowiredPropertyContainer autowiredPropertyContainer = TypeToAutowiredPropertyContainerAdapter.newInstance( mockClass );
    
    //
    autowiredPropertyContainer.put( "another value" );
    
    //
    assertEquals( mockClass.getFieldString(), autowiredPropertyContainer.getValue( String.class ) );
  }
  
  @Test
  public void testGetValue()
  {
    //
    MockClass mockClass = new MockClass();
    
    //
    AutowiredPropertyContainer autowiredPropertyContainer = TypeToAutowiredPropertyContainerAdapter.newInstance( mockClass );
    
    //
    assertEquals( mockClass.getFieldString(), autowiredPropertyContainer.getValue( String.class ) );
  }
  
}

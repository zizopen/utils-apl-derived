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
package org.omnaest.utils.beans.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.omnaest.utils.beans.adapter.TypeToPropertynameMapAdapter;
import org.omnaest.utils.beans.adapter.TypeToPropertynameMapAdapter.PropertyAccessOption;

/**
 * @see TypeToPropertynameMapAdapter
 * @author Omnaest
 */
public class TypeToPropertynameMapAdapterTest
{
  /**
   * MockClass TypeToPropertynameMapAdapterTestpAdapterTest
   * 
   * @author Omnaest
   */
  public static class MockBean
  {
    protected String fieldString = null;
    protected Double fieldDouble = null;
    
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
  
  @Test
  public void testNewInstance()
  {
    //
    MockBean mockBean = new MockBean();
    mockBean.setFieldString( "new value" );
    mockBean.setFieldDouble( 1.35 );
    
    //
    Map<String, Object> mapAdapter = TypeToPropertynameMapAdapter.newInstance( mockBean );
    
    //
    assertNotNull( mapAdapter );
    assertEquals( 2, mapAdapter.size() );
    assertEquals( "new value", mapAdapter.get( "fieldString" ) );
    assertEquals( 1.35, mapAdapter.get( "fieldDouble" ) );
  }
  
  @Test
  public void testNewInstanceLowerAndUppercase()
  {
    //
    MockBean mockBean = new MockBean();
    mockBean.setFieldString( "new value" );
    mockBean.setFieldDouble( 1.35 );
    
    //
    Map<String, Object> mapAdapterLowercase = TypeToPropertynameMapAdapter.newInstance( mockBean, PropertyAccessOption.PROPERTY_LOWERCASE );
    Map<String, Object> mapAdapterUppercase = TypeToPropertynameMapAdapter.newInstance( mockBean, PropertyAccessOption.PROPERTY_UPPERCASE );
    
    //
    assertNotNull( mapAdapterLowercase );
    assertEquals( 2, mapAdapterLowercase.size() );
    assertEquals( "new value", mapAdapterLowercase.get( "fieldstring" ) );
    assertEquals( 1.35, mapAdapterLowercase.get( "fielddouble" ) );
    
    assertNotNull( mapAdapterUppercase );
    assertEquals( 2, mapAdapterUppercase.size() );
    assertEquals( "new value", mapAdapterUppercase.get( "FIELDSTRING" ) );
    assertEquals( 1.35, mapAdapterUppercase.get( "FIELDDOUBLE" ) );
  }
  
  @Test
  public void testPut()
  {
    //
    MockBean mockBean = new MockBean();
    mockBean.setFieldString( "new value" );
    mockBean.setFieldDouble( 1.35 );
    
    //
    Map<String, Object> mapAdapter = TypeToPropertynameMapAdapter.newInstance( mockBean );
    
    //
    mapAdapter.put( "fieldString", "other value" );
    mapAdapter.put( "fieldDouble", 6.5 );
    
    //
    assertNotNull( mapAdapter );
    assertEquals( 2, mapAdapter.size() );
    assertEquals( "other value", mapAdapter.get( "fieldString" ) );
    assertEquals( 6.5, mapAdapter.get( "fieldDouble" ) );
  }
  
  @Test
  public void testRemove()
  {
    //
    MockBean mockBean = new MockBean();
    mockBean.setFieldString( "new value" );
    mockBean.setFieldDouble( 1.35 );
    
    //
    Map<String, Object> mapAdapter = TypeToPropertynameMapAdapter.newInstance( mockBean );
    
    //
    mapAdapter.remove( "fieldString" );
    
    //
    assertNotNull( mapAdapter );
    assertEquals( 2, mapAdapter.size() );
    assertNull( mapAdapter.get( "fieldString" ) );
    
  }
  
  @Test
  public void testKeySet()
  {
    //
    MockBean mockBean = new MockBean();
    mockBean.setFieldString( "new value" );
    mockBean.setFieldDouble( 1.35 );
    
    //
    Map<String, Object> mapAdapter = TypeToPropertynameMapAdapter.newInstance( mockBean );
    
    //
    Set<String> keySet = mapAdapter.keySet();
    assertEquals( 2, keySet.size() );
    assertTrue( keySet.contains( "fieldString" ) );
    assertTrue( keySet.contains( "fieldDouble" ) );
  }
  
  @Test
  public void testValues()
  {
    //
    MockBean mockBean = new MockBean();
    mockBean.setFieldString( "new value" );
    mockBean.setFieldDouble( 1.35 );
    
    //
    Map<String, Object> mapAdapter = TypeToPropertynameMapAdapter.newInstance( mockBean );
    
    //
    Collection<Object> values = mapAdapter.values();
    assertEquals( 2, values.size() );
    assertTrue( values.contains( "new value" ) );
    assertTrue( values.contains( 1.35 ) );
  }
  
}

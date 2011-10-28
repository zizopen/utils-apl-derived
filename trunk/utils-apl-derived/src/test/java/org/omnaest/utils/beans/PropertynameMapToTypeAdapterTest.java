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
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.beans.PropertynameMapToTypeAdapter.PropertyAccessOption;
import org.omnaest.utils.structure.element.converter.Adapter;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.map.UnderlyingMapAware;

public class PropertynameMapToTypeAdapterTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  protected static interface TestType
  {
    public Double getFieldDouble();
    
    public void setFieldDouble( Double fieldDouble );
    
    public String getFieldString();
    
    public void setFieldString( String fieldString );
    
  }
  
  protected static class StringToIntegerElementConverter implements ElementConverter<String, Integer>
  {
    
    @Override
    public Integer convert( String element )
    {
      return Integer.valueOf( element );
    }
    
  }
  
  protected static class IntegerToStringElementConverter implements ElementConverter<Integer, String>
  {
    @Override
    public String convert( Integer element )
    {
      return String.valueOf( element.intValue() );
    }
  }
  
  protected static interface TestTypeWithAdapter
  {
    @Adapter(type = IntegerToStringElementConverter.class)
    public String getFieldString();
    
    @Adapter(type = StringToIntegerElementConverter.class)
    public void setFieldString( String fieldString );
  }
  
  @Test
  public void testNewInstance()
  {
    //
    Map<String, Object> map = new HashMap<String, Object>();
    
    //reading from facade
    TestType testType = PropertynameMapToTypeAdapter.newInstance( map, TestType.class, false );
    
    //
    map.put( "fieldString", "String value" );
    map.put( "fieldDouble", 10.0 );
    
    assertEquals( "String value", testType.getFieldString() );
    assertEquals( 10.0, testType.getFieldDouble(), 0.01 );
    
    //writing to facade
    testType.setFieldString( "New String value" );
    testType.setFieldDouble( 11.0 );
    
    assertEquals( "New String value", testType.getFieldString() );
    assertEquals( 11.0, testType.getFieldDouble(), 0.01 );
    
    assertEquals( "New String value", map.get( "fieldString" ) );
    assertEquals( 11.0, (Double) map.get( "fieldDouble" ), 0.01 );
    assertEquals( 2, map.size() );
    
  }
  
  @Test
  public void testNewInstanceUpperAndLowercasePropertyAccess()
  {
    //
    {
      //
      Map<String, Object> map = new HashMap<String, Object>();
      
      //reading from facade
      TestType testType = PropertynameMapToTypeAdapter.newInstance( map, TestType.class, false,
                                                                    PropertyAccessOption.PROPERTY_LOWERCASE );
      
      //
      map.put( "fieldstring", "String value" );
      map.put( "fielddouble", 10.0 );
      
      assertEquals( "String value", testType.getFieldString() );
      assertEquals( 10.0, testType.getFieldDouble(), 0.01 );
      
      //writing to facade
      testType.setFieldString( "New String value" );
      testType.setFieldDouble( 11.0 );
      
      assertEquals( "New String value", testType.getFieldString() );
      assertEquals( 11.0, testType.getFieldDouble(), 0.01 );
      
      assertEquals( "New String value", map.get( "fieldstring" ) );
      assertEquals( 11.0, (Double) map.get( "fielddouble" ), 0.01 );
      assertEquals( 2, map.size() );
    }
    
    //
    {
      //
      Map<String, Object> map = new HashMap<String, Object>();
      
      //reading from facade
      TestType testType = PropertynameMapToTypeAdapter.newInstance( map, TestType.class, false,
                                                                    PropertyAccessOption.PROPERTY_UPPERCASE );
      
      //
      map.put( "FIELDSTRING", "String value" );
      map.put( "FIELDDOUBLE", 10.0 );
      
      assertEquals( "String value", testType.getFieldString() );
      assertEquals( 10.0, testType.getFieldDouble(), 0.01 );
      
      //writing to facade
      testType.setFieldString( "New String value" );
      testType.setFieldDouble( 11.0 );
      
      assertEquals( "New String value", testType.getFieldString() );
      assertEquals( 11.0, testType.getFieldDouble(), 0.01 );
      
      assertEquals( "New String value", map.get( "FIELDSTRING" ) );
      assertEquals( 11.0, (Double) map.get( "FIELDDOUBLE" ), 0.01 );
      assertEquals( 2, map.size() );
    }
  }
  
  @Test
  public void testNewInstanceUnderlyingMapAware()
  {
    //
    Map<String, Object> map = new HashMap<String, Object>();
    
    map.put( "fieldString", "String value" );
    map.put( "fieldDouble", 10.0 );
    
    //reading from facade
    TestType testType = PropertynameMapToTypeAdapter.newInstance( map, TestType.class, true );
    
    assertEquals( "String value", testType.getFieldString() );
    assertEquals( 10.0, testType.getFieldDouble(), 0.01 );
    
    //
    assertTrue( testType instanceof UnderlyingMapAware );
    @SuppressWarnings("unchecked")
    UnderlyingMapAware<String, Object> underlyingMapAware = (UnderlyingMapAware<String, Object>) testType;
    Map<String, Object> underlyingMap = underlyingMapAware.getUnderlyingMap();
    assertEquals( map, underlyingMap );
  }
  
  @Test
  public void testSimulatingToString()
  {
    //
    Map<String, Object> map = new LinkedHashMap<String, Object>();
    
    map.put( "fieldString", "String value" );
    map.put( "fieldDouble", 10.0 );
    map.put( "otherfieldDouble", 10.0 );
    
    //reading from facade
    TestType testType = PropertynameMapToTypeAdapter.newInstance( map, TestType.class, true, true );
    
    //
    //System.out.println( testType );
    assertEquals( "[\n  fieldString=String value\n  fieldDouble=10.0\n  otherfieldDouble=10.0\n]", testType.toString() );
    
  }
  
  @Test
  public void testAdapter()
  {
    //
    Map<String, Object> map = new HashMap<String, Object>();
    
    //reading from facade
    TestTypeWithAdapter testTypeWithAdapter = PropertynameMapToTypeAdapter.newInstance( map, TestTypeWithAdapter.class, true,
                                                                                        true );
    
    //
    testTypeWithAdapter.setFieldString( "123" );
    String fieldString = testTypeWithAdapter.getFieldString();
    
    //
    assertEquals( "123", fieldString );
    assertTrue( map.values().iterator().next() instanceof Integer );
    
  }
  
}

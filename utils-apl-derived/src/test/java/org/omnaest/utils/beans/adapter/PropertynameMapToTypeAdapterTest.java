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
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.omnaest.utils.beans.adapter.PropertynameMapToTypeAdapter.Configuration;
import org.omnaest.utils.structure.element.Range;
import org.omnaest.utils.structure.element.converter.Converter;
import org.omnaest.utils.structure.element.converter.ElementConverterNumberToString;
import org.omnaest.utils.structure.element.converter.ElementConverterStringToInteger;
import org.omnaest.utils.structure.map.UnderlyingMapAware;

/**
 * @see PropertynameMapToTypeAdapter
 * @author Omnaest
 */
public class PropertynameMapToTypeAdapterTest
{
  
  @Rule
  public ContiPerfRule contiPerfRule = new ContiPerfRule();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  protected static interface TestType
  {
    public Double getFieldDouble();
    
    public void setFieldDouble( Double fieldDouble );
    
    public String getFieldString();
    
    public void setFieldString( String fieldString );
    
  }
  
  protected static interface TestTypeWithAdapter
  {
    @Converter(type = ElementConverterNumberToString.class)
    public String getFieldString();
    
    @Converter(type = ElementConverterStringToInteger.class)
    public void setFieldString( String fieldString );
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  @Test
  public void testNewInstance()
  {
    //
    Map<String, Object> map = new HashMap<String, Object>();
    
    //reading from facade
    TestType testType = PropertynameMapToTypeAdapter.newInstance( map, TestType.class );
    
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
  public void testNewInstancePropertyAccessVariations()
  {
    //
    {
      //
      Map<String, Object> map = new HashMap<String, Object>();
      
      //reading from facade
      
      PropertyAccessOption propertyAccessOption = PropertyAccessOption.PROPERTY_LOWERCASE;
      boolean isRegardingAdapterAnnotation = false;
      boolean isRegardingPropertyNameTemplate = false;
      boolean underlyingMapAware = false;
      boolean simulatingToString = false;
      TestType testType = PropertynameMapToTypeAdapter.newInstance( map, TestType.class,
                                                                    new Configuration( propertyAccessOption,
                                                                                       isRegardingAdapterAnnotation,
                                                                                       isRegardingPropertyNameTemplate,
                                                                                       underlyingMapAware, simulatingToString ) );
      
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
      PropertyAccessOption propertyAccessOption = PropertyAccessOption.PROPERTY_UPPERCASE;
      boolean isRegardingAdapterAnnotation = false;
      boolean isRegardingPropertyNameTemplate = false;
      boolean underlyingMapAware = false;
      boolean simulatingToString = false;
      TestType testType = PropertynameMapToTypeAdapter.newInstance( map, TestType.class,
                                                                    new Configuration( propertyAccessOption,
                                                                                       isRegardingAdapterAnnotation,
                                                                                       isRegardingPropertyNameTemplate,
                                                                                       underlyingMapAware, simulatingToString ) );
      
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
    
    //
    {
      //
      Map<String, Object> map = new HashMap<String, Object>();
      
      //reading from facade
      
      PropertyAccessOption propertyAccessOption = PropertyAccessOption.PROPERTY_CAPITALIZED;
      boolean isRegardingAdapterAnnotation = false;
      boolean isRegardingPropertyNameTemplate = false;
      boolean underlyingMapAware = false;
      boolean simulatingToString = false;
      TestType testType = PropertynameMapToTypeAdapter.newInstance( map, TestType.class,
                                                                    new Configuration( propertyAccessOption,
                                                                                       isRegardingAdapterAnnotation,
                                                                                       isRegardingPropertyNameTemplate,
                                                                                       underlyingMapAware, simulatingToString ) );
      
      //
      map.put( "FieldString", "String value" );
      map.put( "FieldDouble", 10.0 );
      
      assertEquals( "String value", testType.getFieldString() );
      assertEquals( 10.0, testType.getFieldDouble(), 0.01 );
      
      //writing to facade
      testType.setFieldString( "New String value" );
      testType.setFieldDouble( 11.0 );
      
      assertEquals( "New String value", testType.getFieldString() );
      assertEquals( 11.0, testType.getFieldDouble(), 0.01 );
      
      assertEquals( "New String value", map.get( "FieldString" ) );
      assertEquals( 11.0, (Double) map.get( "FieldDouble" ), 0.01 );
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
    boolean simulatingToString = false;
    boolean isUnderlyingMapAware = true;
    TestType testType = PropertynameMapToTypeAdapter.newInstance( map, TestType.class, new Configuration( isUnderlyingMapAware,
                                                                                                          simulatingToString ) );
    
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
    boolean underlyingMapAware = true;
    boolean simulatingToString = true;
    TestType testType = PropertynameMapToTypeAdapter.newInstance( map, TestType.class, new Configuration( underlyingMapAware,
                                                                                                          simulatingToString ) );
    
    //
    //System.out.println( testType );
    assertEquals( "[\n  fieldString=String value\n  fieldDouble=10.0\n  otherfieldDouble=10.0\n]", testType.toString() );
    
  }
  
  @Test
  public void testAdapterAnnotation()
  {
    //
    Map<String, Object> map = new HashMap<String, Object>();
    
    //reading from facade
    boolean underlyingMapAware = true;
    boolean simulatingToString = true;
    boolean isRegardingPropertyNameTemplate = true;
    boolean isRegardingAdapterAnnotation = true;
    PropertyAccessOption propertyAccessOption = null;
    
    TestTypeWithAdapter testTypeWithAdapter = PropertynameMapToTypeAdapter.newInstance( map,
                                                                                        TestTypeWithAdapter.class,
                                                                                        new Configuration(
                                                                                                           propertyAccessOption,
                                                                                                           isRegardingAdapterAnnotation,
                                                                                                           isRegardingPropertyNameTemplate,
                                                                                                           underlyingMapAware,
                                                                                                           simulatingToString ) );
    
    //
    testTypeWithAdapter.setFieldString( "123" );
    String fieldString = testTypeWithAdapter.getFieldString();
    
    //
    assertEquals( "123", fieldString );
    assertTrue( map.values().iterator().next() instanceof Integer );
    
  }
  
  @Test
  @PerfTest(invocations = 100)
  @Required(average = 100)
  public void testPerformance()
  {
    //    
    Map<String, Object> map = new HashMap<String, Object>();
    boolean underlyingMapAware = true;
    boolean simulatingToString = true;
    TestTypeWithAdapter testTypeWithAdapter = PropertynameMapToTypeAdapter.newInstance( map, TestTypeWithAdapter.class,
                                                                                        new Configuration( underlyingMapAware,
                                                                                                           simulatingToString ) );
    
    for ( @SuppressWarnings("unused")
    long ii : new Range( 1, 100 ) )
    {
      //
      testTypeWithAdapter.setFieldString( "123" );
      String fieldString = testTypeWithAdapter.getFieldString();
      
      //
      assertEquals( "123", fieldString );
    }
    
  }
  
}

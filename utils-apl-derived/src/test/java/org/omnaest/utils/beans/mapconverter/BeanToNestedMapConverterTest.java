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
package org.omnaest.utils.beans.mapconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.junit.Test;
import org.omnaest.utils.structure.element.FutureSimple;

/**
 * @see BeanToNestedMapConverter
 * @author Omnaest
 */
public class BeanToNestedMapConverterTest
{
  /* ********************************************** Variables ********************************************** */
  private BeanToNestedMapConverter<TestClass> beanToNestedMapConverter = new BeanToNestedMapConverter<TestClass>( TestClass.class );
  private TestClass                           testClass                = new TestClass( "value1", 1.234,
                                                                                        new TestClass( "subvalue", 5.678, null ) );
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @author Omnaest
   */
  @SuppressWarnings("unused")
  private static class TestClass
  {
    /* ********************************************** Variables ********************************************** */
    private String              valueString       = null;
    private Double              valueDouble       = null;
    private TestClass           testClass         = null;
    private TestClass           testClassCopy     = null;
    private Map<String, Double> stringToDoubleMap = new HashMap<String, Double>();
    private Future<String>      future            = new FutureSimple<String>();
    private String              privateField      = "privateValue";
    
    /* ********************************************** Methods ********************************************** */
    
    public TestClass()
    {
      super();
    }
    
    public TestClass( String valueString, Double valueDouble, TestClass testClass )
    {
      //
      super();
      
      //
      this.valueString = valueString;
      this.valueDouble = valueDouble;
      this.testClass = testClass;
      this.testClassCopy = testClass;
      
      //
      this.stringToDoubleMap.put( "key1", 1.234 );
      this.stringToDoubleMap.put( "key2", 2.234 );
    }
    
    public String getValueString()
    {
      return this.valueString;
    }
    
    public void setValueString( String valueString )
    {
      this.valueString = valueString;
    }
    
    public Double getValueDouble()
    {
      return this.valueDouble;
    }
    
    public void setValueDouble( Double valueDouble )
    {
      this.valueDouble = valueDouble;
    }
    
    public TestClass getTestClass()
    {
      return this.testClass;
    }
    
    public void setTestClass( TestClass testClass )
    {
      this.testClass = testClass;
    }
    
    public TestClass getTestClassCopy()
    {
      return this.testClassCopy;
    }
    
    public void setTestClassCopy( TestClass testClassCopy )
    {
      this.testClassCopy = testClassCopy;
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.testClass == null ) ? 0 : this.testClass.hashCode() );
      result = prime * result + ( ( this.testClassCopy == null ) ? 0 : this.testClassCopy.hashCode() );
      result = prime * result + ( ( this.valueDouble == null ) ? 0 : this.valueDouble.hashCode() );
      result = prime * result + ( ( this.valueString == null ) ? 0 : this.valueString.hashCode() );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof TestClass ) )
      {
        return false;
      }
      TestClass other = (TestClass) obj;
      if ( this.testClass == null )
      {
        if ( other.testClass != null )
        {
          return false;
        }
      }
      else if ( !this.testClass.equals( other.testClass ) )
      {
        return false;
      }
      if ( this.testClassCopy == null )
      {
        if ( other.testClassCopy != null )
        {
          return false;
        }
      }
      else if ( !this.testClassCopy.equals( other.testClassCopy ) )
      {
        return false;
      }
      if ( this.valueDouble == null )
      {
        if ( other.valueDouble != null )
        {
          return false;
        }
      }
      else if ( !this.valueDouble.equals( other.valueDouble ) )
      {
        return false;
      }
      if ( this.valueString == null )
      {
        if ( other.valueString != null )
        {
          return false;
        }
      }
      else if ( !this.valueString.equals( other.valueString ) )
      {
        return false;
      }
      return true;
    }
    
    public Map<String, Double> getStringToDoubleMap()
    {
      return this.stringToDoubleMap;
    }
    
    public void setStringToDoubleMap( Map<String, Double> stringToDoubleMap )
    {
      this.stringToDoubleMap = stringToDoubleMap;
    }
    
    public Future<String> getFuture()
    {
      return this.future;
    }
    
    public void setFuture( Future<String> future )
    {
      this.future = future;
    }
  }
  
  @Test
  public void testMarshal()
  {
    // 
    Map<String, Object> map = this.beanToNestedMapConverter.marshal( this.testClass );
    assertNotNull( map );
    assertEquals( 6, map.size() );
    assertEquals( this.testClass.getValueString(), map.get( "valueString" ) );
    assertEquals( this.testClass.getValueDouble(), map.get( "valueDouble" ) );
    assertEquals( this.testClass.getStringToDoubleMap(), map.get( "stringToDoubleMap" ) );
    assertTrue( map.get( "testClass" ) instanceof Map );
    assertTrue( map.get( "testClassCopy" ) instanceof Map );
    assertTrue( map.get( "testClass" ) == map.get( "testClassCopy" ) );
    
    //
    @SuppressWarnings("unchecked")
    Map<String, Object> subMap = (Map<String, Object>) map.get( "testClass" );
    assertEquals( 6, subMap.size() );
    assertEquals( this.testClass.getTestClass().getValueString(), subMap.get( "valueString" ) );
    assertEquals( this.testClass.getTestClass().getValueDouble(), subMap.get( "valueDouble" ) );
    assertEquals( this.testClass.getTestClass().getStringToDoubleMap(), subMap.get( "stringToDoubleMap" ) );
    assertNull( subMap.get( "testClass" ) );
    assertNull( subMap.get( "testClassCopy" ) );
  }
  
  @Test
  public void testUnmarshal()
  {
    //
    Map<String, Object> map = this.beanToNestedMapConverter.marshal( this.testClass );
    TestClass testClassResult = this.beanToNestedMapConverter.unmarshal( map );
    
    //
    assertEquals( this.testClass, testClassResult );
  }
  
}

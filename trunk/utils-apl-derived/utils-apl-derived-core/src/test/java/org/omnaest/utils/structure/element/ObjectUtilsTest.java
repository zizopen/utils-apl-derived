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
package org.omnaest.utils.structure.element;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.omnaest.utils.structure.map.MapBuilder;

/**
 * @see ObjectUtils
 * @author Omnaest
 */
public class ObjectUtilsTest
{
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  protected static class TestValueOf
  {
    /* ********************************************** Variables ********************************************** */
    protected Integer value  = null;
    protected String  string = null;
    
    /* ********************************************** Methods ********************************************** */
    
    protected TestValueOf( String string )
    {
      super();
      this.string = string;
    }
    
    protected TestValueOf( int value )
    {
      super();
      this.value = value;
    }
    
    public static TestValueOf valueOf( Integer value )
    {
      return new TestValueOf( value );
    }
    
    public Integer getValue()
    {
      return this.value;
    }
    
    public String getString()
    {
      return this.string;
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testCastTo()
  {
    //  
    {
      //
      Object object = "5";
      Class<?> type = Integer.class;
      
      //
      Object objectCasted = ObjectUtils.castTo( type, object );
      
      //
      assertTrue( objectCasted instanceof Integer );
      assertEquals( Integer.valueOf( 5 ), objectCasted );
    }
    {
      //
      Object object = "5";
      Class<?> type = long.class;
      
      //
      Object objectCasted = ObjectUtils.castTo( type, object );
      
      //
      assertTrue( objectCasted instanceof Long );
      assertEquals( Long.valueOf( 5 ), objectCasted );
    }
    {
      //
      Object object = "true";
      Class<?> type = Boolean.class;
      
      //
      Object objectCasted = ObjectUtils.castTo( type, object );
      
      //
      assertTrue( objectCasted instanceof Boolean );
      assertEquals( Boolean.valueOf( (String) object ), objectCasted );
    }
    {
      //
      Object object = "true";
      Class<?> type = boolean.class;
      
      //
      Object objectCasted = ObjectUtils.castTo( type, object );
      
      //
      assertTrue( objectCasted instanceof Boolean );
      assertEquals( Boolean.valueOf( (String) object ), objectCasted );
    }
    {
      //
      Object object = Integer.valueOf( 10 );
      Class<?> type = TestValueOf.class;
      
      //
      Object objectCasted = ObjectUtils.castTo( type, object );
      
      //
      assertTrue( objectCasted instanceof TestValueOf );
      assertEquals( object, ( (TestValueOf) objectCasted ).getValue() );
    }
    {
      //
      Object object = "10";
      Class<?> type = TestValueOf.class;
      
      //
      Object objectCasted = ObjectUtils.castTo( type, object );
      
      //
      assertTrue( objectCasted instanceof TestValueOf );
      assertEquals( object, ( (TestValueOf) objectCasted ).getString() );
    }
    {
      //
      Object object = new Object();
      Class<?> type = String.class;
      
      //
      Object objectCasted = ObjectUtils.castTo( type, object );
      
      //
      assertTrue( objectCasted instanceof String );
      assertTrue( ( (String) objectCasted ).startsWith( "java.lang.Object@" ) );
    }
  }
  
  @Test
  public void testDefaultIfNull()
  {
    {
      //
      Double object = null;
      Double defaultObject = 1.34;
      Double value = ObjectUtils.defaultIfNull( object, defaultObject );
      assertEquals( defaultObject, value );
    }
    {
      //
      Double object = 5.67;
      Double defaultObject = 1.34;
      Double value = ObjectUtils.defaultIfNull( object, defaultObject );
      assertEquals( object, value );
    }
    {
      //
      Double object = null;
      Double defaultObject = null;
      Double value = ObjectUtils.defaultIfNull( object, defaultObject );
      assertEquals( null, value );
    }
    {
      //
      Double object = null;
      Double defaultObject = null;
      Double defaultObject2 = 1.34;
      Double value = ObjectUtils.defaultIfNull( object, defaultObject, defaultObject2 );
      assertEquals( defaultObject2, value );
    }
  }
  
  @Test
  public void testDefaultIfNullWithFactory()
  {
    {
      //
      Double object = null;
      final Double defaultObject = 1.34;
      Factory<Double> defaultObjectFactory = new Factory<Double>()
      {
        @Override
        public Double newInstance()
        {
          return defaultObject;
        }
      };
      Double value = ObjectUtils.defaultIfNull( object, defaultObjectFactory );
      assertEquals( defaultObject, value );
    }
    {
      //
      Double object = 5.67;
      final Double defaultObject = 1.34;
      Factory<Double> defaultObjectFactory = new Factory<Double>()
      {
        @Override
        public Double newInstance()
        {
          return defaultObject;
        }
      };
      Double value = ObjectUtils.defaultIfNull( object, defaultObjectFactory );
      assertEquals( object, value );
    }
    {
      //
      Double object = null;
      final Double defaultObject = null;
      Factory<Double> defaultObjectFactory = new Factory<Double>()
      {
        @Override
        public Double newInstance()
        {
          return defaultObject;
        }
      };
      Double value = ObjectUtils.defaultIfNull( object, defaultObjectFactory );
      assertEquals( null, value );
    }
    {
      //
      Double object = null;
      Factory<Double> defaultObjectFactory = null;
      Double value = ObjectUtils.defaultIfNull( object, defaultObjectFactory );
      assertEquals( null, value );
    }
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testCastArrayTo()
  {
    {
      //
      Double[] doubles = new Double[] { 1.5, 1.6 };
      List<String> castedArray = ObjectUtils.castArrayTo( List.class, String.class, doubles );
      assertEquals( Arrays.asList( "1.5", "1.6" ), castedArray );
    }
    {
      //
      Double[] doubles = new Double[] { 1.5, 1.6 };
      LinkedHashSet<String> castedArray = ObjectUtils.castArrayTo( LinkedHashSet.class, String.class, doubles );
      assertEquals( new LinkedHashSet<String>( Arrays.asList( "1.5", "1.6" ) ), castedArray );
    }
    {
      //
      Double[] doubles = new Double[] { 1.5, 1.6, 2.5, 2.6 };
      Map<String, String> castedArray = ObjectUtils.castArrayTo( Map.class, String.class, doubles );
      assertEquals( new MapBuilder<String, String>().linkedHashMap().put( "1.5", "1.6" ).put( "2.5", "2.6" ).build(), castedArray );
    }
    {
      //
      double[] doubles = new double[] { 1.5, 1.6 };
      LinkedHashSet<String> castedArray = ObjectUtils.castArrayTo( LinkedHashSet.class, String.class, doubles );
      assertEquals( new LinkedHashSet<String>( Arrays.asList( "1.5", "1.6" ) ), castedArray );
    }
    {
      //
      float[] floats = new float[] { 1.5f, 1.6f };
      double[] castedArray = ObjectUtils.castArrayTo( double[].class, double.class, floats );
      assertArrayEquals( new double[] { 1.5, 1.6 }, castedArray, 0.1 );
    }
    {
      //
      int[] ints = new int[] { 10, 11 };
      long[] castedArray = ObjectUtils.castArrayTo( long[].class, long.class, ints );
      assertArrayEquals( new long[] { 10l, 11l }, castedArray );
    }
    {
      //
      int[] ints = new int[] { 10, 11 };
      long[] castedArray = ObjectUtils.castArrayTo( long[].class, Long.class, ints );
      assertArrayEquals( new long[] { 10l, 11l }, castedArray );
    }
    {
      //
      int[] ints = new int[] { 10, 11 };
      Long[] castedArray = ObjectUtils.castArrayTo( Long[].class, long.class, ints );
      assertArrayEquals( new Long[] { 10l, 11l }, castedArray );
    }
  }
  
  @Test
  public void testIsPrimitiveWrapperType()
  {
    assertTrue( ObjectUtils.isPrimitiveWrapperType( Integer.class ) );
    assertFalse( ObjectUtils.isPrimitiveWrapperType( boolean.class ) );
    assertFalse( ObjectUtils.isPrimitiveWrapperType( String.class ) );
  }
  
  @Test
  public void testObjectTypeFor()
  {
    assertEquals( null, ObjectUtils.objectTypeFor( null ) );
    assertEquals( Double.class, ObjectUtils.objectTypeFor( double.class ) );
    assertEquals( String.class, ObjectUtils.objectTypeFor( String.class ) );
  }
}

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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.collections.ComparatorUtils;
import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.beans.ListToTypeAdapter.UnderlyingListAware;

public class ListToTypeAdapterTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  public static interface TestInterface
  {
    public void setFieldString( String fieldString );
    
    public String getFieldString();
    
    public void setFieldDouble( Double fieldDouble );
    
    public Double getFieldDouble();
    
    public void setFieldStringList( List<String> fieldStringList );
    
    public List<String> getFieldStringList();
  }
  
  public static class TestClass implements TestInterface
  {
    /* ********************************************** Variables ********************************************** */
    protected String       fieldString     = "";
    protected Double       fieldDouble     = 0.1;
    protected List<String> fieldStringList = new ArrayList<String>();
    
    /* ********************************************** Methods ********************************************** */
    @Override
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    @Override
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
    @Override
    public Double getFieldDouble()
    {
      return this.fieldDouble;
    }
    
    @Override
    public void setFieldDouble( Double fieldDouble )
    {
      this.fieldDouble = fieldDouble;
    }
    
    @Override
    public List<String> getFieldStringList()
    {
      return this.fieldStringList;
    }
    
    @Override
    public void setFieldStringList( List<String> fieldStringList )
    {
      this.fieldStringList = fieldStringList;
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.fieldDouble == null ) ? 0 : this.fieldDouble.hashCode() );
      result = prime * result + ( ( this.fieldString == null ) ? 0 : this.fieldString.hashCode() );
      result = prime * result + ( ( this.fieldStringList == null ) ? 0 : this.fieldStringList.hashCode() );
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
      if ( this.fieldDouble == null )
      {
        if ( other.fieldDouble != null )
        {
          return false;
        }
      }
      else if ( !this.fieldDouble.equals( other.fieldDouble ) )
      {
        return false;
      }
      if ( this.fieldString == null )
      {
        if ( other.fieldString != null )
        {
          return false;
        }
      }
      else if ( !this.fieldString.equals( other.fieldString ) )
      {
        return false;
      }
      if ( this.fieldStringList == null )
      {
        if ( other.fieldStringList != null )
        {
          return false;
        }
      }
      else if ( !this.fieldStringList.equals( other.fieldStringList ) )
      {
        return false;
      }
      return true;
    }
    
  }
  
  @Test
  public void testNewInstance()
  {
    //
    TestInterface testInterface = new TestClass();
    testInterface.getFieldStringList().add( "first list value" );
    testInterface.getFieldStringList().add( "second list value" );
    
    //
    List<Object> list = new ArrayList<Object>();
    
    //
    TestInterface testInterfaceAdapter = ListToTypeAdapter.newInstance( list, TestInterface.class, false );
    
    //
    BeanUtils.copyPropertyValues( testInterface, testInterfaceAdapter );
    
    //
    assertEquals( 3, list.size() );
    assertTrue( list.contains( testInterface.getFieldString() ) );
    assertTrue( list.contains( testInterface.getFieldDouble() ) );
    assertTrue( list.contains( testInterface.getFieldStringList() ) );
    
    //
    assertEquals( testInterface.getFieldString(), testInterfaceAdapter.getFieldString() );
    assertEquals( testInterface.getFieldDouble(), testInterfaceAdapter.getFieldDouble() );
    assertEquals( testInterface.getFieldStringList(), testInterfaceAdapter.getFieldStringList() );
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testNewInstanceOrderedByComparable()
  {
    //
    TestInterface testInterface = new TestClass();
    testInterface.getFieldStringList().add( "first list value" );
    testInterface.getFieldStringList().add( "second list value" );
    
    //
    List<Object> list = new ArrayList<Object>();
    
    //
    TestInterface testInterfaceAdapter = ListToTypeAdapter.newInstance( list, TestInterface.class,
                                                                        ComparatorUtils.NATURAL_COMPARATOR, false );
    
    //
    BeanUtils.copyPropertyValues( testInterface, testInterfaceAdapter );
    
    //
    assertEquals( 3, list.size() );
    assertEquals( testInterface.getFieldDouble(), list.get( 0 ) );
    assertEquals( testInterface.getFieldString(), list.get( 1 ) );
    assertEquals( testInterface.getFieldStringList(), list.get( 2 ) );
    
  }
  
  @Test
  public void testNewInstanceOrderedByPropertynameList()
  {
    //
    TestInterface testInterface = new TestClass();
    testInterface.getFieldStringList().add( "first list value" );
    testInterface.getFieldStringList().add( "second list value" );
    
    //
    List<Object> list = new ArrayList<Object>();
    
    //
    TestInterface testInterfaceAdapter = ListToTypeAdapter.newInstance( list, TestInterface.class,
                                                                        Arrays.asList( "fieldString", "fieldStringList",
                                                                                       "fieldDouble" ), false );
    
    //
    BeanUtils.copyPropertyValues( testInterface, testInterfaceAdapter );
    
    //
    assertEquals( 3, list.size() );
    assertEquals( testInterface.getFieldString(), list.get( 0 ) );
    assertEquals( testInterface.getFieldStringList(), list.get( 1 ) );
    assertEquals( testInterface.getFieldDouble(), list.get( 2 ) );
    
  }
  
  @Test
  public void testNewInstanceAccessUnderlyingData()
  {
    //
    TestInterface testInterface = new TestClass();
    testInterface.getFieldStringList().add( "first list value" );
    testInterface.getFieldStringList().add( "second list value" );
    
    //
    List<Object> list = new ArrayList<Object>();
    
    //
    TestInterface testInterfaceAdapter = ListToTypeAdapter.newInstance( list, TestInterface.class, true );
    
    //
    BeanUtils.copyPropertyValues( testInterface, testInterfaceAdapter );
    
    //
    assertEquals( 3, list.size() );
    assertTrue( testInterfaceAdapter instanceof UnderlyingListAware );
    
    //
    UnderlyingListAware<?> underlyingListAware = (UnderlyingListAware<?>) testInterfaceAdapter;
    assertEquals( list, underlyingListAware.getUnderlyingList() );
    assertNotNull( underlyingListAware.getUnderlyingPropertynameList() );
    assertEquals( 3, underlyingListAware.getUnderlyingList().size() );
  }
  
  @Test
  public void testReflectionInvokation()
  {
    //
    List<Object> list = new ArrayList<Object>();
    TestInterface testInterfaceAdapter = ListToTypeAdapter.newInstance( list, TestInterface.class, false );
    
    //
    testInterfaceAdapter.setFieldDouble( 1.67 );
    
    //
    assertEquals( 3, list.size() );
    assertEquals( 1.67, testInterfaceAdapter.getFieldDouble(), 0.001 );
    
    //
    try
    {
      //
      Method declaredMethod = testInterfaceAdapter.getClass().getDeclaredMethod( "setFieldString", String.class );
      declaredMethod.invoke( testInterfaceAdapter, "new value" );
    }
    catch ( Exception e )
    {
      Assert.fail( e.getMessage() );
    }
    
    //
    assertEquals( "new value", testInterfaceAdapter.getFieldString() );
  }
  
}

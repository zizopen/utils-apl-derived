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
package org.omnaest.utils.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.omnaest.utils.proxy.BeanProperty.beanProperty;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;
import org.omnaest.utils.beans.result.BeanPropertyAccessors;

/**
 * @see BeanProperty
 * @author Omnaest
 */
public class BeanPropertyTest
{
  /* ********************************************** Variables ********************************************** */
  protected BeanProperty beanProperty = new BeanProperty();
  protected TestClass    testClass    = this.beanProperty.newInstanceOfTransitivlyCapturedType( TestClass.class );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see BeanPropertyTest
   */
  protected static class TestClass
  {
    /* ********************************************** Variables ********************************************** */
    protected String    fieldString = null;
    protected Double    fieldDouble = null;
    protected TestClass testClass   = null;
    
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
    
    public TestClass getTestClass()
    {
      return this.testClass;
    }
    
    public void setTestClass( TestClass testClass )
    {
      this.testClass = testClass;
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testNewInstanceOfCapturedType()
  {
    assertNotNull( this.testClass );
  }
  
  @Test
  public void testOf()
  {
    //
    {
      String propertyName = this.beanProperty.name.of( this.testClass.getFieldDouble() );
      assertEquals( "fieldDouble", propertyName );
    }
    
    //
    {
      String propertyName = this.beanProperty.name.of( this.testClass.getTestClass().getFieldDouble() );
      assertEquals( "testClass.fieldDouble", propertyName );
    }
    
    //
    {
      String[] propertyNames = this.beanProperty.name.of( this.testClass.getTestClass().getFieldDouble(),
                                                          this.testClass.getTestClass().getFieldString() );
      Assert.assertArrayEquals( new String[] { "testClass.fieldDouble", "testClass.fieldString" }, propertyNames );
    }
    
  }
  
  @Test
  public void testOfBeanPropertyAccessor()
  {
    //
    {
      //
      BeanPropertyAccessor<TestClass> beanPropertyAccessor = this.beanProperty.accessor.of( this.testClass.getFieldDouble() );
      
      //
      assertNotNull( beanPropertyAccessor );
      assertEquals( "fieldDouble", beanPropertyAccessor.getPropertyName() );
      assertTrue( beanPropertyAccessor.hasGetterAndSetter() );
    }
    
    //
    {
      //
      BeanPropertyAccessors<TestClass> beanPropertyAccessors = this.beanProperty.accessor.of( this.testClass.getFieldString(),
                                                                                              this.testClass.getTestClass()
                                                                                                            .getFieldDouble() );
      
      //
      assertNotNull( beanPropertyAccessors );
      assertEquals( 2, beanPropertyAccessors.size() );
      
      //
      Iterator<BeanPropertyAccessor<TestClass>> iterator = beanPropertyAccessors.iterator();
      assertEquals( "fieldString", iterator.next().getPropertyName() );
      assertEquals( "fieldDouble", iterator.next().getPropertyName() );
    }
    
  }
  
  @Test
  public void testBeanProperty()
  {
    String name = beanProperty().name.of( beanProperty().newInstanceOfCapturedType( TestClass.class ).getFieldDouble() );
    assertEquals( "fieldDouble", name );
  }
  
}

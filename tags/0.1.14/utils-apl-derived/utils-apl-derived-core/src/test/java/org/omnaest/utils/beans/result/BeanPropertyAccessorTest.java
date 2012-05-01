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
package org.omnaest.utils.beans.result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.beans.result.BeanPropertyAccessor.PropertyAccessType;

/**
 * @see BeanPropertyAccessor
 * @author Omnaest
 */
public class BeanPropertyAccessorTest
{
  /* ********************************************** Variables ********************************************** */
  protected BeanPropertyAccessor<TestBean> beanPropertyAccessorValue1 = BeanUtils.beanPropertyAccessor( TestBean.class, "value1" );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @author Omnaest
   */
  protected static class TestBean
  {
    /* ********************************************** Variables ********************************************** */
    private String value1 = "value1";
    private String value2 = "value2";
    
    /* ********************************************** Methods ********************************************** */
    
    public String getValue1()
    {
      return this.value1;
    }
    
    public void setValue1( String value1 )
    {
      this.value1 = value1;
    }
    
    public String getValue2()
    {
      return this.value2;
    }
    
    public void setValue2( String value2 )
    {
      this.value2 = value2;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  @Test
  public void testGetPropertyValueBObjectPropertyAccessType()
  {
    assertTrue( this.beanPropertyAccessorValue1.hasGetterAndSetter() );
    assertEquals( "value1", this.beanPropertyAccessorValue1.getPropertyValue( new TestBean() ) );
  }
  
  @Test
  public void testGetAndSetPropertyValueBObjectPropertyAccessType()
  {
    //
    TestBean testBean = new TestBean();
    
    //
    assertTrue( this.beanPropertyAccessorValue1.hasField() );
    assertEquals( "value1", this.beanPropertyAccessorValue1.getPropertyValue( testBean, PropertyAccessType.FIELD ) );
    
    //
    this.beanPropertyAccessorValue1.setPropertyValue( testBean, "other value" );
    
    //
    assertEquals( "other value", this.beanPropertyAccessorValue1.getPropertyValue( testBean ) );
    assertEquals( String.class, this.beanPropertyAccessorValue1.getDeclaringPropertyType() );
  }
}

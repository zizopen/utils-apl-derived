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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;

/**
 * @see BeanUtils
 * @author Omnaest
 */
public class BeanUtilsTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  protected static class TestBeanImpl implements TestBean
  {
    /* ********************************************** Variables ********************************************** */
    protected String fieldString = null;
    protected Double fieldDouble = null;
    
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
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.fieldDouble == null ) ? 0 : this.fieldDouble.hashCode() );
      result = prime * result + ( ( this.fieldString == null ) ? 0 : this.fieldString.hashCode() );
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
      if ( !( obj instanceof TestBeanImpl ) )
      {
        return false;
      }
      TestBeanImpl other = (TestBeanImpl) obj;
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
      return true;
    }
    
  }
  
  protected static interface TestBean
  {
    public void setFieldDouble( Double fieldDouble );
    
    public Double getFieldDouble();
    
    public void setFieldString( String fieldString );
    
    public String getFieldString();
  }
  
  /* ********************************************** Methods ********************************************** */
  @Test
  public void testDeterminePropertyNames()
  {
    //
    String[] propertyNames = BeanUtils.determinePropertyNamesForMethodAccess( TestBean.class );
    
    //
    Assert.assertArrayEquals( new String[] { "fieldString", "fieldDouble" }, propertyNames );
  }
  
  @Test
  public void testCopyPropertyValues()
  {
    //
    TestBeanImpl beanSource = new TestBeanImpl();
    beanSource.setFieldString( "value1" );
    beanSource.setFieldDouble( 1.3 );
    
    //
    TestBeanImpl beanDestination = new TestBeanImpl();
    assertTrue( !beanSource.equals( beanDestination ) );
    
    //
    BeanUtils.copyPropertyValues( beanSource, beanDestination );
    
    //
    assertEquals( beanSource, beanDestination );
  }
  
  @Test
  public void testDetermineBeanMethodInformationSet()
  {
    //
    assertEquals( 4, BeanUtils.determineBeanMethodInformationSet( TestBean.class ).size() );
  }
  
  @Test
  public void testDeterminePropertynameToBeanPropertyAccessorMap()
  {
    //
    assertEquals( 2, BeanUtils.determinePropertynameToBeanPropertyAccessorMap( TestBean.class ).size() );
    assertEquals( 2, BeanUtils.determinePropertynameToBeanPropertyAccessorMap( TestBeanImpl.class ).size() );
  }
  
  @Test
  public void testDetermineBeanPropertyAccessorSet()
  {
    //
    assertEquals( 2, BeanUtils.determineBeanPropertyAccessorSet( TestBean.class ).size() );
    assertEquals( 2, BeanUtils.determineBeanPropertyAccessorSet( TestBeanImpl.class ).size() );
  }
  
  @Test
  public void testDetermineBeanPropertyAccessor() throws Throwable
  {
    //
    {
      BeanPropertyAccessor<TestBeanImpl> beanPropertyAccessor = BeanUtils.determineBeanPropertyAccessor( TestBeanImpl.class,
                                                                                                         TestBeanImpl.class.getDeclaredFields()[0] );
      
      assertNotNull( beanPropertyAccessor );
      assertEquals( true, beanPropertyAccessor.hasGetterAndSetter() );
    }
    
    //
    {
      BeanPropertyAccessor<TestBeanImpl> beanPropertyAccessor = BeanUtils.determineBeanPropertyAccessor( TestBeanImpl.class,
                                                                                                         TestBeanImpl.class.getDeclaredField( "fieldString" ) );
      
      assertNotNull( beanPropertyAccessor );
      assertEquals( true, beanPropertyAccessor.hasGetterAndSetter() );
    }
  }
  
  @Test
  public void testDeterminePropertyNamesForMethodAccess()
  {
    String[] propertyNamesForMethodAccess = BeanUtils.determinePropertyNamesForMethodAccess( TestBean.class );
    Assert.assertArrayEquals( new String[] { "fieldString", "fieldDouble" }, propertyNamesForMethodAccess );
  }
  
  @Test
  public void testDetermineNumberOfProperties()
  {
    Assert.assertEquals( 2, BeanUtils.determinePropertyNamesForMethodAccess( TestBean.class ).length );
  }
}

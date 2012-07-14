/*******************************************************************************
 * Copyright 2012 Danny Kunz
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
package org.omnaest.utils.beans.replicator2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;

/**
 * @see BeanReplicator
 * @author Omnaest
 */
public class BeanReplicatorSimpleTest
{
  /* ************************************************** Constants *************************************************** */
  private final BeanReplicator<TestSimpleBeanFrom, TestSimpleBeanTo> beanReplicator = new BeanReplicator<TestSimpleBeanFrom, TestSimpleBeanTo>(
                                                                                                                                                TestSimpleBeanFrom.class,
                                                                                                                                                TestSimpleBeanTo.class );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  private static class TestSimpleBeanFrom extends TestSimpleBeanBase
  {
    
  }
  
  private static class TestSimpleBeanTo extends TestSimpleBeanBase
  {
    
  }
  
  private static class TestSimpleBeanBase
  {
    private String  fieldString;
    private Double  fieldDouble;
    private Boolean fieldBoolean;
    private Long    fieldLong;
    private Integer fieldInteger;
    private Float   fieldFloat;
    
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
    public Long getFieldLong()
    {
      return this.fieldLong;
    }
    
    public void setFieldLong( Long fieldLong )
    {
      this.fieldLong = fieldLong;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "SimpleBean [fieldString=" );
      builder.append( this.fieldString );
      builder.append( ", fieldLong=" );
      builder.append( this.fieldLong );
      builder.append( "]" );
      return builder.toString();
    }
    
    public Double getFieldDouble()
    {
      return this.fieldDouble;
    }
    
    public void setFieldDouble( Double fieldDouble )
    {
      this.fieldDouble = fieldDouble;
    }
    
    public Boolean getFieldBoolean()
    {
      return this.fieldBoolean;
    }
    
    public void setFieldBoolean( Boolean fieldBoolean )
    {
      this.fieldBoolean = fieldBoolean;
    }
    
    public Integer getFieldInteger()
    {
      return this.fieldInteger;
    }
    
    public void setFieldInteger( Integer fieldInteger )
    {
      this.fieldInteger = fieldInteger;
    }
    
    public Float getFieldFloat()
    {
      return this.fieldFloat;
    }
    
    public void setFieldFloat( Float fieldFloat )
    {
      this.fieldFloat = fieldFloat;
    }
    
  }
  
  /* *************************************************** Methods **************************************************** */
  
  @Test
  public void testBeanReplicator()
  {
    final TestSimpleBeanFrom simpleBean = newPreparedSimpleBean();
    {
      final TestSimpleBeanTo clone = new TestSimpleBeanTo();
      this.beanReplicator.copy( simpleBean, clone );
      assertSimpleBean( simpleBean, clone );
    }
    {
      final TestSimpleBeanTo clone = this.beanReplicator.clone( simpleBean );
      assertSimpleBean( simpleBean, clone );
    }
  }
  
  @Test
  public void testSerialization()
  {
    BeanReplicator<TestSimpleBeanFrom, TestSimpleBeanTo> beanReplicator = SerializationUtils.clone( this.beanReplicator );
    final TestSimpleBeanFrom simpleBean = newPreparedSimpleBean();
    final TestSimpleBeanTo clone = beanReplicator.clone( simpleBean );
    assertSimpleBean( simpleBean, clone );
  }
  
  @SuppressWarnings({ "unchecked" })
  @Test
  public void testMappingWithMap()
  {
    {
      BeanReplicator<TestSimpleBeanFrom, Map<String, Object>> beanReplicator = new BeanReplicator<TestSimpleBeanFrom, Map<String, Object>>(
                                                                                                                                            TestSimpleBeanFrom.class,
                                                                                                                                            (Class<? extends Map<String, Object>>) Map.class );
      
      final TestSimpleBeanFrom source = new TestSimpleBeanFrom();
      source.setFieldString( "test" );
      Map<String, Object> map = beanReplicator.clone( source );
      assertNotNull( map );
      assertEquals( "test", map.get( "fieldString" ) );
    }
    {
      BeanReplicator<Map<String, Object>, TestSimpleBeanTo> beanReplicator = new BeanReplicator<Map<String, Object>, TestSimpleBeanTo>(
                                                                                                                                        Map.class,
                                                                                                                                        TestSimpleBeanTo.class );
      
      final Map<String, Object> map = new HashMap<String, Object>();
      map.put( "fieldString", "test" );
      TestSimpleBeanTo testSimpleBeanTo = beanReplicator.clone( map );
      assertNotNull( testSimpleBeanTo );
      assertEquals( "test", testSimpleBeanTo.getFieldString() );
    }
  }
  
  private static void assertSimpleBean( final TestSimpleBeanFrom simpleBean, TestSimpleBeanTo clone )
  {
    assertEquals( simpleBean.getFieldString(), clone.getFieldString() );
    assertEquals( simpleBean.getFieldLong(), clone.getFieldLong() );
    assertEquals( simpleBean.getFieldInteger(), clone.getFieldInteger() );
    assertEquals( simpleBean.getFieldDouble(), clone.getFieldDouble() );
    assertEquals( simpleBean.getFieldFloat(), clone.getFieldFloat() );
    assertEquals( simpleBean.getFieldBoolean(), clone.getFieldBoolean() );
  }
  
  private static TestSimpleBeanFrom newPreparedSimpleBean()
  {
    final TestSimpleBeanFrom simpleBean = new TestSimpleBeanFrom();
    simpleBean.setFieldString( "test" );
    simpleBean.setFieldLong( 123l );
    simpleBean.setFieldBoolean( true );
    simpleBean.setFieldDouble( 123.5 );
    simpleBean.setFieldFloat( 156.7f );
    simpleBean.setFieldInteger( 12 );
    return simpleBean;
  }
}
